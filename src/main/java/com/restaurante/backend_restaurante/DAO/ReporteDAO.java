package com.restaurante.backend_restaurante.DAO;

import com.restaurante.backend_restaurante.Config.EstadosConfig;
import com.restaurante.backend_restaurante.Model.ReporteVista;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Consultas de reportes: dashboard de inicio y cierre de caja.
 * Solo cuentan los recibos y egresos con estado "Activo" (los anulados no suman).
 */
@Repository
public class ReporteDAO {

    private final JdbcTemplate jdbcTemplate;
    private final EstadosConfig estados;
    private final int pqrsPendiente;

    public ReporteDAO(
            JdbcTemplate jdbcTemplate,
            EstadosConfig estados,
            @Value("${app.estados.pqrs.pendiente:11}") int pqrsPendiente) {
        this.jdbcTemplate = jdbcTemplate;
        this.estados = estados;
        this.pqrsPendiente = pqrsPendiente;
    }

    private double numero(String sql, Object... parametros) {
        Double valor = jdbcTemplate.queryForObject(sql, Double.class, parametros);
        return valor == null ? 0 : valor;
    }

    private int entero(String sql, Object... parametros) {
        Integer valor = jdbcTemplate.queryForObject(sql, Integer.class, parametros);
        return valor == null ? 0 : valor;
    }

    // ----------------------------- Dashboard -----------------------------

    public double ventasDelDia(LocalDate dia) {
        return numero("SELECT COALESCE(SUM(rc_total), 0) FROM recibo_caja WHERE rc_estado = 'Activo' AND DATE(rc_fecha) = ?",
                Date.valueOf(dia));
    }

    public int recibosDelDia(LocalDate dia) {
        return entero("SELECT COUNT(*) FROM recibo_caja WHERE rc_estado = 'Activo' AND DATE(rc_fecha) = ?",
                Date.valueOf(dia));
    }

    public double egresosDelDia(LocalDate dia) {
        return numero("SELECT COALESCE(SUM(valor_egreso), 0) FROM encabezado_egresos WHERE estado = 'Activo' AND DATE(fecha_documento) = ?",
                Date.valueOf(dia));
    }

    public int pedidosAbiertos() {
        return entero("SELECT COUNT(*) FROM pedido WHERE est_id = ?", estados.pedidoCreado);
    }

    public int platosEnCocina() {
        return entero("""
                SELECT COALESCE(SUM(d.ped_det_cant), 0)
                FROM pedido_detalle d JOIN pedido p ON p.ped_id = d.ped_id
                WHERE d.est_id = ? AND p.est_id = ?
                """, estados.detallePreparacion, estados.pedidoCreado);
    }

    /** {cantidad, valor} de pedidos finalizados sin recibo activo. */
    public double[] porCobrar() {
        return jdbcTemplate.queryForObject("""
                SELECT COUNT(DISTINCT p.ped_id) AS cantidad,
                       COALESCE(SUM(d.ped_det_cant * d.ped_det_precio), 0) AS valor
                FROM pedido p
                LEFT JOIN pedido_detalle d ON d.ped_id = p.ped_id AND d.est_id <> ?
                WHERE p.est_id = ?
                  AND NOT EXISTS (SELECT 1 FROM recibo_caja r WHERE r.ped_id = p.ped_id AND r.rc_estado = 'Activo')
                """,
                (rs, i) -> new double[] { rs.getDouble("cantidad"), rs.getDouble("valor") },
                estados.detalleCancelado, estados.pedidoFinalizado);
    }

    public List<LocalDateTime> fechasPqrsPendientes() {
        return jdbcTemplate.query("SELECT pqrs_fecha FROM pqrs WHERE est_id = ?",
                (rs, i) -> rs.getTimestamp("pqrs_fecha").toLocalDateTime(), pqrsPendiente);
    }

