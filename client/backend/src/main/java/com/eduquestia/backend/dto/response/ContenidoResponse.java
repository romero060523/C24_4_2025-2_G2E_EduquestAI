package com.eduquestia.backend.dto.response;

import com.eduquestia.backend.entity.enums.TipoContenido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContenidoResponse {
    private UUID id;
    private TipoContenido tipoContenido;
    private String titulo;
    private String contenidoUrl;
    private String contenidoTexto;
    private Integer orden;
}
