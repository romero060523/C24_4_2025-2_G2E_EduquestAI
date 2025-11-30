package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Modelo de Curso con información del estudiante
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

    // Estadísticas del estudiante
    val progreso: Int = 0, // Porcentaje de progreso
    val misionesCompletadas: Int = 0,
    val totalMisiones: Int = 0,
    val totalEstudiantes: Int = 0
)


