package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.ChatRequestDTO;
import com.eduquestia.backend.dto.response.ChatResponseDTO;
import com.eduquestia.backend.dto.response.ConversacionDTO;
import com.eduquestia.backend.dto.response.MensajeDTO;
import com.eduquestia.backend.dto.response.SugerenciaIAResponse;
import com.eduquestia.backend.entity.Conversacion;
import com.eduquestia.backend.entity.Mensaje;
import com.eduquestia.backend.repository.ConversacionRepository;
import com.eduquestia.backend.repository.MensajeRepository;
import com.eduquestia.backend.service.ChatService;
import com.eduquestia.backend.service.GamificacionService;
import com.eduquestia.backend.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final GeminiService geminiService;
    private final GamificacionService gamificacionService;
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

        // Verificar si el mensaje pregunta sobre progreso o metas (solo para estudiantes)
        String mensajeLower = request.getMensaje().toLowerCase(Locale.ROOT);
        boolean preguntaProgreso = esPreguntaProgreso(mensajeLower);
        boolean preguntaMetas = esPreguntaMetas(mensajeLower);
        
        String respuestaIA;
        
        // Si es estudiante y pregunta sobre progreso o metas, usar el servicio de sugerencias
        if ("estudiante".equalsIgnoreCase(request.getRolUsuario()) && (preguntaProgreso || preguntaMetas)) {
            try {
                log.info("Detectada pregunta sobre progreso/metas. Generando sugerencias para estudiante: {}", request.getUsuarioId());
                SugerenciaIAResponse sugerencias = gamificacionService.generarSugerenciasIA(request.getUsuarioId());
                respuestaIA = formatearRespuestaConSugerencias(sugerencias, preguntaProgreso, preguntaMetas);
            } catch (Exception e) {
                log.error("Error al obtener sugerencias de IA: {}", e.getMessage(), e);
                // Respuesta por defecto sin depender de Gemini ni del servicio de gamificación
                respuestaIA = generarRespuestaPorDefecto(preguntaProgreso, preguntaMetas);
            }
        } else {
            // Generar respuesta de la IA según el rol
            try {
                String systemPrompt = obtenerPromptPorRol(request.getRolUsuario());
                respuestaIA = geminiService.generateResponse(systemPrompt, request.getMensaje());
            } catch (Exception e) {
                log.error("Error al generar respuesta de IA: {}", e.getMessage(), e);
                // Respuesta por defecto si falla Gemini
                respuestaIA = "Lo siento, estoy teniendo problemas técnicos en este momento. Por favor, intenta más tarde o contacta con soporte si el problema persiste.";
            }
        }

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

    /**
     * Detecta si el mensaje pregunta sobre el progreso del estudiante
     */
    private boolean esPreguntaProgreso(String mensaje) {
        String[] palabrasClave = {
            "progreso", "cómo voy", "como voy", "cómo estoy", "como estoy",
            "avance", "avances", "rendimiento", "desempeño", "estadísticas",
            "estadisticas", "puntos", "nivel", "logros", "misiones completadas"
        };
        
        for (String palabra : palabrasClave) {
            if (mensaje.contains(palabra)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detecta si el mensaje pregunta sobre metas o motivación
     */
    private boolean esPreguntaMetas(String mensaje) {
        String[] palabrasClave = {
            "meta", "metas", "objetivo", "objetivos", "motiv", "motivación",
            "motivacion", "sugerencia", "sugerencias", "recomendación",
            "recomendacion", "qué puedo hacer", "que puedo hacer",
            "qué debería hacer", "que deberia hacer", "cómo mejorar",
            "como mejorar", "recompensa", "recompensas"
        };
        
        for (String palabra : palabrasClave) {
            if (mensaje.contains(palabra)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Genera una respuesta por defecto cuando falla el servicio de IA
     */
    private String generarRespuestaPorDefecto(boolean preguntaProgreso, boolean preguntaMetas) {
        StringBuilder respuesta = new StringBuilder();
        
        if (preguntaProgreso) {
            respuesta.append("📊 **Análisis de tu Progreso**\n\n");
            respuesta.append("Puedo ayudarte a revisar tu progreso. Te recomiendo que:\n");
            respuesta.append("- Revises tu dashboard para ver tus puntos y nivel actual\n");
            respuesta.append("- Consultes las misiones completadas y pendientes\n");
            respuesta.append("- Veas tus logros obtenidos\n\n");
        }
        
        if (preguntaMetas) {
            respuesta.append("🎯 **Sugerencias de Metas**\n\n");
            respuesta.append("Para mantenerte motivado, te sugiero:\n");
            respuesta.append("1. Completar al menos una misión por día\n");
            respuesta.append("2. Alcanzar el siguiente nivel de puntos\n");
            respuesta.append("3. Participar activamente en tus cursos\n\n");
            respuesta.append("🏆 **Recompensas Disponibles**\n\n");
            respuesta.append("Puedes obtener recompensas completando misiones y alcanzando objetivos. ");
            respuesta.append("¡Mantén el esfuerzo y verás los resultados!\n\n");
        }
        
        respuesta.append("¿Te gustaría que profundice en algún aspecto específico?");
        
        return respuesta.toString();
    }

    /**
     * Formatea la respuesta del chat incluyendo las sugerencias de IA
     */
    private String formatearRespuestaConSugerencias(SugerenciaIAResponse sugerencias, 
                                                     boolean preguntaProgreso, 
                                                     boolean preguntaMetas) {
        StringBuilder respuesta = new StringBuilder();
        
        // Mensaje motivacional
        if (sugerencias.getMensajeMotivacional() != null && !sugerencias.getMensajeMotivacional().isEmpty()) {
            respuesta.append("💪 ").append(sugerencias.getMensajeMotivacional()).append("\n\n");
        }
        
        // Análisis de progreso
        if (preguntaProgreso && sugerencias.getAnalisisProgreso() != null && !sugerencias.getAnalisisProgreso().isEmpty()) {
            respuesta.append("## 📊 Análisis de tu Progreso\n\n");
            respuesta.append(sugerencias.getAnalisisProgreso()).append("\n\n");
        }
        
        // Metas sugeridas
        if (preguntaMetas && sugerencias.getMetasSugeridas() != null && !sugerencias.getMetasSugeridas().isEmpty()) {
            respuesta.append("## 🎯 Metas Sugeridas para Ti\n\n");
            for (int i = 0; i < sugerencias.getMetasSugeridas().size(); i++) {
                var meta = sugerencias.getMetasSugeridas().get(i);
                respuesta.append("**").append(i + 1).append(". ").append(meta.getTitulo()).append("**\n");
                respuesta.append(meta.getDescripcion()).append("\n");
                if (meta.getObjetivo() != null) {
                    respuesta.append("- Objetivo: ").append(meta.getObjetivo());
                    if (meta.getTipo() != null) {
                        respuesta.append(" ").append(meta.getTipo().equals("puntos") ? "puntos" : "misiones");
                    }
                    respuesta.append("\n");
                }
                if (meta.getRazon() != null && !meta.getRazon().isEmpty()) {
                    respuesta.append("- 💡 ").append(meta.getRazon()).append("\n");
                }
                respuesta.append("\n");
            }
        }
        
        // Recompensas sugeridas
        if (preguntaMetas && sugerencias.getRecompensasSugeridas() != null && !sugerencias.getRecompensasSugeridas().isEmpty()) {
            respuesta.append("## 🏆 Recompensas que Puedes Obtener\n\n");
            for (int i = 0; i < sugerencias.getRecompensasSugeridas().size(); i++) {
                var recompensa = sugerencias.getRecompensasSugeridas().get(i);
                respuesta.append("**").append(i + 1).append(". ").append(recompensa.getNombre()).append("**\n");
                respuesta.append(recompensa.getDescripcion()).append("\n");
                if (recompensa.getRazon() != null && !recompensa.getRazon().isEmpty()) {
                    respuesta.append("- ✨ ").append(recompensa.getRazon()).append("\n");
                }
                respuesta.append("\n");
            }
        }
        
        // Mensaje final
        respuesta.append("¿Te gustaría que profundice en alguna de estas sugerencias o tienes alguna otra pregunta?");
        
        return respuesta.toString();
    }
}
