package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Estado;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EstadoDAO {

    private static final String SELECT_BASE = """
        SELECT e.est_id, e.est_descripcion, e.tes_id, e.est_estado, t.tes_descripcion
        FROM estado e
        JOIN tipo_estado t ON t.tes_id = e.tes_id
        """;

    private final JdbcTemplate jdbcTemplate;

    public EstadoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Estado mapEstado(ResultSet rs, int rowNum) throws SQLException {

        Estado estado = new Estado();

        estado.setEst_id(rs.getInt("est_id"));
        estado.setEst_descripcion(rs.getString("est_descripcion"));
        estado.setTes_id(rs.getInt("tes_id"));
        estado.setEst_estado(rs.getString("est_estado"));
        estado.setTes_descripcion(rs.getString("tes_descripcion"));

        return estado;
    }

    public List<Estado> getAllEstados() {
        return jdbcTemplate.query(SELECT_BASE + " ORDER BY t.tes_descripcion, e.est_id", this::mapEstado);
    }

    /** Estados de un tipo concreto (por ejemplo, solo los de Plato). */
    public List<Estado> getEstadosPorTipo(int tesId) {
        return jdbcTemplate.query(
                SELECT_BASE + " WHERE e.tes_id = ? ORDER BY e.est_id",
                this::mapEstado,
                tesId);
    }

    public Estado getEstadoById(int id) {

        List<Estado> estados = jdbcTemplate.query(
                SELECT_BASE + " WHERE e.est_id = ?",
                this::mapEstado,
                id
        );

        return estados.isEmpty() ? null : estados.get(0);
    }

    public boolean existeTipo(int tesId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tipo_estado WHERE tes_id = ?", Integer.class, tesId);
        return total != null && total > 0;
    }

    /** Indica si ya existe otro estado con la misma descripción dentro del mismo tipo. */
    public boolean existeDescripcion(int tesId, String descripcion, int idExcluir) {
        Integer total = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*) FROM estado
                WHERE tes_id = ? AND LOWER(TRIM(est_descripcion)) = LOWER(TRIM(?)) AND est_id <> ?
                """,
                Integer.class, tesId, descripcion, idExcluir);
        return total != null && total > 0;
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
