package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CriterioRequest {

    @NotBlank(message = "El criterio es obligatorio")
    @Size(max = 200, message = "El criterio no puede exceder 200 caracteres")
    private String criterio;

    @NotNull(message = "Los puntos máximos son obligatorios")
    @Min(value = 0, message = "Los puntos máximos deben ser positivos")
    private Integer puntosMaximos;

    private String descripcion;

    @NotNull(message = "El orden es obligatorio")
    private Integer orden;
}
