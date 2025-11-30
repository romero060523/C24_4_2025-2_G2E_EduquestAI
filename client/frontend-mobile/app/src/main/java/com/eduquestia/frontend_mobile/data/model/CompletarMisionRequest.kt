package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable

/**
 * Request para completar una misión
 */
@Serializable
data class CompletarMisionRequest(
    val contenidoEntrega: String,
    val archivoUrl: String? = null,
    val comentariosEstudiante: String? = null
)

