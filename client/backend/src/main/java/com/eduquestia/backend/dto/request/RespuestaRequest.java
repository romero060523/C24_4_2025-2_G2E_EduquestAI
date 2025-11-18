package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class RespuestaRequest {
    @NotNull(message = "El ID de la pregunta es obligatorio")
    private UUID preguntaId;

    private UUID opcionId; // Para opción múltiple
    private String respuestaTexto; // Para completar espacios, ordenar, etc.
    private Integer tiempoRespuestaSegundos;
}


