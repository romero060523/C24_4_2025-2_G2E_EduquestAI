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
public class ResultadoEvaluacionResponse {
    private UUID id;
    private UUID evaluacionId;
    private UUID estudianteId;
    private String estudianteNombre;
    private Integer puntosTotales;
    private Integer puntosMaximos;
    private Integer puntosBonus;
    private Double porcentaje;
    private Integer preguntasCorrectas;
    private Integer preguntasTotales;
    private Integer tiempoTotalSegundos;
    private Integer intentoNumero;
    private Boolean completada;
    private LocalDateTime fechaCompletado;
}


