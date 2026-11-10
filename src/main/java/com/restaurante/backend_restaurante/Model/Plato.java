package com.restaurante.backend_restaurante.Model;

public class Plato {

    private int idPlato;
    private int idEstado;
    private String descripcion;
    private float precio;

    public Plato() {
    }

    public Plato(int idPlato, int idEstado, String descripcion, float precio) {
        this.idPlato = idPlato;
        this.idEstado = idEstado;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public int getIdPlato() {
        return idPlato;
    }

    public void setIdPlato(int idPlato) {
        this.idPlato = idPlato;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }
}