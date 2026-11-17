-- =====================================================================
-- Migración 004 - Recibos de caja y comprobantes de egreso
-- Fecha: 2026-11-17
--
-- Qué hace:
--   1. recibo_caja: guarda la forma de pago y la hora; la observación es opcional.
--   2. Cliente genérico "Consumidor final" para ventas sin datos del cliente.
--   3. encabezado_egresos: llave primaria automática, consecutivo único,
--      fecha que ya no cambia sola al modificar el registro, usuario que lo
--      registra y relaciones con forma_pago y concepto.
-- Ejecutar una sola vez, después de 003_pedidos.sql.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Recibo de caja
-- ---------------------------------------------------------------------
ALTER TABLE recibo_caja
    MODIFY COLUMN rc_fecha       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN rc_observacion VARCHAR(360) NULL,
    ADD COLUMN fp_id INT(11) NOT NULL AFTER cli_id,
    ADD KEY idx_recibo_forma_pago (fp_id),
    ADD CONSTRAINT fk_recibo_forma_pago FOREIGN KEY (fp_id) REFERENCES forma_pago (fp_id);

-- ---------------------------------------------------------------------
-- 2. Cliente "Consumidor final"
-- ---------------------------------------------------------------------
INSERT INTO cliente (tipo_id, cli_documento, cli_nombre, cli_apellidos, cli_direccion,
                     cli_telefono, cli_correo, cli_estado)
SELECT t.tipo_id, '222222222222', 'Consumidor', 'Final', NULL, NULL, 'consumidor@final.co', 'Activo'
FROM tipo_identidad t
WHERE t.tipo_sigla = 'CC'
  AND NOT EXISTS (SELECT 1 FROM cliente WHERE cli_documento = '222222222222');

-- ---------------------------------------------------------------------
-- 3. Comprobante de egreso
-- ---------------------------------------------------------------------
ALTER TABLE encabezado_egresos
    MODIFY COLUMN id INT(11) NOT NULL AUTO_INCREMENT,
    ADD PRIMARY KEY (id);

ALTER TABLE encabezado_egresos
    MODIFY COLUMN fecha_documento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN fp_id INT(11) NOT NULL,
    MODIFY COLUMN conceptoEgreso_codigo INT(11) NOT NULL,
    MODIFY COLUMN no_documento VARCHAR(15) NULL,
    ADD COLUMN usu_id INT(11) NULL AFTER estado,
    ADD UNIQUE KEY uq_egreso_consecutivo (no_egreso),
    ADD KEY idx_egreso_forma_pago (fp_id),
    ADD KEY idx_egreso_concepto (conceptoEgreso_codigo),
    ADD KEY idx_egreso_usuario (usu_id),
    ADD CONSTRAINT fk_egreso_forma_pago FOREIGN KEY (fp_id) REFERENCES forma_pago (fp_id),
    ADD CONSTRAINT fk_egreso_concepto FOREIGN KEY (conceptoEgreso_codigo) REFERENCES concepto (con_id),
    ADD CONSTRAINT fk_egreso_usuario FOREIGN KEY (usu_id) REFERENCES usuario (usu_id);
