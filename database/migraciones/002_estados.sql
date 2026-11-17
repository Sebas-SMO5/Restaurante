-- =====================================================================
-- Migración 002 - Estados
-- Fecha: 2026-11-17
--
-- Qué hace:
--   1. Normaliza el texto "InActivo" -> "Inactivo" en los catálogos de estados.
--   2. Evita estados repetidos dentro del mismo tipo de estado.
--
-- No modifica los estados asignados a los platos: los que no son de tipo
-- "Plato" se marcan en la pantalla para corregirlos manualmente.
-- Ejecutar una sola vez, después de 001_correcciones_estructura.sql.
-- =====================================================================

UPDATE tipo_estado SET tes_estado = 'Inactivo' WHERE LOWER(tes_estado) = 'inactivo';
UPDATE tipo_estado SET tes_estado = 'Activo'   WHERE LOWER(tes_estado) = 'activo';
UPDATE estado      SET est_estado = 'Inactivo' WHERE LOWER(est_estado) = 'inactivo';
UPDATE estado      SET est_estado = 'Activo'   WHERE LOWER(est_estado) = 'activo';

ALTER TABLE estado
    ADD UNIQUE KEY uq_estado_tipo_descripcion (tes_id, est_descripcion);

ALTER TABLE tipo_estado
    ADD UNIQUE KEY uq_tipo_estado_descripcion (tes_descripcion);
