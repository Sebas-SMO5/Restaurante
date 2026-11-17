package com.restaurante.backend_restaurante.Model;

import java.time.LocalDateTime;

/**
 * Modelos de solo lectura y de petición para la gestión de PQRS
 * (pantalla interna y página pública).
 */
public final class PqrsVista {

    private PqrsVista() {
    }

    /** PQRS completa para la pantalla interna. */
    public record Pqrs(
            int pqrs_id,
            LocalDateTime pqrs_fecha,
            String pqrs_nombre,
            String pqrs_correo,
            String pqrs_telefono,
            int tpqrs_id,
            String tipo,
            String pqrs_descripcion,
            int est_id,
            String estado,
            String pqrs_origen,
            String pqrs_respuesta,
            LocalDateTime pqrs_fecha_respuesta,
            String respondido_por
    ) {
    }

    /** Lo que ve el cliente al consultar su PQRS en la página pública (sin datos internos). */
    public record Consulta(
            int pqrs_id,
            LocalDateTime pqrs_fecha,
            String tipo,
            String estado,
            String pqrs_descripcion,
            String pqrs_respuesta,
            LocalDateTime pqrs_fecha_respuesta
    ) {
    }

    /** Petición para radicar una PQRS (interna o pública). */
    public record Nueva(
            Integer tpqrs_id,
            String pqrs_nombre,
            String pqrs_correo,
            String pqrs_telefono,
            String pqrs_descripcion,
            String sitio_web // Campo trampa: las personas no lo ven; si llega lleno, es un robot.
    ) {
    }

    /** Petición para responder una PQRS. */
    public record Respuesta(String pqrs_respuesta) {
    }

    /** Tipo de PQRS activo para la página pública. */
    public record Tipo(int tpqrs_id, String tpqrs_descripcion) {
    }
}
