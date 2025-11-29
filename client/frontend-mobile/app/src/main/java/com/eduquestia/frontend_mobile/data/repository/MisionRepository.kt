package com.eduquestia.frontend_mobile.data.repository

import com.eduquestia.frontend_mobile.data.model.CompletarMisionRequest
import com.eduquestia.frontend_mobile.data.model.MisionEstudiante
import com.eduquestia.frontend_mobile.data.remote.ApiService
import com.eduquestia.frontend_mobile.data.remote.RetrofitClient

class MisionRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun obtenerMisionesPorEstudiante(estudianteId: String): Result<List<MisionEstudiante>> {
        return try {
            val response = apiService.obtenerMisionesPorEstudiante(estudianteId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener misiones"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerMisionPorId(misionId: String, estudianteId: String): Result<MisionEstudiante> {
        return try {
            // Obtenemos todas las misiones del estudiante y filtramos
            val response = apiService.obtenerMisionesPorEstudiante(estudianteId)
            if (response.success && response.data != null) {
                val mision = response.data.find { it.id == misionId }
                if (mision != null) {
                    Result.success(mision)
                } else {
                    Result.failure(Exception("Misión no encontrada"))
                }
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener misión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completarMision(
        misionId: String,
        estudianteId: String,
        contenidoEntrega: String,
        archivoUrl: String? = null,
        comentarios: String? = null
    ): Result<MisionEstudiante> {
        return try {
            val request = CompletarMisionRequest(
                contenidoEntrega = contenidoEntrega,
                archivoUrl = archivoUrl,
                comentariosEstudiante = comentarios
            )
            val response = apiService.completarMision(misionId, request, estudianteId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al completar misión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

