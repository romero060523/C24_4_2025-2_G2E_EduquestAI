package com.eduquestia.backend.dto.response;

import com.eduquestia.backend.entity.enums.CategoriaMision;
import com.eduquestia.backend.entity.enums.DificultadMision;
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
public class MisionListResponse {
    private UUID id;
    private String titulo;
    private String descripcionResumida;
    private CategoriaMision categoria;
    private DificultadMision dificultad;
    private Integer puntosRecompensa;
    private LocalDateTime fechaLimite;
    private Boolean activo;
    private String cursoNombre;
    private Integer estudiantesCompletados;
    private Integer totalEstudiantes;
}
