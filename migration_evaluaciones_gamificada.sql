-- Crear tabla de evaluaciones gamificada
CREATE TABLE IF NOT EXISTS grupo_03.evaluaciones_gamificada (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    mision_id UUID NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    tiempo_limite_minutos INTEGER,
    intentos_permitidos INTEGER NOT NULL DEFAULT 1,
    mostrar_resultados_inmediato BOOLEAN NOT NULL DEFAULT true,
    puntos_por_pregunta INTEGER NOT NULL DEFAULT 10,
    puntos_bonus_tiempo INTEGER DEFAULT 5,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP,
    
    CONSTRAINT fk_evaluacion_mision FOREIGN KEY (mision_id) 
        REFERENCES grupo_03.misiones(id) ON DELETE CASCADE
);

-- Crear índice único parcial para evitar múltiples evaluaciones activas por misión
CREATE UNIQUE INDEX IF NOT EXISTS idx_evaluacion_mision_activa 
    ON grupo_03.evaluaciones_gamificada(mision_id) 
    WHERE activo = true;

-- Crear tabla de preguntas
CREATE TABLE IF NOT EXISTS grupo_03.preguntas (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evaluacion_id UUID NOT NULL,
    enunciado TEXT NOT NULL,
    tipo_pregunta VARCHAR(30) NOT NULL,
    puntos INTEGER NOT NULL DEFAULT 10,
    orden INTEGER NOT NULL DEFAULT 0,
    imagen_url VARCHAR(500),
    explicacion TEXT,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_pregunta_evaluacion FOREIGN KEY (evaluacion_id) 
        REFERENCES grupo_03.evaluaciones_gamificada(id) ON DELETE CASCADE,
    CONSTRAINT chk_tipo_pregunta CHECK (tipo_pregunta IN (
        'OPCION_MULTIPLE', 'VERDADERO_FALSO', 'ARRASTRAR_SOLTAR', 
        'COMPLETAR_ESPACIOS', 'ORDENAR', 'EMPAREJAR', 'SELECCION_MULTIPLE'
    ))
);

-- Crear tabla de opciones de respuesta
CREATE TABLE IF NOT EXISTS grupo_03.opciones_respuesta (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pregunta_id UUID NOT NULL,
    texto TEXT NOT NULL,
    es_correcta BOOLEAN NOT NULL DEFAULT false,
    orden INTEGER NOT NULL DEFAULT 0,
    imagen_url VARCHAR(500),
    feedback TEXT,
    
    CONSTRAINT fk_opcion_pregunta FOREIGN KEY (pregunta_id) 
        REFERENCES grupo_03.preguntas(id) ON DELETE CASCADE
);

-- Crear tabla de respuestas de estudiantes
CREATE TABLE IF NOT EXISTS grupo_03.respuestas_estudiante (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evaluacion_id UUID NOT NULL,
    pregunta_id UUID NOT NULL,
    estudiante_id UUID NOT NULL,
    opcion_id UUID,
    respuesta_texto TEXT,
    es_correcta BOOLEAN NOT NULL DEFAULT false,
    puntos_obtenidos INTEGER NOT NULL DEFAULT 0,
    tiempo_respuesta_segundos INTEGER,
    intento_numero INTEGER NOT NULL DEFAULT 1,
    fecha_respuesta TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_respuesta_evaluacion FOREIGN KEY (evaluacion_id) 
        REFERENCES grupo_03.evaluaciones_gamificada(id) ON DELETE CASCADE,
    CONSTRAINT fk_respuesta_pregunta FOREIGN KEY (pregunta_id) 
        REFERENCES grupo_03.preguntas(id) ON DELETE CASCADE,
    CONSTRAINT fk_respuesta_estudiante FOREIGN KEY (estudiante_id) 
        REFERENCES grupo_03.usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_respuesta_opcion FOREIGN KEY (opcion_id) 
        REFERENCES grupo_03.opciones_respuesta(id) ON DELETE SET NULL
);

-- Crear tabla de resultados de evaluación
CREATE TABLE IF NOT EXISTS grupo_03.resultados_evaluacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evaluacion_id UUID NOT NULL,
    estudiante_id UUID NOT NULL,
    puntos_totales INTEGER NOT NULL DEFAULT 0,
    puntos_maximos INTEGER NOT NULL DEFAULT 0,
    puntos_bonus INTEGER NOT NULL DEFAULT 0,
    porcentaje DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    preguntas_correctas INTEGER NOT NULL DEFAULT 0,
    preguntas_totales INTEGER NOT NULL DEFAULT 0,
    tiempo_total_segundos INTEGER,
    intento_numero INTEGER NOT NULL DEFAULT 1,
    completada BOOLEAN NOT NULL DEFAULT false,
    fecha_completado TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT fk_resultado_evaluacion FOREIGN KEY (evaluacion_id) 
        REFERENCES grupo_03.evaluaciones_gamificada(id) ON DELETE CASCADE,
    CONSTRAINT fk_resultado_estudiante FOREIGN KEY (estudiante_id) 
        REFERENCES grupo_03.usuario(id) ON DELETE CASCADE
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_evaluaciones_mision ON grupo_03.evaluaciones_gamificada(mision_id);
CREATE INDEX IF NOT EXISTS idx_evaluaciones_activo ON grupo_03.evaluaciones_gamificada(activo);
CREATE INDEX IF NOT EXISTS idx_preguntas_evaluacion ON grupo_03.preguntas(evaluacion_id);
CREATE INDEX IF NOT EXISTS idx_preguntas_orden ON grupo_03.preguntas(evaluacion_id, orden);
CREATE INDEX IF NOT EXISTS idx_opciones_pregunta ON grupo_03.opciones_respuesta(pregunta_id);
CREATE INDEX IF NOT EXISTS idx_opciones_orden ON grupo_03.opciones_respuesta(pregunta_id, orden);
CREATE INDEX IF NOT EXISTS idx_respuestas_evaluacion_estudiante ON grupo_03.respuestas_estudiante(evaluacion_id, estudiante_id);
CREATE INDEX IF NOT EXISTS idx_respuestas_intento ON grupo_03.respuestas_estudiante(evaluacion_id, estudiante_id, intento_numero);
CREATE INDEX IF NOT EXISTS idx_resultados_evaluacion_estudiante ON grupo_03.resultados_evaluacion(evaluacion_id, estudiante_id);
CREATE INDEX IF NOT EXISTS idx_resultados_intento ON grupo_03.resultados_evaluacion(evaluacion_id, estudiante_id, intento_numero);

