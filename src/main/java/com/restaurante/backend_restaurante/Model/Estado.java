package com.restaurante.backend_restaurante.Model;


public class Estado {

    private int est_id;
    private String est_descripcion;
    private int tes_id;
    private String est_estado;

    public Estado() {
    }

    public Estado(int est_id, String est_descripcion, int tes_id, String est_estado) {
        this.est_id = est_id;
        this.est_descripcion = est_descripcion;
        this.tes_id = tes_id;
        this.est_estado = est_estado;
    }

    public int getEst_id() {
        return est_id;
    }

    public void setEst_id(int est_id) {
        this.est_id = est_id;
    }

    public String getEst_descripcion() {
        return est_descripcion;
    }

    public void setEst_descripcion(String est_descripcion) {
        this.est_descripcion = est_descripcion;
    }

    public int getTes_id() {
        return tes_id;
    }

    public void setTes_id(int tes_id) {
        this.tes_id = tes_id;
    }

    public String getEst_estado() {
        return est_estado;
    }

    public void setEst_estado(String est_estado) {
        this.est_estado = est_estado;
    }
}
