
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.PqrsDAO;
import com.restaurante.backend_restaurante.Model.Pqrs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pqrs")
public class PqrsController {

    private final PqrsDAO pqrsDAO;

    public PqrsController(PqrsDAO pqrsDAO) {
        this.pqrsDAO = pqrsDAO;
    }

    @GetMapping
    public ResponseEntity<List<Pqrs>> listarPqrs() {
        return ResponseEntity.ok(pqrsDAO.getAllPqrs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPqrsPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID de la PQRS no es válido.");
        }

        Pqrs pqrs = pqrsDAO.getPqrsById(id);

        if (pqrs == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la PQRS.");
        }

        return ResponseEntity.ok(pqrs);
    }

    @PostMapping
    public ResponseEntity<String> registrarPqrs(@RequestBody Pqrs pqrs) {

        if (pqrs.getPqrs_fecha() == null
                || pqrs.getPqrs_descripcion() == null
                || pqrs.getPqrs_descripcion().length == 0
                || pqrs.getPqrs_correo() == null
                || pqrs.getPqrs_correo().trim().isEmpty()
                || pqrs.getPqrs_telefono() == null
                || pqrs.getPqrs_telefono().trim().isEmpty()
                || pqrs.getTpqrs_id() <= 0
                || pqrs.getEst_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Completa los datos obligatorios de la PQRS.");
        }

        if (pqrs.getPqrs_respuesta() == null) {
            pqrs.setPqrs_respuesta(new byte[0]);
        }

        boolean registrado = pqrsDAO.add(pqrs);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("PQRS registrada correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar la PQRS.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarPqrs(
            @PathVariable int id,
            @RequestBody Pqrs pqrs) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID de la PQRS no es válido.");
        }

        if (pqrs.getPqrs_fecha() == null
                || pqrs.getPqrs_descripcion() == null
                || pqrs.getPqrs_descripcion().length == 0
                || pqrs.getPqrs_correo() == null
                || pqrs.getPqrs_correo().trim().isEmpty()
                || pqrs.getPqrs_telefono() == null
                || pqrs.getPqrs_telefono().trim().isEmpty()
                || pqrs.getTpqrs_id() <= 0
                || pqrs.getEst_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Completa los datos obligatorios de la PQRS.");
        }

        if (pqrs.getPqrs_respuesta() == null) {
            pqrs.setPqrs_respuesta(new byte[0]);
        }

        pqrs.setPqrs_id(id);

        boolean actualizado = pqrsDAO.update(pqrs);

        if (actualizado) {
            return ResponseEntity.ok("PQRS actualizada correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la PQRS o no se pudo actualizar.");
    }
}
