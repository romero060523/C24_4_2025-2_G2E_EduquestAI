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
public class CriterioResponse {
    private UUID id;
    private String criterio;
    private Integer puntosMaximos;
    private String descripcion;
    private Integer orden;
}