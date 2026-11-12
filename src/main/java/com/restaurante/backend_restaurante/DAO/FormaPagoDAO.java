
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.FormaPago;

import java.util.List;

@Repository
public class FormaPagoDAO {

    private final JdbcTemplate jdbcTemplate;

    public FormaPagoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private FormaPago mapFormaPago(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        return new FormaPago(
                rs.getInt("fp_id"),
                rs.getString("fp_descripcion"),
                rs.getString("fp_estado")
        );
    }

    public List<FormaPago> getAllFormasPago() {

        String sql = """
            SELECT fp_id, fp_descripcion, fp_estado
            FROM forma_pago
            ORDER BY fp_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapFormaPago);
    }

    public FormaPago getFormaPagoById(int id) {

        String sql = """
            SELECT fp_id, fp_descripcion, fp_estado
            FROM forma_pago
            WHERE fp_id = ?
            """;

        List<FormaPago> formasPago = jdbcTemplate.query(
                sql,
                this::mapFormaPago,
                id
        );

        return formasPago.isEmpty() ? null : formasPago.get(0);
    }

    public boolean add(FormaPago formaPago) {

        String sql = """
            INSERT INTO forma_pago (fp_id, fp_descripcion, fp_estado)
            VALUES (?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                formaPago.getFp_id(),
                formaPago.getFp_descripcion(),
                formaPago.getFp_estado()
        ) > 0;
    }

    public boolean update(FormaPago formaPago) {

        String sql = """
            UPDATE forma_pago
            SET fp_descripcion = ?,
                fp_estado = ?
            WHERE fp_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                formaPago.getFp_descripcion(),
                formaPago.getFp_estado(),
                formaPago.getFp_id()
        ) > 0;
    }
}
