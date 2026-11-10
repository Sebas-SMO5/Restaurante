
package com.restaurante.backend_restaurante.Model;

public class Comanda {

    private int com_id;
    private Integer mesa;
    private Integer pla_id;
    private String com_obs;
    private Integer est_id;

    public Comanda() {
    }

    public Comanda(int com_id, Integer mesa, Integer pla_id, String com_obs, Integer est_id) {
        this.com_id = com_id;
        this.mesa = mesa;
        this.pla_id = pla_id;
        this.com_obs = com_obs;
        this.est_id = est_id;
    }

    public int getCom_id() {
        return com_id;
    }

    public void setCom_id(int com_id) {
        this.com_id = com_id;
    }

    public Integer getMesa() {
        return mesa;
    }

    public void setMesa(Integer mesa) {
        this.mesa = mesa;
    }

    public Integer getPla_id() {
        return pla_id;
    }

    public void setPla_id(Integer pla_id) {
        this.pla_id = pla_id;
    }

    public String getCom_obs() {
        return com_obs;
    }

    public void setCom_obs(String com_obs) {
        this.com_obs = com_obs;
    }

    public Integer getEst_id() {
        return est_id;
    }

    public void setEst_id(Integer est_id) {
        this.est_id = est_id;
    }
}
