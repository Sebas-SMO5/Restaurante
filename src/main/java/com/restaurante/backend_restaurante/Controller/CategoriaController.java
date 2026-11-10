
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.CategoriaDAO;
import com.restaurante.backend_restaurante.Model.Categoria;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaDAO categoriaDAO;

    public CategoriaController(CategoriaDAO categoriaDAO) {
        this.categoriaDAO = categoriaDAO;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        return ResponseEntity.ok(categoriaDAO.getAllCategorias());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCategoriaPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID de la categoría no es válido.");
        }

        Categoria categoria = categoriaDAO.getCategoriaById(id);

        if (categoria == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la categoría.");
        }

        return ResponseEntity.ok(categoria);
    }

    @PostMapping
    public ResponseEntity<String> registrarCategoria(
            @RequestBody Categoria categoria) {

        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("El nombre de la categoría es obligatorio.");
        }

        boolean registrada = categoriaDAO.add(categoria);

        if (registrada) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Categoría registrada correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar la categoría.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCategoria(
            @PathVariable int id,
            @RequestBody Categoria categoria) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID de la categoría no es válido.");
        }

        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("El nombre de la categoría es obligatorio.");
        }

        categoria.setIdCategoria(id);

        boolean actualizada = categoriaDAO.update(categoria);

        if (actualizada) {
            return ResponseEntity.ok("Categoría actualizada correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la categoría o no se pudo actualizar.");
    }
}
