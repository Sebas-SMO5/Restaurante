
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.ClienteDAO;
import com.restaurante.backend_restaurante.Model.Cliente;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteDAO clienteDAO;

    public ClienteController(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteDAO.getAllClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerClientePorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del cliente no es válido.");
        }

        Cliente cliente = clienteDAO.getClienteById(id);

        if (cliente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el cliente.");
        }

        return ResponseEntity.ok(cliente);
    }

    @PostMapping
    public ResponseEntity<String> registrarCliente(@RequestBody Cliente cliente) {

        if (cliente.getCli_nombre() == null || cliente.getCli_nombre().isBlank()
                || cliente.getCli_apellidos() == null || cliente.getCli_apellidos().isBlank()
                || cliente.getCli_correo() == null || cliente.getCli_correo().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("El nombre, los apellidos y el correo son obligatorios.");
        }

        boolean registrado = clienteDAO.add(cliente);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Cliente registrado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("No se pudo registrar el cliente.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarCliente(
            @PathVariable int id,
            @RequestBody Cliente cliente) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del cliente no es válido.");
        }

        if (cliente.getCli_nombre() == null || cliente.getCli_nombre().isBlank()
                || cliente.getCli_apellidos() == null || cliente.getCli_apellidos().isBlank()
                || cliente.getCli_correo() == null || cliente.getCli_correo().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("El nombre, los apellidos y el correo son obligatorios.");
        }

        cliente.setCli_id(id);

        boolean actualizado = clienteDAO.update(cliente);

        if (actualizado) {
            return ResponseEntity.ok("Cliente actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el cliente o no se pudo actualizar.");
    }
}
