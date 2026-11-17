package com.restaurante.backend_restaurante.Model;

/**
 * Datos que envía el formulario de inicio de sesión.
 */
public record LoginRequest(String usu_login, String usu_pass) {
}
