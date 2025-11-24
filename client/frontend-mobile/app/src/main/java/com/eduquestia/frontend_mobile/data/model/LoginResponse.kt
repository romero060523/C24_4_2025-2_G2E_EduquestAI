package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LoginResponse(
    val id: String,
    val username: String,
    val email: String,
    val nombreCompleto: String,
    val rol: String,
    val avatarUrl: String? = null,
    val token: String,
    val message: String? = null
) {
    fun toUsuario(): Usuario {
        return Usuario(
            id = UUID.fromString(id),
            username = username,
            email = email,
            nombreCompleto = nombreCompleto,
            rol = rol,
            avatarUrl = avatarUrl,
            token = token
        )
    }
}


