package com.restaurante.backend_restaurante.Model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelos de solo lectura y de petición para Recibos de caja y Comprobantes de egreso.
 */
public final class CajaVista {

    private CajaVista() {
    }

    /** Pedido finalizado que todavía no tiene un recibo activo. */
    public record PorCobrar(
            int ped_id,
            String ped_mesa,
            LocalDateTime ped_fecha,
            String mesero,
            int items,
            double total
    ) {
    }

    /** Fila del listado de recibos. */
    public record Recibo(
            int rc_num,
            LocalDateTime rc_fecha,
            int ped_id,
            String ped_mesa,
            int cli_id,
            String cliente,
            String cli_documento,
            int fp_id,
            String forma_pago,
            int usu_id,
            String cajero,
            double rc_total,
            String rc_observacion,
            String rc_estado
    ) {
    }

    /** Línea del recibo (platos cobrados). */
    public record ReciboItem(
            int pla_id,
            String plato,
            double rcd_cantidad,
            double rcd_precio,
            double subtotal
    ) {
    }

    public record ReciboCompleto(Recibo recibo, List<ReciboItem> items) {
    }

    /** Petición para cobrar un pedido. */
    public record Cobro(Integer ped_id, Integer cli_id, Integer fp_id, String rc_observacion) {
    }

    /** Petición para anular un recibo o un egreso. */
    public record Anulacion(String motivo) {
    }

    /** Fila del listado de comprobantes de egreso. */
    public record Egreso(
            int id,
            int no_egreso,
            LocalDateTime fecha_documento,
            String tercero_identificacion,
            String detalle,
            int fp_id,
            String forma_pago,
            int conceptoEgreso_codigo,
            String concepto,
            String no_documento,
            double valor_egreso,
            String estado,
            String registrado_por
    ) {
    }

    /** Petición para registrar un comprobante de egreso. */
    public record NuevoEgreso(
            Integer conceptoEgreso_codigo,
            Integer fp_id,
            String tercero_identificacion,
            String no_documento,
            String detalle,
            Double valor_egreso
    ) {
    }
}
