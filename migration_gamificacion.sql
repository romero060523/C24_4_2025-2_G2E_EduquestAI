-- =====================================================
-- MIGRACIÓN: Sistema de Gamificación
-- Fecha: 2025-01-XX
-- Descripción: Agrega tablas de logros y columna profesor_id a cursos
-- =====================================================

-- Establecer el esquema
SET search_path TO grupo_03;

-- =====================================================
-- 1. Agregar columna profesor_id a la tabla cursos
-- =====================================================
DO $$
BEGIN
    -- Verificar si la columna ya existe antes de agregarla
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'grupo_03' 
        AND table_name = 'cursos' 
        AND column_name = 'profesor_id'
    ) THEN
        ALTER TABLE grupo_03.cursos 
        ADD COLUMN profesor_id UUID;
        
        -- Agregar constraint de foreign key
        ALTER TABLE grupo_03.cursos
        ADD CONSTRAINT fk_curso_profesor 
        FOREIGN KEY (profesor_id) 
        REFERENCES grupo_03.usuario(id);
        
        RAISE NOTICE 'Columna profesor_id agregada a la tabla cursos';
    ELSE
        RAISE NOTICE 'La columna profesor_id ya existe en la tabla cursos';
    END IF;
END $$;

-- =====================================================
-- 2. Crear tabla logros
-- =====================================================
CREATE TABLE IF NOT EXISTS grupo_03.logros (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    icono VARCHAR(50),
    puntos_requeridos INTEGER NOT NULL DEFAULT 0,
    nivel_requerido INTEGER,
    misiones_completadas_requeridas INTEGER,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Agregar comentarios descriptivos
COMMENT ON TABLE grupo_03.logros IS 'Tabla que almacena los logros disponibles en el sistema';
COMMENT ON COLUMN grupo_03.logros.nombre IS 'Nombre del logro';
COMMENT ON COLUMN grupo_03.logros.descripcion IS 'Descripción detallada del logro';
COMMENT ON COLUMN grupo_03.logros.icono IS 'Emoji o código de icono para visualización';
COMMENT ON COLUMN grupo_03.logros.puntos_requeridos IS 'Puntos mínimos necesarios para obtener el logro';
COMMENT ON COLUMN grupo_03.logros.nivel_requerido IS 'Nivel mínimo necesario para obtener el logro';
COMMENT ON COLUMN grupo_03.logros.misiones_completadas_requeridas IS 'Cantidad de misiones completadas necesarias';

-- =====================================================
-- 3. Crear tabla logros_estudiante
-- =====================================================
CREATE TABLE IF NOT EXISTS grupo_03.logros_estudiante (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    estudiante_id UUID NOT NULL,
    logro_id UUID NOT NULL,
    fecha_obtenido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_logro_estudiante_usuario 
        FOREIGN KEY (estudiante_id) 
        REFERENCES grupo_03.usuario(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_logro_estudiante_logro 
        FOREIGN KEY (logro_id) 
        REFERENCES grupo_03.logros(id) 
        ON DELETE CASCADE,
    CONSTRAINT uq_logro_estudiante 
        UNIQUE (estudiante_id, logro_id)
);

-- Agregar comentarios descriptivos
COMMENT ON TABLE grupo_03.logros_estudiante IS 'Tabla que relaciona estudiantes con los logros que han obtenido';
COMMENT ON COLUMN grupo_03.logros_estudiante.fecha_obtenido IS 'Fecha y hora en que el estudiante obtuvo el logro';

-- =====================================================
-- 4. Crear índices para optimizar consultas
-- =====================================================

-- Índice para búsquedas por puntos requeridos
CREATE INDEX IF NOT EXISTS idx_logros_puntos_requeridos 
    ON grupo_03.logros(puntos_requeridos) 
    WHERE activo = true;

-- Índice para búsquedas por estudiante
CREATE INDEX IF NOT EXISTS idx_logros_estudiante_estudiante_id 
    ON grupo_03.logros_estudiante(estudiante_id);

-- Índice para búsquedas por logro
CREATE INDEX IF NOT EXISTS idx_logros_estudiante_logro_id 
    ON grupo_03.logros_estudiante(logro_id);

-- =====================================================
-- 5. Insertar logros iniciales (opcional)
-- =====================================================

-- Logro: Primeros Pasos (100 puntos)
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'Primeros Pasos',
    'Has alcanzado tus primeros 100 puntos',
    '🌱',
    100,
    1,
    1,
    true
)
ON CONFLICT DO NOTHING;

-- Logro: En Camino (500 puntos)
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'En Camino',
    'Has alcanzado 500 puntos',
    '🚀',
    500,
    2,
    5,
    true
)
ON CONFLICT DO NOTHING;

-- Logro: Aprendiz Intermedio (1000 puntos)
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'Aprendiz Intermedio',
    'Has alcanzado 1000 puntos',
    '🎓',
    1000,
    3,
    10,
    true
)
ON CONFLICT DO NOTHING;

-- Logro: Estudiante Avanzado (2500 puntos)
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'Estudiante Avanzado',
    'Has alcanzado 2500 puntos',
    '⭐',
    2500,
    4,
    25,
    true
)
ON CONFLICT DO NOTHING;

-- Logro: Experto (5000 puntos)
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'Experto',
    'Has alcanzado 5000 puntos',
    '🏆',
    5000,
    5,
    50,
    true
)
ON CONFLICT DO NOTHING;

-- Logro: Maestro (Más de 5000 puntos)
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'Maestro',
    'Has alcanzado más de 5000 puntos',
    '👑',
    10000,
    6,
    100,
    true
)
ON CONFLICT DO NOTHING;

-- Logro: Primera Misión Completada
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'Primera Misión',
    'Has completado tu primera misión',
    '🎯',
    0,
    1,
    1,
    true
)
ON CONFLICT DO NOTHING;

-- Logro: Misiones Múltiples (10 misiones)
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'Persistente',
    'Has completado 10 misiones',
    '💪',
    0,
    1,
    10,
    true
)
ON CONFLICT DO NOTHING;

-- Logro: Especialista (25 misiones)
INSERT INTO grupo_03.logros (nombre, descripcion, icono, puntos_requeridos, nivel_requerido, misiones_completadas_requeridas, activo)
VALUES (
    'Especialista',
    'Has completado 25 misiones',
    '🎖️',
    0,
    2,
    25,
    true
)
ON CONFLICT DO NOTHING;

-- Verificación final
DO $$
BEGIN
    RAISE NOTICE '========================================';
    RAISE NOTICE 'MIGRACIÓN COMPLETADA EXITOSAMENTE';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Tablas creadas:';
    RAISE NOTICE '  - logros';
    RAISE NOTICE '  - logros_estudiante';
    RAISE NOTICE 'Columna agregada:';
    RAISE NOTICE '  - cursos.profesor_id';
    RAISE NOTICE 'Logros iniciales insertados: 9';
END $$;

