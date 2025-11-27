package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Ranking(
    val cursoId: String? = null,
    val cursoNombre: String,
    val estudiantes: List<RankingEstudiante>,
    val totalEstudiantes: Int
)

@Serializable
data class RankingEstudiante(
    val estudianteId: String,
    val nombreEstudiante: String,
    val puntosTotales: Int,
    val nivel: Int,
    val nombreNivel: String,
    val misionesCompletadas: Int,
    val posicion: Int
)

