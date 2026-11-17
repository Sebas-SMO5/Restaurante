-- =====================================================================
-- Migración 005 - PQRS
-- Fecha: 2026-11-17
--
-- Qué hace:
--   1. Descripción y respuesta pasan de BLOB a TEXT (texto con tildes);
--      la respuesta queda vacía (NULL) hasta que se responda.
--   2. Se guarda la hora del registro, el nombre de quien radica, el origen
--      (Interno = registrada por un empleado, Web = radicada por el cliente),
--      la fecha de respuesta y el usuario que respondió.
--   3. Corrige la tilde de "Petición".
-- Ejecutar una sola vez, después de 004_recibos_egresos.sql.
-- =====================================================================

ALTER TABLE pqrs
    MODIFY COLUMN pqrs_fecha       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY COLUMN pqrs_descripcion TEXT     NOT NULL,
    MODIFY COLUMN pqrs_respuesta   TEXT     NULL,
    ADD COLUMN pqrs_nombre          VARCHAR(80) NOT NULL DEFAULT '' AFTER pqrs_fecha,
    ADD COLUMN pqrs_origen          VARCHAR(10) NOT NULL DEFAULT 'Interno' AFTER est_id,
    ADD COLUMN pqrs_fecha_respuesta DATETIME NULL AFTER pqrs_respuesta,
    ADD COLUMN usu_id_respuesta     INT(11)  NULL AFTER pqrs_fecha_respuesta,
    ADD KEY idx_pqrs_usuario_respuesta (usu_id_respuesta),
    ADD CONSTRAINT fk_pqrs_usuario_respuesta
        FOREIGN KEY (usu_id_respuesta) REFERENCES usuario (usu_id);

UPDATE tipo_tpqrs SET tpqrs_descripcion = 'Petición' WHERE tpqrs_descripcion = 'Peticion';
