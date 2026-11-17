
/*
 * Página pública de PQRS (/pqrs.html): radicar y consultar sin iniciar sesión.
 * Usa solo las rutas /api/publico/..., que no exigen sesión.
 */
(function () {
    const $ = (id) => document.getElementById(id);

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const fecha = (iso) => iso
        ? new Date(iso).toLocaleString("es-CO", { day: "2-digit", month: "long", year: "numeric", hour: "2-digit", minute: "2-digit" })
        : "";

    const mostrar = (elemento, texto, tipo = "error") => {
        elemento.textContent = texto;
        elemento.classList.remove("hidden", "success", "error");
        elemento.classList.add(tipo);
    };

    const ocultar = (elemento) => elemento.classList.add("hidden");

    const errorDeRed = (error) => error instanceof TypeError
        ? "No hay conexión con el servidor. Inténtalo de nuevo en unos minutos."
        : error.message;

    async function leerError(respuesta) {
        const texto = (await respuesta.text()).trim();
        return !texto || texto.startsWith("{") || texto.startsWith("<")
            ? "No fue posible completar la solicitud. Inténtalo de nuevo."
            : texto;
    }

    /* ---------- pestañas ---------- */

    document.querySelector(".pqrs-tabs").addEventListener("click", evento => {
        const boton = evento.target.closest("[data-tab]");
        if (!boton) return;

        document.querySelectorAll(".pqrs-tabs button").forEach(b => b.classList.toggle("active", b === boton));
        $("tabRadicar").classList.toggle("hidden", boton.dataset.tab !== "radicar");
        $("tabConsultar").classList.toggle("hidden", boton.dataset.tab !== "consultar");
    });

    /* ---------- radicar ---------- */

    const radicar = {
        form: $("radicarForm"),
        mensaje: $("radicarMensaje"),
        tipo: $("pubTipo"),
        nombre: $("pubNombre"),
        correo: $("pubCorreo"),
        telefono: $("pubTelefono"),
        descripcion: $("pubDescripcion"),
        contador: $("pubContador"),
        trampa: $("pubSitioWeb"),
        boton: $("btnRadicar"),
        ok: $("radicadoOk")
    };

    fetch("/api/publico/pqrs/tipos")
        .then(r => r.ok ? r.json() : [])
        .then(tipos => {
            radicar.tipo.innerHTML = `<option value="">Selecciona el tipo</option>` +
                tipos.map(t => `<option value="${t.tpqrs_id}">${escapar(t.tpqrs_descripcion)}</option>`).join("");
        })
        .catch(() => {
            radicar.tipo.innerHTML = `<option value="">No se pudieron cargar los tipos</option>`;
        });

    radicar.descripcion.addEventListener("input", () => {
        radicar.contador.textContent = `${radicar.descripcion.value.length} / 2000`;
    });

    radicar.form.addEventListener("submit", async evento => {
        evento.preventDefault();
        ocultar(radicar.mensaje);

        const datos = {
            tpqrs_id: Number(radicar.tipo.value) || null,
            pqrs_nombre: radicar.nombre.value.trim(),
            pqrs_correo: radicar.correo.value.trim(),
            pqrs_telefono: radicar.telefono.value.trim(),
            pqrs_descripcion: radicar.descripcion.value.trim(),
            sitio_web: radicar.trampa.value
        };

        if (!datos.tpqrs_id || !datos.pqrs_nombre || !datos.pqrs_correo || !datos.pqrs_telefono || !datos.pqrs_descripcion) {
            mostrar(radicar.mensaje, "Completa todos los campos.");
            return;
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(datos.pqrs_correo)) {
            mostrar(radicar.mensaje, "Escribe un correo válido.");
            return;
        }

        if (datos.pqrs_descripcion.length < 15) {
            mostrar(radicar.mensaje, "Cuéntanos un poco más: la descripción debe tener al menos 15 caracteres.");
            return;
        }

        radicar.boton.disabled = true;
        radicar.boton.textContent = "Enviando...";

        try {
            const respuesta = await fetch("/api/publico/pqrs", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(datos)
            });

            if (!respuesta.ok) {
                throw new Error(await leerError(respuesta));
            }

            const resultado = await respuesta.json();

            $("radicadoNumero").textContent = resultado.pqrs_id;
            $("consNumero").value = resultado.pqrs_id;
            $("consCorreo").value = datos.pqrs_correo;

            radicar.form.classList.add("hidden");
            radicar.ok.classList.remove("hidden");

        } catch (error) {
            mostrar(radicar.mensaje, errorDeRed(error));
        } finally {
            radicar.boton.disabled = false;
            radicar.boton.textContent = "Enviar PQRS";
        }
    });

    $("btnOtraPqrs").addEventListener("click", () => {
        radicar.form.reset();
        radicar.contador.textContent = "0 / 2000";
        radicar.ok.classList.add("hidden");
        radicar.form.classList.remove("hidden");
    });

    /* ---------- consultar ---------- */

    const consultar = {
        form: $("consultarForm"),
        mensaje: $("consultarMensaje"),
        numero: $("consNumero"),
        correo: $("consCorreo"),
        boton: $("btnConsultar"),
        resultado: $("consultaResultado")
    };

    consultar.form.addEventListener("submit", async evento => {
        evento.preventDefault();
        ocultar(consultar.mensaje);
        consultar.resultado.classList.add("hidden");

        const numero = consultar.numero.value.trim();
        const correo = consultar.correo.value.trim();

        if (!numero || !correo) {
            mostrar(consultar.mensaje, "Escribe el número de radicado y el correo.");
            return;
        }

        consultar.boton.disabled = true;

        try {
            const respuesta = await fetch(
                `/api/publico/pqrs/consulta?numero=${encodeURIComponent(numero)}&correo=${encodeURIComponent(correo)}`);

            if (!respuesta.ok) {
                throw new Error(await leerError(respuesta));
            }

            const p = await respuesta.json();
            const respondida = Boolean(p.pqrs_respuesta);

            consultar.resultado.innerHTML = `
                <header>
                    <strong>Radicado N.° ${p.pqrs_id} · ${escapar(p.tipo)}</strong>
                    <span class="badge ${respondida ? "badge-success" : "badge-warning"}">${escapar(p.estado)}</span>
                </header>
                <p class="consulta-fecha">Radicada el ${fecha(p.pqrs_fecha)}</p>
                <h3>Tu mensaje</h3>
                <p class="pqrs-texto">${escapar(p.pqrs_descripcion)}</p>
                <h3>Respuesta del restaurante</h3>
                ${respondida
                    ? `<p class="pqrs-texto pqrs-respuesta">${escapar(p.pqrs_respuesta)}</p>
                       <p class="consulta-fecha">Respondida el ${fecha(p.pqrs_fecha_respuesta)}</p>`
                    : `<p class="pqrs-texto">Aún estamos revisando tu caso. Te responderemos lo antes posible.</p>`}
            `;

            consultar.resultado.classList.remove("hidden");

        } catch (error) {
            mostrar(consultar.mensaje, errorDeRed(error));
        } finally {
            consultar.boton.disabled = false;
        }
    });
})();
