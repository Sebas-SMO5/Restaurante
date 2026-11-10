
package com.restaurante.backend_restaurante.Model;

public class PedidoDetalle {

    private int ped_det_id;
    private Integer ped_det_cant;
    private Float ped_det_precio;
    private String ped_det_obser;
    private Integer pla_id;
    private Integer ped_id;
    private int est_id;

    public PedidoDetalle() {
    }

    public PedidoDetalle(int ped_det_id, Integer ped_det_cant,
            Float ped_det_precio, String ped_det_obser,
            Integer pla_id, Integer ped_id, int est_id) {
        this.ped_det_id = ped_det_id;
        this.ped_det_cant = ped_det_cant;
        this.ped_det_precio = ped_det_precio;
        this.ped_det_obser = ped_det_obser;
        this.pla_id = pla_id;
        this.ped_id = ped_id;
        this.est_id = est_id;
    }

    public int getPed_det_id() {
        return ped_det_id;
    }

    public void setPed_det_id(int ped_det_id) {
        this.ped_det_id = ped_det_id;
    }

    public Integer getPed_det_cant() {
        return ped_det_cant;
    }

    public void setPed_det_cant(Integer ped_det_cant) {
        this.ped_det_cant = ped_det_cant;
    }

    public Float getPed_det_precio() {
        return ped_det_precio;
    }

    public void setPed_det_precio(Float ped_det_precio) {
        this.ped_det_precio = ped_det_precio;
    }

    public String getPed_det_obser() {
        return ped_det_obser;
    }

    public void setPed_det_obser(String ped_det_obser) {
        this.ped_det_obser = ped_det_obser;
    }

    public Integer getPla_id() {
        return pla_id;
    }

    public void setPla_id(Integer pla_id) {
        this.pla_id = pla_id;
    }

    public Integer getPed_id() {
        return ped_id;
    }

    public void setPed_id(Integer ped_id) {
        this.ped_id = ped_id;
    }

    public int getEst_id() {
        return est_id;
    }

    public void setEst_id(int est_id) {
        this.est_id = est_id;
    }
}
