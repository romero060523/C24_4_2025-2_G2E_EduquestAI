package com.eduquestia.frontend_mobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension para DataStore
private val Context.themeDataStore: DataStore<Preferences> by
        preferencesDataStore(name = "theme_preferences")

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

class ThemeManager(private val context: Context) {

    companion object {
        private val THEME_KEY = stringPreferencesKey("theme_mode")
    }

    // Flow del tema actual
    val themeMode: Flow<ThemeMode> =
            context.themeDataStore.data.map { preferences ->
                val themeName = preferences[THEME_KEY] ?: ThemeMode.SYSTEM.name
                try {
                    ThemeMode.valueOf(themeName)
                } catch (e: Exception) {
                    ThemeMode.SYSTEM
                }
            }

    // Guardar tema seleccionado
    suspend fun setThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { preferences -> preferences[THEME_KEY] = mode.name }
    }
}
