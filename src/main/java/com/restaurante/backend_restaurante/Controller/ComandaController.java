
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.ComandaDAO;
import com.restaurante.backend_restaurante.Model.Comanda;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comandas")
public class ComandaController {

    private final ComandaDAO comandaDAO;

    public ComandaController(ComandaDAO comandaDAO) {
        this.comandaDAO = comandaDAO;
    }

    @GetMapping
    public ResponseEntity<List<Comanda>> listarComandas() {
        return ResponseEntity.ok(comandaDAO.getAllComandas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerComandaPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID de la comanda no es válido.");
        }

        Comanda comanda = comandaDAO.getComandaById(id);

        if (comanda == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró la comanda.");
        }

        return ResponseEntity.ok(comanda);
    }

    @PostMapping
    public ResponseEntity<String> registrarComanda(
            @RequestBody Comanda comanda) {

        if (comanda.getCom_obs() == null || comanda.getCom_obs().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("La observación de la comanda es obligatoria.");
        }

        boolean registrada = comandaDAO.add(comanda);

        if (registrada) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Comanda registrada correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar la comanda.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarComanda(
            @PathVariable int id,
            @RequestBody Comanda comanda) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID de la comanda no es válido.");
        }

        if (comanda.getCom_obs() == null || comanda.getCom_obs().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("La observación de la comanda es obligatoria.");
        }

        comanda.setCom_id(id);

        boolean actualizada = comandaDAO.update(comanda);

        if (actualizada) {
            return ResponseEntity.ok("Comanda actualizada correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la comanda o no se pudo actualizar.");
    }
}
