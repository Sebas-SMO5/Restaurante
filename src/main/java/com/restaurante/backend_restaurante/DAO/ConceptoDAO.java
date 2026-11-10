
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Concepto;

import java.util.List;

@Repository
public class ConceptoDAO {

    private final JdbcTemplate jdbcTemplate;

    public ConceptoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Concepto mapConcepto(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        return new Concepto(
                rs.getInt("con_id"),
                rs.getString("con_descripcion"),
                rs.getString("con_estado")
        );
    }

    public List<Concepto> getAllConceptos() {

        String sql = """
            SELECT con_id, con_descripcion, con_estado
            FROM concepto
            ORDER BY con_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapConcepto);
    }

    public Concepto getConceptoById(int id) {

        String sql = """
            SELECT con_id, con_descripcion, con_estado
            FROM concepto
            WHERE con_id = ?
            """;

        List<Concepto> conceptos = jdbcTemplate.query(
                sql,
                this::mapConcepto,
                id
        );

        return conceptos.isEmpty() ? null : conceptos.get(0);
    }

    public boolean add(Concepto concepto) {

        String sql = """
            INSERT INTO concepto (con_id, con_descripcion, con_estado)
            VALUES (?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                concepto.getCon_id(),
                concepto.getCon_descripcion(),
                concepto.getCon_estado()
        ) > 0;
    }

    public boolean update(Concepto concepto) {

        String sql = """
            UPDATE concepto
            SET con_descripcion = ?,
                con_estado = ?
            WHERE con_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                concepto.getCon_descripcion(),
                concepto.getCon_estado(),
                concepto.getCon_id()
        ) > 0;
    }
}
