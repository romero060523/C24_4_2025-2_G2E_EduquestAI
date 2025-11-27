package com.eduquestia.frontend_mobile.data.repository

import com.eduquestia.frontend_mobile.data.model.CursoEstudiante
import com.eduquestia.frontend_mobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CursoRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Obtiene los cursos en los que está inscrito un estudiante
     * con información completa (profesor, progreso, misiones)
     */
    suspend fun obtenerCursosPorEstudiante(estudianteId: String): Result<List<CursoEstudiante>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.obtenerCursosPorEstudiante(estudianteId)
                if (response.success && response.data != null) {
                    Result.success(response.data)
                } else {
                    Result.failure(Exception(response.message ?: "Error al obtener cursos"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
