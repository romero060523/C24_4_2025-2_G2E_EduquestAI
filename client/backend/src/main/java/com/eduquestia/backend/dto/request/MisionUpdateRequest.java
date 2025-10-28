package com.eduquestia.backend.dto.request;

import com.eduquestia.backend.entity.enums.CategoriaMision;
import com.eduquestia.backend.entity.enums.DificultadMision;
import com.eduquestia.backend.entity.enums.TipoMision;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MisionUpdateRequest {

    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    private String titulo;

    private String descripcion;

    private TipoMision tipoMision;

    private CategoriaMision categoria;

    private DificultadMision dificultad;

    @Min(value = 0, message = "Los puntos deben ser positivos")
    private Integer puntosRecompensa;

    @Min(value = 0, message = "La experiencia debe ser positiva")
    private Integer experienciaRecompensa;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaLimite;

    private Boolean activo;

    private String requisitosPrevios;
}
