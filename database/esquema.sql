
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria` (
  `idCategoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`idCategoria`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `categoria_insumo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria_insumo` (
  `cins_id` int(11) NOT NULL AUTO_INCREMENT,
  `cins_descripcion` varchar(40) NOT NULL,
  `cins_estado` varchar(10) NOT NULL DEFAULT 'Activo',
  PRIMARY KEY (`cins_id`),
  UNIQUE KEY `uq_categoria_insumo_descripcion` (`cins_descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cierre_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cierre_caja` (
  `cc_id` int(11) NOT NULL AUTO_INCREMENT,
  `cc_fecha` date NOT NULL,
  `cc_ingresos` double NOT NULL,
  `cc_egresos` double NOT NULL,
  `cc_efectivo_esperado` double NOT NULL,
  `cc_efectivo_contado` double NOT NULL,
  `cc_diferencia` double NOT NULL,
  `cc_observacion` varchar(300) DEFAULT NULL,
  `usu_id` int(11) NOT NULL,
  `cc_fecha_registro` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`cc_id`),
  UNIQUE KEY `uq_cierre_fecha` (`cc_fecha`),
  KEY `idx_cierre_usuario` (`usu_id`),
  CONSTRAINT `fk_cierre_usuario` FOREIGN KEY (`usu_id`) REFERENCES `usuario` (`usu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cliente` (
  `cli_id` int(15) NOT NULL AUTO_INCREMENT,
  `tipo_id` int(11) DEFAULT NULL,
  `cli_documento` varchar(20) DEFAULT NULL,
  `cli_nombre` varchar(60) DEFAULT NULL,
  `cli_apellidos` varchar(60) DEFAULT NULL,
  `cli_direccion` varchar(40) DEFAULT NULL,
  `cli_telefono` varchar(40) DEFAULT NULL,
  `cli_correo` varchar(40) NOT NULL,
  `cli_estado` varchar(10) NOT NULL,
  PRIMARY KEY (`cli_id`),
  UNIQUE KEY `uq_cliente_documento` (`tipo_id`,`cli_documento`),
  CONSTRAINT `fk_cliente_tipo_identidad` FOREIGN KEY (`tipo_id`) REFERENCES `tipo_identidad` (`tipo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `comanda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `comanda` (
  `com_id` int(11) NOT NULL AUTO_INCREMENT,
  `mesa` int(2) DEFAULT NULL,
  `pla_id` int(11) DEFAULT NULL,
  `com_obs` varchar(60) NOT NULL,
  `est_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`com_id`),
  KEY `pla_id` (`pla_id`),
  KEY `est_id` (`est_id`),
  CONSTRAINT `comanda_ibfk_1` FOREIGN KEY (`pla_id`) REFERENCES `plato` (`pla_id`),
  CONSTRAINT `comanda_ibfk_2` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `concepto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `concepto` (
  `con_id` int(3) NOT NULL,
  `con_descripcion` varchar(100) NOT NULL,
  `con_estado` varchar(10) NOT NULL,
  PRIMARY KEY (`con_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `encabezado_egresos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `encabezado_egresos` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `no_egreso` int(11) NOT NULL,
  `fecha_documento` datetime NOT NULL DEFAULT current_timestamp(),
  `tercero_identificacion` varchar(15) NOT NULL,
  `detalle` varchar(250) NOT NULL,
  `fp_id` int(11) NOT NULL,
  `conceptoEgreso_codigo` int(11) NOT NULL,
  `no_documento` varchar(15) DEFAULT NULL,
  `valor_egreso` double NOT NULL,
  `estado` varchar(10) NOT NULL,
  `usu_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_egreso_consecutivo` (`no_egreso`),
  KEY `idx_egreso_forma_pago` (`fp_id`),
  KEY `idx_egreso_concepto` (`conceptoEgreso_codigo`),
  KEY `idx_egreso_usuario` (`usu_id`),
  CONSTRAINT `fk_egreso_concepto` FOREIGN KEY (`conceptoEgreso_codigo`) REFERENCES `concepto` (`con_id`),
  CONSTRAINT `fk_egreso_forma_pago` FOREIGN KEY (`fp_id`) REFERENCES `forma_pago` (`fp_id`),
  CONSTRAINT `fk_egreso_usuario` FOREIGN KEY (`usu_id`) REFERENCES `usuario` (`usu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `estado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `estado` (
  `est_id` int(11) NOT NULL AUTO_INCREMENT,
  `est_descripcion` varchar(40) DEFAULT NULL,
  `tes_id` int(2) NOT NULL,
  `est_estado` varchar(10) NOT NULL,
  PRIMARY KEY (`est_id`),
  UNIQUE KEY `uq_estado_tipo_descripcion` (`tes_id`,`est_descripcion`),
  KEY `tes_id` (`tes_id`),
  CONSTRAINT `estado_ibfk_1` FOREIGN KEY (`tes_id`) REFERENCES `tipo_estado` (`tes_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `forma_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `forma_pago` (
  `fp_id` int(2) NOT NULL,
  `fp_descripcion` varchar(50) NOT NULL,
  `fp_estado` varchar(15) NOT NULL,
  PRIMARY KEY (`fp_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `insumo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `insumo` (
  `ins_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cins_id` int(11) NOT NULL,
  `um_id` int(11) NOT NULL,
  `ins_nombre` varchar(150) NOT NULL,
  `ins_codigo` varchar(30) DEFAULT NULL,
  `ins_codigo_barras` varchar(50) DEFAULT NULL,
  `ins_precio_compra` decimal(10,2) DEFAULT NULL,
  `ins_stock` decimal(12,3) NOT NULL DEFAULT 0.000,
  `ins_stock_min` decimal(12,3) NOT NULL DEFAULT 0.000,
  `ins_vendible` tinyint(1) NOT NULL DEFAULT 0,
  `ins_imagen` varchar(255) DEFAULT NULL,
  `ins_estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`ins_id`),
  KEY `idx_insumo_categoria` (`cins_id`),
  KEY `idx_insumo_unidad` (`um_id`),
  CONSTRAINT `fk_insumo_categoria_insumo` FOREIGN KEY (`cins_id`) REFERENCES `categoria_insumo` (`cins_id`),
  CONSTRAINT `fk_insumo_unidad_medida` FOREIGN KEY (`um_id`) REFERENCES `unidad_medida` (`um_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido` (
  `ped_id` int(11) NOT NULL AUTO_INCREMENT,
  `ped_fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `usu_id` int(11) DEFAULT NULL,
  `ped_mesa` varchar(4) NOT NULL,
  `est_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`ped_id`),
  KEY `est_id` (`est_id`),
  KEY `usu_id` (`usu_id`),
  CONSTRAINT `pedido_ibfk_1` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`),
  CONSTRAINT `pedido_ibfk_2` FOREIGN KEY (`usu_id`) REFERENCES `usuario` (`usu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `pedido_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido_detalle` (
  `ped_det_id` int(11) NOT NULL AUTO_INCREMENT,
  `ped_det_cant` int(11) NOT NULL DEFAULT 1,
  `ped_det_precio` float DEFAULT NULL,
  `ped_det_obser` varchar(100) DEFAULT NULL,
  `pla_id` int(11) DEFAULT NULL,
  `ped_id` int(11) DEFAULT NULL,
  `est_id` int(11) NOT NULL,
  PRIMARY KEY (`ped_det_id`),
  KEY `pla_id` (`pla_id`),
  KEY `ped_id` (`ped_id`),
  KEY `idx_pedido_detalle_estado` (`est_id`),
  CONSTRAINT `fk_pedido_detalle_estado` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`),
  CONSTRAINT `pedido_detalle_ibfk_1` FOREIGN KEY (`pla_id`) REFERENCES `plato` (`pla_id`),
  CONSTRAINT `pedido_detalle_ibfk_2` FOREIGN KEY (`ped_id`) REFERENCES `pedido` (`ped_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `perfil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `perfil` (
  `perf_id` int(11) NOT NULL AUTO_INCREMENT,
  `perf_descripcion` varchar(40) DEFAULT NULL,
  `perf_estado` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`perf_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `plato`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `plato` (
  `pla_id` int(11) NOT NULL AUTO_INCREMENT,
  `pla_descripcion` varchar(40) DEFAULT NULL,
  `idCategoria` int(11) DEFAULT NULL,
  `pla_precio` float DEFAULT NULL,
  `est_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`pla_id`),
  KEY `est_id` (`est_id`),
  KEY `idx_plato_categoria` (`idCategoria`),
  CONSTRAINT `fk_plato_categoria` FOREIGN KEY (`idCategoria`) REFERENCES `categoria` (`idCategoria`),
  CONSTRAINT `plato_ibfk_1` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `pqrs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pqrs` (
  `pqrs_id` int(4) NOT NULL AUTO_INCREMENT,
  `pqrs_fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `pqrs_nombre` varchar(80) NOT NULL DEFAULT '',
  `pqrs_descripcion` text NOT NULL,
  `pqrs_correo` varchar(60) NOT NULL,
  `pqrs_telefono` varchar(20) NOT NULL,
  `tpqrs_id` int(2) NOT NULL,
  `est_id` int(2) NOT NULL,
  `pqrs_origen` varchar(10) NOT NULL DEFAULT 'Interno',
  `pqrs_respuesta` text DEFAULT NULL,
  `pqrs_fecha_respuesta` datetime DEFAULT NULL,
  `usu_id_respuesta` int(11) DEFAULT NULL,
  PRIMARY KEY (`pqrs_id`),
  KEY `est_id` (`est_id`),
  KEY `tpqrs_id` (`tpqrs_id`),
  KEY `idx_pqrs_usuario_respuesta` (`usu_id_respuesta`),
  CONSTRAINT `fk_pqrs_usuario_respuesta` FOREIGN KEY (`usu_id_respuesta`) REFERENCES `usuario` (`usu_id`),
  CONSTRAINT `pqrs_ibfk_1` FOREIGN KEY (`est_id`) REFERENCES `estado` (`est_id`),
  CONSTRAINT `pqrs_ibfk_2` FOREIGN KEY (`tpqrs_id`) REFERENCES `tipo_tpqrs` (`tpqrs_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `recibo_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recibo_caja` (
  `rc_num` int(11) NOT NULL AUTO_INCREMENT,
  `usu_id` int(15) NOT NULL,
  `rc_fecha` datetime NOT NULL DEFAULT current_timestamp(),
  `ped_id` int(11) NOT NULL,
  `cli_id` int(15) NOT NULL,
  `fp_id` int(11) NOT NULL,
  `rc_total` double NOT NULL,
  `rc_observacion` varchar(360) DEFAULT NULL,
  `rc_estado` varchar(10) NOT NULL,
  PRIMARY KEY (`rc_num`),
  KEY `ped_id` (`ped_id`),
  KEY `cli_id` (`cli_id`),
  KEY `usu_id` (`usu_id`),
  KEY `idx_recibo_forma_pago` (`fp_id`),
  CONSTRAINT `fk_recibo_forma_pago` FOREIGN KEY (`fp_id`) REFERENCES `forma_pago` (`fp_id`),
  CONSTRAINT `recibo_caja_ibfk_1` FOREIGN KEY (`ped_id`) REFERENCES `pedido` (`ped_id`),
  CONSTRAINT `recibo_caja_ibfk_2` FOREIGN KEY (`cli_id`) REFERENCES `cliente` (`cli_id`),
  CONSTRAINT `recibo_caja_ibfk_3` FOREIGN KEY (`usu_id`) REFERENCES `usuario` (`usu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `recibo_caja_detalle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recibo_caja_detalle` (
  `rcd_id` int(11) NOT NULL AUTO_INCREMENT,
  `rcd_num` int(11) NOT NULL,
  `pla_id` int(4) NOT NULL,
  `rcd_precio` double NOT NULL,
  `rcd_cantidad` double NOT NULL,
  PRIMARY KEY (`rcd_id`),
  KEY `pla_id` (`pla_id`),
  KEY `rcd_num` (`rcd_num`),
  CONSTRAINT `recibo_caja_detalle_ibfk_1` FOREIGN KEY (`pla_id`) REFERENCES `plato` (`pla_id`),
  CONSTRAINT `recibo_caja_detalle_ibfk_2` FOREIGN KEY (`rcd_num`) REFERENCES `recibo_caja` (`rc_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_estado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo_estado` (
  `tes_id` int(2) NOT NULL AUTO_INCREMENT,
  `tes_descripcion` varchar(40) NOT NULL,
  `tes_estado` varchar(10) NOT NULL,
  PRIMARY KEY (`tes_id`),
  UNIQUE KEY `uq_tipo_estado_descripcion` (`tes_descripcion`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_identidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo_identidad` (
  `tipo_id` int(11) NOT NULL AUTO_INCREMENT,
  `tipo_descripcion` varchar(40) NOT NULL,
  `tipo_sigla` varchar(3) NOT NULL,
  `tipo_estado` varchar(10) NOT NULL,
  PRIMARY KEY (`tipo_id`),
  UNIQUE KEY `uq_tipo_identidad_sigla` (`tipo_sigla`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `tipo_tpqrs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo_tpqrs` (
  `tpqrs_id` int(2) NOT NULL AUTO_INCREMENT,
  `tpqrs_descripcion` varchar(40) DEFAULT NULL,
  `tpqrs_estado` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`tpqrs_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `unidad_medida`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `unidad_medida` (
  `um_id` int(11) NOT NULL,
  `um_descripcion` varchar(30) NOT NULL,
  `um_abreviatura` varchar(10) NOT NULL,
  `um_estado` varchar(10) NOT NULL,
  PRIMARY KEY (`um_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `usu_id` int(11) NOT NULL AUTO_INCREMENT,
  `usu_nombre` varchar(60) NOT NULL,
  `usu_apellido` varchar(60) NOT NULL,
  `usu_direccion` varchar(100) NOT NULL,
  `usu_telefono` varchar(20) NOT NULL,
  `usu_correo` varchar(70) NOT NULL,
  `perf_id` int(11) DEFAULT NULL,
  `usu_login` varchar(30) NOT NULL,
  `usu_pass` varchar(100) NOT NULL,
  `usu_estado` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`usu_id`),
  KEY `perf_id` (`perf_id`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`perf_id`) REFERENCES `perfil` (`perf_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1144156123 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

