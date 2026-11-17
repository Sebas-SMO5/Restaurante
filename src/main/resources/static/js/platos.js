
const API_URL = "/api/platos";

const tabla = document.getElementById("tablaPlatos");
const mensaje = document.getElementById("mensaje");
const buscar = document.getElementById("buscar");
const contenidoPlatos = document.getElementById("contenidoPlatos");
const contenedorVistaExterna = document.getElementById("contenedorVistaExterna");
const tituloVista = document.getElementById("tituloVista");
const descripcionVista = document.getElementById("descripcionVista");
const moduloEtiqueta = document.getElementById("moduloEtiqueta");
const btnNuevo = document.getElementById("btnNuevo");

let platos = [];
let categoriasPlato = [];
let vistaActual = "inicio";

const API_CATEGORIAS_PLATO = "/api/categorias";

/** Llena la lista de categorías; solo activas, salvo la que ya tiene el plato editado. */
function llenarCategoriasPlato(idActual = null) {
    const select = document.getElementById("idCategoriaPlato");

    const disponibles = categoriasPlato.filter(c =>
        c.estado || Number(c.idCategoria) === Number(idActual)
    );

    select.innerHTML = `<option value="">Seleccione una categoría</option>` +
        disponibles.map(c => `
            <option value="${escaparHTML(c.idCategoria)}">
                ${escaparHTML(c.nombre)}${c.estado ? "" : " (inactiva)"}
            </option>
        `).join("");

    select.value = idActual ?? "";
}

async function cargarCategoriasPlato() {
    try {
        categoriasPlato = await solicitar(API_CATEGORIAS_PLATO) || [];
        categoriasPlato.sort((a, b) => String(a.nombre).localeCompare(String(b.nombre), "es"));
    } catch (error) {
        console.error(error);
        categoriasPlato = [];
    }
}

/* Estados de plato: los del tipo de estado llamado "Plato" (Configuración > Estados). */
let estadosPlato = [];

async function cargarEstadosPlato() {
    try {
        const estados = await solicitar("/api/estados") || [];
        estadosPlato = estados.filter(e => String(e.tes_descripcion ?? "").toLowerCase() === "plato");
    } catch (error) {
        console.error(error);
        estadosPlato = [];
    }
}

/** Llena la lista de estados; solo activos de tipo Plato. Si el actual no es válido, se pide elegir otro. */
function llenarEstadosPlato(idActual = null) {
    const select = document.getElementById("idEstado");
    const activos = estadosPlato.filter(e => String(e.est_estado).toLowerCase() === "activo");
    const actualValido = activos.some(e => Number(e.est_id) === Number(idActual));

    select.innerHTML = `<option value="">Seleccione un estado</option>` +
        activos.map(e => `
            <option value="${escaparHTML(e.est_id)}">${escaparHTML(e.est_descripcion)}</option>
        `).join("");

    select.value = actualValido ? idActual : "";
}

const platoDisponible = (plato) =>
    plato.estadoValido && String(plato.nombreEstado ?? "").toLowerCase() === "disponible";

const platoPorCorregir = (plato) => !plato.estadoValido || !plato.idCategoria;

