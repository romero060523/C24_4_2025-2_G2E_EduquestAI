package com.eduquestia.frontend_mobile.data.repository

import com.eduquestia.frontend_mobile.data.model.ChatRequest
import com.eduquestia.frontend_mobile.data.model.ChatResponse
import com.eduquestia.frontend_mobile.data.remote.ConversacionResponse
import com.eduquestia.frontend_mobile.data.remote.MensajeResponse
import com.eduquestia.frontend_mobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Envía un mensaje al chat IA y obtiene la respuesta
     */
    suspend fun enviarMensaje(
        mensaje: String,
        usuarioId: String,
        rolUsuario: String,
        conversacionId: String? = null
    ): Result<ChatResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request = ChatRequest(
                    mensaje = mensaje,
                    usuarioId = usuarioId,
                    rolUsuario = rolUsuario,
                    conversacionId = conversacionId
                )
                val response = apiService.enviarMensajeChat(request)
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene todas las conversaciones de un usuario
     */
    suspend fun obtenerConversaciones(usuarioId: String): Result<List<ConversacionResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val conversaciones = apiService.obtenerConversaciones(usuarioId)
                Result.success(conversaciones)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene los mensajes de una conversación específica
     */
    suspend fun obtenerMensajes(
        conversacionId: String,
        usuarioId: String
    ): Result<List<MensajeResponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val mensajes = apiService.obtenerMensajes(conversacionId, usuarioId)
                Result.success(mensajes)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}







