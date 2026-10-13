package universal.file.server.app.server

import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.documentfile.provider.DocumentFile
import universal.file.server.app.utils.StorageHelper
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FtpFileSystem(
    private val context: Context? = null,
    rootPath: String,
    val rootTreeUriString: String = "",
    val isReadOnly: Boolean = false
) {
    private val effectiveRootPath = if (rootPath.isBlank()) {
        StorageHelper.getDefaultStoragePath()
    } else {
        rootPath
    }

    private val rootDir: File = try {
        val f = File(effectiveRootPath)
        if (!f.exists()) f.mkdirs()
        f.canonicalFile
    } catch (_: Exception) {
        File(effectiveRootPath).absoluteFile
    }

    private var currentVirtualDir = "/"

    fun getRootDirectory(): File = rootDir

    fun getWorkingVirtualPath(): String = currentVirtualDir

    companion object {
        fun cleanFtpPath(raw: String): String {
            var s = raw.trim()
            // Strip surrounding double or single quotes (often added by Windows Explorer)
            if (s.startsWith("\"") && s.endsWith("\"") && s.length >= 2) {
                s = s.substring(1, s.length - 1).trim()
            } else if (s.startsWith("'") && s.endsWith("'") && s.length >= 2) {
                s = s.substring(1, s.length - 1).trim()
            }
            // Normalize Windows backslashes to forward slashes
            s = s.replace('\\', '/')
            // Remove trailing slash unless it's just "/"
            if (s.length > 1 && s.endsWith("/")) {
                s = s.substring(0, s.length - 1)
            }
            return s
        }
    }

    fun changeDirectory(path: String): Boolean {
        val targetFile = resolveVirtualPath(path)
        if (targetFile.exists() && targetFile.isDirectory && isWithinRoot(targetFile)) {
            currentVirtualDir = toVirtualPath(targetFile)
            return true
        }
        val relPath = toVirtualPath(targetFile).trimStart('/')
        val doc = findDocument(relPath)
        if (doc != null && doc.isDirectory) {
            currentVirtualDir = toVirtualPath(targetFile)
            return true
        }
        return false
    }

    fun changeToParentDirectory(): Boolean {
        if (currentVirtualDir == "/") return true
        val target = resolveVirtualPath("..")
        return if (target.exists() && target.isDirectory && isWithinRoot(target)) {
            currentVirtualDir = toVirtualPath(target)
            true
        } else {
            currentVirtualDir = "/"
            true
        }
    }

    fun getFile(path: String): File {
        return resolveVirtualPath(path)
    }

    fun resolveVirtualPath(rawPath: String): File {
        val clean = cleanFtpPath(rawPath)

        if (clean.isEmpty() || clean == "." || clean == "/") {
            return if (currentVirtualDir == "/" || currentVirtualDir.isEmpty()) {
                rootDir
            } else {
                File(rootDir, currentVirtualDir.trimStart('/'))
            }
        }

        val rootAbs = rootDir.absolutePath
        val rootCanon = try { rootDir.canonicalPath } catch (_: Exception) { rootAbs }

        // If the client sent a full physical path on the device
        if (clean == rootAbs || clean == rootCanon) {
            return rootDir
        }
        if (clean.startsWith("$rootCanon/")) {
            val rel = clean.removePrefix("$rootCanon/").trimStart('/')
            return if (rel.isEmpty()) rootDir else File(rootDir, rel)
        }
        if (clean.startsWith("$rootAbs/")) {
            val rel = clean.removePrefix("$rootAbs/").trimStart('/')
            return if (rel.isEmpty()) rootDir else File(rootDir, rel)
        }
        if (clean.startsWith("/storage/emulated/0/")) {
            val rel = clean.removePrefix("/storage/emulated/0/").trimStart('/')
            return if (rel.isEmpty()) rootDir else File(rootDir, rel)
        }
        if (clean.startsWith("/sdcard/")) {
            val rel = clean.removePrefix("/sdcard/").trimStart('/')
            return if (rel.isEmpty()) rootDir else File(rootDir, rel)
        }

        val target = if (clean.startsWith("/")) {
            // Absolute virtual path
            val rel = clean.trimStart('/')
            if (rel.isEmpty()) rootDir else File(rootDir, rel)
        } else {
            // Relative from current working virtual directory
            val currentRel = currentVirtualDir.trimStart('/').trimEnd('/')
            val currentActual = if (currentRel.isEmpty()) rootDir else File(rootDir, currentRel)
            File(currentActual, clean)
        }

        val normalized = try {
            target.canonicalFile
        } catch (_: Exception) {
            target.absoluteFile
        }

        return if (isWithinRoot(normalized)) normalized else rootDir
    }

    private fun isWithinRoot(file: File): Boolean {
        return try {
            val fileCanon = file.canonicalPath
            val rootCanon = rootDir.canonicalPath
            fileCanon == rootCanon || fileCanon.startsWith(rootCanon + File.separator) ||
                    file.absolutePath == rootDir.absolutePath || file.absolutePath.startsWith(rootDir.absolutePath + File.separator)
        } catch (_: Exception) {
            val abs = file.absolutePath
            val rootAbs = rootDir.absolutePath
            abs == rootAbs || abs.startsWith(rootAbs + File.separator)
        }
    }

    fun toVirtualPath(file: File): String {
        return try {
            val fileCanon = file.canonicalPath
            val rootCanon = rootDir.canonicalPath
            when {
                fileCanon == rootCanon -> "/"
                fileCanon.startsWith(rootCanon) -> {
                    val rel = fileCanon.substring(rootCanon.length).replace('\\', '/')
                    if (rel.startsWith("/")) rel else "/$rel"
                }
                file.absolutePath.startsWith(rootDir.absolutePath) -> {
                    val rel = file.absolutePath.substring(rootDir.absolutePath.length).replace('\\', '/')
                    if (rel.startsWith("/")) rel else "/$rel"
                }
                else -> "/"
            }
        } catch (_: Exception) {
            "/"
        }
    }

    // ==========================================
    // SAF (Storage Access Framework) Helpers
    // ==========================================

    private fun getRootDocumentFile(): DocumentFile? {
        val ctx = context ?: return null
        if (rootTreeUriString.isNotEmpty()) {
            try {
                val uri = Uri.parse(rootTreeUriString)
                val doc = DocumentFile.fromTreeUri(ctx, uri)
                if (doc != null && doc.canRead()) return doc
            } catch (_: Exception) {}
        }
        // Check persisted URI permissions from context
        try {
            val permissions = ctx.contentResolver.persistedUriPermissions
            for (perm in permissions) {
                if (perm.isWritePermission || perm.isReadPermission) {
                    val doc = DocumentFile.fromTreeUri(ctx, perm.uri)
                    if (doc != null && doc.canRead()) return doc
                }
            }
        } catch (_: Exception) {}
        return null
    }

    private fun findDocument(relativePath: String): DocumentFile? {
        val rootDoc = getRootDocumentFile() ?: return null
        val clean = relativePath.trimStart('/').trimEnd('/')
        if (clean.isEmpty()) return rootDoc
        val parts = clean.split("/").filter { it.isNotEmpty() && it != "." }
        var current: DocumentFile = rootDoc
        for (part in parts) {
            if (part == "..") {
                current = current.parentFile ?: current
                continue
            }
            val next = current.findFile(part) ?: return null
            current = next
        }
        return current
    }

    private fun findOrCreateDirectoryDoc(relativePath: String): DocumentFile? {
        val rootDoc = getRootDocumentFile() ?: return null
        val clean = relativePath.trimStart('/').trimEnd('/')
        if (clean.isEmpty()) return rootDoc
        val parts = clean.split("/").filter { it.isNotEmpty() && it != "." }
        var current: DocumentFile = rootDoc
        for (part in parts) {
            if (part == "..") {
                current = current.parentFile ?: current
                continue
            }
            var next = current.findFile(part)
            if (next == null || !next.isDirectory) {
                next = current.createDirectory(part)
            }
            if (next == null) return null
            current = next
        }
        return current
    }

    // ==========================================
    // File & Directory Operations
    // ==========================================

    fun makeDirectory(path: String): Boolean {
        if (isReadOnly) return false
        val file = resolveVirtualPath(path)
        if (file == rootDir && currentVirtualDir == "/") return false

        // 1. Try POSIX File API
        try {
            file.parentFile?.mkdirs()
            if (file.mkdirs() || file.mkdir() || (file.exists() && file.isDirectory)) {
                return true
            }
        } catch (_: Exception) {}

        // 2. Try SAF DocumentFile
        val relPath = toVirtualPath(file).trimStart('/')
        if (relPath.isNotEmpty()) {
            val created = findOrCreateDirectoryDoc(relPath)
            if (created != null && created.isDirectory) {
                return true
            }
        }

        return false
    }

    fun openOutputStream(path: String, append: Boolean): OutputStream? {
        if (isReadOnly) return null
        val file = resolveVirtualPath(path)

        // 1. Try POSIX FileOutputStream
        try {
            file.parentFile?.mkdirs()
            return FileOutputStream(file, append)
        } catch (_: Exception) {}

        // 2. Try SAF DocumentFile
        val ctx = context ?: return null
        val relPath = toVirtualPath(file).trimStart('/')
        val segments = relPath.split("/").filter { it.isNotEmpty() && it != "." }
        if (segments.isEmpty()) return null

        val fileName = segments.last()
        val parentRel = segments.dropLast(1).joinToString("/")
        val parentDoc = if (parentRel.isEmpty()) {
            getRootDocumentFile()
        } else {
            findOrCreateDirectoryDoc(parentRel)
        } ?: return null

        val existing = parentDoc.findFile(fileName)
        val targetDoc = if (existing != null && existing.isFile) {
            existing
        } else {
            parentDoc.createFile("application/octet-stream", fileName)
        } ?: return null

        return try {
            ctx.contentResolver.openOutputStream(targetDoc.uri, if (append) "wa" else "wt")
        } catch (_: Exception) {
            null
        }
    }

    fun openInputStream(path: String): InputStream? {
        val file = resolveVirtualPath(path)

        // 1. Try POSIX FileInputStream
        try {
            if (file.exists() && file.canRead()) {
                return FileInputStream(file)
            }
        } catch (_: Exception) {}

        // 2. Try SAF DocumentFile
        val ctx = context ?: return null
        val relPath = toVirtualPath(file).trimStart('/')
        val doc = findDocument(relPath) ?: return null
        return try {
            ctx.contentResolver.openInputStream(doc.uri)
        } catch (_: Exception) {
            null
        }
    }

    fun openParcelFileDescriptor(path: String, mode: String = "r"): ParcelFileDescriptor? {
        val file = resolveVirtualPath(path)
        try {
            if (file.exists() && file.canRead()) {
                return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            }
        } catch (_: Exception) {}

        val ctx = context ?: return null
        val relPath = toVirtualPath(file).trimStart('/')
        val doc = findDocument(relPath) ?: return null
        return try {
            ctx.contentResolver.openFileDescriptor(doc.uri, mode)
        } catch (_: Exception) {
            null
        }
    }

    fun deleteFile(path: String): Boolean {
        if (isReadOnly) return false
        val file = resolveVirtualPath(path)
        try {
            if (file.exists() && file.isFile && file.delete()) return true
        } catch (_: Exception) {}

        val relPath = toVirtualPath(file).trimStart('/')
        val doc = findDocument(relPath)
        return if (doc != null && doc.isFile) doc.delete() else false
    }

    fun removeDirectory(path: String): Boolean {
        if (isReadOnly) return false
        val file = resolveVirtualPath(path)
        try {
            if (file.exists() && file.isDirectory && file != rootDir && file.deleteRecursively()) return true
        } catch (_: Exception) {}

        val relPath = toVirtualPath(file).trimStart('/')
        if (relPath.isEmpty()) return false
        val doc = findDocument(relPath)
        return if (doc != null && doc.isDirectory) doc.delete() else false
    }

    fun rename(fromPath: String, toPath: String): Boolean {
        if (isReadOnly) return false
        val fromFile = resolveVirtualPath(fromPath)
        val toFile = resolveVirtualPath(toPath)
        val isCaseOnlyChange = fromPath.equals(toPath, ignoreCase = true) && fromPath != toPath
        try {
            toFile.parentFile?.mkdirs()
            if (fromFile.exists()) {
                if (isCaseOnlyChange) {
                    val tempFile = File(fromFile.parentFile, ".__ufs_tmp_${System.currentTimeMillis()}_${fromFile.name}")
                    if (fromFile.renameTo(tempFile)) {
                        if (tempFile.renameTo(toFile)) return true
                        tempFile.renameTo(fromFile)
                    }
                } else if (!toFile.exists() && fromFile.renameTo(toFile)) {
                    return true
                }
            }
        } catch (_: Exception) {}

        val fromRel = toVirtualPath(fromFile).trimStart('/')
        val doc = findDocument(fromRel) ?: return false
        val toName = toFile.name
        return doc.renameTo(toName)
    }

    /**
     * Copies a file or directory (recursively) to [targetDirectoryVirtualPath].
     * Supports both POSIX File API and SAF DocumentFile.
     */
    fun copyPath(
        sourceVirtualPath: String,
        targetDirectoryVirtualPath: String,
        customTargetName: String? = null
    ): Boolean {
        if (isReadOnly) return false
        val srcNorm = cleanFtpPath(sourceVirtualPath).let { if (it.startsWith("/")) it else "/$it" }
        val dstDirNorm = cleanFtpPath(targetDirectoryVirtualPath).let {
            if (it.isEmpty() || it == "/") "/" else if (it.startsWith("/")) it else "/$it"
        }

        if (srcNorm == "/" || !exists(srcNorm)) return false
        if (!exists(dstDirNorm) || !isDirectory(dstDirNorm)) return false

        val baseName = srcNorm.substringAfterLast('/')
        if (baseName.isEmpty()) return false

        val srcParent = srcNorm.substringBeforeLast('/').ifEmpty { "/" }
        val destName = when {
            !customTargetName.isNullOrBlank() -> customTargetName.trim().replace("/", "_")
            srcParent == dstDirNorm -> generateUniqueCopyName(dstDirNorm, baseName, isDirectory(srcNorm))
            else -> baseName
        }

        val destVirtualPath = if (dstDirNorm == "/") "/$destName" else "$dstDirNorm/$destName"

        // Prevent copying a directory into itself or its own subdirectory
        if (isDirectory(srcNorm)) {
            if (destVirtualPath == srcNorm || destVirtualPath.startsWith("$srcNorm/")) {
                return false
            }
        }

        return copyRecursiveInternal(srcNorm, destVirtualPath)
    }

    /**
     * Moves a file or directory into [targetDirectoryVirtualPath].
     */
    fun movePath(
        sourceVirtualPath: String,
        targetDirectoryVirtualPath: String,
        customTargetName: String? = null
    ): Boolean {
        if (isReadOnly) return false
        val srcNorm = cleanFtpPath(sourceVirtualPath).let { if (it.startsWith("/")) it else "/$it" }
        val dstDirNorm = cleanFtpPath(targetDirectoryVirtualPath).let {
            if (it.isEmpty() || it == "/") "/" else if (it.startsWith("/")) it else "/$it"
        }

        if (srcNorm == "/" || !exists(srcNorm)) return false
        if (!exists(dstDirNorm) || !isDirectory(dstDirNorm)) return false

        val baseName = srcNorm.substringAfterLast('/')
        if (baseName.isEmpty()) return false

        val destName = customTargetName?.trim()?.replace("/", "_")?.takeIf { it.isNotEmpty() } ?: baseName
        val srcParent = srcNorm.substringBeforeLast('/').ifEmpty { "/" }
        if (srcParent == dstDirNorm && destName == baseName) return true // Already in target directory

        val destVirtualPath = if (dstDirNorm == "/") "/$destName" else "$dstDirNorm/$destName"

        // Prevent moving a directory into itself or its own subdirectory
        if (isDirectory(srcNorm)) {
            if (destVirtualPath == srcNorm || destVirtualPath.startsWith("$srcNorm/")) {
                return false
            }
        }

        // 1. Try fast POSIX rename if destination does not exist
        val fromFile = resolveVirtualPath(srcNorm)
        val toFile = resolveVirtualPath(destVirtualPath)
        try {
            toFile.parentFile?.mkdirs()
            if (fromFile.exists() && !toFile.exists() && fromFile.renameTo(toFile)) {
                return true
            }
        } catch (_: Exception) {}

        // 2. Fallback to recursive copy + delete (works across SAF DocumentFile & overwritten targets)
        if (copyRecursiveInternal(srcNorm, destVirtualPath)) {
            return if (isDirectory(srcNorm)) {
                removeDirectory(srcNorm)
            } else {
                deleteFile(srcNorm)
            }
        }
        return false
    }

    fun suggestAvailableName(dirVirtualPath: String, originalName: String, isDir: Boolean = false): String {
        val cleanDir = cleanFtpPath(dirVirtualPath).let {
            if (it.isEmpty() || it == "/") "/" else if (it.startsWith("/")) it else "/$it"
        }
        val dotIndex = if (!isDir) originalName.lastIndexOf('.') else -1
        val rawStem = if (dotIndex > 0) originalName.substring(0, dotIndex) else originalName
        val ext = if (dotIndex > 0) originalName.substring(dotIndex) else ""

        val numberedMatch = Regex("^(.*?)\\s*\\((\\d+)\\)$").find(rawStem)
        val stem = if (numberedMatch != null && numberedMatch.groupValues[1].isNotBlank()) {
            numberedMatch.groupValues[1]
        } else {
            rawStem
        }

        var counter = if (numberedMatch != null) {
            (numberedMatch.groupValues[2].toIntOrNull() ?: 1) + 1
        } else {
            1
        }
        var candidate = "$stem ($counter)$ext"
        while (exists(if (cleanDir == "/") "/$candidate" else "$cleanDir/$candidate")) {
            counter++
            candidate = "$stem ($counter)$ext"
        }
        return candidate
    }

    private fun generateUniqueCopyName(dirVirtualPath: String, originalName: String, isDir: Boolean): String {
        val dotIndex = if (!isDir) originalName.lastIndexOf('.') else -1
        val stem = if (dotIndex > 0) originalName.substring(0, dotIndex) else originalName
        val ext = if (dotIndex > 0) originalName.substring(dotIndex) else ""

        var candidate = "$stem (Copy)$ext"
        var counter = 2
        while (exists(if (dirVirtualPath == "/") "/$candidate" else "$dirVirtualPath/$candidate")) {
            candidate = "$stem (Copy $counter)$ext"
            counter++
        }
        return candidate
    }

    private fun copyRecursiveInternal(srcVirtualPath: String, dstVirtualPath: String): Boolean {
        return if (isDirectory(srcVirtualPath)) {
            if (!exists(dstVirtualPath) && !makeDirectory(dstVirtualPath)) {
                return false
            }
            val children = getDirectoryItems(srcVirtualPath)
            var allOk = true
            for (child in children) {
                val childSrc = if (srcVirtualPath == "/") "/${child.name}" else "$srcVirtualPath/${child.name}"
                val childDst = if (dstVirtualPath == "/") "/${child.name}" else "$dstVirtualPath/${child.name}"
                if (!copyRecursiveInternal(childSrc, childDst)) {
                    allOk = false
                }
            }
            allOk
        } else {
            val input = openInputStream(srcVirtualPath) ?: return false
            val output = openOutputStream(dstVirtualPath, append = false)
            if (output == null) {
                try { input.close() } catch (_: Exception) {}
                return false
            }
            try {
                input.use { ins ->
                    output.use { outs ->
                        val buf = ByteArray(65536)
                        while (true) {
                            val read = ins.read(buf)
                            if (read <= 0) break
                            outs.write(buf, 0, read)
                        }
                        outs.flush()
                    }
                }
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    fun exists(path: String): Boolean {
        val file = resolveVirtualPath(path)
        if (file.exists()) return true
        val relPath = toVirtualPath(file).trimStart('/')
        return findDocument(relPath) != null
    }

    fun isDirectory(path: String): Boolean {
        val file = resolveVirtualPath(path)
        if (file.exists()) return file.isDirectory
        val relPath = toVirtualPath(file).trimStart('/')
        val doc = findDocument(relPath)
        return doc?.isDirectory == true
    }

    fun getFileLength(path: String): Long {
        val file = resolveVirtualPath(path)
        if (file.exists() && file.isFile) return file.length()
        val relPath = toVirtualPath(file).trimStart('/')
        val doc = findDocument(relPath)
        return if (doc != null && doc.isFile) doc.length() else -1L
    }

    fun getFileLastModified(path: String): Long {
        val file = resolveVirtualPath(path)
        if (file.exists()) return file.lastModified()
        val relPath = toVirtualPath(file).trimStart('/')
        val doc = findDocument(relPath)
        return doc?.lastModified() ?: 0L
    }

    // ==========================================
    // Directory Listing
    // ==========================================

    data class FtpItem(
        val name: String,
        val isDirectory: Boolean,
        val size: Long,
        val lastModified: Long
    )

    data class FtpSearchResultItem(
        val name: String,
        val virtualPath: String,
        val parentVirtualPath: String,
        val relativePath: String,
        val isDirectory: Boolean,
        val size: Long,
        val lastModified: Long
    )

    fun getDirectoryItems(path: String = ""): List<FtpItem> {
        val target = if (path.isEmpty()) resolveVirtualPath(currentVirtualDir) else resolveVirtualPath(path)
        val files = target.listFiles()
        if (files != null && files.isNotEmpty()) {
            return files.map {
                FtpItem(it.name, it.isDirectory, if (it.isDirectory) 4096L else it.length(), it.lastModified())
            }
        }

        // Try SAF DocumentFile
        val relPath = toVirtualPath(target).trimStart('/')
        val doc = findDocument(relPath)
        if (doc != null && doc.isDirectory) {
            val childDocs = doc.listFiles()
            return childDocs.map {
                FtpItem(
                    name = it.name ?: "unnamed",
                    isDirectory = it.isDirectory,
                    size = if (it.isDirectory) 4096L else it.length(),
                    lastModified = it.lastModified()
                )
            }
        }

        if (files != null) return emptyList()
        return emptyList()
    }

    /**
     * Recursively calculates the total byte size and file count of a directory.
     * Supports both POSIX File and SAF DocumentFile.
     */
    fun calculateDirectorySize(path: String, maxDepth: Int = 25): Triple<Long, Int, Int> {
        val cleanPath = cleanFtpPath(path).let {
            if (it.isEmpty() || it == "/") "/" else if (it.startsWith("/")) it else "/$it"
        }
        var totalBytes = 0L
        var fileCount = 0
        var folderCount = 0
        val visited = HashSet<String>()

        fun walk(currentDir: String, depth: Int) {
            if (depth > maxDepth) return
            val resolvedFile = resolveVirtualPath(currentDir)
            val visitKey = try {
                resolvedFile.canonicalPath
            } catch (_: Exception) {
                currentDir
            }
            if (!visited.add(visitKey)) return

            val items = getDirectoryItems(currentDir)
            for (item in items) {
                if (item.isDirectory) {
                    folderCount++
                    val childVirtual = if (currentDir == "/") {
                        "/${item.name}"
                    } else {
                        "${currentDir.trimEnd('/')}/${item.name}"
                    }
                    walk(childVirtual, depth + 1)
                } else {
                    totalBytes += item.size.coerceAtLeast(0L)
                    fileCount++
                }
            }
        }

        walk(cleanPath, 0)
        return Triple(totalBytes, fileCount, folderCount)
    }

    /**
     * Searches for files and folders matching [query] starting at [basePath].
     * When [recursive] is true, searches all subdirectories under [basePath] as well.
     */
    fun searchItems(
        basePath: String,
        query: String,
        recursive: Boolean,
        maxResults: Int = 1000,
        maxDepth: Int = 20
    ): List<FtpSearchResultItem> {
        val needle = query.trim().lowercase(Locale.ROOT)
        if (needle.isEmpty()) return emptyList()

        val cleanBase = cleanFtpPath(basePath).let {
            if (it.isEmpty() || it == "/") "/" else if (it.startsWith("/")) it else "/$it"
        }

        val results = mutableListOf<FtpSearchResultItem>()
        val visitedDirs = HashSet<String>()

        fun walk(currentDir: String, depth: Int) {
            if (results.size >= maxResults || depth > maxDepth) return
            val resolvedFile = resolveVirtualPath(currentDir)
            val visitKey = try {
                resolvedFile.canonicalPath
            } catch (_: Exception) {
                currentDir
            }
            if (!visitedDirs.add(visitKey)) return

            val items = getDirectoryItems(currentDir)
            val subDirsToVisit = mutableListOf<String>()

            for (item in items) {
                val itemVirtualPath = if (currentDir == "/") {
                    "/${item.name}"
                } else {
                    "${currentDir.trimEnd('/')}/${item.name}"
                }
                val relPath = if (cleanBase == "/") {
                    itemVirtualPath.removePrefix("/")
                } else {
                    itemVirtualPath.removePrefix("$cleanBase/").removePrefix("/")
                }

                if (item.name.lowercase(Locale.ROOT).contains(needle)) {
                    results.add(
                        FtpSearchResultItem(
                            name = item.name,
                            virtualPath = itemVirtualPath,
                            parentVirtualPath = currentDir,
                            relativePath = relPath,
                            isDirectory = item.isDirectory,
                            size = item.size,
                            lastModified = item.lastModified
                        )
                    )
                    if (results.size >= maxResults) return
                }

                if (recursive && item.isDirectory) {
                    subDirsToVisit.add(itemVirtualPath)
                }
            }

            if (recursive) {
                for (subDir in subDirsToVisit) {
                    if (results.size >= maxResults) break
                    walk(subDir, depth + 1)
                }
            }
        }

        walk(cleanBase, 0)
        return results
    }

    fun listUnix(path: String = ""): List<String> {
        val items = getDirectoryItems(path)
        val dateFormatRecent = SimpleDateFormat("MMM dd HH:mm", Locale.ENGLISH)
        val dateFormatOld = SimpleDateFormat("MMM dd  yyyy", Locale.ENGLISH)
        val sixMonthsAgo = System.currentTimeMillis() - 180L * 24 * 3600 * 1000

        val lines = mutableListOf<String>()
        val sorted = items.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))

        for (item in sorted) {
            val permissions = if (item.isDirectory) "drwxrwxrwx" else "-rw-rw-rw-"
            val links = if (item.isDirectory) "3" else "1"
            val owner = "ftp"
            val group = "ftp"
            val dateStr = if (item.lastModified > sixMonthsAgo && item.lastModified > 0) {
                dateFormatRecent.format(Date(item.lastModified))
            } else {
                dateFormatOld.format(Date(if (item.lastModified > 0) item.lastModified else System.currentTimeMillis()))
            }
            lines.add("$permissions $links $owner $group ${String.format(Locale.US, "%10d", item.size)} $dateStr ${item.name}")
        }
        return lines
    }

    fun listMlsd(path: String = ""): List<String> {
        val items = getDirectoryItems(path)
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH)

        val lines = mutableListOf<String>()
        val sorted = items.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))

        for (item in sorted) {
            val type = if (item.isDirectory) "dir" else "file"
            val modify = dateFormat.format(Date(if (item.lastModified > 0) item.lastModified else System.currentTimeMillis()))
            val perms = if (isReadOnly) {
                if (item.isDirectory) "el" else "r"
            } else {
                if (item.isDirectory) "cdeflmp" else "adfrw"
            }
            lines.add("Type=$type;Size=${item.size};Modify=$modify;Perm=$perms; ${item.name}")
        }
        return lines
    }

    fun listNames(path: String = ""): List<String> {
        return getDirectoryItems(path).map { it.name }
    }
}
