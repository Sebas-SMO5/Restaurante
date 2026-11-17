package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.PqrsGestionDAO;
import com.restaurante.backend_restaurante.Model.PqrsVista;
import com.restaurante.backend_restaurante.Model.UsuarioSesion;
import com.restaurante.backend_restaurante.Service.PqrsService;
import com.restaurante.backend_restaurante.Service.ReglaNegocioException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * PQRS:
 *  - /api/pqrs/...     pantalla interna (requiere sesión).
 *  - /api/publico/...  página pública pqrs.html (sin sesión; excluida en SeguridadConfig).
 * Convive con el CRUD básico de /api/pqrs.
 */
@RestController
public class PqrsGestionController {

    private final PqrsService pqrsService;
    private final PqrsGestionDAO dao;

    public PqrsGestionController(PqrsService pqrsService, PqrsGestionDAO dao) {
        this.pqrsService = pqrsService;
        this.dao = dao;
    }

    // ----------------------------- Interno -----------------------------

    @GetMapping("/api/pqrs/resumen")
    public Map<String, Object> listar() {
        return Map.of(
                "pqrs", dao.listar(),
                "estadoPendiente", dao.estadoPendiente(),
                "estadoAtendida", dao.estadoAtendida());
    }

    @PostMapping("/api/pqrs/registrar")
    public ResponseEntity<?> registrar(@RequestBody PqrsVista.Nueva datos) {
        return ejecutar(() -> {
            int id = pqrsService.radicarInterna(datos);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("pqrs_id", id, "mensaje", "PQRS radicada con el número " + id + "."));
        });
    }

    @PutMapping("/api/pqrs/{id}/responder")
    public ResponseEntity<?> responder(
            @PathVariable int id,
            @RequestBody PqrsVista.Respuesta datos,
            HttpServletRequest request) {

        UsuarioSesion usuario = AuthController.usuarioEnSesion(request);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debes iniciar sesión para responder.");
        }

        return ejecutar(() -> {
            pqrsService.responder(id, datos, usuario.usu_id());
            return ResponseEntity.ok("Respuesta registrada. La PQRS #" + id + " quedó atendida.");
        });
    }

    // ----------------------------- Público -----------------------------

    @GetMapping("/api/publico/pqrs/tipos")
    public List<PqrsVista.Tipo> tipos() {
        return dao.tiposActivos();
    }

    @PostMapping("/api/publico/pqrs")
    public ResponseEntity<?> radicarPublica(@RequestBody PqrsVista.Nueva datos, HttpServletRequest request) {
        return ejecutar(() -> {
            int id = pqrsService.radicarPublica(datos, request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("pqrs_id", id));
        });
    }

    @GetMapping("/api/publico/pqrs/consulta")
    public ResponseEntity<?> consultar(
            @RequestParam(name = "numero", required = false) Integer numero,
            @RequestParam(name = "correo", required = false) String correo) {
        return ejecutar(() -> ResponseEntity.ok(pqrsService.consultar(numero, correo)));
    }

    // -------------------------------------------------------------------

    private ResponseEntity<?> ejecutar(Supplier<ResponseEntity<?>> accion) {
        try {
            return accion.get();
        } catch (ReglaNegocioException error) {
            return ResponseEntity.badRequest().body(error.getMessage());
        }
    }
}
