package com.restaurante.backend_restaurante.Service;

import com.restaurante.backend_restaurante.Config.EstadosConfig;
import com.restaurante.backend_restaurante.DAO.CajaDAO;
import com.restaurante.backend_restaurante.Model.CajaVista;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Reglas de caja:
 *  - Recibo de caja: cobra un pedido finalizado (una sola vez mientras el recibo esté activo).
 *    Las líneas del recibo se copian de los platos no cancelados del pedido.
 *  - Comprobante de egreso: registra una salida de dinero con consecutivo propio.
 *  - Anular (recibo o egreso) solo lo hace un administrador y exige un motivo.
 */
@Service
public class CajaService {

    private final CajaDAO dao;
    private final EstadosConfig estados;

    public CajaService(CajaDAO dao, EstadosConfig estados) {
        this.dao = dao;
        this.estados = estados;
    }

    // =================================================================
    // Recibos de caja
    // =================================================================

    public CajaVista.ReciboCompleto recibo(int rcNum) {
        CajaVista.Recibo recibo = dao.recibo(rcNum);

        if (recibo == null) {
            throw new ReglaNegocioException("No se encontró el recibo.");
        }

        return new CajaVista.ReciboCompleto(recibo, dao.itemsRecibo(rcNum));
    }

    @Transactional
    public int cobrar(CajaVista.Cobro cobro, int usuId) {

        if (cobro == null || cobro.ped_id() == null || cobro.cli_id() == null || cobro.fp_id() == null) {
            throw new ReglaNegocioException("Selecciona el pedido, el cliente y la forma de pago.");
        }

        exigirCajaAbierta(LocalDate.now(), "cobrar más pedidos hoy");

        Integer estadoPedido = dao.estadoPedido(cobro.ped_id());

        if (estadoPedido == null) {
            throw new ReglaNegocioException("No se encontró el pedido.");
        }

        if (estadoPedido != estados.pedidoFinalizado) {
            throw new ReglaNegocioException("Solo se pueden cobrar pedidos finalizados.");
        }

        Integer existente = dao.reciboActivoDePedido(cobro.ped_id());

        if (existente != null) {
            throw new ReglaNegocioException("El pedido #" + cobro.ped_id() + " ya se cobró con el recibo #" + existente + ".");
        }

        if (!dao.clienteActivo(cobro.cli_id())) {
            throw new ReglaNegocioException("El cliente seleccionado no existe o está inactivo.");
        }

        if (!dao.formaPagoActiva(cobro.fp_id())) {
            throw new ReglaNegocioException("La forma de pago seleccionada no existe o está inactiva.");
        }

        String observacion = recortar(cobro.rc_observacion(), 360);

        List<double[]> lineas = dao.lineasCobrables(cobro.ped_id());

        if (lineas.isEmpty()) {
            throw new ReglaNegocioException("El pedido no tiene platos por cobrar.");
        }

        double total = lineas.stream().mapToDouble(l -> l[1] * l[2]).sum();

        int rcNum = dao.insertarRecibo(usuId, cobro.ped_id(), cobro.cli_id(), cobro.fp_id(), total, observacion);

        for (double[] linea : lineas) {
            dao.insertarLineaRecibo(rcNum, (int) linea[0], linea[1], linea[2]);
        }

        return rcNum;
    }

    /** Anula el recibo; el pedido vuelve a quedar pendiente de cobro. */
    @Transactional
    public void anularRecibo(int rcNum, String motivo, boolean esAdmin) {

        if (!esAdmin) {
            throw new ReglaNegocioException("Solo un administrador puede anular recibos de caja.");
        }

        CajaVista.Recibo recibo = dao.recibo(rcNum);

        if (recibo == null) {
            throw new ReglaNegocioException("No se encontró el recibo.");
        }

        if (CajaDAO.ANULADO.equalsIgnoreCase(recibo.rc_estado())) {
            throw new ReglaNegocioException("El recibo ya está anulado.");
        }

        exigirCajaAbierta(recibo.rc_fecha().toLocalDate(), "anular sus recibos");

        String texto = exigirMotivo(motivo);
        String anterior = recibo.rc_observacion() == null || recibo.rc_observacion().isBlank()
                ? ""
                : recibo.rc_observacion() + " | ";

        dao.anularRecibo(rcNum, recortar(anterior + "ANULADO: " + texto, 360));
    }

