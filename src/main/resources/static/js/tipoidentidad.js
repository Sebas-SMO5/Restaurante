
const API_TIPOS_IDENTIDAD = "/api/tipos-identidad";

window.inicializarTipoIdentidad = function () {
    const vista = document.getElementById("vistaTipoIdentidad");

    if (!vista) return;

    const formulario = document.getElementById("tipoIdentidadForm");
    const seccionFormulario = document.getElementById("tipoIdentidadFormSection");
    const tituloFormulario = document.getElementById("tipoIdentidadFormTitle");
    const tabla = document.getElementById("tablaTiposIdentidad");
    const mensajeFormulario = document.getElementById("tipoIdentidadMensaje");
    const mensajeListado = document.getElementById("mensajeTiposIdentidad");
    const buscar = document.getElementById("buscarTipoIdentidad");
    const campoId = document.getElementById("tipoIdentidadId");
    const campoDescripcion = document.getElementById("tipoIdentidadDescripcion");
    const campoSigla = document.getElementById("tipoIdentidadSigla");
    const campoEstado = document.getElementById("tipoIdentidadEstado");

    if (vista.dataset.inicializada === "true") {
        cargarTiposIdentidad();
        return;
    }

    vista.dataset.inicializada = "true";

    let tiposIdentidad = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (tipo) => tipo.tipo_estado?.toLowerCase() === "activo";

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
        tituloFormulario.textContent = "Registrar tipo de identidad";
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
        const respuesta = await fetch(`${API_TIPOS_IDENTIDAD}/${id}`);

        if (!respuesta.ok) {
            throw new Error("No se pudo consultar el tipo de identidad.");
        }

        return respuesta.json();
    };

    const guardar = async (id, datos) => {
        const respuesta = await fetch(id ? `${API_TIPOS_IDENTIDAD}/${id}` : API_TIPOS_IDENTIDAD, {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado || "No se pudo guardar el tipo de identidad.");
        }

        return resultado;
    };

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = tiposIdentidad.filter(tipo =>
            String(tipo.tipo_id).includes(texto) ||
            (tipo.tipo_descripcion ?? "").toLowerCase().includes(texto) ||
            (tipo.tipo_sigla ?? "").toLowerCase().includes(texto) ||
            (tipo.tipo_estado ?? "").toLowerCase().includes(texto)
        );

        if (!filtrados.length) {
            tabla.innerHTML = `<tr><td colspan="5">${tiposIdentidad.length
                ? "No hay coincidencias con la búsqueda."
                : "Aún no hay tipos de identidad registrados. Usa “+ Nuevo tipo” para crear el primero."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtrados.map(tipo => `
            <tr>
                <td>${escapar(tipo.tipo_id)}</td>
                <td>${escapar(tipo.tipo_descripcion)}</td>
                <td><span class="badge badge-neutral">${escapar(tipo.tipo_sigla)}</span></td>
                <td>
                    <span class="badge ${esActivo(tipo) ? "badge-success" : "badge-muted"}">
                        ${escapar(tipo.tipo_estado)}
                    </span>
                </td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button"
                            data-editar="${escapar(tipo.tipo_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button"
                            data-estado="${escapar(tipo.tipo_id)}">
                            ${esActivo(tipo) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarTiposIdentidad() {
        try {
            const respuesta = await fetch(API_TIPOS_IDENTIDAD);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar los tipos de identidad.");
            }

            tiposIdentidad = await respuesta.json();
            tiposIdentidad.sort((a, b) => a.tipo_id - b.tipo_id);

            document.getElementById("totalTiposIdentidad").textContent = tiposIdentidad.length;
            document.getElementById("tiposIdentidadActivos").textContent =
                tiposIdentidad.filter(esActivo).length;
            document.getElementById("tiposIdentidadInactivos").textContent =
                tiposIdentidad.filter(tipo => !esActivo(tipo)).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="5">Error al cargar los tipos de identidad.</td></tr>`;
        }
    }

    document.getElementById("btnNuevoTipoIdentidad").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario();
    });

    document.getElementById("btnCerrarTipoIdentidad").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarTipoIdentidad").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarTiposIdentidad").addEventListener("click", cargarTiposIdentidad);

    buscar.addEventListener("input", pintarTabla);

    campoSigla.addEventListener("input", () => {
        campoSigla.value = campoSigla.value.toUpperCase();
    });

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campoId.value;

        const datos = {
            tipo_descripcion: campoDescripcion.value.trim(),
            tipo_sigla: campoSigla.value.trim().toUpperCase(),
            tipo_estado: campoEstado.value
        };

        if (!datos.tipo_descripcion || !datos.tipo_sigla || !datos.tipo_estado) {
            mostrarMensaje(mensajeFormulario, "Completa todos los campos.", "error");
            return;
        }

        const siglaRepetida = tiposIdentidad.some(tipo =>
            String(tipo.tipo_id) !== id &&
            (tipo.tipo_sigla ?? "").trim().toUpperCase() === datos.tipo_sigla
        );

        if (siglaRepetida) {
            mostrarMensaje(mensajeFormulario, "Ya existe un tipo de identidad con esa sigla.", "error");
            return;
        }

        try {
            const resultado = await guardar(id, datos);

            ocultarFormulario();
            await cargarTiposIdentidad();
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
                campoId.value = tipo.tipo_id;
                campoDescripcion.value = tipo.tipo_descripcion ?? "";
                campoSigla.value = tipo.tipo_sigla ?? "";
                campoEstado.value = esActivo(tipo) ? "Activo" : "Inactivo";
                tituloFormulario.textContent = `Editar tipo de identidad #${tipo.tipo_id}`;

                mostrarFormulario();

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }

        if (botonEstado) {
            try {
                const tipo = await consultarTipo(botonEstado.dataset.estado);
                const nuevoEstado = esActivo(tipo) ? "Inactivo" : "Activo";

                await guardar(String(tipo.tipo_id), {
                    tipo_descripcion: tipo.tipo_descripcion,
                    tipo_sigla: tipo.tipo_sigla,
                    tipo_estado: nuevoEstado
                });

                await cargarTiposIdentidad();
                avisarListado(`"${tipo.tipo_descripcion}" quedó ${nuevoEstado.toLowerCase()}.`);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }
    });

    limpiarFormulario();
    cargarTiposIdentidad();
};
