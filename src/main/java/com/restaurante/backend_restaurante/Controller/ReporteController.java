package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.ReporteDAO;
import com.restaurante.backend_restaurante.Model.ReporteVista;
import com.restaurante.backend_restaurante.Model.UsuarioSesion;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Dashboard de inicio y cierre de caja.
 */
@RestController
public class ReporteController {

    private static final int PLAZO_PQRS_DIAS_HABILES = 15;

    private final ReporteDAO dao;

    public ReporteController(ReporteDAO dao) {
        this.dao = dao;
    }

    // ----------------------------- Dashboard -----------------------------

    @GetMapping("/api/dashboard")
    public ReporteVista.Dashboard dashboard() {
        LocalDate hoy = LocalDate.now();
        double[] porCobrar = dao.porCobrar();

        List<LocalDateTime> pendientes = dao.fechasPqrsPendientes();
        int vencidas = (int) pendientes.stream()
                .filter(f -> diasHabiles(f.toLocalDate(), hoy) >= PLAZO_PQRS_DIAS_HABILES)
                .count();

        return new ReporteVista.Dashboard(
                dao.ventasDelDia(hoy),
                dao.recibosDelDia(hoy),
                dao.egresosDelDia(hoy),
                dao.pedidosAbiertos(),
                dao.platosEnCocina(),
                (int) porCobrar[0],
                porCobrar[1],
                pendientes.size(),
                vencidas,
                dao.stockBajo(),
                ventasUltimosDias(hoy, 7),
                dao.topPlatos(hoy.minusDays(29), 5));
    }

    /** Ventas de los últimos n días, incluidos los días sin ventas (en cero). */
    private List<ReporteVista.VentaDia> ventasUltimosDias(LocalDate hoy, int dias) {
        LocalDate desde = hoy.minusDays(dias - 1L);

        Map<LocalDate, ReporteVista.VentaDia> porDia = dao.ventasPorDia(desde, hoy).stream()
                .collect(Collectors.toMap(ReporteVista.VentaDia::fecha, Function.identity()));

        List<ReporteVista.VentaDia> resultado = new ArrayList<>();

        for (LocalDate dia = desde; !dia.isAfter(hoy); dia = dia.plusDays(1)) {
            resultado.add(porDia.getOrDefault(dia, new ReporteVista.VentaDia(dia, 0, 0)));
        }

        return resultado;
    }

    private static int diasHabiles(LocalDate desde, LocalDate hasta) {
        int habiles = 0;

        for (LocalDate dia = desde; dia.isBefore(hasta); dia = dia.plusDays(1)) {
            if (dia.getDayOfWeek() != DayOfWeek.SATURDAY && dia.getDayOfWeek() != DayOfWeek.SUNDAY) {
                habiles++;
            }
        }

        return habiles;
    }

    // ---------------------------- Cierre de caja ----------------------------

    /** Resumen de caja de un día (por defecto hoy) y el cierre guardado, si existe. */
    @GetMapping("/api/caja/cierre")
    public ResponseEntity<?> cierre(
            @RequestParam(name = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDate dia = fecha == null ? LocalDate.now() : fecha;

        if (dia.isAfter(LocalDate.now())) {
            return ResponseEntity.badRequest().body("No se puede consultar la caja de una fecha futura.");
        }

        return ResponseEntity.ok(resumen(dia));
    }

    @PostMapping("/api/caja/cierre")
    public ResponseEntity<?> registrarCierre(
            @RequestBody ReporteVista.NuevoCierre datos,
            HttpServletRequest request) {

        UsuarioSesion usuario = AuthController.usuarioEnSesion(request);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debes iniciar sesión.");
        }

        LocalDate dia = datos == null || datos.fecha() == null ? LocalDate.now() : datos.fecha();

        if (dia.isAfter(LocalDate.now())) {
            return ResponseEntity.badRequest().body("No se puede cerrar la caja de una fecha futura.");
        }

        if (datos == null || datos.efectivo_contado() == null || datos.efectivo_contado() < 0) {
            return ResponseEntity.badRequest().body("Escribe el efectivo contado en caja (puede ser 0).");
        }

        String observacion = datos.observacion() == null || datos.observacion().isBlank()
                ? null
                : datos.observacion().trim();

        if (observacion != null && observacion.length() > 300) {
            return ResponseEntity.badRequest().body("La observación admite máximo 300 caracteres.");
        }

        ReporteVista.Cierre resumen = resumen(dia);

        if (resumen.cierre() != null) {
            return ResponseEntity.badRequest().body("La caja del " + dia + " ya fue cerrada.");
        }

        double diferencia = datos.efectivo_contado() - resumen.efectivoEsperado();

        if (Math.abs(diferencia) >= 1 && observacion == null) {
            return ResponseEntity.badRequest()
                    .body("Hay una diferencia en el efectivo. Escribe una observación que la explique.");
        }

        try {
            dao.guardarCierre(dia, resumen.totalIngresos(), resumen.totalEgresos(), resumen.efectivoEsperado(),
                    datos.efectivo_contado(), observacion, usuario.usu_id());
        } catch (DuplicateKeyException error) {
            return ResponseEntity.badRequest().body("La caja del " + dia + " ya fue cerrada.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "mensaje", "Cierre de caja del " + dia + " registrado.",
                "cierre", resumen(dia)));
    }

    private ReporteVista.Cierre resumen(LocalDate dia) {
        List<ReporteVista.PorFormaPago> ingresos = dao.ingresosPorFormaPago(dia);
        List<ReporteVista.PorFormaPago> egresos = dao.egresosPorFormaPago(dia);

        double totalIngresos = ingresos.stream().mapToDouble(ReporteVista.PorFormaPago::total).sum();
        double totalEgresos = egresos.stream().mapToDouble(ReporteVista.PorFormaPago::total).sum();

        double efectivoEsperado =
                ingresos.stream().filter(ReporteVista.PorFormaPago::efectivo).mapToDouble(ReporteVista.PorFormaPago::total).sum()
              - egresos.stream().filter(ReporteVista.PorFormaPago::efectivo).mapToDouble(ReporteVista.PorFormaPago::total).sum();

        return new ReporteVista.Cierre(
                dia,
                ingresos,
                egresos,
                totalIngresos,
                totalEgresos,
                totalIngresos - totalEgresos,
                efectivoEsperado,
                dia.equals(LocalDate.now()) ? dao.pedidosSinCobrar() : 0,
                dao.movimientos(dia),
                dao.cierreDelDia(dia));
    }
}
