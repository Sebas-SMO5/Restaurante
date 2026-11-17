
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.PlatoDAO;
import com.restaurante.backend_restaurante.Model.Plato;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platos")
public class PlatoController {

    private final PlatoDAO platoDAO;

    public PlatoController(PlatoDAO platoDAO) {
        this.platoDAO = platoDAO;
    }

    @GetMapping
    public List<Plato> listarPlatos() {
        return platoDAO.getAllPlatos();
    }

    @PostMapping
    public ResponseEntity<String> registrarPlato(@RequestBody Plato plato) {

        String error = validar(plato);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        boolean registrado = platoDAO.add(plato);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Plato registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el plato. Verifica si la descripción ya existe.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarPlato(
            @PathVariable int id,
            @RequestBody Plato plato) {

        String error = validar(plato);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        plato.setIdPlato(id);

        boolean actualizado = platoDAO.update(plato);

        if (actualizado) {
            return ResponseEntity.ok("Plato actualizado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo actualizar el plato. Verifica si la descripción ya existe.");
    }

    /** Devuelve el mensaje de error o null si los datos son válidos. */
    private String validar(Plato plato) {

        if (plato == null || plato.getDescripcion() == null || plato.getDescripcion().isBlank()) {
            return "La descripción del plato es obligatoria.";
        }

        plato.setDescripcion(plato.getDescripcion().trim());

        if (plato.getDescripcion().length() > 40) {
            return "La descripción admite máximo 40 caracteres.";
        }

        if (plato.getPrecio() <= 0) {
            return "El precio debe ser mayor que cero.";
        }

        if (plato.getIdCategoria() == null || !platoDAO.existeCategoria(plato.getIdCategoria())) {
            return "Selecciona una categoría válida.";
        }

        if (plato.getIdEstado() <= 0 || !platoDAO.esEstadoDePlato(plato.getIdEstado())) {
            return "Selecciona un estado de plato válido (Creado, Disponible, Agotado...).";
        }

        return null;
    }
}
