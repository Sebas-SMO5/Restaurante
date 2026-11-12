
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
let vistaActual = "cliente";

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
    "inicio-sesion": {
        modulo: "COMPROBANTE DE EGRESO",
        titulo: "Inicio de sesión",
        descripcion: "Acceso de los usuarios al sistema."
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

    document.getElementById("platosActivos").textContent =
        platos.filter(plato => Number(plato.idEstado) === 1).length;
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
                <td colspan="5">No hay platos para mostrar.</td>
            </tr>
        `;
        actualizarEstadisticas();
        return;
    }

    tabla.innerHTML = resultado.map(plato => `
        <tr>
            <td>${escaparHTML(plato.idPlato)}</td>
            <td>${escaparHTML(plato.descripcion)}</td>
            <td>${Number(plato.precio).toLocaleString("es-CO", {
                style: "currency",
                currency: "COP",
                maximumFractionDigits: 2
            })}</td>
            <td>${escaparHTML(plato.idEstado)}</td>
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
            <td colspan="5">Cargando platos...</td>
        </tr>
    `;

    try {
        platos = await solicitar(API_URL) || [];
        mostrarPlatos();
        actualizarEstadisticas();
    } catch (error) {
        tabla.innerHTML = `
            <tr>
                <td colspan="5">No se pudieron cargar los platos.</td>
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
        const respuesta = await fetch("/views/tipo-identidad.html");

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
        const respuesta = await fetch("/views/materias-primas.html");

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
        const respuesta = await fetch("/views/concepto-pago.html");

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
        const respuesta = await fetch("/views/forma-pago.html");

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

    if (nombre === "concepto-pago") {
        cargarVistaConceptos();
        return;
    }

    if (nombre === "forma-pago") {
        cargarVistaFormaPago();
        return;
    }

    mostrarVistaPendiente(vista);
}

function abrirFormularioPlato(plato = null) {
    const formulario = document.getElementById("platoForm");

    formulario.reset();

    document.getElementById("idPlato").value = plato?.idPlato ?? "";
    document.getElementById("descripcion").value = plato?.descripcion ?? "";
    document.getElementById("precio").value = plato?.precio ?? "";
    document.getElementById("idEstado").value = plato?.idEstado ?? "";

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

    if (!descripcion || !Number.isFinite(precio) || precio <= 0 ||
        !Number.isInteger(idEstado) || idEstado <= 0) {
        mostrarMensaje("Verifica la descripción, el precio y el ID del estado.", "error");
        return;
    }

    const plato = {
        descripcion,
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

seleccionarVista("cliente");
