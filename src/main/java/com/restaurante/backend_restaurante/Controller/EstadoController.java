package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.EstadoDAO;
import com.restaurante.backend_restaurante.Model.Estado;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estados")
public class EstadoController {

    private final EstadoDAO estadoDAO;

    public EstadoController(EstadoDAO estadoDAO) {
        this.estadoDAO = estadoDAO;
    }

    /**
     * Lista los estados. Con ?tipo=3 devuelve solo los de ese tipo de estado
     * (por ejemplo, los estados de Plato).
     */
    @GetMapping
    public ResponseEntity<List<Estado>> listarEstados(
            @RequestParam(name = "tipo", required = false) Integer tipo) {

        return ResponseEntity.ok(tipo == null
                ? estadoDAO.getAllEstados()
                : estadoDAO.getEstadosPorTipo(tipo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEstadoPorId(@PathVariable int id) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del estado no es válido.");
        }

        Estado estado = estadoDAO.getEstadoById(id);

        if (estado == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró el estado.");
        }

        return ResponseEntity.ok(estado);
    }

    @PostMapping
    public ResponseEntity<String> registrarEstado(@RequestBody Estado estado) {

        String error = validar(estado, 0);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        if (estadoDAO.add(estado)) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Estado registrado correctamente.");
        }

        return ResponseEntity.badRequest()
                .body("No se pudo registrar el estado.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarEstado(
            @PathVariable int id,
            @RequestBody Estado estado) {

        if (id <= 0) {
            return ResponseEntity.badRequest()
                    .body("El ID del estado no es válido.");
        }

        String error = validar(estado, id);

        if (error != null) {
            return ResponseEntity.badRequest().body(error);
        }

        estado.setEst_id(id);

        if (estadoDAO.update(estado)) {
            return ResponseEntity.ok("Estado actualizado correctamente.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se encontró el estado o no se pudo actualizar.");
    }

    /** Devuelve el mensaje de error o null si los datos son válidos. */
    private String validar(Estado estado, int idActual) {

        if (estado == null
                || estado.getEst_descripcion() == null || estado.getEst_descripcion().isBlank()
                || estado.getEst_estado() == null || estado.getEst_estado().isBlank()) {
            return "La descripción y el estado son obligatorios.";
        }

        estado.setEst_descripcion(estado.getEst_descripcion().trim());
        estado.setEst_estado(estado.getEst_estado().trim());

        if (estado.getEst_descripcion().length() > 40) {
            return "La descripción admite máximo 40 caracteres.";
        }

        if (!"Activo".equalsIgnoreCase(estado.getEst_estado())
                && !"Inactivo".equalsIgnoreCase(estado.getEst_estado())) {
            return "El estado debe ser Activo o Inactivo.";
        }

        if (estado.getTes_id() <= 0 || !estadoDAO.existeTipo(estado.getTes_id())) {
            return "Debes seleccionar un tipo de estado válido.";
        }

        if (estadoDAO.existeDescripcion(estado.getTes_id(), estado.getEst_descripcion(), idActual)) {
            return "Ya existe un estado con esa descripción en el mismo tipo.";
        }

        return null;
    }
}
