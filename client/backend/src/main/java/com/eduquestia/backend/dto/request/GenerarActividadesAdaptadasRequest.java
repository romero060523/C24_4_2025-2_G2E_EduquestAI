package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerarActividadesAdaptadasRequest {
    @NotNull(message = "El ID del curso es requerido")
    private UUID cursoId;
    
    private String tema; // Tema o contenido específico (opcional)
    private Integer cantidadPreguntas; // Cantidad de preguntas a generar (opcional, default: 5)
    private String tipoActividad; // "evaluacion", "mision", "preguntas" (opcional)
    
    // Opcional: si se envía, las actividades se adaptan al nivel de un estudiante específico
    private UUID estudianteId;
}

