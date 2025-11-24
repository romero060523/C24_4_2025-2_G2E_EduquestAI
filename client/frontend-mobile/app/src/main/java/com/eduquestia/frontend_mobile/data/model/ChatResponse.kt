package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val respuesta: String,
    val conversacionId: String,
    val mensajeId: String
)

