package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpcionRespuestaResponse {
    private UUID id;
    private String texto;
    private Boolean esCorrecta;
    private Integer orden;
    private String imagenUrl;
    private String feedback;
}


