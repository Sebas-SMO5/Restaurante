
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.PedidoDetalleDAO;
import com.restaurante.backend_restaurante.Model.PedidoDetalle;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos-detalle")
public class PedidoDetalleController {

    private final PedidoDetalleDAO pedidoDetalleDAO;

    public PedidoDetalleController(PedidoDetalleDAO pedidoDetalleDAO) {
        this.pedidoDetalleDAO = pedidoDetalleDAO;
    }

    @GetMapping
    public ResponseEntity<List<PedidoDetalle>> listarDetalles() {
        return ResponseEntity.ok(pedidoDetalleDAO.getAllPedidoDetalles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerDetallePorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del detalle no es válido.");
        }

        PedidoDetalle detalle = pedidoDetalleDAO.getPedidoDetalleById(id);

        if (detalle == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el detalle del pedido.");
        }

        return ResponseEntity.ok(detalle);
    }

    @PostMapping
    public ResponseEntity<String> registrarDetalle(
            @RequestBody PedidoDetalle detalle) {

        if (detalle.getPed_det_cant() == null
                || detalle.getPed_det_cant() <= 0) {
            return ResponseEntity.badRequest()
                    .body("La cantidad debe ser mayor que cero.");
        }

        if (detalle.getPed_det_precio() == null
                || detalle.getPed_det_precio() < 0) {
            return ResponseEntity.badRequest()
                    .body("El precio del plato debe ser válido.");
        }

        if (detalle.getPla_id() == null || detalle.getPla_id() <= 0
                || detalle.getPed_id() == null || detalle.getPed_id() <= 0
                || detalle.getEst_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Debes seleccionar un plato, un pedido y un estado válidos.");
        }

        boolean registrado = pedidoDetalleDAO.add(detalle);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Detalle del pedido registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el detalle del pedido.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarDetalle(
            @PathVariable int id,
            @RequestBody PedidoDetalle detalle) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del detalle no es válido.");
        }

        if (detalle.getPed_det_cant() == null
                || detalle.getPed_det_cant() <= 0) {
            return ResponseEntity.badRequest()
                    .body("La cantidad debe ser mayor que cero.");
        }

        if (detalle.getPed_det_precio() == null
                || detalle.getPed_det_precio() < 0) {
            return ResponseEntity.badRequest()
                    .body("El precio del plato debe ser válido.");
        }

        if (detalle.getPla_id() == null || detalle.getPla_id() <= 0
                || detalle.getPed_id() == null || detalle.getPed_id() <= 0
                || detalle.getEst_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("Debes seleccionar un plato, un pedido y un estado válidos.");
        }

        detalle.setPed_det_id(id);

        boolean actualizado = pedidoDetalleDAO.update(detalle);

        if (actualizado) {
            return ResponseEntity.ok("Detalle del pedido actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el detalle o no se pudo actualizar.");
    }
}
