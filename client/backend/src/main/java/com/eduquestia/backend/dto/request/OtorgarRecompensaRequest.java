package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para otorgar una recompensa manual a un estudiante
 * Historia de Usuario #12: Recompensas manuales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtorgarRecompensaRequest {

    @NotNull(message = "El ID del estudiante es obligatorio")
    private UUID estudianteId;

    @NotNull(message = "El ID del curso es obligatorio")
    private UUID cursoId;

    @NotNull(message = "Los puntos son obligatorios")
    @Min(value = 1, message = "Los puntos deben ser al menos 1")
    @Max(value = 1000, message = "Los puntos no pueden exceder 1000")
    private Integer puntos;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 500, message = "El motivo no puede exceder 500 caracteres")
    private String motivo;

    @Size(max = 255, message = "Las observaciones no pueden exceder 255 caracteres")
    private String observaciones;
}

