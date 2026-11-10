
package com.restaurante.backend_restaurante.Model;

public class Perfil {

    private int perf_id;
    private String perf_descripcion;
    private String perf_estado;

    public Perfil() {
    }

    public Perfil(int perf_id, String perf_descripcion, String perf_estado) {
        this.perf_id = perf_id;
        this.perf_descripcion = perf_descripcion;
        this.perf_estado = perf_estado;
    }

    public int getPerf_id() {
        return perf_id;
    }

    public void setPerf_id(int perf_id) {
        this.perf_id = perf_id;
    }

    public String getPerf_descripcion() {
        return perf_descripcion;
    }

    public void setPerf_descripcion(String perf_descripcion) {
        this.perf_descripcion = perf_descripcion;
    }

    public String getPerf_estado() {
        return perf_estado;
    }

    public void setPerf_estado(String perf_estado) {
        this.perf_estado = perf_estado;
    }
}
