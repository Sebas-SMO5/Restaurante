
const API_CATEGORIAS = "/api/categorias";

let categorias = [];

function escaparHTMLCategoria(valor) {
    return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    })[caracter]);
}

function mostrarMensajeCategoria(elemento, mensaje, tipo = "success") {
    const contenedor = document.getElementById(elemento);

    if (!contenedor) return;

    contenedor.textContent = mensaje;
    contenedor.className = `message ${tipo}`;
    contenedor.classList.remove("hidden");
}

function ocultarMensajeCategoria(elemento) {
    const contenedor = document.getElementById(elemento);

    if (!contenedor) return;

    contenedor.textContent = "";
    contenedor.className = "message hidden";
}

async function cargarCategorias() {
    const tabla = document.getElementById("tablaCategorias");

    if (!tabla) return;

    tabla.innerHTML = `
        <tr>
            <td colspan="5">Cargando categorías...</td>
        </tr>
    `;

    try {
        const respuesta = await fetch(API_CATEGORIAS);

        if (!respuesta.ok) {
            const detalle = await respuesta.text();
            throw new Error(detalle || "No fue posible consultar las categorías.");
        }

        categorias = await respuesta.json();

        actualizarEstadisticasCategorias();
        mostrarCategorias(categorias);
        ocultarMensajeCategoria("mensajeCategorias");

    } catch (error) {
        tabla.innerHTML = `
            <tr>
                <td colspan="5">No se pudieron cargar las categorías.</td>
            </tr>
        `;

        mostrarMensajeCategoria(
            "mensajeCategorias",
            error.message,
            "error"
        );

        console.error(error);
    }
}

function actualizarEstadisticasCategorias() {
    document.getElementById("totalCategorias").textContent = categorias.length;

    document.getElementById("categoriasActivas").textContent =
        categorias.filter(categoria => categoria.estado === true).length;
}

function mostrarCategorias(lista) {
    const tabla = document.getElementById("tablaCategorias");

    if (!tabla) return;

    if (lista.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="5">No hay categorías para mostrar.</td>
            </tr>
        `;
        return;
    }

    tabla.innerHTML = lista.map(categoria => `
        <tr>
            <td>${escaparHTMLCategoria(categoria.idCategoria)}</td>
            <td>${escaparHTMLCategoria(categoria.nombre)}</td>
            <td>${escaparHTMLCategoria(categoria.descripcion || "Sin descripción")}</td>
            <td>
                <span class="status ${categoria.estado ? "active" : "inactive"}">
                    ${categoria.estado ? "Activo" : "Inactivo"}
                </span>
            </td>
            <td>
                <button
                    type="button"
                    class="action-button"
                    data-editar-categoria="${escaparHTMLCategoria(categoria.idCategoria)}">
                    Editar
                </button>
            </td>
        </tr>
    `).join("");
}

function abrirFormularioCategoria(categoria = null) {
    const formulario = document.getElementById("categoriaForm");
    const seccion = document.getElementById("categoriaFormSection");

    if (!formulario || !seccion) return;

    formulario.reset();

    ocultarMensajeCategoria("categoriaMensaje");

    document.getElementById("categoriaId").value =
        categoria ? categoria.idCategoria : "";

    document.getElementById("categoriaNombre").value =
        categoria ? categoria.nombre : "";

    document.getElementById("categoriaDescripcion").value =
        categoria ? categoria.descripcion || "" : "";

    document.getElementById("categoriaEstado").value =
        categoria ? (categoria.estado ? "1" : "0") : "1";

    document.getElementById("categoriaFormTitle").textContent =
        categoria ? "Actualizar categoría" : "Registrar categoría";

    document.getElementById("btnGuardarCategoria").textContent =
        categoria ? "Actualizar categoría" : "Guardar categoría";

    seccion.classList.remove("hidden");

    seccion.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

function cerrarFormularioCategoria() {
    const formulario = document.getElementById("categoriaForm");
    const seccion = document.getElementById("categoriaFormSection");

    if (formulario) formulario.reset();

    if (seccion) seccion.classList.add("hidden");

    ocultarMensajeCategoria("categoriaMensaje");
}

async function editarCategoria(id) {
    try {
        const respuesta = await fetch(`${API_CATEGORIAS}/${id}`);

        if (!respuesta.ok) {
            const detalle = await respuesta.text();
            throw new Error(detalle || "No se encontró la categoría seleccionada.");
        }

        const categoria = await respuesta.json();

        abrirFormularioCategoria(categoria);

    } catch (error) {
        mostrarMensajeCategoria(
            "mensajeCategorias",
            error.message,
            "error"
        );

        console.error(error);
    }
}

async function guardarCategoria(evento) {
    evento.preventDefault();

    const id = document.getElementById("categoriaId").value;

    const categoria = {
        nombre: document.getElementById("categoriaNombre").value.trim(),
        descripcion: document.getElementById("categoriaDescripcion").value.trim(),
        estado: document.getElementById("categoriaEstado").value === "1"
    };

    if (!categoria.nombre) {
        mostrarMensajeCategoria(
            "categoriaMensaje",
            "El nombre de la categoría es obligatorio.",
            "error"
        );
        return;
    }

    const boton = document.getElementById("btnGuardarCategoria");
    boton.disabled = true;

    try {
        const respuesta = await fetch(
            id ? `${API_CATEGORIAS}/${id}` : API_CATEGORIAS,
            {
                method: id ? "PUT" : "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(categoria)
            }
        );

        const mensaje = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(mensaje || "No se pudo guardar la categoría.");
        }

        cerrarFormularioCategoria();

        await cargarCategorias();

        mostrarMensajeCategoria(
            "mensajeCategorias",
            mensaje || "Categoría guardada correctamente.",
            "success"
        );

    } catch (error) {
        mostrarMensajeCategoria(
            "categoriaMensaje",
            error.message,
            "error"
        );

        console.error(error);

    } finally {
        boton.disabled = false;
    }
}

function buscarCategorias() {
    const busqueda = document
        .getElementById("buscarCategoria")
        .value
        .toLowerCase()
        .trim();

    const filtradas = categorias.filter(categoria =>
        String(categoria.nombre ?? "").toLowerCase().includes(busqueda) ||
        String(categoria.descripcion ?? "").toLowerCase().includes(busqueda)
    );

    mostrarCategorias(filtradas);
}

function inicializarCategorias() {
    const vista = document.getElementById("vistaCategorias");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    document.getElementById("categoriaForm")
        .addEventListener("submit", guardarCategoria);

    document.getElementById("btnCancelarCategoria")
        .addEventListener("click", cerrarFormularioCategoria);

    document.getElementById("btnNuevaCategoria")
        .addEventListener("click", () => abrirFormularioCategoria());

    document.getElementById("btnActualizarCategorias")
        .addEventListener("click", cargarCategorias);

    document.getElementById("buscarCategoria")
        .addEventListener("input", buscarCategorias);

    document.getElementById("tablaCategorias")
        .addEventListener("click", evento => {
            const boton = evento.target.closest("[data-editar-categoria]");

            if (!boton) return;

            editarCategoria(Number(boton.dataset.editarCategoria));
        });

    cargarCategorias();
}

window.inicializarCategorias = inicializarCategorias;
