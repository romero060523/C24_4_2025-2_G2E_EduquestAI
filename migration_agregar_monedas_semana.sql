-- Migración: Agregar campos de monedas y semana de clase a la tabla misiones
-- Fecha: 2025-11-17
-- Descripción: Agrega campos para gamificación: monedas_recompensa y semana_clase

-- Agregar columna monedas_recompensa
ALTER TABLE grupo_03.misiones 
ADD COLUMN IF NOT EXISTS monedas_recompensa INTEGER DEFAULT 0;

-- Agregar columna semana_clase
ALTER TABLE grupo_03.misiones 
ADD COLUMN IF NOT EXISTS semana_clase INTEGER;

-- Comentarios para documentación
COMMENT ON COLUMN grupo_03.misiones.monedas_recompensa IS 'Cantidad de monedas que otorga la misión al completarla';
COMMENT ON COLUMN grupo_03.misiones.semana_clase IS 'Semana del curso a la que pertenece la misión (1-20)';


