
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.UsuarioDAO;
import com.restaurante.backend_restaurante.Model.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioDAO usuarioDAO;

    public UsuarioController(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        return ResponseEntity.ok(usuarioDAO.getAllUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUsuarioPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del usuario no es válido.");
        }

        Usuario usuario = usuarioDAO.getUsuarioById(id);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el usuario.");
        }

        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<String> registrarUsuario(
            @RequestBody Usuario usuario) {

        if (usuario.getUsu_nombre() == null
                || usuario.getUsu_nombre().trim().isEmpty()
                || usuario.getUsu_apellido() == null
                || usuario.getUsu_apellido().trim().isEmpty()
                || usuario.getUsu_direccion() == null
                || usuario.getUsu_direccion().trim().isEmpty()
                || usuario.getUsu_telefono() == null
                || usuario.getUsu_telefono().trim().isEmpty()
                || usuario.getUsu_correo() == null
                || usuario.getUsu_correo().trim().isEmpty()
                || usuario.getUsu_login() == null
                || usuario.getUsu_login().trim().isEmpty()
                || usuario.getUsu_pass() == null
                || usuario.getUsu_pass().trim().isEmpty()
                || usuario.getUsu_estado() == null
                || usuario.getUsu_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Completa todos los datos obligatorios del usuario.");
        }

        if (usuario.getPerf_id() != null && usuario.getPerf_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El perfil seleccionado no es válido.");
        }

        boolean registrado = usuarioDAO.add(usuario);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Usuario registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el usuario.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarUsuario(
            @PathVariable int id,
            @RequestBody Usuario usuario) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del usuario no es válido.");
        }

        if (usuario.getUsu_nombre() == null
                || usuario.getUsu_nombre().trim().isEmpty()
                || usuario.getUsu_apellido() == null
                || usuario.getUsu_apellido().trim().isEmpty()
                || usuario.getUsu_direccion() == null
                || usuario.getUsu_direccion().trim().isEmpty()
                || usuario.getUsu_telefono() == null
                || usuario.getUsu_telefono().trim().isEmpty()
                || usuario.getUsu_correo() == null
                || usuario.getUsu_correo().trim().isEmpty()
                || usuario.getUsu_login() == null
                || usuario.getUsu_login().trim().isEmpty()
                || usuario.getUsu_pass() == null
                || usuario.getUsu_pass().trim().isEmpty()
                || usuario.getUsu_estado() == null
                || usuario.getUsu_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Completa todos los datos obligatorios del usuario.");
        }

        if (usuario.getPerf_id() != null && usuario.getPerf_id() <= 0) {
            return ResponseEntity.badRequest()
                    .body("El perfil seleccionado no es válido.");
        }

        usuario.setUsu_id(id);

        boolean actualizado = usuarioDAO.update(usuario);

        if (actualizado) {
            return ResponseEntity.ok("Usuario actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el usuario o no se pudo actualizar.");
    }
}
