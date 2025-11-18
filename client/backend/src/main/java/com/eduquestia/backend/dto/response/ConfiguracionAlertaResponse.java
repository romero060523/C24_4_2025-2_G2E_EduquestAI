package com.eduquestia.backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConfiguracionAlertaResponse {
    private UUID id;
    private UUID cursoId;
    private String cursoNombre;
    private Integer diasInactividad;
    private Double porcentajeCompletitudMinimo;
    private Boolean puntosDebajoPromedio;
    private Integer misionesPendientesMinimo;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}
