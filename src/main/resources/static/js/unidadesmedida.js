
const API_UNIDADES_MEDIDA = "/api/unidades-medida";

window.inicializarUnidadesMedida = function () {
    const vista = document.getElementById("vistaUnidadesMedida");

    if (!vista) return;

    const formulario = document.getElementById("unidadMedidaForm");
    const seccionFormulario = document.getElementById("unidadMedidaFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioUnidadMedida");
    const tabla = document.getElementById("tablaUnidadesMedida");
    const mensajeFormulario = document.getElementById("unidadMedidaMensaje");
    const mensajeListado = document.getElementById("unidadesMedidaListadoMensaje");
    const buscar = document.getElementById("buscarUnidadMedida");
    const campoId = document.getElementById("unidadMedidaId");
    const campoDescripcion = document.getElementById("unidadMedidaDescripcion");
    const campoAbreviatura = document.getElementById("unidadMedidaAbreviatura");
    const campoEstado = document.getElementById("unidadMedidaEstado");

    if (vista.dataset.inicializada === "true") {
        cargarUnidadesMedida();
        return;
    }

    vista.dataset.inicializada = "true";

    let unidadesMedida = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (um) => um.um_estado?.toLowerCase() === "activo";

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
        campoId.readOnly = false;
        campoEstado.value = "Activo";
        formulario.dataset.edicion = "false";
        tituloFormulario.textContent = "Registrar unidad de medida";
        ocultarMensaje(mensajeFormulario);
    };

    const mostrarFormulario = (campoFoco) => {
        seccionFormulario.classList.remove("hidden");
        seccionFormulario.scrollIntoView({ behavior: "smooth", block: "start" });
        campoFoco.focus({ preventScroll: true });
    };

    const ocultarFormulario = () => {
        seccionFormulario.classList.add("hidden");
        limpiarFormulario();
    };

    const consultarUnidad = async (id) => {
        const respuesta = await fetch(`${API_UNIDADES_MEDIDA}/${id}`);

        if (!respuesta.ok) {
            throw new Error("No se pudo consultar la unidad de medida.");
        }

        return respuesta.json();
    };

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtradas = unidadesMedida.filter(um =>
            String(um.um_id).includes(texto) ||
            (um.um_descripcion ?? "").toLowerCase().includes(texto) ||
            (um.um_abreviatura ?? "").toLowerCase().includes(texto) ||
            (um.um_estado ?? "").toLowerCase().includes(texto)
        );

        if (!filtradas.length) {
            tabla.innerHTML = `<tr><td colspan="5">${unidadesMedida.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay unidades de medida registradas."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtradas.map(um => `
            <tr>
                <td>${escapar(um.um_id)}</td>
                <td>${escapar(um.um_descripcion)}</td>
                <td><span class="badge badge-neutral">${escapar(um.um_abreviatura)}</span></td>
                <td>
                    <span class="badge ${esActivo(um) ? "badge-success" : "badge-muted"}">
                        ${escapar(um.um_estado)}
                    </span>
                </td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button"
                            data-editar="${escapar(um.um_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button"
                            data-estado="${escapar(um.um_id)}">
                            ${esActivo(um) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarUnidadesMedida() {
        try {
            const respuesta = await fetch(API_UNIDADES_MEDIDA);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar las unidades de medida.");
            }

            unidadesMedida = await respuesta.json();
            unidadesMedida.sort((a, b) => a.um_id - b.um_id);

            document.getElementById("totalUnidadesMedida").textContent = unidadesMedida.length;
            document.getElementById("unidadesMedidaActivas").textContent =
                unidadesMedida.filter(esActivo).length;
            document.getElementById("unidadesMedidaInactivas").textContent =
                unidadesMedida.filter(um => !esActivo(um)).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="5">Error al cargar las unidades de medida.</td></tr>`;
        }
    }

    document.getElementById("btnNuevaUnidadMedida").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario(campoId);
    });

    document.getElementById("btnCerrarUnidadMedida").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarUnidadMedida").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarUnidadesMedida").addEventListener("click", cargarUnidadesMedida);

    buscar.addEventListener("input", pintarTabla);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campoId.value.trim();
        const editando = formulario.dataset.edicion === "true";

        const datos = {
            um_id: Number(id),
            um_descripcion: campoDescripcion.value.trim(),
            um_abreviatura: campoAbreviatura.value.trim(),
            um_estado: campoEstado.value
        };

        if (!id || !Number.isInteger(datos.um_id) || datos.um_id <= 0) {
            mostrarMensaje(mensajeFormulario, "Ingresa un código válido.", "error");
            return;
        }

        if (!datos.um_descripcion || !datos.um_abreviatura || !datos.um_estado) {
            mostrarMensaje(mensajeFormulario, "Completa todos los campos.", "error");
            return;
        }

        if (!editando && unidadesMedida.some(um => um.um_id === datos.um_id)) {
            mostrarMensaje(mensajeFormulario, "Ya existe una unidad de medida con ese código.", "error");
            return;
        }

        try {
            const respuesta = await fetch(
                editando ? `${API_UNIDADES_MEDIDA}/${id}` : API_UNIDADES_MEDIDA,
                {
                    method: editando ? "PUT" : "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(datos)
                }
            );

            const resultado = await respuesta.text();

            if (!respuesta.ok) {
                throw new Error(resultado);
            }

            ocultarFormulario();
            await cargarUnidadesMedida();
            avisarListado(resultado);

        } catch (error) {
            mostrarMensaje(mensajeFormulario,
                error.message || "No se pudo guardar la unidad de medida.", "error");
        }
    });

    tabla.addEventListener("click", async (evento) => {
        const botonEditar = evento.target.closest("[data-editar]");
        const botonEstado = evento.target.closest("[data-estado]");

        if (botonEditar) {
            try {
                const unidad = await consultarUnidad(botonEditar.dataset.editar);

                limpiarFormulario();
                campoId.value = unidad.um_id;
                campoId.readOnly = true;
                campoDescripcion.value = unidad.um_descripcion;
                campoAbreviatura.value = unidad.um_abreviatura;
                campoEstado.value = esActivo(unidad) ? "Activo" : "Inactivo";

                formulario.dataset.edicion = "true";
                tituloFormulario.textContent = "Editar unidad de medida";

                mostrarFormulario(campoDescripcion);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }

        if (botonEstado) {
            try {
                const unidad = await consultarUnidad(botonEstado.dataset.estado);
                const nuevoEstado = esActivo(unidad) ? "Inactivo" : "Activo";

                const respuesta = await fetch(`${API_UNIDADES_MEDIDA}/${unidad.um_id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        um_id: unidad.um_id,
                        um_descripcion: unidad.um_descripcion,
                        um_abreviatura: unidad.um_abreviatura,
                        um_estado: nuevoEstado
                    })
                });

                const resultado = await respuesta.text();

                if (!respuesta.ok) {
                    throw new Error(resultado);
                }

                await cargarUnidadesMedida();
                avisarListado(`"${unidad.um_descripcion}" quedó ${nuevoEstado.toLowerCase()}.`);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }
    });

    limpiarFormulario();
    cargarUnidadesMedida();
};
