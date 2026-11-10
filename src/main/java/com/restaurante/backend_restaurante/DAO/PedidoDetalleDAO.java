
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.PedidoDetalle;

import java.util.List;

@Repository
public class PedidoDetalleDAO {

    private final JdbcTemplate jdbcTemplate;

    public PedidoDetalleDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private PedidoDetalle mapPedidoDetalle(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        PedidoDetalle detalle = new PedidoDetalle();

        detalle.setPed_det_id(rs.getInt("ped_det_id"));
        detalle.setPed_det_cant(rs.getObject("ped_det_cant", Integer.class));
        detalle.setPed_det_precio(rs.getObject("ped_det_precio", Float.class));
        detalle.setPed_det_obser(rs.getString("ped_det_obser"));
        detalle.setPla_id(rs.getObject("pla_id", Integer.class));
        detalle.setPed_id(rs.getObject("ped_id", Integer.class));
        detalle.setEst_id(rs.getInt("est_id"));

        return detalle;
    }

    public List<PedidoDetalle> getAllPedidoDetalles() {

        String sql = """
            SELECT ped_det_id, ped_det_cant, ped_det_precio,
                   ped_det_obser, pla_id, ped_id, est_id
            FROM pedido_detalle
            ORDER BY ped_det_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapPedidoDetalle);
    }

    public PedidoDetalle getPedidoDetalleById(int id) {

        String sql = """
            SELECT ped_det_id, ped_det_cant, ped_det_precio,
                   ped_det_obser, pla_id, ped_id, est_id
            FROM pedido_detalle
            WHERE ped_det_id = ?
            """;

        List<PedidoDetalle> detalles = jdbcTemplate.query(
                sql,
                this::mapPedidoDetalle,
                id
        );

        return detalles.isEmpty() ? null : detalles.get(0);
    }

    public boolean add(PedidoDetalle detalle) {

        String sql = """
            INSERT INTO pedido_detalle (
                ped_det_cant, ped_det_precio, ped_det_obser,
                pla_id, ped_id, est_id
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                detalle.getPed_det_cant(),
                detalle.getPed_det_precio(),
                detalle.getPed_det_obser(),
                detalle.getPla_id(),
                detalle.getPed_id(),
                detalle.getEst_id()
        ) > 0;
    }

    public boolean update(PedidoDetalle detalle) {

        String sql = """
            UPDATE pedido_detalle
            SET ped_det_cant = ?,
                ped_det_precio = ?,
                ped_det_obser = ?,
                pla_id = ?,
                ped_id = ?,
                est_id = ?
            WHERE ped_det_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                detalle.getPed_det_cant(),
                detalle.getPed_det_precio(),
                detalle.getPed_det_obser(),
                detalle.getPla_id(),
                detalle.getPed_id(),
                detalle.getEst_id(),
                detalle.getPed_det_id()
        ) > 0;
    }
}
