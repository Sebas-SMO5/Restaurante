
const API_CONCEPTOS = "/api/conceptos";

window.inicializarConceptos = function () {
    const vista = document.getElementById("vistaConceptos");

    if (!vista) return;

    const formulario = document.getElementById("conceptoForm");
    const seccionFormulario = document.getElementById("conceptoFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioConcepto");
    const campoId = document.getElementById("conceptoId");
    const campoDescripcion = document.getElementById("conceptoDescripcion");
    const campoEstado = document.getElementById("conceptoEstado");
    const mensaje = document.getElementById("conceptoMensaje");
    const tabla = document.getElementById("tablaConceptos");
    const buscador = document.getElementById("buscarConcepto");

    let conceptos = [];
    let modoEdicion = false;

    function mostrarMensaje(texto, tipo) {
        mensaje.textContent = texto;
        mensaje.className = `mensaje ${tipo}`;
    }

    function limpiarMensaje() {
        mensaje.textContent = "";
        mensaje.className = "mensaje";
    }

    function escaparHTML(valor) {
        return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#039;"
        })[caracter]);
    }

    function mostrarFormulario(edicion = false) {
        modoEdicion = edicion;
        formulario.reset();
        limpiarMensaje();

        tituloFormulario.textContent = edicion
            ? "Actualizar concepto"
            : "Registrar concepto";

        campoId.disabled = edicion;
        campoEstado.value = "Activo";
        seccionFormulario.hidden = false;

        if (!edicion) {
            campoId.focus();
        }
    }

    function ocultarFormulario() {
        seccionFormulario.hidden = true;
        formulario.reset();
        campoId.disabled = false;
        limpiarMensaje();
        modoEdicion = false;
    }

    function actualizarEstadisticas() {
        const activos = conceptos.filter(concepto =>
            String(concepto.con_estado).toLowerCase() === "activo"
        ).length;

        document.getElementById("totalConceptos").textContent = conceptos.length;
        document.getElementById("conceptosActivos").textContent = activos;
        document.getElementById("conceptosInactivos").textContent = conceptos.length - activos;
    }

    function renderizarTabla(lista = conceptos) {
        if (lista.length === 0) {
            tabla.innerHTML = `
                <tr>
                    <td colspan="4">No hay conceptos registrados.</td>
                </tr>
            `;
            return;
        }

        tabla.innerHTML = lista.map(concepto => {
            const estado = String(concepto.con_estado ?? "");
            const claseEstado = estado.toLowerCase() === "activo"
                ? "estado-activo"
                : "estado-inactivo";

            return `
                <tr>
                    <td>${escaparHTML(concepto.con_id)}</td>
                    <td>${escaparHTML(concepto.con_descripcion)}</td>
                    <td>
                        <span class="${claseEstado}">
                            ${escaparHTML(estado)}
                        </span>
                    </td>
                    <td>
                        <button
                            type="button"
                            class="btn-secondary btn-editar-concepto"
                            data-id="${escaparHTML(concepto.con_id)}">
                            Editar
                        </button>
                    </td>
                </tr>
            `;
        }).join("");
    }

    async function cargarConceptos() {
        try {
            const respuesta = await fetch(API_CONCEPTOS);

            if (!respuesta.ok) {
                throw new Error("No fue posible consultar los conceptos.");
            }

            conceptos = await respuesta.json();

            actualizarEstadisticas();
            filtrarConceptos();
        } catch (error) {
            tabla.innerHTML = `
                <tr>
                    <td colspan="4">${escaparHTML(error.message)}</td>
                </tr>
            `;
        }
    }

    function filtrarConceptos() {
        const busqueda = buscador.value.trim().toLowerCase();

        const filtrados = conceptos.filter(concepto =>
            String(concepto.con_id).includes(busqueda) ||
            String(concepto.con_descripcion).toLowerCase().includes(busqueda) ||
            String(concepto.con_estado).toLowerCase().includes(busqueda)
        );

        renderizarTabla(filtrados);
    }

    async function editarConcepto(id) {
        try {
            const respuesta = await fetch(`${API_CONCEPTOS}/${id}`);

            if (!respuesta.ok) {
                throw new Error("No fue posible consultar el concepto seleccionado.");
            }

            const concepto = await respuesta.json();

            mostrarFormulario(true);
            campoId.value = concepto.con_id;
            campoDescripcion.value = concepto.con_descripcion;
            campoEstado.value = concepto.con_estado;

        } catch (error) {
            alert(error.message);
        }
    }

    async function guardarConcepto(evento) {
        evento.preventDefault();
        limpiarMensaje();

        const concepto = {
            con_id: Number(campoId.value),
            con_descripcion: campoDescripcion.value.trim(),
            con_estado: campoEstado.value
        };

        if (!modoEdicion && (!Number.isInteger(concepto.con_id) || concepto.con_id <= 0)) {
            mostrarMensaje("Ingresa un código válido.", "mensaje-error");
            return;
        }

        if (!concepto.con_descripcion) {
            mostrarMensaje("La descripción es obligatoria.", "mensaje-error");
            return;
        }

        const url = modoEdicion
            ? `${API_CONCEPTOS}/${concepto.con_id}`
            : API_CONCEPTOS;

        const metodo = modoEdicion ? "PUT" : "POST";

        try {
            const respuesta = await fetch(url, {
                method: metodo,
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(concepto)
            });

            const resultado = await respuesta.text();

            if (!respuesta.ok) {
                throw new Error(resultado || "No fue posible guardar el concepto.");
            }

            mostrarMensaje(resultado, "mensaje-exito");

            await cargarConceptos();
            ocultarFormulario();

        } catch (error) {
            mostrarMensaje(error.message, "mensaje-error");
        }
    }

    document.getElementById("btnNuevoConcepto").addEventListener("click", () => {
        mostrarFormulario(false);
    });

    document.getElementById("btnCancelarConcepto").addEventListener("click", ocultarFormulario);

    formulario.addEventListener("submit", guardarConcepto);

    buscador.addEventListener("input", filtrarConceptos);

    tabla.addEventListener("click", evento => {
        const boton = evento.target.closest(".btn-editar-concepto");

        if (boton) {
            editarConcepto(boton.dataset.id);
        }
    });

    cargarConceptos();
};
