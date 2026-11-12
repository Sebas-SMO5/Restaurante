
package com.restaurante.backend_restaurante.Model;

public class UnidadMedida {

    private int um_id;
    private String um_descripcion;
    private String um_abreviatura;
    private String um_estado;

    public UnidadMedida() {
    }

    public UnidadMedida(int um_id, String um_descripcion,
            String um_abreviatura, String um_estado) {
        this.um_id = um_id;
        this.um_descripcion = um_descripcion;
        this.um_abreviatura = um_abreviatura;
        this.um_estado = um_estado;
    }

    public int getUm_id() {
        return um_id;
    }

    public void setUm_id(int um_id) {
        this.um_id = um_id;
    }

    public String getUm_descripcion() {
        return um_descripcion;
    }

    public void setUm_descripcion(String um_descripcion) {
        this.um_descripcion = um_descripcion;
    }

    public String getUm_abreviatura() {
        return um_abreviatura;
    }

    public void setUm_abreviatura(String um_abreviatura) {
        this.um_abreviatura = um_abreviatura;
    }

    public String getUm_estado() {
        return um_estado;
    }

    public void setUm_estado(String um_estado) {
        this.um_estado = um_estado;
    }
}
