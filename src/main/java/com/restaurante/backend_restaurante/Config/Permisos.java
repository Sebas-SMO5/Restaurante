package com.restaurante.backend_restaurante.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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

    /**
     * Rutas genéricas (CRUD antiguo) de pedidos, recibos, comandas y PQRS.
     * Escribir por ellas se salta las reglas del negocio (por ejemplo, cambiar el total
     * de un recibo o reabrir un pedido cerrado), así que solo un administrador puede usarlas.
     * El trabajo diario va por las rutas del flujo: /api/pedidos/completo, /api/recibos-caja/cobrar,
     * /api/pqrs/registrar, etc., que no coinciden con estos patrones.
     */
    public static final List<Pattern> ESCRITURA_DIRECTA_ADMIN = List.of(
            Pattern.compile("^/api/pedidos(/\\d+)?/?$"),
            Pattern.compile("^/api/pedidos-detalle(/\\d+)?/?$"),
            Pattern.compile("^/api/recibos-caja(/\\d+)?/?$"),
            Pattern.compile("^/api/recibos-caja-detalle(/.*)?$"),
            Pattern.compile("^/api/comandas(/.*)?$"),
            Pattern.compile("^/api/pqrs(/\\d+)?/?$")
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

        if (!esConsulta && ESCRITURA_DIRECTA_ADMIN.stream().anyMatch(patron -> patron.matcher(ruta).matches())) {
            return "Esta operación se hace desde la pantalla correspondiente del sistema (Pedidos, Cocina, Recibos o PQRS).";
        }

        return null;
    }

    private boolean coincide(String ruta, List<String> bases) {
        return bases.stream().anyMatch(base -> ruta.equals(base) || ruta.startsWith(base + "/"));
    }
}
