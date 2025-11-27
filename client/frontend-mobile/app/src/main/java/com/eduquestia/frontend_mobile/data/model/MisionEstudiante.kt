package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MisionEstudiante(
    val id: String,
    val titulo: String,
    val descripcion: String? = null,
    val categoria: String, // "LECTURA", "EJERCICIO", "PROYECTO", "QUIZ", "DESAFIO"
    val dificultad: String, // "FACIL", "MEDIO", "DIFICIL", "EXPERTO"
    val puntosRecompensa: Int = 0,
    val experienciaRecompensa: Int = 0,
    val monedasRecompensa: Int = 0,
    val semanaClase: Int? = null,
    val temaVisual: String? = null,
    val fechaInicio: String? = null,
    val fechaLimite: String? = null,
    val activo: Boolean = true,
    val cursoNombre: String = "",
    // Información del progreso del estudiante
    val porcentajeCompletado: Int = 0,
    val completada: Boolean = false,
    val fechaCompletado: String? = null,
    val estadoEntrega: String = "PENDIENTE",
    val puntosObtenidos: Int? = null,
    val ultimaActividad: String? = null
)

