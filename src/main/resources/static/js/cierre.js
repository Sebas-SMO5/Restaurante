
/*
 * Cierre de caja: resume ingresos y egresos de un día por forma de pago,
 * calcula el efectivo esperado y registra el efectivo contado (una vez por día).
 */
window.inicializarCierre = function () {
    const vista = document.getElementById("vistaCierre");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    const $ = (id) => document.getElementById(id);

    const fecha = $("cierreFecha");
    const aviso = $("cierreAviso");
    const form = $("cierreForm");
    const contado = $("cierreContado");
    const diferencia = $("cierreDiferencia");
    const observacion = $("cierreObservacion");
    const mensaje = $("cierreMensaje");
    const boton = $("btnRegistrarCierre");
    const guardado = $("cierreGuardado");

    let resumen = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const pesos = (valor) => Number(valor ?? 0).toLocaleString("es-CO", {
        style: "currency", currency: "COP", maximumFractionDigits: 0
    });

    const hoyISO = () => {
        const d = new Date();
        return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")}`;
    };

    const fechaLarga = (iso) => {
        const [a, m, d] = iso.split("-").map(Number);
        return new Date(a, m - 1, d).toLocaleDateString("es-CO", { weekday: "long", day: "numeric", month: "long", year: "numeric" });
    };

    const hora = (iso) => new Date(iso).toLocaleTimeString("es-CO", { hour: "2-digit", minute: "2-digit" });

    const mostrar = (elemento, texto, tipo = "success") => {
        elemento.textContent = texto;
        elemento.classList.remove("hidden", "success", "error");
        elemento.classList.add(tipo);
    };

    const ocultar = (elemento) => elemento.classList.add("hidden");

    /* ---------- pintar ---------- */

    function pintar() {
        const r = resumen;

        $("cierreTituloImpresion").textContent = `Cierre de caja · ${fechaLarga(r.fecha)}`;
        $("cierreIngresos").textContent = pesos(r.totalIngresos);
        $("cierreEgresos").textContent = pesos(r.totalEgresos);
        $("cierreNeto").textContent = pesos(r.neto);
        $("cierreEsperado").textContent = pesos(r.efectivoEsperado);

        // Unifica ingresos y egresos por forma de pago.
        const formas = new Map();
        const agregar = (lista, campo) => lista.forEach(f => {
            if (!formas.has(f.fp_id)) formas.set(f.fp_id, { nombre: f.forma_pago, efectivo: f.efectivo, ingresos: 0, egresos: 0 });
            formas.get(f.fp_id)[campo] += f.total;
        });
        agregar(r.ingresos, "ingresos");
        agregar(r.egresos, "egresos");

        $("cierrePorForma").innerHTML = formas.size
            ? [...formas.values()].map(f => `
                <tr>
                    <td>${escapar(f.nombre)} ${f.efectivo ? `<span class="badge badge-neutral badge-small">En caja</span>` : ""}</td>
                    <td>${pesos(f.ingresos)}</td>
                    <td>${pesos(f.egresos)}</td>
                    <td><strong>${pesos(f.ingresos - f.egresos)}</strong></td>
                </tr>`).join("")
            : `<tr><td colspan="4">Sin movimientos este día.</td></tr>`;

        $("cierreMovimientosResumen").textContent =
            `${r.movimientos.filter(m => m.tipo === "Ingreso").length} ingreso(s) y ${r.movimientos.filter(m => m.tipo === "Egreso").length} egreso(s).`;

        $("cierreMovimientos").innerHTML = r.movimientos.length
            ? r.movimientos.map(m => `
                <tr>
                    <td>${hora(m.fecha)}</td>
                    <td><span class="badge ${m.tipo === "Ingreso" ? "badge-success" : "badge-muted"}">${m.tipo}</span></td>
                    <td>${escapar(m.numero)}</td>
                    <td>${escapar(m.detalle)}</td>
                    <td>${escapar(m.forma_pago)}</td>
                    <td>${m.tipo === "Egreso" ? "− " : ""}${pesos(m.valor)}</td>
                </tr>`).join("")
            : `<tr><td colspan="6">No hubo recibos ni egresos este día.</td></tr>`;

        // Avisos
        if (r.pedidosSinCobrar > 0 && !r.cierre) {
            mostrar(aviso, `Hay ${r.pedidosSinCobrar} pedido(s) finalizado(s) sin cobrar. Cóbralos antes de cerrar la caja.`, "error");
        } else {
            ocultar(aviso);
        }

        // Cierre registrado o formulario
        const cerrado = Boolean(r.cierre);
        form.classList.toggle("hidden", cerrado);
        guardado.classList.toggle("hidden", !cerrado);

        $("cierreRegistroTitulo").innerHTML = cerrado
            ? `Caja cerrada <span class="badge badge-success">Registrado</span>`
            : "Cerrar la caja";
        $("cierreRegistroSubtitulo").textContent = cerrado
            ? "Este día ya tiene cierre; no se puede modificar."
            : "Cuenta el efectivo y regístralo.";

        if (cerrado) {
            const c = r.cierre;
            const tipo = c.cc_diferencia > 0 ? "Sobrante" : c.cc_diferencia < 0 ? "Faltante" : "Sin diferencia";

            guardado.innerHTML = `
                <div><dt>Efectivo esperado</dt><dd>${pesos(c.cc_efectivo_esperado)}</dd></div>
                <div><dt>Efectivo contado</dt><dd>${pesos(c.cc_efectivo_contado)}</dd></div>
                <div><dt>Diferencia</dt><dd>${tipo}${c.cc_diferencia ? `: ${pesos(Math.abs(c.cc_diferencia))}` : ""}</dd></div>
                ${c.cc_observacion ? `<div><dt>Observación</dt><dd>${escapar(c.cc_observacion)}</dd></div>` : ""}
                <div><dt>Registrado por</dt><dd>${escapar(c.registrado_por ?? "—")} · ${new Date(c.cc_fecha_registro).toLocaleString("es-CO")}</dd></div>
            `;
        } else {
            form.reset();
            ocultar(diferencia);
            ocultar(mensaje);
        }
    }

    function actualizarDiferencia() {
        if (!contado.value) {
            ocultar(diferencia);
            return;
        }

        const valor = Number(contado.value) - resumen.efectivoEsperado;
        diferencia.classList.remove("hidden");
        diferencia.classList.toggle("falta", valor < 0);
        diferencia.textContent = valor === 0
            ? "La caja cuadra exactamente."
            : valor > 0 ? `Sobrante: ${pesos(valor)}` : `Faltante: ${pesos(-valor)}`;
    }

    /* ---------- carga ---------- */

    async function cargar() {
        try {
            const respuesta = await fetch(`/api/caja/cierre?fecha=${encodeURIComponent(fecha.value)}`);
            const texto = await respuesta.text();

            if (!respuesta.ok) {
                throw new Error(texto || "No se pudo cargar el cierre de caja.");
            }

            resumen = JSON.parse(texto);
            pintar();

        } catch (error) {
            mostrar(aviso, error.message, "error");
        }
    }

    /* ---------- eventos ---------- */

    fecha.max = hoyISO();
    fecha.value = hoyISO();

    fecha.addEventListener("change", () => {
        if (!fecha.value || fecha.value > hoyISO()) fecha.value = hoyISO();
        cargar();
    });

    $("btnHoyCierre").addEventListener("click", () => {
        fecha.value = hoyISO();
        cargar();
    });

    $("btnImprimirCierre").addEventListener("click", () => window.print());
    contado.addEventListener("input", actualizarDiferencia);

    form.addEventListener("submit", async evento => {
        evento.preventDefault();

        if (contado.value === "" || Number(contado.value) < 0) {
            mostrar(mensaje, "Escribe el efectivo contado (puede ser 0).", "error");
            return;
        }

        const dif = Number(contado.value) - resumen.efectivoEsperado;

        if (Math.abs(dif) >= 1 && !observacion.value.trim()) {
            mostrar(mensaje, "Hay una diferencia: escribe una observación que la explique.", "error");
            observacion.focus();
            return;
        }

        if (!confirm(`¿Registrar el cierre de caja del ${fechaLarga(fecha.value)}? Después no se podrá modificar.`)) {
            return;
        }

        boton.disabled = true;

        try {
            const respuesta = await fetch("/api/caja/cierre", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    fecha: fecha.value,
                    efectivo_contado: Number(contado.value),
                    observacion: observacion.value.trim() || null
                })
            });

            const texto = await respuesta.text();

            if (!respuesta.ok) {
                throw new Error(texto || "No se pudo registrar el cierre.");
            }

            const resultado = JSON.parse(texto);
            resumen = resultado.cierre;
            pintar();
            mostrar(aviso, resultado.mensaje);

        } catch (error) {
            mostrar(mensaje, error.message, "error");
        } finally {
            boton.disabled = false;
        }
    });

    cargar();
};