const vistas = {
    "cliente": {
        modulo: "RECIBO DE CAJA",
        titulo: "Gestión de clientes",
        descripcion: "Administra la información de los clientes."
    },
    "tipo-identidad": {
        modulo: "RECIBO DE CAJA",
        titulo: "Tipos de identidad",
        descripcion: "Consulta y administra los tipos de identidad."
    },
    "platos": {
        modulo: "PEDIDO",
        titulo: "Gestión de platos",
        descripcion: "Consulta y administra los platos del restaurante."
    },
    "categorias": {
        modulo: "PEDIDO",
        titulo: "Gestión de categorías",
        descripcion: "Administra las categorías de los platos."
    },
    "materias-primas": {
        modulo: "PEDIDO",
        titulo: "Gestión de materias primas",
        descripcion: "Administra las materias primas del restaurante."
    },
    "inicio": {
        modulo: "INICIO",
        titulo: "Resumen del restaurante",
        descripcion: "Lo más importante del día de un vistazo."
    },
    "cierre": {
        modulo: "RECIBO DE CAJA",
        titulo: "Cierre de caja",
        descripcion: "Cuadra la caja del día: ingresos, egresos y efectivo contado."
    },
    "pqrs": {
        modulo: "PQRS",
        titulo: "Bandeja de PQRS",
        descripcion: "Peticiones, quejas, reclamos y sugerencias de los clientes."
    },
    "recibos": {
        modulo: "RECIBO DE CAJA",
        titulo: "Recibos de caja",
        descripcion: "Cobra los pedidos finalizados y consulta los recibos generados."
    },
    "egresos": {
        modulo: "COMPROBANTE DE EGRESO",
        titulo: "Comprobantes de egreso",
        descripcion: "Registra las salidas de dinero: proveedores, servicios, nómina..."
    },
    "pedidos": {
        modulo: "PEDIDO",
        titulo: "Pedidos",
        descripcion: "Toma los pedidos por mesa, envíalos a cocina y finalízalos para cobrar."
    },
    "cocina": {
        modulo: "PEDIDO",
        titulo: "Cocina / Comandas",
        descripcion: "Platos pendientes por preparar, del pedido más antiguo al más reciente."
    },
    "estados": {
        modulo: "CONFIGURACIÓN",
        titulo: "Estados",
        descripcion: "Administra los estados de pedidos, platos, comandas y PQRS."
    },
    "categorias-insumo": {
        modulo: "PEDIDO",
        titulo: "Categorías de insumo",
        descripcion: "Clasifica las materias primas (granos, lácteos, carnes...)."
    },
    "unidades-medida": {
        modulo: "PEDIDO",
        titulo: "Unidades de medida",
        descripcion: "Administra las unidades de medida de las materias primas."
    },
    "concepto-pago": {
        modulo: "COMPROBANTE DE EGRESO",
        titulo: "Conceptos de pago",
        descripcion: "Administra los conceptos de los comprobantes de egreso."
    },
    "forma-pago": {
        modulo: "COMPROBANTE DE EGRESO",
        titulo: "Formas de pago",
        descripcion: "Administra las formas de pago registradas."
    },
    "usuario": {
        modulo: "PQRS",
        titulo: "Gestión de usuarios",
        descripcion: "Consulta y administra los usuarios del sistema."
    },
    "rol": {
        modulo: "PQRS",
        titulo: "Gestión de roles",
        descripcion: "Administra los roles de los usuarios."
    },
    "tipo-pqrs": {
        modulo: "PQRS",
        titulo: "Tipos de PQRS",
        descripcion: "Administra los tipos de peticiones, quejas, reclamos y sugerencias."
    }
};

