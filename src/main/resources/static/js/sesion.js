
/*
 * Control de sesión del sistema.
 * - Verifica que haya una sesión iniciada; si no, envía a /login.html.
 * - Si cualquier petición a la API responde 401 (sesión vencida), vuelve al login.
 * - Muestra el usuario conectado y el botón "Cerrar sesión" en la barra superior.
 *
 * Debe cargarse antes que los demás scripts en index.html.
 */
(function () {
    const fetchOriginal = window.fetch.bind(window);
    let redirigiendo = false;

    const irAlLogin = (motivo) => {
        if (redirigiendo) return;
        redirigiendo = true;
        window.location.replace(`/login.html${motivo ? `?motivo=${motivo}` : ""}`);
    };

    window.fetch = async function (...argumentos) {
        const respuesta = await fetchOriginal(...argumentos);
        const url = String(argumentos[0] instanceof Request ? argumentos[0].url : argumentos[0]);

        if (respuesta.status === 401 && url.includes("/api/") && !url.includes("/api/auth/")) {
            irAlLogin("expirada");
        }

        return respuesta;
    };

    window.usuarioSesion = null;

    function pintarUsuario(usuario) {
        const nombre = document.getElementById("sesionNombre");
        const rol = document.getElementById("sesionRol");
        const iniciales = document.getElementById("sesionIniciales");

        const nombreCompleto = `${usuario.usu_nombre ?? ""} ${usuario.usu_apellido ?? ""}`.trim()
            || usuario.usu_login;

        if (nombre) nombre.textContent = nombreCompleto;
        if (rol) {
            rol.textContent = (usuario.perf_descripcion || "Sin rol") +
                (usuario.es_admin === false ? " · Acceso limitado" : " · Acceso completo");
        }
        if (iniciales) {
            iniciales.textContent = nombreCompleto
                .split(/\s+/)
                .slice(0, 2)
                .map(parte => parte.charAt(0).toUpperCase())
                .join("");
        }
    }

    async function verificarSesion() {
        try {
            const respuesta = await fetchOriginal("/api/auth/sesion");

            if (!respuesta.ok) {
                irAlLogin();
                return;
            }

            window.usuarioSesion = await respuesta.json();
            document.dispatchEvent(new CustomEvent("sesion-lista", { detail: window.usuarioSesion }));

            // Roles que no son administradores: se ocultan las opciones que no pueden usar.
            // Solo se limita si el backend indica explícitamente que no es administrador.
            document.documentElement.classList.toggle("rol-limitado", window.usuarioSesion.es_admin === false);

            const mostrar = () => {
                pintarUsuario(window.usuarioSesion);
                document.documentElement.classList.remove("verificando-sesion");
            };

            if (document.readyState === "loading") {
                document.addEventListener("DOMContentLoaded", mostrar, { once: true });
            } else {
                mostrar();
            }

        } catch (error) {
            console.error(error);
            document.documentElement.classList.remove("verificando-sesion");
        }
    }

    async function cerrarSesion() {
        try {
            await fetchOriginal("/api/auth/logout", { method: "POST" });
        } finally {
            irAlLogin("salida");
        }
    }

    /* ---------- Cambiar mi contraseña ---------- */

    function configurarCambioContrasena() {
        const dialogo = document.getElementById("dialogoContrasena");
        const form = document.getElementById("formContrasena");
        const mensaje = document.getElementById("passMensaje");
        const boton = document.getElementById("btnGuardarContrasena");

        if (!dialogo || !form) return;

        const mostrar = (texto, tipo = "error") => {
            mensaje.textContent = texto;
            mensaje.className = `message ${tipo}`;
        };

        document.getElementById("btnMiContrasena")?.addEventListener("click", () => {
            form.reset();
            mensaje.className = "message hidden";
            dialogo.showModal();
            document.getElementById("passActual").focus();
        });

        document.getElementById("btnCancelarContrasena").addEventListener("click", () => dialogo.close());

        form.addEventListener("submit", async evento => {
            evento.preventDefault();

            const actual = document.getElementById("passActual").value;
            const nueva = document.getElementById("passNueva").value;
            const confirmar = document.getElementById("passConfirmar").value;

            if (!actual || !nueva) {
                mostrar("Escribe la contraseña actual y la nueva.");
                return;
            }

            if (nueva.length < 6) {
                mostrar("La nueva contraseña debe tener al menos 6 caracteres.");
                return;
            }

            if (nueva !== confirmar) {
                mostrar("La confirmación no coincide con la nueva contraseña.");
                return;
            }

            boton.disabled = true;

            try {
                const respuesta = await fetchOriginal("/api/auth/mi-contrasena", {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ actual, nueva })
                });

                const texto = await respuesta.text();

                if (respuesta.status === 401) {
                    irAlLogin("expirada");
                    return;
                }

                if (!respuesta.ok) {
                    throw new Error(texto || "No se pudo cambiar la contraseña.");
                }

                form.reset();
                mostrar(texto, "success");
                setTimeout(() => dialogo.close(), 1500);

            } catch (error) {
                mostrar(error.message);
            } finally {
                boton.disabled = false;
            }
        });
    }

    document.addEventListener("DOMContentLoaded", () => {
        document.getElementById("btnCerrarSesion")?.addEventListener("click", cerrarSesion);
        configurarCambioContrasena();
    });

    verificarSesion();
})();
