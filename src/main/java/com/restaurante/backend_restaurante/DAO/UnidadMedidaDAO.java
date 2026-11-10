
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.UnidadMedida;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UnidadMedidaDAO {

    private final JdbcTemplate jdbcTemplate;

    public UnidadMedidaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private UnidadMedida mapUnidadMedida(ResultSet rs, int rowNum) throws SQLException {
        UnidadMedida unidad = new UnidadMedida();

        unidad.setUm_id(rs.getInt("um_id"));
        unidad.setUm_descripcion(rs.getString("um_descripcion"));
        unidad.setUm_abreviatura(rs.getString("um_abreviatura"));
        unidad.setUm_estado(rs.getString("um_estado"));

        return unidad;
    }

    public List<UnidadMedida> getAllUnidadesMedida() {
        String sql = """
            SELECT um_id, um_descripcion, um_abreviatura, um_estado
            FROM unidad_medida
            ORDER BY um_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapUnidadMedida);
    }

    public UnidadMedida getUnidadMedidaById(int id) {
        String sql = """
            SELECT um_id, um_descripcion, um_abreviatura, um_estado
            FROM unidad_medida
            WHERE um_id = ?
            """;

        List<UnidadMedida> resultados = jdbcTemplate.query(
            sql,
            this::mapUnidadMedida,
            id
        );

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public boolean add(UnidadMedida unidad) {
        String sql = """
            INSERT INTO unidad_medida (
                um_id, um_descripcion, um_abreviatura, um_estado
            )
            VALUES (?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
            sql,
            unidad.getUm_id(),
            unidad.getUm_descripcion(),
            unidad.getUm_abreviatura(),
            unidad.getUm_estado()
        ) > 0;
    }

    public boolean update(UnidadMedida unidad) {
        String sql = """
            UPDATE unidad_medida
            SET um_descripcion = ?,
                um_abreviatura = ?,
                um_estado = ?
            WHERE um_id = ?
            """;

        return jdbcTemplate.update(
            sql,
            unidad.getUm_descripcion(),
            unidad.getUm_abreviatura(),
            unidad.getUm_estado(),
            unidad.getUm_id()
        ) > 0;
    }
}
