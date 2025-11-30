package com.eduquestia.frontend_mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.eduquestia.frontend_mobile.data.model.AvatarTipo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.avatarDataStore by preferencesDataStore(name = "avatar_settings")

/**
 * Manager para persistir la selección de avatar del usuario
 */
class AvatarManager(private val context: Context) {

    companion object {
        private val SELECTED_AVATAR_KEY = stringPreferencesKey("selected_avatar")
    }

    /**
     * Flow del avatar seleccionado actualmente
     */
    val selectedAvatar: Flow<AvatarTipo> = context.avatarDataStore.data
        .map { preferences ->
            val avatarName = preferences[SELECTED_AVATAR_KEY] ?: AvatarTipo.PERSONAJE_1.name
            AvatarTipo.fromString(avatarName)
        }

    /**
     * Guardar el avatar seleccionado
     */
    suspend fun saveSelectedAvatar(avatar: AvatarTipo) {
        context.avatarDataStore.edit { preferences ->
            preferences[SELECTED_AVATAR_KEY] = avatar.name
        }
    }
}

