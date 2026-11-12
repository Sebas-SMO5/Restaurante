
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.TipoIdentidadDAO;
import com.restaurante.backend_restaurante.Model.TipoIdentidad;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-identidad")
public class TipoIdentidadController {

    private final TipoIdentidadDAO tipoIdentidadDAO;

    public TipoIdentidadController(TipoIdentidadDAO tipoIdentidadDAO) {
        this.tipoIdentidadDAO = tipoIdentidadDAO;
    }

    @GetMapping
    public ResponseEntity<List<TipoIdentidad>> listarTiposIdentidad() {
        return ResponseEntity.ok(tipoIdentidadDAO.getAllTiposIdentidad());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTipoIdentidadPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del tipo de identidad no es válido.");
        }

        TipoIdentidad tipoIdentidad = tipoIdentidadDAO.getTipoIdentidadById(id);

        if (tipoIdentidad == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el tipo de identidad.");
        }

        return ResponseEntity.ok(tipoIdentidad);
    }

    @PostMapping
    public ResponseEntity<String> registrarTipoIdentidad(
            @RequestBody TipoIdentidad tipoIdentidad) {

        if (tipoIdentidad.getTipo_descripcion() == null
                || tipoIdentidad.getTipo_descripcion().trim().isEmpty()
                || tipoIdentidad.getTipo_sigla() == null
                || tipoIdentidad.getTipo_sigla().trim().isEmpty()
                || tipoIdentidad.getTipo_estado() == null
                || tipoIdentidad.getTipo_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción, la sigla y el estado son obligatorios.");
        }

        boolean registrado = tipoIdentidadDAO.add(tipoIdentidad);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Tipo de identidad registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el tipo de identidad.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarTipoIdentidad(
            @PathVariable int id,
            @RequestBody TipoIdentidad tipoIdentidad) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del tipo de identidad no es válido.");
        }

        if (tipoIdentidad.getTipo_descripcion() == null
                || tipoIdentidad.getTipo_descripcion().trim().isEmpty()
                || tipoIdentidad.getTipo_sigla() == null
                || tipoIdentidad.getTipo_sigla().trim().isEmpty()
                || tipoIdentidad.getTipo_estado() == null
                || tipoIdentidad.getTipo_estado().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("La descripción, la sigla y el estado son obligatorios.");
        }

        tipoIdentidad.setTipo_id(id);

        boolean actualizado = tipoIdentidadDAO.update(tipoIdentidad);

        if (actualizado) {
            return ResponseEntity.ok("Tipo de identidad actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el tipo de identidad o no se pudo actualizar.");
    }
}
