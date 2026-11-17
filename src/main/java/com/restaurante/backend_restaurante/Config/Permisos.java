package com.restaurante.backend_restaurante.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Reglas de permisos por rol.
 *
 * - Los roles administradores se configuran en application.properties
 *   (app.seguridad.roles-admin) y tienen acceso completo.
 * - Los demás roles (Auxiliar y futuros empleados) tienen acceso limitado:
 *     · SOLO_ADMIN:       no pueden ni consultar.
 *     · ESCRITURA_ADMIN:  pueden consultar, pero no crear ni modificar.
 *     · Cualquier otra ruta /api/** queda disponible para el trabajo diario
 *       (clientes, materias primas, pedidos, comandas, recibos de caja, PQRS).
 */
@Component
public class Permisos {

    /** Rutas a las que solo puede entrar un administrador. */
    public static final List<String> SOLO_ADMIN = List.of(
            "/api/usuarios"
    );

    /** Rutas que todos pueden consultar (GET), pero solo un administrador modifica. */
    public static final List<String> ESCRITURA_ADMIN = List.of(
            "/api/perfiles",
            "/api/tipos-pqrs",
            "/api/tipos-identidad",
            "/api/categorias",
            "/api/categorias-insumo",
            "/api/platos",
            "/api/unidades-medida",
            "/api/conceptos",
            "/api/formas-pago",
            "/api/estados",
            "/api/tipos-estado",
            "/api/egresos"
    );

    private final List<String> rolesAdmin;

    public Permisos(@Value("${app.seguridad.roles-admin:Admin,Administrador}") String rolesAdmin) {
        this.rolesAdmin = Arrays.stream(rolesAdmin.split(","))
                .map(String::trim)
                .filter(rol -> !rol.isEmpty())
                .map(String::toLowerCase)
                .toList();
    }

    /** Indica si la descripción de un rol corresponde a un rol administrador. */
    public boolean esRolAdmin(String descripcionRol) {
        return descripcionRol != null && rolesAdmin.contains(descripcionRol.trim().toLowerCase());
    }

    /** Devuelve null si el acceso está permitido, o el mensaje de rechazo. */
    public String validarAcceso(String ruta, String metodo, boolean esAdmin) {

        if (esAdmin) {
            return null;
        }

        if (coincide(ruta, SOLO_ADMIN)) {
            return "No tienes permiso para acceder a este módulo. Solo un administrador puede hacerlo.";
        }

        boolean esConsulta = "GET".equalsIgnoreCase(metodo) || "HEAD".equalsIgnoreCase(metodo);

        if (!esConsulta && coincide(ruta, ESCRITURA_ADMIN)) {
            return "Tu rol solo permite consultar esta información. Solo un administrador puede modificarla.";
        }

        return null;
    }

    private boolean coincide(String ruta, List<String> bases) {
        return bases.stream().anyMatch(base -> ruta.equals(base) || ruta.startsWith(base + "/"));
    }
}
