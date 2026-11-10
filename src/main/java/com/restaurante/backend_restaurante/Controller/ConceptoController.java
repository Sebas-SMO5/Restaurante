
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.ConceptoDAO;
import com.restaurante.backend_restaurante.Model.Concepto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conceptos")
public class ConceptoController {

    private final ConceptoDAO conceptoDAO;

    public ConceptoController(ConceptoDAO conceptoDAO) {
        this.conceptoDAO = conceptoDAO;
    }

    @GetMapping
    public ResponseEntity<List<Concepto>> listarConceptos() {
        return ResponseEntity.ok(conceptoDAO.getAllConceptos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerConceptoPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código del concepto no es válido.");
        }

        Concepto concepto = conceptoDAO.getConceptoById(id);

        if (concepto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el concepto.");
        }

        return ResponseEntity.ok(concepto);
    }

    @PostMapping
    public ResponseEntity<String> registrarConcepto(
            @RequestBody Concepto concepto) {

        if (concepto.getCon_descripcion() == null
                || concepto.getCon_descripcion().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("La descripción del concepto es obligatoria.");
        }

        if (concepto.getCon_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código del concepto debe ser válido.");
        }

        boolean registrado = conceptoDAO.add(concepto);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Concepto registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el concepto.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarConcepto(
            @PathVariable int id,
            @RequestBody Concepto concepto) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código del concepto no es válido.");
        }

        if (concepto.getCon_descripcion() == null
                || concepto.getCon_descripcion().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("La descripción del concepto es obligatoria.");
        }

        concepto.setCon_id(id);

        boolean actualizado = conceptoDAO.update(concepto);

        if (actualizado) {
            return ResponseEntity.ok("Concepto actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el concepto o no se pudo actualizar.");
    }
}
