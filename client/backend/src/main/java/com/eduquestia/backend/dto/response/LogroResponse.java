package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogroResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Integer puntosRequeridos;
    private LocalDateTime fechaObtenido;
    private Boolean obtenido;
}

