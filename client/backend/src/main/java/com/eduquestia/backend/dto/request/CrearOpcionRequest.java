package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearOpcionRequest {
    @NotBlank(message = "El texto de la opción es obligatorio")
    private String texto;
    
    private Boolean esCorrecta = false;
    private Integer orden = 0;
    private String imagenUrl;
    private String feedback;
}


