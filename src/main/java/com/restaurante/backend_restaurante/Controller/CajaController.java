package com.restaurante.backend_restaurante.Controller;

import com.restaurante.backend_restaurante.DAO.CajaDAO;
import com.restaurante.backend_restaurante.Model.CajaVista;
import com.restaurante.backend_restaurante.Model.UsuarioSesion;
import com.restaurante.backend_restaurante.Service.CajaService;
import com.restaurante.backend_restaurante.Service.ReglaNegocioException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Pantallas "Recibos de caja" y "Comprobantes de egreso".
 * Convive con el CRUD básico de /api/recibos-caja y /api/egresos.
 */
@RestController
public class CajaController {

    private final CajaService cajaService;
    private final CajaDAO dao;

    public CajaController(CajaService cajaService, CajaDAO dao) {
        this.cajaService = cajaService;
        this.dao = dao;
    }

    // ----------------------------- Recibos -----------------------------

    @GetMapping("/api/recibos-caja/por-cobrar")
    public List<CajaVista.PorCobrar> porCobrar() {
        return dao.pedidosPorCobrar();
    }

    @GetMapping("/api/recibos-caja/resumen")
    public List<CajaVista.Recibo> recibos() {
        return dao.recibos();
    }

    @GetMapping("/api/recibos-caja/{num}/completo")
    public ResponseEntity<?> recibo(@PathVariable int num) {
        return ejecutar(() -> ResponseEntity.ok(cajaService.recibo(num)));
    }

    @PostMapping("/api/recibos-caja/cobrar")
    public ResponseEntity<?> cobrar(@RequestBody CajaVista.Cobro cobro, HttpServletRequest request) {
        UsuarioSesion usuario = AuthController.usuarioEnSesion(request);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debes iniciar sesión para cobrar.");
        }

        return ejecutar(() -> {
            int rcNum = cajaService.cobrar(cobro, usuario.usu_id());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("rc_num", rcNum, "mensaje", "Recibo de caja #" + rcNum + " generado correctamente."));
        });
    }

    @PutMapping("/api/recibos-caja/{num}/anular")
    public ResponseEntity<?> anularRecibo(
            @PathVariable int num,
            @RequestBody CajaVista.Anulacion datos,
            HttpServletRequest request) {

        return ejecutar(() -> {
            cajaService.anularRecibo(num, datos == null ? null : datos.motivo(), esAdmin(request));
            return ResponseEntity.ok("Recibo #" + num + " anulado. El pedido vuelve a quedar pendiente de cobro.");
        });
    }

    // ----------------------------- Egresos -----------------------------

    @GetMapping("/api/egresos/resumen")
    public List<CajaVista.Egreso> egresos() {
        return dao.egresos();
    }

    @PostMapping("/api/egresos/registrar")
    public ResponseEntity<?> registrarEgreso(@RequestBody CajaVista.NuevoEgreso datos, HttpServletRequest request) {
        UsuarioSesion usuario = AuthController.usuarioEnSesion(request);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Debes iniciar sesión para registrar egresos.");
        }

        return ejecutar(() -> {
            int consecutivo = cajaService.registrarEgreso(datos, usuario.usu_id());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("no_egreso", consecutivo,
                            "mensaje", "Comprobante de egreso N.° " + consecutivo + " registrado correctamente."));
        });
    }

    @PutMapping("/api/egresos/{id}/anular")
    public ResponseEntity<?> anularEgreso(
            @PathVariable int id,
            @RequestBody CajaVista.Anulacion datos,
            HttpServletRequest request) {

        return ejecutar(() -> {
            cajaService.anularEgreso(id, datos == null ? null : datos.motivo(), esAdmin(request));
            return ResponseEntity.ok("Comprobante de egreso anulado.");
        });
    }

    // -------------------------------------------------------------------

    private boolean esAdmin(HttpServletRequest request) {
        UsuarioSesion usuario = AuthController.usuarioEnSesion(request);
        return usuario != null && usuario.es_admin();
    }

    private ResponseEntity<?> ejecutar(Supplier<ResponseEntity<?>> accion) {
        try {
            return accion.get();
        } catch (ReglaNegocioException error) {
            return ResponseEntity.badRequest().body(error.getMessage());
        }
    }
}
