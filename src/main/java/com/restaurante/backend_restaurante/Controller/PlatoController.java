
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

        plato.setIdPlato(id);

        boolean actualizado = platoDAO.update(plato);

        if (actualizado) {
            return ResponseEntity.ok("Plato actualizado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo actualizar el plato.");
    }
}
