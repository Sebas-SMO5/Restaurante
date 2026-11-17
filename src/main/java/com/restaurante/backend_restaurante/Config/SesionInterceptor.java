package com.restaurante.backend_restaurante.Config;

import com.restaurante.backend_restaurante.Model.UsuarioSesion;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Protege la API:
 * 1. Sin sesión iniciada responde 401 (el frontend redirige al login).
 * 2. Con sesión, aplica los permisos del rol; si no alcanza, responde 403.
 */
@Component
public class SesionInterceptor implements HandlerInterceptor {

    private final Permisos permisos;

    public SesionInterceptor(Permisos permisos) {
        this.permisos = permisos;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        // Las peticiones de verificación previa del navegador (CORS) no llevan sesión.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        HttpSession sesion = request.getSession(false);

        if (sesion == null || !(sesion.getAttribute(UsuarioSesion.ATRIBUTO) instanceof UsuarioSesion usuario)) {
            responder(response, HttpStatus.UNAUTHORIZED, "Debes iniciar sesión para continuar.");
            return false;
        }

        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        String rechazo = permisos.validarAcceso(ruta, request.getMethod(), usuario.es_admin());

        if (rechazo != null) {
            responder(response, HttpStatus.FORBIDDEN, rechazo);
            return false;
        }

        return true;
    }

    private void responder(HttpServletResponse response, HttpStatus estado, String mensaje) throws IOException {
        response.setStatus(estado.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(mensaje);
    }
}
