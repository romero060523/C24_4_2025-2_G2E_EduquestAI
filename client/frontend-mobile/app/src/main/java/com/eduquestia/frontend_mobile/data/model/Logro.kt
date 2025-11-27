package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Logro(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val icono: String? = null,
    val puntosRequeridos: Int,
    val fechaObtenido: String? = null, // ISO date string
    val obtenido: Boolean
)

