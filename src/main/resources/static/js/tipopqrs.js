
const API_TIPOS_PQRS = "/api/tipos-pqrs";

window.inicializarTiposPqrs = function () {
    const vista = document.getElementById("vistaTiposPqrs");

    if (!vista) return;

    const formulario = document.getElementById("tipoPqrsForm");
    const seccionFormulario = document.getElementById("tipoPqrsFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioTipoPqrs");
    const tabla = document.getElementById("tablaTiposPqrs");
    const mensajeFormulario = document.getElementById("tipoPqrsMensaje");
    const mensajeListado = document.getElementById("tiposPqrsListadoMensaje");
    const buscar = document.getElementById("buscarTipoPqrs");
    const campoId = document.getElementById("tipoPqrsId");
    const campoDescripcion = document.getElementById("tipoPqrsDescripcion");
    const campoEstado = document.getElementById("tipoPqrsEstado");

    if (vista.dataset.inicializada === "true") {
        cargarTiposPqrs();
        return;
    }

    vista.dataset.inicializada = "true";

    let tiposPqrs = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (tipo) => tipo.tpqrs_estado?.toLowerCase() === "activo";

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

    const limpiarFormulario = () => {
        formulario.reset();
        campoId.value = "";
        campoEstado.value = "Activo";
        tituloFormulario.textContent = "Registrar tipo de PQRS";
        ocultarMensaje(mensajeFormulario);
    };

    const mostrarFormulario = () => {
        seccionFormulario.classList.remove("hidden");
        seccionFormulario.scrollIntoView({ behavior: "smooth", block: "start" });
        campoDescripcion.focus({ preventScroll: true });
    };

    const ocultarFormulario = () => {
        seccionFormulario.classList.add("hidden");
        limpiarFormulario();
    };

    const consultarTipo = async (id) => {
        const respuesta = await fetch(`${API_TIPOS_PQRS}/${id}`);

        if (!respuesta.ok) {
            throw new Error("No se pudo consultar el tipo de PQRS.");
        }

        return respuesta.json();
    };

    const guardar = async (id, datos) => {
        const respuesta = await fetch(id ? `${API_TIPOS_PQRS}/${id}` : API_TIPOS_PQRS, {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado || "No se pudo guardar el tipo de PQRS.");
        }

        return resultado;
    };

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = tiposPqrs.filter(tipo =>
            String(tipo.tpqrs_id).includes(texto) ||
            (tipo.tpqrs_descripcion ?? "").toLowerCase().includes(texto) ||
            (tipo.tpqrs_estado ?? "").toLowerCase().includes(texto)
        );

        if (!filtrados.length) {
            tabla.innerHTML = `<tr><td colspan="4">${tiposPqrs.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay tipos de PQRS registrados."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtrados.map(tipo => `
            <tr>
                <td>${escapar(tipo.tpqrs_id)}</td>
                <td>${escapar(tipo.tpqrs_descripcion)}</td>
                <td>
                    <span class="badge ${esActivo(tipo) ? "badge-success" : "badge-muted"}">
                        ${escapar(tipo.tpqrs_estado)}
                    </span>
                </td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button"
                            data-editar="${escapar(tipo.tpqrs_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button"
                            data-estado="${escapar(tipo.tpqrs_id)}">
                            ${esActivo(tipo) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarTiposPqrs() {
        try {
            const respuesta = await fetch(API_TIPOS_PQRS);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar los tipos de PQRS.");
            }

            tiposPqrs = await respuesta.json();
            tiposPqrs.sort((a, b) => a.tpqrs_id - b.tpqrs_id);

            document.getElementById("totalTiposPqrs").textContent = tiposPqrs.length;
            document.getElementById("tiposPqrsActivos").textContent =
                tiposPqrs.filter(esActivo).length;
            document.getElementById("tiposPqrsInactivos").textContent =
                tiposPqrs.filter(tipo => !esActivo(tipo)).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="4">Error al cargar los tipos de PQRS.</td></tr>`;
        }
    }

    document.getElementById("btnNuevoTipoPqrs").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario();
    });

    document.getElementById("btnCerrarTipoPqrs").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarTipoPqrs").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarTiposPqrs").addEventListener("click", cargarTiposPqrs);

    buscar.addEventListener("input", pintarTabla);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campoId.value;

        const datos = {
            tpqrs_descripcion: campoDescripcion.value.trim(),
            tpqrs_estado: campoEstado.value
        };

        if (!datos.tpqrs_descripcion) {
            mostrarMensaje(mensajeFormulario, "La descripción es obligatoria.", "error");
            return;
        }

        const repetido = tiposPqrs.some(tipo =>
            String(tipo.tpqrs_id) !== id &&
            (tipo.tpqrs_descripcion ?? "").trim().toLowerCase() === datos.tpqrs_descripcion.toLowerCase()
        );

        if (repetido) {
            mostrarMensaje(mensajeFormulario, "Ya existe un tipo de PQRS con esa descripción.", "error");
            return;
        }

        try {
            const resultado = await guardar(id, datos);

            ocultarFormulario();
            await cargarTiposPqrs();
            avisarListado(resultado);

        } catch (error) {
            mostrarMensaje(mensajeFormulario, error.message, "error");
        }
    });

    tabla.addEventListener("click", async (evento) => {
        const botonEditar = evento.target.closest("[data-editar]");
        const botonEstado = evento.target.closest("[data-estado]");

        if (botonEditar) {
            try {
                const tipo = await consultarTipo(botonEditar.dataset.editar);

                limpiarFormulario();
                campoId.value = tipo.tpqrs_id;
                campoDescripcion.value = tipo.tpqrs_descripcion ?? "";
                campoEstado.value = esActivo(tipo) ? "Activo" : "Inactivo";
                tituloFormulario.textContent = `Editar tipo de PQRS #${tipo.tpqrs_id}`;

                mostrarFormulario();

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }

        if (botonEstado) {
            try {
                const tipo = await consultarTipo(botonEstado.dataset.estado);
                const nuevoEstado = esActivo(tipo) ? "Inactivo" : "Activo";

                await guardar(String(tipo.tpqrs_id), {
                    tpqrs_descripcion: tipo.tpqrs_descripcion,
                    tpqrs_estado: nuevoEstado
                });

                await cargarTiposPqrs();
                avisarListado(`"${tipo.tpqrs_descripcion}" quedó ${nuevoEstado.toLowerCase()}.`);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }
    });

    limpiarFormulario();
    cargarTiposPqrs();
};
