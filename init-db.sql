-- Script de inicialización de PostgreSQL
-- Crear el esquema grupo_03

CREATE SCHEMA IF NOT EXISTS grupo_03;

-- Dar permisos al usuario postgres
GRANT ALL PRIVILEGES ON SCHEMA grupo_03 TO postgres;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA grupo_03 TO postgres;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA grupo_03 TO postgres;

-- Configurar search_path por defecto (opcional)
ALTER DATABASE eduquest_db SET search_path TO grupo_03, public;

-- ===================================================================
-- NOTA IMPORTANTE: 
-- La tabla 'usuario' será creada por Django (admin-backend) 
-- mediante migraciones cuando el servicio se inicie.
-- Las otras tablas serán creadas manualmente o por Spring Boot.
-- ===================================================================
