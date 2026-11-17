package com.restaurante.backend_restaurante.Model;

public class Plato {

    private int idPlato;
    private int idEstado;
    private Integer idCategoria;
    private String descripcion;
    private float precio;

    // Solo lectura: se obtiene con un JOIN para mostrar el nombre de la categoría.
    private String nombreCategoria;

    public Plato() {
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

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
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

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    // Solo lectura: nombre del estado y si ese estado es realmente de tipo "Plato".
    private String nombreEstado;
    private boolean estadoValido;

    public String getNombreEstado() {
        return nombreEstado;
    }

    public void setNombreEstado(String nombreEstado) {
        this.nombreEstado = nombreEstado;
    }

    public boolean isEstadoValido() {
        return estadoValido;
    }

    public void setEstadoValido(boolean estadoValido) {
        this.estadoValido = estadoValido;
    }
}
