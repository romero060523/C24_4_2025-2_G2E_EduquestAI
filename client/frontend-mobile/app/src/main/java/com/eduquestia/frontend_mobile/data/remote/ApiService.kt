package com.eduquestia.frontend_mobile.data.remote

import com.eduquestia.frontend_mobile.data.model.ApiResponse
import com.eduquestia.frontend_mobile.data.model.ChatRequest
import com.eduquestia.frontend_mobile.data.model.ChatResponse
import com.eduquestia.frontend_mobile.data.model.CursoEstudiante
import com.eduquestia.frontend_mobile.data.model.LoginRequest
import com.eduquestia.frontend_mobile.data.model.LoginResponse
import com.eduquestia.frontend_mobile.data.model.MisionEstudiante
import com.eduquestia.frontend_mobile.data.model.PerfilGamificado
import com.eduquestia.frontend_mobile.data.model.Ranking
import com.eduquestia.frontend_mobile.data.model.CompletarMisionRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // ========== AUTH ==========
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    // ========== CURSOS ==========
    @GET("cursos/por-estudiante/{estudianteId}")
    suspend fun obtenerCursosPorEstudiante(
        @Path("estudianteId") estudianteId: String
    ): ApiResponse<List<CursoEstudiante>>

    @GET("cursos")
    suspend fun obtenerTodosLosCursos(): Map<String, Any>

    @GET("cursos/{id}")
    suspend fun obtenerCursoPorId(
        @Path("id") cursoId: String
    ): Map<String, Any>

    // ========== MISIONES ==========
    @GET("misiones/estudiante/{estudianteId}")
    suspend fun obtenerMisionesPorEstudiante(
        @Path("estudianteId") estudianteId: String
    ): ApiResponse<List<MisionEstudiante>>

    @GET("misiones/estudiante/{estudianteId}/puntos")
    suspend fun obtenerPuntosTotalesEstudiante(
        @Path("estudianteId") estudianteId: String
    ): ApiResponse<Int>

    @GET("misiones/{id}")
    suspend fun obtenerMisionPorId(
        @Path("id") misionId: String
    ): ApiResponse<MisionEstudiante>

    @GET("misiones/curso/{cursoId}")
    suspend fun obtenerMisionesPorCurso(
        @Path("cursoId") cursoId: String
    ): ApiResponse<List<MisionEstudiante>>

    @POST("misiones/{misionId}/completar")
    suspend fun completarMision(
        @Path("misionId") misionId: String,
        @Body request: CompletarMisionRequest,
        @Header("X-Estudiante-Id") estudianteId: String
    ): ApiResponse<MisionEstudiante>

    // ========== GAMIFICACIÓN ==========
    @GET("gamificacion/estudiante/{estudianteId}/perfil")
    suspend fun obtenerPerfilGamificado(
        @Path("estudianteId") estudianteId: String
    ): ApiResponse<PerfilGamificado>

    @GET("gamificacion/ranking/global")
    suspend fun obtenerRankingGlobal(): ApiResponse<Ranking>

    @GET("gamificacion/ranking/curso/{cursoId}")
    suspend fun obtenerRankingPorCurso(
        @Path("cursoId") cursoId: String
    ): ApiResponse<Ranking>

    @GET("gamificacion/recompensas/estudiante/{estudianteId}")
    suspend fun obtenerRecompensasEstudiante(
        @Path("estudianteId") estudianteId: String
    ): ApiResponse<List<Any>>

    // ========== CHAT IA ==========
    @POST("chat")
    suspend fun enviarMensajeChat(@Body request: ChatRequest): ChatResponse

    @GET("chat/conversaciones")
    suspend fun obtenerConversaciones(
        @Query("usuarioId") usuarioId: String
    ): List<ConversacionResponse>

    @GET("chat/conversaciones/{conversacionId}/mensajes")
    suspend fun obtenerMensajes(
        @Path("conversacionId") conversacionId: String,
        @Query("usuarioId") usuarioId: String
    ): List<MensajeResponse>
}

// DTOs adicionales para Chat
@kotlinx.serialization.Serializable
data class ConversacionResponse(
    val id: String,
    val titulo: String? = null,
    val fechaCreacion: String? = null,
    val ultimoMensaje: String? = null
)

@kotlinx.serialization.Serializable
data class MensajeResponse(
    val id: String,
    val contenido: String,
    val esUsuario: Boolean,
    val fechaCreacion: String
)
