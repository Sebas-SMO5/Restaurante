package com.restaurante.backend_restaurante.Model;

import java.sql.Timestamp;

public class EncabezadoEgresos {

    private int id;
    private int no_egreso;
    private Timestamp fecha_documento;
    private String tercero_identificacion;
    private String detalle;
    private int fp_id;
    private int conceptoEgreso_codigo;
    private String no_documento;
    private double valor_egreso;
    private String estado;

    public EncabezadoEgresos() {
    }

    public EncabezadoEgresos(int id, int no_egreso, Timestamp fecha_documento,
            String tercero_identificacion, String detalle, int fp_id,
            int conceptoEgreso_codigo, String no_documento,
            double valor_egreso, String estado) {
        this.id = id;
        this.no_egreso = no_egreso;
        this.fecha_documento = fecha_documento;
        this.tercero_identificacion = tercero_identificacion;
        this.detalle = detalle;
        this.fp_id = fp_id;
        this.conceptoEgreso_codigo = conceptoEgreso_codigo;
        this.no_documento = no_documento;
        this.valor_egreso = valor_egreso;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNo_egreso() {
        return no_egreso;
    }

    public void setNo_egreso(int no_egreso) {
        this.no_egreso = no_egreso;
    }

    public Timestamp getFecha_documento() {
        return fecha_documento;
    }

    public void setFecha_documento(Timestamp fecha_documento) {
        this.fecha_documento = fecha_documento;
    }

    public String getTercero_identificacion() {
        return tercero_identificacion;
    }

    public void setTercero_identificacion(String tercero_identificacion) {
        this.tercero_identificacion = tercero_identificacion;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public int getFp_id() {
        return fp_id;
    }

    public void setFp_id(int fp_id) {
        this.fp_id = fp_id;
    }

    public int getConceptoEgreso_codigo() {
        return conceptoEgreso_codigo;
    }

    public void setConceptoEgreso_codigo(int conceptoEgreso_codigo) {
        this.conceptoEgreso_codigo = conceptoEgreso_codigo;
    }

    public String getNo_documento() {
        return no_documento;
    }

    public void setNo_documento(String no_documento) {
        this.no_documento = no_documento;
    }

    public double getValor_egreso() {
        return valor_egreso;
    }

    public void setValor_egreso(double valor_egreso) {
        this.valor_egreso = valor_egreso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}