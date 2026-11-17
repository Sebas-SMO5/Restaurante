package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.ClienteDAO;
import com.restaurante.backend_restaurante.DAO.TipoIdentidadDAO;
import com.restaurante.backend_restaurante.Model.Cliente;
import com.restaurante.backend_restaurante.Model.TipoIdentidad;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern DOCUMENTO = Pattern.compile("^[A-Za-z0-9-]{4,20}$");

    private final ClienteDAO clienteDAO;
    private final TipoIdentidadDAO tipoIdentidadDAO;

    public ClienteController(ClienteDAO clienteDAO, TipoIdentidadDAO tipoIdentidadDAO) {
        this.clienteDAO = clienteDAO;
        this.tipoIdentidadDAO = tipoIdentidadDAO;
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

        String error = validar(cliente, 0);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
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

        String error = validar(cliente, id);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        cliente.setCli_id(id);

        boolean actualizado = clienteDAO.update(cliente);

        if (actualizado) {
            return ResponseEntity.ok("Cliente actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el cliente o no se pudo actualizar.");
    }

    /** Valida y normaliza los datos según la tabla cliente. Devuelve el error o null. */
    private String validar(Cliente cliente, int idActual) {

        if (cliente == null
                || vacio(cliente.getCli_nombre())
                || vacio(cliente.getCli_apellidos())
                || vacio(cliente.getCli_correo())) {
            return "El nombre, los apellidos y el correo son obligatorios.";
        }

        if (cliente.getTipo_id() == null || vacio(cliente.getCli_documento())) {
            return "El tipo y el número de documento son obligatorios.";
        }

        cliente.setCli_documento(cliente.getCli_documento().trim().toUpperCase());
        cliente.setCli_nombre(cliente.getCli_nombre().trim());
        cliente.setCli_apellidos(cliente.getCli_apellidos().trim());
        cliente.setCli_correo(cliente.getCli_correo().trim());
        cliente.setCli_direccion(recortar(cliente.getCli_direccion()));
        cliente.setCli_telefono(recortar(cliente.getCli_telefono()));
        cliente.setCli_estado(vacio(cliente.getCli_estado()) ? "Activo" : cliente.getCli_estado().trim());

        TipoIdentidad tipo = tipoIdentidadDAO.getTipoIdentidadById(cliente.getTipo_id());

        if (tipo == null) {
            return "El tipo de documento seleccionado no existe.";
        }

        if (!DOCUMENTO.matcher(cliente.getCli_documento()).matches()) {
            return "El número de documento debe tener entre 4 y 20 caracteres (letras, números o guion).";
        }

        if (clienteDAO.existeDocumento(cliente.getTipo_id(), cliente.getCli_documento(), idActual)) {
            return "Ya existe un cliente con ese tipo y número de documento.";
        }

        if (cliente.getCli_nombre().length() > 60 || cliente.getCli_apellidos().length() > 60) {
            return "El nombre y los apellidos admiten máximo 60 caracteres.";
        }

        if (cliente.getCli_correo().length() > 40 || !CORREO.matcher(cliente.getCli_correo()).matches()) {
            return "Ingresa un correo válido (máximo 40 caracteres).";
        }

        if (cliente.getCli_direccion() != null && cliente.getCli_direccion().length() > 40) {
            return "La dirección admite máximo 40 caracteres.";
        }

        if (cliente.getCli_telefono() != null && cliente.getCli_telefono().length() > 40) {
            return "El teléfono admite máximo 40 caracteres.";
        }

        if (!"Activo".equalsIgnoreCase(cliente.getCli_estado())
                && !"Inactivo".equalsIgnoreCase(cliente.getCli_estado())) {
            return "El estado debe ser Activo o Inactivo.";
        }

        return null;
    }

    private boolean vacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String recortar(String valor) {
        return vacio(valor) ? null : valor.trim();
    }
}
