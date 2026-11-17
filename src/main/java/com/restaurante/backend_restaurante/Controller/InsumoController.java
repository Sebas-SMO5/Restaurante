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
    public ResponseEntity<String> registrarInsumo(@RequestBody Insumo insumo) {

        String error = validar(insumo, 0);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        boolean registrado = insumoDAO.add(insumo);

        if (registrado) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Materia prima registrada correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar la materia prima.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarInsumo(
            @PathVariable long id,
            @RequestBody Insumo insumo) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El código del insumo no es válido.");
        }

        String error = validar(insumo, id);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        insumo.setIns_id(id);

        boolean actualizado = insumoDAO.update(insumo);

        if (actualizado) {
            return ResponseEntity.ok("Materia prima actualizada correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró la materia prima o no se pudo actualizar.");
    }

    /** Valida los datos según la tabla insumo. Devuelve el error o null. */
    private String validar(Insumo insumo, long idActual) {

        if (insumo == null || insumo.getIns_nombre() == null || insumo.getIns_nombre().isBlank()) {
            return "El nombre de la materia prima es obligatorio.";
        }

        insumo.setIns_nombre(insumo.getIns_nombre().trim());
        insumo.setIns_codigo(recortar(insumo.getIns_codigo()));
        insumo.setIns_codigo_barras(recortar(insumo.getIns_codigo_barras()));
        insumo.setIns_imagen(recortar(insumo.getIns_imagen()));

        if (insumo.getIns_nombre().length() > 150) {
            return "El nombre admite máximo 150 caracteres.";
        }

        if (insumoDAO.existeNombre(insumo.getIns_nombre(), idActual)) {
            return "Ya existe una materia prima con ese nombre.";
        }

        if (insumo.getCins_id() <= 0 || !insumoDAO.existeCategoria(insumo.getCins_id())) {
            return "Selecciona una categoría de insumo válida.";
        }

        if (insumo.getUm_id() <= 0 || !insumoDAO.existeUnidad(insumo.getUm_id())) {
            return "Selecciona una unidad de medida válida.";
        }

        if (insumo.getIns_stock() == null || insumo.getIns_stock().signum() < 0
                || insumo.getIns_stock_min() == null || insumo.getIns_stock_min().signum() < 0) {
            return "Las existencias y el stock mínimo no pueden ser negativos ni estar vacíos.";
        }

        if (insumo.getIns_precio_compra() != null && insumo.getIns_precio_compra().signum() < 0) {
            return "El precio de compra no puede ser negativo.";
        }

        if (insumo.getIns_codigo() != null && insumo.getIns_codigo().length() > 30) {
            return "El código interno admite máximo 30 caracteres.";
        }

        if (insumo.getIns_codigo_barras() != null && insumo.getIns_codigo_barras().length() > 50) {
            return "El código de barras admite máximo 50 caracteres.";
        }

        return null;
    }

    private String recortar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
