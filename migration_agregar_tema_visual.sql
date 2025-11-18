-- Migración para agregar campo tema_visual a la tabla misiones
ALTER TABLE grupo_03.misiones
ADD COLUMN IF NOT EXISTS tema_visual VARCHAR(20) DEFAULT 'DEFAULT';

-- Actualizar misiones existentes para que tengan un tema por defecto
UPDATE grupo_03.misiones
SET tema_visual = 'DEFAULT'
WHERE tema_visual IS NULL;


