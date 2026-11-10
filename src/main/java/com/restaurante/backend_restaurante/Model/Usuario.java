
package com.restaurante.backend_restaurante.Model;

public class Usuario {

    private int usu_id;
    private String usu_nombre;
    private String usu_apellido;
    private String usu_direccion;
    private String usu_telefono;
    private String usu_correo;
    private Integer perf_id;
    private String usu_login;
    private String usu_pass;
    private String usu_estado;

    public Usuario() {
    }

    public Usuario(int usu_id, String usu_nombre, String usu_apellido,
            String usu_direccion, String usu_telefono, String usu_correo,
            Integer perf_id, String usu_login, String usu_pass,
            String usu_estado) {
        this.usu_id = usu_id;
        this.usu_nombre = usu_nombre;
        this.usu_apellido = usu_apellido;
        this.usu_direccion = usu_direccion;
        this.usu_telefono = usu_telefono;
        this.usu_correo = usu_correo;
        this.perf_id = perf_id;
        this.usu_login = usu_login;
        this.usu_pass = usu_pass;
        this.usu_estado = usu_estado;
    }

    public int getUsu_id() {
        return usu_id;
    }

    public void setUsu_id(int usu_id) {
        this.usu_id = usu_id;
    }

    public String getUsu_nombre() {
        return usu_nombre;
    }

    public void setUsu_nombre(String usu_nombre) {
        this.usu_nombre = usu_nombre;
    }

    public String getUsu_apellido() {
        return usu_apellido;
    }

    public void setUsu_apellido(String usu_apellido) {
        this.usu_apellido = usu_apellido;
    }

    public String getUsu_direccion() {
        return usu_direccion;
    }

    public void setUsu_direccion(String usu_direccion) {
        this.usu_direccion = usu_direccion;
    }

    public String getUsu_telefono() {
        return usu_telefono;
    }

    public void setUsu_telefono(String usu_telefono) {
        this.usu_telefono = usu_telefono;
    }

    public String getUsu_correo() {
        return usu_correo;
    }

    public void setUsu_correo(String usu_correo) {
        this.usu_correo = usu_correo;
    }

    public Integer getPerf_id() {
        return perf_id;
    }

    public void setPerf_id(Integer perf_id) {
        this.perf_id = perf_id;
    }

    public String getUsu_login() {
        return usu_login;
    }

    public void setUsu_login(String usu_login) {
        this.usu_login = usu_login;
    }

    public String getUsu_pass() {
        return usu_pass;
    }

    public void setUsu_pass(String usu_pass) {
        this.usu_pass = usu_pass;
    }

    public String getUsu_estado() {
        return usu_estado;
    }

    public void setUsu_estado(String usu_estado) {
        this.usu_estado = usu_estado;
    }
}
