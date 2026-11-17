-- =====================================================================
-- Migración 001 - Correcciones de estructura de bd_restaurante
-- Fecha: 2026-11-17
--
-- Qué hace:
--   1. Codificación: toda la base pasa a utf8mb4 y se reparan 2 textos dañados.
--   2. Cliente: se agrega tipo y número de documento (ligado a tipo_identidad).
--   3. Plato: se agrega la categoría (ligada a categoria).
--   4. Insumo: se agrega la unidad de medida y la categoría de insumo;
--      se eliminan sede_id y pres_id, que apuntaban a tablas inexistentes.
--   5. Otras: llave primaria en concepto y forma_pago (no tenían).
--
-- Antes de ejecutarla, haz una copia de seguridad de la base.
-- Ejecutar una sola vez (phpMyAdmin > bd_restaurante > SQL).
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. Codificación utf8mb4 en toda la base
-- ---------------------------------------------------------------------
ALTER DATABASE bd_restaurante CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

ALTER TABLE cliente             CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE comanda             CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE estado              CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE pedido              CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE pedido_detalle      CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE perfil              CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE plato               CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE pqrs                CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE recibo_caja         CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE recibo_caja_detalle CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE tipo_estado         CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE tipo_tpqrs          CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
ALTER TABLE usuario             CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Textos que estaban guardados con tildes dañadas
UPDATE estado         SET est_descripcion = 'En Preparación'
 WHERE est_id = 1;
UPDATE pedido_detalle SET ped_det_obser = REPLACE(ped_det_obser, 'prociÃ³n', 'porción')
 WHERE ped_det_obser LIKE '%Ã³%';

-- ---------------------------------------------------------------------
-- 2. Cliente: tipo y número de documento
-- ---------------------------------------------------------------------
-- Tipos de identidad base (la tabla estaba vacía)
INSERT INTO tipo_identidad (tipo_descripcion, tipo_sigla, tipo_estado)
SELECT * FROM (
    SELECT 'Cédula de ciudadanía' AS d, 'CC' AS s, 'Activo' AS e UNION ALL
    SELECT 'Tarjeta de identidad',  'TI',  'Activo' UNION ALL
    SELECT 'Cédula de extranjería', 'CE',  'Activo' UNION ALL
    SELECT 'Pasaporte',             'PA',  'Activo' UNION ALL
    SELECT 'NIT',                   'NIT', 'Activo'
) AS base
WHERE NOT EXISTS (SELECT 1 FROM tipo_identidad);

ALTER TABLE tipo_identidad
    ADD UNIQUE KEY uq_tipo_identidad_sigla (tipo_sigla);

-- Los clientes existentes quedan sin documento (NULL) hasta que se editen.
ALTER TABLE cliente
    ADD COLUMN tipo_id INT(11) NULL AFTER cli_id,
    ADD COLUMN cli_documento VARCHAR(20) NULL AFTER tipo_id,
    ADD UNIQUE KEY uq_cliente_documento (tipo_id, cli_documento),
    ADD CONSTRAINT fk_cliente_tipo_identidad
        FOREIGN KEY (tipo_id) REFERENCES tipo_identidad (tipo_id);

-- Estado escrito de forma inconsistente
UPDATE cliente SET cli_estado = 'Inactivo' WHERE LOWER(cli_estado) = 'inactivo';
UPDATE cliente SET cli_estado = 'Activo'   WHERE LOWER(cli_estado) = 'activo';

-- ---------------------------------------------------------------------
-- 3. Plato: categoría
-- ---------------------------------------------------------------------
ALTER TABLE plato
    ADD COLUMN idCategoria INT(11) NULL AFTER pla_descripcion,
    ADD KEY idx_plato_categoria (idCategoria),
    ADD CONSTRAINT fk_plato_categoria
        FOREIGN KEY (idCategoria) REFERENCES categoria (idCategoria);

-- ---------------------------------------------------------------------
-- 4. Insumo: unidad de medida y categoría de insumo
-- ---------------------------------------------------------------------
CREATE TABLE categoria_insumo (
    cins_id          INT(11)     NOT NULL AUTO_INCREMENT,
    cins_descripcion VARCHAR(40) NOT NULL,
    cins_estado      VARCHAR(10) NOT NULL DEFAULT 'Activo',
    PRIMARY KEY (cins_id),
    UNIQUE KEY uq_categoria_insumo_descripcion (cins_descripcion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO categoria_insumo (cins_id, cins_descripcion, cins_estado) VALUES
    (1, 'Granos y harinas',      'Activo'),
    (2, 'Lácteos',               'Activo'),
    (3, 'Aceites y grasas',      'Activo'),
    (4, 'Condimentos y azúcares','Activo'),
    (5, 'Bebidas',               'Activo'),
    (6, 'Carnes',                'Activo'),
    (7, 'Frutas y verduras',     'Activo');

-- Se clasifican los insumos actuales según su nombre
UPDATE insumo SET cins_id = 1 WHERE ins_nombre IN ('Arroz', 'Harina de trigo', 'Lentejas', 'Frijoles', 'Pasta');
UPDATE insumo SET cins_id = 2 WHERE ins_nombre IN ('Leche');
UPDATE insumo SET cins_id = 3 WHERE ins_nombre IN ('Aceite vegetal');
UPDATE insumo SET cins_id = 4 WHERE ins_nombre IN ('Azúcar', 'Sal');
UPDATE insumo SET cins_id = 5 WHERE ins_nombre IN ('Café');

-- Unidad de medida: los insumos actuales quedan en 'Unidad' (um_id = 1)
ALTER TABLE insumo
    MODIFY COLUMN cins_id INT(11) NOT NULL,
    ADD COLUMN um_id INT(11) NOT NULL DEFAULT 1 AFTER cins_id;

ALTER TABLE insumo
    ALTER COLUMN um_id DROP DEFAULT,
    DROP COLUMN sede_id,
    DROP COLUMN pres_id,
    ADD KEY idx_insumo_categoria (cins_id),
    ADD KEY idx_insumo_unidad (um_id),
    ADD CONSTRAINT fk_insumo_categoria_insumo
        FOREIGN KEY (cins_id) REFERENCES categoria_insumo (cins_id),
    ADD CONSTRAINT fk_insumo_unidad_medida
        FOREIGN KEY (um_id) REFERENCES unidad_medida (um_id);

-- ---------------------------------------------------------------------
-- 5. Llaves primarias faltantes
-- ---------------------------------------------------------------------
ALTER TABLE concepto   ADD PRIMARY KEY (con_id);
ALTER TABLE forma_pago ADD PRIMARY KEY (fp_id);

-- ---------------------------------------------------------------------
-- 6. Insumo: el código se genera automáticamente
--    (la pantalla de Materias primas no envía el código al registrar).
-- ---------------------------------------------------------------------
ALTER TABLE insumo MODIFY COLUMN ins_id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT;
