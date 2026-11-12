
package com.restaurante.backend_restaurante.Model;

public class TipoEstado {

    private int tes_id;
    private String tes_descripcion;
    private String tes_estado;

    public TipoEstado() {
    }

    public TipoEstado(int tes_id, String tes_descripcion, String tes_estado) {
        this.tes_id = tes_id;
        this.tes_descripcion = tes_descripcion;
        this.tes_estado = tes_estado;
    }

    public int getTes_id() {
        return tes_id;
    }

    public void setTes_id(int tes_id) {
        this.tes_id = tes_id;
    }

    public String getTes_descripcion() {
        return tes_descripcion;
    }

    public void setTes_descripcion(String tes_descripcion) {
        this.tes_descripcion = tes_descripcion;
    }

    public String getTes_estado() {
        return tes_estado;
    }

    public void setTes_estado(String tes_estado) {
        this.tes_estado = tes_estado;
    }
}
