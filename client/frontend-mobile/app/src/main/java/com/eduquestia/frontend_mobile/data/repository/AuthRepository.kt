package com.eduquestia.frontend_mobile.data.repository

import com.eduquestia.frontend_mobile.data.local.TokenManager
import com.eduquestia.frontend_mobile.data.model.LoginRequest
import com.eduquestia.frontend_mobile.data.model.LoginResponse
import com.eduquestia.frontend_mobile.data.model.Usuario
import com.eduquestia.frontend_mobile.data.remote.ApiService
import com.eduquestia.frontend_mobile.data.remote.RetrofitClient

class AuthRepository(
    private val apiService: ApiService = RetrofitClient.apiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response = apiService.login(request)

            if (response.success && response.data != null) {
                val loginResponse = response.data
                val usuario = loginResponse.toUsuario()

                // Guardar token y datos del usuario
                tokenManager.saveToken(loginResponse.token)
                tokenManager.saveUser(
                    userId = loginResponse.id,
                    email = loginResponse.email,
                    rol = loginResponse.rol
                )

                Result.success(usuario)
            } else {
                Result.failure(Exception(response.message ?: "Error al iniciar sesión"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        tokenManager.clear()
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }
}

