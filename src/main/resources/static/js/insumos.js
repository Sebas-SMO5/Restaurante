
const API_INSUMOS = "/api/insumos";
const API_CATEGORIAS_INSUMO = "/api/categorias-insumo";
const API_UNIDADES_INSUMO = "/api/unidades-medida";

window.inicializarInsumos = function () {
    const vista = document.getElementById("vistaInsumos");

    if (!vista) return;

    const formulario = document.getElementById("insumoForm");
    const seccionFormulario = document.getElementById("insumoFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioInsumo");
    const tabla = document.getElementById("tablaInsumos");
    const buscar = document.getElementById("buscarInsumo");
    const filtroCategoria = document.getElementById("filtroCategoriaInsumo");
    const mensajeListado = document.getElementById("mensajeInsumo");
    const mensajeFormulario = document.getElementById("insumoMensaje");
    const botonGuardar = document.getElementById("btnGuardarInsumo");
    const selectCategoria = document.getElementById("cins_id");
    const selectUnidad = document.getElementById("um_id");

    const campo = (id) => document.getElementById(id);

    if (vista.dataset.inicializada === "true") {
        cargarTodo();
        return;
    }

    vista.dataset.inicializada = "true";

    let insumos = [];
    let categorias = [];
    let unidades = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (registro, campoEstado) =>
        String(registro?.[campoEstado] ?? "").toLowerCase() === "activo";

    const booleano = (valor) => valor === true || valor === 1 || valor === "1" || valor === "true";

    const stockBajo = (i) => booleano(i.ins_estado) && Number(i.ins_stock) <= Number(i.ins_stock_min);

    const formatoCantidad = (valor) => Number(valor ?? 0).toLocaleString("es-CO", { maximumFractionDigits: 3 });

    const formatoPrecio = (valor) => valor == null
        ? "—"
        : Number(valor).toLocaleString("es-CO", { style: "currency", currency: "COP", maximumFractionDigits: 2 });

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

    /** Llena una lista con los registros activos, conservando el valor actual aunque esté inactivo. */
    function llenarSelect(select, lista, idCampo, textoCampo, estadoCampo, actual, vacio) {
        const disponibles = lista.filter(r =>
            esActivo(r, estadoCampo) || Number(r[idCampo]) === Number(actual)
        );

        select.innerHTML = `<option value="">${vacio}</option>` +
            disponibles.map(r => `
                <option value="${escapar(r[idCampo])}">
                    ${escapar(textoCampo(r))}${esActivo(r, estadoCampo) ? "" : " (inactiva)"}
                </option>
            `).join("");

        select.value = actual ?? "";
    }

    const llenarCategorias = (actual = null) => llenarSelect(
        selectCategoria, categorias, "cins_id", r => r.cins_descripcion, "cins_estado",
        actual, "Seleccione una categoría");

    const llenarUnidades = (actual = null) => llenarSelect(
        selectUnidad, unidades, "um_id", r => `${r.um_descripcion} (${r.um_abreviatura})`, "um_estado",
        actual, "Seleccione una unidad");

    function llenarFiltroCategorias() {
        const valor = filtroCategoria.value;

        filtroCategoria.innerHTML = `<option value="">Todas las categorías</option>` +
            categorias.map(c => `
                <option value="${escapar(c.cins_id)}">${escapar(c.cins_descripcion)}</option>
            `).join("");

        filtroCategoria.value = valor;
    }

    function limpiarFormulario() {
        formulario.reset();
        campo("ins_id").value = "";
        campo("ins_estado").checked = true;
        campo("ins_vendible").checked = false;
        llenarCategorias();
        llenarUnidades();
        tituloFormulario.textContent = "Registrar materia prima";
        botonGuardar.textContent = "Guardar materia prima";
        ocultarMensaje(mensajeFormulario);
    }

    function mostrarFormulario() {
        seccionFormulario.classList.remove("hidden");
        seccionFormulario.scrollIntoView({ behavior: "smooth", block: "start" });
        campo("ins_nombre").focus({ preventScroll: true });
    }

    function ocultarFormulario() {
        seccionFormulario.classList.add("hidden");
        limpiarFormulario();
    }

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();
        const categoria = filtroCategoria.value;

        const filtrados = insumos.filter(i =>
            (!categoria || String(i.cins_id) === categoria) &&
            [i.ins_id, i.ins_nombre, i.ins_codigo, i.ins_codigo_barras, i.cins_descripcion]
                .some(valor => String(valor ?? "").toLowerCase().includes(texto))
        );

        if (!filtrados.length) {
            tabla.innerHTML = `<tr><td colspan="8">${insumos.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay materias primas registradas."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtrados.map(i => {
            const unidad = escapar(i.um_abreviatura);
            const activo = booleano(i.ins_estado);

            return `
                <tr>
                    <td>${escapar(i.ins_id)}</td>
                    <td>
                        ${escapar(i.ins_nombre)}
                        ${i.ins_codigo ? `<span class="badge badge-neutral badge-small">${escapar(i.ins_codigo)}</span>` : ""}
                    </td>
                    <td>${escapar(i.cins_descripcion)}</td>
                    <td>
                        ${formatoCantidad(i.ins_stock)} ${unidad}
                        ${stockBajo(i) ? `<span class="badge badge-warning badge-small">Stock bajo</span>` : ""}
                    </td>
                    <td>${formatoCantidad(i.ins_stock_min)} ${unidad}</td>
                    <td>${formatoPrecio(i.ins_precio_compra)}</td>
                    <td>
                        <span class="badge ${activo ? "badge-success" : "badge-muted"}">
                            ${activo ? "Activa" : "Inactiva"}
                        </span>
                    </td>
                    <td>
                        <div class="actions">
                            <button class="action-button" type="button"
                                data-editar="${escapar(i.ins_id)}">
                                Editar
                            </button>
                            <button class="action-button action-button-light" type="button"
                                data-estado="${escapar(i.ins_id)}">
                                ${activo ? "Inactivar" : "Activar"}
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join("");
    }

    async function obtenerLista(url, nombre) {
        const respuesta = await fetch(url);

        if (!respuesta.ok) {
            throw new Error(`No se pudieron cargar ${nombre}.`);
        }

        return respuesta.json();
    }

    async function cargarCatalogos() {
        try {
            [categorias, unidades] = await Promise.all([
                obtenerLista(API_CATEGORIAS_INSUMO, "las categorías de insumo"),
                obtenerLista(API_UNIDADES_INSUMO, "las unidades de medida")
            ]);
        } catch (error) {
            console.error(error);
            avisarListado(error.message, "error");
        }

        llenarCategorias();
        llenarUnidades();
        llenarFiltroCategorias();
    }

    async function cargarInsumos() {
        try {
            insumos = await obtenerLista(API_INSUMOS, "las materias primas");

            document.getElementById("totalInsumos").textContent = insumos.length;
            document.getElementById("insumosActivos").textContent =
                insumos.filter(i => booleano(i.ins_estado)).length;
            document.getElementById("insumosStockBajo").textContent = insumos.filter(stockBajo).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="8">Error al cargar las materias primas.</td></tr>`;
        }
    }

    async function cargarTodo() {
        await cargarCatalogos();
        await cargarInsumos();
    }

    async function guardar(id, datos) {
        const respuesta = await fetch(id ? `${API_INSUMOS}/${id}` : API_INSUMOS, {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado || "No se pudo guardar la materia prima.");
        }

        return resultado;
    }

    function datosFormulario() {
        const valor = (id) => campo(id).value.trim();
        const precio = valor("ins_precio_compra");

        return {
            cins_id: Number(selectCategoria.value) || 0,
            um_id: Number(selectUnidad.value) || 0,
            ins_nombre: valor("ins_nombre"),
            ins_codigo: valor("ins_codigo") || null,
            ins_codigo_barras: valor("ins_codigo_barras") || null,
            ins_precio_compra: precio === "" ? null : Number(precio),
            ins_stock: valor("ins_stock") === "" ? NaN : Number(valor("ins_stock")),
            ins_stock_min: valor("ins_stock_min") === "" ? NaN : Number(valor("ins_stock_min")),
            ins_vendible: campo("ins_vendible").checked,
            ins_imagen: valor("ins_imagen") || null,
            ins_estado: campo("ins_estado").checked
        };
    }

    function validar(datos, id) {
        if (!datos.ins_nombre) {
            return "El nombre de la materia prima es obligatorio.";
        }

        const repetido = insumos.some(i =>
            String(i.ins_id) !== String(id ?? "") &&
            String(i.ins_nombre ?? "").trim().toLowerCase() === datos.ins_nombre.toLowerCase()
        );

        if (repetido) {
            return "Ya existe una materia prima con ese nombre.";
        }

        if (!datos.cins_id) {
            return "Selecciona la categoría.";
        }

        if (!datos.um_id) {
            return "Selecciona la unidad de medida.";
        }

        if (!Number.isFinite(datos.ins_stock) || datos.ins_stock < 0 ||
            !Number.isFinite(datos.ins_stock_min) || datos.ins_stock_min < 0) {
            return "Las existencias y el stock mínimo son obligatorios y no pueden ser negativos.";
        }

        if (datos.ins_precio_compra !== null &&
            (!Number.isFinite(datos.ins_precio_compra) || datos.ins_precio_compra < 0)) {
            return "El precio de compra no puede ser negativo.";
        }

        return null;
    }

    document.getElementById("btnNuevoInsumo").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario();
    });

    document.getElementById("btnCerrarInsumo").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarInsumo").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarInsumos").addEventListener("click", cargarTodo);

    buscar.addEventListener("input", pintarTabla);
    filtroCategoria.addEventListener("change", pintarTabla);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campo("ins_id").value || null;
        const datos = datosFormulario();
        const error = validar(datos, id);

        if (error) {
            mostrarMensaje(mensajeFormulario, error, "error");
            return;
        }

        botonGuardar.disabled = true;

        try {
            const resultado = await guardar(id, datos);

            ocultarFormulario();
            await cargarInsumos();
            avisarListado(resultado);

        } catch (error) {
            mostrarMensaje(mensajeFormulario, error.message, "error");
        } finally {
            botonGuardar.disabled = false;
        }
    });

    tabla.addEventListener("click", async (evento) => {
        const botonEditar = evento.target.closest("[data-editar]");
        const botonEstado = evento.target.closest("[data-estado]");

        if (!botonEditar && !botonEstado) return;

        const id = botonEditar ? botonEditar.dataset.editar : botonEstado.dataset.estado;
        const insumo = insumos.find(i => String(i.ins_id) === String(id));

        if (!insumo) return;

        if (botonEditar) {
            limpiarFormulario();

            campo("ins_id").value = insumo.ins_id;
            campo("ins_nombre").value = insumo.ins_nombre ?? "";
            llenarCategorias(insumo.cins_id);
            llenarUnidades(insumo.um_id);
            campo("ins_codigo").value = insumo.ins_codigo ?? "";
            campo("ins_codigo_barras").value = insumo.ins_codigo_barras ?? "";
            campo("ins_precio_compra").value = insumo.ins_precio_compra ?? "";
            campo("ins_stock").value = insumo.ins_stock ?? "";
            campo("ins_stock_min").value = insumo.ins_stock_min ?? "";
            campo("ins_imagen").value = insumo.ins_imagen ?? "";
            campo("ins_vendible").checked = booleano(insumo.ins_vendible);
            campo("ins_estado").checked = booleano(insumo.ins_estado);

            tituloFormulario.textContent = `Editar materia prima: ${insumo.ins_nombre}`;
            botonGuardar.textContent = "Actualizar materia prima";

            mostrarFormulario();
            return;
        }

        try {
            const activo = booleano(insumo.ins_estado);

            await guardar(insumo.ins_id, { ...insumo, ins_estado: !activo });
            await cargarInsumos();
            avisarListado(`"${insumo.ins_nombre}" quedó ${activo ? "inactiva" : "activa"}.`);

        } catch (error) {
            avisarListado(error.message, "error");
        }
    });

    limpiarFormulario();
    cargarTodo();
};

// Nombre alternativo usado por el menú.
window.inicializarMateriasPrimas = window.inicializarInsumos;
