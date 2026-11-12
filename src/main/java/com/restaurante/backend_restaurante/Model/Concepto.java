
package com.restaurante.backend_restaurante.Model;



public class Concepto {

    private int con_id;
    private String con_descripcion;
    private String con_estado;

    public Concepto() {
    }

    public Concepto(int con_id, String con_descripcion, String con_estado) {
        this.con_id = con_id;
        this.con_descripcion = con_descripcion;
        this.con_estado = con_estado;
    }

    public int getCon_id() {
        return con_id;
    }

    public void setCon_id(int con_id) {
        this.con_id = con_id;
    }

    public String getCon_descripcion() {
        return con_descripcion;
    }

    public void setCon_descripcion(String con_descripcion) {
        this.con_descripcion = con_descripcion;
    }

    public String getCon_estado() {
        return con_estado;
    }

    public void setCon_estado(String con_estado) {
        this.con_estado = con_estado;
    }
}
