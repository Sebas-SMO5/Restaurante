
const API_CLIENTES = "/api/clientes";
const API_TIPOS_IDENTIDAD_CLIENTE = "/api/tipos-identidad";

window.inicializarClientes = function () {
    const vista = document.getElementById("vistaClientes");

    if (!vista) return;

    const formulario = document.getElementById("clienteForm");
    const seccionFormulario = document.getElementById("clienteFormSection");
    const tituloFormulario = document.getElementById("clienteFormTitle");
    const tabla = document.getElementById("tablaClientes");
    const buscar = document.getElementById("buscarCliente");
    const mensajeListado = document.getElementById("mensajeClientes");
    const mensajeFormulario = document.getElementById("clienteMensaje");
    const botonGuardar = document.getElementById("btnGuardarCliente");
    const selectTipo = document.getElementById("cliTipo");

    const campos = {
        id: document.getElementById("clienteId"),
        documento: document.getElementById("cliDocumento"),
        estado: document.getElementById("cliEstado"),
        nombre: document.getElementById("cliNombre"),
        apellidos: document.getElementById("cliApellidos"),
        correo: document.getElementById("cliCorreo"),
        telefono: document.getElementById("cliTelefono"),
        direccion: document.getElementById("cliDireccion")
    };

    if (vista.dataset.inicializada === "true") {
        cargarTodo();
        return;
    }

    vista.dataset.inicializada = "true";

    let clientes = [];
    let tiposIdentidad = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (registro, campo) => String(registro?.[campo] ?? "").toLowerCase() === "activo";
    const clienteActivo = (c) => esActivo(c, "cli_estado");

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

    /** Solo se ofrecen tipos activos; si el cliente tiene uno inactivo, se conserva visible. */
    function llenarTipos(tipoActual = null) {
        const disponibles = tiposIdentidad.filter(t =>
            esActivo(t, "tipo_estado") || Number(t.tipo_id) === Number(tipoActual)
        );

        selectTipo.innerHTML = `<option value="">Seleccione un tipo</option>` +
            disponibles.map(t => `
                <option value="${escapar(t.tipo_id)}">
                    ${escapar(t.tipo_sigla)} - ${escapar(t.tipo_descripcion)}
                </option>
            `).join("");

        selectTipo.value = tipoActual ?? "";
    }

    function limpiarFormulario() {
        formulario.reset();
        campos.id.value = "";
        campos.estado.value = "Activo";
        llenarTipos();
        tituloFormulario.textContent = "Registrar cliente";
        botonGuardar.textContent = "Guardar cliente";
        ocultarMensaje(mensajeFormulario);
    }

    function mostrarFormulario(campoFoco) {
        seccionFormulario.classList.remove("hidden");
        seccionFormulario.scrollIntoView({ behavior: "smooth", block: "start" });
        campoFoco.focus({ preventScroll: true });
    }

    function ocultarFormulario() {
        seccionFormulario.classList.add("hidden");
        limpiarFormulario();
    }

    const documentoTexto = (c) => c.cli_documento
        ? `${c.tipo_sigla ?? ""} ${c.cli_documento}`.trim()
        : "";

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = clientes.filter(c =>
            [
                documentoTexto(c),
                `${c.cli_nombre ?? ""} ${c.cli_apellidos ?? ""}`,
                c.cli_correo,
                c.cli_telefono,
                c.cli_direccion,
                c.cli_estado
            ].some(valor => String(valor ?? "").toLowerCase().includes(texto))
        );

        if (!filtrados.length) {
            tabla.innerHTML = `<tr><td colspan="7">${clientes.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay clientes registrados."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtrados.map(c => `
            <tr>
                <td>
                    ${c.cli_documento
                        ? `<span class="badge badge-neutral">${escapar(c.tipo_sigla)}</span> ${escapar(c.cli_documento)}`
                        : `<span class="badge badge-muted">Sin documento</span>`}
                </td>
                <td>${escapar(c.cli_nombre)} ${escapar(c.cli_apellidos)}</td>
                <td>${escapar(c.cli_correo)}</td>
                <td>${escapar(c.cli_telefono)}</td>
                <td>${escapar(c.cli_direccion)}</td>
                <td>
                    <span class="badge ${clienteActivo(c) ? "badge-success" : "badge-muted"}">
                        ${escapar(c.cli_estado)}
                    </span>
                </td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button"
                            data-editar="${escapar(c.cli_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button"
                            data-estado="${escapar(c.cli_id)}">
                            ${clienteActivo(c) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarTipos() {
        try {
            const respuesta = await fetch(API_TIPOS_IDENTIDAD_CLIENTE);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar los tipos de documento.");
            }

            tiposIdentidad = await respuesta.json();
            tiposIdentidad.sort((a, b) => a.tipo_id - b.tipo_id);
        } catch (error) {
            console.error(error);
            tiposIdentidad = [];
        }

        llenarTipos();
    }

    async function cargarClientes() {
        try {
            const respuesta = await fetch(API_CLIENTES);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar los clientes.");
            }

            clientes = await respuesta.json();
            clientes.sort((a, b) =>
                `${a.cli_nombre} ${a.cli_apellidos}`.localeCompare(`${b.cli_nombre} ${b.cli_apellidos}`, "es")
            );

            document.getElementById("totalClientes").textContent = clientes.length;
            document.getElementById("clientesActivos").textContent = clientes.filter(clienteActivo).length;
            document.getElementById("clientesSinDocumento").textContent =
                clientes.filter(c => !c.cli_documento).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="7">Error al cargar los clientes.</td></tr>`;
        }
    }

    async function cargarTodo() {
        await cargarTipos();
        await cargarClientes();
    }

    async function guardar(id, datos) {
        const respuesta = await fetch(id ? `${API_CLIENTES}/${id}` : API_CLIENTES, {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado || "No se pudo guardar el cliente.");
        }

        return resultado;
    }

    function datosFormulario() {
        return {
            tipo_id: selectTipo.value ? Number(selectTipo.value) : null,
            cli_documento: campos.documento.value.trim().toUpperCase(),
            cli_nombre: campos.nombre.value.trim(),
            cli_apellidos: campos.apellidos.value.trim(),
            cli_correo: campos.correo.value.trim(),
            cli_telefono: campos.telefono.value.trim(),
            cli_direccion: campos.direccion.value.trim(),
            cli_estado: campos.estado.value
        };
    }

    function validar(datos, id) {
        if (!datos.tipo_id || !datos.cli_documento) {
            return "Selecciona el tipo de documento y escribe el número.";
        }

        if (!/^[A-Za-z0-9-]{4,20}$/.test(datos.cli_documento)) {
            return "El número de documento debe tener entre 4 y 20 caracteres (letras, números o guion).";
        }

        const repetido = clientes.some(c =>
            String(c.cli_id) !== String(id ?? "") &&
            Number(c.tipo_id) === datos.tipo_id &&
            String(c.cli_documento ?? "").toUpperCase() === datos.cli_documento
        );

        if (repetido) {
            return "Ya existe un cliente con ese tipo y número de documento.";
        }

        if (!datos.cli_nombre || !datos.cli_apellidos || !datos.cli_correo) {
            return "El nombre, los apellidos y el correo son obligatorios.";
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(datos.cli_correo)) {
            return "Ingresa un correo válido.";
        }

        if (datos.cli_telefono && !/^[0-9+\-\s()]{7,40}$/.test(datos.cli_telefono)) {
            return "Ingresa un teléfono válido (solo números, espacios, +, - o paréntesis).";
        }

        return null;
    }

    document.getElementById("btnNuevoCliente").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario(selectTipo);
    });

    document.getElementById("btnCerrarCliente").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarCliente").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarClientes").addEventListener("click", cargarTodo);

    buscar.addEventListener("input", pintarTabla);

    campos.documento.addEventListener("input", () => {
        campos.documento.value = campos.documento.value.toUpperCase();
    });

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campos.id.value || null;
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
            await cargarClientes();
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
        const cliente = clientes.find(c => String(c.cli_id) === String(id));

        if (!cliente) return;

        if (botonEditar) {
            limpiarFormulario();

            campos.id.value = cliente.cli_id;
            llenarTipos(cliente.tipo_id);
            campos.documento.value = cliente.cli_documento ?? "";
            campos.estado.value = clienteActivo(cliente) ? "Activo" : "Inactivo";
            campos.nombre.value = cliente.cli_nombre ?? "";
            campos.apellidos.value = cliente.cli_apellidos ?? "";
            campos.correo.value = cliente.cli_correo ?? "";
            campos.telefono.value = cliente.cli_telefono ?? "";
            campos.direccion.value = cliente.cli_direccion ?? "";

            tituloFormulario.textContent =
                `Editar cliente: ${cliente.cli_nombre ?? ""} ${cliente.cli_apellidos ?? ""}`.trim();
            botonGuardar.textContent = "Actualizar cliente";

            if (!cliente.cli_documento) {
                mostrarMensaje(mensajeFormulario,
                    "Este cliente no tiene documento registrado. Complétalo para poder guardar.", "error");
            }

            mostrarFormulario(cliente.cli_documento ? campos.nombre : selectTipo);
            return;
        }

        if (!cliente.cli_documento) {
            avisarListado("Primero edita el cliente y completa su documento.", "error");
            return;
        }

        try {
            const nuevoEstado = clienteActivo(cliente) ? "Inactivo" : "Activo";

            await guardar(cliente.cli_id, { ...cliente, cli_estado: nuevoEstado });
            await cargarClientes();
            avisarListado(`${cliente.cli_nombre} ${cliente.cli_apellidos} quedó ${nuevoEstado.toLowerCase()}.`);

        } catch (error) {
            avisarListado(error.message, "error");
        }
    });

    limpiarFormulario();
    cargarTodo();
};
