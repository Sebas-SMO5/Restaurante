
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.TipoEstado;

import java.util.List;

@Repository
public class TipoEstadoDAO {

    private final JdbcTemplate jdbcTemplate;

    public TipoEstadoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private TipoEstado mapTipoEstado(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        TipoEstado tipoEstado = new TipoEstado();

        tipoEstado.setTes_id(rs.getInt("tes_id"));
        tipoEstado.setTes_descripcion(rs.getString("tes_descripcion"));
        tipoEstado.setTes_estado(rs.getString("tes_estado"));

        return tipoEstado;
    }

    public List<TipoEstado> getAllTiposEstado() {

        String sql = """
            SELECT tes_id, tes_descripcion, tes_estado
            FROM tipo_estado
            ORDER BY tes_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapTipoEstado);
    }

    public TipoEstado getTipoEstadoById(int id) {

        String sql = """
            SELECT tes_id, tes_descripcion, tes_estado
            FROM tipo_estado
            WHERE tes_id = ?
            """;

        List<TipoEstado> tiposEstado = jdbcTemplate.query(
                sql,
                this::mapTipoEstado,
                id
        );

        return tiposEstado.isEmpty() ? null : tiposEstado.get(0);
    }

    public boolean add(TipoEstado tipoEstado) {

        String sql = """
            INSERT INTO tipo_estado (tes_descripcion, tes_estado)
            VALUES (?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                tipoEstado.getTes_descripcion(),
                tipoEstado.getTes_estado()
        ) > 0;
    }

    public boolean update(TipoEstado tipoEstado) {

        String sql = """
            UPDATE tipo_estado
            SET tes_descripcion = ?,
                tes_estado = ?
            WHERE tes_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                tipoEstado.getTes_descripcion(),
                tipoEstado.getTes_estado(),
                tipoEstado.getTes_id()
        ) > 0;
    }
}
