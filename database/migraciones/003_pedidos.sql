-- =====================================================================
-- Migración 003 - Pedidos y comandas
-- Fecha: 2026-11-17
--
-- Qué hace:
--   1. pedido.ped_fecha guarda fecha y hora (para ordenar la cocina y los reportes).
--   2. pedido_detalle: el estado queda ligado a la tabla estado, la cantidad
--      es obligatoria y la observación admite 100 caracteres. (pla_id y ped_id
--      no se pueden volver NOT NULL por sus llaves foráneas; se validan en el código.)
--   3. Activa los estados que usan los pedidos y que estaban inactivos:
--      el tipo "Pedido" y el estado "En Preparación".
-- Ejecutar una sola vez, después de 002_estados.sql.
-- =====================================================================

ALTER TABLE pedido
    MODIFY COLUMN ped_fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE pedido_detalle
    MODIFY COLUMN ped_det_cant  INT(11)      NOT NULL DEFAULT 1,
    MODIFY COLUMN ped_det_obser VARCHAR(100) NULL,
    MODIFY COLUMN est_id        INT(11)      NOT NULL,
    ADD KEY idx_pedido_detalle_estado (est_id),
    ADD CONSTRAINT fk_pedido_detalle_estado
        FOREIGN KEY (est_id) REFERENCES estado (est_id);

UPDATE tipo_estado SET tes_estado = 'Activo' WHERE tes_id = 2;  -- Pedido
UPDATE estado      SET est_estado = 'Activo' WHERE est_id = 1;  -- En Preparación