    // =================================================================
    // Comprobantes de egreso
    // =================================================================

    @Transactional
    public int registrarEgreso(CajaVista.NuevoEgreso e, int usuId) {

        if (e == null || e.conceptoEgreso_codigo() == null || e.fp_id() == null) {
            throw new ReglaNegocioException("Selecciona el concepto y la forma de pago.");
        }

        exigirCajaAbierta(LocalDate.now(), "registrar más egresos hoy");

        if (!dao.conceptoActivo(e.conceptoEgreso_codigo())) {
            throw new ReglaNegocioException("El concepto seleccionado no existe o está inactivo.");
        }

        if (!dao.formaPagoActiva(e.fp_id())) {
            throw new ReglaNegocioException("La forma de pago seleccionada no existe o está inactiva.");
        }

        String tercero = e.tercero_identificacion() == null ? "" : e.tercero_identificacion().trim().toUpperCase();

        if (!tercero.matches("^[A-Z0-9-]{4,15}$")) {
            throw new ReglaNegocioException("La identificación del tercero debe tener entre 4 y 15 caracteres (letras, números o guion).");
        }

        String detalle = e.detalle() == null ? "" : e.detalle().trim();

        if (detalle.isEmpty() || detalle.length() > 250) {
            throw new ReglaNegocioException("El detalle es obligatorio y admite máximo 250 caracteres.");
        }

        String documento = recortar(e.no_documento(), 15);

        if (e.no_documento() != null && e.no_documento().trim().length() > 15) {
            throw new ReglaNegocioException("El número de documento soporte admite máximo 15 caracteres.");
        }

        if (e.valor_egreso() == null || e.valor_egreso() <= 0 || e.valor_egreso() > 1_000_000_000) {
            throw new ReglaNegocioException("El valor del egreso debe ser mayor que cero.");
        }

        int consecutivo = dao.siguienteConsecutivoEgreso();

        dao.insertarEgreso(consecutivo,
                new CajaVista.NuevoEgreso(e.conceptoEgreso_codigo(), e.fp_id(), tercero, documento, detalle, e.valor_egreso()),
                usuId);

        return consecutivo;
    }

    @Transactional
    public void anularEgreso(int id, String motivo, boolean esAdmin) {

        if (!esAdmin) {
            throw new ReglaNegocioException("Solo un administrador puede anular comprobantes de egreso.");
        }

        CajaVista.Egreso egreso = dao.egreso(id);

        if (egreso == null) {
            throw new ReglaNegocioException("No se encontró el comprobante de egreso.");
        }

        if (CajaDAO.ANULADO.equalsIgnoreCase(egreso.estado())) {
            throw new ReglaNegocioException("El comprobante ya está anulado.");
        }

        exigirCajaAbierta(egreso.fecha_documento().toLocalDate(), "anular sus comprobantes");

        String texto = exigirMotivo(motivo);
        dao.anularEgreso(id, recortar(egreso.detalle() + " | ANULADO: " + texto, 250));
    }

    // =================================================================

    /** Después del cierre, los totales de ese día no pueden cambiar. */
    private void exigirCajaAbierta(LocalDate dia, String accion) {
        if (dao.cajaCerrada(dia)) {
            throw new ReglaNegocioException("La caja del " + dia + " ya fue cerrada. Ya no se permite " + accion + ".");
        }
    }

    private String exigirMotivo(String motivo) {
        String texto = motivo == null ? "" : motivo.trim();

        if (texto.length() < 5) {
            throw new ReglaNegocioException("Escribe el motivo de la anulación (mínimo 5 caracteres).");
        }

        return texto;
    }

    private String recortar(String valor, int maximo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }

        String texto = valor.trim();
        return texto.length() > maximo ? texto.substring(0, maximo) : texto;
    }
}
