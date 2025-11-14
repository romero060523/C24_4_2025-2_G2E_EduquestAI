package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletarMisionRequest {
    
    @NotBlank(message = "El contenido de la entrega es obligatorio")
    private String contenidoEntrega;
    
    private String archivoUrl;
    
    private String comentariosEstudiante;
}

