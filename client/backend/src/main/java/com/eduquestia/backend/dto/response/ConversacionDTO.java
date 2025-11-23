package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversacionDTO {

    private UUID id;
    private String titulo;
    private String ultimoMensaje;
    private OffsetDateTime fechaActualizacion;
}
