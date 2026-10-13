package universal.file.server.app.server

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Build
import android.util.Base64
import android.util.LruCache
import universal.file.server.app.data.FtpSettings
import universal.file.server.app.data.LogLevel
import universal.file.server.app.utils.StorageHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class HttpServerEngine(
    private val context: Context? = null,
    private val settings: FtpSettings,
    private val onLog: (LogLevel, String, String?) -> Unit,
    private val onConnectionCountChanged: (Int) -> Unit
) {
    private var serverSocket: ServerSocket? = null
    private var serverScope: CoroutineScope? = null
    private var acceptJob: Job? = null
    private var sessionReaperJob: Job? = null
    private val isRunning = AtomicBoolean(false)
    private val activeWebSessions = ConcurrentHashMap<String, Long>()
    private val activeTransferCount = AtomicInteger(0)
    private val lastReportedConnectionCount = AtomicInteger(0)
    private val clientSockets = ConcurrentHashMap.newKeySet<Socket>()
    private val thumbnailCache = object : LruCache<String, ByteArray>(8 * 1024 * 1024) {
        override fun sizeOf(key: String, value: ByteArray): Int = value.size
    }

    companion object {
        private const val WEB_SESSION_TIMEOUT_MS = 15_000L
        private const val SESSION_REAPER_INTERVAL_MS = 2_000L
    }

    private val fileSystem = FtpFileSystem(
        context = context,
        rootPath = settings.rootPath,
        rootTreeUriString = settings.rootTreeUriString,
        isReadOnly = settings.isReadOnly
    )

    private fun updateAndGetConnectionCount(): Int {
        if (!isRunning.get()) {
            activeWebSessions.clear()
            activeTransferCount.set(0)
            lastReportedConnectionCount.set(0)
            return 0
        }
        val now = System.currentTimeMillis()
        val iterator = activeWebSessions.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value > WEB_SESSION_TIMEOUT_MS) {
                iterator.remove()
            }
        }
        val openTabs = activeWebSessions.size
        val transfers = activeTransferCount.get().coerceAtLeast(0)
        val total = (openTabs + transfers).coerceAtLeast(0)
        val prev = lastReportedConnectionCount.getAndSet(total)
        if (prev != total) {
            onConnectionCountChanged(total)
        }
        return total
    }

    private inline fun <T> withTransferTracking(block: () -> T): T {
        activeTransferCount.incrementAndGet()
        updateAndGetConnectionCount()
        return try {
            block()
        } finally {
            activeTransferCount.decrementAndGet().coerceAtLeast(0)
            updateAndGetConnectionCount()
        }
    }

    fun start(): Boolean {
        if (isRunning.get()) return true

        return try {
            val socket = ServerSocket()
            socket.reuseAddress = true
            socket.bind(InetSocketAddress("0.0.0.0", settings.port))
            serverSocket = socket
            isRunning.set(true)
            activeWebSessions.clear()
            activeTransferCount.set(0)
            lastReportedConnectionCount.set(0)
            onConnectionCountChanged(0)

            val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            serverScope = scope

            onLog(
                LogLevel.SUCCESS,
                "HTTP Web Server started on port ${settings.port} (Root: ${fileSystem.getRootDirectory().absolutePath})",
                null
            )

            sessionReaperJob = scope.launch {
                while (isActive && isRunning.get()) {
                    delay(SESSION_REAPER_INTERVAL_MS)
                    updateAndGetConnectionCount()
                }
            }

            acceptJob = scope.launch {
                while (isActive && isRunning.get()) {
                    try {
                        val client = socket.accept()
                        clientSockets.add(client)

                        launch {
                            handleClient(client)
                        }
                    } catch (e: Exception) {
                        if (isRunning.get()) {
                            onLog(LogLevel.ERROR, "HTTP accept error: ${e.message}", null)
                        }
                        break
                    }
                }
            }
            true
        } catch (e: Exception) {
            onLog(LogLevel.ERROR, "Failed to start HTTP server on port ${settings.port}: ${e.message}", null)
            stop()
            false
        }
    }

    fun stop() {
        if (!isRunning.getAndSet(false)) return
        try {
            acceptJob?.cancel()
            sessionReaperJob?.cancel()
            sessionReaperJob = null
            clientSockets.forEach { socket ->
                try {
                    socket.close()
                } catch (_: Exception) {}
            }
            clientSockets.clear()
            serverSocket?.close()
            serverSocket = null
            serverScope?.cancel()
            serverScope = null
            activeWebSessions.clear()
            activeTransferCount.set(0)
            lastReportedConnectionCount.set(0)
            onConnectionCountChanged(0)
            onLog(LogLevel.WARNING, "HTTP Web Server stopped", null)
        } catch (_: Exception) {}
    }

    fun isServerRunning(): Boolean = isRunning.get()

    private fun handleClient(socket: Socket) {
        val clientIp = socket.inetAddress?.hostAddress ?: "unknown"
        try {
            socket.soTimeout = 60_000
            val input = BufferedInputStream(socket.getInputStream(), 65536)
            val output = BufferedOutputStream(socket.getOutputStream(), 65536)

            val requestLine = readHeaderLine(input) ?: return
            if (requestLine.isBlank()) return

            val parts = requestLine.split(" ")
            if (parts.size < 2) return
            val method = parts[0].uppercase(Locale.US)
            val rawUri = parts[1]

            val headers = mutableMapOf<String, String>()
            while (true) {
                val line = readHeaderLine(input) ?: break
                if (line.isEmpty()) break
                val colonIndex = line.indexOf(':')
                if (colonIndex > 0) {
                    val key = line.substring(0, colonIndex).trim().lowercase(Locale.US)
                    val value = line.substring(colonIndex + 1).trim()
                    headers[key] = value
                }
            }

            val qIndex = rawUri.indexOf('?')
            val pathPart = if (qIndex >= 0) rawUri.substring(0, qIndex) else rawUri
            val queryPart = if (qIndex >= 0) rawUri.substring(qIndex + 1) else ""
            val queryParams = parseQueryParams(queryPart)

            val rawSid = (queryParams["sid"] ?: headers["x-ufs-session"])?.trim()
            val actionParam = queryParams["action"]?.trim()?.lowercase(Locale.US)
            if (!rawSid.isNullOrEmpty() && rawSid.length <= 96) {
                if (actionParam == "leave") {
                    activeWebSessions.remove(rawSid)
                } else {
                    activeWebSessions[rawSid] = System.currentTimeMillis()
                }
                updateAndGetConnectionCount()
            }

            if ((method == "GET" || method == "POST") && pathPart == "/api/heartbeat") {
                val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                if (contentLength > 0L) {
                    discardInput(input, contentLength)
                }
                val totalConnections = updateAndGetConnectionCount()
                val heartbeatJson = JSONObject()
                    .put("ok", true)
                    .put("online", isRunning.get())
                    .put("activeConnections", totalConnections)
                    .put("openTabs", activeWebSessions.size)
                    .put("activeTransfers", activeTransferCount.get().coerceAtLeast(0))
                sendJson(output, "200 OK", heartbeatJson)
                return
            }

            // Serve Web UI shell and authentication endpoints without triggering native browser Basic Auth popup
            if (method == "GET" && (pathPart == "/" || pathPart == "/index.html")) {
                onLog(LogLevel.INFO, "Opened Web File Manager UI", clientIp)
                val htmlBytes = HttpWebUiHtml.buildWebPortalHtml(settings).toByteArray(StandardCharsets.UTF_8)
                sendResponse(
                    output = output,
                    status = "200 OK",
                    contentType = "text/html; charset=utf-8",
                    body = htmlBytes
                )
                return
            }

            if (method == "GET" && (pathPart == "/favicon.ico" || pathPart == "/favicon.svg")) {
                val svgBytes = HttpWebUiHtml.APP_FAVICON_SVG.toByteArray(StandardCharsets.UTF_8)
                val contentType = if (pathPart == "/favicon.ico") "image/x-icon" else "image/svg+xml"
                sendResponse(
                    output = output,
                    status = "200 OK",
                    contentType = contentType,
                    body = svgBytes
                )
                return
            }

            if (method == "POST" && pathPart == "/api/login") {
                val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                handleLogin(contentLength, input, output, clientIp)
                return
            }

            if (method == "POST" && pathPart == "/api/logout") {
                val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                discardInput(input, contentLength)
                handleLogout(output, clientIp)
                return
            }

            if (method == "GET" && pathPart == "/api/auth-status") {
                val authorized = settings.isAnonymous || isRequestAuthorized(headers, queryParams)
                val statusJson = JSONObject()
                    .put("ok", true)
                    .put("authRequired", !settings.isAnonymous)
                    .put("authenticated", authorized)
                    .put("username", if (settings.isAnonymous) "anonymous" else settings.username)
                sendJson(output, "200 OK", statusJson)
                return
            }

            // Verify authentication if anonymous login is disabled
            if (!settings.isAnonymous) {
                if (!isRequestAuthorized(headers, queryParams)) {
                    val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                    discardInput(input, contentLength)
                    onLog(LogLevel.WARNING, "Authentication required / rejected for $method $rawUri", clientIp)
                    sendUnauthorized(output)
                    return
                }
            }

            when {
                method == "GET" && pathPart == "/api/list" -> {
                    val reqPath = normalizeVirtualPath(queryParams["path"] ?: "/")
                    handleApiList(reqPath, output)
                }

                method == "GET" && pathPart == "/api/exists" -> {
                    val reqPath = normalizeVirtualPath(queryParams["path"] ?: "/")
                    handleApiExists(reqPath, output)
                }

                method == "GET" && pathPart == "/api/folder-size" -> {
                    val reqPath = normalizeVirtualPath(queryParams["path"] ?: "/")
                    handleApiFolderSize(reqPath, output)
                }

                method == "GET" && pathPart == "/api/search" -> {
                    val reqPath = normalizeVirtualPath(queryParams["path"] ?: "/")
                    val query = queryParams["q"] ?: ""
                    val recursiveParam = queryParams["recursive"] ?: "1"
                    val recursive = recursiveParam == "1" || recursiveParam.equals("true", ignoreCase = true)
                    handleApiSearch(reqPath, query, recursive, output)
                }

                method == "GET" && pathPart == "/api/thumbnail" -> {
                    val reqPath = normalizeVirtualPath(queryParams["path"] ?: "/")
                    handleThumbnail(reqPath, output)
                }

                method == "GET" && (pathPart == "/api/download" || pathPart.startsWith("/api/download/")) -> {
                    val reqPath = normalizeVirtualPath(queryParams["path"] ?: "/")
                    val inline = queryParams["inline"] == "1"
                    val rangeHeader = headers["range"]
                    withTransferTracking {
                        handleDownload(reqPath, inline, rangeHeader, output, clientIp)
                    }
                }

                method == "GET" && (pathPart == "/api/zip" || pathPart.startsWith("/api/zip/")) -> {
                    val itemsParam = queryParams["items"]
                    withTransferTracking {
                        if (!itemsParam.isNullOrBlank()) {
                            val selectedPaths = parseItemsListParam(itemsParam)
                            handleStreamingMultiZip(selectedPaths, output, clientIp)
                        } else {
                            val reqPath = normalizeVirtualPath(queryParams["path"] ?: "/")
                            handleStreamingZip(reqPath, output, clientIp)
                        }
                    }
                }

                method == "POST" && pathPart == "/api/upload" -> {
                    val baseDir = normalizeVirtualPath(queryParams["path"] ?: "/")
                    val relativePath = sanitizeRelativePath(queryParams["relativePath"] ?: "")
                    val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                    withTransferTracking {
                        handleUpload(baseDir, relativePath, contentLength, input, output, clientIp)
                    }
                }

                method == "POST" && pathPart == "/api/mkdir" -> {
                    val targetPath = normalizeVirtualPath(queryParams["path"] ?: "")
                    handleMkdir(targetPath, output, clientIp)
                }

                method == "POST" && pathPart == "/api/rename" -> {
                    val fromPath = normalizeVirtualPath(queryParams["from"] ?: "")
                    val toPath = normalizeVirtualPath(queryParams["to"] ?: "")
                    handleRename(fromPath, toPath, output, clientIp)
                }

                method == "POST" && pathPart == "/api/delete" -> {
                    val targetPath = normalizeVirtualPath(queryParams["path"] ?: "")
                    handleDelete(targetPath, output, clientIp)
                }

                method == "POST" && pathPart == "/api/batch-copy" -> {
                    val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                    handleBatchCopy(queryParams, contentLength, input, output, clientIp)
                }

                method == "POST" && pathPart == "/api/batch-move" -> {
                    val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                    handleBatchMove(queryParams, contentLength, input, output, clientIp)
                }

                method == "POST" && pathPart == "/api/batch-delete" -> {
                    val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                    handleBatchDelete(queryParams, contentLength, input, output, clientIp)
                }

                method == "POST" && pathPart == "/api/batch-rename" -> {
                    val contentLength = headers["content-length"]?.toLongOrNull() ?: 0L
                    handleBatchRename(contentLength, input, output, clientIp)
                }

                else -> {
                    sendJson(output, "404 Not Found", JSONObject().put("ok", false).put("error", "Not found"))
                }
            }
        } catch (_: Exception) {
        } finally {
            clientSockets.remove(socket)
            try {
                socket.close()
            } catch (_: Exception) {}
        }
    }

    private fun handleApiList(virtualPath: String, output: OutputStream) {
        val items = fileSystem.getDirectoryItems(virtualPath)
        val rootPath = settings.rootPath.ifEmpty { StorageHelper.getDefaultStoragePath() }
        val (freeBytes, totalBytes) = StorageHelper.getStorageSpace(rootPath)

        val arr = JSONArray()
        for (item in items) {
            val obj = JSONObject()
                .put("name", item.name)
                .put("isDirectory", item.isDirectory)
                .put("size", if (item.isDirectory) -1L else item.size)
                .put("lastModified", item.lastModified)
            arr.put(obj)
        }

        val response = JSONObject()
            .put("ok", true)
            .put("path", virtualPath)
            .put("readOnly", settings.isReadOnly)
            .put("rootDisplayName", settings.rootDisplayName)
            .put("freeSpace", freeBytes)
            .put("totalSpace", totalBytes)
            .put("activeConnections", updateAndGetConnectionCount())
            .put("authToken", if (!settings.isAnonymous) expectedAuthToken() else "")
            .put("items", arr)

        sendJson(output, "200 OK", response)
    }

    private fun handleThumbnail(virtualPath: String, output: OutputStream) {
        if (virtualPath == "/" || !fileSystem.exists(virtualPath) || fileSystem.isDirectory(virtualPath)) {
            sendJson(output, "404 Not Found", JSONObject().put("ok", false).put("error", "File not found"))
            return
        }

        val fileName = virtualPath.substringAfterLast('/')
        val ext = fileName.substringAfterLast('.', "").lowercase(Locale.US)
        if (ext == "svg") {
            handleDownload(virtualPath, inline = true, rangeHeader = null, output = output, clientIp = "")
            return
        }

        val lastMod = fileSystem.getFileLastModified(virtualPath)
        val cacheKey = "$virtualPath|$lastMod"
        val cachedBytes = synchronized(thumbnailCache) { thumbnailCache.get(cacheKey) }
        if (cachedBytes != null) {
            sendResponse(
                output = output,
                status = "200 OK",
                contentType = "image/jpeg",
                body = cachedBytes,
                extraHeaders = listOf("Cache-Control: private, max-age=3600")
            )
            return
        }

        val maxDim = 320
        var thumbBitmap: Bitmap? = null

        val isVideo = ext in setOf("mp4", "webm", "mkv", "mov", "avi", "3gp", "m4v")
        val isImage = ext in setOf("jpg", "jpeg", "png", "webp", "bmp", "gif")

        if (isVideo) {
            val retriever = MediaMetadataRetriever()
            try {
                val resolvedFile = fileSystem.getFile(virtualPath)
                if (resolvedFile.exists() && resolvedFile.canRead()) {
                    retriever.setDataSource(resolvedFile.absolutePath)
                } else {
                    val pfd = fileSystem.openParcelFileDescriptor(virtualPath, "r")
                    if (pfd != null) {
                        pfd.use { retriever.setDataSource(it.fileDescriptor) }
                    }
                }
                thumbBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    retriever.getScaledFrameAtTime(-1L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, maxDim, maxDim)
                        ?: retriever.getFrameAtTime(-1L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                } else {
                    retriever.getFrameAtTime(-1L, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                }
            } catch (_: Exception) {
            } finally {
                try {
                    retriever.release()
                } catch (_: Exception) {}
            }
        } else if (isImage) {
            try {
                val boundsOpts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                fileSystem.openInputStream(virtualPath)?.use { ins ->
                    BitmapFactory.decodeStream(ins, null, boundsOpts)
                }
                val outW = boundsOpts.outWidth
                val outH = boundsOpts.outHeight
                if (outW > 0 && outH > 0) {
                    var sampleSize = 1
                    while ((outW / sampleSize) > maxDim * 2 || (outH / sampleSize) > maxDim * 2) {
                        sampleSize *= 2
                    }
                    val decodeOpts = BitmapFactory.Options().apply {
                        inSampleSize = sampleSize
                        inPreferredConfig = Bitmap.Config.RGB_565
                    }
                    fileSystem.openInputStream(virtualPath)?.use { ins ->
                        thumbBitmap = BitmapFactory.decodeStream(ins, null, decodeOpts)
                    }
                }
            } catch (_: Exception) {}
        }

        val bmp = thumbBitmap
        if (bmp == null) {
            sendJson(output, "404 Not Found", JSONObject().put("ok", false).put("error", "Thumbnail unavailable"))
            return
        }

        try {
            val w = bmp.width
            val h = bmp.height
            val finalBmp = if (w > maxDim || h > maxDim) {
                val scale = maxDim.toFloat() / maxOf(w, h).toFloat()
                val targetW = (w * scale).toInt().coerceAtLeast(1)
                val targetH = (h * scale).toInt().coerceAtLeast(1)
                val scaled = Bitmap.createScaledBitmap(bmp, targetW, targetH, true)
                if (scaled !== bmp) {
                    bmp.recycle()
                }
                scaled
            } else {
                bmp
            }

            val baos = ByteArrayOutputStream(16384)
            finalBmp.compress(Bitmap.CompressFormat.JPEG, 78, baos)
            finalBmp.recycle()
            val jpegBytes = baos.toByteArray()
            if (jpegBytes.isNotEmpty()) {
                synchronized(thumbnailCache) {
                    thumbnailCache.put(cacheKey, jpegBytes)
                }
                sendResponse(
                    output = output,
                    status = "200 OK",
                    contentType = "image/jpeg",
                    body = jpegBytes,
                    extraHeaders = listOf("Cache-Control: private, max-age=3600")
                )
                return
            }
        } catch (_: Exception) {}

        sendJson(output, "404 Not Found", JSONObject().put("ok", false).put("error", "Thumbnail encoding failed"))
    }

    private fun handleApiExists(virtualPath: String, output: OutputStream) {
        val exists = virtualPath != "/" && fileSystem.exists(virtualPath)
        if (!exists) {
            val response = JSONObject()
                .put("ok", true)
                .put("exists", false)
                .put("path", virtualPath)
            sendJson(output, "200 OK", response)
            return
        }

        val isDir = fileSystem.isDirectory(virtualPath)
        val size = if (isDir) 0L else fileSystem.getFileLength(virtualPath).coerceAtLeast(0L)
        val lastModified = fileSystem.getFileLastModified(virtualPath)
        val parentDir = virtualPath.substringBeforeLast('/').ifEmpty { "/" }
        val baseName = virtualPath.substringAfterLast('/')
        val suggestedName = fileSystem.suggestAvailableName(parentDir, baseName, isDir)

        val response = JSONObject()
            .put("ok", true)
            .put("exists", true)
            .put("path", virtualPath)
            .put("name", baseName)
            .put("isDirectory", isDir)
            .put("size", size)
            .put("lastModified", lastModified)
            .put("suggestedName", suggestedName)

        sendJson(output, "200 OK", response)
    }

    private fun handleApiFolderSize(virtualPath: String, output: OutputStream) {
        if (!fileSystem.exists(virtualPath) || !fileSystem.isDirectory(virtualPath)) {
            sendJson(
                output,
                "404 Not Found",
                JSONObject().put("ok", false).put("error", "Folder not found")
            )
            return
        }
        val (totalBytes, fileCount, folderCount) = fileSystem.calculateDirectorySize(virtualPath)
        val response = JSONObject()
            .put("ok", true)
            .put("path", virtualPath)
            .put("size", totalBytes)
            .put("fileCount", fileCount)
            .put("folderCount", folderCount)
        sendJson(output, "200 OK", response)
    }

    private fun handleApiSearch(
        virtualPath: String,
        query: String,
        recursive: Boolean,
        output: OutputStream
    ) {
        val results = fileSystem.searchItems(
            basePath = virtualPath,
            query = query,
            recursive = recursive
        )

        val arr = JSONArray()
        for (item in results) {
            val obj = JSONObject()
                .put("name", item.name)
                .put("path", item.virtualPath)
                .put("parentPath", item.parentVirtualPath)
                .put("relativePath", item.relativePath)
                .put("isDirectory", item.isDirectory)
                .put("size", item.size)
                .put("lastModified", item.lastModified)
            arr.put(obj)
        }

        val response = JSONObject()
            .put("ok", true)
            .put("path", virtualPath)
            .put("query", query)
            .put("recursive", recursive)
            .put("items", arr)

        sendJson(output, "200 OK", response)
    }

    private fun handleDownload(
        virtualPath: String,
        inline: Boolean,
        rangeHeader: String?,
        output: OutputStream,
        clientIp: String
    ) {
        if (!fileSystem.exists(virtualPath) || fileSystem.isDirectory(virtualPath)) {
            sendJson(output, "404 Not Found", JSONObject().put("ok", false).put("error", "File not found"))
            return
        }

        val fileName = virtualPath.substringAfterLast('/').ifEmpty { "download" }
        val fileLength = fileSystem.getFileLength(virtualPath).coerceAtLeast(0L)
        val mimeType = guessMimeType(fileName)
        val dispositionType = if (inline) "inline" else "attachment"
        val encodedFileName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20")

        val inputStream = fileSystem.openInputStream(virtualPath)
        if (inputStream == null) {
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Cannot read file"))
            return
        }

        inputStream.use { stream ->
            var startByte = 0L
            var endByte = if (fileLength > 0) fileLength - 1 else 0L
            var isPartial = false

            if (!rangeHeader.isNullOrBlank() && rangeHeader.startsWith("bytes=") && fileLength > 0) {
                val rangeSpec = rangeHeader.removePrefix("bytes=").substringBefore(',').trim()
                val dashIdx = rangeSpec.indexOf('-')
                if (dashIdx >= 0) {
                    val startStr = rangeSpec.substring(0, dashIdx).trim()
                    val endStr = rangeSpec.substring(dashIdx + 1).trim()
                    startByte = startStr.toLongOrNull() ?: 0L
                    if (endStr.isNotEmpty()) {
                        endByte = endStr.toLongOrNull()?.coerceAtMost(fileLength - 1) ?: (fileLength - 1)
                    }
                    if (startByte in 0 until fileLength && endByte >= startByte) {
                        isPartial = true
                    } else {
                        startByte = 0L
                        endByte = fileLength - 1
                    }
                }
            }

            if (isPartial && startByte > 0) {
                skipFully(stream, startByte)
            }

            val contentLengthToSend = if (fileLength > 0) (endByte - startByte + 1) else 0L
            val statusLine = if (isPartial) "HTTP/1.1 206 Partial Content" else "HTTP/1.1 200 OK"

            val headerBuilder = StringBuilder()
            headerBuilder.append("$statusLine\r\n")
            headerBuilder.append("Content-Type: $mimeType\r\n")
            headerBuilder.append("Accept-Ranges: bytes\r\n")
            if (fileLength > 0) {
                headerBuilder.append("Content-Length: $contentLengthToSend\r\n")
            }
            if (isPartial) {
                headerBuilder.append("Content-Range: bytes $startByte-$endByte/$fileLength\r\n")
            }
            headerBuilder.append("Content-Disposition: $dispositionType; filename=\"${fileName.replace("\"", "_")}\"; filename*=UTF-8''$encodedFileName\r\n")
            headerBuilder.append("Connection: close\r\n\r\n")

            output.write(headerBuilder.toString().toByteArray(StandardCharsets.UTF_8))

            if (!isPartial || startByte == 0L) {
                onLog(LogLevel.INFO, "Downloading $virtualPath (${StorageHelper.formatBytes(fileLength)})", clientIp)
            }

            val buffer = ByteArray(65536)
            var remaining = if (fileLength > 0) contentLengthToSend else Long.MAX_VALUE
            while (remaining > 0) {
                val toRead = minOf(buffer.size.toLong(), remaining).toInt()
                val read = stream.read(buffer, 0, toRead)
                if (read <= 0) break
                output.write(buffer, 0, read)
                remaining -= read
            }
            output.flush()
        }
    }

    /**
     * Streams a directory and all its subdirectories on-the-fly as a ZIP archive directly
     * to the client socket without creating any temporary file on the Android device.
     */
    private fun handleStreamingZip(
        virtualPath: String,
        output: OutputStream,
        clientIp: String
    ) {
        if (!fileSystem.exists(virtualPath) || !fileSystem.isDirectory(virtualPath)) {
            sendJson(output, "404 Not Found", JSONObject().put("ok", false).put("error", "Folder not found"))
            return
        }

        val folderName = virtualPath.trim('/').substringAfterLast('/').ifEmpty { "StorageRoot" }
        val zipFileName = "$folderName.zip"
        val encodedZipName = URLEncoder.encode(zipFileName, "UTF-8").replace("+", "%20")

        onLog(LogLevel.INFO, "Streaming folder as ZIP: $virtualPath -> $zipFileName", clientIp)

        val headers = buildString {
            append("HTTP/1.1 200 OK\r\n")
            append("Content-Type: application/zip\r\n")
            append("Content-Disposition: attachment; filename=\"${zipFileName.replace("\"", "_")}\"; filename*=UTF-8''$encodedZipName\r\n")
            append("Cache-Control: no-store\r\n")
            append("Connection: close\r\n\r\n")
        }
        output.write(headers.toByteArray(StandardCharsets.UTF_8))
        output.flush()

        val zipOut = ZipOutputStream(BufferedOutputStream(output, 65536))
        // Use fast compression so media/large files stream at maximum LAN/USB speed with minimal CPU usage
        zipOut.setLevel(Deflater.BEST_SPEED)

        val buffer = ByteArray(65536)
        var fileCount = 0

        fun addDirectoryToZip(currentVirtualDir: String, zipRelativePrefix: String) {
            val items = fileSystem.getDirectoryItems(currentVirtualDir)
            if (items.isEmpty() && zipRelativePrefix.isNotEmpty()) {
                try {
                    val emptyDirEntry = ZipEntry(zipRelativePrefix.trimEnd('/') + "/")
                    zipOut.putNextEntry(emptyDirEntry)
                    zipOut.closeEntry()
                } catch (_: Exception) {}
                return
            }

            for (item in items) {
                val childVirtualPath = if (currentVirtualDir == "/") {
                    "/${item.name}"
                } else {
                    "${currentVirtualDir.trimEnd('/')}/${item.name}"
                }
                val entryName = if (zipRelativePrefix.isEmpty()) {
                    item.name
                } else {
                    "$zipRelativePrefix/${item.name}"
                }

                if (item.isDirectory) {
                    addDirectoryToZip(childVirtualPath, entryName)
                } else {
                    val inputStream = fileSystem.openInputStream(childVirtualPath) ?: continue
                    try {
                        val entry = ZipEntry(entryName)
                        if (item.lastModified > 0) {
                            entry.time = item.lastModified
                        }
                        zipOut.putNextEntry(entry)
                        inputStream.use { fis ->
                            while (true) {
                                val read = fis.read(buffer)
                                if (read <= 0) break
                                zipOut.write(buffer, 0, read)
                            }
                        }
                        zipOut.closeEntry()
                        fileCount++
                    } catch (_: Exception) {
                        // Client may have cancelled download or single file failed
                    }
                }
            }
        }

        try {
            val rootPrefix = if (virtualPath == "/") "" else folderName
            addDirectoryToZip(virtualPath, rootPrefix)
            zipOut.finish()
            zipOut.flush()
            onLog(LogLevel.SUCCESS, "Completed ZIP stream for $virtualPath ($fileCount files)", clientIp)
        } catch (e: Exception) {
            onLog(LogLevel.WARNING, "ZIP stream interrupted for $virtualPath: ${e.message}", clientIp)
        }
    }

    private fun handleUpload(
        baseDir: String,
        relativePath: String,
        contentLength: Long,
        input: InputStream,
        output: OutputStream,
        clientIp: String
    ) {
        if (settings.isReadOnly) {
            discardInput(input, contentLength)
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Server is in Read-Only mode"))
            return
        }

        if (relativePath.isBlank()) {
            discardInput(input, contentLength)
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "Missing file name"))
            return
        }

        val targetVirtualPath = if (baseDir == "/") {
            "/$relativePath"
        } else {
            "${baseDir.trimEnd('/')}/$relativePath"
        }

        // Ensure parent subdirectories exist when uploading nested folders
        val parentVirtualDir = targetVirtualPath.substringBeforeLast('/', "")
        if (parentVirtualDir.isNotEmpty() && parentVirtualDir != "/" && !fileSystem.exists(parentVirtualDir)) {
            fileSystem.makeDirectory(parentVirtualDir)
        }

        val outStream = fileSystem.openOutputStream(targetVirtualPath, append = false)
        if (outStream == null) {
            discardInput(input, contentLength)
            onLog(LogLevel.ERROR, "Upload failed (cannot open output): $targetVirtualPath", clientIp)
            sendJson(output, "500 Internal Server Error", JSONObject().put("ok", false).put("error", "Failed to write file"))
            return
        }

        var totalWritten = 0L
        var uploadSuccess = false
        try {
            outStream.use { fos ->
                val buffer = ByteArray(65536)
                var remaining = contentLength
                while (remaining > 0) {
                    val toRead = minOf(buffer.size.toLong(), remaining).toInt()
                    val read = input.read(buffer, 0, toRead)
                    if (read <= 0) break
                    fos.write(buffer, 0, read)
                    remaining -= read
                    totalWritten += read
                }
                fos.flush()
            }
            if (contentLength > 0 && totalWritten < contentLength) {
                // Incomplete upload (client cancelled or aborted) -> immediately delete partial file to prevent corruption
                try {
                    fileSystem.deleteFile(targetVirtualPath)
                } catch (_: Exception) {}
                onLog(LogLevel.WARNING, "Upload cancelled/incomplete: $targetVirtualPath ($totalWritten of $contentLength bytes written) - cleaned up", clientIp)
                try {
                    sendJson(output, "499 Client Closed Request", JSONObject().put("ok", false).put("error", "Upload cancelled or incomplete"))
                } catch (_: Exception) {}
                return
            }
            uploadSuccess = true
        } catch (e: Exception) {
            try {
                fileSystem.deleteFile(targetVirtualPath)
            } catch (_: Exception) {}
            onLog(LogLevel.WARNING, "Upload aborted: $targetVirtualPath - cleaned up partial file", clientIp)
            return
        }

        if (uploadSuccess) {
            onLog(LogLevel.SUCCESS, "Uploaded $targetVirtualPath (${StorageHelper.formatBytes(totalWritten)})", clientIp)
            sendJson(output, "200 OK", JSONObject().put("ok", true).put("path", targetVirtualPath).put("size", totalWritten))
        }
    }

    private fun handleMkdir(targetPath: String, output: OutputStream, clientIp: String) {
        if (settings.isReadOnly) {
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Server is in Read-Only mode"))
            return
        }
        val created = fileSystem.makeDirectory(targetPath)
        if (created) {
            onLog(LogLevel.SUCCESS, "Created folder $targetPath", clientIp)
            sendJson(output, "200 OK", JSONObject().put("ok", true))
        } else {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "Could not create folder"))
        }
    }

    private fun handleRename(fromPath: String, toPath: String, output: OutputStream, clientIp: String) {
        if (settings.isReadOnly) {
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Server is in Read-Only mode"))
            return
        }
        val renamed = fileSystem.rename(fromPath, toPath)
        if (renamed) {
            onLog(LogLevel.SUCCESS, "Renamed $fromPath -> $toPath", clientIp)
            sendJson(output, "200 OK", JSONObject().put("ok", true))
        } else {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "Could not rename item"))
        }
    }

    private fun handleDelete(targetPath: String, output: OutputStream, clientIp: String) {
        if (settings.isReadOnly) {
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Server is in Read-Only mode"))
            return
        }
        val isDir = fileSystem.isDirectory(targetPath)
        val deleted = if (isDir) {
            fileSystem.removeDirectory(targetPath)
        } else {
            fileSystem.deleteFile(targetPath)
        }
        if (deleted) {
            onLog(LogLevel.WARNING, "Deleted ${if (isDir) "folder" else "file"} $targetPath", clientIp)
            sendJson(output, "200 OK", JSONObject().put("ok", true))
        } else {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "Could not delete item"))
        }
    }

    /**
     * Streams multiple selected files and/or folders on-the-fly into a single ZIP archive.
     */
    private fun handleStreamingMultiZip(
        selectedPaths: List<String>,
        output: OutputStream,
        clientIp: String
    ) {
        val validPaths = selectedPaths.map { normalizeVirtualPath(it) }.filter { it != "/" && fileSystem.exists(it) }
        if (validPaths.isEmpty()) {
            sendJson(output, "404 Not Found", JSONObject().put("ok", false).put("error", "No valid items selected"))
            return
        }

        val zipFileName = if (validPaths.size == 1) {
            "${validPaths.first().trim('/').substringAfterLast('/').ifEmpty { "Selected" }}.zip"
        } else {
            "Selected_${validPaths.size}_Items.zip"
        }
        val encodedZipName = URLEncoder.encode(zipFileName, "UTF-8").replace("+", "%20")

        onLog(LogLevel.INFO, "Streaming ${validPaths.size} selected item(s) as ZIP -> $zipFileName", clientIp)

        val headers = buildString {
            append("HTTP/1.1 200 OK\r\n")
            append("Content-Type: application/zip\r\n")
            append("Content-Disposition: attachment; filename=\"${zipFileName.replace("\"", "_")}\"; filename*=UTF-8''$encodedZipName\r\n")
            append("Cache-Control: no-store\r\n")
            append("Connection: close\r\n\r\n")
        }
        output.write(headers.toByteArray(StandardCharsets.UTF_8))
        output.flush()

        val zipOut = ZipOutputStream(BufferedOutputStream(output, 65536))
        zipOut.setLevel(Deflater.BEST_SPEED)
        val buffer = ByteArray(65536)
        var fileCount = 0

        fun addFileToZip(virtualFilePath: String, zipEntryName: String) {
            val inputStream = fileSystem.openInputStream(virtualFilePath) ?: return
            try {
                val entry = ZipEntry(zipEntryName)
                val lastMod = fileSystem.getFileLastModified(virtualFilePath)
                if (lastMod > 0) entry.time = lastMod
                zipOut.putNextEntry(entry)
                inputStream.use { fis ->
                    while (true) {
                        val read = fis.read(buffer)
                        if (read <= 0) break
                        zipOut.write(buffer, 0, read)
                    }
                }
                zipOut.closeEntry()
                fileCount++
            } catch (_: Exception) {}
        }

        fun addDirectoryToZip(currentVirtualDir: String, zipRelativePrefix: String) {
            val items = fileSystem.getDirectoryItems(currentVirtualDir)
            if (items.isEmpty() && zipRelativePrefix.isNotEmpty()) {
                try {
                    zipOut.putNextEntry(ZipEntry(zipRelativePrefix.trimEnd('/') + "/"))
                    zipOut.closeEntry()
                } catch (_: Exception) {}
                return
            }
            for (item in items) {
                val childVirtualPath = if (currentVirtualDir == "/") "/${item.name}" else "${currentVirtualDir.trimEnd('/')}/${item.name}"
                val entryName = if (zipRelativePrefix.isEmpty()) item.name else "$zipRelativePrefix/${item.name}"
                if (item.isDirectory) {
                    addDirectoryToZip(childVirtualPath, entryName)
                } else {
                    addFileToZip(childVirtualPath, entryName)
                }
            }
        }

        try {
            for (path in validPaths) {
                val itemName = path.trim('/').substringAfterLast('/')
                if (itemName.isEmpty()) continue
                if (fileSystem.isDirectory(path)) {
                    addDirectoryToZip(path, itemName)
                } else {
                    addFileToZip(path, itemName)
                }
            }
            zipOut.finish()
            zipOut.flush()
            onLog(LogLevel.SUCCESS, "Completed multi-item ZIP stream ($fileCount files)", clientIp)
        } catch (e: Exception) {
            onLog(LogLevel.WARNING, "Multi-item ZIP stream interrupted: ${e.message}", clientIp)
        }
    }

    private fun handleBatchCopy(
        queryParams: Map<String, String>,
        contentLength: Long,
        input: InputStream,
        output: OutputStream,
        clientIp: String
    ) {
        if (settings.isReadOnly) {
            discardInput(input, contentLength)
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Server is in Read-Only mode"))
            return
        }
        val bodyJson = readJsonBody(input, contentLength)
        val targetDir = normalizeVirtualPath(
            bodyJson?.optString("targetDir")?.takeIf { it.isNotEmpty() } ?: (queryParams["targetDir"] ?: "/")
        )
        val items = extractItemsFromRequest(bodyJson, queryParams["items"])
        if (items.isEmpty()) {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "No items specified"))
            return
        }

        val targetNamesObj = bodyJson?.optJSONObject("targetNames")
        var copiedCount = 0
        for (rawItem in items) {
            val itemPath = normalizeVirtualPath(rawItem)
            val customName = targetNamesObj?.optString(rawItem)?.takeIf { it.isNotBlank() }
                ?: targetNamesObj?.optString(itemPath)?.takeIf { it.isNotBlank() }
            if (fileSystem.copyPath(itemPath, targetDir, customName)) {
                copiedCount++
            }
        }

        if (copiedCount > 0) {
            onLog(LogLevel.SUCCESS, "Copied $copiedCount/${items.size} item(s) to $targetDir", clientIp)
            sendJson(output, "200 OK", JSONObject().put("ok", true).put("copied", copiedCount).put("total", items.size))
        } else {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "Could not copy selected items (check destination folder)"))
        }
    }

    private fun handleBatchMove(
        queryParams: Map<String, String>,
        contentLength: Long,
        input: InputStream,
        output: OutputStream,
        clientIp: String
    ) {
        if (settings.isReadOnly) {
            discardInput(input, contentLength)
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Server is in Read-Only mode"))
            return
        }
        val bodyJson = readJsonBody(input, contentLength)
        val targetDir = normalizeVirtualPath(
            bodyJson?.optString("targetDir")?.takeIf { it.isNotEmpty() } ?: (queryParams["targetDir"] ?: "/")
        )
        val items = extractItemsFromRequest(bodyJson, queryParams["items"])
        if (items.isEmpty()) {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "No items specified"))
            return
        }

        val targetNamesObj = bodyJson?.optJSONObject("targetNames")
        var movedCount = 0
        for (rawItem in items) {
            val itemPath = normalizeVirtualPath(rawItem)
            val customName = targetNamesObj?.optString(rawItem)?.takeIf { it.isNotBlank() }
                ?: targetNamesObj?.optString(itemPath)?.takeIf { it.isNotBlank() }
            if (fileSystem.movePath(itemPath, targetDir, customName)) {
                movedCount++
            }
        }

        if (movedCount > 0) {
            onLog(LogLevel.SUCCESS, "Moved $movedCount/${items.size} item(s) to $targetDir", clientIp)
            sendJson(output, "200 OK", JSONObject().put("ok", true).put("moved", movedCount).put("total", items.size))
        } else {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "Could not move selected items (cannot move a folder into itself)"))
        }
    }

    private fun handleBatchDelete(
        queryParams: Map<String, String>,
        contentLength: Long,
        input: InputStream,
        output: OutputStream,
        clientIp: String
    ) {
        if (settings.isReadOnly) {
            discardInput(input, contentLength)
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Server is in Read-Only mode"))
            return
        }
        val bodyJson = readJsonBody(input, contentLength)
        val items = extractItemsFromRequest(bodyJson, queryParams["items"])
        if (items.isEmpty()) {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "No items specified"))
            return
        }

        var deletedCount = 0
        for (rawItem in items) {
            val itemPath = normalizeVirtualPath(rawItem)
            if (itemPath == "/") continue
            val ok = if (fileSystem.isDirectory(itemPath)) {
                fileSystem.removeDirectory(itemPath)
            } else {
                fileSystem.deleteFile(itemPath)
            }
            if (ok) deletedCount++
        }

        if (deletedCount > 0) {
            onLog(LogLevel.WARNING, "Batch deleted $deletedCount/${items.size} item(s)", clientIp)
            sendJson(output, "200 OK", JSONObject().put("ok", true).put("deleted", deletedCount).put("total", items.size))
        } else {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "Could not delete selected items"))
        }
    }

    private data class BatchRenameOperation(
        val fromPath: String,
        val parentDir: String,
        val isDir: Boolean,
        val desiredName: String,
        val baseName: String,
        val format: String,
        val ext: String,
        val startNum: Int
    )

    private fun formatBatchNumberedName(
        baseName: String,
        format: String,
        number: Int,
        ext: String
    ): String {
        return when (format) {
            "underscore" -> "${baseName}_$number$ext"
            "pad2" -> "${baseName}_${number.toString().padStart(2, '0')}$ext"
            "pad3" -> "${baseName}_${number.toString().padStart(3, '0')}$ext"
            "dash" -> "$baseName-${number.toString().padStart(3, '0')}$ext"
            "space" -> "$baseName $number$ext"
            else -> "$baseName ($number)$ext"
        }
    }

    private fun sanitizeSingleFileName(raw: String): String {
        return raw.trim()
            .replace("/", "")
            .replace("\\", "")
            .replace(":", "")
            .replace("*", "")
            .replace("?", "")
            .replace("\"", "")
            .replace("<", "")
            .replace(">", "")
            .replace("|", "")
    }

    private fun handleBatchRename(
        contentLength: Long,
        input: InputStream,
        output: OutputStream,
        clientIp: String
    ) {
        if (settings.isReadOnly) {
            discardInput(input, contentLength)
            sendJson(output, "403 Forbidden", JSONObject().put("ok", false).put("error", "Server is in Read-Only mode"))
            return
        }

        val bodyJson = readJsonBody(input, contentLength)
        val opsArray = bodyJson?.optJSONArray("operations")
        if (opsArray == null || opsArray.length() == 0) {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "No rename operations provided"))
            return
        }

        val operations = mutableListOf<BatchRenameOperation>()
        for (i in 0 until opsArray.length()) {
            val obj = opsArray.optJSONObject(i) ?: continue
            val rawFrom = obj.optString("from", "")
            val fromPath = normalizeVirtualPath(rawFrom)
            if (fromPath == "/" || !fileSystem.exists(fromPath)) continue
            val parentDir = fromPath.substringBeforeLast('/').ifEmpty { "/" }
            val isDir = fileSystem.isDirectory(fromPath)
            val desiredName = sanitizeSingleFileName(obj.optString("toName", ""))
            if (desiredName.isEmpty() || desiredName == "." || desiredName == "..") continue
            val baseName = sanitizeSingleFileName(obj.optString("baseName", ""))
            val format = obj.optString("format", "paren").ifBlank { "paren" }
            val ext = if (isDir) "" else obj.optString("ext", "")
            val startNum = obj.optInt("index", i + 1).coerceAtLeast(1)
            operations.add(
                BatchRenameOperation(
                    fromPath = fromPath,
                    parentDir = parentDir,
                    isDir = isDir,
                    desiredName = desiredName,
                    baseName = baseName,
                    format = format,
                    ext = ext,
                    startNum = startNum
                )
            )
        }

        if (operations.isEmpty()) {
            sendJson(output, "400 Bad Request", JSONObject().put("ok", false).put("error", "No valid existing items to rename"))
            return
        }

        val batchSourcePathsLower = operations.map { it.fromPath.lowercase(Locale.US) }.toSet()
        val claimedTargetPathsLower = mutableSetOf<String>()

        data class ResolvedPlan(
            val fromPath: String,
            val parentDir: String,
            val finalTargetPath: String
        )

        val resolvedPlans = mutableListOf<ResolvedPlan>()

        for (op in operations) {
            fun buildVirtualPath(dir: String, fileName: String): String {
                return if (dir == "/") "/$fileName" else "${dir.trimEnd('/')}/$fileName"
            }

            var candidateName = op.desiredName
            var candidatePath = buildVirtualPath(op.parentDir, candidateName)
            var counter = op.startNum

            while (true) {
                val lowerCandidate = candidatePath.lowercase(Locale.US)
                val alreadyClaimed = lowerCandidate in claimedTargetPathsLower
                val existsOutsideBatch = lowerCandidate !in batchSourcePathsLower && fileSystem.exists(candidatePath)
                if (!alreadyClaimed && !existsOutsideBatch) {
                    break
                }
                counter++
                candidateName = if (op.baseName.isNotEmpty()) {
                    formatBatchNumberedName(op.baseName, op.format, counter, op.ext)
                } else {
                    fileSystem.suggestAvailableName(op.parentDir, op.desiredName, op.isDir)
                }
                candidatePath = buildVirtualPath(op.parentDir, candidateName)
                if (counter - op.startNum > 10000) break
            }

            claimedTargetPathsLower.add(candidatePath.lowercase(Locale.US))
            resolvedPlans.add(
                ResolvedPlan(
                    fromPath = op.fromPath,
                    parentDir = op.parentDir,
                    finalTargetPath = candidatePath
                )
            )
        }

        // Phase 1: Move items that overlap with batch source paths (or change casing only) to temporary names
        val tempPathsByIndex = mutableMapOf<Int, String>()
        val batchTimestamp = System.currentTimeMillis()
        for ((idx, plan) in resolvedPlans.withIndex()) {
            if (plan.fromPath == plan.finalTargetPath) continue
            val targetLower = plan.finalTargetPath.lowercase(Locale.US)
            val needsTwoPhase = targetLower in batchSourcePathsLower ||
                plan.fromPath.equals(plan.finalTargetPath, ignoreCase = true)
            if (needsTwoPhase) {
                val origName = plan.fromPath.substringAfterLast('/')
                val tempName = ".__ufs_batch_${batchTimestamp}_${idx}_$origName"
                val tempPath = if (plan.parentDir == "/") "/$tempName" else "${plan.parentDir.trimEnd('/')}/$tempName"
                if (fileSystem.rename(plan.fromPath, tempPath)) {
                    tempPathsByIndex[idx] = tempPath
                }
            }
        }

        // Phase 2: Rename from tempPath (or original fromPath) to finalTargetPath
        var renamedCount = 0
        for ((idx, plan) in resolvedPlans.withIndex()) {
            if (plan.fromPath == plan.finalTargetPath) {
                renamedCount++
                continue
            }
            val currentSource = tempPathsByIndex[idx] ?: plan.fromPath
            if (fileSystem.rename(currentSource, plan.finalTargetPath)) {
                renamedCount++
            } else if (tempPathsByIndex.containsKey(idx)) {
                // Restore original filename if final rename failed
                fileSystem.rename(currentSource, plan.fromPath)
            }
        }

        if (renamedCount > 0) {
            onLog(LogLevel.SUCCESS, "Batch renamed $renamedCount/${operations.size} item(s)", clientIp)
            sendJson(
                output,
                "200 OK",
                JSONObject()
                    .put("ok", true)
                    .put("renamed", renamedCount)
                    .put("total", operations.size)
            )
        } else {
            sendJson(
                output,
                "400 Bad Request",
                JSONObject()
                    .put("ok", false)
                    .put("error", "Could not rename selected items")
            )
        }
    }

    private fun readJsonBody(input: InputStream, contentLength: Long): JSONObject? {
        if (contentLength <= 0 || contentLength > 1024 * 1024) return null
        return try {
            val bytes = ByteArray(contentLength.toInt())
            var offset = 0
            while (offset < bytes.size) {
                val read = input.read(bytes, offset, bytes.size - offset)
                if (read <= 0) break
                offset += read
            }
            JSONObject(String(bytes, 0, offset, StandardCharsets.UTF_8))
        } catch (_: Exception) {
            null
        }
    }

    private fun extractItemsFromRequest(bodyJson: JSONObject?, queryItemsParam: String?): List<String> {
        val arr = bodyJson?.optJSONArray("items")
        if (arr != null) {
            val list = mutableListOf<String>()
            for (i in 0 until arr.length()) {
                val s = arr.optString(i)
                if (s.isNotBlank()) list.add(s)
            }
            return list
        }
        if (!queryItemsParam.isNullOrBlank()) {
            return parseItemsListParam(queryItemsParam)
        }
        return emptyList()
    }

    private fun parseItemsListParam(raw: String): List<String> {
        val trimmed = raw.trim()
        if (trimmed.startsWith("[")) {
            return try {
                val arr = JSONArray(trimmed)
                (0 until arr.length()).mapNotNull { arr.optString(it).takeIf { s -> s.isNotBlank() } }
            } catch (_: Exception) {
                emptyList()
            }
        }
        return trimmed.split("|").map { it.trim() }.filter { it.isNotEmpty() }
    }

    private fun expectedAuthToken(): String {
        val raw = "${settings.username}:${settings.password}"
        return Base64.encodeToString(raw.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    }

    private fun isRequestAuthorized(
        headers: Map<String, String>,
        queryParams: Map<String, String>
    ): Boolean {
        if (settings.isAnonymous) return true
        val expected = expectedAuthToken()

        // 1. Check standard Authorization: Basic <token> header
        val authHeader = headers["authorization"]
        if (!authHeader.isNullOrBlank() && isAuthorized(authHeader)) {
            return true
        }

        // 2. Check custom X-UFS-Auth header from Web UI fetch/XHR
        val customHeader = headers["x-ufs-auth"]?.trim()
        if (!customHeader.isNullOrEmpty() && customHeader == expected) {
            return true
        }

        // 3. Check Cookie header (ufs_auth=<token>)
        val cookieHeader = headers["cookie"]
        if (!cookieHeader.isNullOrBlank()) {
            for (cookie in cookieHeader.split(";")) {
                val trimmed = cookie.trim()
                if (trimmed.startsWith("ufs_auth=")) {
                    val tokenVal = trimmed.removePrefix("ufs_auth=").trim()
                    if (tokenVal == expected) return true
                }
            }
        }

        // 4. Check query param ?auth=<token> as fallback for media/download/zip links
        val queryAuth = queryParams["auth"]?.trim()
        if (!queryAuth.isNullOrEmpty() && queryAuth == expected) {
            return true
        }

        return false
    }

    private fun isAuthorized(authHeader: String?): Boolean {
        if (authHeader.isNullOrBlank()) return false
        if (!authHeader.startsWith("Basic ", ignoreCase = true)) return false
        return try {
            val base64Part = authHeader.substring(6).trim()
            val decoded = String(Base64.decode(base64Part, Base64.DEFAULT), StandardCharsets.UTF_8)
            val colonIdx = decoded.indexOf(':')
            if (colonIdx < 0) return false
            val user = decoded.substring(0, colonIdx)
            val pass = decoded.substring(colonIdx + 1)
            user == settings.username && pass == settings.password
        } catch (_: Exception) {
            false
        }
    }

    private fun handleLogin(
        contentLength: Long,
        input: InputStream,
        output: OutputStream,
        clientIp: String
    ) {
        val bodyJson = readJsonBody(input, contentLength)
        val user = bodyJson?.optString("username", "") ?: ""
        val pass = bodyJson?.optString("password", "") ?: ""
        val rememberMe = bodyJson?.optBoolean("rememberMe", false) ?: false

        if (settings.isAnonymous || (user == settings.username && pass == settings.password)) {
            val token = expectedAuthToken()
            onLog(LogLevel.SUCCESS, "Web UI user '$user' authenticated (rememberMe=$rememberMe)", clientIp)
            val json = JSONObject()
                .put("ok", true)
                .put("token", token)
                .put("username", settings.username)
                .put("rememberMe", rememberMe)
            val cookieHeader = if (rememberMe) {
                // Max-Age=315360000 (10 years) for permanent login
                "Set-Cookie: ufs_auth=$token; Path=/; Max-Age=315360000; SameSite=Lax"
            } else {
                // Session cookie: expires when browser/tab session ends
                "Set-Cookie: ufs_auth=$token; Path=/; SameSite=Lax"
            }
            sendJson(
                output = output,
                status = "200 OK",
                json = json,
                extraHeaders = listOf(cookieHeader)
            )
        } else {
            onLog(LogLevel.WARNING, "Web UI login failed for username '$user'", clientIp)
            val json = JSONObject()
                .put("ok", false)
                .put("authRequired", true)
                .put("error", "Invalid username or password")
            sendJson(output, "401 Unauthorized", json)
        }
    }

    private fun handleLogout(output: OutputStream, clientIp: String) {
        onLog(LogLevel.INFO, "Web UI user signed out", clientIp)
        val json = JSONObject().put("ok", true)
        sendJson(
            output = output,
            status = "200 OK",
            json = json,
            extraHeaders = listOf("Set-Cookie: ufs_auth=; Path=/; Max-Age=0; SameSite=Lax")
        )
    }

    private fun sendUnauthorized(output: OutputStream) {
        val json = JSONObject()
            .put("ok", false)
            .put("authRequired", true)
            .put("error", "Authentication required. Please sign in with your server username and password.")
        sendJson(output, "401 Unauthorized", json)
    }

    private fun sendJson(
        output: OutputStream,
        status: String,
        json: JSONObject,
        extraHeaders: List<String> = emptyList()
    ) {
        val bytes = json.toString().toByteArray(StandardCharsets.UTF_8)
        sendResponse(output, status, "application/json; charset=utf-8", bytes, extraHeaders)
    }

    private fun sendResponse(
        output: OutputStream,
        status: String,
        contentType: String,
        body: ByteArray,
        extraHeaders: List<String> = emptyList()
    ) {
        val headers = buildString {
            append("HTTP/1.1 $status\r\n")
            append("Content-Type: $contentType\r\n")
            append("Content-Length: ${body.size}\r\n")
            append("Cache-Control: no-cache\r\n")
            for (h in extraHeaders) {
                append("$h\r\n")
            }
            append("Connection: close\r\n\r\n")
        }
        output.write(headers.toByteArray(StandardCharsets.UTF_8))
        output.write(body)
        output.flush()
    }

    private fun readHeaderLine(input: InputStream): String? {
        val baos = ByteArrayOutputStream(128)
        var sawBytes = false
        while (true) {
            val b = input.read()
            if (b == -1) {
                return if (sawBytes) baos.toString("UTF-8").trimEnd('\r') else null
            }
            sawBytes = true
            if (b == '\n'.code) {
                break
            }
            baos.write(b)
            if (baos.size() > 16384) break
        }
        return baos.toString("UTF-8").trimEnd('\r')
    }

    private fun parseQueryParams(query: String): Map<String, String> {
        if (query.isBlank()) return emptyMap()
        val map = mutableMapOf<String, String>()
        for (pair in query.split("&")) {
            if (pair.isBlank()) continue
            val eq = pair.indexOf('=')
            if (eq >= 0) {
                val k = URLDecoder.decode(pair.substring(0, eq), "UTF-8")
                val v = URLDecoder.decode(pair.substring(eq + 1), "UTF-8")
                map[k] = v
            } else {
                map[URLDecoder.decode(pair, "UTF-8")] = ""
            }
        }
        return map
    }

    private fun normalizeVirtualPath(raw: String): String {
        val cleaned = raw.replace('\\', '/').trim()
        if (cleaned.isEmpty() || cleaned == "/") return "/"
        val segments = cleaned.split('/').filter { it.isNotEmpty() && it != "." && it != ".." }
        return if (segments.isEmpty()) "/" else "/" + segments.joinToString("/")
    }

    private fun sanitizeRelativePath(raw: String): String {
        val cleaned = raw.replace('\\', '/').trim()
        val segments = cleaned.split('/').filter { it.isNotEmpty() && it != "." && it != ".." }
        return segments.joinToString("/")
    }

    private fun skipFully(stream: InputStream, bytesToSkip: Long) {
        var remaining = bytesToSkip
        val skipBuf = ByteArray(8192)
        while (remaining > 0) {
            val skipped = stream.skip(remaining)
            if (skipped > 0) {
                remaining -= skipped
            } else {
                val read = stream.read(skipBuf, 0, minOf(skipBuf.size.toLong(), remaining).toInt())
                if (read <= 0) break
                remaining -= read
            }
        }
    }

    private fun discardInput(stream: InputStream, bytes: Long) {
        if (bytes <= 0) return
        skipFully(stream, bytes)
    }

    private fun guessMimeType(fileName: String): String {
        val ext = fileName.substringAfterLast('.', "").lowercase(Locale.US)
        return when (ext) {
            "html", "htm" -> "text/html; charset=utf-8"
            "txt", "log", "md", "csv", "ini", "conf", "kt", "java", "py", "sh" -> "text/plain; charset=utf-8"
            "json" -> "application/json; charset=utf-8"
            "xml" -> "application/xml; charset=utf-8"
            "css" -> "text/css; charset=utf-8"
            "js" -> "application/javascript; charset=utf-8"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "svg" -> "image/svg+xml"
            "mp4" -> "video/mp4"
            "webm" -> "video/webm"
            "mkv" -> "video/x-matroska"
            "mov" -> "video/quicktime"
            "mp3" -> "audio/mpeg"
            "wav" -> "audio/wav"
            "ogg" -> "audio/ogg"
            "m4a" -> "audio/mp4"
            "flac" -> "audio/flac"
            "pdf" -> "application/pdf"
            "zip" -> "application/zip"
            "apk" -> "application/vnd.android.package-archive"
            else -> "application/octet-stream"
        }
    }
}
