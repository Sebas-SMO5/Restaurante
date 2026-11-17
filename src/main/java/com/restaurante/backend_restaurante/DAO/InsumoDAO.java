package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Insumo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class InsumoDAO {

    private static final String SELECT_BASE = """
        SELECT i.ins_id, i.cins_id, i.um_id, i.ins_nombre,
               i.ins_codigo, i.ins_codigo_barras, i.ins_precio_compra,
               i.ins_stock, i.ins_stock_min, i.ins_vendible,
               i.ins_imagen, i.ins_estado,
               c.cins_descripcion, u.um_descripcion, u.um_abreviatura
        FROM insumo i
        JOIN categoria_insumo c ON c.cins_id = i.cins_id
        JOIN unidad_medida u ON u.um_id = i.um_id
        """;

    private final JdbcTemplate jdbcTemplate;

    public InsumoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Insumo mapInsumo(ResultSet rs, int rowNum) throws SQLException {

        Insumo insumo = new Insumo();

        insumo.setIns_id(rs.getLong("ins_id"));
        insumo.setCins_id(rs.getInt("cins_id"));
        insumo.setUm_id(rs.getInt("um_id"));
        insumo.setIns_nombre(rs.getString("ins_nombre"));
        insumo.setIns_codigo(rs.getString("ins_codigo"));
        insumo.setIns_codigo_barras(rs.getString("ins_codigo_barras"));
        insumo.setIns_precio_compra(rs.getBigDecimal("ins_precio_compra"));
        insumo.setIns_stock(rs.getBigDecimal("ins_stock"));
        insumo.setIns_stock_min(rs.getBigDecimal("ins_stock_min"));
        insumo.setIns_vendible(rs.getBoolean("ins_vendible"));
        insumo.setIns_imagen(rs.getString("ins_imagen"));
        insumo.setIns_estado(rs.getBoolean("ins_estado"));
        insumo.setCins_descripcion(rs.getString("cins_descripcion"));
        insumo.setUm_descripcion(rs.getString("um_descripcion"));
        insumo.setUm_abreviatura(rs.getString("um_abreviatura"));

        return insumo;
    }

    public List<Insumo> getAllInsumos() {
        return jdbcTemplate.query(SELECT_BASE + " ORDER BY i.ins_nombre", this::mapInsumo);
    }

    public Insumo getInsumoById(long id) {

        List<Insumo> insumos = jdbcTemplate.query(
                SELECT_BASE + " WHERE i.ins_id = ?",
                this::mapInsumo,
                id
        );

        return insumos.isEmpty() ? null : insumos.get(0);
    }

    public boolean existeCategoria(int cinsId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categoria_insumo WHERE cins_id = ?", Integer.class, cinsId);
        return total != null && total > 0;
    }

    public boolean existeUnidad(int umId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM unidad_medida WHERE um_id = ?", Integer.class, umId);
        return total != null && total > 0;
    }

    /** Indica si otro insumo ya usa ese nombre (sin distinguir mayúsculas). */
    public boolean existeNombre(String nombre, long idExcluir) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM insumo WHERE LOWER(TRIM(ins_nombre)) = LOWER(TRIM(?)) AND ins_id <> ?",
                Integer.class, nombre, idExcluir);
        return total != null && total > 0;
    }

    public boolean add(Insumo insumo) {

        String sql = """
            INSERT INTO insumo (
                cins_id, um_id, ins_nombre,
                ins_codigo, ins_codigo_barras, ins_precio_compra,
                ins_stock, ins_stock_min, ins_vendible,
                ins_imagen, ins_estado
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                insumo.getCins_id(),
                insumo.getUm_id(),
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
            SET cins_id = ?,
                um_id = ?,
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
                insumo.getCins_id(),
                insumo.getUm_id(),
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
