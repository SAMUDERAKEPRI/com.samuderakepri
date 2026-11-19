package com.samuderakepri.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "samudera_kepri_prefs")

data class UserPreferences(
    val isDarkTheme: Boolean = false,
    val fontSizeScale: Float = 1.0f,
    val autoCacheOffline: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val hasGelarFaktaAccess: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val FONT_SCALE = floatPreferencesKey("font_scale")
        val AUTO_CACHE = booleanPreferencesKey("auto_cache_offline")
        val NOTIFICATIONS = booleanPreferencesKey("notifications_enabled")
        val GELAR_FAKTA_ACCESS = booleanPreferencesKey("has_gelar_fakta_access")
    }

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            isDarkTheme = prefs[Keys.DARK_THEME] ?: false,
            fontSizeScale = prefs[Keys.FONT_SCALE] ?: 1.0f,
            autoCacheOffline = prefs[Keys.AUTO_CACHE] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS] ?: true,
            hasGelarFaktaAccess = prefs[Keys.GELAR_FAKTA_ACCESS] ?: false
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DARK_THEME] = enabled }
    }

    suspend fun setFontSizeScale(scale: Float) {
        context.dataStore.edit { it[Keys.FONT_SCALE] = scale }
    }

    suspend fun setAutoCacheOffline(enabled: Boolean) {
        context.dataStore.edit { it[Keys.AUTO_CACHE] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS] = enabled }
    }

    suspend fun setGelarFaktaAccess(granted: Boolean) {
        context.dataStore.edit { it[Keys.GELAR_FAKTA_ACCESS] = granted }
    }
}
