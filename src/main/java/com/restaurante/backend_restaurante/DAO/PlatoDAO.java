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
            SELECT p.pla_id, p.pla_descripcion, p.pla_precio, p.est_id,
                   p.idCategoria, c.nombre AS nombre_categoria,
                   e.est_descripcion AS nombre_estado,
                   (LOWER(t.tes_descripcion) = ?) AS estado_valido
            FROM plato p
            LEFT JOIN categoria c ON c.idCategoria = p.idCategoria
            LEFT JOIN estado e ON e.est_id = p.est_id
            LEFT JOIN tipo_estado t ON t.tes_id = e.tes_id
            ORDER BY p.pla_descripcion
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Plato plato = new Plato();
            plato.setIdPlato(rs.getInt("pla_id"));
            plato.setIdEstado(rs.getInt("est_id"));
            plato.setIdCategoria(rs.getObject("idCategoria", Integer.class));
            plato.setDescripcion(rs.getString("pla_descripcion"));
            plato.setPrecio(rs.getFloat("pla_precio"));
            plato.setNombreCategoria(rs.getString("nombre_categoria"));
            plato.setNombreEstado(rs.getString("nombre_estado"));
            plato.setEstadoValido(rs.getBoolean("estado_valido"));
            return plato;
        }, TIPO_ESTADO_PLATO);
    }

    /** Descripción del tipo de estado que agrupa los estados de los platos (tabla tipo_estado). */
    public static final String TIPO_ESTADO_PLATO = "plato";

    /** Indica si el estado existe, está activo y pertenece al tipo "Plato". */
    public boolean esEstadoDePlato(int idEstado) {
        Integer total = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM estado e
            JOIN tipo_estado t ON t.tes_id = e.tes_id
            WHERE e.est_id = ?
              AND LOWER(t.tes_descripcion) = ?
              AND LOWER(e.est_estado) = 'activo'
            """,
            Integer.class,
            idEstado, TIPO_ESTADO_PLATO
        );

        return total != null && total > 0;
    }

    public boolean existeCategoria(int idCategoria) {
        Integer total = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM categoria WHERE idCategoria = ?",
            Integer.class,
            idCategoria
        );

        return total != null && total > 0;
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
            INSERT INTO plato (pla_descripcion, idCategoria, pla_precio, est_id)
            VALUES (?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
            sql,
            plato.getDescripcion(),
            plato.getIdCategoria(),
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
            SET pla_descripcion = ?, idCategoria = ?, pla_precio = ?, est_id = ?
            WHERE pla_id = ?
            """;

        return jdbcTemplate.update(
            sql,
            plato.getDescripcion(),
            plato.getIdCategoria(),
            plato.getPrecio(),
            plato.getIdEstado(),
            plato.getIdPlato()
        ) > 0;
    }
}
