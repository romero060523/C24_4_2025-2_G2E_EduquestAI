package com.eduquestia.frontend_mobile.data.model

import kotlinx.serialization.Serializable

/**
 * Modelo básico de Curso
 * Usado para representaciones simples de cursos
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
    val activo: Boolean = true
)
