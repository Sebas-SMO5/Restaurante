package com.restaurante.backend_restaurante.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Modelos de solo lectura para el Dashboard de inicio y el Cierre de caja.
 */
public final class ReporteVista {

    private ReporteVista() {
    }

    // ----------------------------- Dashboard -----------------------------

    public record VentaDia(LocalDate fecha, double total, int recibos) {
    }

    public record PlatoVendido(String plato, double cantidad, double total) {
    }

    public record StockBajo(String nombre, double stock, double minimo, String unidad) {
    }

    public record Dashboard(
            double ventasHoy,
            int recibosHoy,
            double egresosHoy,
            int pedidosAbiertos,
            int platosEnCocina,
            int porCobrarCantidad,
            double porCobrarValor,
            int pqrsPendientes,
            int pqrsVencidas,
            List<StockBajo> stockBajo,
            List<VentaDia> ventas7Dias,
            List<PlatoVendido> topPlatos30Dias
    ) {
    }

    // ---------------------------- Cierre de caja ----------------------------

    /** Suma por forma de pago (para ingresos o egresos). */
    public record PorFormaPago(int fp_id, String forma_pago, boolean efectivo, int cantidad, double total) {
    }

    public record MovimientoCaja(
            String tipo,          // "Ingreso" o "Egreso"
            String numero,        // "Recibo #12" / "Egreso N.° 3"
            LocalDateTime fecha,
            String detalle,
            String forma_pago,
            double valor
    ) {
    }

    public record CierreGuardado(
            int cc_id,
            LocalDate cc_fecha,
            double cc_ingresos,
            double cc_egresos,
            double cc_efectivo_esperado,
            double cc_efectivo_contado,
            double cc_diferencia,
            String cc_observacion,
            String registrado_por,
            LocalDateTime cc_fecha_registro
    ) {
    }

    public record Cierre(
            LocalDate fecha,
            List<PorFormaPago> ingresos,
            List<PorFormaPago> egresos,
            double totalIngresos,
            double totalEgresos,
            double neto,
            double efectivoEsperado,
            int pedidosSinCobrar,
            List<MovimientoCaja> movimientos,
            CierreGuardado cierre
    ) {
    }

    /** Petición para registrar el cierre del día. */
    public record NuevoCierre(LocalDate fecha, Double efectivo_contado, String observacion) {
    }

    /** Petición para cambiar la propia contraseña. */
    public record CambioContrasena(String actual, String nueva) {
    }
}
