package com.restaurante.backend_restaurante.DAO;

import com.restaurante.backend_restaurante.Model.Cliente;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ClienteDAO {

    private static final String SELECT_BASE = """
        SELECT c.cli_id, c.tipo_id, c.cli_documento, c.cli_nombre, c.cli_apellidos,
               c.cli_direccion, c.cli_telefono, c.cli_correo, c.cli_estado,
               t.tipo_sigla
        FROM cliente c
        LEFT JOIN tipo_identidad t ON t.tipo_id = c.tipo_id
        """;

    private final JdbcTemplate jdbcTemplate;

    public ClienteDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Cliente mapCliente(ResultSet rs, int rowNum) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setCli_id(rs.getInt("cli_id"));
        cliente.setTipo_id(rs.getObject("tipo_id", Integer.class));
        cliente.setCli_documento(rs.getString("cli_documento"));
        cliente.setCli_nombre(rs.getString("cli_nombre"));
        cliente.setCli_apellidos(rs.getString("cli_apellidos"));
        cliente.setCli_direccion(rs.getString("cli_direccion"));
        cliente.setCli_telefono(rs.getString("cli_telefono"));
        cliente.setCli_correo(rs.getString("cli_correo"));
        cliente.setCli_estado(rs.getString("cli_estado"));
        cliente.setTipo_sigla(rs.getString("tipo_sigla"));
        return cliente;
    }

    public List<Cliente> getAllClientes() {
        return jdbcTemplate.query(SELECT_BASE + " ORDER BY c.cli_id DESC", this::mapCliente);
    }

    public Cliente getClienteById(int id) {
        List<Cliente> clientes = jdbcTemplate.query(
                SELECT_BASE + " WHERE c.cli_id = ?",
                this::mapCliente,
                id
        );

        return clientes.isEmpty() ? null : clientes.get(0);
    }

    /** Indica si ya existe otro cliente con el mismo tipo y número de documento. */
    public boolean existeDocumento(int tipoId, String documento, int idExcluir) {
        Integer total = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM cliente
                WHERE tipo_id = ? AND cli_documento = ? AND cli_id <> ?
                """,
                Integer.class,
                tipoId, documento, idExcluir
        );

        return total != null && total > 0;
    }

    public boolean add(Cliente cliente) {
        String sql = """
            INSERT INTO cliente (
                tipo_id, cli_documento, cli_nombre, cli_apellidos,
                cli_direccion, cli_telefono, cli_correo, cli_estado
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                cliente.getTipo_id(),
                cliente.getCli_documento(),
                cliente.getCli_nombre(),
                cliente.getCli_apellidos(),
                cliente.getCli_direccion(),
                cliente.getCli_telefono(),
                cliente.getCli_correo(),
                cliente.getCli_estado()
        ) > 0;
    }

    public boolean update(Cliente cliente) {
        String sql = """
            UPDATE cliente
            SET tipo_id = ?,
                cli_documento = ?,
                cli_nombre = ?,
                cli_apellidos = ?,
                cli_direccion = ?,
                cli_telefono = ?,
                cli_correo = ?,
                cli_estado = ?
            WHERE cli_id = ?
            """;

        return jdbcTemplate.update(
                sql,
                cliente.getTipo_id(),
                cliente.getCli_documento(),
                cliente.getCli_nombre(),
                cliente.getCli_apellidos(),
                cliente.getCli_direccion(),
                cliente.getCli_telefono(),
                cliente.getCli_correo(),
                cliente.getCli_estado(),
                cliente.getCli_id()
        ) > 0;
    }
}
