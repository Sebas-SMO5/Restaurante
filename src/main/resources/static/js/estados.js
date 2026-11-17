
const API_ESTADOS = "/api/estados";
const API_TIPOS_ESTADO = "/api/tipos-estado";

window.inicializarEstados = function () {
    const vista = document.getElementById("vistaEstados");

    if (!vista) return;

    const $ = (id) => document.getElementById(id);

    const tablaTipos = $("tablaTiposEstado");
    const tablaEstados = $("tablaEstados");
    const buscar = $("buscarEstado");
    const filtroTipo = $("filtroTipoEstado");
    const mensajeListado = $("estadosListadoMensaje");

    const formTipo = {
        seccion: $("tipoEstadoFormSection"),
        form: $("tipoEstadoForm"),
        titulo: $("tituloFormularioTipoEstado"),
        mensaje: $("tipoEstadoMensaje"),
        id: $("tipoEstadoId"),
        descripcion: $("tipoEstadoDescripcion"),
        estado: $("tipoEstadoEstado")
    };

    const formEstado = {
        seccion: $("estadoFormSection"),
        form: $("estadoForm"),
        titulo: $("tituloFormularioEstado"),
        mensaje: $("estadoMensaje"),
        id: $("estadoId"),
        descripcion: $("estadoDescripcion"),
        tipo: $("estadoTipo"),
        estado: $("estadoEstado")
    };

    if (vista.dataset.inicializada === "true") {
        cargarTodo();
        return;
    }

    vista.dataset.inicializada = "true";

    let tipos = [];
    let estados = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const activo = (valor) => String(valor ?? "").toLowerCase() === "activo";

    const badgeEstado = (valor) =>
        `<span class="badge ${activo(valor) ? "badge-success" : "badge-muted"}">${escapar(valor)}</span>`;

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
        temporizadorMensaje = setTimeout(() => ocultarMensaje(mensajeListado), 4000);
    };

    async function enviar(url, metodo, datos) {
        const respuesta = await fetch(url, {
            method: metodo,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado || "No se pudo guardar.");
        }

        return resultado;
    }

    function abrir(formulario, foco) {
        formTipo.seccion.classList.add("hidden");
        formEstado.seccion.classList.add("hidden");
        formulario.seccion.classList.remove("hidden");
        formulario.seccion.scrollIntoView({ behavior: "smooth", block: "start" });
        foco.focus({ preventScroll: true });
    }

    function cerrar(formulario) {
        formulario.seccion.classList.add("hidden");
        formulario.form.reset();
        formulario.id.value = "";
        ocultarMensaje(formulario.mensaje);
    }

    /* ---------- Tipos de estado ---------- */

    function pintarTipos() {
        if (!tipos.length) {
            tablaTipos.innerHTML = `<tr><td colspan="4">No hay tipos de estado registrados.</td></tr>`;
            return;
        }

        const seleccionado = filtroTipo.value;

        tablaTipos.innerHTML = tipos.map(t => {
            const cantidad = estados.filter(e => e.tes_id === t.tes_id).length;

            return `
                <tr class="${String(t.tes_id) === seleccionado ? "row-selected" : ""}">
                    <td>
                        <button class="link-button" type="button" data-filtrar="${escapar(t.tes_id)}">
                            ${escapar(t.tes_descripcion)}
                        </button>
                    </td>
                    <td>${cantidad}</td>
                    <td>${badgeEstado(t.tes_estado)}</td>
                    <td>
                        <div class="actions">
                            <button class="action-button" type="button" data-editar-tipo="${escapar(t.tes_id)}">
                                Editar
                            </button>
                            <button class="action-button action-button-light" type="button" data-estado-tipo="${escapar(t.tes_id)}">
                                ${activo(t.tes_estado) ? "Inactivar" : "Activar"}
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join("");
    }

    function llenarSelectTipos(actual = null) {
        const disponibles = tipos.filter(t => activo(t.tes_estado) || Number(t.tes_id) === Number(actual));

        formEstado.tipo.innerHTML = `<option value="">Seleccione un tipo</option>` +
            disponibles.map(t => `
                <option value="${escapar(t.tes_id)}">
                    ${escapar(t.tes_descripcion)}${activo(t.tes_estado) ? "" : " (inactivo)"}
                </option>
            `).join("");

        formEstado.tipo.value = actual ?? "";

        const filtro = filtroTipo.value;
        filtroTipo.innerHTML = `<option value="">Todos los tipos</option>` +
            tipos.map(t => `<option value="${escapar(t.tes_id)}">${escapar(t.tes_descripcion)}</option>`).join("");
        filtroTipo.value = filtro;
    }

    /* ---------- Estados ---------- */

    function pintarEstados() {
        const texto = buscar.value.trim().toLowerCase();
        const tipo = filtroTipo.value;

        const filtrados = estados.filter(e =>
            (!tipo || String(e.tes_id) === tipo) &&
            [e.est_id, e.est_descripcion, e.tes_descripcion, e.est_estado]
                .some(v => String(v ?? "").toLowerCase().includes(texto))
        );

        if (!filtrados.length) {
            tablaEstados.innerHTML = `<tr><td colspan="5">${estados.length
                ? "No hay estados que coincidan."
                : "No hay estados registrados."}</td></tr>`;
            return;
        }

        tablaEstados.innerHTML = filtrados.map(e => `
            <tr>
                <td>${escapar(e.est_id)}</td>
                <td>${escapar(e.est_descripcion)}</td>
                <td><span class="badge badge-neutral">${escapar(e.tes_descripcion)}</span></td>
                <td>${badgeEstado(e.est_estado)}</td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button" data-editar-estado="${escapar(e.est_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button" data-estado-estado="${escapar(e.est_id)}">
                            ${activo(e.est_estado) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    /* ---------- Carga ---------- */

    async function cargarTodo() {
        try {
            const [respTipos, respEstados] = await Promise.all([fetch(API_TIPOS_ESTADO), fetch(API_ESTADOS)]);

            if (!respTipos.ok || !respEstados.ok) {
                throw new Error("No se pudieron cargar los estados.");
            }

            tipos = (await respTipos.json()).sort((a, b) => a.tes_id - b.tes_id);
            estados = await respEstados.json();

            $("totalTiposEstado").textContent = tipos.length;
            $("totalEstados").textContent = estados.length;
            $("estadosActivos").textContent = estados.filter(e => activo(e.est_estado)).length;

            llenarSelectTipos();
            pintarTipos();
            pintarEstados();

        } catch (error) {
            console.error(error);
            tablaTipos.innerHTML = `<tr><td colspan="4">Error al cargar los tipos de estado.</td></tr>`;
            tablaEstados.innerHTML = `<tr><td colspan="5">Error al cargar los estados.</td></tr>`;
        }
    }

    /* ---------- Eventos: tipos ---------- */

    $("btnNuevoTipoEstado").addEventListener("click", () => {
        cerrar(formTipo);
        formTipo.estado.value = "Activo";
        formTipo.titulo.textContent = "Registrar tipo de estado";
        abrir(formTipo, formTipo.descripcion);
    });

    $("btnCerrarTipoEstado").addEventListener("click", () => cerrar(formTipo));
    $("btnCancelarTipoEstado").addEventListener("click", () => cerrar(formTipo));

    formTipo.form.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = formTipo.id.value;
        const datos = {
            tes_descripcion: formTipo.descripcion.value.trim(),
            tes_estado: formTipo.estado.value
        };

        if (!datos.tes_descripcion) {
            mostrarMensaje(formTipo.mensaje, "La descripción es obligatoria.", "error");
            return;
        }

        try {
            const resultado = await enviar(id ? `${API_TIPOS_ESTADO}/${id}` : API_TIPOS_ESTADO, id ? "PUT" : "POST", datos);
            cerrar(formTipo);
            await cargarTodo();
            avisarListado(resultado);
        } catch (error) {
            mostrarMensaje(formTipo.mensaje, error.message, "error");
        }
    });

    tablaTipos.addEventListener("click", async (evento) => {
        const filtrar = evento.target.closest("[data-filtrar]");
        const editar = evento.target.closest("[data-editar-tipo]");
        const cambiar = evento.target.closest("[data-estado-tipo]");

        if (filtrar) {
            filtroTipo.value = filtroTipo.value === filtrar.dataset.filtrar ? "" : filtrar.dataset.filtrar;
            pintarTipos();
            pintarEstados();
            return;
        }

        const id = editar?.dataset.editarTipo ?? cambiar?.dataset.estadoTipo;
        const tipo = tipos.find(t => String(t.tes_id) === String(id));

        if (!tipo) return;

        if (editar) {
            cerrar(formTipo);
            formTipo.id.value = tipo.tes_id;
            formTipo.descripcion.value = tipo.tes_descripcion ?? "";
            formTipo.estado.value = activo(tipo.tes_estado) ? "Activo" : "Inactivo";
            formTipo.titulo.textContent = `Editar tipo de estado: ${tipo.tes_descripcion}`;
            abrir(formTipo, formTipo.descripcion);
            return;
        }

        try {
            const nuevo = activo(tipo.tes_estado) ? "Inactivo" : "Activo";
            await enviar(`${API_TIPOS_ESTADO}/${tipo.tes_id}`, "PUT", {
                tes_descripcion: tipo.tes_descripcion,
                tes_estado: nuevo
            });
            await cargarTodo();
            avisarListado(`El tipo "${tipo.tes_descripcion}" quedó ${nuevo.toLowerCase()}.`);
        } catch (error) {
            avisarListado(error.message, "error");
        }
    });

    /* ---------- Eventos: estados ---------- */

    $("btnNuevoEstado").addEventListener("click", () => {
        cerrar(formEstado);
        formEstado.estado.value = "Activo";
        llenarSelectTipos(filtroTipo.value || null);
        formEstado.titulo.textContent = "Registrar estado";
        abrir(formEstado, formEstado.descripcion);
    });

    $("btnCerrarEstado").addEventListener("click", () => cerrar(formEstado));
    $("btnCancelarEstado").addEventListener("click", () => cerrar(formEstado));
    $("btnActualizarEstados").addEventListener("click", cargarTodo);

    buscar.addEventListener("input", pintarEstados);
    filtroTipo.addEventListener("change", () => {
        pintarTipos();
        pintarEstados();
    });

    formEstado.form.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = formEstado.id.value;
        const datos = {
            est_descripcion: formEstado.descripcion.value.trim(),
            tes_id: Number(formEstado.tipo.value) || 0,
            est_estado: formEstado.estado.value
        };

        if (!datos.est_descripcion || !datos.tes_id) {
            mostrarMensaje(formEstado.mensaje, "Escribe la descripción y selecciona el tipo de estado.", "error");
            return;
        }

        const repetido = estados.some(e =>
            String(e.est_id) !== id &&
            e.tes_id === datos.tes_id &&
            String(e.est_descripcion ?? "").trim().toLowerCase() === datos.est_descripcion.toLowerCase()
        );

        if (repetido) {
            mostrarMensaje(formEstado.mensaje, "Ya existe un estado con esa descripción en el mismo tipo.", "error");
            return;
        }

        try {
            const resultado = await enviar(id ? `${API_ESTADOS}/${id}` : API_ESTADOS, id ? "PUT" : "POST", datos);
            cerrar(formEstado);
            await cargarTodo();
            avisarListado(resultado);
        } catch (error) {
            mostrarMensaje(formEstado.mensaje, error.message, "error");
        }
    });

    tablaEstados.addEventListener("click", async (evento) => {
        const editar = evento.target.closest("[data-editar-estado]");
        const cambiar = evento.target.closest("[data-estado-estado]");

        const id = editar?.dataset.editarEstado ?? cambiar?.dataset.estadoEstado;
        const estado = estados.find(e => String(e.est_id) === String(id));

        if (!estado) return;

        if (editar) {
            cerrar(formEstado);
            formEstado.id.value = estado.est_id;
            formEstado.descripcion.value = estado.est_descripcion ?? "";
            llenarSelectTipos(estado.tes_id);
            formEstado.estado.value = activo(estado.est_estado) ? "Activo" : "Inactivo";
            formEstado.titulo.textContent = `Editar estado #${estado.est_id}`;
            abrir(formEstado, formEstado.descripcion);
            return;
        }

        try {
            const nuevo = activo(estado.est_estado) ? "Inactivo" : "Activo";
            await enviar(`${API_ESTADOS}/${estado.est_id}`, "PUT", {
                est_descripcion: estado.est_descripcion,
                tes_id: estado.tes_id,
                est_estado: nuevo
            });
            await cargarTodo();
            avisarListado(`"${estado.est_descripcion}" (${estado.tes_descripcion}) quedó ${nuevo.toLowerCase()}.`);
        } catch (error) {
            avisarListado(error.message, "error");
        }
    });

    cargarTodo();
};
