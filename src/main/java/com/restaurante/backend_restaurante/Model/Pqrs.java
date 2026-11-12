
package com.restaurante.backend_restaurante.Model;

import java.sql.Date;

public class Pqrs {

    private int pqrs_id;
    private Date pqrs_fecha;
    private byte[] pqrs_descripcion;
    private String pqrs_correo;
    private String pqrs_telefono;
    private int tpqrs_id;
    private int est_id;
    private byte[] pqrs_respuesta;

    public Pqrs() {
    }

    public Pqrs(int pqrs_id, Date pqrs_fecha, byte[] pqrs_descripcion,
            String pqrs_correo, String pqrs_telefono, int tpqrs_id,
            int est_id, byte[] pqrs_respuesta) {
        this.pqrs_id = pqrs_id;
        this.pqrs_fecha = pqrs_fecha;
        this.pqrs_descripcion = pqrs_descripcion;
        this.pqrs_correo = pqrs_correo;
        this.pqrs_telefono = pqrs_telefono;
        this.tpqrs_id = tpqrs_id;
        this.est_id = est_id;
        this.pqrs_respuesta = pqrs_respuesta;
    }

    public int getPqrs_id() {
        return pqrs_id;
    }

    public void setPqrs_id(int pqrs_id) {
        this.pqrs_id = pqrs_id;
    }

    public Date getPqrs_fecha() {
        return pqrs_fecha;
    }

    public void setPqrs_fecha(Date pqrs_fecha) {
        this.pqrs_fecha = pqrs_fecha;
    }

    public byte[] getPqrs_descripcion() {
        return pqrs_descripcion;
    }

    public void setPqrs_descripcion(byte[] pqrs_descripcion) {
        this.pqrs_descripcion = pqrs_descripcion;
    }

    public String getPqrs_correo() {
        return pqrs_correo;
    }

    public void setPqrs_correo(String pqrs_correo) {
        this.pqrs_correo = pqrs_correo;
    }

    public String getPqrs_telefono() {
        return pqrs_telefono;
    }

    public void setPqrs_telefono(String pqrs_telefono) {
        this.pqrs_telefono = pqrs_telefono;
    }

    public int getTpqrs_id() {
        return tpqrs_id;
    }

    public void setTpqrs_id(int tpqrs_id) {
        this.tpqrs_id = tpqrs_id;
    }

    public int getEst_id() {
        return est_id;
    }

    public void setEst_id(int est_id) {
        this.est_id = est_id;
    }

    public byte[] getPqrs_respuesta() {
        return pqrs_respuesta;
    }

    public void setPqrs_respuesta(byte[] pqrs_respuesta) {
        this.pqrs_respuesta = pqrs_respuesta;
    }
}
