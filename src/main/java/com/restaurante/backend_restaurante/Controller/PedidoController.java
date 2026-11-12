
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.PedidoDAO;
import com.restaurante.backend_restaurante.Model.Pedido;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoDAO pedidoDAO;

    public PedidoController(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        return ResponseEntity.ok(pedidoDAO.getAllPedidos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPedidoPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del pedido no es válido.");
        }

        Pedido pedido = pedidoDAO.getPedidoById(id);

        if (pedido == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el pedido.");
        }

        return ResponseEntity.ok(pedido);
    }

    @PostMapping
    public ResponseEntity<String> registrarPedido(
            @RequestBody Pedido pedido) {

        if (pedido.getPed_mesa() == null
                || pedido.getPed_mesa().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("Debes indicar la mesa del pedido.");
        }

        if (pedido.getPed_mesa().length() > 4) {
            return ResponseEntity.badRequest()
                    .body("El número de mesa no puede superar los 4 caracteres.");
        }

        if (pedido.getUsu_id() != null && pedido.getUsu_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El usuario seleccionado no es válido.");
        }

        if (pedido.getEst_id() != null && pedido.getEst_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El estado seleccionado no es válido.");
        }

        boolean registrado = pedidoDAO.add(pedido);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Pedido registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el pedido.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarPedido(
            @PathVariable int id,
            @RequestBody Pedido pedido) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del pedido no es válido.");
        }

        if (pedido.getPed_mesa() == null
                || pedido.getPed_mesa().isBlank()
                || pedido.getPed_mesa().length() > 4) {

            return ResponseEntity.badRequest()
                    .body("Debes indicar una mesa válida para el pedido.");
        }

        if (pedido.getUsu_id() != null && pedido.getUsu_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El usuario seleccionado no es válido.");
        }

        if (pedido.getEst_id() != null && pedido.getEst_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El estado seleccionado no es válido.");
        }

        pedido.setPed_id(id);

        boolean actualizado = pedidoDAO.update(pedido);

        if (actualizado) {
            return ResponseEntity.ok("Pedido actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el pedido o no se pudo actualizar.");
    }
}
