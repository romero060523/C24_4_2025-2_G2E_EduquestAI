package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseDTO {

    private String respuesta;
    private UUID conversacionId;
    private UUID mensajeId;
}
