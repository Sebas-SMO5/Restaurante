
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.EncabezadoEgresos;

import java.util.List;

@Repository
public class EncabezadoEgresosDAO {

    private final JdbcTemplate jdbcTemplate;

    public EncabezadoEgresosDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private EncabezadoEgresos mapEncabezadoEgreso(
            java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        EncabezadoEgresos egreso = new EncabezadoEgresos();

        egreso.setId(rs.getInt("id"));
        egreso.setNo_egreso(rs.getInt("no_egreso"));
        egreso.setFecha_documento(rs.getTimestamp("fecha_documento"));
        egreso.setTercero_identificacion(rs.getString("tercero_identificacion"));
        egreso.setDetalle(rs.getString("detalle"));
        egreso.setFp_id(rs.getInt("fp_id"));
        egreso.setConceptoEgreso_codigo(rs.getInt("conceptoEgreso_codigo"));
        egreso.setNo_documento(rs.getString("no_documento"));
        egreso.setValor_egreso(rs.getDouble("valor_egreso"));
        egreso.setEstado(rs.getString("estado"));

        return egreso;
    }

    public List<EncabezadoEgresos> getAllEncabezadosEgresos() {

        String sql = """
            SELECT id, no_egreso, fecha_documento,
                   tercero_identificacion, detalle, fp_id,
                   conceptoEgreso_codigo, no_documento,
                   valor_egreso, estado
            FROM encabezado_egresos
            ORDER BY id DESC
            """;

        return jdbcTemplate.query(sql, this::mapEncabezadoEgreso);
    }

    public EncabezadoEgresos getEncabezadoEgresoById(int id) {

        String sql = """
            SELECT id, no_egreso, fecha_documento,
                   tercero_identificacion, detalle, fp_id,
                   conceptoEgreso_codigo, no_documento,
                   valor_egreso, estado
            FROM encabezado_egresos
            WHERE id = ?
            """;

        List<EncabezadoEgresos> egresos = jdbcTemplate.query(
                sql,
                this::mapEncabezadoEgreso,
                id
        );

        return egresos.isEmpty() ? null : egresos.get(0);
    }

    public boolean add(EncabezadoEgresos egreso) {

        String sql = """
            INSERT INTO encabezado_egresos (
                id, no_egreso, fecha_documento,
                tercero_identificacion, detalle, fp_id,
                conceptoEgreso_codigo, no_documento,
                valor_egreso, estado
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                egreso.getId(),
                egreso.getNo_egreso(),
                egreso.getFecha_documento(),
                egreso.getTercero_identificacion(),
                egreso.getDetalle(),
                egreso.getFp_id(),
                egreso.getConceptoEgreso_codigo(),
                egreso.getNo_documento(),
                egreso.getValor_egreso(),
                egreso.getEstado()
        ) > 0;
    }

    public boolean update(EncabezadoEgresos egreso) {

        String sql = """
            UPDATE encabezado_egresos
            SET no_egreso = ?,
                fecha_documento = ?,
                tercero_identificacion = ?,
                detalle = ?,
                fp_id = ?,
                conceptoEgreso_codigo = ?,
                no_documento = ?,
                valor_egreso = ?,
                estado = ?
            WHERE id = ?
            """;

        return jdbcTemplate.update(
                sql,
                egreso.getNo_egreso(),
                egreso.getFecha_documento(),
                egreso.getTercero_identificacion(),
                egreso.getDetalle(),
                egreso.getFp_id(),
                egreso.getConceptoEgreso_codigo(),
                egreso.getNo_documento(),
                egreso.getValor_egreso(),
                egreso.getEstado(),
                egreso.getId()
        ) > 0;
    }
}