async function solicitar(url, opciones = {}) {
    const respuesta = await fetch(url, {
        ...opciones,
        headers: {
            "Content-Type": "application/json",
            ...opciones.headers
        }
    });

    if (!respuesta.ok) {
        const detalle = await respuesta.text();
        throw new Error(detalle || `Error HTTP ${respuesta.status}`);
    }

    if (respuesta.status === 204) {
        return null;
    }

    const contenido = await respuesta.text();

    if (!contenido) {
        return null;
    }

    try {
        return JSON.parse(contenido);
    } catch {
        return contenido;
    }
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

function mostrarMensaje(texto, tipo = "success") {
    if (!mensaje) return;

    mensaje.textContent = texto;
    mensaje.className = `message ${tipo}`;
}

function actualizarEstadisticas() {
    document.getElementById("totalPlatos").textContent = platos.length;

    document.getElementById("platosActivos").textContent = platos.filter(platoDisponible).length;
    document.getElementById("platosPorCorregir").textContent = platos.filter(platoPorCorregir).length;
}

function mostrarPlatos() {
    const filtro = buscar.value.trim().toLowerCase();

    const resultado = platos.filter(plato =>
        String(plato.idPlato).includes(filtro) ||
        String(plato.descripcion ?? "").toLowerCase().includes(filtro)
    );

    if (resultado.length === 0) {
        tabla.innerHTML = `
            <tr>
                <td colspan="6">No hay platos para mostrar.</td>
            </tr>
        `;
        actualizarEstadisticas();
        return;
    }

    tabla.innerHTML = resultado.map(plato => `
        <tr>
            <td>${escaparHTML(plato.idPlato)}</td>
            <td>${escaparHTML(plato.descripcion)}</td>
            <td>${plato.nombreCategoria
                ? escaparHTML(plato.nombreCategoria)
                : `<span class="badge badge-muted">Sin categoría</span>`}</td>
            <td>${Number(plato.precio).toLocaleString("es-CO", {
                style: "currency",
                currency: "COP",
                maximumFractionDigits: 2
            })}</td>
            <td>${plato.estadoValido
                ? `<span class="badge ${platoDisponible(plato) ? "badge-success" : "badge-muted"}">${escaparHTML(plato.nombreEstado)}</span>`
                : `<span class="badge badge-warning" title="El estado '${escaparHTML(plato.nombreEstado ?? plato.idEstado)}' no es un estado de plato. Edita el plato para corregirlo.">Estado no válido</span>`}</td>
            <td>
                <button class="action-button" type="button"
                    data-editar="${escaparHTML(plato.idPlato)}">
                    Editar
                </button>
            </td>
        </tr>
    `).join("");

    actualizarEstadisticas();
}

async function cargarPlatos() {
    tabla.innerHTML = `
        <tr>
            <td colspan="6">Cargando platos...</td>
        </tr>
    `;

    try {
        const [listaPlatos] = await Promise.all([solicitar(API_URL), cargarCategoriasPlato(), cargarEstadosPlato()]);
        platos = listaPlatos || [];
        mostrarPlatos();
        actualizarEstadisticas();
    } catch (error) {
        tabla.innerHTML = `
            <tr>
                <td colspan="6">No se pudieron cargar los platos.</td>
            </tr>
        `;

        mostrarMensaje(error.message, "error");
        console.error(error);
    }
}

async function cargarVistaClientes() {
    try {
        const respuesta = await fetch("/views/clientes.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de clientes.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarClientes !== "function") {
            throw new Error("No se encontró clientes.js o su función de inicialización.");
        }

        window.inicializarClientes();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaTipoIdentidad() {
    try {
        const respuesta = await fetch("/views/tipoidentidad.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de tipos de identidad.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarTipoIdentidad !== "function") {
            throw new Error("No se encontró tipo-identidad.js o su función de inicialización.");
        }

        window.inicializarTipoIdentidad();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaCategorias() {
    try {
        const respuesta = await fetch("/views/categorias.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de categorías.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarCategorias !== "function") {
            throw new Error("No se encontró categorias.js o su función de inicialización.");
        }

        window.inicializarCategorias();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaMateriasPrimas() {
    try {
        const respuesta = await fetch("/views/insumos.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de materias primas.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarMateriasPrimas !== "function") {
            throw new Error("No se encontró materias-primas.js o su función de inicialización.");
        }

        window.inicializarMateriasPrimas();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaConceptos() {
    try {
        const respuesta = await fetch("/views/concepto.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de conceptos de pago.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarConceptos !== "function") {
            throw new Error("No se encontró concepto-pago.js o su función de inicialización.");
        }

        window.inicializarConceptos();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaFormaPago() {
    try {
        const respuesta = await fetch("/views/formapago.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de formas de pago.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarFormasPago !== "function") {
            throw new Error("No se encontró forma-pago.js o su función de inicialización.");
        }

        window.inicializarFormasPago();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaUnidadesMedida() {
    try {
        const respuesta = await fetch("/views/unidadesmedida.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de unidades de medida.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarUnidadesMedida !== "function") {
            throw new Error("No se encontró unidadesmedida.js o su función de inicialización.");
        }

        window.inicializarUnidadesMedida();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

/** Carga una vista HTML externa y ejecuta su función de inicialización. */
async function cargarVistaExterna(archivo, funcion, nombre) {
    try {
        const respuesta = await fetch(`/views/${archivo}`);

        if (!respuesta.ok) {
            throw new Error(`No fue posible cargar la vista de ${nombre}.`);
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window[funcion] !== "function") {
            throw new Error(`No se encontró la función ${funcion} de la vista de ${nombre}.`);
        }

        window[funcion]();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaEstados() {
    try {
        const respuesta = await fetch("/views/estados.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de estados.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarEstados !== "function") {
            throw new Error("No se encontró estados.js o su función de inicialización.");
        }

        window.inicializarEstados();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaCategoriasInsumo() {
    try {
        const respuesta = await fetch("/views/categoriasinsumo.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de categorías de insumo.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarCategoriasInsumo !== "function") {
            throw new Error("No se encontró categoriasinsumo.js o su función de inicialización.");
        }

        window.inicializarCategoriasInsumo();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaRoles() {
    try {
        const respuesta = await fetch("/views/roles.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de roles.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarRoles !== "function") {
            throw new Error("No se encontró roles.js o su función de inicialización.");
        }

        window.inicializarRoles();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaTiposPqrs() {
    try {
        const respuesta = await fetch("/views/tipopqrs.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de tipos de PQRS.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarTiposPqrs !== "function") {
            throw new Error("No se encontró tipopqrs.js o su función de inicialización.");
        }

        window.inicializarTiposPqrs();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

async function cargarVistaUsuarios() {
    try {
        const respuesta = await fetch("/views/usuarios.html");

        if (!respuesta.ok) {
            throw new Error("No fue posible cargar la vista de usuarios.");
        }

        contenedorVistaExterna.innerHTML = await respuesta.text();

        if (typeof window.inicializarUsuarios !== "function") {
            throw new Error("No se encontró usuarios.js o su función de inicialización.");
        }

        window.inicializarUsuarios();

    } catch (error) {
        mostrarErrorVista(error);
    }
}

function mostrarErrorVista(error) {
    contenedorVistaExterna.innerHTML = `
        <div class="message error">${escaparHTML(error.message)}</div>
    `;

    console.error(error);
}

function mostrarVistaPendiente(vista) {
    contenedorVistaExterna.innerHTML = `
        <section class="panel">
            <div class="panel-heading">
                <div>
                    <h3>${escaparHTML(vista.titulo)}</h3>
                    <p>${escaparHTML(vista.descripcion)}</p>
                </div>
            </div>
            <div class="message">
                Esta vista está pendiente de desarrollo.
            </div>
        </section>
    `;
}

function seleccionarVista(nombre) {
    const vista = vistas[nombre];

    if (!vista) return;

    vistaActual = nombre;

    tituloVista.textContent = vista.titulo;
    descripcionVista.textContent = vista.descripcion;
    moduloEtiqueta.textContent = `MÓDULO ${vista.modulo}`;

    const esPlatos = nombre === "platos";

    contenidoPlatos.classList.toggle("hidden", !esPlatos);
    btnNuevo.classList.toggle("hidden", !esPlatos);

    contenedorVistaExterna.innerHTML = "";

    document.querySelectorAll(".submenu-item").forEach(boton => {
        boton.classList.toggle("active", boton.dataset.vista === nombre);
    });

    if (esPlatos) {
        btnNuevo.textContent = "+ Nuevo plato";
        cargarPlatos();
        return;
    }

    if (nombre === "cliente") {
        cargarVistaClientes();
        return;
    }

    if (nombre === "tipo-identidad") {
        cargarVistaTipoIdentidad();
        return;
    }

    if (nombre === "categorias") {
        cargarVistaCategorias();
        return;
    }

    if (nombre === "materias-primas") {
        cargarVistaMateriasPrimas();
        return;
    }

    if (nombre === "inicio") {
        cargarVistaExterna("inicio.html", "inicializarInicio", "inicio");
        return;
    }

    if (nombre === "cierre") {
        cargarVistaExterna("cierre.html", "inicializarCierre", "cierre de caja");
        return;
    }

    if (nombre === "pqrs") {
        cargarVistaExterna("pqrs.html", "inicializarPqrs", "PQRS");
        return;
    }

    if (nombre === "recibos") {
        cargarVistaExterna("recibos.html", "inicializarRecibos", "recibos de caja");
        return;
    }

    if (nombre === "egresos") {
        cargarVistaExterna("egresos.html", "inicializarEgresos", "comprobantes de egreso");
        return;
    }

    if (nombre === "pedidos") {
        cargarVistaExterna("pedidos.html", "inicializarPedidos", "pedidos");
        return;
    }

    if (nombre === "cocina") {
        cargarVistaExterna("cocina.html", "inicializarCocina", "cocina");
        return;
    }

    if (nombre === "estados") {
        cargarVistaEstados();
        return;
    }

    if (nombre === "categorias-insumo") {
        cargarVistaCategoriasInsumo();
        return;
    }

    if (nombre === "unidades-medida") {
        cargarVistaUnidadesMedida();
        return;
    }

    if (nombre === "concepto-pago") {
        cargarVistaConceptos();
        return;
    }

    if (nombre === "forma-pago") {
        cargarVistaFormaPago();
        return;
    }

    if (nombre === "usuario") {
        cargarVistaUsuarios();
        return;
    }

    if (nombre === "rol") {
        cargarVistaRoles();
        return;
    }

    if (nombre === "tipo-pqrs") {
        cargarVistaTiposPqrs();
        return;
    }

    mostrarVistaPendiente(vista);
}

function abrirFormularioPlato(plato = null) {
    const formulario = document.getElementById("platoForm");

    formulario.reset();

    document.getElementById("idPlato").value = plato?.idPlato ?? "";
    document.getElementById("descripcion").value = plato?.descripcion ?? "";
    llenarCategoriasPlato(plato?.idCategoria ?? null);
    document.getElementById("precio").value = plato?.precio ?? "";
    llenarEstadosPlato(plato?.idEstado ?? null);

    if (plato && !plato.estadoValido) {
        mostrarMensaje(`"${plato.descripcion}" tiene un estado que no es de plato. Selecciona uno nuevo antes de guardar.`, "error");
    }

    document.getElementById("formTitle").textContent =
        plato ? "Actualizar plato" : "Registrar plato";

    document.getElementById("formSection").classList.remove("hidden");

    document.getElementById("formSection").scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

function cerrarFormulario() {
    document.getElementById("formSection").classList.add("hidden");
    document.getElementById("platoForm").reset();
}

document.querySelectorAll(".submenu-item").forEach(boton => {
    boton.addEventListener("click", () => {
        seleccionarVista(boton.dataset.vista);
    });
});

buscar.addEventListener("input", mostrarPlatos);

document.getElementById("btnActualizar").addEventListener("click", cargarPlatos);

tabla.addEventListener("click", evento => {
    const boton = evento.target.closest("[data-editar]");

    if (!boton) return;

    const plato = platos.find(item =>
        Number(item.idPlato) === Number(boton.dataset.editar)
    );

    if (plato) {
        abrirFormularioPlato(plato);
    }
});

btnNuevo.addEventListener("click", () => {
    if (vistaActual === "platos") {
        abrirFormularioPlato();
    }
});

document.getElementById("btnCerrar").addEventListener("click", cerrarFormulario);
document.getElementById("btnCancelar").addEventListener("click", cerrarFormulario);

document.getElementById("platoForm").addEventListener("submit", async evento => {
    evento.preventDefault();

    const id = document.getElementById("idPlato").value;
    const descripcion = document.getElementById("descripcion").value.trim();
    const precio = Number(document.getElementById("precio").value);
    const idEstado = Number(document.getElementById("idEstado").value);
    const idCategoria = Number(document.getElementById("idCategoriaPlato").value);

    if (!descripcion || !Number.isFinite(precio) || precio <= 0 ||
        !Number.isInteger(idEstado) || idEstado <= 0) {
        mostrarMensaje("Verifica la descripción, el precio y el estado del plato.", "error");
        return;
    }

    if (!idCategoria) {
        mostrarMensaje("Selecciona la categoría del plato.", "error");
        return;
    }

    const plato = {
        descripcion,
        idCategoria,
        precio,
        idEstado
    };

    const botonGuardar = evento.submitter;

    if (botonGuardar) {
        botonGuardar.disabled = true;
    }

    try {
        if (id) {
            await solicitar(`${API_URL}/${encodeURIComponent(id)}`, {
                method: "PUT",
                body: JSON.stringify({
                    ...plato,
                    idPlato: Number(id)
                })
            });

            mostrarMensaje("Plato actualizado correctamente.");
        } else {
            await solicitar(API_URL, {
                method: "POST",
                body: JSON.stringify(plato)
            });

            mostrarMensaje("Plato registrado correctamente.");
        }

        cerrarFormulario();
        await cargarPlatos();

    } catch (error) {
        mostrarMensaje(error.message || "No fue posible guardar el plato.", "error");
    } finally {
        if (botonGuardar) {
            botonGuardar.disabled = false;
        }
    }
});

seleccionarVista("inicio");
