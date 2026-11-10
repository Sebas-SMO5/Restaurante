
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Cliente;

import java.util.List;

@Repository
public class ClienteDAO {

    private final JdbcTemplate jdbcTemplate;

    public ClienteDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Cliente mapCliente(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        return new Cliente(
                rs.getInt("cli_id"),
                rs.getString("cli_nombre"),
                rs.getString("cli_apellidos"),
                rs.getString("cli_direccion"),
                rs.getString("cli_telefono"),
                rs.getString("cli_correo"),
                rs.getString("cli_estado")
        );
    }

    public List<Cliente> getAllClientes() {

        String sql = """
            SELECT cli_id, cli_nombre, cli_apellidos, cli_direccion,
                   cli_telefono, cli_correo, cli_estado
            FROM cliente
            ORDER BY cli_id DESC
            """;

        return jdbcTemplate.query(sql, this::mapCliente);
    }

    public Cliente getClienteById(int id) {

        String sql = """
            SELECT cli_id, cli_nombre, cli_apellidos, cli_direccion,
                   cli_telefono, cli_correo, cli_estado
            FROM cliente
            WHERE cli_id = ?
            """;

        List<Cliente> clientes = jdbcTemplate.query(
                sql,
                this::mapCliente,
                id
        );

        return clientes.isEmpty() ? null : clientes.get(0);
    }

    public boolean add(Cliente cliente) {

        String sql = """
            INSERT INTO cliente (
                cli_nombre, cli_apellidos, cli_direccion,
                cli_telefono, cli_correo, cli_estado
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
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
            SET cli_nombre = ?,
                cli_apellidos = ?,
                cli_direccion = ?,
                cli_telefono = ?,
                cli_correo = ?,
                cli_estado = ?
            WHERE cli_id = ?
            """;

        return jdbcTemplate.update(
                sql,
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
