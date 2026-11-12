
package com.restaurante.backend_restaurante.Model;

public class TipoIdentidad {

    private int tipo_id;
    private String tipo_descripcion;
    private String tipo_sigla;
    private String tipo_estado;

    public TipoIdentidad() {
    }

    public TipoIdentidad(int tipo_id, String tipo_descripcion,
            String tipo_sigla, String tipo_estado) {
        this.tipo_id = tipo_id;
        this.tipo_descripcion = tipo_descripcion;
        this.tipo_sigla = tipo_sigla;
        this.tipo_estado = tipo_estado;
    }

    public int getTipo_id() {
        return tipo_id;
    }

    public void setTipo_id(int tipo_id) {
        this.tipo_id = tipo_id;
    }

    public String getTipo_descripcion() {
        return tipo_descripcion;
    }

    public void setTipo_descripcion(String tipo_descripcion) {
        this.tipo_descripcion = tipo_descripcion;
    }

    public String getTipo_sigla() {
        return tipo_sigla;
    }

    public void setTipo_sigla(String tipo_sigla) {
        this.tipo_sigla = tipo_sigla;
    }

    public String getTipo_estado() {
        return tipo_estado;
    }

    public void setTipo_estado(String tipo_estado) {
        this.tipo_estado = tipo_estado;
    }
}
