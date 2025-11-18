package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CrearEvaluacionRequest {
    // Ahora la misión es OPCIONAL (solo si quieres asociarla a una misión específica)
    private UUID misionId;

    // El curso es OBLIGATORIO (toda evaluación pertenece a un curso)
    @NotNull(message = "El ID del curso es obligatorio")
    private UUID cursoId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 200, message = "El título no puede exceder 200 caracteres")
    private String titulo;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;

    private Integer tiempoLimiteMinutos;
    private Integer intentosPermitidos = 1;
    private Boolean mostrarResultadosInmediato = true;
    private Integer puntosPorPregunta = 10;
    private Integer puntosBonusTiempo = 5;

    @NotNull(message = "Debe incluir al menos una pregunta")
    @Size(min = 1, message = "Debe incluir al menos una pregunta")
    private List<CrearPreguntaRequest> preguntas;
}


