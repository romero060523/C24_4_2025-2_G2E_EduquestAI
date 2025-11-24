package com.eduquestia.frontend_mobile.data.remote

import com.eduquestia.frontend_mobile.data.model.ApiResponse
import com.eduquestia.frontend_mobile.data.model.ChatRequest
import com.eduquestia.frontend_mobile.data.model.ChatResponse
import com.eduquestia.frontend_mobile.data.model.LoginRequest
import com.eduquestia.frontend_mobile.data.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    // Chat
    @POST("chat")
    suspend fun enviarMensaje(@Body request: ChatRequest): ChatResponse

    @GET("chat/conversaciones")
    suspend fun obtenerConversaciones(@Query("usuarioId") usuarioId: String): List<Any>

    @GET("chat/conversaciones/{conversacionId}/mensajes")
    suspend fun obtenerMensajes(
        @Path("conversacionId") conversacionId: String,
        @Query("usuarioId") usuarioId: String
    ): List<Any>
}

