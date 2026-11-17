package com.restaurante.backend_restaurante.Model;

public class CategoriaInsumo {

    private int cins_id;
    private String cins_descripcion;
    private String cins_estado;

    public CategoriaInsumo() {
    }

    public int getCins_id() {
        return cins_id;
    }

    public void setCins_id(int cins_id) {
        this.cins_id = cins_id;
    }

    public String getCins_descripcion() {
        return cins_descripcion;
    }

    public void setCins_descripcion(String cins_descripcion) {
        this.cins_descripcion = cins_descripcion;
    }

    public String getCins_estado() {
        return cins_estado;
    }

    public void setCins_estado(String cins_estado) {
        this.cins_estado = cins_estado;
    }
}
