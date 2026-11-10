
package com.restaurante.backend_restaurante.DAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.restaurante.backend_restaurante.Model.Categoria;

import java.util.List;

@Repository
public class CategoriaDAO {

    private final JdbcTemplate jdbcTemplate;

    public CategoriaDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Categoria mapCategoria(java.sql.ResultSet rs, int rowNum)
            throws java.sql.SQLException {

        return new Categoria(
                rs.getInt("idCategoria"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getBoolean("estado")
        );
    }

    public List<Categoria> getAllCategorias() {

        String sql = """
            SELECT idCategoria, nombre, descripcion, estado
            FROM categoria
            ORDER BY idCategoria DESC
            """;

        return jdbcTemplate.query(sql, this::mapCategoria);
    }

    public Categoria getCategoriaById(int id) {

        String sql = """
            SELECT idCategoria, nombre, descripcion, estado
            FROM categoria
            WHERE idCategoria = ?
            """;

        List<Categoria> categorias = jdbcTemplate.query(
                sql,
                this::mapCategoria,
                id
        );

        return categorias.isEmpty() ? null : categorias.get(0);
    }

    public boolean add(Categoria categoria) {

        String sql = """
            INSERT INTO categoria (nombre, descripcion, estado)
            VALUES (?, ?, ?)
            """;

        return jdbcTemplate.update(
                sql,
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.isEstado()
        ) > 0;
    }

    public boolean update(Categoria categoria) {

        String sql = """
            UPDATE categoria
            SET nombre = ?,
                descripcion = ?,
                estado = ?
            WHERE idCategoria = ?
            """;

        return jdbcTemplate.update(
                sql,
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.isEstado(),
                categoria.getIdCategoria()
        ) > 0;
    }
}
