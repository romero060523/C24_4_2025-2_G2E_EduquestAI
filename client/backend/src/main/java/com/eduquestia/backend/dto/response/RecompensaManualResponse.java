package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para recompensas manuales
 * Historia de Usuario #12: Recompensas manuales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecompensaManualResponse {

    private UUID id;
    private UUID profesorId;
    private String profesorNombre;
    private UUID estudianteId;
    private String estudianteNombre;
    private UUID cursoId;
    private String cursoNombre;
    private Integer puntos;
    private String motivo;
    private String observaciones;
    private LocalDateTime fechaOtorgamiento;
}

