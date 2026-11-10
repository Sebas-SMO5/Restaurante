
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.FormaPagoDAO;
import com.restaurante.backend_restaurante.Model.FormaPago;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formas-pago")
public class FormaPagoController {

    private final FormaPagoDAO formaPagoDAO;

    public FormaPagoController(FormaPagoDAO formaPagoDAO) {
        this.formaPagoDAO = formaPagoDAO;
    }

    @GetMapping
    public ResponseEntity<List<FormaPago>> listarFormasPago() {
        return ResponseEntity.ok(formaPagoDAO.getAllFormasPago());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerFormaPagoPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código de la forma de pago no es válido.");
        }

        FormaPago formaPago = formaPagoDAO.getFormaPagoById(id);

        if (formaPago == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la forma de pago.");
        }

        return ResponseEntity.ok(formaPago);
    }

    @PostMapping
    public ResponseEntity<String> registrarFormaPago(
            @RequestBody FormaPago formaPago) {

        if (formaPago.getFp_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código de la forma de pago es obligatorio.");
        }

        if (formaPago.getFp_descripcion() == null
                || formaPago.getFp_descripcion().isBlank()
                || formaPago.getFp_estado() == null
                || formaPago.getFp_estado().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("La descripción y el estado son obligatorios.");
        }

        boolean registrada = formaPagoDAO.add(formaPago);

        if (registrada) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Forma de pago registrada correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar la forma de pago.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarFormaPago(
            @PathVariable int id,
            @RequestBody FormaPago formaPago) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código de la forma de pago no es válido.");
        }

        if (formaPago.getFp_descripcion() == null
                || formaPago.getFp_descripcion().isBlank()
                || formaPago.getFp_estado() == null
                || formaPago.getFp_estado().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("La descripción y el estado son obligatorios.");
        }

        formaPago.setFp_id(id);

        boolean actualizado = formaPagoDAO.update(formaPago);

        if (actualizado) {
            return ResponseEntity.ok("Forma de pago actualizada correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la forma de pago o no se pudo actualizar.");
    }
}
