package com.eduquestia.backend.dto.response;

import com.eduquestia.backend.entity.enums.EstadoAlerta;
import com.eduquestia.backend.entity.enums.TipoAlerta;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AlertaResponse {
    private UUID id;
    private UUID estudianteId;
    private String estudianteNombre;
    private String estudianteEmail;
    private UUID cursoId;
    private String cursoNombre;
    private TipoAlerta tipo;
    private String descripcion;
    private String datosContexto;
    private EstadoAlerta estado;
    private LocalDateTime fechaCreacion;
}
