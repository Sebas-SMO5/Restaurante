
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.ReciboCajaDetalleDAO;
import com.restaurante.backend_restaurante.Model.ReciboCajaDetalle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recibos-caja-detalle")
public class ReciboCajaDetalleController {

    private final ReciboCajaDetalleDAO reciboCajaDetalleDAO;

    public ReciboCajaDetalleController(ReciboCajaDetalleDAO reciboCajaDetalleDAO) {
        this.reciboCajaDetalleDAO = reciboCajaDetalleDAO;
    }

    @GetMapping
    public ResponseEntity<List<ReciboCajaDetalle>> listarDetalles() {
        return ResponseEntity.ok(reciboCajaDetalleDAO.getAllRecibosCajaDetalle());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetallePorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del detalle no es válido.");
        }

        ReciboCajaDetalle detalle = reciboCajaDetalleDAO.getReciboCajaDetalleById(id);

        if (detalle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el detalle del recibo.");
        }

        return ResponseEntity.ok(detalle);
    }

    @PostMapping
    public ResponseEntity<String> registrarDetalle(
            @RequestBody ReciboCajaDetalle detalle) {

        if (detalle.getRcd_num() <= 0
                || detalle.getPla_id() <= 0
                || detalle.getRcd_precio() <= 0
                || detalle.getRcd_cantidad() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Completa los datos obligatorios del detalle del recibo.");
        }

        boolean registrado = reciboCajaDetalleDAO.add(detalle);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Detalle del recibo registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el detalle del recibo.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarDetalle(
            @PathVariable int id,
            @RequestBody ReciboCajaDetalle detalle) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del detalle no es válido.");
        }

        if (detalle.getRcd_num() <= 0
                || detalle.getPla_id() <= 0
                || detalle.getRcd_precio() <= 0
                || detalle.getRcd_cantidad() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Completa los datos obligatorios del detalle del recibo.");
        }

        detalle.setRcd_id(id);

        boolean actualizado = reciboCajaDetalleDAO.update(detalle);

        if (actualizado) {
            return ResponseEntity.ok("Detalle del recibo actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el detalle o no se pudo actualizar.");
    }
}