    public List<ReporteVista.StockBajo> stockBajo() {
        return jdbcTemplate.query("""
                SELECT i.ins_nombre, i.ins_stock, i.ins_stock_min, u.um_abreviatura
                FROM insumo i JOIN unidad_medida u ON u.um_id = i.um_id
                WHERE i.ins_estado = 1 AND i.ins_stock <= i.ins_stock_min
                ORDER BY (i.ins_stock - i.ins_stock_min), i.ins_nombre
                LIMIT 8
                """,
                (rs, i) -> new ReporteVista.StockBajo(
                        rs.getString("ins_nombre"), rs.getDouble("ins_stock"),
                        rs.getDouble("ins_stock_min"), rs.getString("um_abreviatura")));
    }

    /** Ventas por día en el rango [desde, hasta]; los días sin ventas no vienen (se completan en el controlador). */
    public List<ReporteVista.VentaDia> ventasPorDia(LocalDate desde, LocalDate hasta) {
        return jdbcTemplate.query("""
                SELECT DATE(rc_fecha) AS dia, SUM(rc_total) AS total, COUNT(*) AS recibos
                FROM recibo_caja
                WHERE rc_estado = 'Activo' AND DATE(rc_fecha) BETWEEN ? AND ?
                GROUP BY DATE(rc_fecha)
                """,
                (rs, i) -> new ReporteVista.VentaDia(
                        rs.getDate("dia").toLocalDate(), rs.getDouble("total"), rs.getInt("recibos")),
                Date.valueOf(desde), Date.valueOf(hasta));
    }

    public List<ReporteVista.PlatoVendido> topPlatos(LocalDate desde, int limite) {
        return jdbcTemplate.query("""
                SELECT pl.pla_descripcion AS plato, SUM(d.rcd_cantidad) AS cantidad,
                       SUM(d.rcd_cantidad * d.rcd_precio) AS total
                FROM recibo_caja_detalle d
                JOIN recibo_caja r ON r.rc_num = d.rcd_num
                JOIN plato pl ON pl.pla_id = d.pla_id
                WHERE r.rc_estado = 'Activo' AND DATE(r.rc_fecha) >= ?
                GROUP BY pl.pla_id, pl.pla_descripcion
                ORDER BY cantidad DESC, total DESC
                LIMIT ?
                """,
                (rs, i) -> new ReporteVista.PlatoVendido(
                        rs.getString("plato"), rs.getDouble("cantidad"), rs.getDouble("total")),
                Date.valueOf(desde), limite);
    }

    // ---------------------------- Cierre de caja ----------------------------

    public List<ReporteVista.PorFormaPago> ingresosPorFormaPago(LocalDate dia) {
        return jdbcTemplate.query("""
                SELECT f.fp_id, f.fp_descripcion, COUNT(r.rc_num) AS cantidad, COALESCE(SUM(r.rc_total), 0) AS total
                FROM recibo_caja r JOIN forma_pago f ON f.fp_id = r.fp_id
                WHERE r.rc_estado = 'Activo' AND DATE(r.rc_fecha) = ?
                GROUP BY f.fp_id, f.fp_descripcion
                ORDER BY f.fp_id
                """,
                (rs, i) -> new ReporteVista.PorFormaPago(
                        rs.getInt("fp_id"), rs.getString("fp_descripcion"),
                        esEfectivo(rs.getString("fp_descripcion")), rs.getInt("cantidad"), rs.getDouble("total")),
                Date.valueOf(dia));
    }

    public List<ReporteVista.PorFormaPago> egresosPorFormaPago(LocalDate dia) {
        return jdbcTemplate.query("""
                SELECT f.fp_id, f.fp_descripcion, COUNT(e.id) AS cantidad, COALESCE(SUM(e.valor_egreso), 0) AS total
                FROM encabezado_egresos e JOIN forma_pago f ON f.fp_id = e.fp_id
                WHERE e.estado = 'Activo' AND DATE(e.fecha_documento) = ?
                GROUP BY f.fp_id, f.fp_descripcion
                ORDER BY f.fp_id
                """,
                (rs, i) -> new ReporteVista.PorFormaPago(
                        rs.getInt("fp_id"), rs.getString("fp_descripcion"),
                        esEfectivo(rs.getString("fp_descripcion")), rs.getInt("cantidad"), rs.getDouble("total")),
                Date.valueOf(dia));
    }

