package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.PerfilDAO;
import com.restaurante.backend_restaurante.DAO.UsuarioDAO;
import com.restaurante.backend_restaurante.Model.Usuario;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private static final int PASS_MIN = 6;
    private static final int PASS_MAX = 72; // Límite de BCrypt
    private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern LOGIN = Pattern.compile("^[A-Za-z0-9._-]+$");

    private final UsuarioDAO usuarioDAO;
    private final PerfilDAO perfilDAO;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioDAO usuarioDAO, PerfilDAO perfilDAO, PasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.perfilDAO = perfilDAO;
        this.passwordEncoder = passwordEncoder;
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
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario usuario) {

        String error = validarDatos(usuario, 0, true);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        usuario.setUsu_pass(passwordEncoder.encode(usuario.getUsu_pass()));

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

        boolean cambiaPass = usuario.getUsu_pass() != null && !usuario.getUsu_pass().isEmpty();

        String error = validarDatos(usuario, id, cambiaPass);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        usuario.setUsu_id(id);
        usuario.setUsu_pass(cambiaPass ? passwordEncoder.encode(usuario.getUsu_pass()) : null);

        boolean actualizado = usuarioDAO.update(usuario);

        if (actualizado) {
            return ResponseEntity.ok("Usuario actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el usuario o no se pudo actualizar.");
    }

    /**
     * Valida los datos del usuario según las columnas de la tabla usuario.
     * Devuelve el mensaje de error o null si todo está correcto.
     */
    private String validarDatos(Usuario usuario, int idActual, boolean validarPass) {

        if (usuario == null
                || vacio(usuario.getUsu_nombre())
                || vacio(usuario.getUsu_apellido())
                || vacio(usuario.getUsu_direccion())
                || vacio(usuario.getUsu_telefono())
                || vacio(usuario.getUsu_correo())
                || vacio(usuario.getUsu_login())
                || vacio(usuario.getUsu_estado())) {
            return "Completa todos los datos obligatorios del usuario.";
        }

        usuario.setUsu_nombre(usuario.getUsu_nombre().trim());
        usuario.setUsu_apellido(usuario.getUsu_apellido().trim());
        usuario.setUsu_direccion(usuario.getUsu_direccion().trim());
        usuario.setUsu_telefono(usuario.getUsu_telefono().trim());
        usuario.setUsu_correo(usuario.getUsu_correo().trim());
        usuario.setUsu_login(usuario.getUsu_login().trim());
        usuario.setUsu_estado(usuario.getUsu_estado().trim());

        if (usuario.getUsu_nombre().length() > 60 || usuario.getUsu_apellido().length() > 60) {
            return "El nombre y el apellido admiten máximo 60 caracteres.";
        }

        if (usuario.getUsu_direccion().length() > 100) {
            return "La dirección admite máximo 100 caracteres.";
        }

        if (usuario.getUsu_telefono().length() > 20) {
            return "El teléfono admite máximo 20 caracteres.";
        }

        if (usuario.getUsu_correo().length() > 70 || !CORREO.matcher(usuario.getUsu_correo()).matches()) {
            return "Ingresa un correo válido (máximo 70 caracteres).";
        }

        if (usuario.getUsu_login().length() < 3 || usuario.getUsu_login().length() > 30
                || !LOGIN.matcher(usuario.getUsu_login()).matches()) {
            return "El usuario (login) debe tener entre 3 y 30 caracteres, sin espacios: letras, números, punto, guion o guion bajo.";
        }

        if (usuarioDAO.existeLogin(usuario.getUsu_login(), idActual)) {
            return "Ya existe otro usuario con ese login.";
        }

        if (!"Activo".equalsIgnoreCase(usuario.getUsu_estado())
                && !"Inactivo".equalsIgnoreCase(usuario.getUsu_estado())) {
            return "El estado debe ser Activo o Inactivo.";
        }

        if (usuario.getPerf_id() != null
                && (usuario.getPerf_id() <= 0 || perfilDAO.getPerfilById(usuario.getPerf_id()) == null)) {
            return "El rol seleccionado no es válido.";
        }

        if (validarPass) {
            String pass = usuario.getUsu_pass();

            if (pass == null || pass.length() < PASS_MIN || pass.length() > PASS_MAX) {
                return "La contraseña debe tener entre " + PASS_MIN + " y " + PASS_MAX + " caracteres.";
            }
        }

        return null;
    }

    private boolean vacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
