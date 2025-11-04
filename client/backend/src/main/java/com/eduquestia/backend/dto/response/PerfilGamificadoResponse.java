package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerfilGamificadoResponse {
    private Integer puntosTotales;
    private Integer nivel;
    private String nombreNivel;
    private Integer puntosParaSiguienteNivel;
    private Integer misionesCompletadas;
    private Integer logrosObtenidos;
    private List<LogroResponse> logros;
    private Integer posicionRanking;
}

