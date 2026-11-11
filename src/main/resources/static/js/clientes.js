const API_CLIENTES = "/api/clientes";

function inicializarClientes() {
    const vista = document.getElementById("vistaClientes");

    if (!vista || vista.dataset.inicializada === "true") return;

    vista.dataset.inicializada = "true";

    const formulario = vista.querySelector("#clienteForm");
    const tabla = vista.querySelector("#tablaClientes");
    const seccionFormulario = vista.querySelector("#clienteFormSection");
    const buscar = vista.querySelector("#buscarCliente");
    const mensaje = vista.querySelector("#mensajeClientes");
    const mensajeFormulario = vista.querySelector("#clienteMensaje");

    let clientes = [];

    function escaparHTML(valor) {
        return String(valor ?? "").replace(/[&<>"']/g, caracter => ({
            "&": "&amp;",
            "<": "&lt;",
            ">": "&gt;",
            '"': "&quot;",
            "'": "&#39;"
        })[caracter]);
    }

    function mostrarMensaje(elemento, texto, error = false) {
        elemento.textContent = texto;
        elemento.classList.remove("hidden");
        elemento.classList.toggle("error", error);
    }

    function ocultarMensaje(elemento) {
        elemento.textContent = "";
        elemento.classList.add("hidden");
        elemento.classList.remove("error");
    }

    async function solicitar(url, opciones = {}) {
        const respuesta = await fetch(url, {
            ...opciones,
            headers: {
                "Content-Type": "application/json",
                ...opciones.headers
            }
        });

        const texto = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(texto || "No se pudo completar la solicitud.");
        }

        if (!texto) return null;

        try {
            return JSON.parse(texto);
        } catch {
            return texto;
        }
    }

    function actualizarEstadisticas() {
        vista.querySelector("#totalClientes").textContent = clientes.length;

        const activos = clientes.filter(cliente =>
            String(cliente.cli_estado).toLowerCase() === "activo"
        ).length;

        vista.querySelector("#clientesActivos").textContent = activos;
    }

    function mostrarClientes() {
        const texto = buscar.value.toLowerCase().trim();

        const filtrados = clientes.filter(cliente =>
            [
                cliente.cli_nombre,
                cliente.cli_apellidos,
                cliente.cli_correo,
                cliente.cli_telefono,
                cliente.cli_direccion
            ].some(valor => String(valor ?? "").toLowerCase().includes(texto))
        );

        if (filtrados.length === 0) {
            tabla.innerHTML = `
                <tr>
                    <td colspan="8">No se encontraron clientes.</td>
                </tr>
            `;
            return;
        }

        tabla.innerHTML = filtrados.map(cliente => `
            <tr>
                <td>${escaparHTML(cliente.cli_id)}</td>
                <td>${escaparHTML(cliente.cli_nombre)}</td>
                <td>${escaparHTML(cliente.cli_apellidos)}</td>
                <td>${escaparHTML(cliente.cli_direccion)}</td>
                <td>${escaparHTML(cliente.cli_telefono)}</td>
                <td>${escaparHTML(cliente.cli_correo)}</td>
                <td>${escaparHTML(cliente.cli_estado)}</td>
                <td>
                    <button type="button" class="button"
                        data-editar="${escaparHTML(cliente.cli_id)}">
                        Editar
                    </button>
                </td>
            </tr>
        `).join("");
    }

    async function cargarClientes() {
        try {
            ocultarMensaje(mensaje);

            clientes = await solicitar(API_CLIENTES) || [];

            actualizarEstadisticas();
            mostrarClientes();
        } catch (error) {
            mostrarMensaje(mensaje, error.message, true);

            tabla.innerHTML = `
                <tr>
                    <td colspan="8">No fue posible cargar los clientes.</td>
                </tr>
            `;

            console.error(error);
        }
    }

    function abrirFormulario(cliente = null) {
        formulario.reset();
        ocultarMensaje(mensajeFormulario);

        vista.querySelector("#clienteId").value = cliente?.cli_id ?? "";
        vista.querySelector("#cliNombre").value = cliente?.cli_nombre ?? "";
        vista.querySelector("#cliApellidos").value = cliente?.cli_apellidos ?? "";
        vista.querySelector("#cliDireccion").value = cliente?.cli_direccion ?? "";
        vista.querySelector("#cliTelefono").value = cliente?.cli_telefono ?? "";
        vista.querySelector("#cliCorreo").value = cliente?.cli_correo ?? "";
        vista.querySelector("#cliEstado").value = cliente?.cli_estado ?? "";

        vista.querySelector("#clienteFormTitle").textContent =
            cliente ? "Editar cliente" : "Registrar cliente";

        vista.querySelector("#btnGuardarCliente").textContent =
            cliente ? "Actualizar cliente" : "Guardar cliente";

        seccionFormulario.classList.remove("hidden");

        seccionFormulario.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }

    function cerrarFormulario() {
        formulario.reset();
        seccionFormulario.classList.add("hidden");
        ocultarMensaje(mensajeFormulario);
    }

    async function guardarCliente(evento) {
        evento.preventDefault();

        const id = vista.querySelector("#clienteId").value;

        const cliente = {
            cli_nombre: vista.querySelector("#cliNombre").value.trim(),
            cli_apellidos: vista.querySelector("#cliApellidos").value.trim(),
            cli_direccion: vista.querySelector("#cliDireccion").value.trim(),
            cli_telefono: vista.querySelector("#cliTelefono").value.trim(),
            cli_correo: vista.querySelector("#cliCorreo").value.trim(),
            cli_estado: vista.querySelector("#cliEstado").value.trim()
        };

        const botonGuardar = vista.querySelector("#btnGuardarCliente");
        botonGuardar.disabled = true;

        try {
            await solicitar(
                id ? `${API_CLIENTES}/${encodeURIComponent(id)}` : API_CLIENTES,
                {
                    method: id ? "PUT" : "POST",
                    body: JSON.stringify(cliente)
                }
            );

            cerrarFormulario();
            await cargarClientes();

            mostrarMensaje(
                mensaje,
                id
                    ? "Cliente actualizado correctamente."
                    : "Cliente registrado correctamente."
            );
        } catch (error) {
            mostrarMensaje(mensajeFormulario, error.message, true);
        } finally {
            botonGuardar.disabled = false;
        }
    }

   vista.querySelector("#btnNuevoCliente").addEventListener("click", () => {
    abrirFormulario();
});

vista.querySelector("#btnActualizarClientes").addEventListener("click", cargarClientes);

    vista.querySelector("#btnCancelarCliente").addEventListener("click", cerrarFormulario);

    formulario.addEventListener("submit", guardarCliente);

    buscar.addEventListener("input", mostrarClientes);

    tabla.addEventListener("click", evento => {
        const boton = evento.target.closest("[data-editar]");

        if (!boton) return;

        const cliente = clientes.find(item =>
            Number(item.cli_id) === Number(boton.dataset.editar)
        );

        if (cliente) {
            abrirFormulario(cliente);
        }
    });

    cargarClientes();
}