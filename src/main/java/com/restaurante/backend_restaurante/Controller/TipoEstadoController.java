
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.TipoEstadoDAO;
import com.restaurante.backend_restaurante.Model.TipoEstado;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-estado")
public class TipoEstadoController {

    private final TipoEstadoDAO tipoEstadoDAO;

    public TipoEstadoController(TipoEstadoDAO tipoEstadoDAO) {
        this.tipoEstadoDAO = tipoEstadoDAO;
    }

    @GetMapping
    public ResponseEntity<List<TipoEstado>> listarTiposEstado() {
        return ResponseEntity.ok(tipoEstadoDAO.getAllTiposEstado());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTipoEstadoPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del tipo de estado no es válido.");
        }

        TipoEstado tipoEstado = tipoEstadoDAO.getTipoEstadoById(id);

        if (tipoEstado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el tipo de estado.");
        }

        return ResponseEntity.ok(tipoEstado);
    }

    @PostMapping
    public ResponseEntity<String> registrarTipoEstado(
            @RequestBody TipoEstado tipoEstado) {

        if (tipoEstado.getTes_descripcion() == null
                || tipoEstado.getTes_descripcion().trim().isEmpty()
                || tipoEstado.getTes_estado() == null
                || tipoEstado.getTes_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción y el estado son obligatorios.");
        }

        boolean registrado = tipoEstadoDAO.add(tipoEstado);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Tipo de estado registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el tipo de estado.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarTipoEstado(
            @PathVariable int id,
            @RequestBody TipoEstado tipoEstado) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del tipo de estado no es válido.");
        }

        if (tipoEstado.getTes_descripcion() == null
                || tipoEstado.getTes_descripcion().trim().isEmpty()
                || tipoEstado.getTes_estado() == null
                || tipoEstado.getTes_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción y el estado son obligatorios.");
        }

        tipoEstado.setTes_id(id);

        boolean actualizado = tipoEstadoDAO.update(tipoEstado);

        if (actualizado) {
            return ResponseEntity.ok("Tipo de estado actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el tipo de estado o no se pudo actualizar.");
    }
}
