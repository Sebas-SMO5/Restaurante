package com.restaurante.backend_restaurante.DAO;

import com.restaurante.backend_restaurante.Config.EstadosConfig;
import com.restaurante.backend_restaurante.Model.CajaVista;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

/**
 * Consultas de caja: recibos de caja (ingresos) y comprobantes de egreso (salidas).
 */
@Repository
public class CajaDAO {

    public static final String ACTIVO = "Activo";
    public static final String ANULADO = "Anulado";

    private final JdbcTemplate jdbcTemplate;
    private final EstadosConfig estados;

    public CajaDAO(JdbcTemplate jdbcTemplate, EstadosConfig estados) {
        this.jdbcTemplate = jdbcTemplate;
        this.estados = estados;
    }

    // =================================================================
    // Recibos de caja
    // =================================================================

    /** Pedidos finalizados sin recibo activo, con su total (sin platos cancelados). */
    public List<CajaVista.PorCobrar> pedidosPorCobrar() {
        return jdbcTemplate.query(
                """
                SELECT p.ped_id, p.ped_mesa, p.ped_fecha,
                       CONCAT_WS(' ', u.usu_nombre, u.usu_apellido) AS mesero,
                       COALESCE(SUM(d.ped_det_cant), 0) AS items,
                       COALESCE(SUM(d.ped_det_cant * d.ped_det_precio), 0) AS total
                FROM pedido p
                LEFT JOIN usuario u ON u.usu_id = p.usu_id
                LEFT JOIN pedido_detalle d ON d.ped_id = p.ped_id AND d.est_id <> ?
                WHERE p.est_id = ?
                  AND NOT EXISTS (SELECT 1 FROM recibo_caja r WHERE r.ped_id = p.ped_id AND r.rc_estado = ?)
                GROUP BY p.ped_id, p.ped_mesa, p.ped_fecha, u.usu_nombre, u.usu_apellido
                ORDER BY p.ped_fecha
                """,
                (rs, i) -> new CajaVista.PorCobrar(
                        rs.getInt("ped_id"),
                        rs.getString("ped_mesa"),
                        rs.getTimestamp("ped_fecha").toLocalDateTime(),
                        rs.getString("mesero"),
                        rs.getInt("items"),
                        rs.getDouble("total")),
                estados.detalleCancelado, estados.pedidoFinalizado, ACTIVO);
    }

    private static final String SELECT_RECIBO = """
        SELECT r.rc_num, r.rc_fecha, r.ped_id, p.ped_mesa, r.cli_id,
               CONCAT_WS(' ', c.cli_nombre, c.cli_apellidos) AS cliente,
               CONCAT_WS(' ', t.tipo_sigla, c.cli_documento) AS cli_documento,
               r.fp_id, f.fp_descripcion AS forma_pago, r.usu_id,
               CONCAT_WS(' ', u.usu_nombre, u.usu_apellido) AS cajero,
               r.rc_total, r.rc_observacion, r.rc_estado
        FROM recibo_caja r
        JOIN pedido p ON p.ped_id = r.ped_id
        JOIN cliente c ON c.cli_id = r.cli_id
        LEFT JOIN tipo_identidad t ON t.tipo_id = c.tipo_id
        JOIN forma_pago f ON f.fp_id = r.fp_id
        LEFT JOIN usuario u ON u.usu_id = r.usu_id
        """;

    private CajaVista.Recibo mapRecibo(ResultSet rs, int rowNum) throws SQLException {
        return new CajaVista.Recibo(
                rs.getInt("rc_num"),
                rs.getTimestamp("rc_fecha").toLocalDateTime(),
                rs.getInt("ped_id"),
                rs.getString("ped_mesa"),
                rs.getInt("cli_id"),
                rs.getString("cliente"),
                rs.getString("cli_documento"),
                rs.getInt("fp_id"),
                rs.getString("forma_pago"),
                rs.getInt("usu_id"),
                rs.getString("cajero"),
                rs.getDouble("rc_total"),
                rs.getString("rc_observacion"),
                rs.getString("rc_estado"));
    }

