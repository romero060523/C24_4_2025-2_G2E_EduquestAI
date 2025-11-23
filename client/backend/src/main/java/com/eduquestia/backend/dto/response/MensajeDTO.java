package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDTO {

    private UUID id;
    private String contenido;
    private Boolean esUsuario;
    private OffsetDateTime fechaCreacion;
}
