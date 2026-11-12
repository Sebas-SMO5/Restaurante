
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.EncabezadoEgresosDAO;
import com.restaurante.backend_restaurante.Model.EncabezadoEgresos;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/egresos")
public class EncabezadoEgresosController {

    private final EncabezadoEgresosDAO egresosDAO;

    public EncabezadoEgresosController(EncabezadoEgresosDAO egresosDAO) {
        this.egresosDAO = egresosDAO;
    }

    @GetMapping
    public ResponseEntity<List<EncabezadoEgresos>> listarEgresos() {
        return ResponseEntity.ok(egresosDAO.getAllEncabezadosEgresos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEgresoPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del egreso no es válido.");
        }

        EncabezadoEgresos egreso = egresosDAO.getEncabezadoEgresoById(id);

        if (egreso == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el egreso.");
        }

        return ResponseEntity.ok(egreso);
    }

    @PostMapping
    public ResponseEntity<String> registrarEgreso(
            @RequestBody EncabezadoEgresos egreso) {

        if (egreso.getId() <= 0 || egreso.getNo_egreso() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID y el número de egreso son obligatorios.");
        }

        if (egreso.getTercero_identificacion() == null
                || egreso.getTercero_identificacion().isBlank()
                || egreso.getDetalle() == null
                || egreso.getDetalle().isBlank()
                || egreso.getNo_documento() == null
                || egreso.getNo_documento().isBlank()
                || egreso.getEstado() == null
                || egreso.getEstado().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Completa los datos obligatorios del egreso.");
        }

        if (egreso.getValor_egreso() <= 0
                || egreso.getFp_id() <= 0
                || egreso.getConceptoEgreso_codigo() <= 0) {

            return ResponseEntity.badRequest()
                    .body("El valor, la forma de pago y el concepto deben ser válidos.");
        }

        boolean registrado = egresosDAO.add(egreso);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Egreso registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el egreso.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarEgreso(
            @PathVariable int id,
            @RequestBody EncabezadoEgresos egreso) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del egreso no es válido.");
        }

        if (egreso.getTercero_identificacion() == null
                || egreso.getTercero_identificacion().isBlank()
                || egreso.getDetalle() == null
                || egreso.getDetalle().isBlank()
                || egreso.getNo_documento() == null
                || egreso.getNo_documento().isBlank()
                || egreso.getEstado() == null
                || egreso.getEstado().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Completa los datos obligatorios del egreso.");
        }

        if (egreso.getValor_egreso() <= 0
                || egreso.getFp_id() <= 0
                || egreso.getConceptoEgreso_codigo() <= 0) {

            return ResponseEntity.badRequest()
                    .body("El valor, la forma de pago y el concepto deben ser válidos.");
        }

        egreso.setId(id);

        boolean actualizado = egresosDAO.update(egreso);

        if (actualizado) {
            return ResponseEntity.ok("Egreso actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el egreso o no se pudo actualizar.");
    }
}
