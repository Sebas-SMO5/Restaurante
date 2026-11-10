
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Insumo;

import java.util.List;

@Repository
public class InsumoDAO {

    private final JdbcTemplate jdbcTemplate;

    public InsumoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Insumo mapInsumo(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        Insumo insumo = new Insumo();

        insumo.setIns_id(rs.getLong("ins_id"));
        insumo.setSede_id(rs.getLong("sede_id"));
        insumo.setCins_id(rs.getLong("cins_id"));
        insumo.setPres_id(rs.getLong("pres_id"));
        insumo.setIns_nombre(rs.getString("ins_nombre"));
        insumo.setIns_codigo(rs.getString("ins_codigo"));
        insumo.setIns_codigo_barras(rs.getString("ins_codigo_barras"));
        insumo.setIns_precio_compra(rs.getBigDecimal("ins_precio_compra"));
        insumo.setIns_stock(rs.getBigDecimal("ins_stock"));
        insumo.setIns_stock_min(rs.getBigDecimal("ins_stock_min"));
        insumo.setIns_vendible(rs.getBoolean("ins_vendible"));
        insumo.setIns_imagen(rs.getString("ins_imagen"));
        insumo.setIns_estado(rs.getBoolean("ins_estado"));

        return insumo;
    }

    public List<Insumo> getAllInsumos() {

        String sql = """
            SELECT ins_id, sede_id, cins_id, pres_id, ins_nombre,
                   ins_codigo, ins_codigo_barras, ins_precio_compra,
                   ins_stock, ins_stock_min, ins_vendible,
                   ins_imagen, ins_estado
            FROM insumo
            ORDER BY ins_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapInsumo);
    }

    public Insumo getInsumoById(long id) {

        String sql = """
            SELECT ins_id, sede_id, cins_id, pres_id, ins_nombre,
                   ins_codigo, ins_codigo_barras, ins_precio_compra,
                   ins_stock, ins_stock_min, ins_vendible,
                   ins_imagen, ins_estado
            FROM insumo
            WHERE ins_id = ?
            """;

        List<Insumo> insumos = jdbcTemplate.query(
                sql,
                this::mapInsumo,
                id
        );

        return insumos.isEmpty() ? null : insumos.get(0);
    }

    public boolean add(Insumo insumo) {

        String sql = """
            INSERT INTO insumo (
                ins_id, sede_id, cins_id, pres_id, ins_nombre,
                ins_codigo, ins_codigo_barras, ins_precio_compra,
                ins_stock, ins_stock_min, ins_vendible,
                ins_imagen, ins_estado
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                insumo.getIns_id(),
                insumo.getSede_id(),
                insumo.getCins_id(),
                insumo.getPres_id(),
                insumo.getIns_nombre(),
                insumo.getIns_codigo(),
                insumo.getIns_codigo_barras(),
                insumo.getIns_precio_compra(),
                insumo.getIns_stock(),
                insumo.getIns_stock_min(),
                insumo.getIns_vendible(),
                insumo.getIns_imagen(),
                insumo.getIns_estado()
        ) > 0;
    }

    public boolean update(Insumo insumo) {

        String sql = """
            UPDATE insumo
            SET sede_id = ?,
                cins_id = ?,
                pres_id = ?,
                ins_nombre = ?,
                ins_codigo = ?,
                ins_codigo_barras = ?,
                ins_precio_compra = ?,
                ins_stock = ?,
                ins_stock_min = ?,
                ins_vendible = ?,
                ins_imagen = ?,
                ins_estado = ?
            WHERE ins_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                insumo.getSede_id(),
                insumo.getCins_id(),
                insumo.getPres_id(),
                insumo.getIns_nombre(),
                insumo.getIns_codigo(),
                insumo.getIns_codigo_barras(),
                insumo.getIns_precio_compra(),
                insumo.getIns_stock(),
                insumo.getIns_stock_min(),
                insumo.getIns_vendible(),
                insumo.getIns_imagen(),
                insumo.getIns_estado(),
                insumo.getIns_id()
        ) > 0;
    }
}
