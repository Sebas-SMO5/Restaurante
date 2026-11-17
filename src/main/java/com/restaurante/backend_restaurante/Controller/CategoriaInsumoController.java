package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.CategoriaInsumoDAO;
import com.restaurante.backend_restaurante.Model.CategoriaInsumo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-insumo")
public class CategoriaInsumoController {

    private final CategoriaInsumoDAO categoriaInsumoDAO;

    public CategoriaInsumoController(CategoriaInsumoDAO categoriaInsumoDAO) {
        this.categoriaInsumoDAO = categoriaInsumoDAO;
    }

    @GetMapping
    public ResponseEntity<List<CategoriaInsumo>> listar() {
        return ResponseEntity.ok(categoriaInsumoDAO.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest().body("El código de la categoría no es válido.");
        }

        CategoriaInsumo categoria = categoriaInsumoDAO.getById(id);

        if (categoria == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la categoría de insumo.");
        }

        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody CategoriaInsumo categoria) {

        String error = validar(categoria, 0);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        if (categoriaInsumoDAO.add(categoria)) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Categoría de insumo registrada correctamente.");
        }

        return ResponseEntity.badRequest().body("No se pudo registrar la categoría de insumo.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizar(
            @PathVariable int id,
            @RequestBody CategoriaInsumo categoria) {

        if (id <= 0) {
            return ResponseEntity.badRequest().body("El código de la categoría no es válido.");
        }

        String error = validar(categoria, id);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        categoria.setCins_id(id);

        if (categoriaInsumoDAO.update(categoria)) {
            return ResponseEntity.ok("Categoría de insumo actualizada correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la categoría de insumo o no se pudo actualizar.");
    }

    private String validar(CategoriaInsumo categoria, int idActual) {

        if (categoria == null
                || categoria.getCins_descripcion() == null || categoria.getCins_descripcion().isBlank()
                || categoria.getCins_estado() == null || categoria.getCins_estado().isBlank()) {
            return "La descripción y el estado son obligatorios.";
        }

        categoria.setCins_descripcion(categoria.getCins_descripcion().trim());
        categoria.setCins_estado(categoria.getCins_estado().trim());

        if (categoria.getCins_descripcion().length() > 40) {
            return "La descripción admite máximo 40 caracteres.";
        }

        if (!"Activo".equalsIgnoreCase(categoria.getCins_estado())
                && !"Inactivo".equalsIgnoreCase(categoria.getCins_estado())) {
            return "El estado debe ser Activo o Inactivo.";
        }

        if (categoriaInsumoDAO.existeDescripcion(categoria.getCins_descripcion(), idActual)) {
            return "Ya existe una categoría de insumo con esa descripción.";
        }

        return null;
    }
}
