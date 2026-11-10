
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.EstadoDAO;
import com.restaurante.backend_restaurante.Model.Estado;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
public class EstadoController {

    private final EstadoDAO estadoDAO;

    public EstadoController(EstadoDAO estadoDAO) {
        this.estadoDAO = estadoDAO;
    }

    @GetMapping
    public ResponseEntity<List<Estado>> listarEstados() {
        return ResponseEntity.ok(estadoDAO.getAllEstados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEstadoPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del estado no es válido.");
        }

        Estado estado = estadoDAO.getEstadoById(id);

        if (estado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el estado.");
        }

        return ResponseEntity.ok(estado);
    }

    @PostMapping
    public ResponseEntity<String> registrarEstado(
            @RequestBody Estado estado) {

        if (estado.getEst_descripcion() == null
                || estado.getEst_descripcion().isBlank()
                || estado.getEst_estado() == null
                || estado.getEst_estado().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("La descripción y el estado son obligatorios.");
        }

        if (estado.getTes_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Debes seleccionar un tipo de estado válido.");
        }

        boolean registrado = estadoDAO.add(estado);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Estado registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el estado.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarEstado(
            @PathVariable int id,
            @RequestBody Estado estado) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del estado no es válido.");
        }

        if (estado.getEst_descripcion() == null
                || estado.getEst_descripcion().isBlank()
                || estado.getEst_estado() == null
                || estado.getEst_estado().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("La descripción y el estado son obligatorios.");
        }

        if (estado.getTes_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Debes seleccionar un tipo de estado válido.");
        }

        estado.setEst_id(id);

        boolean actualizado = estadoDAO.update(estado);

        if (actualizado) {
            return ResponseEntity.ok("Estado actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el estado o no se pudo actualizar.");
    }
}
