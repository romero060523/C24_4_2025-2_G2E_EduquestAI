package com.eduquestia.backend.dto.request;

import com.eduquestia.backend.entity.enums.TipoContenido;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoRequest {

    @NotNull(message = "El tipo de contenido es obligatorio")
    private TipoContenido tipoContenido;

    @Size(max = 100, message = "El título no puede exceder 100 caracteres")
    private String titulo;

    @Size(max = 500, message = "La URL no puede exceder 500 caracteres")
    private String contenidoUrl;

    private String contenidoTexto;

    @NotNull(message = "El orden es obligatorio")
    private Integer orden;
}

