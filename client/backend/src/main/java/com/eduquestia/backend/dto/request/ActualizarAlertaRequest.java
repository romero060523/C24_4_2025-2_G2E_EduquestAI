package com.eduquestia.backend.dto.request;

import com.eduquestia.backend.entity.enums.EstadoAlerta;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarAlertaRequest {
    private EstadoAlerta estado;
    
    @Size(max = 2000, message = "La acción tomada no puede exceder 2000 caracteres")
    private String accionTomada;
}


