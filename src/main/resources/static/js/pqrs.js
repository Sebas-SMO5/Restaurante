
/*
 * Bandeja interna de PQRS: registrar (presenciales o telefónicas), ver el detalle y responder.
 * Las PQRS que radican los clientes desde /pqrs.html llegan aquí con origen "Web".
 */
window.inicializarPqrs = function () {
    const vista = document.getElementById("vistaPqrs");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    const $ = (id) => document.getElementById(id);
    const PLAZO_DIAS = 15;

    const tabla = $("tablaPqrs");
    const buscar = $("buscarPqrs");
    const filtros = $("filtroPqrs");
    const filtroTipo = $("filtroTipoPqrs");
    const mensajeListado = $("pqrsListadoMensaje");

    const form = {
        seccion: $("pqrsFormSection"),
        form: $("pqrsForm"),
        tipo: $("pqrsTipo"),
        nombre: $("pqrsNombre"),
        correo: $("pqrsCorreo"),
        telefono: $("pqrsTelefono"),
        descripcion: $("pqrsDescripcion"),
        mensaje: $("pqrsMensaje"),
        boton: $("btnGuardarPqrs")
    };

    const detalle = {
        seccion: $("pqrsDetalleSection"),
        titulo: $("pqrsDetalleTitulo"),
        subtitulo: $("pqrsDetalleSubtitulo"),
        datos: $("pqrsDatos"),
        descripcion: $("pqrsDetalleDescripcion"),
        existente: $("pqrsRespuestaExistente"),
        respuestaTexto: $("pqrsRespuestaTexto"),
        respuestaInfo: $("pqrsRespuestaInfo"),
        form: $("pqrsRespuestaForm"),
        respuesta: $("pqrsRespuesta"),
        mensaje: $("pqrsRespuestaMensaje"),
        boton: $("btnResponderPqrs")
    };

    let pqrs = [];
    let estados = { pendiente: 11, atendida: 12 };
    let actual = null;
    let filtro = "pendientes";
    let temporizadorMensaje = null;

    /* ---------- utilidades ---------- */

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const fechaHora = (iso) => iso
        ? new Date(iso).toLocaleString("es-CO", { day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" })
        : "—";

    /** Días hábiles (lunes a viernes) entre dos fechas; no descuenta festivos. */
    const dias = (desde, hasta = new Date()) => {
        const inicio = new Date(desde);
        const fin = new Date(hasta);
        inicio.setHours(0, 0, 0, 0);
        fin.setHours(0, 0, 0, 0);

        let habiles = 0;
        for (const dia = new Date(inicio); dia < fin; dia.setDate(dia.getDate() + 1)) {
            const semana = dia.getDay();
            if (semana !== 0 && semana !== 6) habiles++;
        }
        return habiles;
    };

    const pendiente = (p) => p.est_id === estados.pendiente;

    const recortar = (texto, maximo) => {
        const limpio = String(texto ?? "");
        return limpio.length > maximo ? `${limpio.slice(0, maximo)}…` : limpio;
    };

    const mostrarMensaje = (elemento, texto, tipo = "success") => {
        elemento.textContent = texto;
        elemento.classList.remove("hidden", "success", "error");
        elemento.classList.add(tipo);
    };

    const ocultarMensaje = (elemento) => {
        elemento.textContent = "";
        elemento.classList.add("hidden");
    };

    const avisarListado = (texto, tipo = "success") => {
        mostrarMensaje(mensajeListado, texto, tipo);
        clearTimeout(temporizadorMensaje);
        temporizadorMensaje = setTimeout(() => ocultarMensaje(mensajeListado), 5000);
    };

    async function pedir(url, opciones = {}) {
        const respuesta = await fetch(url, {
            ...opciones,
            headers: { "Content-Type": "application/json", ...(opciones.headers ?? {}) }
        });

        const texto = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(texto || "No se pudo completar la operación.");
        }

        try {
            return texto ? JSON.parse(texto) : null;
        } catch {
            return texto;
        }
    }

    function badgeEstado(p) {
        if (!pendiente(p)) {
            return `<span class="badge badge-success">${escapar(p.estado)}</span>`;
        }

        const transcurridos = dias(p.pqrs_fecha);
        const clase = transcurridos >= PLAZO_DIAS ? "badge-danger" : "badge-warning";
        return `<span class="badge ${clase}">${escapar(p.estado)} · ${transcurridos} d</span>`;
    }

    /* ---------- carga ---------- */

    async function cargarTipos() {
        const tipos = await pedir("/api/publico/pqrs/tipos") ?? [];

        form.tipo.innerHTML = `<option value="">Seleccione el tipo</option>` +
            tipos.map(t => `<option value="${t.tpqrs_id}">${escapar(t.tpqrs_descripcion)}</option>`).join("");

        filtroTipo.innerHTML = `<option value="">Todos los tipos</option>` +
            tipos.map(t => `<option value="${t.tpqrs_id}">${escapar(t.tpqrs_descripcion)}</option>`).join("");
    }

    async function cargarPqrs() {
        try {
            const datos = await pedir("/api/pqrs/resumen");
            pqrs = datos.pqrs ?? [];
            estados = { pendiente: datos.estadoPendiente, atendida: datos.estadoAtendida };

            const pendientes = pqrs.filter(pendiente);
            const atendidas = pqrs.filter(p => !pendiente(p) && p.pqrs_fecha_respuesta);
            const promedio = atendidas.length
                ? atendidas.reduce((s, p) => s + dias(p.pqrs_fecha, p.pqrs_fecha_respuesta), 0) / atendidas.length
                : null;

            $("pqrsPendientes").textContent = pendientes.length;
            $("pqrsVencidas").textContent = `${pendientes.filter(p => dias(p.pqrs_fecha) >= PLAZO_DIAS).length} con más de ${PLAZO_DIAS} días hábiles`;
            $("pqrsAtendidas").textContent = pqrs.length - pendientes.length;
            $("pqrsTiempo").textContent = `Tiempo promedio de respuesta: ${promedio === null ? "—" : `${promedio.toFixed(1)} días hábiles`}`;
            $("pqrsWeb").textContent = pqrs.filter(p => p.pqrs_origen === "Web").length;

            pintar();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="8">Error al cargar las PQRS.</td></tr>`;
        }
    }

    /* ---------- listado ---------- */

    function pintar() {
        const texto = buscar.value.trim().toLowerCase();
        const tipo = filtroTipo.value;

        const filtradas = pqrs.filter(p => {
            const pasaEstado = filtro === "todas" || (filtro === "pendientes") === pendiente(p);
            const pasaTipo = !tipo || String(p.tpqrs_id) === tipo;

            return pasaEstado && pasaTipo &&
                [p.pqrs_id, p.pqrs_nombre, p.pqrs_correo, p.pqrs_telefono, p.pqrs_descripcion]
                    .some(v => String(v ?? "").toLowerCase().includes(texto));
        });

        // Las pendientes más antiguas primero; las demás, de la más reciente a la más antigua.
        if (filtro === "pendientes") {
            filtradas.sort((a, b) => new Date(a.pqrs_fecha) - new Date(b.pqrs_fecha));
        }

        tabla.innerHTML = filtradas.length
            ? filtradas.map(p => `
                <tr>
                    <td><strong>${p.pqrs_id}</strong></td>
                    <td>${fechaHora(p.pqrs_fecha)}</td>
                    <td><span class="badge badge-neutral">${escapar(p.tipo)}</span></td>
                    <td>${escapar(p.pqrs_nombre || "—")}<br><small>${escapar(p.pqrs_correo)}</small></td>
                    <td>${escapar(recortar(p.pqrs_descripcion, 90))}</td>
                    <td>${escapar(p.pqrs_origen)}</td>
                    <td>${badgeEstado(p)}</td>
                    <td>
                        <button class="action-button" type="button" data-ver="${p.pqrs_id}">
                            ${pendiente(p) ? "Responder" : "Ver"}
                        </button>
                    </td>
                </tr>
            `).join("")
            : `<tr><td colspan="8">${filtro === "pendientes" && !texto && !tipo
                ? "No hay PQRS pendientes. ¡Todo al día!"
                : "No hay PQRS que coincidan."}</td></tr>`;
    }

    /* ---------- detalle ---------- */

    function abrirDetalle(id, mensaje = null) {
        actual = pqrs.find(p => p.pqrs_id === id);
        if (!actual) return;

        const p = actual;

        detalle.titulo.innerHTML = `PQRS N.° ${p.pqrs_id} · ${escapar(p.tipo)} ${badgeEstado(p)}`;
        detalle.subtitulo.textContent = `Radicada el ${fechaHora(p.pqrs_fecha)} · Origen: ${p.pqrs_origen}`;

        detalle.datos.innerHTML = `
            <div><dt>Presentada por</dt><dd>${escapar(p.pqrs_nombre || "—")}</dd></div>
            <div><dt>Correo</dt><dd><a href="mailto:${escapar(p.pqrs_correo)}">${escapar(p.pqrs_correo)}</a></dd></div>
            <div><dt>Teléfono</dt><dd>${escapar(p.pqrs_telefono)}</dd></div>
            <div><dt>Días hábiles transcurridos</dt><dd>${dias(p.pqrs_fecha, p.pqrs_fecha_respuesta ?? new Date())}</dd></div>
        `;

        detalle.descripcion.textContent = p.pqrs_descripcion;

        const respondida = !pendiente(p);
        detalle.existente.classList.toggle("hidden", !respondida);
        detalle.form.classList.toggle("hidden", respondida);

        if (respondida) {
            detalle.respuestaTexto.textContent = p.pqrs_respuesta ?? "(Sin texto de respuesta)";
            detalle.respuestaInfo.textContent = p.pqrs_fecha_respuesta
                ? `Respondida el ${fechaHora(p.pqrs_fecha_respuesta)} por ${p.respondido_por ?? "—"}.`
                : "";
        } else {
            detalle.respuesta.value = "";
        }

        if (mensaje) {
            mostrarMensaje(mensajeListado, mensaje);
        }

        ocultarMensaje(detalle.mensaje);
        form.seccion.classList.add("hidden");
        detalle.seccion.classList.remove("hidden");
        detalle.seccion.scrollIntoView({ behavior: "smooth", block: "start" });

        if (!respondida) {
            detalle.respuesta.focus({ preventScroll: true });
        }
    }

    /* ---------- eventos ---------- */

    $("btnNuevaPqrs").addEventListener("click", () => {
        form.form.reset();
        ocultarMensaje(form.mensaje);
        detalle.seccion.classList.add("hidden");
        form.seccion.classList.remove("hidden");
        form.seccion.scrollIntoView({ behavior: "smooth", block: "start" });
        form.tipo.focus({ preventScroll: true });
    });

    $("btnCerrarPqrs").addEventListener("click", () => form.seccion.classList.add("hidden"));
    $("btnCancelarPqrs").addEventListener("click", () => form.seccion.classList.add("hidden"));
    $("btnCerrarDetallePqrs").addEventListener("click", () => detalle.seccion.classList.add("hidden"));
    $("btnActualizarPqrs").addEventListener("click", cargarPqrs);

    buscar.addEventListener("input", pintar);
    filtroTipo.addEventListener("change", pintar);

    filtros.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-filtro]");
        if (!boton) return;
        filtro = boton.dataset.filtro;
        filtros.querySelectorAll("button").forEach(b => b.classList.toggle("active", b === boton));
        pintar();
    });

    tabla.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-ver]");
        if (boton) abrirDetalle(Number(boton.dataset.ver));
    });

    form.form.addEventListener("submit", async evento => {
        evento.preventDefault();

        const datos = {
            tpqrs_id: Number(form.tipo.value) || null,
            pqrs_nombre: form.nombre.value.trim(),
            pqrs_correo: form.correo.value.trim(),
            pqrs_telefono: form.telefono.value.trim(),
            pqrs_descripcion: form.descripcion.value.trim()
        };

        if (!datos.tpqrs_id || !datos.pqrs_nombre || !datos.pqrs_correo || !datos.pqrs_telefono || !datos.pqrs_descripcion) {
            mostrarMensaje(form.mensaje, "Completa todos los campos.", "error");
            return;
        }

        if (datos.pqrs_descripcion.length < 15) {
            mostrarMensaje(form.mensaje, "La descripción debe tener al menos 15 caracteres.", "error");
            return;
        }

        form.boton.disabled = true;

        try {
            const resultado = await pedir("/api/pqrs/registrar", { method: "POST", body: JSON.stringify(datos) });
            form.seccion.classList.add("hidden");
            await cargarPqrs();
            avisarListado(`${resultado.mensaje} Entrégale este número al cliente para que consulte la respuesta.`);
        } catch (error) {
            mostrarMensaje(form.mensaje, error.message, "error");
        } finally {
            form.boton.disabled = false;
        }
    });

    detalle.form.addEventListener("submit", async evento => {
        evento.preventDefault();

        const respuesta = detalle.respuesta.value.trim();

        if (respuesta.length < 10) {
            mostrarMensaje(detalle.mensaje, "Escribe una respuesta de al menos 10 caracteres.", "error");
            return;
        }

        detalle.boton.disabled = true;

        try {
            const resultado = await pedir(`/api/pqrs/${actual.pqrs_id}/responder`, {
                method: "PUT",
                body: JSON.stringify({ pqrs_respuesta: respuesta })
            });

            const id = actual.pqrs_id;
            await cargarPqrs();
            abrirDetalle(id, resultado);

        } catch (error) {
            mostrarMensaje(detalle.mensaje, error.message, "error");
        } finally {
            detalle.boton.disabled = false;
        }
    });

    cargarTipos().catch(error => avisarListado(error.message, "error"));
    cargarPqrs();
};
