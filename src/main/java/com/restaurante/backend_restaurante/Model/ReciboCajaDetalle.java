
package com.restaurante.backend_restaurante.Model;

public class ReciboCajaDetalle {

    private int rcd_id;
    private int rcd_num;
    private int pla_id;
    private double rcd_precio;
    private double rcd_cantidad;

    public ReciboCajaDetalle() {
    }

    public ReciboCajaDetalle(int rcd_id, int rcd_num, int pla_id,
            double rcd_precio, double rcd_cantidad) {
        this.rcd_id = rcd_id;
        this.rcd_num = rcd_num;
        this.pla_id = pla_id;
        this.rcd_precio = rcd_precio;
        this.rcd_cantidad = rcd_cantidad;
    }

    public int getRcd_id() {
        return rcd_id;
    }

    public void setRcd_id(int rcd_id) {
        this.rcd_id = rcd_id;
    }

    public int getRcd_num() {
        return rcd_num;
    }

    public void setRcd_num(int rcd_num) {
        this.rcd_num = rcd_num;
    }

    public int getPla_id() {
        return pla_id;
    }

    public void setPla_id(int pla_id) {
        this.pla_id = pla_id;
    }

    public double getRcd_precio() {
        return rcd_precio;
    }

    public void setRcd_precio(double rcd_precio) {
        this.rcd_precio = rcd_precio;
    }

    public double getRcd_cantidad() {
        return rcd_cantidad;
    }

    public void setRcd_cantidad(double rcd_cantidad) {
        this.rcd_cantidad = rcd_cantidad;
    }
}
