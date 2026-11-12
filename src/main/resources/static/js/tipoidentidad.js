
const API_TIPOS_IDENTIDAD = "/api/tipos-identidad";

let tiposIdentidad = [];

function escaparTipoIdentidad(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[caracter]);
}

function mostrarMensajeTipoIdentidad(id, texto, tipo = "success") {
    const elemento = document.getElementById(id);

    if (!elemento) return;

    elemento.textContent = texto;
    elemento.className = `message ${tipo}`;
    elemento.classList.remove("hidden");
}

function ocultarMensajeTipoIdentidad(id) {
    const elemento = document.getElementById(id);

    if (!elemento) return;

    elemento.textContent = "";
    elemento.className = "message hidden";
}

function actualizarEstadisticasTipoIdentidad() {
    const total = document.getElementById("totalTiposIdentidad");
    const activos = document.getElementById("tiposIdentidadActivos");

    if (total) {
        total.textContent = tiposIdentidad.length;
    }

    if (activos) {
        activos.textContent = tiposIdentidad.filter(tipo =>
            ["activo", "1", "true"].includes(
                String(tipo.tipo_estado).trim().toLowerCase()
            )
        ).length;
    }
}

function mostrarTiposIdentidad(lista) {
    const tabla = document.getElementById("tablaTiposIdentidad");

    if (!tabla) return;

    if (lista.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="5">No hay tipos de identidad para mostrar.</td>
            </tr>
        `;
        return;
    }

    tabla.innerHTML = lista.map(tipo => `
        <tr>
            <td>${escaparTipoIdentidad(tipo.tipo_id)}</td>
            <td>${escaparTipoIdentidad(tipo.tipo_descripcion)}</td>
            <td>${escaparTipoIdentidad(tipo.tipo_sigla)}</td>
            <td>${escaparTipoIdentidad(tipo.tipo_estado)}</td>
            <td>
                <button
                    type="button"
                    class="action-button"
                    data-editar-tipo-identidad="${escaparTipoIdentidad(tipo.tipo_id)}">
                    Editar
                </button>
            </td>
        </tr>
    `).join("");
}

async function cargarTiposIdentidad() {
    const tabla = document.getElementById("tablaTiposIdentidad");

    if (!tabla) return;

    tabla.innerHTML = `
        <tr>
            <td colspan="5">Cargando tipos de identidad...</td>
        </tr>
    `;

    try {
        const respuesta = await fetch(API_TIPOS_IDENTIDAD);

        if (!respuesta.ok) {
            const detalle = await respuesta.text();
            throw new Error(detalle || "No se pudieron consultar los tipos de identidad.");
        }

        const datos = await respuesta.json();

        if (!Array.isArray(datos)) {
            throw new Error("La respuesta del servidor no tiene el formato esperado.");
        }

        tiposIdentidad = datos;

        actualizarEstadisticasTipoIdentidad();
        mostrarTiposIdentidad(tiposIdentidad);
        ocultarMensajeTipoIdentidad("mensajeTiposIdentidad");

    } catch (error) {
        tabla.innerHTML = `
            <tr>
                <td colspan="5">No se pudieron cargar los tipos de identidad.</td>
            </tr>
        `;

        mostrarMensajeTipoIdentidad(
            "mensajeTiposIdentidad",
            error.message,
            "error"
        );

        console.error(error);
    }
}

function abrirFormularioTipoIdentidad(tipo = null) {
    const formulario = document.getElementById("tipoIdentidadForm");
    const seccion = document.getElementById("tipoIdentidadFormSection");

    if (!formulario || !seccion) return;

    formulario.reset();

    ocultarMensajeTipoIdentidad("tipoIdentidadMensaje");

    document.getElementById("tipoIdentidadId").value =
        tipo ? tipo.tipo_id : "";

    document.getElementById("tipoIdentidadDescripcion").value =
        tipo ? tipo.tipo_descripcion : "";

    document.getElementById("tipoIdentidadSigla").value =
        tipo ? tipo.tipo_sigla : "";

    document.getElementById("tipoIdentidadEstado").value =
        tipo ? tipo.tipo_estado : "";

    document.getElementById("tipoIdentidadFormTitle").textContent =
        tipo ? "Actualizar tipo de identidad" : "Registrar tipo de identidad";

    document.getElementById("btnGuardarTipoIdentidad").textContent =
        tipo ? "Actualizar tipo de identidad" : "Guardar tipo de identidad";

    seccion.classList.remove("hidden");

    seccion.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

function cerrarFormularioTipoIdentidad() {
    const formulario = document.getElementById("tipoIdentidadForm");
    const seccion = document.getElementById("tipoIdentidadFormSection");

    if (!formulario || !seccion) return;

    formulario.reset();
    seccion.classList.add("hidden");

    ocultarMensajeTipoIdentidad("tipoIdentidadMensaje");
}

async function editarTipoIdentidad(id) {
    try {
        const respuesta = await fetch(`${API_TIPOS_IDENTIDAD}/${encodeURIComponent(id)}`);

        if (!respuesta.ok) {
            const detalle = await respuesta.text();
            throw new Error(detalle || "No se encontró el tipo de identidad.");
        }

        const tipo = await respuesta.json();

        abrirFormularioTipoIdentidad(tipo);

    } catch (error) {
        mostrarMensajeTipoIdentidad(
            "mensajeTiposIdentidad",
            error.message,
            "error"
        );

        console.error(error);
    }
}

async function guardarTipoIdentidad(evento) {
    evento.preventDefault();

    const id = document.getElementById("tipoIdentidadId").value;

    const tipo = {
        tipo_descripcion: document
            .getElementById("tipoIdentidadDescripcion")
            .value.trim(),

        tipo_sigla: document
            .getElementById("tipoIdentidadSigla")
            .value.trim(),

        tipo_estado: document
            .getElementById("tipoIdentidadEstado")
            .value
    };

    if (!tipo.tipo_descripcion || !tipo.tipo_sigla || !tipo.tipo_estado) {
        mostrarMensajeTipoIdentidad(
            "tipoIdentidadMensaje",
            "Todos los campos son obligatorios.",
            "error"
        );
        return;
    }

    const boton = document.getElementById("btnGuardarTipoIdentidad");

    boton.disabled = true;

    try {
        const respuesta = await fetch(
            id
                ? `${API_TIPOS_IDENTIDAD}/${encodeURIComponent(id)}`
                : API_TIPOS_IDENTIDAD,
            {
                method: id ? "PUT" : "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(tipo)
            }
        );

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado || "No se pudo guardar el tipo de identidad.");
        }

        cerrarFormularioTipoIdentidad();

        await cargarTiposIdentidad();

        mostrarMensajeTipoIdentidad(
            "mensajeTiposIdentidad",
            resultado || "Tipo de identidad guardado correctamente.",
            "success"
        );

    } catch (error) {
        mostrarMensajeTipoIdentidad(
            "tipoIdentidadMensaje",
            error.message,
            "error"
        );

        console.error(error);

    } finally {
        boton.disabled = false;
    }
}

function buscarTiposIdentidad() {
    const campoBusqueda = document.getElementById("buscarTipoIdentidad");

    if (!campoBusqueda) return;

    const busqueda = campoBusqueda.value.trim().toLowerCase();

    const filtrados = tiposIdentidad.filter(tipo =>
        String(tipo.tipo_descripcion ?? "").toLowerCase().includes(busqueda) ||
        String(tipo.tipo_sigla ?? "").toLowerCase().includes(busqueda) ||
        String(tipo.tipo_id ?? "").includes(busqueda) ||
        String(tipo.tipo_estado ?? "").toLowerCase().includes(busqueda)
    );

    mostrarTiposIdentidad(filtrados);
}

function inicializarTipoIdentidad() {
    const vista = document.getElementById("vistaTipoIdentidad");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    document.getElementById("tipoIdentidadForm")
        .addEventListener("submit", guardarTipoIdentidad);

    document.getElementById("btnNuevoTipoIdentidad")
        .addEventListener("click", () => abrirFormularioTipoIdentidad());

    document.getElementById("btnCancelarTipoIdentidad")
        .addEventListener("click", cerrarFormularioTipoIdentidad);

    document.getElementById("btnActualizarTiposIdentidad")
        .addEventListener("click", cargarTiposIdentidad);

    document.getElementById("buscarTipoIdentidad")
        .addEventListener("input", buscarTiposIdentidad);

    document.getElementById("tablaTiposIdentidad")
        .addEventListener("click", evento => {
            const boton = evento.target.closest("[data-editar-tipo-identidad]");

            if (!boton) return;

            editarTipoIdentidad(Number(boton.dataset.editarTipoIdentidad));
        });

    cargarTiposIdentidad();
}

window.inicializarTipoIdentidad = inicializarTipoIdentidad;
