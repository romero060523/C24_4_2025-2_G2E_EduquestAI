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
}


