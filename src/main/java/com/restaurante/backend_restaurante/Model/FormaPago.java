
package com.restaurante.backend_restaurante.Model;

public class FormaPago {

    private int fp_id;
    private String fp_descripcion;
    private String fp_estado;

    public FormaPago() {
    }

    public FormaPago(int fp_id, String fp_descripcion, String fp_estado) {
        this.fp_id = fp_id;
        this.fp_descripcion = fp_descripcion;
        this.fp_estado = fp_estado;
    }

    public int getFp_id() {
        return fp_id;
    }

    public void setFp_id(int fp_id) {
        this.fp_id = fp_id;
    }

    public String getFp_descripcion() {
        return fp_descripcion;
    }

    public void setFp_descripcion(String fp_descripcion) {
        this.fp_descripcion = fp_descripcion;
    }

    public String getFp_estado() {
        return fp_estado;
    }

    public void setFp_estado(String fp_estado) {
        this.fp_estado = fp_estado;
    }
}
