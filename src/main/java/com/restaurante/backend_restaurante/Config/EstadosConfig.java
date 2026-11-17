package com.restaurante.backend_restaurante.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Códigos de la tabla estado que usa el flujo de pedidos.
 * Se configuran en application.properties (app.estados.*), así el flujo sigue
 * funcionando aunque se renombren los estados o los tipos de estado.
 */
@Component
public class EstadosConfig {

    public final int pedidoCreado;
    public final int pedidoFinalizado;
    public final int pedidoCancelado;

    public final int detallePreparacion;
    public final int detalleListo;
    public final int detalleCancelado;

    public final int platoDisponible;

    public EstadosConfig(
            @Value("${app.estados.pedido.creado:4}") int pedidoCreado,
            @Value("${app.estados.pedido.finalizado:5}") int pedidoFinalizado,
            @Value("${app.estados.pedido.cancelado:6}") int pedidoCancelado,
            @Value("${app.estados.detalle.preparacion:1}") int detallePreparacion,
            @Value("${app.estados.detalle.listo:2}") int detalleListo,
            @Value("${app.estados.detalle.cancelado:3}") int detalleCancelado,
            @Value("${app.estados.plato.disponible:8}") int platoDisponible) {

        this.pedidoCreado = pedidoCreado;
        this.pedidoFinalizado = pedidoFinalizado;
        this.pedidoCancelado = pedidoCancelado;
        this.detallePreparacion = detallePreparacion;
        this.detalleListo = detalleListo;
        this.detalleCancelado = detalleCancelado;
        this.platoDisponible = platoDisponible;
    }
}
