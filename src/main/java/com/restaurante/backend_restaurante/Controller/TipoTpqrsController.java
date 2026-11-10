
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.TipoTpqrsDAO;
import com.restaurante.backend_restaurante.Model.TipoTpqrs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-pqrs")
public class TipoTpqrsController {

    private final TipoTpqrsDAO tipoTpqrsDAO;

    public TipoTpqrsController(TipoTpqrsDAO tipoTpqrsDAO) {
        this.tipoTpqrsDAO = tipoTpqrsDAO;
    }

    @GetMapping
    public ResponseEntity<List<TipoTpqrs>> listarTiposPqrs() {
        return ResponseEntity.ok(tipoTpqrsDAO.getAllTiposTpqrs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTipoPqrsPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del tipo de PQRS no es válido.");
        }

        TipoTpqrs tipoTpqrs = tipoTpqrsDAO.getTipoTpqrsById(id);

        if (tipoTpqrs == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el tipo de PQRS.");
        }

        return ResponseEntity.ok(tipoTpqrs);
    }

    @PostMapping
    public ResponseEntity<String> registrarTipoPqrs(
            @RequestBody TipoTpqrs tipoTpqrs) {

        if (tipoTpqrs.getTpqrs_descripcion() == null
                || tipoTpqrs.getTpqrs_descripcion().trim().isEmpty()
                || tipoTpqrs.getTpqrs_estado() == null
                || tipoTpqrs.getTpqrs_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción y el estado son obligatorios.");
        }

        boolean registrado = tipoTpqrsDAO.add(tipoTpqrs);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Tipo de PQRS registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el tipo de PQRS.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarTipoPqrs(
            @PathVariable int id,
            @RequestBody TipoTpqrs tipoTpqrs) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del tipo de PQRS no es válido.");
        }

        if (tipoTpqrs.getTpqrs_descripcion() == null
                || tipoTpqrs.getTpqrs_descripcion().trim().isEmpty()
                || tipoTpqrs.getTpqrs_estado() == null
                || tipoTpqrs.getTpqrs_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción y el estado son obligatorios.");
        }

        tipoTpqrs.setTpqrs_id(id);

        boolean actualizado = tipoTpqrsDAO.update(tipoTpqrs);

        if (actualizado) {
            return ResponseEntity.ok("Tipo de PQRS actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el tipo de PQRS o no se pudo actualizar.");
    }
}
