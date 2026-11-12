package com.restaurante.backend_restaurante.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Plato {

    int idPlato;
    int idEstado;
    String descripcion;
    float precio;

    public Plato() {
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Plato(
            @JsonProperty("descripcion") String descripcion,
            @JsonProperty("precio") float precio,
            @JsonProperty("idEstado") int idEstado) {

        this.descripcion = descripcion;
        this.precio = precio;
        this.idEstado = idEstado;
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