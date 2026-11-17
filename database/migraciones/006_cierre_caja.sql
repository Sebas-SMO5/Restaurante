-- =====================================================================
-- Migración 006 - Cierre de caja
-- Fecha: 2026-11-17
--
-- Guarda el cierre diario: ingresos (recibos activos), egresos activos,
-- efectivo esperado en caja, efectivo contado y la diferencia (sobrante/faltante).
-- Un solo cierre por día.
-- Ejecutar una sola vez, después de 005_pqrs.sql.
-- =====================================================================

CREATE TABLE cierre_caja (
    cc_id                 INT(11)      NOT NULL AUTO_INCREMENT,
    cc_fecha              DATE         NOT NULL,
    cc_ingresos           DOUBLE       NOT NULL,
    cc_egresos            DOUBLE       NOT NULL,
    cc_efectivo_esperado  DOUBLE       NOT NULL,
    cc_efectivo_contado   DOUBLE       NOT NULL,
    cc_diferencia         DOUBLE       NOT NULL,
    cc_observacion        VARCHAR(300) NULL,
    usu_id                INT(11)      NOT NULL,
    cc_fecha_registro     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (cc_id),
    UNIQUE KEY uq_cierre_fecha (cc_fecha),
    KEY idx_cierre_usuario (usu_id),
    CONSTRAINT fk_cierre_usuario FOREIGN KEY (usu_id) REFERENCES usuario (usu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
