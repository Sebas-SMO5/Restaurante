package com.restaurante.backend_restaurante.DAO;

import com.restaurante.backend_restaurante.Config.EstadosConfig;
import com.restaurante.backend_restaurante.Model.PedidoVista;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Consultas del flujo de pedidos: listado con totales, detalle, cocina
 * y las operaciones de escritura que usa PedidoService.
 */
@Repository
public class PedidoGestionDAO {

    private final JdbcTemplate jdbcTemplate;
    private final EstadosConfig estados;

    public PedidoGestionDAO(JdbcTemplate jdbcTemplate, EstadosConfig estados) {
        this.jdbcTemplate = jdbcTemplate;
        this.estados = estados;
    }

    // Total e ítems sin contar los platos cancelados.
    private static final String SELECT_RESUMEN = """
        SELECT p.ped_id, p.ped_fecha, p.ped_mesa, p.usu_id, p.est_id,
               CONCAT_WS(' ', u.usu_nombre, u.usu_apellido) AS mesero,
               e.est_descripcion AS estado,
               COALESCE(SUM(CASE WHEN d.est_id <> ? THEN d.ped_det_cant END), 0) AS items,
               COALESCE(SUM(CASE WHEN d.est_id = ? THEN 1 END), 0) AS pendientes,
               COALESCE(SUM(CASE WHEN d.est_id <> ? THEN d.ped_det_cant * d.ped_det_precio END), 0) AS total
        FROM pedido p
        LEFT JOIN usuario u ON u.usu_id = p.usu_id
        LEFT JOIN estado e ON e.est_id = p.est_id
        LEFT JOIN pedido_detalle d ON d.ped_id = p.ped_id
        """;

    private static final String GROUP_RESUMEN = """
        GROUP BY p.ped_id, p.ped_fecha, p.ped_mesa, p.usu_id, p.est_id,
                 u.usu_nombre, u.usu_apellido, e.est_descripcion
        """;

    private PedidoVista.Resumen mapResumen(ResultSet rs, int rowNum) throws SQLException {
        return new PedidoVista.Resumen(
                rs.getInt("ped_id"),
                rs.getTimestamp("ped_fecha").toLocalDateTime(),
                rs.getString("ped_mesa"),
                rs.getObject("usu_id", Integer.class),
                rs.getString("mesero"),
                rs.getInt("est_id"),
                rs.getString("estado"),
                rs.getInt("items"),
                rs.getInt("pendientes"),
                rs.getDouble("total"));
    }

    private Object[] parametrosResumen(Object... extra) {
        List<Object> parametros = new ArrayList<>(List.of(
                estados.detalleCancelado, estados.detallePreparacion, estados.detalleCancelado));
        parametros.addAll(List.of(extra));
        return parametros.toArray();
    }

    /** Pedidos ordenados del más reciente al más antiguo; estado null = todos. */
    public List<PedidoVista.Resumen> listar(Integer estado) {
        String filtro = estado == null ? "" : " WHERE p.est_id = ? ";
        Object[] parametros = estado == null ? parametrosResumen() : parametrosResumen(estado);

        return jdbcTemplate.query(
                SELECT_RESUMEN + filtro + GROUP_RESUMEN + " ORDER BY p.ped_fecha DESC, p.ped_id DESC",
                this::mapResumen,
                parametros);
    }

    public PedidoVista.Resumen obtener(int pedId) {
        List<PedidoVista.Resumen> resultado = jdbcTemplate.query(
                SELECT_RESUMEN + " WHERE p.ped_id = ? " + GROUP_RESUMEN,
                this::mapResumen,
                parametrosResumen(pedId));

        return resultado.isEmpty() ? null : resultado.get(0);
    }

    public List<PedidoVista.Item> items(int pedId) {
        return jdbcTemplate.query(
                """
                SELECT d.ped_det_id, d.ped_id, d.pla_id, pl.pla_descripcion AS plato,
                       d.ped_det_cant, d.ped_det_precio, d.ped_det_obser, d.est_id,
                       e.est_descripcion AS estado
                FROM pedido_detalle d
                JOIN plato pl ON pl.pla_id = d.pla_id
                LEFT JOIN estado e ON e.est_id = d.est_id
                WHERE d.ped_id = ?
                ORDER BY d.ped_det_id
                """,
                (rs, i) -> new PedidoVista.Item(
                        rs.getInt("ped_det_id"),
                        rs.getInt("ped_id"),
                        rs.getInt("pla_id"),
                        rs.getString("plato"),
                        rs.getInt("ped_det_cant"),
                        rs.getDouble("ped_det_precio"),
                        rs.getInt("ped_det_cant") * rs.getDouble("ped_det_precio"),
                        rs.getString("ped_det_obser"),
                        rs.getInt("est_id"),
                        rs.getString("estado")),
                pedId);
    }

