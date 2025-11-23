package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.ChatRequestDTO;
import com.eduquestia.backend.dto.response.ChatResponseDTO;
import com.eduquestia.backend.dto.response.ConversacionDTO;
import com.eduquestia.backend.dto.response.MensajeDTO;
import com.eduquestia.backend.entity.Conversacion;
import com.eduquestia.backend.entity.Mensaje;
import com.eduquestia.backend.repository.ConversacionRepository;
import com.eduquestia.backend.repository.MensajeRepository;
import com.eduquestia.backend.service.ChatService;
import com.eduquestia.backend.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final GeminiService geminiService;
    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;

    private static final String PROMPT_ESTUDIANTE = """
            Eres un asistente educativo virtual llamado "EduQuest AI" para una plataforma de aprendizaje gamificado.
            
            Tu rol es:
            - Ayudar a estudiantes con dudas sobre sus cursos y misiones
            - Explicar conceptos de forma clara y didáctica
            - Motivar a los estudiantes a seguir aprendiendo
            - Ser amigable, paciente y alentador
            - Responder en español de forma concisa (máximo 150 palabras)
            
            NO debes:
            - Resolver tareas o exámenes completamente
            - Dar respuestas sin explicación
            - Hablar de temas no relacionados con educación
            
            Siempre termina tus respuestas preguntando si el estudiante tiene más dudas.
            """;

    private static final String PROMPT_PROFESOR = """
            Eres un asistente educativo virtual llamado "EduQuest AI" para una plataforma de aprendizaje gamificado.
            
            Tu rol es:
            - Ayudar a profesores con la gestión de sus cursos y evaluaciones
            - Proporcionar ideas para planificación de clases y misiones
            - Sugerir estrategias de evaluación y seguimiento de estudiantes
            - Ofrecer consejos sobre gamificación educativa
            - Responder en español de forma detallada y profesional
            
            Puedes proporcionar explicaciones más técnicas y detalladas que para estudiantes.
            Enfócate en aspectos pedagógicos y de gestión educativa.
            
            Siempre termina tus respuestas preguntando si necesita más asistencia.
            """;

    @Override
    @Transactional
    public ChatResponseDTO enviarMensaje(ChatRequestDTO request) {
        // Obtener o crear conversación
        Conversacion conversacion;
        if (request.getConversacionId() != null) {
            conversacion = conversacionRepository.findByUsuarioIdAndId(
                    request.getUsuarioId(), 
                    request.getConversacionId()
            ).orElseThrow(() -> new RuntimeException("Conversación no encontrada"));
        } else {
            conversacion = new Conversacion();
            conversacion.setUsuarioId(request.getUsuarioId());
            conversacion.setTitulo(generarTitulo(request.getMensaje()));
            conversacion = conversacionRepository.save(conversacion);
        }

        // Guardar mensaje del usuario
        Mensaje mensajeUsuario = new Mensaje();
        mensajeUsuario.setConversacion(conversacion);
        mensajeUsuario.setContenido(request.getMensaje());
        mensajeUsuario.setEsUsuario(true);
        mensajeRepository.save(mensajeUsuario);

        // Generar respuesta de la IA según el rol
        String systemPrompt = obtenerPromptPorRol(request.getRolUsuario());
        String respuestaIA = geminiService.generateResponse(systemPrompt, request.getMensaje());

        // Guardar respuesta de la IA
        Mensaje mensajeIA = new Mensaje();
        mensajeIA.setConversacion(conversacion);
        mensajeIA.setContenido(respuestaIA);
        mensajeIA.setEsUsuario(false);
        mensajeIA = mensajeRepository.save(mensajeIA);

        return new ChatResponseDTO(respuestaIA, conversacion.getId(), mensajeIA.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversacionDTO> obtenerConversaciones(UUID usuarioId) {
        List<Conversacion> conversaciones = conversacionRepository
                .findByUsuarioIdOrderByFechaActualizacionDesc(usuarioId);

        return conversaciones.stream()
                .map(conv -> {
                    String ultimoMensaje = conv.getMensajes().isEmpty() 
                            ? "" 
                            : conv.getMensajes().get(conv.getMensajes().size() - 1).getContenido();
                    
                    // Truncar mensaje si es muy largo
                    if (ultimoMensaje.length() > 100) {
                        ultimoMensaje = ultimoMensaje.substring(0, 97) + "...";
                    }
                    
                    return new ConversacionDTO(
                            conv.getId(),
                            conv.getTitulo(),
                            ultimoMensaje,
                            conv.getFechaActualizacion()
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MensajeDTO> obtenerMensajes(UUID conversacionId, UUID usuarioId) {
        // Verificar que la conversación pertenece al usuario (validación de seguridad)
        conversacionRepository.findByUsuarioIdAndId(usuarioId, conversacionId)
                .orElseThrow(() -> new RuntimeException("Conversación no encontrada"));

        List<Mensaje> mensajes = mensajeRepository.findByConversacionIdOrderByFechaCreacionAsc(conversacionId);

        return mensajes.stream()
                .map(msg -> new MensajeDTO(
                        msg.getId(),
                        msg.getContenido(),
                        msg.getEsUsuario(),
                        msg.getFechaCreacion()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el prompt del sistema según el rol del usuario
     */
    private String obtenerPromptPorRol(String rol) {
        if ("profesor".equalsIgnoreCase(rol)) {
            return PROMPT_PROFESOR;
        }
        return PROMPT_ESTUDIANTE; // Por defecto, usar prompt de estudiante
    }

    /**
     * Genera un título para la conversación basado en el primer mensaje
     */
    private String generarTitulo(String primerMensaje) {
        // Tomar las primeras palabras del mensaje (máximo 50 caracteres)
        String titulo = primerMensaje.length() > 50 
                ? primerMensaje.substring(0, 47) + "..." 
                : primerMensaje;
        
        return titulo;
    }
}
