
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Perfil;

import java.util.List;

@Repository
public class PerfilDAO {

    private final JdbcTemplate jdbcTemplate;

    public PerfilDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Perfil mapPerfil(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        Perfil perfil = new Perfil();

        perfil.setPerf_id(rs.getInt("perf_id"));
        perfil.setPerf_descripcion(rs.getString("perf_descripcion"));
        perfil.setPerf_estado(rs.getString("perf_estado"));

        return perfil;
    }

    public List<Perfil> getAllPerfiles() {

        String sql = """
            SELECT perf_id, perf_descripcion, perf_estado
            FROM perfil
            ORDER BY perf_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapPerfil);
    }

    public Perfil getPerfilById(int id) {

        String sql = """
            SELECT perf_id, perf_descripcion, perf_estado
            FROM perfil
            WHERE perf_id = ?
            """;

        List<Perfil> perfiles = jdbcTemplate.query(
                sql,
                this::mapPerfil,
                id
        );

        return perfiles.isEmpty() ? null : perfiles.get(0);
    }

    public boolean add(Perfil perfil) {

        String sql = """
            INSERT INTO perfil (perf_descripcion, perf_estado)
            VALUES (?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                perfil.getPerf_descripcion(),
                perfil.getPerf_estado()
        ) > 0;
    }

    public boolean update(Perfil perfil) {

        String sql = """
            UPDATE perfil
            SET perf_descripcion = ?,
                perf_estado = ?
            WHERE perf_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                perfil.getPerf_descripcion(),
                perfil.getPerf_estado(),
                perfil.getPerf_id()
        ) > 0;
    }
}
