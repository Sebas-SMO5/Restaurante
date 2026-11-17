
/*
 * Dashboard de inicio: indicadores del día, ventas de la semana, platos más vendidos
 * y materias primas por reponer. Los accesos rápidos abren otras pantallas del menú.
 */
window.inicializarInicio = function () {
    const vista = document.getElementById("vistaInicio");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    const $ = (id) => document.getElementById(id);
    const tooltip = $("chartTooltip");

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const pesos = (valor) => Number(valor ?? 0).toLocaleString("es-CO", {
        style: "currency", currency: "COP", maximumFractionDigits: 0
    });

    /** Valores compactos para etiquetas del gráfico: $ 850 mil, $ 1,2 M. */
    const pesosCortos = (valor) => {
        const v = Number(valor ?? 0);
        if (v >= 1_000_000) return `$ ${(v / 1_000_000).toLocaleString("es-CO", { maximumFractionDigits: 1 })} M`;
        if (v >= 1_000) return `$ ${Math.round(v / 1_000).toLocaleString("es-CO")} mil`;
        return pesos(v);
    };

    const nombreDia = (iso, largo = false) => {
        const [anio, mes, dia] = iso.split("-").map(Number);
        const fecha = new Date(anio, mes - 1, dia);
        return fecha.toLocaleDateString("es-CO", largo
            ? { weekday: "long", day: "numeric", month: "long" }
            : { weekday: "short", day: "numeric" });
    };

    /* ---------- saludo ---------- */

    function pintarSaludo() {
        const hora = new Date().getHours();
        const saludo = hora < 12 ? "Buenos días" : hora < 19 ? "Buenas tardes" : "Buenas noches";
        const nombre = window.usuarioSesion?.usu_nombre?.split(" ")[0] ?? "";

        $("inicioSaludo").textContent = `${saludo}${nombre ? `, ${nombre}` : ""}`;
    }

    pintarSaludo();

    // La sesión puede terminar de cargar después de abrir el inicio.
    if (!window.usuarioSesion) {
        document.addEventListener("sesion-lista", pintarSaludo, { once: true });
    }

    $("inicioFecha").textContent = new Date().toLocaleDateString("es-CO", {
        weekday: "long", day: "numeric", month: "long", year: "numeric"
    });

    /* ---------- accesos rápidos ---------- */

    function ir(vistaDestino) {
        const opcion = document.querySelector(`.submenu-item[data-vista="${vistaDestino}"]`);
        if (!opcion) return;

        const grupo = opcion.closest("details");
        if (grupo) grupo.open = true;

        opcion.click();
    }

    vista.addEventListener("click", evento => {
        const destino = evento.target.closest("[data-ir]");
        if (destino) ir(destino.dataset.ir);
    });

    vista.addEventListener("keydown", evento => {
        const destino = evento.target.closest(".stat-link[data-ir]");
        if (destino && (evento.key === "Enter" || evento.key === " ")) {
            evento.preventDefault();
            ir(destino.dataset.ir);
        }
    });

    /* ---------- tooltip compartido ---------- */

    function mostrarTooltip(marca, html) {
        tooltip.innerHTML = html;
        tooltip.classList.remove("hidden");

        const caja = marca.getBoundingClientRect();
        const base = vista.getBoundingClientRect();
        const ancho = tooltip.offsetWidth;

        let izquierda = caja.left - base.left + caja.width / 2 - ancho / 2;
        izquierda = Math.max(0, Math.min(izquierda, base.width - ancho));

        tooltip.style.left = `${izquierda}px`;
        tooltip.style.top = `${caja.top - base.top - tooltip.offsetHeight - 8}px`;
    }

    const ocultarTooltip = () => tooltip.classList.add("hidden");

    function conectarTooltip(contenedor) {
        const mostrar = (evento) => {
            const marca = evento.target.closest("[data-tip]");
            if (marca) mostrarTooltip(marca, marca.dataset.tip);
        };

        contenedor.addEventListener("mouseover", mostrar);
        contenedor.addEventListener("focusin", mostrar);
        contenedor.addEventListener("mouseleave", ocultarTooltip);
        contenedor.addEventListener("focusout", ocultarTooltip);
    }

    /* ---------- gráfico: ventas de 7 días (columnas) ---------- */

    function pintarVentas(dias) {
        const contenedor = $("graficoVentas");
        const maximo = Math.max(...dias.map(d => d.total), 0);
        const total = dias.reduce((s, d) => s + d.total, 0);
        const indiceMaximo = dias.findIndex(d => d.total === maximo && maximo > 0);

        $("ventasSemanaTotal").textContent = `Total de la semana: ${pesos(total)}`;

        if (maximo === 0) {
            contenedor.innerHTML = `<p class="empty-state">Aún no hay ventas en los últimos 7 días. Aparecerán aquí al cobrar pedidos.</p>`;
        } else {
            contenedor.innerHTML = dias.map((d, i) => {
                const alto = Math.max(d.total / maximo * 100, d.total > 0 ? 2 : 0);
                const esHoy = i === dias.length - 1;
                // Etiqueta solo en el día más alto y en hoy (etiquetado selectivo).
                const etiqueta = (i === indiceMaximo || esHoy) && d.total > 0
                    ? `<span class="chart-value">${pesosCortos(d.total)}</span>`
                    : "";

                return `
                    <div class="chart-col">
                        <div class="chart-col-track">
                            ${etiqueta}
                            <div class="chart-col-bar" style="height:${alto}%"
                                tabindex="0"
                                aria-label="${escapar(nombreDia(d.fecha, true))}: ${escapar(pesos(d.total))}"
                                data-tip="<strong>${escapar(nombreDia(d.fecha, true))}</strong><br>${escapar(pesos(d.total))} · ${d.recibos} recibo(s)"></div>
                        </div>
                        <span class="chart-col-label ${esHoy ? "today" : ""}">${esHoy ? "Hoy" : escapar(nombreDia(d.fecha))}</span>
                    </div>
                `;
            }).join("");
        }

        $("tablaVentasSemana").querySelector("tbody").innerHTML = dias.map(d =>
            `<tr><td>${escapar(nombreDia(d.fecha, true))}</td><td>${pesos(d.total)}</td><td>${d.recibos}</td></tr>`).join("");
    }

    /* ---------- gráfico: platos más vendidos (barras horizontales) ---------- */

    function pintarPlatos(platos) {
        const contenedor = $("graficoPlatos");

        if (!platos.length) {
            contenedor.innerHTML = `<p class="empty-state">Sin ventas en los últimos 30 días.</p>`;
            return;
        }

        const maximo = Math.max(...platos.map(p => p.cantidad));

        contenedor.innerHTML = platos.map(p => `
            <div class="chart-bar-row">
                <span class="chart-bar-name">${escapar(p.plato)}</span>
                <div class="chart-bar-track">
                    <div class="chart-bar" style="width:${Math.max(p.cantidad / maximo * 100, 2)}%"
                        tabindex="0"
                        aria-label="${escapar(p.plato)}: ${Number(p.cantidad)} unidades, ${escapar(pesos(p.total))}"
                        data-tip="<strong>${escapar(p.plato)}</strong><br>${Number(p.cantidad)} unidades · ${escapar(pesos(p.total))}"></div>
                    <span class="chart-bar-value">${Number(p.cantidad).toLocaleString("es-CO")}</span>
                </div>
            </div>
        `).join("");
    }

    /* ---------- stock bajo ---------- */

    function pintarStock(lista) {
        const contenedor = $("listaStockBajo");

        contenedor.innerHTML = lista.length
            ? lista.map(i => `
                <div class="stock-item">
                    <span class="badge ${i.stock <= 0 ? "badge-danger" : "badge-warning"}">${i.stock <= 0 ? "Agotado" : "Bajo"}</span>
                    <strong>${escapar(i.nombre)}</strong>
                    <span>${Number(i.stock).toLocaleString("es-CO")} ${escapar(i.unidad)} de ${Number(i.minimo).toLocaleString("es-CO")} mínimo</span>
                </div>
            `).join("")
            : `<p class="empty-state">Todas las materias primas están por encima del mínimo.</p>`;
    }

    /* ---------- carga ---------- */

    async function cargar() {
        try {
            const respuesta = await fetch("/api/dashboard");

            if (!respuesta.ok) {
                throw new Error(await respuesta.text() || "No se pudo cargar el resumen.");
            }

            const d = await respuesta.json();

            $("kpiVentas").textContent = pesos(d.ventasHoy);
            $("kpiVentasDetalle").textContent = `${d.recibosHoy} recibo(s) · egresos ${pesos(d.egresosHoy)}`;
            $("kpiPedidos").textContent = d.pedidosAbiertos;
            $("kpiPedidosDetalle").textContent = `${d.platosEnCocina} plato(s) en cocina`;
            $("kpiPorCobrar").textContent = pesos(d.porCobrarValor);
            $("kpiPorCobrarDetalle").textContent = `${d.porCobrarCantidad} pedido(s) finalizado(s)`;
            $("kpiPqrs").textContent = d.pqrsPendientes;
            $("kpiPqrsDetalle").textContent = `${d.pqrsVencidas} fuera de plazo (15 días hábiles)`;

            pintarVentas(d.ventas7Dias);
            pintarPlatos(d.topPlatos30Dias);
            pintarStock(d.stockBajo);

        } catch (error) {
            console.error(error);
            $("graficoVentas").innerHTML = `<div class="message error">${escapar(error.message)}</div>`;
        }
    }

    conectarTooltip($("graficoVentas"));
    conectarTooltip($("graficoPlatos"));

    cargar();
};
