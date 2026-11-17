package com.restaurante.backend_restaurante.DAO;

import com.restaurante.backend_restaurante.Model.CategoriaInsumo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CategoriaInsumoDAO {

    private final JdbcTemplate jdbcTemplate;

    public CategoriaInsumoDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private CategoriaInsumo mapCategoria(ResultSet rs, int rowNum) throws SQLException {
        CategoriaInsumo categoria = new CategoriaInsumo();
        categoria.setCins_id(rs.getInt("cins_id"));
        categoria.setCins_descripcion(rs.getString("cins_descripcion"));
        categoria.setCins_estado(rs.getString("cins_estado"));
        return categoria;
    }

    public List<CategoriaInsumo> getAll() {
        return jdbcTemplate.query(
                "SELECT cins_id, cins_descripcion, cins_estado FROM categoria_insumo ORDER BY cins_descripcion",
                this::mapCategoria);
    }

    public CategoriaInsumo getById(int id) {
        List<CategoriaInsumo> resultados = jdbcTemplate.query(
                "SELECT cins_id, cins_descripcion, cins_estado FROM categoria_insumo WHERE cins_id = ?",
                this::mapCategoria, id);

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    public boolean existeDescripcion(String descripcion, int idExcluir) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM categoria_insumo WHERE LOWER(TRIM(cins_descripcion)) = LOWER(TRIM(?)) AND cins_id <> ?",
                Integer.class, descripcion, idExcluir);

        return total != null && total > 0;
    }

    public boolean add(CategoriaInsumo categoria) {
        return jdbcTemplate.update(
                "INSERT INTO categoria_insumo (cins_descripcion, cins_estado) VALUES (?, ?)",
                categoria.getCins_descripcion(),
                categoria.getCins_estado()) > 0;
    }

    public boolean update(CategoriaInsumo categoria) {
        return jdbcTemplate.update(
                "UPDATE categoria_insumo SET cins_descripcion = ?, cins_estado = ? WHERE cins_id = ?",
                categoria.getCins_descripcion(),
                categoria.getCins_estado(),
                categoria.getCins_id()) > 0;
    }
}
