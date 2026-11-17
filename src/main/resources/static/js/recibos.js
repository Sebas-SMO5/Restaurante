
/*
 * Pantalla de Recibos de caja: cobra los pedidos finalizados, genera el recibo,
 * permite imprimirlo y (solo administradores) anularlo.
 */
window.inicializarRecibos = function () {
    const vista = document.getElementById("vistaRecibos");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    const $ = (id) => document.getElementById(id);
    const CONSUMIDOR_FINAL = "222222222222";

    const tablaPorCobrar = $("tablaPorCobrar");
    const tablaRecibos = $("tablaRecibos");
    const buscar = $("buscarRecibo");
    const filtros = $("filtroRecibos");
    const mensajeListado = $("recibosListadoMensaje");

    const cobro = {
        seccion: $("cobroSection"),
        titulo: $("cobroTitulo"),
        subtitulo: $("cobroSubtitulo"),
        items: $("cobroItems"),
        total: $("cobroTotal"),
        form: $("cobroForm"),
        buscarCliente: $("cobroBuscarCliente"),
        cliente: $("cobroCliente"),
        formaPago: $("cobroFormaPago"),
        campoRecibido: $("campoRecibido"),
        recibido: $("cobroRecibido"),
        cambio: $("cobroCambio"),
        observacion: $("cobroObservacion"),
        mensaje: $("cobroMensaje"),
        boton: $("btnGenerarRecibo")
    };

    const recibo = {
        seccion: $("reciboSection"),
        mensaje: $("reciboMensaje"),
        imprimible: $("reciboImprimible"),
        anular: $("btnAnularRecibo")
    };

    let porCobrar = [];
    let recibos = [];
    let clientes = [];
    let formasPago = [];
    let flujo = { detalleCancelado: 3 };
    let pedidoACobrar = null;
    let reciboActual = null;
    let filtro = "hoy";
    let temporizadorMensaje = null;

    /* ---------- utilidades ---------- */

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

    const esHoy = (iso) => new Date(iso).toDateString() === new Date().toDateString();
    const activo = (valor) => String(valor ?? "").toLowerCase() === "activo";
    const esAdmin = () => window.usuarioSesion?.es_admin !== false;

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

    /* ---------- carga ---------- */

    async function cargarCatalogos() {
        [clientes, formasPago, flujo] = await Promise.all([
            pedir("/api/clientes"),
            pedir("/api/formas-pago"),
            pedir("/api/pedidos/estados-flujo")
        ]);

        clientes = (clientes ?? []).filter(c => activo(c.cli_estado) && c.cli_documento);
        clientes.sort((a, b) =>
            (a.cli_documento === CONSUMIDOR_FINAL ? -1 : b.cli_documento === CONSUMIDOR_FINAL ? 1 : 0) ||
            `${a.cli_nombre} ${a.cli_apellidos}`.localeCompare(`${b.cli_nombre} ${b.cli_apellidos}`, "es"));

        formasPago = (formasPago ?? []).filter(f => activo(f.fp_estado)).sort((a, b) => a.fp_id - b.fp_id);
    }

    async function cargarListas() {
        try {
            [porCobrar, recibos] = await Promise.all([
                pedir("/api/recibos-caja/por-cobrar"),
                pedir("/api/recibos-caja/resumen")
            ]);

            const deHoy = recibos.filter(r => activo(r.rc_estado) && esHoy(r.rc_fecha));

            $("recibosPorCobrar").textContent = porCobrar.length;
            $("recibosPorCobrarValor").textContent = `${pesos(porCobrar.reduce((s, p) => s + p.total, 0))} pendientes`;
            $("recibosHoyValor").textContent = pesos(deHoy.reduce((s, r) => s + r.rc_total, 0));
            $("recibosHoyCantidad").textContent = `${deHoy.length} recibo(s)`;
            $("recibosAnulados").textContent = recibos.filter(r => !activo(r.rc_estado)).length;

            pintarPorCobrar();
            pintarRecibos();

        } catch (error) {
            console.error(error);
            avisarListado(error.message, "error");
        }
    }

    async function cargarTodo() {
        try {
            await cargarCatalogos();
        } catch (error) {
            console.error(error);
            avisarListado("No se pudieron cargar los clientes o las formas de pago.", "error");
        }

        await cargarListas();
    }

    /* ---------- listas ---------- */

    function pintarPorCobrar() {
        tablaPorCobrar.innerHTML = porCobrar.length
            ? porCobrar.map(p => `
                <tr>
                    <td><strong>#${p.ped_id}</strong></td>
                    <td><span class="badge badge-neutral">Mesa ${escapar(p.ped_mesa)}</span></td>
                    <td>${fechaHora(p.ped_fecha)}</td>
                    <td>${escapar(p.mesero ?? "—")}</td>
                    <td>${p.items}</td>
                    <td><strong>${pesos(p.total)}</strong></td>
                    <td>
                        <button class="action-button" type="button" data-cobrar="${p.ped_id}">Cobrar</button>
                    </td>
                </tr>
            `).join("")
            : `<tr><td colspan="7">No hay pedidos pendientes de cobro. Los pedidos aparecen aquí cuando se finalizan.</td></tr>`;
    }

    function pintarRecibos() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = recibos.filter(r => {
            const pasa =
                filtro === "todos" ||
                (filtro === "hoy" && esHoy(r.rc_fecha)) ||
                (filtro === "activos" && activo(r.rc_estado)) ||
                (filtro === "anulados" && !activo(r.rc_estado));

            return pasa && [`#${r.rc_num}`, r.cliente, r.cli_documento, r.ped_mesa, `#${r.ped_id}`]
                .some(v => String(v ?? "").toLowerCase().includes(texto));
        });

        tablaRecibos.innerHTML = filtrados.length
            ? filtrados.map(r => `
                <tr class="${activo(r.rc_estado) ? "" : "row-muted"}">
                    <td><strong>#${r.rc_num}</strong></td>
                    <td>${fechaHora(r.rc_fecha)}</td>
                    <td>${escapar(r.cliente)}<br><small>${escapar(r.cli_documento)}</small></td>
                    <td>#${r.ped_id} · Mesa ${escapar(r.ped_mesa)}</td>
                    <td>${escapar(r.forma_pago)}</td>
                    <td>${pesos(r.rc_total)}</td>
                    <td><span class="badge ${activo(r.rc_estado) ? "badge-success" : "badge-muted"}">${escapar(r.rc_estado)}</span></td>
                    <td><button class="action-button" type="button" data-ver="${r.rc_num}">Ver</button></td>
                </tr>
            `).join("")
            : `<tr><td colspan="8">${filtro === "hoy" && !texto ? "Aún no hay recibos hoy." : "No hay recibos que coincidan."}</td></tr>`;
    }

    /* ---------- cobro ---------- */

    function llenarClientes() {
        const texto = cobro.buscarCliente.value.trim().toLowerCase();
        const actual = cobro.cliente.value;

        const lista = clientes.filter(c =>
            !texto ||
            String(c.cli_documento).toLowerCase().includes(texto) ||
            `${c.cli_nombre} ${c.cli_apellidos}`.toLowerCase().includes(texto));

        cobro.cliente.innerHTML = lista.length
            ? lista.map(c => `
                <option value="${c.cli_id}">
                    ${escapar(c.tipo_sigla)} ${escapar(c.cli_documento)} — ${escapar(c.cli_nombre)} ${escapar(c.cli_apellidos)}
                </option>`).join("")
            : `<option value="">Ningún cliente coincide</option>`;

        if (lista.some(c => String(c.cli_id) === actual)) {
            cobro.cliente.value = actual;
        }
    }

    function esEfectivo() {
        const fp = formasPago.find(f => String(f.fp_id) === cobro.formaPago.value);
        return String(fp?.fp_descripcion ?? "").toLowerCase().includes("efectivo");
    }

    function actualizarCambio() {
        const efectivo = esEfectivo();
        cobro.campoRecibido.classList.toggle("hidden", !efectivo);

        const recibido = Number(cobro.recibido.value);

        if (!efectivo || !cobro.recibido.value) {
            cobro.cambio.classList.add("hidden");
            return;
        }

        const diferencia = recibido - pedidoACobrar.total;
        cobro.cambio.classList.remove("hidden");
        cobro.cambio.classList.toggle("falta", diferencia < 0);
        cobro.cambio.textContent = diferencia >= 0
            ? `Cambio a devolver: ${pesos(diferencia)}`
            : `Faltan ${pesos(-diferencia)}`;
    }

    async function abrirCobro(pedId) {
        pedidoACobrar = porCobrar.find(p => p.ped_id === pedId);
        if (!pedidoACobrar) return;

        try {
            const detalle = await pedir(`/api/pedidos/${pedId}/completo`);
            const cobrables = detalle.items.filter(i => i.est_id !== flujo.detalleCancelado);

            cobro.items.innerHTML = cobrables.map(i => `
                <tr>
                    <td>${escapar(i.plato)}</td>
                    <td>${i.ped_det_cant}</td>
                    <td>${pesos(i.ped_det_precio)}</td>
                    <td>${pesos(i.subtotal)}</td>
                </tr>`).join("");

        } catch (error) {
            avisarListado(error.message, "error");
            return;
        }

        cobro.titulo.textContent = `Cobrar pedido #${pedidoACobrar.ped_id} · Mesa ${pedidoACobrar.ped_mesa}`;
        cobro.subtitulo.textContent = `${fechaHora(pedidoACobrar.ped_fecha)} · Atendido por ${pedidoACobrar.mesero ?? "—"}`;
        cobro.total.textContent = pesos(pedidoACobrar.total);

        cobro.form.reset();
        llenarClientes();
        const consumidor = clientes.find(c => c.cli_documento === CONSUMIDOR_FINAL);
        if (consumidor) cobro.cliente.value = consumidor.cli_id;

        cobro.formaPago.innerHTML = formasPago.map(f =>
            `<option value="${f.fp_id}">${escapar(f.fp_descripcion)}</option>`).join("");

        ocultarMensaje(cobro.mensaje);
        actualizarCambio();

        recibo.seccion.classList.add("hidden");
        cobro.seccion.classList.remove("hidden");
        cobro.seccion.scrollIntoView({ behavior: "smooth", block: "start" });
    }

    /* ---------- recibo ---------- */

    async function verRecibo(rcNum, mensaje = null) {
        try {
            reciboActual = await pedir(`/api/recibos-caja/${rcNum}/completo`);
        } catch (error) {
            avisarListado(error.message, "error");
            return;
        }

        const r = reciboActual.recibo;
        const anulado = !activo(r.rc_estado);

        recibo.imprimible.innerHTML = `
            <header>
                <div>
                    <h2>Restaurante</h2>
                    <p>Sistema de gestión</p>
                </div>
                <div class="receipt-number">
                    <span>Recibo de caja</span>
                    <strong>N.° ${r.rc_num}</strong>
                    ${anulado ? `<span class="badge badge-muted">ANULADO</span>` : ""}
                </div>
            </header>

            <dl>
                <div><dt>Fecha</dt><dd>${fechaHora(r.rc_fecha)}</dd></div>
                <div><dt>Cliente</dt><dd>${escapar(r.cliente)} · ${escapar(r.cli_documento)}</dd></div>
                <div><dt>Pedido</dt><dd>#${r.ped_id} · Mesa ${escapar(r.ped_mesa)}</dd></div>
                <div><dt>Forma de pago</dt><dd>${escapar(r.forma_pago)}</dd></div>
                <div><dt>Atendió</dt><dd>${escapar(r.cajero ?? "—")}</dd></div>
            </dl>

            <table>
                <thead>
                    <tr><th>Plato</th><th>Cant.</th><th>Precio</th><th>Subtotal</th></tr>
                </thead>
                <tbody>
                    ${reciboActual.items.map(i => `
                        <tr>
                            <td>${escapar(i.plato)}</td>
                            <td>${Number(i.rcd_cantidad)}</td>
                            <td>${pesos(i.rcd_precio)}</td>
                            <td>${pesos(i.subtotal)}</td>
                        </tr>`).join("")}
                </tbody>
                <tfoot>
                    <tr><th colspan="3">Total</th><th>${pesos(r.rc_total)}</th></tr>
                </tfoot>
            </table>

            ${r.rc_observacion ? `<p class="receipt-note">${escapar(r.rc_observacion)}</p>` : ""}
            <p class="receipt-thanks">¡Gracias por su visita!</p>
        `;

        recibo.anular.classList.toggle("hidden", anulado || !esAdmin());

        if (mensaje) {
            mostrarMensaje(recibo.mensaje, mensaje);
        } else {
            ocultarMensaje(recibo.mensaje);
        }

        cobro.seccion.classList.add("hidden");
        recibo.seccion.classList.remove("hidden");
        recibo.seccion.scrollIntoView({ behavior: "smooth", block: "start" });
    }

    /* ---------- eventos ---------- */

    $("btnActualizarRecibos").addEventListener("click", cargarTodo);
    $("btnCerrarCobro").addEventListener("click", () => cobro.seccion.classList.add("hidden"));
    $("btnCerrarRecibo").addEventListener("click", () => recibo.seccion.classList.add("hidden"));
    $("btnImprimirRecibo").addEventListener("click", () => window.print());

    cobro.buscarCliente.addEventListener("input", llenarClientes);
    cobro.formaPago.addEventListener("change", actualizarCambio);
    cobro.recibido.addEventListener("input", actualizarCambio);

    tablaPorCobrar.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-cobrar]");
        if (boton) abrirCobro(Number(boton.dataset.cobrar));
    });

    tablaRecibos.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-ver]");
        if (boton) verRecibo(Number(boton.dataset.ver));
    });

    cobro.form.addEventListener("submit", async evento => {
        evento.preventDefault();

        if (!cobro.cliente.value || !cobro.formaPago.value) {
            mostrarMensaje(cobro.mensaje, "Selecciona el cliente y la forma de pago.", "error");
            return;
        }

        if (esEfectivo() && cobro.recibido.value && Number(cobro.recibido.value) < pedidoACobrar.total) {
            mostrarMensaje(cobro.mensaje, "El valor recibido es menor que el total.", "error");
            return;
        }

        cobro.boton.disabled = true;

        try {
            const resultado = await pedir("/api/recibos-caja/cobrar", {
                method: "POST",
                body: JSON.stringify({
                    ped_id: pedidoACobrar.ped_id,
                    cli_id: Number(cobro.cliente.value),
                    fp_id: Number(cobro.formaPago.value),
                    rc_observacion: cobro.observacion.value.trim() || null
                })
            });

            await cargarListas();
            await verRecibo(resultado.rc_num, resultado.mensaje);

        } catch (error) {
            mostrarMensaje(cobro.mensaje, error.message, "error");
        } finally {
            cobro.boton.disabled = false;
        }
    });

    recibo.anular.addEventListener("click", async () => {
        const r = reciboActual?.recibo;
        if (!r) return;

        const motivo = prompt(`Motivo para anular el recibo N.° ${r.rc_num} (mínimo 5 caracteres):`);
        if (motivo === null) return;

        try {
            const resultado = await pedir(`/api/recibos-caja/${r.rc_num}/anular`, {
                method: "PUT",
                body: JSON.stringify({ motivo })
            });

            await cargarListas();
            await verRecibo(r.rc_num, resultado);

        } catch (error) {
            mostrarMensaje(recibo.mensaje, error.message, "error");
        }
    });

    filtros.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-filtro]");
        if (!boton) return;
        filtro = boton.dataset.filtro;
        filtros.querySelectorAll("button").forEach(b => b.classList.toggle("active", b === boton));
        pintarRecibos();
    });

    buscar.addEventListener("input", pintarRecibos);

    cargarTodo();
};
