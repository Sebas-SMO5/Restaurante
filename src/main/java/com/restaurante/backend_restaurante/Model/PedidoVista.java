package com.restaurante.backend_restaurante.Model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelos de solo lectura y de petición para la pantalla de Pedidos y la de Cocina.
 * Agrupa varios "record" pequeños para no llenar el paquete Model de archivos.
 */
public final class PedidoVista {

    private PedidoVista() {
    }

    /** Fila del listado de pedidos. */
    public record Resumen(
            int ped_id,
            LocalDateTime ped_fecha,
            String ped_mesa,
            Integer usu_id,
            String mesero,
            int est_id,
            String estado,
            int items,
            int pendientes,
            double total
    ) {
    }

    /** Un plato dentro del pedido (también es la comanda que ve la cocina). */
    public record Item(
            int ped_det_id,
            int ped_id,
            int pla_id,
            String plato,
            int ped_det_cant,
            double ped_det_precio,
            double subtotal,
            String ped_det_obser,
            int est_id,
            String estado
    ) {
    }

    /** Pedido completo: encabezado + platos. */
    public record Detalle(Resumen pedido, List<Item> items) {
    }

    /** Comanda pendiente en cocina. */
    public record Comanda(
            int ped_det_id,
            int ped_id,
            String ped_mesa,
            LocalDateTime ped_fecha,
            String mesero,
            String plato,
            int ped_det_cant,
            String ped_det_obser
    ) {
    }

    /** Plato que se agrega a un pedido. */
    public record ItemNuevo(Integer pla_id, Integer ped_det_cant, String ped_det_obser) {
    }

    /** Petición para crear un pedido con sus platos. */
    public record NuevoPedido(String ped_mesa, List<ItemNuevo> items) {
    }

    /** Petición para agregar platos a un pedido abierto. */
    public record NuevosItems(List<ItemNuevo> items) {
    }

    /** Acción sobre un pedido o un plato: "finalizar", "cancelar", "listo". */
    public record Accion(String accion) {
    }
}
