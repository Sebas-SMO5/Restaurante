
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Comanda;

import java.util.List;

@Repository
public class ComandaDAO {

    private final JdbcTemplate jdbcTemplate;

    public ComandaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Comanda mapComanda(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        Comanda comanda = new Comanda();

        comanda.setCom_id(rs.getInt("com_id"));
        comanda.setMesa(rs.getObject("mesa", Integer.class));
        comanda.setPla_id(rs.getObject("pla_id", Integer.class));
        comanda.setCom_obs(rs.getString("com_obs"));
        comanda.setEst_id(rs.getObject("est_id", Integer.class));

        return comanda;
    }

    public List<Comanda> getAllComandas() {

        String sql = """
            SELECT com_id, mesa, pla_id, com_obs, est_id
            FROM comanda
            ORDER BY com_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapComanda);
    }

    public Comanda getComandaById(int id) {

        String sql = """
            SELECT com_id, mesa, pla_id, com_obs, est_id
            FROM comanda
            WHERE com_id = ?
            """;

        List<Comanda> comandas = jdbcTemplate.query(
                sql,
                this::mapComanda,
                id
        );

        return comandas.isEmpty() ? null : comandas.get(0);
    }

    public boolean add(Comanda comanda) {

        String sql = """
            INSERT INTO comanda (mesa, pla_id, com_obs, est_id)
            VALUES (?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                comanda.getMesa(),
                comanda.getPla_id(),
                comanda.getCom_obs(),
                comanda.getEst_id()
        ) > 0;
    }

    public boolean update(Comanda comanda) {

        String sql = """
            UPDATE comanda
            SET mesa = ?,
                pla_id = ?,
                com_obs = ?,
                est_id = ?
            WHERE com_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                comanda.getMesa(),
                comanda.getPla_id(),
                comanda.getCom_obs(),
                comanda.getEst_id(),
                comanda.getCom_id()
        ) > 0;
    }
}
