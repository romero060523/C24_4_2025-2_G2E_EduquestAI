package com.eduquestia.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequestDTO {

    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;

    private UUID conversacionId; // null para nueva conversación

    @NotNull(message = "El ID de usuario es requerido")
    private UUID usuarioId;

    @NotBlank(message = "El rol de usuario es requerido")
    private String rolUsuario; // "estudiante" o "profesor"
}
