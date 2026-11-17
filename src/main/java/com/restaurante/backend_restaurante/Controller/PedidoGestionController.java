package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.Config.EstadosConfig;
import com.restaurante.backend_restaurante.DAO.PedidoGestionDAO;
import com.restaurante.backend_restaurante.Model.PedidoVista;
import com.restaurante.backend_restaurante.Model.UsuarioSesion;
import com.restaurante.backend_restaurante.Service.PedidoService;
import com.restaurante.backend_restaurante.Service.ReglaNegocioException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Flujo de pedidos para la pantalla "Pedidos" y la pantalla "Cocina".
 * Convive con el CRUD básico de /api/pedidos y /api/pedidos-detalle.
 */
@RestController
public class PedidoGestionController {

    private final PedidoService pedidoService;
    private final PedidoGestionDAO dao;
    private final EstadosConfig estados;

    public PedidoGestionController(PedidoService pedidoService, PedidoGestionDAO dao, EstadosConfig estados) {
        this.pedidoService = pedidoService;
        this.dao = dao;
        this.estados = estados;
    }

    /** Listado con mesero, estado, cantidad de platos y total. ?estado=4 filtra por estado. */
    @GetMapping("/api/pedidos/resumen")
    public List<PedidoVista.Resumen> listar(@RequestParam(name = "estado", required = false) Integer estado) {
        return dao.listar(estado);
    }

    /** Códigos de estado del flujo, para que el frontend no los tenga escritos a mano. */
    @GetMapping("/api/pedidos/estados-flujo")
    public Map<String, Integer> estadosFlujo() {
        return Map.of(
                "pedidoCreado", estados.pedidoCreado,
                "pedidoFinalizado", estados.pedidoFinalizado,
                "pedidoCancelado", estados.pedidoCancelado,
                "detallePreparacion", estados.detallePreparacion,
                "detalleListo", estados.detalleListo,
                "detalleCancelado", estados.detalleCancelado,
                "platoDisponible", estados.platoDisponible);
    }

    @GetMapping("/api/pedidos/{id}/completo")
    public ResponseEntity<?> detalle(@PathVariable int id) {
        return ejecutar(() -> ResponseEntity.ok(pedidoService.detalle(id)));
    }

    @PostMapping("/api/pedidos/completo")
    public ResponseEntity<?> crear(@RequestBody PedidoVista.NuevoPedido datos, HttpServletRequest request) {
        UsuarioSesion usuario = AuthController.usuarioEnSesion(request);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debes iniciar sesión para registrar pedidos.");
        }

        return ejecutar(() -> {
            int pedId = pedidoService.crear(datos, usuario.usu_id());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("ped_id", pedId, "mensaje", "Pedido #" + pedId + " enviado a cocina."));
        });
    }

    @PostMapping("/api/pedidos/{id}/items")
    public ResponseEntity<?> agregarItems(@PathVariable int id, @RequestBody PedidoVista.NuevosItems datos) {
        return ejecutar(() -> {
            pedidoService.agregarItems(id, datos == null ? null : datos.items());
            return ResponseEntity.ok("Platos agregados y enviados a cocina.");
        });
    }

    /** Acciones sobre el pedido: {"accion": "finalizar"} o {"accion": "cancelar"}. */
    @PutMapping("/api/pedidos/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable int id, @RequestBody PedidoVista.Accion datos) {
        String accion = datos == null || datos.accion() == null ? "" : datos.accion().trim().toLowerCase();

        return ejecutar(() -> switch (accion) {
            case "finalizar" -> {
                pedidoService.finalizar(id);
                yield ResponseEntity.ok("Pedido #" + id + " finalizado. Queda listo para cobrar.");
            }
            case "cancelar" -> {
                pedidoService.cancelar(id);
                yield ResponseEntity.ok("Pedido #" + id + " cancelado.");
            }
            default -> ResponseEntity.badRequest().body("Acción no válida. Usa \"finalizar\" o \"cancelar\".");
        });
    }

    /** Acciones sobre un plato del pedido: {"accion": "listo"} o {"accion": "cancelar"}. */
    @PutMapping("/api/pedidos-detalle/{id}/estado")
    public ResponseEntity<?> cambiarEstadoItem(@PathVariable int id, @RequestBody PedidoVista.Accion datos) {
        String accion = datos == null || datos.accion() == null ? "" : datos.accion().trim().toLowerCase();

        return ejecutar(() -> switch (accion) {
            case "listo" -> {
                pedidoService.marcarListo(id);
                yield ResponseEntity.ok("Plato marcado como listo.");
            }
            case "cancelar" -> {
                pedidoService.cancelarItem(id);
                yield ResponseEntity.ok("Plato cancelado.");
            }
            default -> ResponseEntity.badRequest().body("Acción no válida. Usa \"listo\" o \"cancelar\".");
        });
    }

    /** Comandas pendientes para la pantalla de cocina. */
    @GetMapping("/api/cocina/comandas")
    public List<PedidoVista.Comanda> comandas() {
        return dao.comandasPendientes();
    }

    // ---------------------------------------------------------------

    private interface Accion {
        ResponseEntity<?> ejecutar();
    }

    /** Convierte los errores de negocio en un 400 con el mensaje para el usuario. */
    private ResponseEntity<?> ejecutar(Accion accion) {
        try {
            return accion.ejecutar();
        } catch (ReglaNegocioException error) {
            return ResponseEntity.badRequest().body(error.getMessage());
        }
    }
}
