
package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.InsumoDAO;
import com.restaurante.backend_restaurante.Model.Insumo;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

    private final InsumoDAO insumoDAO;

    public InsumoController(InsumoDAO insumoDAO) {
        this.insumoDAO = insumoDAO;
    }

    @GetMapping
    public ResponseEntity<List<Insumo>> listarInsumos() {
        return ResponseEntity.ok(insumoDAO.getAllInsumos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerInsumoPorId(@PathVariable long id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código del insumo no es válido.");
        }

        Insumo insumo = insumoDAO.getInsumoById(id);

        if (insumo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el insumo.");
        }

        return ResponseEntity.ok(insumo);
    }

    @PostMapping
    public ResponseEntity<String> registrarInsumo(
            @RequestBody Insumo insumo) {

        if (insumo.getIns_id() <= 0
                || insumo.getSede_id() <= 0
                || insumo.getCins_id() <= 0
                || insumo.getPres_id() <= 0) {

            return ResponseEntity.badRequest()
                    .body("Los códigos del insumo, sede, categoría y presentación deben ser válidos.");
        }

        if (insumo.getIns_nombre() == null
                || insumo.getIns_nombre().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("El nombre del insumo es obligatorio.");
        }

        if (insumo.getIns_stock() == null
                || insumo.getIns_stock().signum() < 0
                || insumo.getIns_stock_min() == null
                || insumo.getIns_stock_min().signum() < 0) {

            return ResponseEntity.badRequest()
                    .body("Las existencias y el stock mínimo no pueden ser negativos ni estar vacíos.");
        }

        if (insumo.getIns_precio_compra() != null
                && insumo.getIns_precio_compra().signum() < 0) {

            return ResponseEntity.badRequest()
                    .body("El precio de compra no puede ser negativo.");
        }

        boolean registrado = insumoDAO.add(insumo);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Insumo registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el insumo.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarInsumo(
            @PathVariable long id,
            @RequestBody Insumo insumo) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código del insumo no es válido.");
        }

        if (insumo.getIns_nombre() == null
                || insumo.getIns_nombre().isBlank()) {

            return ResponseEntity.badRequest()
                    .body("El nombre del insumo es obligatorio.");
        }

        if (insumo.getIns_stock() == null
                || insumo.getIns_stock().signum() < 0
                || insumo.getIns_stock_min() == null
                || insumo.getIns_stock_min().signum() < 0) {

            return ResponseEntity.badRequest()
                    .body("Las existencias y el stock mínimo no pueden ser negativos ni estar vacíos.");
        }

        if (insumo.getIns_precio_compra() != null
                && insumo.getIns_precio_compra().signum() < 0) {

            return ResponseEntity.badRequest()
                    .body("El precio de compra no puede ser negativo.");
        }

        insumo.setIns_id(id);

        boolean actualizado = insumoDAO.update(insumo);

        if (actualizado) {
            return ResponseEntity.ok("Insumo actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el insumo o no se pudo actualizar.");
    }
}
