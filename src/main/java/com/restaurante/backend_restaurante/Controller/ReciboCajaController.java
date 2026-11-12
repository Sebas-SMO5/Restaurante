
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.ReciboCajaDAO;
import com.restaurante.backend_restaurante.Model.ReciboCaja;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recibos-caja")
public class ReciboCajaController {

    private final ReciboCajaDAO reciboCajaDAO;

    public ReciboCajaController(ReciboCajaDAO reciboCajaDAO) {
        this.reciboCajaDAO = reciboCajaDAO;
    }

    @GetMapping
    public ResponseEntity<List<ReciboCaja>> listarRecibos() {
        return ResponseEntity.ok(reciboCajaDAO.getAllRecibosCaja());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReciboPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El número del recibo no es válido.");
        }

        ReciboCaja recibo = reciboCajaDAO.getReciboCajaById(id);

        if (recibo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el recibo de caja.");
        }

        return ResponseEntity.ok(recibo);
    }

    @PostMapping
    public ResponseEntity<String> registrarRecibo(
            @RequestBody ReciboCaja recibo) {

        if (recibo.getUsu_id() <= 0
                || recibo.getRc_fecha() == null
                || recibo.getPed_id() <= 0
                || recibo.getCli_id() <= 0
                || recibo.getRc_total() <= 0
                || recibo.getRc_estado() == null
                || recibo.getRc_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Completa los datos obligatorios del recibo de caja.");
        }

        boolean registrado = reciboCajaDAO.add(recibo);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Recibo de caja registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el recibo de caja.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarRecibo(
            @PathVariable int id,
            @RequestBody ReciboCaja recibo) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El número del recibo no es válido.");
        }

        if (recibo.getUsu_id() <= 0
                || recibo.getRc_fecha() == null
                || recibo.getPed_id() <= 0
                || recibo.getCli_id() <= 0
                || recibo.getRc_total() <= 0
                || recibo.getRc_estado() == null
                || recibo.getRc_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Completa los datos obligatorios del recibo de caja.");
        }

        recibo.setRc_num(id);

        boolean actualizado = reciboCajaDAO.update(recibo);

        if (actualizado) {
            return ResponseEntity.ok("Recibo de caja actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el recibo o no se pudo actualizar.");
    }
}
