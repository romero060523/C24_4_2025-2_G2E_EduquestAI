package com.eduquestia.frontend_mobile.data.repository

import com.eduquestia.frontend_mobile.data.model.PerfilGamificado
import com.eduquestia.frontend_mobile.data.model.Ranking
import com.eduquestia.frontend_mobile.data.remote.ApiService
import com.eduquestia.frontend_mobile.data.remote.RetrofitClient

class GamificacionRepository(
    private val apiService: ApiService = RetrofitClient.apiService
) {

    suspend fun obtenerPerfilGamificado(estudianteId: String): Result<PerfilGamificado> {
        return try {
            val response = apiService.obtenerPerfilGamificado(estudianteId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener perfil gamificado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerRankingGlobal(): Result<Ranking> {
        return try {
            val response = apiService.obtenerRankingGlobal()
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener ranking global"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerRankingPorCurso(cursoId: String): Result<Ranking> {
        return try {
            val response = apiService.obtenerRankingPorCurso(cursoId)
            if (response.success && response.data != null) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener ranking del curso"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

