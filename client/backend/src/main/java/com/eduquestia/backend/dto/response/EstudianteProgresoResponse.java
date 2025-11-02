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
public class EstudianteProgresoResponse {
    private UUID estudianteId;
    private String nombreCompleto;
    private String avatarUrl;
    private Integer porcentajeCompletado;
    private String estado;
    private LocalDateTime ultimaActividad;
}

