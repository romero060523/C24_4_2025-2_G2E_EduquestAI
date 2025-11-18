-- Crear tabla de alertas temprana
CREATE TABLE IF NOT EXISTS grupo_03.alertas_temprana (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    estudiante_id UUID NOT NULL,
    profesor_id UUID NOT NULL,
    curso_id UUID NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'ACTIVA',
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP,
    fecha_resuelta TIMESTAMP,
    accion_tomada TEXT,
    
    CONSTRAINT fk_alerta_estudiante FOREIGN KEY (estudiante_id) 
        REFERENCES grupo_03.usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerta_profesor FOREIGN KEY (profesor_id) 
        REFERENCES grupo_03.usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_alerta_curso FOREIGN KEY (curso_id) 
        REFERENCES grupo_03.cursos(id) ON DELETE CASCADE,
    CONSTRAINT chk_estado_alerta CHECK (estado IN ('ACTIVA', 'EN_SEGUIMIENTO', 'RESUELTA', 'ARCHIVADA'))
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_alertas_estudiante ON grupo_03.alertas_temprana(estudiante_id);
CREATE INDEX IF NOT EXISTS idx_alertas_profesor ON grupo_03.alertas_temprana(profesor_id);
CREATE INDEX IF NOT EXISTS idx_alertas_curso ON grupo_03.alertas_temprana(curso_id);
CREATE INDEX IF NOT EXISTS idx_alertas_estado ON grupo_03.alertas_temprana(estado);
CREATE INDEX IF NOT EXISTS idx_alertas_fecha_creacion ON grupo_03.alertas_temprana(fecha_creacion DESC);