    public List<CajaVista.Recibo> recibos() {
        return jdbcTemplate.query(SELECT_RECIBO + " ORDER BY r.rc_fecha DESC, r.rc_num DESC", this::mapRecibo);
    }

    public CajaVista.Recibo recibo(int rcNum) {
        List<CajaVista.Recibo> lista = jdbcTemplate.query(SELECT_RECIBO + " WHERE r.rc_num = ?", this::mapRecibo, rcNum);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public List<CajaVista.ReciboItem> itemsRecibo(int rcNum) {
        return jdbcTemplate.query(
                """
                SELECT d.pla_id, pl.pla_descripcion AS plato, d.rcd_cantidad, d.rcd_precio
                FROM recibo_caja_detalle d
                JOIN plato pl ON pl.pla_id = d.pla_id
                WHERE d.rcd_num = ?
                ORDER BY d.rcd_id
                """,
                (rs, i) -> new CajaVista.ReciboItem(
                        rs.getInt("pla_id"),
                        rs.getString("plato"),
                        rs.getDouble("rcd_cantidad"),
                        rs.getDouble("rcd_precio"),
                        rs.getDouble("rcd_cantidad") * rs.getDouble("rcd_precio")),
                rcNum);
    }

    public Integer estadoPedido(int pedId) {
        List<Integer> lista = jdbcTemplate.queryForList("SELECT est_id FROM pedido WHERE ped_id = ?", Integer.class, pedId);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public Integer reciboActivoDePedido(int pedId) {
        List<Integer> lista = jdbcTemplate.queryForList(
                "SELECT rc_num FROM recibo_caja WHERE ped_id = ? AND rc_estado = ? LIMIT 1",
                Integer.class, pedId, ACTIVO);
        return lista.isEmpty() ? null : lista.get(0);
    }

    public boolean clienteActivo(int cliId) {
        return contar("SELECT COUNT(*) FROM cliente WHERE cli_id = ? AND LOWER(cli_estado) = 'activo'", cliId) > 0;
    }

    public boolean formaPagoActiva(int fpId) {
        return contar("SELECT COUNT(*) FROM forma_pago WHERE fp_id = ? AND LOWER(fp_estado) = 'activo'", fpId) > 0;
    }

    public boolean conceptoActivo(int conId) {
        return contar("SELECT COUNT(*) FROM concepto WHERE con_id = ? AND LOWER(con_estado) = 'activo'", conId) > 0;
    }

    /** Indica si la caja de ese día ya tiene un cierre registrado. */
    public boolean cajaCerrada(LocalDate dia) {
        return contar("SELECT COUNT(*) FROM cierre_caja WHERE cc_fecha = ?", java.sql.Date.valueOf(dia)) > 0;
    }

    /** Platos cobrables del pedido agrupados por plato y precio: {pla_id, cantidad, precio}. */
    public List<double[]> lineasCobrables(int pedId) {
        return jdbcTemplate.query(
                """
                SELECT pla_id, SUM(ped_det_cant) AS cantidad, ped_det_precio AS precio
                FROM pedido_detalle
                WHERE ped_id = ? AND est_id <> ?
                GROUP BY pla_id, ped_det_precio
                ORDER BY MIN(ped_det_id)
                """,
                (rs, i) -> new double[] { rs.getInt("pla_id"), rs.getDouble("cantidad"), rs.getDouble("precio") },
                pedId, estados.detalleCancelado);
    }

    public int insertarRecibo(int usuId, int pedId, int cliId, int fpId, double total, String observacion) {
        KeyHolder llave = new GeneratedKeyHolder();

        jdbcTemplate.update(conexion -> {
            PreparedStatement ps = conexion.prepareStatement(
                    """
                    INSERT INTO recibo_caja (usu_id, rc_fecha, ped_id, cli_id, fp_id, rc_total, rc_observacion, rc_estado)
                    VALUES (?, NOW(), ?, ?, ?, ?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, usuId);
            ps.setInt(2, pedId);
            ps.setInt(3, cliId);
            ps.setInt(4, fpId);
            ps.setDouble(5, total);
            ps.setString(6, observacion);
            ps.setString(7, ACTIVO);
            return ps;
        }, llave);

        return llave.getKey().intValue();
    }

    public void insertarLineaRecibo(int rcNum, int plaId, double cantidad, double precio) {
        jdbcTemplate.update(
                "INSERT INTO recibo_caja_detalle (rcd_num, pla_id, rcd_precio, rcd_cantidad) VALUES (?, ?, ?, ?)",
                rcNum, plaId, precio, cantidad);
    }

    public void anularRecibo(int rcNum, String observacion) {
        jdbcTemplate.update(
                "UPDATE recibo_caja SET rc_estado = ?, rc_observacion = ? WHERE rc_num = ?",
                ANULADO, observacion, rcNum);
    }

    // =================================================================
    // Comprobantes de egreso
    // =================================================================

    private static final String SELECT_EGRESO = """
        SELECT e.id, e.no_egreso, e.fecha_documento, e.tercero_identificacion, e.detalle,
               e.fp_id, f.fp_descripcion AS forma_pago,
               e.conceptoEgreso_codigo, c.con_descripcion AS concepto,
               e.no_documento, e.valor_egreso, e.estado,
               CONCAT_WS(' ', u.usu_nombre, u.usu_apellido) AS registrado_por
        FROM encabezado_egresos e
        JOIN forma_pago f ON f.fp_id = e.fp_id
        JOIN concepto c ON c.con_id = e.conceptoEgreso_codigo
        LEFT JOIN usuario u ON u.usu_id = e.usu_id
        """;

    private CajaVista.Egreso mapEgreso(ResultSet rs, int rowNum) throws SQLException {
        return new CajaVista.Egreso(
                rs.getInt("id"),
                rs.getInt("no_egreso"),
                rs.getTimestamp("fecha_documento").toLocalDateTime(),
                rs.getString("tercero_identificacion"),
                rs.getString("detalle"),
                rs.getInt("fp_id"),
                rs.getString("forma_pago"),
                rs.getInt("conceptoEgreso_codigo"),
                rs.getString("concepto"),
                rs.getString("no_documento"),
                rs.getDouble("valor_egreso"),
                rs.getString("estado"),
                rs.getString("registrado_por"));
    }

    public List<CajaVista.Egreso> egresos() {
        return jdbcTemplate.query(SELECT_EGRESO + " ORDER BY e.fecha_documento DESC, e.no_egreso DESC", this::mapEgreso);
    }

    public CajaVista.Egreso egreso(int id) {
        List<CajaVista.Egreso> lista = jdbcTemplate.query(SELECT_EGRESO + " WHERE e.id = ?", this::mapEgreso, id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Siguiente consecutivo; bloquea la tabla dentro de la transacción para evitar repetidos. */
    public int siguienteConsecutivoEgreso() {
        Integer maximo = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(no_egreso), 0) FROM encabezado_egresos FOR UPDATE", Integer.class);
        return (maximo == null ? 0 : maximo) + 1;
    }

    public void insertarEgreso(int consecutivo, CajaVista.NuevoEgreso e, int usuId) {
        jdbcTemplate.update(
                """
                INSERT INTO encabezado_egresos (no_egreso, fecha_documento, tercero_identificacion, detalle,
                                                fp_id, conceptoEgreso_codigo, no_documento, valor_egreso, estado, usu_id)
                VALUES (?, NOW(), ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                consecutivo, e.tercero_identificacion(), e.detalle(), e.fp_id(), e.conceptoEgreso_codigo(),
                e.no_documento(), e.valor_egreso(), ACTIVO, usuId);
    }

    public void anularEgreso(int id, String detalle) {
        jdbcTemplate.update("UPDATE encabezado_egresos SET estado = ?, detalle = ? WHERE id = ?", ANULADO, detalle, id);
    }

    private int contar(String sql, Object... parametros) {
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, parametros);
        return total == null ? 0 : total;
    }
}
