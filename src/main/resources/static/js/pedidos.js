
/*
 * Pantalla de Pedidos: tomar pedidos por mesa, agregar platos, ver el detalle,
 * cancelar platos o el pedido completo y finalizarlo cuando la cocina terminó.
 */
window.inicializarPedidos = function () {
    const vista = document.getElementById("vistaPedidos");

    if (!vista) return;

    const $ = (id) => document.getElementById(id);

    if (vista.dataset.inicializada === "true") {
        vista.recargarPedidos?.();
        return;
    }

    vista.dataset.inicializada = "true";

    const API = "/api/pedidos";

    const tabla = $("tablaPedidos");
    const buscar = $("buscarPedido");
    const filtros = $("filtroEstadoPedido");
    const mensajeListado = $("pedidosListadoMensaje");

    const form = {
        seccion: $("pedidoFormSection"),
        form: $("pedidoForm"),
        titulo: $("tituloFormularioPedido"),
        subtitulo: $("subtituloFormularioPedido"),
        campoMesa: $("campoMesa"),
        mesa: $("pedidoMesa"),
        plato: $("pedidoPlato"),
        cantidad: $("pedidoCantidad"),
        observacion: $("pedidoObservacion"),
        items: $("tablaItemsNuevos"),
        total: $("totalNuevo"),
        mensaje: $("pedidoMensaje"),
        enviar: $("btnEnviarPedido")
    };

    const detalle = {
        seccion: $("pedidoDetalleSection"),
        titulo: $("detalleTitulo"),
        subtitulo: $("detalleSubtitulo"),
        mensaje: $("detalleMensaje"),
        tabla: $("tablaDetallePedido"),
        total: $("detalleTotal"),
        agregar: $("btnDetalleAgregar"),
        cancelar: $("btnDetalleCancelar"),
        finalizar: $("btnDetalleFinalizar")
    };

    let flujo = null;
    let pedidos = [];
    let platos = [];
    let itemsNuevos = [];
    let modo = { tipo: "nuevo", pedId: null };
    let detalleActual = null;
    let filtro = "abiertos";
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

    const fechaHora = (iso) => {
        const fecha = new Date(iso);
        return isNaN(fecha) ? "—" : fecha.toLocaleString("es-CO", {
            day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit"
        });
    };

    const esHoy = (iso) => new Date(iso).toDateString() === new Date().toDateString();

    const mostrarMensaje = (elemento, texto, tipo = "success") => {
        elemento.textContent = texto;
        elemento.classList.remove("hidden", "success", "error");
        elemento.classList.add(tipo);
    };

    const ocultarMensaje = (elemento) => {
        elemento.textContent = "";
        elemento.classList.add("hidden");
        elemento.classList.remove("success", "error");
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

    const abierto = (p) => p.est_id === flujo.pedidoCreado;

    function badgePedido(p) {
        const clase = p.est_id === flujo.pedidoCreado ? "badge-warning"
            : p.est_id === flujo.pedidoFinalizado ? "badge-success" : "badge-muted";
        return `<span class="badge ${clase}">${escapar(p.estado)}</span>`;
    }

    function badgeItem(item) {
        const clase = item.est_id === flujo.detallePreparacion ? "badge-warning"
            : item.est_id === flujo.detalleListo ? "badge-success" : "badge-muted";
        const texto = item.est_id === flujo.detalleListo ? "Listo" : item.estado;
        return `<span class="badge ${clase}">${escapar(texto)}</span>`;
    }

    /* ---------- carga ---------- */

    async function cargarPlatos() {
        const lista = await pedir("/api/platos") || [];

        platos = lista
            .filter(p => p.estadoValido && p.idEstado === flujo.platoDisponible)
            .sort((a, b) =>
                String(a.nombreCategoria ?? "zzz").localeCompare(String(b.nombreCategoria ?? "zzz"), "es") ||
                String(a.descripcion).localeCompare(String(b.descripcion), "es"));

        const grupos = new Map();

        platos.forEach(p => {
            const categoria = p.nombreCategoria ?? "Sin categoría";
            if (!grupos.has(categoria)) grupos.set(categoria, []);
            grupos.get(categoria).push(p);
        });

        form.plato.innerHTML = platos.length
            ? `<option value="">Seleccione un plato</option>` +
              [...grupos].map(([categoria, lista]) => `
                <optgroup label="${escapar(categoria)}">
                    ${lista.map(p => `<option value="${p.idPlato}">${escapar(p.descripcion)} — ${pesos(p.precio)}</option>`).join("")}
                </optgroup>
              `).join("")
            : `<option value="">No hay platos con estado "Disponible"</option>`;
    }

    async function cargarPedidos() {
        try {
            pedidos = await pedir(`${API}/resumen`) || [];

            const abiertos = pedidos.filter(abierto);

            $("pedidosAbiertos").textContent = abiertos.length;
            $("platosEnCocina").textContent = abiertos.reduce((suma, p) => suma + p.pendientes, 0);
            $("ventasHoy").textContent = pesos(pedidos
                .filter(p => p.est_id === flujo.pedidoFinalizado && esHoy(p.ped_fecha))
                .reduce((suma, p) => suma + p.total, 0));

            pintarPedidos();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="8">Error al cargar los pedidos.</td></tr>`;
        }
    }

    async function cargarTodo() {
        try {
            flujo = flujo ?? await pedir(`${API}/estados-flujo`);
            await Promise.all([cargarPlatos(), cargarPedidos()]);
        } catch (error) {
            console.error(error);
            avisarListado(error.message, "error");
        }
    }

    vista.recargarPedidos = cargarTodo;

    /* ---------- listado ---------- */

    function pintarPedidos() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = pedidos.filter(p => {
            const pasaEstado =
                filtro === "todos" ||
                (filtro === "abiertos" && p.est_id === flujo.pedidoCreado) ||
                (filtro === "finalizados" && p.est_id === flujo.pedidoFinalizado) ||
                (filtro === "cancelados" && p.est_id === flujo.pedidoCancelado);

            return pasaEstado && [`#${p.ped_id}`, p.ped_id, p.ped_mesa, p.mesero]
                .some(v => String(v ?? "").toLowerCase().includes(texto));
        });

        if (!filtrados.length) {
            tabla.innerHTML = `<tr><td colspan="8">${filtro === "abiertos" && !texto
                ? "No hay pedidos abiertos. Usa “+ Nuevo pedido” para tomar uno."
                : "No hay pedidos que coincidan."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtrados.map(p => `
            <tr>
                <td><strong>#${escapar(p.ped_id)}</strong></td>
                <td><span class="badge badge-neutral">Mesa ${escapar(p.ped_mesa)}</span></td>
                <td>${fechaHora(p.ped_fecha)}</td>
                <td>${escapar(p.mesero ?? "—")}</td>
                <td>
                    ${p.items}
                    ${abierto(p) && p.pendientes ? `<span class="badge badge-warning badge-small">${p.pendientes} en cocina</span>` : ""}
                </td>
                <td>${pesos(p.total)}</td>
                <td>${badgePedido(p)}</td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button" data-ver="${p.ped_id}">Ver</button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    /* ---------- formulario: nuevo pedido / agregar platos ---------- */

    function pintarItemsNuevos() {
        if (!itemsNuevos.length) {
            form.items.innerHTML = `<tr><td colspan="6">Aún no has agregado platos.</td></tr>`;
            form.total.textContent = pesos(0);
            return;
        }

        form.items.innerHTML = itemsNuevos.map((item, indice) => `
            <tr>
                <td>${escapar(item.nombre)}</td>
                <td>
                    <input class="input-cantidad" type="number" min="1" max="50" step="1"
                        value="${item.ped_det_cant}" data-cantidad="${indice}" aria-label="Cantidad">
                </td>
                <td>${pesos(item.precio)}</td>
                <td>${pesos(item.precio * item.ped_det_cant)}</td>
                <td>${escapar(item.ped_det_obser ?? "")}</td>
                <td>
                    <button class="action-button action-button-light" type="button" data-quitar="${indice}">Quitar</button>
                </td>
            </tr>
        `).join("");

        form.total.textContent = pesos(itemsNuevos.reduce((s, i) => s + i.precio * i.ped_det_cant, 0));
    }

    function abrirFormulario(tipo, pedido = null) {
        modo = { tipo, pedId: pedido?.ped_id ?? null };
        itemsNuevos = [];

        form.form.reset();
        form.cantidad.value = 1;
        ocultarMensaje(form.mensaje);
        pintarItemsNuevos();

        const esNuevo = tipo === "nuevo";
        form.campoMesa.classList.toggle("hidden", !esNuevo);
        form.titulo.textContent = esNuevo ? "Nuevo pedido" : `Agregar platos al pedido #${pedido.ped_id}`;
        form.subtitulo.textContent = esNuevo
            ? "Indica la mesa y agrega los platos. Al enviarlo, los platos llegan a cocina."
            : `Mesa ${pedido.ped_mesa}. Los platos nuevos se envían a cocina al guardar.`;

        detalle.seccion.classList.add("hidden");
        form.seccion.classList.remove("hidden");
        form.seccion.scrollIntoView({ behavior: "smooth", block: "start" });
        (esNuevo ? form.mesa : form.plato).focus({ preventScroll: true });
    }

    function cerrarFormulario() {
        form.seccion.classList.add("hidden");
        itemsNuevos = [];
    }

    function agregarPlato() {
        const plato = platos.find(p => String(p.idPlato) === form.plato.value);
        const cantidad = Number(form.cantidad.value);
        const observacion = form.observacion.value.trim();

        if (!plato) {
            mostrarMensaje(form.mensaje, "Selecciona un plato.", "error");
            return;
        }

        if (!Number.isInteger(cantidad) || cantidad < 1 || cantidad > 50) {
            mostrarMensaje(form.mensaje, "La cantidad debe estar entre 1 y 50.", "error");
            return;
        }

        const existente = itemsNuevos.find(i => i.pla_id === plato.idPlato && (i.ped_det_obser ?? "") === observacion);

        if (existente) {
            existente.ped_det_cant = Math.min(50, existente.ped_det_cant + cantidad);
        } else {
            itemsNuevos.push({
                pla_id: plato.idPlato,
                nombre: plato.descripcion,
                precio: plato.precio,
                ped_det_cant: cantidad,
                ped_det_obser: observacion || null
            });
        }

        ocultarMensaje(form.mensaje);
        form.plato.value = "";
        form.cantidad.value = 1;
        form.observacion.value = "";
        form.plato.focus();
        pintarItemsNuevos();
    }

    /* ---------- detalle ---------- */

    async function abrirDetalle(pedId, mensaje = null) {
        try {
            detalleActual = await pedir(`${API}/${pedId}/completo`);
        } catch (error) {
            avisarListado(error.message, "error");
            return;
        }

        const p = detalleActual.pedido;
        const estaAbierto = abierto(p);

        detalle.titulo.innerHTML = `Pedido #${escapar(p.ped_id)} · Mesa ${escapar(p.ped_mesa)} ${badgePedido(p)}`;
        detalle.subtitulo.textContent = `${fechaHora(p.ped_fecha)} · Atendido por ${p.mesero ?? "—"}`;

        detalle.agregar.classList.toggle("hidden", !estaAbierto);
        detalle.cancelar.classList.toggle("hidden", !estaAbierto);
        detalle.finalizar.classList.toggle("hidden", !estaAbierto);

        detalle.tabla.innerHTML = detalleActual.items.length
            ? detalleActual.items.map(i => `
                <tr class="${i.est_id === flujo.detalleCancelado ? "row-muted" : ""}">
                    <td>${escapar(i.plato)}</td>
                    <td>${i.ped_det_cant}</td>
                    <td>${pesos(i.ped_det_precio)}</td>
                    <td>${pesos(i.subtotal)}</td>
                    <td>${escapar(i.ped_det_obser ?? "")}</td>
                    <td>${badgeItem(i)}</td>
                    <td>
                        ${estaAbierto && i.est_id === flujo.detallePreparacion
                            ? `<button class="action-button action-button-light" type="button" data-cancelar-item="${i.ped_det_id}">Cancelar plato</button>`
                            : ""}
                    </td>
                </tr>
            `).join("")
            : `<tr><td colspan="7">Este pedido no tiene platos.</td></tr>`;

        detalle.total.textContent = pesos(p.total);

        if (mensaje) {
            mostrarMensaje(detalle.mensaje, mensaje);
        } else if (estaAbierto && p.pendientes) {
            mostrarMensaje(detalle.mensaje,
                `${p.pendientes} plato(s) siguen en cocina. Podrás finalizar el pedido cuando estén listos.`, "error");
        } else {
            ocultarMensaje(detalle.mensaje);
        }

        cerrarFormulario();
        detalle.seccion.classList.remove("hidden");
        detalle.seccion.scrollIntoView({ behavior: "smooth", block: "start" });
    }

    async function accionPedido(accion) {
        const p = detalleActual?.pedido;
        if (!p) return;

        if (accion === "cancelar" && !confirm(
            `¿Cancelar el pedido #${p.ped_id} de la mesa ${p.ped_mesa}? Los platos que siguen en cocina también se cancelan.`)) {
            return;
        }

        try {
            const resultado = await pedir(`${API}/${p.ped_id}/estado`, {
                method: "PUT",
                body: JSON.stringify({ accion })
            });

            await cargarPedidos();
            await abrirDetalle(p.ped_id, resultado);

        } catch (error) {
            mostrarMensaje(detalle.mensaje, error.message, "error");
        }
    }

    /* ---------- eventos ---------- */

    $("btnNuevoPedido").addEventListener("click", () => abrirFormulario("nuevo"));
    $("btnActualizarPedidos").addEventListener("click", cargarTodo);
    $("btnCerrarPedido").addEventListener("click", cerrarFormulario);
    $("btnCancelarPedidoForm").addEventListener("click", cerrarFormulario);
    $("btnAgregarPlato").addEventListener("click", agregarPlato);

    $("btnDetalleCerrar").addEventListener("click", () => detalle.seccion.classList.add("hidden"));
    detalle.agregar.addEventListener("click", () => abrirFormulario("agregar", detalleActual.pedido));
    detalle.finalizar.addEventListener("click", () => accionPedido("finalizar"));
    detalle.cancelar.addEventListener("click", () => accionPedido("cancelar"));

    // Enter en la observación agrega el plato en lugar de enviar el pedido.
    [form.observacion, form.cantidad].forEach(campo => campo.addEventListener("keydown", evento => {
        if (evento.key === "Enter") {
            evento.preventDefault();
            agregarPlato();
        }
    }));

    form.items.addEventListener("click", evento => {
        const quitar = evento.target.closest("[data-quitar]");
        if (!quitar) return;
        itemsNuevos.splice(Number(quitar.dataset.quitar), 1);
        pintarItemsNuevos();
    });

    form.items.addEventListener("change", evento => {
        const campo = evento.target.closest("[data-cantidad]");
        if (!campo) return;
        const cantidad = Math.max(1, Math.min(50, Math.round(Number(campo.value) || 1)));
        itemsNuevos[Number(campo.dataset.cantidad)].ped_det_cant = cantidad;
        pintarItemsNuevos();
    });

    form.form.addEventListener("submit", async evento => {
        evento.preventDefault();

        const mesa = form.mesa.value.trim();

        if (modo.tipo === "nuevo" && !/^[A-Za-z0-9]{1,4}$/.test(mesa)) {
            mostrarMensaje(form.mensaje, "Indica la mesa (máximo 4 caracteres, letras o números).", "error");
            form.mesa.focus();
            return;
        }

        if (!itemsNuevos.length) {
            mostrarMensaje(form.mensaje, "Agrega al menos un plato.", "error");
            return;
        }

        const items = itemsNuevos.map(({ pla_id, ped_det_cant, ped_det_obser }) => ({ pla_id, ped_det_cant, ped_det_obser }));

        form.enviar.disabled = true;

        try {
            let pedId = modo.pedId;
            let mensaje;

            if (modo.tipo === "nuevo") {
                const resultado = await pedir(`${API}/completo`, {
                    method: "POST",
                    body: JSON.stringify({ ped_mesa: mesa, items })
                });
                pedId = resultado.ped_id;
                mensaje = resultado.mensaje;
            } else {
                mensaje = await pedir(`${API}/${pedId}/items`, {
                    method: "POST",
                    body: JSON.stringify({ items })
                });
            }

            cerrarFormulario();
            await cargarPedidos();
            await abrirDetalle(pedId, mensaje);

        } catch (error) {
            mostrarMensaje(form.mensaje, error.message, "error");
        } finally {
            form.enviar.disabled = false;
        }
    });

    filtros.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-filtro]");
        if (!boton) return;
        filtro = boton.dataset.filtro;
        filtros.querySelectorAll("button").forEach(b => b.classList.toggle("active", b === boton));
        pintarPedidos();
    });

    buscar.addEventListener("input", pintarPedidos);

    tabla.addEventListener("click", evento => {
        const ver = evento.target.closest("[data-ver]");
        if (ver) abrirDetalle(Number(ver.dataset.ver));
    });

    detalle.tabla.addEventListener("click", async evento => {
        const boton = evento.target.closest("[data-cancelar-item]");
        if (!boton || !confirm("¿Cancelar este plato? Se avisará a cocina y no se cobrará.")) return;

        try {
            const resultado = await pedir(`/api/pedidos-detalle/${boton.dataset.cancelarItem}/estado`, {
                method: "PUT",
                body: JSON.stringify({ accion: "cancelar" })
            });

            await cargarPedidos();
            await abrirDetalle(detalleActual.pedido.ped_id, resultado);

        } catch (error) {
            mostrarMensaje(detalle.mensaje, error.message, "error");
        }
    });

    cargarTodo();
};
