package com.eduquestia.backend.dto.response;

import com.eduquestia.backend.entity.enums.EstadoAlerta;
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
public class AlertaTempranaResponse {
    private UUID id;
    private UUID estudianteId;
    private String estudianteNombre;
    private String estudianteEmail;
    private UUID profesorId;
    private String profesorNombre;
    private UUID cursoId;
    private String cursoNombre;
    private String titulo;
    private String mensaje;
    private EstadoAlerta estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaResuelta;
    private String accionTomada;
}


