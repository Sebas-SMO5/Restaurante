
const API_CONCEPTOS = "/api/conceptos";

window.inicializarConceptos = function () {
    const vista = document.getElementById("vistaConceptos");

    if (!vista) return;

    const formulario = document.getElementById("conceptoForm");
    const seccionFormulario = document.getElementById("conceptoFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioConcepto");
    const tabla = document.getElementById("tablaConceptos");
    const mensajeFormulario = document.getElementById("conceptoMensaje");
    const mensajeListado = document.getElementById("conceptosListadoMensaje");
    const buscar = document.getElementById("buscarConcepto");
    const campoId = document.getElementById("conceptoId");
    const campoDescripcion = document.getElementById("conceptoDescripcion");
    const campoEstado = document.getElementById("conceptoEstado");

    if (vista.dataset.inicializada === "true") {
        cargarConceptos();
        return;
    }

    vista.dataset.inicializada = "true";

    let conceptos = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (concepto) => concepto.con_estado?.toLowerCase() === "activo";

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
        tituloFormulario.textContent = "Registrar concepto de pago";
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

    const consultarConcepto = async (id) => {
        const respuesta = await fetch(`${API_CONCEPTOS}/${id}`);

        if (!respuesta.ok) {
            throw new Error("No se pudo consultar el concepto.");
        }

        return respuesta.json();
    };

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = conceptos.filter(concepto =>
            String(concepto.con_id).includes(texto) ||
            (concepto.con_descripcion ?? "").toLowerCase().includes(texto) ||
            (concepto.con_estado ?? "").toLowerCase().includes(texto)
        );

        if (!filtrados.length) {
            tabla.innerHTML = `<tr><td colspan="4">${conceptos.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay conceptos registrados."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtrados.map(concepto => `
            <tr>
                <td>${escapar(concepto.con_id)}</td>
                <td>${escapar(concepto.con_descripcion)}</td>
                <td>
                    <span class="badge ${esActivo(concepto) ? "badge-success" : "badge-muted"}">
                        ${escapar(concepto.con_estado)}
                    </span>
                </td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button"
                            data-editar="${escapar(concepto.con_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button"
                            data-estado="${escapar(concepto.con_id)}">
                            ${esActivo(concepto) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarConceptos() {
        try {
            const respuesta = await fetch(API_CONCEPTOS);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar los conceptos.");
            }

            conceptos = await respuesta.json();
            conceptos.sort((a, b) => a.con_id - b.con_id);

            document.getElementById("totalConceptos").textContent = conceptos.length;
            document.getElementById("conceptosActivos").textContent =
                conceptos.filter(esActivo).length;
            document.getElementById("conceptosInactivos").textContent =
                conceptos.filter(concepto => !esActivo(concepto)).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="4">Error al cargar los conceptos.</td></tr>`;
        }
    }

    document.getElementById("btnNuevoConcepto").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario(campoId);
    });

    document.getElementById("btnCerrarConcepto").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarConcepto").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarConceptos").addEventListener("click", cargarConceptos);

    buscar.addEventListener("input", pintarTabla);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campoId.value.trim();
        const editando = formulario.dataset.edicion === "true";

        const datos = {
            con_id: Number(id),
            con_descripcion: campoDescripcion.value.trim(),
            con_estado: campoEstado.value
        };

        if (!id || !Number.isInteger(datos.con_id) || datos.con_id <= 0 || datos.con_id > 999) {
            mostrarMensaje(mensajeFormulario, "Ingresa un código válido entre 1 y 999.", "error");
            return;
        }

        if (!datos.con_descripcion || !datos.con_estado) {
            mostrarMensaje(mensajeFormulario, "Completa todos los campos.", "error");
            return;
        }

        if (!editando && conceptos.some(concepto => concepto.con_id === datos.con_id)) {
            mostrarMensaje(mensajeFormulario, "Ya existe un concepto con ese código.", "error");
            return;
        }

        try {
            const respuesta = await fetch(
                editando ? `${API_CONCEPTOS}/${id}` : API_CONCEPTOS,
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
            await cargarConceptos();
            avisarListado(resultado);

        } catch (error) {
            mostrarMensaje(mensajeFormulario,
                error.message || "No se pudo guardar el concepto.", "error");
        }
    });

    tabla.addEventListener("click", async (evento) => {
        const botonEditar = evento.target.closest("[data-editar]");
        const botonEstado = evento.target.closest("[data-estado]");

        if (botonEditar) {
            try {
                const concepto = await consultarConcepto(botonEditar.dataset.editar);

                limpiarFormulario();
                campoId.value = concepto.con_id;
                campoId.readOnly = true;
                campoDescripcion.value = concepto.con_descripcion;
                campoEstado.value = esActivo(concepto) ? "Activo" : "Inactivo";

                formulario.dataset.edicion = "true";
                tituloFormulario.textContent = "Editar concepto de pago";

                mostrarFormulario(campoDescripcion);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }

        if (botonEstado) {
            try {
                const concepto = await consultarConcepto(botonEstado.dataset.estado);
                const nuevoEstado = esActivo(concepto) ? "Inactivo" : "Activo";

                const respuesta = await fetch(`${API_CONCEPTOS}/${concepto.con_id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        con_id: concepto.con_id,
                        con_descripcion: concepto.con_descripcion,
                        con_estado: nuevoEstado
                    })
                });

                const resultado = await respuesta.text();

                if (!respuesta.ok) {
                    throw new Error(resultado);
                }

                await cargarConceptos();
                avisarListado(`"${concepto.con_descripcion}" quedó ${nuevoEstado.toLowerCase()}.`);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }
    });

    limpiarFormulario();
    cargarConceptos();
};
