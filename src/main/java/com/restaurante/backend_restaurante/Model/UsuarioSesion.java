package com.restaurante.backend_restaurante.Model;

import java.io.Serializable;

public record UsuarioSesion(
        int usu_id,
        String usu_nombre,
        String usu_apellido,
        String usu_login,
        Integer perf_id,
        String perf_descripcion,
        boolean es_admin
) implements Serializable {

    public static final String ATRIBUTO = "usuarioSesion";
}
