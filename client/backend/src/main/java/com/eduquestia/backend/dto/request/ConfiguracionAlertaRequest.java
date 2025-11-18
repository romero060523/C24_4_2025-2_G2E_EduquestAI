package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class ConfiguracionAlertaRequest {

    @NotNull(message = "El ID del curso es obligatorio")
    private UUID cursoId;

    @Min(value = 1, message = "Los días de inactividad deben ser al menos 1")
    private Integer diasInactividad;

    @Min(value = 0, message = "El porcentaje debe ser entre 0 y 100")
    private Double porcentajeCompletitudMinimo;

    private Boolean puntosDebajoPromedio;

    @Min(value = 1, message = "Las misiones pendientes deben ser al menos 1")
    private Integer misionesPendientesMinimo;
}

