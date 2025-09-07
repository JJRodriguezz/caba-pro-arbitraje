-- Migración para agregar la columna asignacion_id a la tabla notificacion
ALTER TABLE notificacion ADD COLUMN asignacion_id BIGINT NOT NULL DEFAULT 0;
