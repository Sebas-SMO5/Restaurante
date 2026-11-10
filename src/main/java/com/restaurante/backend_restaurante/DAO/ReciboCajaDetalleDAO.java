
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.ReciboCajaDetalle;

import java.util.List;

@Repository
public class ReciboCajaDetalleDAO {

    private final JdbcTemplate jdbcTemplate;

    public ReciboCajaDetalleDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private ReciboCajaDetalle mapReciboCajaDetalle(
            java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        ReciboCajaDetalle detalle = new ReciboCajaDetalle();

        detalle.setRcd_id(rs.getInt("rcd_id"));
        detalle.setRcd_num(rs.getInt("rcd_num"));
        detalle.setPla_id(rs.getInt("pla_id"));
        detalle.setRcd_precio(rs.getDouble("rcd_precio"));
        detalle.setRcd_cantidad(rs.getDouble("rcd_cantidad"));

        return detalle;
    }

    public List<ReciboCajaDetalle> getAllRecibosCajaDetalle() {

        String sql = """
            SELECT rcd_id, rcd_num, pla_id, rcd_precio, rcd_cantidad
            FROM recibo_caja_detalle
            ORDER BY rcd_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapReciboCajaDetalle);
    }

    public ReciboCajaDetalle getReciboCajaDetalleById(int id) {

        String sql = """
            SELECT rcd_id, rcd_num, pla_id, rcd_precio, rcd_cantidad
            FROM recibo_caja_detalle
            WHERE rcd_id = ?
            """;

        List<ReciboCajaDetalle> detalles = jdbcTemplate.query(
                sql,
                this::mapReciboCajaDetalle,
                id
        );

        return detalles.isEmpty() ? null : detalles.get(0);
    }

    public boolean add(ReciboCajaDetalle detalle) {

        String sql = """
            INSERT INTO recibo_caja_detalle (
                rcd_num, pla_id, rcd_precio, rcd_cantidad
            )
            VALUES (?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                detalle.getRcd_num(),
                detalle.getPla_id(),
                detalle.getRcd_precio(),
                detalle.getRcd_cantidad()
        ) > 0;
    }

    public boolean update(ReciboCajaDetalle detalle) {

        String sql = """
            UPDATE recibo_caja_detalle
            SET rcd_num = ?,
                pla_id = ?,
                rcd_precio = ?,
                rcd_cantidad = ?
            WHERE rcd_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                detalle.getRcd_num(),
                detalle.getPla_id(),
                detalle.getRcd_precio(),
                detalle.getRcd_cantidad(),
                detalle.getRcd_id()
        ) > 0;
    }
}
