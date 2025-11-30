package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetroalimentacionResponse {
    private UUID estudianteId;
    private String estudianteNombre;
    private UUID evaluacionId;
    private String evaluacionTitulo;
    private String retroalimentacion;
    private LocalDateTime fechaGeneracion;
}

