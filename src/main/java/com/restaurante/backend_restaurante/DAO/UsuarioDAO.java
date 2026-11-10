
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Usuario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UsuarioDAO {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Usuario mapUsuario(ResultSet rs, int rowNum) throws SQLException {
        Usuario usuario = new Usuario();

        usuario.setUsu_id(rs.getInt("usu_id"));
        usuario.setUsu_nombre(rs.getString("usu_nombre"));
        usuario.setUsu_apellido(rs.getString("usu_apellido"));
        usuario.setUsu_direccion(rs.getString("usu_direccion"));
        usuario.setUsu_telefono(rs.getString("usu_telefono"));
        usuario.setUsu_correo(rs.getString("usu_correo"));
        usuario.setPerf_id(rs.getObject("perf_id", Integer.class));
        usuario.setUsu_login(rs.getString("usu_login"));
        usuario.setUsu_estado(rs.getString("usu_estado"));

        return usuario;
    }

    public List<Usuario> getAllUsuarios() {
        String sql = """
            SELECT usu_id, usu_nombre, usu_apellido, usu_direccion,
                   usu_telefono, usu_correo, perf_id, usu_login, usu_estado
            FROM usuario
            ORDER BY usu_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapUsuario);
    }

    public Usuario getUsuarioById(int id) {
        String sql = """
            SELECT usu_id, usu_nombre, usu_apellido, usu_direccion,
                   usu_telefono, usu_correo, perf_id, usu_login, usu_estado
            FROM usuario
            WHERE usu_id = ?
            """;

        List<Usuario> resultados = jdbcTemplate.query(
            sql,
            this::mapUsuario,
            id
        );

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public boolean add(Usuario usuario) {
        String sql = """
            INSERT INTO usuario (
                usu_nombre, usu_apellido, usu_direccion,
                usu_telefono, usu_correo, perf_id,
                usu_login, usu_pass, usu_estado
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
            sql,
            usuario.getUsu_nombre(),
            usuario.getUsu_apellido(),
            usuario.getUsu_direccion(),
            usuario.getUsu_telefono(),
            usuario.getUsu_correo(),
            usuario.getPerf_id(),
            usuario.getUsu_login(),
            usuario.getUsu_pass(),
            usuario.getUsu_estado()
        ) > 0;
    }

    public boolean update(Usuario usuario) {
        String sql = """
            UPDATE usuario
            SET usu_nombre = ?,
                usu_apellido = ?,
                usu_direccion = ?,
                usu_telefono = ?,
                usu_correo = ?,
                perf_id = ?,
                usu_login = ?,
                usu_pass = ?,
                usu_estado = ?
            WHERE usu_id = ?
            """;

        return jdbcTemplate.update(
            sql,
            usuario.getUsu_nombre(),
            usuario.getUsu_apellido(),
            usuario.getUsu_direccion(),
            usuario.getUsu_telefono(),
            usuario.getUsu_correo(),
            usuario.getPerf_id(),
            usuario.getUsu_login(),
            usuario.getUsu_pass(),
            usuario.getUsu_estado(),
            usuario.getUsu_id()
        ) > 0;
    }
}
