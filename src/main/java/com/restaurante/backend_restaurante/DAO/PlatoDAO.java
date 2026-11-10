
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Plato;

import java.util.List;

@Repository
public class PlatoDAO {

    private final JdbcTemplate jdbcTemplate;

    public PlatoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Plato> getAllPlatos() {
        String sql = """
            SELECT pla_id, pla_descripcion, pla_precio, est_id
            FROM plato
            ORDER BY pla_descripcion DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
            new Plato(
                rs.getInt("pla_id"),
                rs.getInt("est_id"),
                rs.getString("pla_descripcion"),
                rs.getFloat("pla_precio")
            )
        );
    }

    public boolean add(Plato plato) {
        String validar = """
            SELECT COUNT(*)
            FROM plato
            WHERE TRIM(pla_descripcion) = TRIM(?)
            """;

        Integer cantidad = jdbcTemplate.queryForObject(
            validar,
            Integer.class,
            plato.getDescripcion()
        );

        if (cantidad != null && cantidad > 0) {
            return false;
        }

        String sql = """
            INSERT INTO plato (pla_descripcion, pla_precio, est_id)
            VALUES (?, ?, ?)
            """;

        return jdbcTemplate.update(
            sql,
            plato.getDescripcion(),
            plato.getPrecio(),
            plato.getIdEstado()
        ) > 0;
    }

    public boolean update(Plato plato) {
        String validar = """
            SELECT COUNT(*)
            FROM plato
            WHERE TRIM(pla_descripcion) = TRIM(?)
              AND pla_id <> ?
            """;

        Integer cantidad = jdbcTemplate.queryForObject(
            validar,
            Integer.class,
            plato.getDescripcion(),
            plato.getIdPlato()
        );

        if (cantidad != null && cantidad > 0) {
            return false;
        }

        String sql = """
            UPDATE plato
            SET pla_descripcion = ?, pla_precio = ?, est_id = ?
            WHERE pla_id = ?
            """;

        return jdbcTemplate.update(
            sql,
            plato.getDescripcion(),
            plato.getPrecio(),
            plato.getIdEstado(),
            plato.getIdPlato()
        ) > 0;
    }
}
