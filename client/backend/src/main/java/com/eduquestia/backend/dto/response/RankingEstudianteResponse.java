package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingEstudianteResponse {
    private UUID estudianteId;
    private String nombreEstudiante;
    private Integer puntosTotales;
    private Integer nivel;
    private String nombreNivel;
    private Integer misionesCompletadas;
    private Integer posicion;
}

