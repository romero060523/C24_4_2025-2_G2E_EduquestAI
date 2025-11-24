package com.eduquestia.frontend_mobile.data.model

import java.util.UUID

data class Usuario(
    val id: UUID,
    val username: String,
    val email: String,
    val nombreCompleto: String,
    val rol: String,
    val avatarUrl: String? = null,
    val token: String
) {
    val esEstudiante: Boolean
        get() = rol.equals("estudiante", ignoreCase = true)

    val esProfesor: Boolean
        get() = rol.equals("profesor", ignoreCase = true)

    val esAdmin: Boolean
        get() = rol.equals("admin", ignoreCase = true)
}


