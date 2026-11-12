
const API_INSUMOS = "/api/insumos";

window.inicializarMateriasPrimas = inicializarMateriasPrimas;

function inicializarMateriasPrimas() {
    const formulario = document.getElementById("insumoForm");
    const contenedorFormulario = document.getElementById("formularioInsumo");
    const tabla = document.getElementById("tablaInsumos");
    const mensaje = document.getElementById("mensajeInsumo");
    const buscar = document.getElementById("buscarInsumo");

    if (!formulario || !contenedorFormulario || !tabla || !mensaje || !buscar) {
        console.error("No se encontraron todos los elementos necesarios de la vista de materias primas.");
        return;
    }

    if (tabla.dataset.inicializada === "true") {
        cargarInsumos();
        return;
    }

    tabla.dataset.inicializada = "true";

    let insumos = [];
    let editando = false;

    const campos = [
        "ins_id",
        "sede_id",
        "cins_id",
        "pres_id",
        "ins_nombre",
        "ins_codigo",
        "ins_codigo_barras",
        "ins_precio_compra",
        "ins_stock",
        "ins_stock_min",
        "ins_imagen"
    ];

    function mostrarMensaje(texto, error = false) {
        mensaje.textContent = texto;
        mensaje.className = error ? "message error" : "message success";
        mensaje.classList.remove("hidden");
    }

    function limpiarMensaje() {
        mensaje.textContent = "";
        mensaje.className = "message hidden";
    }

    function escaparHTML(valor) {
        return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        })[caracter]);
    }

    function obtenerValor(id) {
        const elemento = document.getElementById(id);
        return elemento ? elemento.value.trim() : "";
    }

    function limpiarFormulario() {
        formulario.reset();

        document.getElementById("ins_estado").checked = true;
        document.getElementById("ins_vendible").checked = false;
        document.getElementById("ins_id").readOnly = false;

        editando = false;

        document.getElementById("tituloFormularioInsumo").textContent = "Registrar insumo";
        document.getElementById("btnGuardarInsumo").textContent = "Guardar";

        limpiarMensaje();
    }

    function abrirFormulario() {
        contenedorFormulario.hidden = false;
        document.getElementById("ins_nombre").focus();
    }

    function cerrarFormulario() {
        contenedorFormulario.hidden = true;
        limpiarFormulario();
    }

    function mostrarInsumos(lista) {
        if (!lista.length) {
            tabla.innerHTML = `
                <tr>
                    <td colspan="9">No se encontraron insumos.</td>
                </tr>
            `;
            return;
        }

        tabla.innerHTML = lista.map(insumo => `
            <tr>
                <td>${escaparHTML(insumo.ins_id)}</td>
                <td>${escaparHTML(insumo.ins_nombre)}</td>
                <td>${escaparHTML(insumo.ins_codigo || "—")}</td>
                <td>${insumo.ins_precio_compra == null
                    ? "—"
                    : Number(insumo.ins_precio_compra).toLocaleString("es-CO", {
                        style: "currency",
                        currency: "COP"
                    })}</td>
                <td>${escaparHTML(insumo.ins_stock)}</td>
                <td>${escaparHTML(insumo.ins_stock_min)}</td>
                <td>${insumo.ins_vendible ? "Sí" : "No"}</td>
                <td>${insumo.ins_estado ? "Activo" : "Inactivo"}</td>
                <td>
                    <div class="mp-acciones-tabla">
                        <button
                            type="button"
                            data-editar-insumo="${escaparHTML(insumo.ins_id)}">
                            Editar
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarInsumos() {
        tabla.innerHTML = `
            <tr>
                <td colspan="9">Cargando insumos...</td>
            </tr>
        `;

        try {
            const respuesta = await fetch(API_INSUMOS);

            if (!respuesta.ok) {
                const detalle = await respuesta.text();
                throw new Error(detalle || `Error al consultar los insumos. Código: ${respuesta.status}`);
            }

            const datos = await respuesta.json();

            if (!Array.isArray(datos)) {
                throw new Error("El servidor no devolvió una lista de insumos.");
            }

            insumos = datos;
            mostrarInsumos(insumos);
            limpiarMensaje();

        } catch (error) {
            tabla.innerHTML = `
                <tr>
                    <td colspan="9">No se pudieron cargar los insumos.</td>
                </tr>
            `;

            mostrarMensaje(error.message, true);
            console.error("Error al cargar insumos:", error);
        }
    }

    function cargarDatosFormulario(insumo) {
        campos.forEach(campo => {
            const elemento = document.getElementById(campo);

            if (elemento) {
                elemento.value = insumo[campo] ?? "";
            }
        });

        document.getElementById("ins_vendible").checked = Boolean(insumo.ins_vendible);
        document.getElementById("ins_estado").checked = Boolean(insumo.ins_estado);

        document.getElementById("ins_id").readOnly = true;
        document.getElementById("tituloFormularioInsumo").textContent = "Actualizar insumo";
        document.getElementById("btnGuardarInsumo").textContent = "Actualizar";

        editando = true;
        abrirFormulario();
    }

    function obtenerDatosFormulario() {
        const precio = obtenerValor("ins_precio_compra");

        return {
            ins_id: Number(obtenerValor("ins_id")),
            sede_id: Number(obtenerValor("sede_id")),
            cins_id: Number(obtenerValor("cins_id")),
            pres_id: Number(obtenerValor("pres_id")),
            ins_nombre: obtenerValor("ins_nombre"),
            ins_codigo: obtenerValor("ins_codigo") || null,
            ins_codigo_barras: obtenerValor("ins_codigo_barras") || null,
            ins_precio_compra: precio === "" ? null : Number(precio),
            ins_stock: Number(obtenerValor("ins_stock")),
            ins_stock_min: Number(obtenerValor("ins_stock_min")),
            ins_vendible: document.getElementById("ins_vendible").checked,
            ins_imagen: obtenerValor("ins_imagen") || null,
            ins_estado: document.getElementById("ins_estado").checked
        };
    }

    function validarDatos(datos) {
        if (
            !Number.isInteger(datos.ins_id) || datos.ins_id <= 0 ||
            !Number.isInteger(datos.sede_id) || datos.sede_id <= 0 ||
            !Number.isInteger(datos.cins_id) || datos.cins_id <= 0 ||
            !Number.isInteger(datos.pres_id) || datos.pres_id <= 0
        ) {
            return "Los códigos del insumo, sede, categoría y presentación deben ser mayores que cero.";
        }

        if (!datos.ins_nombre) {
            return "El nombre del insumo es obligatorio.";
        }

        if (
            !Number.isFinite(datos.ins_stock) ||
            datos.ins_stock < 0 ||
            !Number.isFinite(datos.ins_stock_min) ||
            datos.ins_stock_min < 0
        ) {
            return "Las existencias y el stock mínimo deben ser valores válidos y no negativos.";
        }

        if (
            datos.ins_precio_compra !== null &&
            (!Number.isFinite(datos.ins_precio_compra) || datos.ins_precio_compra < 0)
        ) {
            return "El precio de compra debe ser válido y no negativo.";
        }

        return null;
    }

    async function guardarInsumo(evento) {
        evento.preventDefault();

        const datos = obtenerDatosFormulario();
        const errorValidacion = validarDatos(datos);

        if (errorValidacion) {
            mostrarMensaje(errorValidacion, true);
            return;
        }

        const url = editando
            ? `${API_INSUMOS}/${encodeURIComponent(datos.ins_id)}`
            : API_INSUMOS;

        const metodo = editando ? "PUT" : "POST";
        const boton = document.getElementById("btnGuardarInsumo");

        boton.disabled = true;

        try {
            const respuesta = await fetch(url, {
                method: metodo,
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(datos)
            });

            const resultado = await respuesta.text();

            if (!respuesta.ok) {
                throw new Error(resultado || `No se pudo guardar el insumo. Código: ${respuesta.status}`);
            }

            cerrarFormulario();
            await cargarInsumos();

            mostrarMensaje(resultado || "Operación realizada correctamente.");

        } catch (error) {
            mostrarMensaje(error.message, true);
            console.error("Error al guardar insumo:", error);

        } finally {
            boton.disabled = false;
        }
    }

    document.getElementById("btnNuevoInsumo").addEventListener("click", () => {
        limpiarFormulario();
        abrirFormulario();
    });

    document.getElementById("btnCancelarInsumo").addEventListener("click", cerrarFormulario);

    formulario.addEventListener("submit", guardarInsumo);

    tabla.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-editar-insumo]");

        if (!boton) return;

        const id = Number(boton.dataset.editarInsumo);
        const insumo = insumos.find(item => Number(item.ins_id) === id);

        if (insumo) {
            limpiarMensaje();
            cargarDatosFormulario(insumo);
        }
    });

    buscar.addEventListener("input", () => {
        const termino = buscar.value.toLowerCase().trim();

        const filtrados = insumos.filter(insumo =>
            String(insumo.ins_id).includes(termino) ||
            String(insumo.ins_nombre || "").toLowerCase().includes(termino) ||
            String(insumo.ins_codigo || "").toLowerCase().includes(termino) ||
            String(insumo.ins_codigo_barras || "").toLowerCase().includes(termino)
        );

        mostrarInsumos(filtrados);
    });

    document.getElementById("btnActualizarInsumos")?.addEventListener("click", cargarInsumos);

    cargarInsumos();
}
