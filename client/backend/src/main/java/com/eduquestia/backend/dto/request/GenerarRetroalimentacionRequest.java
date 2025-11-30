package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerarRetroalimentacionRequest {
    @NotNull(message = "El ID del estudiante es requerido")
    private UUID estudianteId;
    
    @NotNull(message = "El ID de la evaluación es requerido")
    private UUID evaluacionId;
}