    public List<ReporteVista.MovimientoCaja> movimientos(LocalDate dia) {
        return jdbcTemplate.query("""
                SELECT 'Ingreso' AS tipo, CONCAT('Recibo #', r.rc_num) AS numero, r.rc_fecha AS fecha,
                       CONCAT('Pedido #', r.ped_id, ' · ', CONCAT_WS(' ', c.cli_nombre, c.cli_apellidos)) AS detalle,
                       f.fp_descripcion AS forma_pago, r.rc_total AS valor
                FROM recibo_caja r
                JOIN forma_pago f ON f.fp_id = r.fp_id
                JOIN cliente c ON c.cli_id = r.cli_id
                WHERE r.rc_estado = 'Activo' AND DATE(r.rc_fecha) = ?
                UNION ALL
                SELECT 'Egreso', CONCAT('Egreso N.° ', e.no_egreso), e.fecha_documento,
                       CONCAT(co.con_descripcion, ' · ', e.detalle), f.fp_descripcion, e.valor_egreso
                FROM encabezado_egresos e
                JOIN forma_pago f ON f.fp_id = e.fp_id
                JOIN concepto co ON co.con_id = e.conceptoEgreso_codigo
                WHERE e.estado = 'Activo' AND DATE(e.fecha_documento) = ?
                ORDER BY fecha
                """,
                (rs, i) -> new ReporteVista.MovimientoCaja(
                        rs.getString("tipo"), rs.getString("numero"),
                        rs.getTimestamp("fecha").toLocalDateTime(), rs.getString("detalle"),
                        rs.getString("forma_pago"), rs.getDouble("valor")),
                Date.valueOf(dia), Date.valueOf(dia));
    }

    public ReporteVista.CierreGuardado cierreDelDia(LocalDate dia) {
        List<ReporteVista.CierreGuardado> lista = jdbcTemplate.query("""
                SELECT c.*, CONCAT_WS(' ', u.usu_nombre, u.usu_apellido) AS registrado_por
                FROM cierre_caja c LEFT JOIN usuario u ON u.usu_id = c.usu_id
                WHERE c.cc_fecha = ?
                """,
                (rs, i) -> new ReporteVista.CierreGuardado(
                        rs.getInt("cc_id"), rs.getDate("cc_fecha").toLocalDate(),
                        rs.getDouble("cc_ingresos"), rs.getDouble("cc_egresos"),
                        rs.getDouble("cc_efectivo_esperado"), rs.getDouble("cc_efectivo_contado"),
                        rs.getDouble("cc_diferencia"), rs.getString("cc_observacion"),
                        rs.getString("registrado_por"),
                        fecha(rs.getTimestamp("cc_fecha_registro"))),
                Date.valueOf(dia));

        return lista.isEmpty() ? null : lista.get(0);
    }

    public void guardarCierre(LocalDate dia, double ingresos, double egresos, double esperado,
                              double contado, String observacion, int usuId) {
        jdbcTemplate.update("""
                INSERT INTO cierre_caja (cc_fecha, cc_ingresos, cc_egresos, cc_efectivo_esperado,
                                         cc_efectivo_contado, cc_diferencia, cc_observacion, usu_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                Date.valueOf(dia), ingresos, egresos, esperado, contado, contado - esperado, observacion, usuId);
    }

    public int pedidosSinCobrar() {
        return (int) porCobrar()[0];
    }

    /** Una forma de pago cuenta como efectivo si su nombre contiene "efectivo". */
    public static boolean esEfectivo(String descripcion) {
        return descripcion != null && descripcion.toLowerCase().contains("efectivo");
    }

    private static LocalDateTime fecha(Timestamp valor) {
        return valor == null ? null : valor.toLocalDateTime();
    }
}
