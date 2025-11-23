package com.eduquestia.backend.service;

import com.eduquestia.backend.dto.request.ChatRequestDTO;
import com.eduquestia.backend.dto.response.ChatResponseDTO;
import com.eduquestia.backend.dto.response.ConversacionDTO;
import com.eduquestia.backend.dto.response.MensajeDTO;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    /**
     * Procesa un mensaje de chat y genera una respuesta de la IA
     * @param request Datos de la solicitud de chat
     * @return Respuesta de la IA con IDs de conversación y mensaje
     */
    ChatResponseDTO enviarMensaje(ChatRequestDTO request);

    /**
     * Obtiene todas las conversaciones de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de conversaciones ordenadas por fecha de actualización
     */
    List<ConversacionDTO> obtenerConversaciones(UUID usuarioId);

    /**
     * Obtiene todos los mensajes de una conversación
     * @param conversacionId ID de la conversación
     * @param usuarioId ID del usuario (para verificar permisos)
     * @return Lista de mensajes ordenados cronológicamente
     */
    List<MensajeDTO> obtenerMensajes(UUID conversacionId, UUID usuarioId);
}