    /** Platos en preparación de pedidos abiertos, del más antiguo al más reciente. */
    public List<PedidoVista.Comanda> comandasPendientes() {
        return jdbcTemplate.query(
                """
                SELECT d.ped_det_id, p.ped_id, p.ped_mesa, p.ped_fecha,
                       CONCAT_WS(' ', u.usu_nombre, u.usu_apellido) AS mesero,
                       pl.pla_descripcion AS plato, d.ped_det_cant, d.ped_det_obser
                FROM pedido_detalle d
                JOIN pedido p ON p.ped_id = d.ped_id
                JOIN plato pl ON pl.pla_id = d.pla_id
                LEFT JOIN usuario u ON u.usu_id = p.usu_id
                WHERE d.est_id = ? AND p.est_id = ?
                ORDER BY p.ped_fecha, p.ped_id, d.ped_det_id
                """,
                (rs, i) -> new PedidoVista.Comanda(
                        rs.getInt("ped_det_id"),
                        rs.getInt("ped_id"),
                        rs.getString("ped_mesa"),
                        rs.getTimestamp("ped_fecha").toLocalDateTime(),
                        rs.getString("mesero"),
                        rs.getString("plato"),
                        rs.getInt("ped_det_cant"),
                        rs.getString("ped_det_obser")),
                estados.detallePreparacion, estados.pedidoCreado);
    }

    /** Pedido abierto (Creado) de una mesa, o null si la mesa está libre. */
    public Integer pedidoAbiertoDeMesa(String mesa) {
        List<Integer> ids = jdbcTemplate.queryForList(
                "SELECT ped_id FROM pedido WHERE ped_mesa = ? AND est_id = ? ORDER BY ped_id DESC LIMIT 1",
                Integer.class, mesa, estados.pedidoCreado);

        return ids.isEmpty() ? null : ids.get(0);
    }

    /** Precio actual del plato si está Disponible; null si no se puede vender. */
    public Double precioPlatoDisponible(int plaId) {
        List<Double> precios = jdbcTemplate.queryForList(
                "SELECT pla_precio FROM plato WHERE pla_id = ? AND est_id = ?",
                Double.class, plaId, estados.platoDisponible);

        return precios.isEmpty() ? null : precios.get(0);
    }

    public Integer estadoPedido(int pedId) {
        List<Integer> estado = jdbcTemplate.queryForList(
                "SELECT est_id FROM pedido WHERE ped_id = ?", Integer.class, pedId);

        return estado.isEmpty() ? null : estado.get(0);
    }

    /** Devuelve {ped_id, est_id} del plato del pedido, o null si no existe. */
    public int[] datosItem(int pedDetId) {
        List<int[]> datos = jdbcTemplate.query(
                "SELECT ped_id, est_id FROM pedido_detalle WHERE ped_det_id = ?",
                (rs, i) -> new int[] { rs.getInt("ped_id"), rs.getInt("est_id") },
                pedDetId);

        return datos.isEmpty() ? null : datos.get(0);
    }

    public int contarItemsEnEstado(int pedId, int estado) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pedido_detalle WHERE ped_id = ? AND est_id = ?",
                Integer.class, pedId, estado);

        return total == null ? 0 : total;
    }

    public int insertarPedido(String mesa, int usuId) {
        KeyHolder llave = new GeneratedKeyHolder();

        jdbcTemplate.update(conexion -> {
            PreparedStatement ps = conexion.prepareStatement(
                    "INSERT INTO pedido (ped_fecha, usu_id, ped_mesa, est_id) VALUES (NOW(), ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, usuId);
            ps.setString(2, mesa);
            ps.setInt(3, estados.pedidoCreado);
            return ps;
        }, llave);

        return llave.getKey().intValue();
    }

    public void insertarItem(int pedId, int plaId, int cantidad, double precio, String observacion) {
        jdbcTemplate.update(
                """
                INSERT INTO pedido_detalle (ped_det_cant, ped_det_precio, ped_det_obser, pla_id, ped_id, est_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """,
                cantidad, precio, observacion, plaId, pedId, estados.detallePreparacion);
    }

    public void cambiarEstadoPedido(int pedId, int estado) {
        jdbcTemplate.update("UPDATE pedido SET est_id = ? WHERE ped_id = ?", estado, pedId);
    }

    public void cambiarEstadoItem(int pedDetId, int estado) {
        jdbcTemplate.update("UPDATE pedido_detalle SET est_id = ? WHERE ped_det_id = ?", estado, pedDetId);
    }

    /** Cambia todos los platos de un pedido que están en un estado a otro estado. */
    public void cambiarEstadoItems(int pedId, int estadoActual, int estadoNuevo) {
        jdbcTemplate.update(
                "UPDATE pedido_detalle SET est_id = ? WHERE ped_id = ? AND est_id = ?",
                estadoNuevo, pedId, estadoActual);
    }
}
