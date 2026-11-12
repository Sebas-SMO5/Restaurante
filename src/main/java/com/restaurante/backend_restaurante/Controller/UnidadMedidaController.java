
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.UnidadMedidaDAO;
import com.restaurante.backend_restaurante.Model.UnidadMedida;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades-medida")
public class UnidadMedidaController {

    private final UnidadMedidaDAO unidadMedidaDAO;

    public UnidadMedidaController(UnidadMedidaDAO unidadMedidaDAO) {
        this.unidadMedidaDAO = unidadMedidaDAO;
    }

    @GetMapping
    public ResponseEntity<List<UnidadMedida>> listarUnidadesMedida() {
        return ResponseEntity.ok(unidadMedidaDAO.getAllUnidadesMedida());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUnidadMedidaPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID de la unidad de medida no es válido.");
        }

        UnidadMedida unidadMedida = unidadMedidaDAO.getUnidadMedidaById(id);

        if (unidadMedida == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la unidad de medida.");
        }

        return ResponseEntity.ok(unidadMedida);
    }

    @PostMapping
    public ResponseEntity<String> registrarUnidadMedida(
            @RequestBody UnidadMedida unidadMedida) {

        if (unidadMedida.getUm_id() <= 0
                || unidadMedida.getUm_descripcion() == null
                || unidadMedida.getUm_descripcion().trim().isEmpty()
                || unidadMedida.getUm_abreviatura() == null
                || unidadMedida.getUm_abreviatura().trim().isEmpty()
                || unidadMedida.getUm_estado() == null
                || unidadMedida.getUm_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Completa el ID, la descripción, la abreviatura y el estado.");
        }

        boolean registrado = unidadMedidaDAO.add(unidadMedida);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Unidad de medida registrada correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar la unidad de medida.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarUnidadMedida(
            @PathVariable int id,
            @RequestBody UnidadMedida unidadMedida) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID de la unidad de medida no es válido.");
        }

        if (unidadMedida.getUm_descripcion() == null
                || unidadMedida.getUm_descripcion().trim().isEmpty()
                || unidadMedida.getUm_abreviatura() == null
                || unidadMedida.getUm_abreviatura().trim().isEmpty()
                || unidadMedida.getUm_estado() == null
                || unidadMedida.getUm_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción, la abreviatura y el estado son obligatorios.");
        }

        unidadMedida.setUm_id(id);

        boolean actualizado = unidadMedidaDAO.update(unidadMedida);

        if (actualizado) {
            return ResponseEntity.ok("Unidad de medida actualizada correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la unidad de medida o no se pudo actualizar.");
    }
}
