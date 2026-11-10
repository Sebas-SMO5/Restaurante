
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Estado;

import java.util.List;

@Repository
public class EstadoDAO {

    private final JdbcTemplate jdbcTemplate;

    public EstadoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Estado mapEstado(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        Estado estado = new Estado();

        estado.setEst_id(rs.getInt("est_id"));
        estado.setEst_descripcion(rs.getString("est_descripcion"));
        estado.setTes_id(rs.getInt("tes_id"));
        estado.setEst_estado(rs.getString("est_estado"));

        return estado;
    }

    public List<Estado> getAllEstados() {

        String sql = """
            SELECT est_id, est_descripcion, tes_id, est_estado
            FROM estado
            ORDER BY est_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapEstado);
    }

    public Estado getEstadoById(int id) {

        String sql = """
            SELECT est_id, est_descripcion, tes_id, est_estado
            FROM estado
            WHERE est_id = ?
            """;

        List<Estado> estados = jdbcTemplate.query(
                sql,
                this::mapEstado,
                id
        );

        return estados.isEmpty() ? null : estados.get(0);
    }

    public boolean add(Estado estado) {

        String sql = """
            INSERT INTO estado (est_descripcion, tes_id, est_estado)
            VALUES (?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                estado.getEst_descripcion(),
                estado.getTes_id(),
                estado.getEst_estado()
        ) > 0;
    }

    public boolean update(Estado estado) {

        String sql = """
            UPDATE estado
            SET est_descripcion = ?,
                tes_id = ?,
                est_estado = ?
            WHERE est_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                estado.getEst_descripcion(),
                estado.getTes_id(),
                estado.getEst_estado(),
                estado.getEst_id()
        ) > 0;
    }
}
