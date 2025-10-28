package com.eduquestia.backend.dto.response;

import com.eduquestia.backend.entity.enums.CategoriaMision;
import com.eduquestia.backend.entity.enums.DificultadMision;
import com.eduquestia.backend.entity.enums.TipoMision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MisionResponse {
    private UUID id;
    private UUID cursoId;
    private String cursoNombre;
    private UUID profesorId;
    private String profesorNombre;
    private String titulo;
    private String descripcion;
    private TipoMision tipoMision;
    private CategoriaMision categoria;
    private DificultadMision dificultad;
    private Integer puntosRecompensa;
    private Integer experienciaRecompensa;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaLimite;
    private Boolean activo;
    private String requisitosPrevios;
    private List<ContenidoResponse> contenidos;
    private List<CriterioResponse> criterios;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
