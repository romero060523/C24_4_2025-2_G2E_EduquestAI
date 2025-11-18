package com.eduquestia.backend.dto.request;

import com.eduquestia.backend.entity.enums.TipoPregunta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CrearPreguntaRequest {
    @NotBlank(message = "El enunciado es obligatorio")
    private String enunciado;

    @NotNull(message = "El tipo de pregunta es obligatorio")
    private TipoPregunta tipoPregunta;

    private Integer puntos = 10;
    private Integer orden = 0;
    private String imagenUrl;
    private String explicacion;

    @NotNull(message = "Debe incluir opciones de respuesta")
    @Size(min = 2, message = "Debe incluir al menos 2 opciones")
    private List<CrearOpcionRequest> opciones;
}


