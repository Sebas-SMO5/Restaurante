
const API_URL = "/api/platos";

const tabla = document.getElementById("tablaPlatos");
const mensaje = document.getElementById("mensaje");
const buscar = document.getElementById("buscar");
const contenidoPlatos = document.getElementById("contenidoPlatos");
const vistaPendiente = document.getElementById("vistaPendiente");
const tituloVista = document.getElementById("tituloVista");
const descripcionVista = document.getElementById("descripcionVista");
const moduloEtiqueta = document.getElementById("moduloEtiqueta");
const btnNuevo = document.getElementById("btnNuevo");

let platos = [];

const vistas = {
    "cliente": {
        modulo: "RECIBO DE CAJA",
        titulo: "Gestión de clientes",
        descripcion: "Administra la información de los clientes.",
        nuevo: "Nuevo cliente"
    },
    "tipo-identidad": {
        modulo: "RECIBO DE CAJA",
        titulo: "Tipos de identidad",
        descripcion: "Consulta y administra los tipos de identidad.",
        nuevo: "Nuevo tipo de identidad"
    },
    "platos": {
        modulo: "PEDIDO",
        titulo: "Gestión de platos",
        descripcion: "Consulta y administra los platos del restaurante.",
        nuevo: "Nuevo plato"
    },
    "categorias": {
        modulo: "PEDIDO",
        titulo: "Gestión de categorías",
        descripcion: "Administra las categorías de los platos.",
        nuevo: "Nueva categoría"
    },
    "materias-primas": {
        modulo: "PEDIDO",
        titulo: "Gestión de materias primas",
        descripcion: "Administra las materias primas del restaurante.",
        nuevo: "Nueva materia prima"
    },
    "unidades-medida": {
        modulo: "PEDIDO",
        titulo: "Unidades de medida",
        descripcion: "Administra las unidades de medida de las materias primas.",
        nuevo: "Nueva unidad"
    },
    "concepto-pago": {
        modulo: "COMPROBANTE DE EGRESO",
        titulo: "Conceptos de pago",
        descripcion: "Administra los conceptos de los comprobantes de egreso.",
        nuevo: "Nuevo concepto"
    },
    "forma-pago": {
        modulo: "COMPROBANTE DE EGRESO",
        titulo: "Formas de pago",
        descripcion: "Administra las formas de pago registradas.",
        nuevo: "Nueva forma de pago"
    },
    "inicio-sesion": {
        modulo: "COMPROBANTE DE EGRESO",
        titulo: "Inicio de sesión",
        descripcion: "Acceso de los usuarios al sistema.",
        nuevo: ""
    },
    "usuario": {
        modulo: "PQRS",
        titulo: "Gestión de usuarios",
        descripcion: "Consulta y administra los usuarios del sistema.",
        nuevo: "Nuevo usuario"
    },
    "rol": {
        modulo: "PQRS",
        titulo: "Gestión de roles",
        descripcion: "Administra los roles de los usuarios.",
        nuevo: "Nuevo rol"
    },
    "tipo-pqrs": {
        modulo: "PQRS",
        titulo: "Tipos de PQRS",
        descripcion: "Administra los tipos de peticiones, quejas, reclamos y sugerencias.",
        nuevo: "Nuevo tipo de PQRS"
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
    return contenido ? JSON.parse(contenido) : null;
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
    if (!mensaje) {
        return;
    }

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
        platos = await solicitar(API_URL) ?? [];
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

function seleccionarVista(nombre) {
    const vista = vistas[nombre];

    if (!vista) {
        return;
    }

    tituloVista.textContent = vista.titulo;
    descripcionVista.textContent = vista.descripcion;
    moduloEtiqueta.textContent = `MÓDULO ${vista.modulo}`;

    const esPlatos = nombre === "platos";

    contenidoPlatos.classList.toggle("hidden", !esPlatos);
    vistaPendiente.classList.toggle("hidden", esPlatos);

    btnNuevo.classList.toggle("hidden", !esPlatos && !vista.nuevo);
    btnNuevo.textContent = vista.nuevo ? `+ ${vista.nuevo}` : "";

    document.querySelectorAll(".submenu-item").forEach(boton => {
        boton.classList.toggle("active", boton.dataset.vista === nombre);
    });

    if (!esPlatos) {
        document.getElementById("pendienteTitulo").textContent = vista.titulo;
        document.getElementById("pendienteDescripcion").textContent =
            "Este formulario se conectará con su tabla de la base de datos en el siguiente paso.";
    }
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

    if (!boton) {
        return;
    }

    const plato = platos.find(item =>
        Number(item.idPlato) === Number(boton.dataset.editar)
    );

    if (plato) {
        document.getElementById("idPlato").value = plato.idPlato;
        document.getElementById("descripcion").value = plato.descripcion;
        document.getElementById("precio").value = plato.precio;
        document.getElementById("idEstado").value = plato.idEstado;
        document.getElementById("formTitle").textContent = "Actualizar plato";
        document.getElementById("formSection").classList.remove("hidden");
        document.getElementById("formSection").scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
});

document.getElementById("btnNuevo").addEventListener("click", () => {
    if (!document.getElementById("contenidoPlatos").classList.contains("hidden")) {
        document.getElementById("platoForm").reset();
        document.getElementById("idPlato").value = "";
        document.getElementById("formTitle").textContent = "Registrar plato";
        document.getElementById("formSection").classList.remove("hidden");
        document.getElementById("formSection").scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
});

function cerrarFormulario() {
    document.getElementById("formSection").classList.add("hidden");
    document.getElementById("platoForm").reset();
}

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

    const plato = { descripcion, precio, idEstado };
    const botonGuardar = evento.submitter;
    botonGuardar.disabled = true;

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
        botonGuardar.disabled = false;
    }
});

seleccionarVista("platos");
cargarPlatos();
