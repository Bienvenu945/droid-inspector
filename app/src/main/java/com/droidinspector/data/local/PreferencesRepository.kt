package com.droidinspector.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "droid_inspector_prefs")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val REFRESH_INTERVAL_MS = longPreferencesKey("refresh_interval_ms")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DARK_THEME] ?: true
    }

    val refreshIntervalMs: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[Keys.REFRESH_INTERVAL_MS] ?: 2000L
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_THEME] = enabled
        }
    }

    suspend fun setRefreshIntervalMs(intervalMs: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.REFRESH_INTERVAL_MS] = intervalMs
        }
    }
}
