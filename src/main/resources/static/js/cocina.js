
/*
 * Pantalla de Cocina: muestra los platos en preparación agrupados por pedido (mesa),
 * del más antiguo al más reciente. La cocina marca cada plato como "Listo".
 */
window.inicializarCocina = function () {
    const vista = document.getElementById("vistaCocina");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    const $ = (id) => document.getElementById(id);

    const tablero = $("cocinaTablero");
    const mensaje = $("cocinaMensaje");
    const auto = $("cocinaAuto");
    const INTERVALO_MS = 15000;

    let temporizador = null;
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const minutosDesde = (iso) => Math.max(0, Math.floor((Date.now() - new Date(iso).getTime()) / 60000));

    const textoEspera = (minutos) => minutos < 60
        ? `${minutos} min`
        : `${Math.floor(minutos / 60)} h ${minutos % 60} min`;

    const avisar = (texto, tipo = "success") => {
        mensaje.textContent = texto;
        mensaje.classList.remove("hidden", "success", "error");
        mensaje.classList.add(tipo);
        clearTimeout(temporizadorMensaje);
        temporizadorMensaje = setTimeout(() => mensaje.classList.add("hidden"), 3500);
    };

    function pintar(comandas) {
        const pedidos = new Map();

        comandas.forEach(c => {
            if (!pedidos.has(c.ped_id)) pedidos.set(c.ped_id, { ...c, platos: [] });
            pedidos.get(c.ped_id).platos.push(c);
        });

        const minutos = comandas.map(c => minutosDesde(c.ped_fecha));

        $("cocinaPlatos").textContent = comandas.reduce((s, c) => s + c.ped_det_cant, 0);
        $("cocinaMesas").textContent = pedidos.size;
        $("cocinaEspera").textContent = minutos.length ? textoEspera(Math.max(...minutos)) : "—";
        $("cocinaActualizado").textContent =
            `Última actualización: ${new Date().toLocaleTimeString("es-CO", { hour: "2-digit", minute: "2-digit", second: "2-digit" })}`;

        if (!pedidos.size) {
            tablero.innerHTML = `<p class="empty-state">No hay platos pendientes. ¡Cocina al día!</p>`;
            return;
        }

        tablero.innerHTML = [...pedidos.values()].map(p => {
            const espera = minutosDesde(p.ped_fecha);
            const urgencia = espera >= 30 ? "urgent" : espera >= 15 ? "warning" : "";

            return `
                <article class="kitchen-card ${urgencia}">
                    <header>
                        <div>
                            <strong>Mesa ${escapar(p.ped_mesa)}</strong>
                            <span>Pedido #${escapar(p.ped_id)} · ${escapar(p.mesero ?? "—")}</span>
                        </div>
                        <span class="kitchen-time">${textoEspera(espera)}</span>
                    </header>

                    <ul>
                        ${p.platos.map(item => `
                            <li>
                                <div>
                                    <strong>${item.ped_det_cant} × ${escapar(item.plato)}</strong>
                                    ${item.ped_det_obser ? `<em>${escapar(item.ped_det_obser)}</em>` : ""}
                                </div>
                                <button class="button button-primary" type="button" data-listo="${item.ped_det_id}">
                                    Listo
                                </button>
                            </li>
                        `).join("")}
                    </ul>
                </article>
            `;
        }).join("");
    }

    async function cargar() {
        // Si el usuario cambió de pantalla, se detiene la actualización automática.
        if (!document.body.contains(vista)) {
            clearInterval(temporizador);
            return;
        }

        try {
            const respuesta = await fetch("/api/cocina/comandas");

            if (!respuesta.ok) {
                throw new Error(await respuesta.text() || "No se pudieron cargar las comandas.");
            }

            pintar(await respuesta.json());

        } catch (error) {
            console.error(error);
            avisar(error.message, "error");
        }
    }

    function programar() {
        clearInterval(temporizador);
        if (auto.checked) temporizador = setInterval(cargar, INTERVALO_MS);
    }

    $("btnActualizarCocina").addEventListener("click", cargar);
    auto.addEventListener("change", programar);

    tablero.addEventListener("click", async evento => {
        const boton = evento.target.closest("[data-listo]");
        if (!boton) return;

        boton.disabled = true;

        try {
            const respuesta = await fetch(`/api/pedidos-detalle/${boton.dataset.listo}/estado`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ accion: "listo" })
            });

            const texto = await respuesta.text();

            if (!respuesta.ok) {
                throw new Error(texto || "No se pudo marcar el plato.");
            }

            avisar(texto);
            await cargar();

        } catch (error) {
            boton.disabled = false;
            avisar(error.message, "error");
            await cargar();
        }
    });

    cargar();
    programar();
};
