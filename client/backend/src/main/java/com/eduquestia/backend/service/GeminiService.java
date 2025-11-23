package com.eduquestia.backend.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    /**
     * Genera una respuesta de la IA usando Gemini
     * @param systemPrompt El prompt del sistema que define el comportamiento de la IA
     * @param userMessage El mensaje del usuario
     * @return La respuesta generada por la IA
     */
    public String generateResponse(String systemPrompt, String userMessage) {
        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userMessage)
                    .call()
                    .content();
        } catch (Exception e) {
            throw new RuntimeException("Error al comunicarse con Gemini AI: " + e.getMessage(), e);
        }
    }
}
