package com.eduquestia.backend.dto.response;

import com.eduquestia.backend.entity.enums.CategoriaMision;
import com.eduquestia.backend.entity.enums.DificultadMision;
import com.eduquestia.backend.entity.enums.EstadoEntrega;
import com.eduquestia.backend.entity.enums.TemaVisual;
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
public class MisionEstudianteResponse {
    private UUID id;
    private String titulo;
    private String descripcion;
    private CategoriaMision categoria;
    private DificultadMision dificultad;
    private Integer puntosRecompensa;
    private Integer experienciaRecompensa;
    private Integer monedasRecompensa;
    private Integer semanaClase;
    private TemaVisual temaVisual;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaLimite;
    private Boolean activo;
    private String cursoNombre;
    
    // Información del progreso del estudiante
    private Integer porcentajeCompletado;
    private Boolean completada;
    private LocalDateTime fechaCompletado;
    private EstadoEntrega estadoEntrega;
    private Integer puntosObtenidos;
    private LocalDateTime ultimaActividad;
}

