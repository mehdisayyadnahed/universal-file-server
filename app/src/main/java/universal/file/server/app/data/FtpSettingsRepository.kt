package universal.file.server.app.data

import android.content.Context
import android.content.SharedPreferences
import universal.file.server.app.utils.StorageHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FtpSettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("ftp_settings", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<FtpSettings> = _settingsFlow.asStateFlow()

    fun loadSettings(): FtpSettings {
        val defaultPath = StorageHelper.getDefaultStoragePath()
        val migratedDefaultProtocolHttp = prefs.getBoolean(KEY_DEFAULT_PROTOCOL_HTTP_MIGRATED, false)
        val rawProtocolName = prefs.getString(KEY_PROTOCOL, ServerProtocol.HTTP.name) ?: ServerProtocol.HTTP.name
        val rawProtocol = try {
            ServerProtocol.valueOf(rawProtocolName)
        } catch (_: Exception) {
            ServerProtocol.HTTP
        }
        val rawPort = prefs.getInt(KEY_PORT, rawProtocol.defaultPort)

        val (protocol, effectivePort) = if (!migratedDefaultProtocolHttp && rawProtocol == ServerProtocol.FTP && rawPort == ServerProtocol.FTP.defaultPort) {
            prefs.edit()
                .putString(KEY_PROTOCOL, ServerProtocol.HTTP.name)
                .putInt(KEY_PORT, ServerProtocol.HTTP.defaultPort)
                .putBoolean(KEY_DEFAULT_PROTOCOL_HTTP_MIGRATED, true)
                .apply()
            ServerProtocol.HTTP to ServerProtocol.HTTP.defaultPort
        } else {
            if (!migratedDefaultProtocolHttp) {
                prefs.edit().putBoolean(KEY_DEFAULT_PROTOCOL_HTTP_MIGRATED, true).apply()
            }
            rawProtocol to rawPort
        }

        val savedDisplayName = prefs.getString(KEY_ROOT_DISPLAY_NAME, "Internal Storage") ?: "Internal Storage"
        val cleanDisplayName = if (savedDisplayName.contains("حافظه داخلی")) "Internal Storage" else savedDisplayName
        val themeModeName = prefs.getString(KEY_THEME_MODE, AppThemeMode.AUTO.name) ?: AppThemeMode.AUTO.name
        val themeMode = try {
            AppThemeMode.valueOf(themeModeName)
        } catch (_: Exception) {
            AppThemeMode.AUTO
        }
        val migratedDefaultPassword = prefs.getBoolean(KEY_DEFAULT_PASSWORD_V2_MIGRATED, false)
        val rawPassword = prefs.getString(KEY_PASSWORD, "12345678") ?: "12345678"
        val effectivePassword = if (!migratedDefaultPassword && rawPassword == "123456") {
            prefs.edit()
                .putString(KEY_PASSWORD, "12345678")
                .putBoolean(KEY_DEFAULT_PASSWORD_V2_MIGRATED, true)
                .apply()
            "12345678"
        } else {
            if (!migratedDefaultPassword) {
                prefs.edit().putBoolean(KEY_DEFAULT_PASSWORD_V2_MIGRATED, true).apply()
            }
            rawPassword
        }
        return FtpSettings(
            protocol = protocol,
            port = effectivePort,
            isAnonymous = prefs.getBoolean(KEY_ANONYMOUS, false),
            username = prefs.getString(KEY_USERNAME, "android") ?: "android",
            password = effectivePassword,
            rootPath = prefs.getString(KEY_ROOT_PATH, defaultPath) ?: defaultPath,
            rootDisplayName = cleanDisplayName,
            rootTreeUriString = prefs.getString(KEY_ROOT_TREE_URI, "") ?: "",
            isReadOnly = prefs.getBoolean(KEY_READ_ONLY, false),
            isWakeLockEnabled = prefs.getBoolean(KEY_WAKE_LOCK, true),
            keepScreenOn = prefs.getBoolean(KEY_KEEP_SCREEN_ON, false),
            themeMode = themeMode
        )
    }

    fun saveSettings(settings: FtpSettings) {
        prefs.edit()
            .putString(KEY_PROTOCOL, settings.protocol.name)
            .putInt(KEY_PORT, settings.port)
            .putBoolean(KEY_ANONYMOUS, settings.isAnonymous)
            .putString(KEY_USERNAME, settings.username)
            .putString(KEY_PASSWORD, settings.password)
            .putString(KEY_ROOT_PATH, settings.rootPath)
            .putString(KEY_ROOT_DISPLAY_NAME, settings.rootDisplayName)
            .putString(KEY_ROOT_TREE_URI, settings.rootTreeUriString)
            .putBoolean(KEY_READ_ONLY, settings.isReadOnly)
            .putBoolean(KEY_WAKE_LOCK, settings.isWakeLockEnabled)
            .putBoolean(KEY_KEEP_SCREEN_ON, settings.keepScreenOn)
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .apply()

        _settingsFlow.value = settings
    }

    companion object {
        private const val KEY_PROTOCOL = "pref_protocol"
        private const val KEY_PORT = "pref_port"
        private const val KEY_ANONYMOUS = "pref_anonymous"
        private const val KEY_USERNAME = "pref_username"
        private const val KEY_PASSWORD = "pref_password"
        private const val KEY_ROOT_PATH = "pref_root_path"
        private const val KEY_ROOT_DISPLAY_NAME = "pref_root_display_name"
        private const val KEY_ROOT_TREE_URI = "pref_root_tree_uri"
        private const val KEY_READ_ONLY = "pref_read_only"
        private const val KEY_WAKE_LOCK = "pref_wake_lock"
        private const val KEY_KEEP_SCREEN_ON = "pref_keep_screen_on"
        private const val KEY_THEME_MODE = "pref_theme_mode"
        private const val KEY_DEFAULT_PASSWORD_V2_MIGRATED = "pref_default_password_v2_migrated"
        private const val KEY_DEFAULT_PROTOCOL_HTTP_MIGRATED = "pref_default_protocol_http_migrated"
    }
}
