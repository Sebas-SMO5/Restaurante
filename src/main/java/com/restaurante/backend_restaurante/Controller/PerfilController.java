
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.PerfilDAO;
import com.restaurante.backend_restaurante.Model.Perfil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilController {

    private final PerfilDAO perfilDAO;

    public PerfilController(PerfilDAO perfilDAO) {
        this.perfilDAO = perfilDAO;
    }

    @GetMapping
    public ResponseEntity<List<Perfil>> listarPerfiles() {
        return ResponseEntity.ok(perfilDAO.getAllPerfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPerfilPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del perfil no es válido.");
        }

        Perfil perfil = perfilDAO.getPerfilById(id);

        if (perfil == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el perfil.");
        }

        return ResponseEntity.ok(perfil);
    }

    @PostMapping
    public ResponseEntity<String> registrarPerfil(@RequestBody Perfil perfil) {

        if (perfil.getPerf_descripcion() == null
                || perfil.getPerf_descripcion().trim().isEmpty()
                || perfil.getPerf_estado() == null
                || perfil.getPerf_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción y el estado del perfil son obligatorios.");
        }

        boolean registrado = perfilDAO.add(perfil);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Perfil registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el perfil.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarPerfil(
            @PathVariable int id,
            @RequestBody Perfil perfil) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del perfil no es válido.");
        }

        if (perfil.getPerf_descripcion() == null
                || perfil.getPerf_descripcion().trim().isEmpty()
                || perfil.getPerf_estado() == null
                || perfil.getPerf_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción y el estado del perfil son obligatorios.");
        }

        perfil.setPerf_id(id);

        boolean actualizado = perfilDAO.update(perfil);

        if (actualizado) {
            return ResponseEntity.ok("Perfil actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el perfil o no se pudo actualizar.");
    }
}
