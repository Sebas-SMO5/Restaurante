
package com.restaurante.backend_restaurante.Model;

import java.math.BigDecimal;

public class Insumo {

    private long ins_id;
    private long sede_id;
    private long cins_id;
    private long pres_id;
    private String ins_nombre;
    private String ins_codigo;
    private String ins_codigo_barras;
    private BigDecimal ins_precio_compra;
    private BigDecimal ins_stock;
    private BigDecimal ins_stock_min;
    private boolean ins_vendible;
    private String ins_imagen;
    private boolean ins_estado;

    public Insumo() {
    }

    public Insumo(long ins_id, long sede_id, long cins_id, long pres_id,
            String ins_nombre, String ins_codigo, String ins_codigo_barras,
            BigDecimal ins_precio_compra, BigDecimal ins_stock,
            BigDecimal ins_stock_min, boolean ins_vendible,
            String ins_imagen, boolean ins_estado) {
        this.ins_id = ins_id;
        this.sede_id = sede_id;
        this.cins_id = cins_id;
        this.pres_id = pres_id;
        this.ins_nombre = ins_nombre;
        this.ins_codigo = ins_codigo;
        this.ins_codigo_barras = ins_codigo_barras;
        this.ins_precio_compra = ins_precio_compra;
        this.ins_stock = ins_stock;
        this.ins_stock_min = ins_stock_min;
        this.ins_vendible = ins_vendible;
        this.ins_imagen = ins_imagen;
        this.ins_estado = ins_estado;
    }

    public long getIns_id() {
        return ins_id;
    }

    public void setIns_id(long ins_id) {
        this.ins_id = ins_id;
    }

    public long getSede_id() {
        return sede_id;
    }

    public void setSede_id(long sede_id) {
        this.sede_id = sede_id;
    }

    public long getCins_id() {
        return cins_id;
    }

    public void setCins_id(long cins_id) {
        this.cins_id = cins_id;
    }

    public long getPres_id() {
        return pres_id;
    }

    public void setPres_id(long pres_id) {
        this.pres_id = pres_id;
    }

    public String getIns_nombre() {
        return ins_nombre;
    }

    public void setIns_nombre(String ins_nombre) {
        this.ins_nombre = ins_nombre;
    }

    public String getIns_codigo() {
        return ins_codigo;
    }

    public void setIns_codigo(String ins_codigo) {
        this.ins_codigo = ins_codigo;
    }

    public String getIns_codigo_barras() {
        return ins_codigo_barras;
    }

    public void setIns_codigo_barras(String ins_codigo_barras) {
        this.ins_codigo_barras = ins_codigo_barras;
    }

    public BigDecimal getIns_precio_compra() {
        return ins_precio_compra;
    }

    public void setIns_precio_compra(BigDecimal ins_precio_compra) {
        this.ins_precio_compra = ins_precio_compra;
    }

    public BigDecimal getIns_stock() {
        return ins_stock;
    }

    public void setIns_stock(BigDecimal ins_stock) {
        this.ins_stock = ins_stock;
    }

    public BigDecimal getIns_stock_min() {
        return ins_stock_min;
    }

    public void setIns_stock_min(BigDecimal ins_stock_min) {
        this.ins_stock_min = ins_stock_min;
    }

    public boolean getIns_vendible() {
        return ins_vendible;
    }

    public void setIns_vendible(boolean ins_vendible) {
        this.ins_vendible = ins_vendible;
    }

    public String getIns_imagen() {
        return ins_imagen;
    }

    public void setIns_imagen(String ins_imagen) {
        this.ins_imagen = ins_imagen;
    }

    public boolean getIns_estado() {
        return ins_estado;
    }

    public void setIns_estado(boolean ins_estado) {
        this.ins_estado = ins_estado;
    }
}
