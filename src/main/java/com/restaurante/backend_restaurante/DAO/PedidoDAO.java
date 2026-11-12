
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Pedido;

import java.util.List;

@Repository
public class PedidoDAO {

    private final JdbcTemplate jdbcTemplate;

    public PedidoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Pedido mapPedido(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        Pedido pedido = new Pedido();

        pedido.setPed_id(rs.getInt("ped_id"));
        pedido.setPed_fecha(rs.getDate("ped_fecha"));
        pedido.setUsu_id(rs.getObject("usu_id", Integer.class));
        pedido.setPed_mesa(rs.getString("ped_mesa"));
        pedido.setEst_id(rs.getObject("est_id", Integer.class));

        return pedido;
    }

    public List<Pedido> getAllPedidos() {

        String sql = """
            SELECT ped_id, ped_fecha, usu_id, ped_mesa, est_id
            FROM pedido
            ORDER BY ped_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapPedido);
    }

    public Pedido getPedidoById(int id) {

        String sql = """
            SELECT ped_id, ped_fecha, usu_id, ped_mesa, est_id
            FROM pedido
            WHERE ped_id = ?
            """;

        List<Pedido> pedidos = jdbcTemplate.query(
                sql,
                this::mapPedido,
                id
        );

        return pedidos.isEmpty() ? null : pedidos.get(0);
    }

    public boolean add(Pedido pedido) {

        String sql = """
            INSERT INTO pedido (ped_fecha, usu_id, ped_mesa, est_id)
            VALUES (?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                pedido.getPed_fecha(),
                pedido.getUsu_id(),
                pedido.getPed_mesa(),
                pedido.getEst_id()
        ) > 0;
    }

    public boolean update(Pedido pedido) {

        String sql = """
            UPDATE pedido
            SET ped_fecha = ?,
                usu_id = ?,
                ped_mesa = ?,
                est_id = ?
            WHERE ped_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                pedido.getPed_fecha(),
                pedido.getUsu_id(),
                pedido.getPed_mesa(),
                pedido.getEst_id(),
                pedido.getPed_id()
        ) > 0;
    }
}
