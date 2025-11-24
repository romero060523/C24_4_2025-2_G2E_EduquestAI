package com.eduquestia.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SugerenciaRecompensa {
    private String nombre;
    private String descripcion;
    private String tipo; // "logro", "nivel", "mision"
    private String razon;
}

