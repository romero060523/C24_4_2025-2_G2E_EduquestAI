package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SugerenciaMeta {
    private String titulo;
    private String descripcion;
    private String tipo; // "mision", "evaluacion", "puntos", "nivel"
    private Integer objetivo;
    private String razon;
}

