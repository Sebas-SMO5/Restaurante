
(function () {
    const formulario = document.getElementById("loginForm");
    const campoUsuario = document.getElementById("loginUsuario");
    const campoPass = document.getElementById("loginPass");
    const botonIngresar = document.getElementById("btnIngresar");
    const botonVerPass = document.getElementById("btnVerPass");
    const mensaje = document.getElementById("loginMensaje");

    const mostrarMensaje = (texto, tipo = "error") => {
        mensaje.textContent = texto;
        mensaje.classList.remove("hidden", "success", "error");
        mensaje.classList.add(tipo);
    };

    /**
     * Devuelve un mensaje entendible para el usuario.
     * El backend responde texto plano; si llega un error técnico (JSON de Spring),
     * se traduce según el código HTTP.
     */
    const mensajeDeError = async (respuesta) => {
        const texto = (await respuesta.text()).trim();
        const esTecnico = !texto || texto.startsWith("{") || texto.startsWith("<");

        if (!esTecnico) return texto;

        if (respuesta.status === 404) {
            return "El servicio de inicio de sesión no está disponible. Reinicia el backend e inténtalo de nuevo.";
        }

        if (respuesta.status >= 500) {
            return "Ocurrió un error en el servidor. Inténtalo de nuevo en unos minutos.";
        }

        return "No fue posible iniciar sesión.";
    };

    const ocultarMensaje = () => {
        mensaje.textContent = "";
        mensaje.classList.add("hidden");
    };

    // Si ya hay una sesión activa, se entra directo al sistema.
    fetch("/api/auth/sesion")
        .then(respuesta => {
            if (respuesta.ok) {
                window.location.replace("/");
            }
        })
        .catch(() => { /* Sin conexión: se queda en el login. */ });

    const parametros = new URLSearchParams(window.location.search);

    if (parametros.get("motivo") === "expirada") {
        mostrarMensaje("Tu sesión terminó. Vuelve a iniciar sesión.", "error");
    } else if (parametros.get("motivo") === "salida") {
        mostrarMensaje("Cerraste sesión correctamente.", "success");
    }

    botonVerPass.addEventListener("click", () => {
        const oculto = campoPass.type === "password";
        campoPass.type = oculto ? "text" : "password";
        botonVerPass.textContent = oculto ? "Ocultar" : "Ver";
        botonVerPass.setAttribute("aria-label", oculto ? "Ocultar contraseña" : "Mostrar contraseña");
        campoPass.focus();
    });

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        ocultarMensaje();

        const usuario = campoUsuario.value.trim();
        const pass = campoPass.value;

        if (!usuario || !pass) {
            mostrarMensaje("Ingresa el usuario y la contraseña.");
            (usuario ? campoPass : campoUsuario).focus();
            return;
        }

        botonIngresar.disabled = true;
        botonIngresar.textContent = "Ingresando...";

        try {
            const respuesta = await fetch("/api/auth/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ usu_login: usuario, usu_pass: pass })
            });

            if (!respuesta.ok) {
                throw new Error(await mensajeDeError(respuesta));
            }

            window.location.replace("/");

        } catch (error) {
            const sinConexion = error instanceof TypeError;
            mostrarMensaje(sinConexion
                ? "No hay conexión con el servidor. Verifica que el backend esté en ejecución."
                : error.message);

            campoPass.value = "";
            campoPass.focus();

        } finally {
            botonIngresar.disabled = false;
            botonIngresar.textContent = "Ingresar";
        }
    });

    campoUsuario.focus();
})();
