
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.TipoTpqrs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TipoTpqrsDAO {

    private final JdbcTemplate jdbcTemplate;

    public TipoTpqrsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private TipoTpqrs mapTipoTpqrs(ResultSet rs, int rowNum) throws SQLException {
        TipoTpqrs tipoTpqrs = new TipoTpqrs();

        tipoTpqrs.setTpqrs_id(rs.getInt("tpqrs_id"));
        tipoTpqrs.setTpqrs_descripcion(rs.getString("tpqrs_descripcion"));
        tipoTpqrs.setTpqrs_estado(rs.getString("tpqrs_estado"));

        return tipoTpqrs;
    }

    public List<TipoTpqrs> getAllTiposTpqrs() {
        String sql = """
            SELECT tpqrs_id, tpqrs_descripcion, tpqrs_estado
            FROM tipo_tpqrs
            ORDER BY tpqrs_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapTipoTpqrs);
    }

    public TipoTpqrs getTipoTpqrsById(int id) {
        String sql = """
            SELECT tpqrs_id, tpqrs_descripcion, tpqrs_estado
            FROM tipo_tpqrs
            WHERE tpqrs_id = ?
            """;

        List<TipoTpqrs> resultados = jdbcTemplate.query(
            sql,
            this::mapTipoTpqrs,
            id
        );

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public boolean add(TipoTpqrs tipoTpqrs) {
        String sql = """
            INSERT INTO tipo_tpqrs (tpqrs_descripcion, tpqrs_estado)
            VALUES (?, ?)
            """;

        return jdbcTemplate.update(
            sql,
            tipoTpqrs.getTpqrs_descripcion(),
            tipoTpqrs.getTpqrs_estado()
        ) > 0;
    }

    public boolean update(TipoTpqrs tipoTpqrs) {
        String sql = """
            UPDATE tipo_tpqrs
            SET tpqrs_descripcion = ?,
                tpqrs_estado = ?
            WHERE tpqrs_id = ?
            """;

        return jdbcTemplate.update(
            sql,
            tipoTpqrs.getTpqrs_descripcion(),
            tipoTpqrs.getTpqrs_estado(),
            tipoTpqrs.getTpqrs_id()
        ) > 0;
    }
}
