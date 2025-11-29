package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CompletarMisionRequest(
    val contenidoEntrega: String,
    val archivoUrl: String? = null,
    val comentariosEstudiante: String? = null
)







