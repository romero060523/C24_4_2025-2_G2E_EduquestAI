package com.eduquestia.backend.service.impl;

import com.eduquestia.backend.dto.request.ActualizarAlertaRequest;
import com.eduquestia.backend.dto.request.CrearAlertaRequest;
import com.eduquestia.backend.dto.response.AlertaTempranaResponse;
import com.eduquestia.backend.entity.AlertaTemprana;
import com.eduquestia.backend.entity.Curso;
import com.eduquestia.backend.entity.Usuario;
import com.eduquestia.backend.entity.enums.EstadoAlerta;
import com.eduquestia.backend.entity.enums.RolUsuario;
import com.eduquestia.backend.exceptions.ResourceNotFoundException;
import com.eduquestia.backend.exceptions.UnauthorizedException;
import com.eduquestia.backend.repository.AlertaTempranaRepository;
import com.eduquestia.backend.repository.CursoRepository;
import com.eduquestia.backend.repository.UsuarioRepository;
import com.eduquestia.backend.service.AlertaTempranaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AlertaTempranaServiceImpl implements AlertaTempranaService {

    private final AlertaTempranaRepository alertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;

    @Override
    public AlertaTempranaResponse crearAlerta(CrearAlertaRequest request, UUID profesorId) {
        log.info("Creando alerta temprana para estudiante: {} por profesor: {}", request.getEstudianteId(), profesorId);

        // Validar profesor
        Usuario profesor = usuarioRepository.findById(profesorId)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor no encontrado"));
        
        if (!profesor.getRolEnum().equals(RolUsuario.PROFESOR)) {
            throw new UnauthorizedException("El usuario no es un profesor");
        }

        // Validar estudiante
        Usuario estudiante = usuarioRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        if (!estudiante.getRolEnum().equals(RolUsuario.ESTUDIANTE)) {
            throw new UnauthorizedException("El usuario no es un estudiante");
        }

        // Validar curso
        Curso curso = cursoRepository.findById(request.getCursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        // Crear alerta
        AlertaTemprana alerta = new AlertaTemprana();
        alerta.setEstudiante(estudiante);
        alerta.setProfesor(profesor);
        alerta.setCurso(curso);
        alerta.setTitulo(request.getTitulo());
        alerta.setMensaje(request.getMensaje());
        alerta.setEstado(EstadoAlerta.ACTIVA);

        AlertaTemprana alertaGuardada = alertaRepository.save(alerta);
        log.info("Alerta temprana creada exitosamente: {}", alertaGuardada.getId());

        return convertirAResponse(alertaGuardada);
    }

    @Override
    public AlertaTempranaResponse actualizarAlerta(UUID alertaId, ActualizarAlertaRequest request, UUID profesorId) {
        log.info("Actualizando alerta: {} por profesor: {}", alertaId, profesorId);

        AlertaTemprana alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));

        if (!alerta.getProfesor().getId().equals(profesorId)) {
            throw new UnauthorizedException("No tienes permiso para actualizar esta alerta");
        }

        if (request.getEstado() != null) {
            alerta.setEstado(request.getEstado());
            if (request.getEstado().equals(EstadoAlerta.RESUELTA)) {
                alerta.setFechaResuelta(LocalDateTime.now());
            }
        }

        if (request.getAccionTomada() != null) {
            alerta.setAccionTomada(request.getAccionTomada());
        }

        AlertaTemprana alertaActualizada = alertaRepository.save(alerta);
        return convertirAResponse(alertaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaTempranaResponse> obtenerAlertasPorEstudiante(UUID estudianteId) {
        log.info("Obteniendo alertas para estudiante: {}", estudianteId);
        List<AlertaTemprana> alertas = alertaRepository.findByEstudianteIdOrderByFechaCreacionDesc(estudianteId);
        return alertas.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaTempranaResponse> obtenerAlertasPorProfesor(UUID profesorId) {
        log.info("Obteniendo alertas creadas por profesor: {}", profesorId);
        List<AlertaTemprana> alertas = alertaRepository.findByProfesorIdOrderByFechaCreacionDesc(profesorId);
        return alertas.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaTempranaResponse> obtenerAlertasPorCurso(UUID cursoId, UUID profesorId) {
        log.info("Obteniendo alertas del curso: {} para profesor: {}", cursoId, profesorId);
        
        // Verificar que el profesor tiene acceso al curso
        Curso curso = cursoRepository.findById(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        List<AlertaTemprana> alertas = alertaRepository.findByCursoIdOrderByFechaCreacionDesc(cursoId);
        return alertas.stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AlertaTempranaResponse obtenerAlertaPorId(UUID alertaId, UUID usuarioId) {
        AlertaTemprana alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Verificar que el usuario tiene acceso (es el estudiante o el profesor)
        if (!alerta.getEstudiante().getId().equals(usuarioId) && 
            !alerta.getProfesor().getId().equals(usuarioId)) {
            throw new UnauthorizedException("No tienes permiso para ver esta alerta");
        }

        return convertirAResponse(alerta);
    }

    @Override
    public void eliminarAlerta(UUID alertaId, UUID profesorId) {
        log.info("Eliminando alerta: {} por profesor: {}", alertaId, profesorId);

        AlertaTemprana alerta = alertaRepository.findById(alertaId)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta no encontrada"));

        if (!alerta.getProfesor().getId().equals(profesorId)) {
            throw new UnauthorizedException("No tienes permiso para eliminar esta alerta");
        }

        alertaRepository.delete(alerta);
        log.info("Alerta eliminada exitosamente: {}", alertaId);
    }

    private AlertaTempranaResponse convertirAResponse(AlertaTemprana alerta) {
        return AlertaTempranaResponse.builder()
                .id(alerta.getId())
                .estudianteId(alerta.getEstudiante().getId())
                .estudianteNombre(alerta.getEstudiante().getNombreCompleto())
                .estudianteEmail(alerta.getEstudiante().getEmail())
                .profesorId(alerta.getProfesor().getId())
                .profesorNombre(alerta.getProfesor().getNombreCompleto())
                .cursoId(alerta.getCurso().getId())
                .cursoNombre(alerta.getCurso().getNombre())
                .titulo(alerta.getTitulo())
                .mensaje(alerta.getMensaje())
                .estado(alerta.getEstado())
                .fechaCreacion(alerta.getFechaCreacion())
                .fechaActualizacion(alerta.getFechaActualizacion())
                .fechaResuelta(alerta.getFechaResuelta())
                .accionTomada(alerta.getAccionTomada())
                .build();
    }
}

