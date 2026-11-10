
package com.restaurante.backend_restaurante.Model;

import java.sql.Date;

public class ReciboCaja {

    private int rc_num;
    private int usu_id;
    private Date rc_fecha;
    private int ped_id;
    private int cli_id;
    private double rc_total;
    private String rc_observacion;
    private String rc_estado;

    public ReciboCaja() {
    }

    public ReciboCaja(int rc_num, int usu_id, Date rc_fecha,
            int ped_id, int cli_id, double rc_total,
            String rc_observacion, String rc_estado) {
        this.rc_num = rc_num;
        this.usu_id = usu_id;
        this.rc_fecha = rc_fecha;
        this.ped_id = ped_id;
        this.cli_id = cli_id;
        this.rc_total = rc_total;
        this.rc_observacion = rc_observacion;
        this.rc_estado = rc_estado;
    }

    public int getRc_num() {
        return rc_num;
    }

    public void setRc_num(int rc_num) {
        this.rc_num = rc_num;
    }

    public int getUsu_id() {
        return usu_id;
    }

    public void setUsu_id(int usu_id) {
        this.usu_id = usu_id;
    }

    public Date getRc_fecha() {
        return rc_fecha;
    }

    public void setRc_fecha(Date rc_fecha) {
        this.rc_fecha = rc_fecha;
    }

    public int getPed_id() {
        return ped_id;
    }

    public void setPed_id(int ped_id) {
        this.ped_id = ped_id;
    }

    public int getCli_id() {
        return cli_id;
    }

    public void setCli_id(int cli_id) {
        this.cli_id = cli_id;
    }

    public double getRc_total() {
        return rc_total;
    }

    public void setRc_total(double rc_total) {
        this.rc_total = rc_total;
    }

    public String getRc_observacion() {
        return rc_observacion;
    }

    public void setRc_observacion(String rc_observacion) {
        this.rc_observacion = rc_observacion;
    }

    public String getRc_estado() {
        return rc_estado;
    }

    public void setRc_estado(String rc_estado) {
        this.rc_estado = rc_estado;
    }
}
