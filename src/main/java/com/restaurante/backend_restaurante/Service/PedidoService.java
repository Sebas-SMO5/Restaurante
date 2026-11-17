package com.restaurante.backend_restaurante.Service;

import com.restaurante.backend_restaurante.Config.EstadosConfig;
import com.restaurante.backend_restaurante.DAO.PedidoGestionDAO;
import com.restaurante.backend_restaurante.Model.PedidoVista;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reglas del flujo de pedidos:
 *   Pedido creado → platos "En preparación" en cocina → cocina los marca "listos"
 *   → el pedido se finaliza (queda listo para cobrar) o se cancela.
 *
 * Los métodos que escriben son transaccionales: si un plato falla, no se guarda nada.
 * Los errores de negocio se lanzan como ReglaNegocioException con un mensaje para el usuario.
 */
@Service
public class PedidoService {

    public static final int MAX_CANTIDAD = 50;

    private final PedidoGestionDAO dao;
    private final EstadosConfig estados;

    public PedidoService(PedidoGestionDAO dao, EstadosConfig estados) {
        this.dao = dao;
        this.estados = estados;
    }

    public PedidoVista.Detalle detalle(int pedId) {
        PedidoVista.Resumen pedido = dao.obtener(pedId);

        if (pedido == null) {
            throw new ReglaNegocioException("No se encontró el pedido.");
        }

        return new PedidoVista.Detalle(pedido, dao.items(pedId));
    }

    @Transactional
    public int crear(PedidoVista.NuevoPedido datos, int usuId) {

        if (datos == null || datos.ped_mesa() == null || datos.ped_mesa().isBlank()) {
            throw new ReglaNegocioException("Indica el número de la mesa.");
        }

        String mesa = datos.ped_mesa().trim();

        if (!mesa.matches("^[A-Za-z0-9]{1,4}$")) {
            throw new ReglaNegocioException("La mesa admite máximo 4 caracteres (letras o números).");
        }

        Integer abierto = dao.pedidoAbiertoDeMesa(mesa);

        if (abierto != null) {
            throw new ReglaNegocioException(
                    "La mesa " + mesa + " ya tiene el pedido #" + abierto + " abierto. Agrega los platos a ese pedido.");
        }

        validarItems(datos.items());

        int pedId = dao.insertarPedido(mesa, usuId);
        insertarItems(pedId, datos.items());

        return pedId;
    }

    @Transactional
    public void agregarItems(int pedId, List<PedidoVista.ItemNuevo> items) {
        exigirPedidoAbierto(pedId);
        validarItems(items);
        insertarItems(pedId, items);
    }

    /** Finaliza un pedido: todos sus platos deben estar listos o cancelados. */
    @Transactional
    public void finalizar(int pedId) {
        exigirPedidoAbierto(pedId);

        int enPreparacion = dao.contarItemsEnEstado(pedId, estados.detallePreparacion);

        if (enPreparacion > 0) {
            throw new ReglaNegocioException(
                    "Aún hay " + enPreparacion + " plato(s) en preparación. La cocina debe marcarlos como listos.");
        }

        if (dao.contarItemsEnEstado(pedId, estados.detalleListo) == 0) {
            throw new ReglaNegocioException("El pedido no tiene platos entregados. Si no se va a cobrar, cancélalo.");
        }

        dao.cambiarEstadoPedido(pedId, estados.pedidoFinalizado);
    }

    /** Cancela el pedido y los platos que aún estaban en preparación. */
    @Transactional
    public void cancelar(int pedId) {
        exigirPedidoAbierto(pedId);
        dao.cambiarEstadoItems(pedId, estados.detallePreparacion, estados.detalleCancelado);
        dao.cambiarEstadoPedido(pedId, estados.pedidoCancelado);
    }

    /** Cocina: marca un plato como listo. */
    @Transactional
    public void marcarListo(int pedDetId) {
        exigirItemEnPreparacion(pedDetId);
        dao.cambiarEstadoItem(pedDetId, estados.detalleListo);
    }

    /** Cancela un plato que aún no ha salido de cocina. */
    @Transactional
    public void cancelarItem(int pedDetId) {
        exigirItemEnPreparacion(pedDetId);
        dao.cambiarEstadoItem(pedDetId, estados.detalleCancelado);
    }

    // ---------------------------------------------------------------

    private void exigirPedidoAbierto(int pedId) {
        Integer estado = dao.estadoPedido(pedId);

        if (estado == null) {
            throw new ReglaNegocioException("No se encontró el pedido.");
        }

        if (estado != estados.pedidoCreado) {
            throw new ReglaNegocioException("El pedido ya está cerrado (finalizado o cancelado) y no se puede modificar.");
        }
    }

    /** Devuelve {ped_id, est_id} si el plato existe, está en preparación y su pedido sigue abierto. */
    private int[] exigirItemEnPreparacion(int pedDetId) {
        int[] item = dao.datosItem(pedDetId);

        if (item == null) {
            throw new ReglaNegocioException("No se encontró el plato del pedido.");
        }

        exigirPedidoAbierto(item[0]);

        if (item[1] != estados.detallePreparacion) {
            throw new ReglaNegocioException("Ese plato ya no está en preparación.");
        }

        return item;
    }

    private void validarItems(List<PedidoVista.ItemNuevo> items) {

        if (items == null || items.isEmpty()) {
            throw new ReglaNegocioException("Agrega al menos un plato al pedido.");
        }

        for (PedidoVista.ItemNuevo item : items) {
            if (item == null || item.pla_id() == null || item.pla_id() <= 0) {
                throw new ReglaNegocioException("Hay un plato no válido en el pedido.");
            }

            if (item.ped_det_cant() == null || item.ped_det_cant() < 1 || item.ped_det_cant() > MAX_CANTIDAD) {
                throw new ReglaNegocioException("La cantidad de cada plato debe estar entre 1 y " + MAX_CANTIDAD + ".");
            }

            if (item.ped_det_obser() != null && item.ped_det_obser().trim().length() > 100) {
                throw new ReglaNegocioException("La observación de cada plato admite máximo 100 caracteres.");
            }

            if (dao.precioPlatoDisponible(item.pla_id()) == null) {
                throw new ReglaNegocioException(
                        "Uno de los platos no está disponible para la venta (código " + item.pla_id() + ").");
            }
        }
    }

    /** Guarda los platos con el precio vigente del plato en este momento. */
    private void insertarItems(int pedId, List<PedidoVista.ItemNuevo> items) {
        for (PedidoVista.ItemNuevo item : items) {
            String observacion = item.ped_det_obser() == null || item.ped_det_obser().isBlank()
                    ? null
                    : item.ped_det_obser().trim();

            dao.insertarItem(pedId, item.pla_id(), item.ped_det_cant(),
                    dao.precioPlatoDisponible(item.pla_id()), observacion);
        }
    }
}
