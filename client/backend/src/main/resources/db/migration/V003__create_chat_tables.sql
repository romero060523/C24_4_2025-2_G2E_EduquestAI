-- Crear tablas para el sistema de chat con IA
-- Schema: grupo_03

-- Tabla de conversaciones
CREATE TABLE IF NOT EXISTS grupo_03.conversacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conversacion_usuario FOREIGN KEY (usuario_id) 
        REFERENCES grupo_03.usuario(id) ON DELETE CASCADE
);

-- Tabla de mensajes
CREATE TABLE IF NOT EXISTS grupo_03.mensaje (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversacion_id UUID NOT NULL,
    contenido TEXT NOT NULL,
    es_usuario BOOLEAN NOT NULL,
    fecha_creacion TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mensaje_conversacion FOREIGN KEY (conversacion_id) 
        REFERENCES grupo_03.conversacion(id) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_conversacion_usuario_id 
    ON grupo_03.conversacion(usuario_id);

CREATE INDEX IF NOT EXISTS idx_conversacion_fecha_actualizacion 
    ON grupo_03.conversacion(fecha_actualizacion DESC);

CREATE INDEX IF NOT EXISTS idx_mensaje_conversacion_id 
    ON grupo_03.mensaje(conversacion_id);

CREATE INDEX IF NOT EXISTS idx_mensaje_fecha_creacion 
    ON grupo_03.mensaje(fecha_creacion ASC);

-- Comentarios para documentación
COMMENT ON TABLE grupo_03.conversacion IS 'Almacena las conversaciones de chat entre usuarios y la IA';
COMMENT ON TABLE grupo_03.mensaje IS 'Almacena los mensajes individuales de cada conversación';

COMMENT ON COLUMN grupo_03.conversacion.usuario_id IS 'ID del usuario que inició la conversación';
COMMENT ON COLUMN grupo_03.conversacion.titulo IS 'Título generado automáticamente del primer mensaje';
COMMENT ON COLUMN grupo_03.mensaje.es_usuario IS 'true si el mensaje es del usuario, false si es de la IA';
