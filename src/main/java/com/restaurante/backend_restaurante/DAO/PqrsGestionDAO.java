package com.restaurante.backend_restaurante.DAO;

import com.restaurante.backend_restaurante.Model.PqrsVista;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PqrsGestionDAO {

    private final JdbcTemplate jdbcTemplate;
    private final int estadoPendiente;
    private final int estadoAtendida;

    public PqrsGestionDAO(
            JdbcTemplate jdbcTemplate,
            @Value("${app.estados.pqrs.pendiente:11}") int estadoPendiente,
            @Value("${app.estados.pqrs.atendida:12}") int estadoAtendida) {
        this.jdbcTemplate = jdbcTemplate;
        this.estadoPendiente = estadoPendiente;
        this.estadoAtendida = estadoAtendida;
    }

    public int estadoPendiente() {
        return estadoPendiente;
    }

    public int estadoAtendida() {
        return estadoAtendida;
    }

    private static final String SELECT_BASE = """
        SELECT q.pqrs_id, q.pqrs_fecha, q.pqrs_nombre, q.pqrs_correo, q.pqrs_telefono,
               q.tpqrs_id, t.tpqrs_descripcion AS tipo, q.pqrs_descripcion,
               q.est_id, e.est_descripcion AS estado, q.pqrs_origen,
               q.pqrs_respuesta, q.pqrs_fecha_respuesta,
               CONCAT_WS(' ', u.usu_nombre, u.usu_apellido) AS respondido_por
        FROM pqrs q
        JOIN tipo_tpqrs t ON t.tpqrs_id = q.tpqrs_id
        JOIN estado e ON e.est_id = q.est_id
        LEFT JOIN usuario u ON u.usu_id = q.usu_id_respuesta
        """;

    private static LocalDateTime fecha(Timestamp valor) {
        return valor == null ? null : valor.toLocalDateTime();
    }

    private PqrsVista.Pqrs mapPqrs(ResultSet rs, int rowNum) throws SQLException {
        return new PqrsVista.Pqrs(
                rs.getInt("pqrs_id"),
                fecha(rs.getTimestamp("pqrs_fecha")),
                rs.getString("pqrs_nombre"),
                rs.getString("pqrs_correo"),
                rs.getString("pqrs_telefono"),
                rs.getInt("tpqrs_id"),
                rs.getString("tipo"),
                rs.getString("pqrs_descripcion"),
                rs.getInt("est_id"),
                rs.getString("estado"),
                rs.getString("pqrs_origen"),
                rs.getString("pqrs_respuesta"),
                fecha(rs.getTimestamp("pqrs_fecha_respuesta")),
                rs.getString("respondido_por"));
    }

    public List<PqrsVista.Pqrs> listar() {
        return jdbcTemplate.query(SELECT_BASE + " ORDER BY q.pqrs_fecha DESC, q.pqrs_id DESC", this::mapPqrs);
    }

    public PqrsVista.Pqrs obtener(int id) {
        List<PqrsVista.Pqrs> lista = jdbcTemplate.query(SELECT_BASE + " WHERE q.pqrs_id = ?", this::mapPqrs, id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    /** Consulta pública: exige número y correo para que nadie vea PQRS ajenas. */
    public PqrsVista.Consulta consultaPublica(int id, String correo) {
        List<PqrsVista.Consulta> lista = jdbcTemplate.query(
                SELECT_BASE + " WHERE q.pqrs_id = ? AND LOWER(q.pqrs_correo) = LOWER(?)",
                (rs, i) -> new PqrsVista.Consulta(
                        rs.getInt("pqrs_id"),
                        fecha(rs.getTimestamp("pqrs_fecha")),
                        rs.getString("tipo"),
                        rs.getString("estado"),
                        rs.getString("pqrs_descripcion"),
                        rs.getString("pqrs_respuesta"),
                        fecha(rs.getTimestamp("pqrs_fecha_respuesta"))),
                id, correo);

        return lista.isEmpty() ? null : lista.get(0);
    }

    public List<PqrsVista.Tipo> tiposActivos() {
        return jdbcTemplate.query(
                "SELECT tpqrs_id, tpqrs_descripcion FROM tipo_tpqrs WHERE LOWER(tpqrs_estado) = 'activo' ORDER BY tpqrs_id",
                (rs, i) -> new PqrsVista.Tipo(rs.getInt("tpqrs_id"), rs.getString("tpqrs_descripcion")));
    }

    public boolean tipoActivo(int tpqrsId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tipo_tpqrs WHERE tpqrs_id = ? AND LOWER(tpqrs_estado) = 'activo'",
                Integer.class, tpqrsId);
        return total != null && total > 0;
    }

    public int insertar(PqrsVista.Nueva p, String origen) {
        KeyHolder llave = new GeneratedKeyHolder();

        jdbcTemplate.update(conexion -> {
            PreparedStatement ps = conexion.prepareStatement(
                    """
                    INSERT INTO pqrs (pqrs_fecha, pqrs_nombre, pqrs_descripcion, pqrs_correo, pqrs_telefono,
                                      tpqrs_id, est_id, pqrs_origen)
                    VALUES (NOW(), ?, ?, ?, ?, ?, ?, ?)
                    """,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.pqrs_nombre());
            ps.setString(2, p.pqrs_descripcion());
            ps.setString(3, p.pqrs_correo());
            ps.setString(4, p.pqrs_telefono());
            ps.setInt(5, p.tpqrs_id());
            ps.setInt(6, estadoPendiente);
            ps.setString(7, origen);
            return ps;
        }, llave);

        return llave.getKey().intValue();
    }

    public void responder(int id, String respuesta, int usuId) {
        jdbcTemplate.update(
                """
                UPDATE pqrs
                SET pqrs_respuesta = ?, pqrs_fecha_respuesta = NOW(), usu_id_respuesta = ?, est_id = ?
                WHERE pqrs_id = ?
                """,
                respuesta, usuId, estadoAtendida, id);
    }
}
