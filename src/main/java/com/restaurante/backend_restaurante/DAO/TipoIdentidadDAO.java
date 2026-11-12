
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.TipoIdentidad;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TipoIdentidadDAO {

    private final JdbcTemplate jdbcTemplate;

    public TipoIdentidadDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private TipoIdentidad mapTipoIdentidad(ResultSet rs, int rowNum) throws SQLException {
        TipoIdentidad tipoIdentidad = new TipoIdentidad();

        tipoIdentidad.setTipo_id(rs.getInt("tipo_id"));
        tipoIdentidad.setTipo_descripcion(rs.getString("tipo_descripcion"));
        tipoIdentidad.setTipo_sigla(rs.getString("tipo_sigla"));
        tipoIdentidad.setTipo_estado(rs.getString("tipo_estado"));

        return tipoIdentidad;
    }

    public List<TipoIdentidad> getAllTiposIdentidad() {
        String sql = """
            SELECT tipo_id, tipo_descripcion, tipo_sigla, tipo_estado
            FROM tipo_identidad
            ORDER BY tipo_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapTipoIdentidad);
    }

    public TipoIdentidad getTipoIdentidadById(int id) {
        String sql = """
            SELECT tipo_id, tipo_descripcion, tipo_sigla, tipo_estado
            FROM tipo_identidad
            WHERE tipo_id = ?
            """;

        List<TipoIdentidad> resultados = jdbcTemplate.query(
            sql,
            this::mapTipoIdentidad,
            id
        );

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public boolean add(TipoIdentidad tipoIdentidad) {
        String sql = """
            INSERT INTO tipo_identidad (
                tipo_descripcion, tipo_sigla, tipo_estado
            )
            VALUES (?, ?, ?)
            """;

        return jdbcTemplate.update(
            sql,
            tipoIdentidad.getTipo_descripcion(),
            tipoIdentidad.getTipo_sigla(),
            tipoIdentidad.getTipo_estado()
        ) > 0;
    }

    public boolean update(TipoIdentidad tipoIdentidad) {
        String sql = """
            UPDATE tipo_identidad
            SET tipo_descripcion = ?,
                tipo_sigla = ?,
                tipo_estado = ?
            WHERE tipo_id = ?
            """;

        return jdbcTemplate.update(
            sql,
            tipoIdentidad.getTipo_descripcion(),
            tipoIdentidad.getTipo_sigla(),
            tipoIdentidad.getTipo_estado(),
            tipoIdentidad.getTipo_id()
        ) > 0;
    }
}
