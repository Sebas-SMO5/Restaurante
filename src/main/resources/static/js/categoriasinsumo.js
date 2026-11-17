
const API_CATEGORIAS_INSUMO_VISTA = "/api/categorias-insumo";

window.inicializarCategoriasInsumo = function () {
    const vista = document.getElementById("vistaCategoriasInsumo");

    if (!vista) return;

    const formulario = document.getElementById("categoriaInsumoForm");
    const seccionFormulario = document.getElementById("categoriaInsumoFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioCategoriaInsumo");
    const tabla = document.getElementById("tablaCategoriasInsumo");
    const mensajeFormulario = document.getElementById("categoriaInsumoMensaje");
    const mensajeListado = document.getElementById("categoriasInsumoListadoMensaje");
    const buscar = document.getElementById("buscarCategoriaInsumo");
    const campoId = document.getElementById("categoriaInsumoId");
    const campoDescripcion = document.getElementById("categoriaInsumoDescripcion");
    const campoEstado = document.getElementById("categoriaInsumoEstado");

    if (vista.dataset.inicializada === "true") {
        cargarCategorias();
        return;
    }

    vista.dataset.inicializada = "true";

    let categorias = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (c) => c.cins_estado?.toLowerCase() === "activo";

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
        tituloFormulario.textContent = "Registrar categoría de insumo";
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

    const guardar = async (id, datos) => {
        const respuesta = await fetch(id ? `${API_CATEGORIAS_INSUMO_VISTA}/${id}` : API_CATEGORIAS_INSUMO_VISTA, {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado || "No se pudo guardar la categoría de insumo.");
        }

        return resultado;
    };

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtradas = categorias.filter(c =>
            String(c.cins_id).includes(texto) ||
            (c.cins_descripcion ?? "").toLowerCase().includes(texto) ||
            (c.cins_estado ?? "").toLowerCase().includes(texto)
        );

        if (!filtradas.length) {
            tabla.innerHTML = `<tr><td colspan="4">${categorias.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay categorías de insumo registradas."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtradas.map(c => `
            <tr>
                <td>${escapar(c.cins_id)}</td>
                <td>${escapar(c.cins_descripcion)}</td>
                <td>
                    <span class="badge ${esActivo(c) ? "badge-success" : "badge-muted"}">
                        ${escapar(c.cins_estado)}
                    </span>
                </td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button"
                            data-editar="${escapar(c.cins_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button"
                            data-estado="${escapar(c.cins_id)}">
                            ${esActivo(c) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarCategorias() {
        try {
            const respuesta = await fetch(API_CATEGORIAS_INSUMO_VISTA);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar las categorías de insumo.");
            }

            categorias = await respuesta.json();
            categorias.sort((a, b) => a.cins_id - b.cins_id);

            document.getElementById("totalCategoriasInsumo").textContent = categorias.length;
            document.getElementById("categoriasInsumoActivas").textContent = categorias.filter(esActivo).length;
            document.getElementById("categoriasInsumoInactivas").textContent =
                categorias.filter(c => !esActivo(c)).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="4">Error al cargar las categorías de insumo.</td></tr>`;
        }
    }

    document.getElementById("btnNuevaCategoriaInsumo").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario();
    });

    document.getElementById("btnCerrarCategoriaInsumo").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarCategoriaInsumo").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarCategoriasInsumo").addEventListener("click", cargarCategorias);

    buscar.addEventListener("input", pintarTabla);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campoId.value;
        const datos = {
            cins_descripcion: campoDescripcion.value.trim(),
            cins_estado: campoEstado.value
        };

        if (!datos.cins_descripcion) {
            mostrarMensaje(mensajeFormulario, "La descripción es obligatoria.", "error");
            return;
        }

        const repetida = categorias.some(c =>
            String(c.cins_id) !== id &&
            (c.cins_descripcion ?? "").trim().toLowerCase() === datos.cins_descripcion.toLowerCase()
        );

        if (repetida) {
            mostrarMensaje(mensajeFormulario, "Ya existe una categoría con esa descripción.", "error");
            return;
        }

        try {
            const resultado = await guardar(id, datos);

            ocultarFormulario();
            await cargarCategorias();
            avisarListado(resultado);

        } catch (error) {
            mostrarMensaje(mensajeFormulario, error.message, "error");
        }
    });

    tabla.addEventListener("click", async (evento) => {
        const botonEditar = evento.target.closest("[data-editar]");
        const botonEstado = evento.target.closest("[data-estado]");

        if (!botonEditar && !botonEstado) return;

        const id = botonEditar ? botonEditar.dataset.editar : botonEstado.dataset.estado;
        const categoria = categorias.find(c => String(c.cins_id) === String(id));

        if (!categoria) return;

        if (botonEditar) {
            limpiarFormulario();
            campoId.value = categoria.cins_id;
            campoDescripcion.value = categoria.cins_descripcion ?? "";
            campoEstado.value = esActivo(categoria) ? "Activo" : "Inactivo";
            tituloFormulario.textContent = `Editar categoría de insumo #${categoria.cins_id}`;
            mostrarFormulario();
            return;
        }

        try {
            const nuevoEstado = esActivo(categoria) ? "Inactivo" : "Activo";

            await guardar(String(categoria.cins_id), {
                cins_descripcion: categoria.cins_descripcion,
                cins_estado: nuevoEstado
            });

            await cargarCategorias();
            avisarListado(`"${categoria.cins_descripcion}" quedó ${nuevoEstado.toLowerCase()}.`);

        } catch (error) {
            avisarListado(error.message, "error");
        }
    });

    limpiarFormulario();
    cargarCategorias();
};
