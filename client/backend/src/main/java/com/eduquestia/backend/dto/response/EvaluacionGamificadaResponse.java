package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionGamificadaResponse {
    private UUID id;
    private UUID misionId;
    private String misionTitulo;
    private UUID cursoId;
    private String cursoNombre;
    private String titulo;
    private String descripcion;
    private Integer tiempoLimiteMinutos;
    private Integer intentosPermitidos;
    private Boolean mostrarResultadosInmediato;
    private Integer puntosPorPregunta;
    private Integer puntosBonusTiempo;
    private Boolean activo;
    private List<PreguntaResponse> preguntas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Campos específicos del estudiante (solo se llenan cuando se consulta para un estudiante)
    private Boolean completada; // Si el estudiante ya completó la evaluación
    private Integer intentosUsados; // Cuántos intentos ha usado el estudiante
    private Integer mejorPuntuacion; // Mejor puntuación obtenida
    private Double mejorPorcentaje; // Mejor porcentaje obtenido
    private LocalDateTime fechaCompletado; // Fecha en que completó (si ya la completó)
}


