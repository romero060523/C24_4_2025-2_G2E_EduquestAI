package com.eduquestia.backend.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class CursoDTO {
    private UUID id;
    private String codigoCurso;
    private String nombre;
    private String descripcion;
    private String imagenPortada;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime fechaActualizacion;
}
