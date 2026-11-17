package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.Config.Permisos;
import com.restaurante.backend_restaurante.DAO.PerfilDAO;
import com.restaurante.backend_restaurante.DAO.UsuarioDAO;
import com.restaurante.backend_restaurante.Model.LoginRequest;
import com.restaurante.backend_restaurante.Model.Perfil;
import com.restaurante.backend_restaurante.Model.ReporteVista;
import com.restaurante.backend_restaurante.Model.Usuario;
import com.restaurante.backend_restaurante.Model.UsuarioSesion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String CREDENCIALES_INVALIDAS = "Usuario o contraseña incorrectos.";

    private final UsuarioDAO usuarioDAO;
    private final PerfilDAO perfilDAO;
    private final PasswordEncoder passwordEncoder;
    private final Permisos permisos;

    public AuthController(
            UsuarioDAO usuarioDAO,
            PerfilDAO perfilDAO,
            PasswordEncoder passwordEncoder,
            Permisos permisos) {
        this.usuarioDAO = usuarioDAO;
        this.perfilDAO = perfilDAO;
        this.passwordEncoder = passwordEncoder;
        this.permisos = permisos;
    }

    @PostMapping("/login")
    public ResponseEntity<?> iniciarSesion(
            @RequestBody LoginRequest datos,
            HttpServletRequest request) {

        if (datos == null
                || datos.usu_login() == null || datos.usu_login().trim().isEmpty()
                || datos.usu_pass() == null || datos.usu_pass().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Ingresa el usuario y la contraseña.");
        }

        Usuario usuario = usuarioDAO.getCredencialesPorLogin(datos.usu_login().trim());

        if (usuario == null || !passCorrecta(datos.usu_pass(), usuario.getUsu_pass())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CREDENCIALES_INVALIDAS);
        }

        if (!"activo".equalsIgnoreCase(usuario.getUsu_estado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Tu usuario está inactivo. Comunícate con el administrador.");
        }

        Perfil perfil = usuario.getPerf_id() != null
                ? perfilDAO.getPerfilById(usuario.getPerf_id())
                : null;

        if (perfil != null && !"activo".equalsIgnoreCase(perfil.getPerf_estado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("El rol asignado a tu usuario está inactivo. Comunícate con el administrador.");
        }

        // Las contraseñas antiguas en texto plano se cifran al iniciar sesión.
        if (!esBcrypt(usuario.getUsu_pass())) {
            usuarioDAO.actualizarPass(usuario.getUsu_id(), passwordEncoder.encode(datos.usu_pass()));
        }

        // Se crea una sesión nueva para evitar reutilizar un identificador anterior.
        HttpSession sesionAnterior = request.getSession(false);
        if (sesionAnterior != null) {
            sesionAnterior.invalidate();
        }

        UsuarioSesion usuarioSesion = new UsuarioSesion(
                usuario.getUsu_id(),
                usuario.getUsu_nombre(),
                usuario.getUsu_apellido(),
                usuario.getUsu_login(),
                usuario.getPerf_id(),
                perfil != null ? perfil.getPerf_descripcion() : null,
                perfil != null && permisos.esRolAdmin(perfil.getPerf_descripcion())
        );

        request.getSession(true).setAttribute(UsuarioSesion.ATRIBUTO, usuarioSesion);

        return ResponseEntity.ok(usuarioSesion);
    }

    @GetMapping("/sesion")
    public ResponseEntity<?> obtenerSesion(HttpServletRequest request) {

        UsuarioSesion usuarioSesion = usuarioEnSesion(request);

        if (usuarioSesion == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("No hay una sesión iniciada.");
        }

        return ResponseEntity.ok(usuarioSesion);
    }

    /** Cada usuario cambia su propia contraseña; debe confirmar la actual. */
    @PutMapping("/mi-contrasena")
    public ResponseEntity<String> cambiarMiContrasena(
            @RequestBody ReporteVista.CambioContrasena datos,
            HttpServletRequest request) {

        UsuarioSesion sesion = usuarioEnSesion(request);

        if (sesion == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debes iniciar sesión.");
        }

        if (datos == null || datos.actual() == null || datos.actual().isEmpty()
                || datos.nueva() == null || datos.nueva().isEmpty()) {
            return ResponseEntity.badRequest().body("Escribe la contraseña actual y la nueva.");
        }

        if (datos.nueva().length() < 6 || datos.nueva().length() > 72) {
            return ResponseEntity.badRequest().body("La nueva contraseña debe tener entre 6 y 72 caracteres.");
        }

        if (datos.nueva().equals(datos.actual())) {
            return ResponseEntity.badRequest().body("La nueva contraseña debe ser diferente a la actual.");
        }

        Usuario usuario = usuarioDAO.getUsuarioById(sesion.usu_id());
        Usuario credenciales = usuario == null ? null : usuarioDAO.getCredencialesPorLogin(usuario.getUsu_login());

        if (credenciales == null || !passCorrecta(datos.actual(), credenciales.getUsu_pass())) {
            return ResponseEntity.badRequest().body("La contraseña actual no es correcta.");
        }

        usuarioDAO.actualizarPass(sesion.usu_id(), passwordEncoder.encode(datos.nueva()));

        return ResponseEntity.ok("Contraseña actualizada correctamente.");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> cerrarSesion(HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion != null) {
            sesion.invalidate();
        }

        return ResponseEntity.ok("Sesión cerrada correctamente.");
    }

    public static UsuarioSesion usuarioEnSesion(HttpServletRequest request) {

        HttpSession sesion = request.getSession(false);

        if (sesion == null) {
            return null;
        }

        return sesion.getAttribute(UsuarioSesion.ATRIBUTO) instanceof UsuarioSesion usuarioSesion
                ? usuarioSesion
                : null;
    }

    private boolean passCorrecta(String passIngresada, String passGuardada) {

        if (passGuardada == null || passGuardada.isEmpty()) {
            return false;
        }

        if (esBcrypt(passGuardada)) {
            return passwordEncoder.matches(passIngresada, passGuardada);
        }

        // Compatibilidad con contraseñas antiguas guardadas sin cifrar.
        return MessageDigest.isEqual(
                passIngresada.getBytes(StandardCharsets.UTF_8),
                passGuardada.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean esBcrypt(String valor) {
        return valor != null && valor.matches("^\\$2[aby]\\$\\d{2}\\$.{53}$");
    }
}
