package com.eduquestia.backend.service;

import com.eduquestia.backend.entity.Mision;
import com.eduquestia.backend.entity.Notificacion;
import com.eduquestia.backend.entity.Usuario;
import com.eduquestia.backend.entity.enums.TipoNotificacion;
import com.eduquestia.backend.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public void crearNotificacionNuevaMision(Usuario estudiante, Mision mision) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            Notificacion notificacion = new Notificacion();
            notificacion.setUsuario(estudiante);
            notificacion.setTipo(TipoNotificacion.MISION_NUEVA);
            notificacion.setTitulo("Nueva misión disponible");
            notificacion.setMensaje(String.format(
                    "Se ha asignado la misión '%s' en el curso %s. Fecha límite: %s",
                    mision.getTitulo(),
                    mision.getCurso().getNombre(),
                    mision.getFechaLimite().format(formatter)
            ));
            notificacion.setReferenciaId(mision.getId());
            notificacion.setLeida(false);

            notificacionRepository.save(notificacion);

            log.info("Notificación creada para estudiante: {} sobre misión: {}",
                    estudiante.getId(), mision.getId());
        } catch (Exception e) {
            log.error("Error al crear notificación para estudiante: {}", estudiante.getId(), e);
            // No lanzamos excepción para no afectar el flujo principal
        }
    }
}
