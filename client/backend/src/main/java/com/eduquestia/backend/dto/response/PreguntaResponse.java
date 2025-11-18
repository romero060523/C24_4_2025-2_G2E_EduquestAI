package com.eduquestia.backend.dto.response;

import com.eduquestia.backend.entity.enums.TipoPregunta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaResponse {
    private UUID id;
    private String enunciado;
    private TipoPregunta tipoPregunta;
    private Integer puntos;
    private Integer orden;
    private String imagenUrl;
    private String explicacion;
    private List<OpcionRespuestaResponse> opciones;
}


