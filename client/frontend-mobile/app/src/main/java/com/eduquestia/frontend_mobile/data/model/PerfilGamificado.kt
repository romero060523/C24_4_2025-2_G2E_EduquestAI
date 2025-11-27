package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PerfilGamificado(
    val puntosTotales: Int,
    val nivel: Int,
    val nombreNivel: String,
    val puntosParaSiguienteNivel: Int,
    val misionesCompletadas: Int,
    val logrosObtenidos: Int,
    val logros: List<Logro>,
    val posicionRanking: Int? = null
)

