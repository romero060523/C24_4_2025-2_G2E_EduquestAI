package com.eduquestia.backend.dto.response;

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
public class ActividadesAdaptadasResponse {
    private UUID cursoId;
    private String cursoNombre;
    private String nivelPromedioEstudiantes;
    private List<ActividadPropuesta> actividades;
    private LocalDateTime fechaGeneracion;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActividadPropuesta {
        private String titulo;
        private String descripcion;
        private String tipo; // "evaluacion", "mision", "pregunta"
        private String dificultad; // "facil", "medio", "dificil"
        private List<PreguntaPropuesta> preguntas;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreguntaPropuesta {
        private String enunciado;
        private String tipoPregunta; // "OPCION_MULTIPLE", "VERDADERO_FALSO", etc.
        private List<String> opciones;
        private Integer indiceCorrecta; // Índice de la opción correcta
        private String explicacion;
    }
}

