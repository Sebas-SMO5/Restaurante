
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Pqrs;

import java.util.List;

@Repository
public class PqrsDAO {

    private final JdbcTemplate jdbcTemplate;

    public PqrsDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Pqrs mapPqrs(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        Pqrs pqrs = new Pqrs();

        pqrs.setPqrs_id(rs.getInt("pqrs_id"));
        pqrs.setPqrs_fecha(rs.getDate("pqrs_fecha"));
        pqrs.setPqrs_descripcion(rs.getBytes("pqrs_descripcion"));
        pqrs.setPqrs_correo(rs.getString("pqrs_correo"));
        pqrs.setPqrs_telefono(rs.getString("pqrs_telefono"));
        pqrs.setTpqrs_id(rs.getInt("tpqrs_id"));
        pqrs.setEst_id(rs.getInt("est_id"));
        pqrs.setPqrs_respuesta(rs.getBytes("pqrs_respuesta"));

        return pqrs;
    }

    public List<Pqrs> getAllPqrs() {

        String sql = """
            SELECT pqrs_id, pqrs_fecha, pqrs_descripcion,
                   pqrs_correo, pqrs_telefono, tpqrs_id,
                   est_id, pqrs_respuesta
            FROM pqrs
            ORDER BY pqrs_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapPqrs);
    }

    public Pqrs getPqrsById(int id) {

        String sql = """
            SELECT pqrs_id, pqrs_fecha, pqrs_descripcion,
                   pqrs_correo, pqrs_telefono, tpqrs_id,
                   est_id, pqrs_respuesta
            FROM pqrs
            WHERE pqrs_id = ?
            """;

        List<Pqrs> pqrsList = jdbcTemplate.query(
                sql,
                this::mapPqrs,
                id
        );

        return pqrsList.isEmpty() ? null : pqrsList.get(0);
    }

    public boolean add(Pqrs pqrs) {

        String sql = """
            INSERT INTO pqrs (
                pqrs_fecha, pqrs_descripcion, pqrs_correo,
                pqrs_telefono, tpqrs_id, est_id, pqrs_respuesta
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                pqrs.getPqrs_fecha(),
                pqrs.getPqrs_descripcion(),
                pqrs.getPqrs_correo(),
                pqrs.getPqrs_telefono(),
                pqrs.getTpqrs_id(),
                pqrs.getEst_id(),
                pqrs.getPqrs_respuesta()
        ) > 0;
    }

    public boolean update(Pqrs pqrs) {

        String sql = """
            UPDATE pqrs
            SET pqrs_fecha = ?,
                pqrs_descripcion = ?,
                pqrs_correo = ?,
                pqrs_telefono = ?,
                tpqrs_id = ?,
                est_id = ?,
                pqrs_respuesta = ?
            WHERE pqrs_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                pqrs.getPqrs_fecha(),
                pqrs.getPqrs_descripcion(),
                pqrs.getPqrs_correo(),
                pqrs.getPqrs_telefono(),
                pqrs.getTpqrs_id(),
                pqrs.getEst_id(),
                pqrs.getPqrs_respuesta(),
                pqrs.getPqrs_id()
        ) > 0;
    }
}
