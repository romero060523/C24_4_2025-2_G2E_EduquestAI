package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable

/**
 * Modelo de curso con información completa para estudiante
 * Corresponde a CursoEstudianteResponse del backend
 */
@Serializable
data class CursoEstudiante(
    val id: String,
    val codigoCurso: String,
    val nombre: String,
    val descripcion: String? = null,
    val imagenPortada: String? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val activo: Boolean = true,
    // Información del profesor
    val profesorNombre: String? = null,
    val profesorEmail: String? = null,
    // Estadísticas
    val progreso: Int = 0,
    val misionesCompletadas: Int = 0,
    val totalMisiones: Int = 0,
    val totalEstudiantes: Int = 0
)

/**
 * Modelo básico de curso (para endpoints que devuelven solo el curso)
 */
@Serializable
data class Curso(
    val id: String,
    val codigoCurso: String,
    val nombre: String,
    val descripcion: String? = null,
    val imagenPortada: String? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val activo: Boolean = true,
    val fechaCreacion: String? = null,
    val fechaActualizacion: String? = null
)
