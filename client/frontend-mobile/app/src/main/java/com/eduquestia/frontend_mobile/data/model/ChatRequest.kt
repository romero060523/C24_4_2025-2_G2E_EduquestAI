package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChatRequest(
    val mensaje: String,
    val conversacionId: String? = null,
    val usuarioId: String,
    val rolUsuario: String // "estudiante" o "profesor"
)

