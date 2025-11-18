-- Agregar columna curso_id a evaluaciones_gamificada
ALTER TABLE grupo_03.evaluaciones_gamificada 
ADD COLUMN IF NOT EXISTS curso_id UUID;

-- Hacer que mision_id sea nullable
ALTER TABLE grupo_03.evaluaciones_gamificada 
ALTER COLUMN mision_id DROP NOT NULL;

-- Poblar curso_id con el curso de la misión existente (migración de datos)
UPDATE grupo_03.evaluaciones_gamificada eg
SET curso_id = m.curso_id
FROM grupo_03.misiones m
WHERE eg.mision_id = m.id AND eg.curso_id IS NULL;

-- Ahora hacer curso_id obligatorio
ALTER TABLE grupo_03.evaluaciones_gamificada 
ALTER COLUMN curso_id SET NOT NULL;

-- Agregar foreign key
ALTER TABLE grupo_03.evaluaciones_gamificada
ADD CONSTRAINT fk_evaluacion_curso 
FOREIGN KEY (curso_id) REFERENCES grupo_03.cursos(id) ON DELETE CASCADE;

