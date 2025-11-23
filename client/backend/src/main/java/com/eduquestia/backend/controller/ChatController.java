package com.eduquestia.backend.controller;

import com.eduquestia.backend.dto.request.ChatRequestDTO;
import com.eduquestia.backend.dto.response.ChatResponseDTO;
import com.eduquestia.backend.dto.response.ConversacionDTO;
import com.eduquestia.backend.dto.response.MensajeDTO;
import com.eduquestia.backend.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    /**
     * Envía un mensaje al chat y obtiene respuesta de la IA
     */
    @PostMapping
    public ResponseEntity<?> enviarMensaje(@Valid @RequestBody ChatRequestDTO request) {
        try {
            ChatResponseDTO response = chatService.enviarMensaje(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar el mensaje: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene todas las conversaciones de un usuario
     */
    @GetMapping("/conversaciones")
    public ResponseEntity<?> obtenerConversaciones(@RequestParam UUID usuarioId) {
        try {
            List<ConversacionDTO> conversaciones = chatService.obtenerConversaciones(usuarioId);
            return ResponseEntity.ok(conversaciones);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener conversaciones: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene todos los mensajes de una conversación
     */
    @GetMapping("/conversaciones/{conversacionId}/mensajes")
    public ResponseEntity<?> obtenerMensajes(
            @PathVariable UUID conversacionId,
            @RequestParam UUID usuarioId) {
        try {
            List<MensajeDTO> mensajes = chatService.obtenerMensajes(conversacionId, usuarioId);
            return ResponseEntity.ok(mensajes);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener mensajes: " + e.getMessage());
        }
    }
}