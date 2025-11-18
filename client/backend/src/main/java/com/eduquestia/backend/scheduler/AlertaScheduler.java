package com.eduquestia.backend.scheduler;

import com.eduquestia.backend.entity.ConfiguracionAlerta;
import com.eduquestia.backend.repository.ConfiguracionAlertaRepository;
import com.eduquestia.backend.service.AlertaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertaScheduler {

    private final ConfiguracionAlertaRepository configuracionRepo;
    private final AlertaService alertaService;

    /**
     * Ejecutar evaluación cada día a las 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void evaluarAlertasDiariamente() {
        log.info("Iniciando evaluación diaria de alertas...");

        try {
            List<ConfiguracionAlerta> configuraciones = configuracionRepo
                    .findByActivoTrue();

            log.info("Se encontraron {} configuraciones activas", configuraciones.size());

            for (ConfiguracionAlerta config : configuraciones) {
                try {
                    alertaService.evaluarEstudiantesCurso(config.getId());
                    log.info("Curso {} evaluado exitosamente", config.getCurso().getNombre());
                } catch (Exception e) {
                    log.error("Error evaluando curso {}", config.getCurso().getNombre(), e);
                }
            }

            log.info("Evaluación diaria de alertas completada");
        } catch (Exception e) {
            log.error("Error en evaluación diaria de alertas", e);
        }
    }

    /**
     * Ejecutar evaluación cada 6 horas (opcional, para mayor frecuencia)
     */
    @Scheduled(fixedRate = 21600000) // 6 horas en milisegundos
    public void evaluarAlertasPeriodicamente() {
        log.info("Iniciando evaluación periódica de alertas...");

        // Mismo código que arriba, o puedes hacer evaluaciones más ligeras
        evaluarAlertasDiariamente();
    }
}
