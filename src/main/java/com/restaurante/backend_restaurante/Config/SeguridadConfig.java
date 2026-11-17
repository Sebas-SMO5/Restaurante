package com.restaurante.backend_restaurante.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de seguridad básica del sistema:
 * - Cifrado de contraseñas con BCrypt.
 * - Protección de las rutas /api/** con la sesión del usuario.
 */
@Configuration
public class SeguridadConfig implements WebMvcConfigurer {

    private final SesionInterceptor sesionInterceptor;
    private final boolean protegerApi;

    public SeguridadConfig(
            SesionInterceptor sesionInterceptor,
            @Value("${app.seguridad.proteger-api:true}") boolean protegerApi) {
        this.sesionInterceptor = sesionInterceptor;
        this.protegerApi = protegerApi;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        if (!protegerApi) {
            return;
        }

        registry.addInterceptor(sesionInterceptor)
                .addPathPatterns("/api/**")
                // /api/publico/** lo usa la página pública de PQRS (pqrs.html), sin iniciar sesión.
                .excludePathPatterns("/api/auth/**", "/api/publico/**");
    }
}
