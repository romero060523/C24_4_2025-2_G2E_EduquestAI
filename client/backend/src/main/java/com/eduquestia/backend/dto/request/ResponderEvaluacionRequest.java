package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ResponderEvaluacionRequest {
    @NotNull(message = "El ID de la evaluación es obligatorio")
    private UUID evaluacionId;

    @NotNull(message = "Debe incluir respuestas")
    private List<RespuestaRequest> respuestas;

    private Integer tiempoTotalSegundos;
}


