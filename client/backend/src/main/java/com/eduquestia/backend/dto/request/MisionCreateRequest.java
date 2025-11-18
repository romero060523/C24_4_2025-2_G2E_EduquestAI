package com.eduquestia.backend.dto.request;

import com.eduquestia.backend.entity.enums.CategoriaMision;
import com.eduquestia.backend.entity.enums.DificultadMision;
import com.eduquestia.backend.entity.enums.TipoMision;
import com.eduquestia.backend.entity.enums.TemaVisual;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MisionCreateRequest {

    @NotNull(message = "El ID del curso es obligatorio")
    private UUID cursoId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede exceder 150 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El tipo de misión es obligatorio")
    private TipoMision tipoMision;

    @NotNull(message = "La categoría es obligatoria")
    private CategoriaMision categoria;

    @NotNull(message = "La dificultad es obligatoria")
    private DificultadMision dificultad;

    @NotNull(message = "Los puntos de recompensa son obligatorios")
    @Min(value = 0, message = "Los puntos deben ser positivos")
    private Integer puntosRecompensa;

    @NotNull(message = "La experiencia de recompensa es obligatoria")
    @Min(value = 0, message = "La experiencia debe ser positiva")
    private Integer experienciaRecompensa;

    @NotNull(message = "Las monedas de recompensa son obligatorias")
    @Min(value = 0, message = "Las monedas deben ser positivas")
    private Integer monedasRecompensa;

    @Min(value = 1, message = "La semana de clase debe ser mayor a 0")
    @Max(value = 20, message = "La semana de clase no puede ser mayor a 20")
    private Integer semanaClase;

    private TemaVisual temaVisual = TemaVisual.DEFAULT;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDateTime fechaInicio;

    @NotNull(message = "La fecha límite es obligatoria")
    private LocalDateTime fechaLimite;

    private String requisitosPrevios;

    @Valid
    private List<ContenidoRequest> contenidos = new ArrayList<>();

    @Valid
    private List<CriterioRequest> criterios = new ArrayList<>();

    @Size(min = 0, message = "La lista de estudiantes no puede ser negativa")
    private List<UUID> estudiantesIds = new ArrayList<>();
}
