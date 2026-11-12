
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.ReciboCaja;

import java.util.List;

@Repository
public class ReciboCajaDAO {

    private final JdbcTemplate jdbcTemplate;

    public ReciboCajaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private ReciboCaja mapReciboCaja(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        ReciboCaja recibo = new ReciboCaja();

        recibo.setRc_num(rs.getInt("rc_num"));
        recibo.setUsu_id(rs.getInt("usu_id"));
        recibo.setRc_fecha(rs.getDate("rc_fecha"));
        recibo.setPed_id(rs.getInt("ped_id"));
        recibo.setCli_id(rs.getInt("cli_id"));
        recibo.setRc_total(rs.getDouble("rc_total"));
        recibo.setRc_observacion(rs.getString("rc_observacion"));
        recibo.setRc_estado(rs.getString("rc_estado"));

        return recibo;
    }

    public List<ReciboCaja> getAllRecibosCaja() {

        String sql = """
            SELECT rc_num, usu_id, rc_fecha, ped_id, cli_id,
                   rc_total, rc_observacion, rc_estado
            FROM recibo_caja
            ORDER BY rc_num DESC
            """;

        return jdbcTemplate.query(sql, this::mapReciboCaja);
    }

    public ReciboCaja getReciboCajaById(int id) {

        String sql = """
            SELECT rc_num, usu_id, rc_fecha, ped_id, cli_id,
                   rc_total, rc_observacion, rc_estado
            FROM recibo_caja
            WHERE rc_num = ?
            """;

        List<ReciboCaja> recibos = jdbcTemplate.query(
                sql,
                this::mapReciboCaja,
                id
        );

        return recibos.isEmpty() ? null : recibos.get(0);
    }

    public boolean add(ReciboCaja recibo) {

        String sql = """
            INSERT INTO recibo_caja (
                usu_id, rc_fecha, ped_id, cli_id,
                rc_total, rc_observacion, rc_estado
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                recibo.getUsu_id(),
                recibo.getRc_fecha(),
                recibo.getPed_id(),
                recibo.getCli_id(),
                recibo.getRc_total(),
                recibo.getRc_observacion(),
                recibo.getRc_estado()
        ) > 0;
    }

    public boolean update(ReciboCaja recibo) {

        String sql = """
            UPDATE recibo_caja
            SET usu_id = ?,
                rc_fecha = ?,
                ped_id = ?,
                cli_id = ?,
                rc_total = ?,
                rc_observacion = ?,
                rc_estado = ?
            WHERE rc_num = ?
            """;

        return jdbcTemplate.update(
                sql,
                recibo.getUsu_id(),
                recibo.getRc_fecha(),
                recibo.getPed_id(),
                recibo.getCli_id(),
                recibo.getRc_total(),
                recibo.getRc_observacion(),
                recibo.getRc_estado(),
                recibo.getRc_num()
        ) > 0;
    }
}
