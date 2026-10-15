package universal.file.server.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import universal.file.server.app.data.AppThemeMode
import universal.file.server.app.data.FtpSettingsRepository
import universal.file.server.app.data.NetworkType
import universal.file.server.app.utils.NetworkHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Universal File Server", appName)
    }

    @Test
    fun `only wifi hotspot usb tethering and bluetooth network are supported`() {
        assertEquals(NetworkType.WIFI, NetworkHelper.classifySupportedInterface("wlan0", "192.168.1.10")?.first)
        assertEquals(NetworkType.HOTSPOT, NetworkHelper.classifySupportedInterface("ap0", "192.168.43.1")?.first)
        assertEquals(NetworkType.HOTSPOT, NetworkHelper.classifySupportedInterface("swlan0", "192.168.50.1")?.first)
        assertEquals(NetworkType.USB_TETHERING, NetworkHelper.classifySupportedInterface("rndis0", "192.168.42.129")?.first)
        assertEquals(NetworkType.USB_TETHERING, NetworkHelper.classifySupportedInterface("ncm0", "192.168.42.1")?.first)
        assertEquals(NetworkType.BLUETOOTH, NetworkHelper.classifySupportedInterface("bt-pan", "192.168.44.1")?.first)
        assertEquals(NetworkType.BLUETOOTH, NetworkHelper.classifySupportedInterface("bnep0", "192.168.44.2")?.first)

        // Cellular, Ethernet, and other interfaces must be rejected
        assertNull(NetworkHelper.classifySupportedInterface("seth_lte8", "10.123.45.67"))
        assertNull(NetworkHelper.classifySupportedInterface("rmnet_data0", "10.0.0.1"))
        assertNull(NetworkHelper.classifySupportedInterface("ccmni0", "100.64.0.1"))
        assertNull(NetworkHelper.classifySupportedInterface("eth0", "192.168.1.50"))
        assertNull(NetworkHelper.classifySupportedInterface("tun0", "10.8.0.2"))
    }

    @Test
    fun `settings repository loads and saves correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = FtpSettingsRepository(context)
        val initial = repo.loadSettings()
        assertNotNull(initial)
        assertEquals(8080, initial.port)
        assertEquals(AppThemeMode.AUTO, initial.themeMode)

        val updated = initial.copy(port = 8021, username = "testuser", isAnonymous = true, themeMode = AppThemeMode.LIGHT)
        repo.saveSettings(updated)
        val loaded = repo.loadSettings()
        assertEquals(8021, loaded.port)
        assertEquals("testuser", loaded.username)
        assertEquals(true, loaded.isAnonymous)
        assertEquals(AppThemeMode.LIGHT, loaded.themeMode)
    }

    @Test
    fun `http server serves web ui uploads nested folder files and streams zip`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempRoot = java.io.File(context.cacheDir, "http_test_root").apply {
            deleteRecursively()
            mkdirs()
        }
        val port = java.net.ServerSocket(0).use { it.localPort }
        val settings = universal.file.server.app.data.FtpSettings(
            protocol = universal.file.server.app.data.ServerProtocol.HTTP,
            port = port,
            isAnonymous = true,
            rootPath = tempRoot.absolutePath,
            isReadOnly = false
        )

        val engine = universal.file.server.app.server.HttpServerEngine(
            context = context,
            settings = settings,
            onLog = { _, _, _ -> },
            onConnectionCountChanged = {}
        )

        try {
            org.junit.Assert.assertTrue(engine.start())

            // 1. Upload a file inside a nested folder path
            val uploadUrl = java.net.URL("http://127.0.0.1:$port/api/upload?path=/&relativePath=MyFolder/SubDir/hello.txt")
            val payload = "Hello HTTP Folder Upload & ZIP!".toByteArray()
            val conn = (uploadUrl.openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setFixedLengthStreamingMode(payload.size)
            }
            conn.outputStream.use { it.write(payload) }
            assertEquals(200, conn.responseCode)

            val uploadedFile = java.io.File(tempRoot, "MyFolder/SubDir/hello.txt")
            org.junit.Assert.assertTrue(uploadedFile.exists())
            assertEquals("Hello HTTP Folder Upload & ZIP!", uploadedFile.readText())

            // 2. Download MyFolder as on-the-fly streaming ZIP
            val zipUrl = java.net.URL("http://127.0.0.1:$port/api/zip?path=/MyFolder")
            val zipConn = zipUrl.openConnection() as java.net.HttpURLConnection
            assertEquals(200, zipConn.responseCode)

            val entries = mutableMapOf<String, String>()
            java.util.zip.ZipInputStream(zipConn.inputStream).use { zis ->
                while (true) {
                    val entry = zis.nextEntry ?: break
                    val content = zis.readBytes().toString(Charsets.UTF_8)
                    entries[entry.name] = content
                    zis.closeEntry()
                }
            }
            assertEquals("Hello HTTP Folder Upload & ZIP!", entries["MyFolder/SubDir/hello.txt"])

            // 3. Test batch copy (copy MyFolder into DestDir)
            java.io.File(tempRoot, "DestDir").mkdirs()
            val copyBody = """{"items":["/MyFolder"],"targetDir":"/DestDir"}""".toByteArray()
            val copyConn = (java.net.URL("http://127.0.0.1:$port/api/batch-copy").openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setFixedLengthStreamingMode(copyBody.size)
            }
            copyConn.outputStream.use { it.write(copyBody) }
            assertEquals(200, copyConn.responseCode)
            org.junit.Assert.assertTrue(java.io.File(tempRoot, "DestDir/MyFolder/SubDir/hello.txt").exists())

            // 4. Test batch move (move MyFolder into MovedDir)
            java.io.File(tempRoot, "MovedDir").mkdirs()
            val moveBody = """{"items":["/MyFolder"],"targetDir":"/MovedDir"}""".toByteArray()
            val moveConn = (java.net.URL("http://127.0.0.1:$port/api/batch-move").openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setFixedLengthStreamingMode(moveBody.size)
            }
            moveConn.outputStream.use { it.write(moveBody) }
            assertEquals(200, moveConn.responseCode)
            org.junit.Assert.assertFalse(java.io.File(tempRoot, "MyFolder").exists())
            org.junit.Assert.assertTrue(java.io.File(tempRoot, "MovedDir/MyFolder/SubDir/hello.txt").exists())

            // 5. Test batch delete
            val deleteBody = """{"items":["/DestDir","/MovedDir"]}""".toByteArray()
            val deleteConn = (java.net.URL("http://127.0.0.1:$port/api/batch-delete").openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setFixedLengthStreamingMode(deleteBody.size)
            }
            deleteConn.outputStream.use { it.write(deleteBody) }
            assertEquals(200, deleteConn.responseCode)
            org.junit.Assert.assertFalse(java.io.File(tempRoot, "DestDir").exists())
            org.junit.Assert.assertFalse(java.io.File(tempRoot, "MovedDir").exists())
        } finally {
            engine.stop()
            tempRoot.deleteRecursively()
        }
    }

    @Test
    fun `http server with auth enabled serves themed web ui and authenticates via login api without browser basic auth popup`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempRoot = java.io.File(context.cacheDir, "http_auth_test_root").apply {
            deleteRecursively()
            mkdirs()
        }
        val port = java.net.ServerSocket(0).use { it.localPort }
        val settings = universal.file.server.app.data.FtpSettings(
            protocol = universal.file.server.app.data.ServerProtocol.HTTP,
            port = port,
            isAnonymous = false,
            username = "admin",
            password = "secret123",
            rootPath = tempRoot.absolutePath,
            isReadOnly = false
        )

        val engine = universal.file.server.app.server.HttpServerEngine(
            context = context,
            settings = settings,
            onLog = { _, _, _ -> },
            onConnectionCountChanged = {}
        )

        try {
            org.junit.Assert.assertTrue(engine.start())

            // 1. GET / must return 200 OK with custom themed modals (#authModal, #dialogModal)
            val indexConn = java.net.URL("http://127.0.0.1:$port/").openConnection() as java.net.HttpURLConnection
            assertEquals(200, indexConn.responseCode)
            assertNull(indexConn.getHeaderField("WWW-Authenticate"))
            val html = indexConn.inputStream.bufferedReader().readText()
            org.junit.Assert.assertTrue(html.contains("id=\"authModal\""))
            org.junit.Assert.assertTrue(html.contains("id=\"dialogModal\""))

            // 2. Unauthenticated API call returns 401 JSON without WWW-Authenticate header
            val unauthConn = java.net.URL("http://127.0.0.1:$port/api/list?path=/").openConnection() as java.net.HttpURLConnection
            assertEquals(401, unauthConn.responseCode)
            assertNull(unauthConn.getHeaderField("WWW-Authenticate"))

            // 3. POST /api/login with valid credentials without rememberMe returns session Set-Cookie (no Max-Age)
            val loginPayload = """{"username":"admin","password":"secret123","rememberMe":false}""".toByteArray()
            val loginConn = (java.net.URL("http://127.0.0.1:$port/api/login").openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setFixedLengthStreamingMode(loginPayload.size)
            }
            loginConn.outputStream.use { it.write(loginPayload) }
            assertEquals(200, loginConn.responseCode)
            val sessionCookie = loginConn.getHeaderField("Set-Cookie")
            assertNotNull(sessionCookie)
            assertTrue(sessionCookie.contains("ufs_auth="))
            assertFalse(sessionCookie.contains("Max-Age"))
            val loginJson = org.json.JSONObject(loginConn.inputStream.bufferedReader().readText())
            org.junit.Assert.assertTrue(loginJson.getBoolean("ok"))
            val token = loginJson.getString("token")

            // 4. POST /api/login with rememberMe: true sets long-lived cookie (Max-Age)
            val rememberPayload = """{"username":"admin","password":"secret123","rememberMe":true}""".toByteArray()
            val rememberConn = (java.net.URL("http://127.0.0.1:$port/api/login").openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setFixedLengthStreamingMode(rememberPayload.size)
            }
            rememberConn.outputStream.use { it.write(rememberPayload) }
            assertEquals(200, rememberConn.responseCode)
            val rememberCookie = rememberConn.getHeaderField("Set-Cookie")
            assertNotNull(rememberCookie)
            assertTrue(rememberCookie.contains("ufs_auth="))
            assertTrue(rememberCookie.contains("Max-Age=315360000"))

            // 5. Subsequent API call with X-UFS-Auth token succeeds
            val authedConn = (java.net.URL("http://127.0.0.1:$port/api/list?path=/").openConnection() as java.net.HttpURLConnection).apply {
                setRequestProperty("X-UFS-Auth", token)
            }
            assertEquals(200, authedConn.responseCode)
        } finally {
            engine.stop()
            tempRoot.deleteRecursively()
        }
    }

    @Test
    fun `http server search supports both current directory and recursive subdirectories`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempRoot = java.io.File(context.cacheDir, "http_search_test_root").apply {
            deleteRecursively()
            mkdirs()
        }
        java.io.File(tempRoot, "report_root.txt").writeText("root report")
        java.io.File(tempRoot, "Docs/2026").mkdirs()
        java.io.File(tempRoot, "Docs/2026/report_nested.txt").writeText("nested report")
        java.io.File(tempRoot, "Photos").mkdirs()
        java.io.File(tempRoot, "Photos/vacation.jpg").writeText("jpg")

        val port = java.net.ServerSocket(0).use { it.localPort }
        val settings = universal.file.server.app.data.FtpSettings(
            protocol = universal.file.server.app.data.ServerProtocol.HTTP,
            port = port,
            isAnonymous = true,
            rootPath = tempRoot.absolutePath,
            isReadOnly = false
        )

        val engine = universal.file.server.app.server.HttpServerEngine(
            context = context,
            settings = settings,
            onLog = { _, _, _ -> },
            onConnectionCountChanged = {}
        )

        try {
            org.junit.Assert.assertTrue(engine.start())

            // Verify Web UI HTML contains search scope buttons
            val html = (java.net.URL("http://127.0.0.1:$port/").openConnection() as java.net.HttpURLConnection)
                .inputStream.bufferedReader().readText()
            org.junit.Assert.assertTrue(html.contains("id=\"btnScopeCurrent\""))
            org.junit.Assert.assertTrue(html.contains("id=\"btnScopeSubfolders\""))

            // 1. Search only in current folder (recursive=0)
            val nonRecConn = java.net.URL("http://127.0.0.1:$port/api/search?path=/&q=report&recursive=0")
                .openConnection() as java.net.HttpURLConnection
            assertEquals(200, nonRecConn.responseCode)
            val nonRecJson = org.json.JSONObject(nonRecConn.inputStream.bufferedReader().readText())
            val nonRecItems = nonRecJson.getJSONArray("items")
            assertEquals(1, nonRecItems.length())
            assertEquals("report_root.txt", nonRecItems.getJSONObject(0).getString("name"))

            // 2. Search in current folder and subfolders (recursive=1)
            val recConn = java.net.URL("http://127.0.0.1:$port/api/search?path=/&q=report&recursive=1")
                .openConnection() as java.net.HttpURLConnection
            assertEquals(200, recConn.responseCode)
            val recJson = org.json.JSONObject(recConn.inputStream.bufferedReader().readText())
            val recItems = recJson.getJSONArray("items")
            assertEquals(2, recItems.length())
            val foundPaths = (0 until recItems.length()).map { recItems.getJSONObject(it).getString("path") }.toSet()
            org.junit.Assert.assertTrue(foundPaths.contains("/report_root.txt"))
            org.junit.Assert.assertTrue(foundPaths.contains("/Docs/2026/report_nested.txt"))

            // 3. Search inside a specific subfolder (/Docs) with recursive=1
            val subRecConn = java.net.URL("http://127.0.0.1:$port/api/search?path=/Docs&q=report&recursive=1")
                .openConnection() as java.net.HttpURLConnection
            assertEquals(200, subRecConn.responseCode)
            val subRecJson = org.json.JSONObject(subRecConn.inputStream.bufferedReader().readText())
            val subRecItems = subRecJson.getJSONArray("items")
            assertEquals(1, subRecItems.length())
            assertEquals("/Docs/2026/report_nested.txt", subRecItems.getJSONObject(0).getString("path"))
            assertEquals("/Docs/2026", subRecItems.getJSONObject(0).getString("parentPath"))
        } finally {
            engine.stop()
            tempRoot.deleteRecursively()
        }
    }

    @Test
    fun `http server tracks active web sessions via heartbeat and decrements on tab leave`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val tempRoot = java.io.File(context.cacheDir, "http_conn_test_root").apply {
            deleteRecursively()
            mkdirs()
        }
        val port = java.net.ServerSocket(0).use { it.localPort }
        val settings = universal.file.server.app.data.FtpSettings(
            protocol = universal.file.server.app.data.ServerProtocol.HTTP,
            port = port,
            isAnonymous = true,
            rootPath = tempRoot.absolutePath,
            isReadOnly = false
        )

        var latestReportedCount = 0
        val engine = universal.file.server.app.server.HttpServerEngine(
            context = context,
            settings = settings,
            onLog = { _, _, _ -> },
            onConnectionCountChanged = { count -> latestReportedCount = count }
        )

        try {
            org.junit.Assert.assertTrue(engine.start())
            assertEquals(0, latestReportedCount)

            // Tab 1 opens and sends heartbeat
            val hb1 = java.net.URL("http://127.0.0.1:$port/api/heartbeat?sid=tab_alpha")
                .openConnection() as java.net.HttpURLConnection
            assertEquals(200, hb1.responseCode)
            val hb1Json = org.json.JSONObject(hb1.inputStream.bufferedReader().readText())
            assertEquals(1, hb1Json.getInt("activeConnections"))
            assertEquals(1, latestReportedCount)

            // Tab 2 opens and sends heartbeat
            val hb2 = java.net.URL("http://127.0.0.1:$port/api/heartbeat?sid=tab_beta")
                .openConnection() as java.net.HttpURLConnection
            assertEquals(200, hb2.responseCode)
            val hb2Json = org.json.JSONObject(hb2.inputStream.bufferedReader().readText())
            assertEquals(2, hb2Json.getInt("activeConnections"))
            assertEquals(2, latestReportedCount)

            // Tab 1 closes (action=leave)
            val leave1 = (java.net.URL("http://127.0.0.1:$port/api/heartbeat?sid=tab_alpha&action=leave")
                .openConnection() as java.net.HttpURLConnection).apply {
                requestMethod = "POST"
            }
            assertEquals(200, leave1.responseCode)
            val leave1Json = org.json.JSONObject(leave1.inputStream.bufferedReader().readText())
            assertEquals(1, leave1Json.getInt("activeConnections"))
            assertEquals(1, latestReportedCount)
        } finally {
            engine.stop()
            tempRoot.deleteRecursively()
        }
    }
}

