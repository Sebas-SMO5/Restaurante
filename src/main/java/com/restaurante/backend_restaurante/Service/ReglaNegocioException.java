package com.restaurante.backend_restaurante.Service;

/**
 * Error de una regla del negocio (por ejemplo, "la mesa ya tiene un pedido abierto").
 * Los controladores la convierten en un 400 y muestran el mensaje tal cual al usuario.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
