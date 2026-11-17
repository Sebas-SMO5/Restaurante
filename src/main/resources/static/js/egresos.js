
/*
 * Pantalla de Comprobantes de egreso: registra las salidas de dinero y permite anularlas.
 * Registrar y anular son acciones de administrador (el backend lo valida).
 */
window.inicializarEgresos = function () {
    const vista = document.getElementById("vistaEgresos");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    const $ = (id) => document.getElementById(id);

    const tabla = $("tablaEgresos");
    const buscar = $("buscarEgreso");
    const filtros = $("filtroEgresos");
    const mensajeListado = $("egresosListadoMensaje");

    const form = {
        seccion: $("egresoFormSection"),
        form: $("egresoForm"),
        concepto: $("egresoConcepto"),
        formaPago: $("egresoFormaPago"),
        valor: $("egresoValor"),
        tercero: $("egresoTercero"),
        documento: $("egresoDocumento"),
        detalle: $("egresoDetalle"),
        mensaje: $("egresoMensaje"),
        boton: $("btnGuardarEgreso")
    };

    let egresos = [];
    let filtro = "mes";
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const pesos = (valor) => Number(valor ?? 0).toLocaleString("es-CO", {
        style: "currency", currency: "COP", maximumFractionDigits: 0
    });

    const fechaHora = (iso) => new Date(iso).toLocaleString("es-CO", {
        day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit"
    });

    const activo = (valor) => String(valor ?? "").toLowerCase() === "activo";
    const esHoy = (iso) => new Date(iso).toDateString() === new Date().toDateString();
    const esEsteMes = (iso) => {
        const fecha = new Date(iso);
        const hoy = new Date();
        return fecha.getFullYear() === hoy.getFullYear() && fecha.getMonth() === hoy.getMonth();
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

    async function cargarCatalogos() {
        const [conceptos, formas] = await Promise.all([pedir("/api/conceptos"), pedir("/api/formas-pago")]);

        form.concepto.innerHTML = `<option value="">Seleccione un concepto</option>` +
            (conceptos ?? []).filter(c => activo(c.con_estado))
                .map(c => `<option value="${c.con_id}">${escapar(c.con_descripcion)}</option>`).join("");

        form.formaPago.innerHTML = `<option value="">Seleccione una forma de pago</option>` +
            (formas ?? []).filter(f => activo(f.fp_estado))
                .map(f => `<option value="${f.fp_id}">${escapar(f.fp_descripcion)}</option>`).join("");
    }

    async function cargarEgresos() {
        try {
            egresos = await pedir("/api/egresos/resumen") ?? [];

            const vigentes = egresos.filter(e => activo(e.estado));
            const hoy = vigentes.filter(e => esHoy(e.fecha_documento));

            $("egresosHoy").textContent = pesos(hoy.reduce((s, e) => s + e.valor_egreso, 0));
            $("egresosHoyCantidad").textContent = `${hoy.length} comprobante(s)`;
            $("egresosMes").textContent = pesos(vigentes
                .filter(e => esEsteMes(e.fecha_documento))
                .reduce((s, e) => s + e.valor_egreso, 0));
            $("egresosTotal").textContent = egresos.length;
            $("egresosAnulados").textContent = `${egresos.length - vigentes.length} anulado(s)`;

            pintar();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="9">Error al cargar los comprobantes.</td></tr>`;
        }
    }

    function pintar() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = egresos.filter(e => {
            const pasa =
                filtro === "todos" ||
                (filtro === "hoy" && esHoy(e.fecha_documento)) ||
                (filtro === "mes" && esEsteMes(e.fecha_documento)) ||
                (filtro === "anulados" && !activo(e.estado));

            return pasa && [e.no_egreso, e.tercero_identificacion, e.concepto, e.detalle, e.no_documento]
                .some(v => String(v ?? "").toLowerCase().includes(texto));
        });

        tabla.innerHTML = filtrados.length
            ? filtrados.map(e => `
                <tr class="${activo(e.estado) ? "" : "row-muted"}">
                    <td><strong>${e.no_egreso}</strong></td>
                    <td>${fechaHora(e.fecha_documento)}</td>
                    <td>${escapar(e.concepto)}</td>
                    <td>${escapar(e.tercero_identificacion)}</td>
                    <td>
                        ${escapar(e.detalle)}
                        ${e.no_documento ? `<br><small>Soporte: ${escapar(e.no_documento)}</small>` : ""}
                        ${e.registrado_por ? `<br><small>Registró: ${escapar(e.registrado_por)}</small>` : ""}
                    </td>
                    <td>${escapar(e.forma_pago)}</td>
                    <td>${pesos(e.valor_egreso)}</td>
                    <td><span class="badge ${activo(e.estado) ? "badge-success" : "badge-muted"}">${escapar(e.estado)}</span></td>
                    <td>
                        ${activo(e.estado)
                            ? `<button class="action-button action-button-light" type="button" data-anular="${e.id}">Anular</button>`
                            : ""}
                    </td>
                </tr>
            `).join("")
            : `<tr><td colspan="9">No hay comprobantes que coincidan.</td></tr>`;
    }

    function abrirFormulario() {
        form.form.reset();
        ocultarMensaje(form.mensaje);
        form.seccion.classList.remove("hidden");
        form.seccion.scrollIntoView({ behavior: "smooth", block: "start" });
        form.concepto.focus({ preventScroll: true });
    }

    const cerrarFormulario = () => form.seccion.classList.add("hidden");

    $("btnNuevoEgreso").addEventListener("click", abrirFormulario);
    $("btnCerrarEgreso").addEventListener("click", cerrarFormulario);
    $("btnCancelarEgreso").addEventListener("click", cerrarFormulario);
    $("btnActualizarEgresos").addEventListener("click", cargarEgresos);
    buscar.addEventListener("input", pintar);

    form.tercero.addEventListener("input", () => {
        form.tercero.value = form.tercero.value.toUpperCase();
    });

    filtros.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-filtro]");
        if (!boton) return;
        filtro = boton.dataset.filtro;
        filtros.querySelectorAll("button").forEach(b => b.classList.toggle("active", b === boton));
        pintar();
    });

    form.form.addEventListener("submit", async evento => {
        evento.preventDefault();

        const datos = {
            conceptoEgreso_codigo: Number(form.concepto.value) || null,
            fp_id: Number(form.formaPago.value) || null,
            valor_egreso: Number(form.valor.value) || null,
            tercero_identificacion: form.tercero.value.trim(),
            no_documento: form.documento.value.trim() || null,
            detalle: form.detalle.value.trim()
        };

        if (!datos.conceptoEgreso_codigo || !datos.fp_id) {
            mostrarMensaje(form.mensaje, "Selecciona el concepto y la forma de pago.", "error");
            return;
        }

        if (!datos.valor_egreso || datos.valor_egreso <= 0) {
            mostrarMensaje(form.mensaje, "El valor debe ser mayor que cero.", "error");
            return;
        }

        if (!/^[A-Za-z0-9-]{4,15}$/.test(datos.tercero_identificacion)) {
            mostrarMensaje(form.mensaje, "La identificación del tercero debe tener entre 4 y 15 caracteres.", "error");
            return;
        }

        if (!datos.detalle) {
            mostrarMensaje(form.mensaje, "Escribe el detalle del egreso.", "error");
            return;
        }

        form.boton.disabled = true;

        try {
            const resultado = await pedir("/api/egresos/registrar", { method: "POST", body: JSON.stringify(datos) });
            cerrarFormulario();
            await cargarEgresos();
            avisarListado(resultado.mensaje);
        } catch (error) {
            mostrarMensaje(form.mensaje, error.message, "error");
        } finally {
            form.boton.disabled = false;
        }
    });

    tabla.addEventListener("click", async evento => {
        const boton = evento.target.closest("[data-anular]");
        if (!boton) return;

        const egreso = egresos.find(e => String(e.id) === boton.dataset.anular);
        const motivo = prompt(`Motivo para anular el comprobante N.° ${egreso?.no_egreso} (mínimo 5 caracteres):`);
        if (motivo === null) return;

        try {
            const resultado = await pedir(`/api/egresos/${boton.dataset.anular}/anular`, {
                method: "PUT",
                body: JSON.stringify({ motivo })
            });
            await cargarEgresos();
            avisarListado(resultado);
        } catch (error) {
            avisarListado(error.message, "error");
        }
    });

    cargarCatalogos().catch(error => avisarListado(error.message, "error"));
    cargarEgresos();
};
