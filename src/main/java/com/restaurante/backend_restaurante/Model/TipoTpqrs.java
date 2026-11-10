
package com.restaurante.backend_restaurante.Model;

public class TipoTpqrs {

    private int tpqrs_id;
    private String tpqrs_descripcion;
    private String tpqrs_estado;

    public TipoTpqrs() {
    }

    public TipoTpqrs(int tpqrs_id, String tpqrs_descripcion, String tpqrs_estado) {
        this.tpqrs_id = tpqrs_id;
        this.tpqrs_descripcion = tpqrs_descripcion;
        this.tpqrs_estado = tpqrs_estado;
    }

    public int getTpqrs_id() {
        return tpqrs_id;
    }

    public void setTpqrs_id(int tpqrs_id) {
        this.tpqrs_id = tpqrs_id;
    }

    public String getTpqrs_descripcion() {
        return tpqrs_descripcion;
    }

    public void setTpqrs_descripcion(String tpqrs_descripcion) {
        this.tpqrs_descripcion = tpqrs_descripcion;
    }

    public String getTpqrs_estado() {
        return tpqrs_estado;
    }

    public void setTpqrs_estado(String tpqrs_estado) {
        this.tpqrs_estado = tpqrs_estado;
    }
}
