
package com.restaurante.backend_restaurante.Model;

import java.sql.Date;

public class Pedido {

    private int ped_id;
    private Date ped_fecha;
    private Integer usu_id;
    private String ped_mesa;
    private Integer est_id;

    public Pedido() {
    }

    public Pedido(int ped_id, Date ped_fecha, Integer usu_id,
            String ped_mesa, Integer est_id) {
        this.ped_id = ped_id;
        this.ped_fecha = ped_fecha;
        this.usu_id = usu_id;
        this.ped_mesa = ped_mesa;
        this.est_id = est_id;
    }

    public int getPed_id() {
        return ped_id;
    }

    public void setPed_id(int ped_id) {
        this.ped_id = ped_id;
    }

    public Date getPed_fecha() {
        return ped_fecha;
    }

    public void setPed_fecha(Date ped_fecha) {
        this.ped_fecha = ped_fecha;
    }

    public Integer getUsu_id() {
        return usu_id;
    }

    public void setUsu_id(Integer usu_id) {
        this.usu_id = usu_id;
    }

    public String getPed_mesa() {
        return ped_mesa;
    }

    public void setPed_mesa(String ped_mesa) {
        this.ped_mesa = ped_mesa;
    }

    public Integer getEst_id() {
        return est_id;
    }

    public void setEst_id(Integer est_id) {
        this.est_id = est_id;
    }
}
