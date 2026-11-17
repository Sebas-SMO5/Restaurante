
const API_ROLES = "/api/perfiles";

window.inicializarRoles = function () {
    const vista = document.getElementById("vistaRoles");

    if (!vista) return;

    const formulario = document.getElementById("rolForm");
    const seccionFormulario = document.getElementById("rolFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioRol");
    const tabla = document.getElementById("tablaRoles");
    const mensajeFormulario = document.getElementById("rolMensaje");
    const mensajeListado = document.getElementById("rolesListadoMensaje");
    const buscar = document.getElementById("buscarRol");
    const campoId = document.getElementById("rolId");
    const campoDescripcion = document.getElementById("rolDescripcion");
    const campoEstado = document.getElementById("rolEstado");

    if (vista.dataset.inicializada === "true") {
        cargarRoles();
        return;
    }

    vista.dataset.inicializada = "true";

    let roles = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (rol) => rol.perf_estado?.toLowerCase() === "activo";

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
        tituloFormulario.textContent = "Registrar rol";
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

    const consultarRol = async (id) => {
        const respuesta = await fetch(`${API_ROLES}/${id}`);

        if (!respuesta.ok) {
            throw new Error("No se pudo consultar el rol.");
        }

        return respuesta.json();
    };

    const guardar = async (id, datos) => {
        const respuesta = await fetch(id ? `${API_ROLES}/${id}` : API_ROLES, {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(
                (resultado || "No se pudo guardar el rol.")
                    .replace(/Perfil/g, "Rol").replace(/perfil/g, "rol")
            );
        }

        // El backend responde con la palabra "perfil"; en esta vista se llama "rol".
        return resultado.replace(/Perfil/g, "Rol").replace(/perfil/g, "rol");
    };

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = roles.filter(rol =>
            String(rol.perf_id).includes(texto) ||
            (rol.perf_descripcion ?? "").toLowerCase().includes(texto) ||
            (rol.perf_estado ?? "").toLowerCase().includes(texto)
        );

        if (!filtrados.length) {
            tabla.innerHTML = `<tr><td colspan="4">${roles.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay roles registrados."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtrados.map(rol => `
            <tr>
                <td>${escapar(rol.perf_id)}</td>
                <td>${escapar(rol.perf_descripcion)}</td>
                <td>
                    <span class="badge ${esActivo(rol) ? "badge-success" : "badge-muted"}">
                        ${escapar(rol.perf_estado)}
                    </span>
                </td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button"
                            data-editar="${escapar(rol.perf_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button"
                            data-estado="${escapar(rol.perf_id)}">
                            ${esActivo(rol) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarRoles() {
        try {
            const respuesta = await fetch(API_ROLES);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar los roles.");
            }

            roles = await respuesta.json();
            roles.sort((a, b) => a.perf_id - b.perf_id);

            document.getElementById("totalRoles").textContent = roles.length;
            document.getElementById("rolesActivos").textContent =
                roles.filter(esActivo).length;
            document.getElementById("rolesInactivos").textContent =
                roles.filter(rol => !esActivo(rol)).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="4">Error al cargar los roles.</td></tr>`;
        }
    }

    document.getElementById("btnNuevoRol").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario();
    });

    document.getElementById("btnCerrarRol").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarRol").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarRoles").addEventListener("click", cargarRoles);

    buscar.addEventListener("input", pintarTabla);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campoId.value;

        const datos = {
            perf_descripcion: campoDescripcion.value.trim(),
            perf_estado: campoEstado.value
        };

        if (!datos.perf_descripcion) {
            mostrarMensaje(mensajeFormulario, "La descripción es obligatoria.", "error");
            return;
        }

        const repetido = roles.some(rol =>
            String(rol.perf_id) !== id &&
            (rol.perf_descripcion ?? "").trim().toLowerCase() === datos.perf_descripcion.toLowerCase()
        );

        if (repetido) {
            mostrarMensaje(mensajeFormulario, "Ya existe un rol con esa descripción.", "error");
            return;
        }

        try {
            const resultado = await guardar(id, datos);

            ocultarFormulario();
            await cargarRoles();
            avisarListado(resultado);

        } catch (error) {
            mostrarMensaje(mensajeFormulario, error.message, "error");
        }
    });

    tabla.addEventListener("click", async (evento) => {
        const botonEditar = evento.target.closest("[data-editar]");
        const botonEstado = evento.target.closest("[data-estado]");

        if (botonEditar) {
            try {
                const rol = await consultarRol(botonEditar.dataset.editar);

                limpiarFormulario();
                campoId.value = rol.perf_id;
                campoDescripcion.value = rol.perf_descripcion ?? "";
                campoEstado.value = esActivo(rol) ? "Activo" : "Inactivo";
                tituloFormulario.textContent = `Editar rol #${rol.perf_id}`;

                mostrarFormulario();

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }

        if (botonEstado) {
            try {
                const rol = await consultarRol(botonEstado.dataset.estado);
                const nuevoEstado = esActivo(rol) ? "Inactivo" : "Activo";

                if (nuevoEstado === "Inactivo" && !confirm(
                    `¿Inactivar el rol "${rol.perf_descripcion}"? Los usuarios que lo tienen asignado lo conservarán.`
                )) {
                    return;
                }

                await guardar(String(rol.perf_id), {
                    perf_descripcion: rol.perf_descripcion,
                    perf_estado: nuevoEstado
                });

                await cargarRoles();
                avisarListado(`El rol "${rol.perf_descripcion}" quedó ${nuevoEstado.toLowerCase()}.`);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }
    });

    limpiarFormulario();
    cargarRoles();
};
