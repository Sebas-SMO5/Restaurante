
const API_USUARIOS = "/api/usuarios";
const API_PERFILES_USUARIO = "/api/perfiles";

window.inicializarUsuarios = function () {
    const vista = document.getElementById("vistaUsuarios");

    if (!vista) return;

    const formulario = document.getElementById("usuarioForm");
    const seccionFormulario = document.getElementById("usuarioFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioUsuario");
    const tabla = document.getElementById("tablaUsuarios");
    const mensajeFormulario = document.getElementById("usuarioMensaje");
    const mensajeListado = document.getElementById("usuariosListadoMensaje");
    const buscar = document.getElementById("buscarUsuario");
    const selectPerfil = document.getElementById("usuarioPerfil");
    const campoPass = document.getElementById("usuarioPass");
    const campoPassConfirmar = document.getElementById("usuarioPassConfirmar");
    const etiquetaPass = document.getElementById("usuarioPassEtiqueta");
    const ayudaPass = document.getElementById("usuarioPassAyuda");
    const botonGuardar = document.getElementById("btnGuardarUsuario");

    const campos = {
        nombre: document.getElementById("usuarioNombre"),
        apellido: document.getElementById("usuarioApellido"),
        direccion: document.getElementById("usuarioDireccion"),
        telefono: document.getElementById("usuarioTelefono"),
        correo: document.getElementById("usuarioCorreo"),
        login: document.getElementById("usuarioLogin"),
        estado: document.getElementById("usuarioEstado")
    };

    const PASS_MIN = 6;
    const AYUDA_NUEVO = "El login debe tener entre 3 y 30 caracteres, sin espacios. La contraseña se guarda cifrada.";
    const AYUDA_EDICION = "Deja la contraseña vacía para conservar la actual. Si la escribes, se reemplaza.";

    if (vista.dataset.inicializada === "true") {
        cargarTodo();
        return;
    }

    vista.dataset.inicializada = "true";

    let usuarios = [];
    let perfiles = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (registro, campo) => String(registro?.[campo] ?? "").toLowerCase() === "activo";
    const usuarioActivo = (u) => esActivo(u, "usu_estado");
    const idSesion = () => Number(window.usuarioSesion?.usu_id ?? 0);

    const perfilPorId = (perfId) => perfiles.find(p => Number(p.perf_id) === Number(perfId));

    const nombrePerfil = (perfId) => {
        if (perfId == null) return "Sin rol";
        return perfilPorId(perfId)?.perf_descripcion ?? `Rol ${perfId}`;
    };

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

    /**
     * Solo se ofrecen roles activos. Si el usuario editado tiene un rol inactivo,
     * se muestra también para no perderlo sin querer.
     */
    function llenarPerfiles(perfIdActual = null) {
        const disponibles = perfiles.filter(p =>
            esActivo(p, "perf_estado") || Number(p.perf_id) === Number(perfIdActual)
        );

        selectPerfil.innerHTML = `<option value="">Seleccione un rol</option>` +
            disponibles.map(p => `
                <option value="${escapar(p.perf_id)}">
                    ${escapar(p.perf_descripcion)}${esActivo(p, "perf_estado") ? "" : " (inactivo)"}
                </option>
            `).join("");

        selectPerfil.value = perfIdActual ?? "";
    }

    const limpiarFormulario = () => {
        formulario.reset();
        delete formulario.dataset.id;
        campos.estado.value = "Activo";
        campos.estado.disabled = false;
        etiquetaPass.textContent = "Contraseña";
        campoPass.placeholder = `Mínimo ${PASS_MIN} caracteres`;
        ayudaPass.textContent = AYUDA_NUEVO;
        tituloFormulario.textContent = "Registrar usuario";
        llenarPerfiles();
        ocultarMensaje(mensajeFormulario);
    };

    const mostrarFormulario = () => {
        seccionFormulario.classList.remove("hidden");
        seccionFormulario.scrollIntoView({ behavior: "smooth", block: "start" });
        campos.nombre.focus({ preventScroll: true });
    };

    const ocultarFormulario = () => {
        seccionFormulario.classList.add("hidden");
        limpiarFormulario();
    };

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtrados = usuarios.filter(u =>
            [
                u.usu_id,
                `${u.usu_nombre ?? ""} ${u.usu_apellido ?? ""}`,
                u.usu_login,
                u.usu_correo,
                u.usu_telefono,
                u.usu_estado,
                nombrePerfil(u.perf_id)
            ].some(valor => String(valor ?? "").toLowerCase().includes(texto))
        );

        if (!filtrados.length) {
            tabla.innerHTML = `<tr><td colspan="8">${usuarios.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay usuarios registrados."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtrados.map(u => {
            const esYo = Number(u.usu_id) === idSesion();
            const perfil = perfilPorId(u.perf_id);
            const perfilInactivo = perfil && !esActivo(perfil, "perf_estado");

            return `
                <tr>
                    <td>${escapar(u.usu_id)}</td>
                    <td>
                        ${escapar(u.usu_nombre)} ${escapar(u.usu_apellido)}
                        ${esYo ? `<span class="badge badge-neutral badge-small">Tú</span>` : ""}
                    </td>
                    <td><span class="badge badge-neutral">${escapar(u.usu_login)}</span></td>
                    <td>${escapar(u.usu_correo)}</td>
                    <td>${escapar(u.usu_telefono)}</td>
                    <td>
                        ${escapar(nombrePerfil(u.perf_id))}
                        ${perfilInactivo ? `<span class="badge badge-muted badge-small">Rol inactivo</span>` : ""}
                    </td>
                    <td>
                        <span class="badge ${usuarioActivo(u) ? "badge-success" : "badge-muted"}">
                            ${escapar(u.usu_estado)}
                        </span>
                    </td>
                    <td>
                        <div class="actions">
                            <button class="action-button" type="button"
                                data-editar="${escapar(u.usu_id)}">
                                Editar
                            </button>
                            <button class="action-button action-button-light" type="button"
                                data-estado="${escapar(u.usu_id)}">
                                ${usuarioActivo(u) ? "Inactivar" : "Activar"}
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        }).join("");
    }

    async function cargarPerfiles() {
        try {
            const respuesta = await fetch(API_PERFILES_USUARIO);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar los roles.");
            }

            perfiles = await respuesta.json();
        } catch (error) {
            console.error(error);
            perfiles = [];
        }

        llenarPerfiles();
    }

    async function cargarUsuarios() {
        try {
            const respuesta = await fetch(API_USUARIOS);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar los usuarios.");
            }

            usuarios = await respuesta.json();
            usuarios.sort((a, b) =>
                `${a.usu_nombre} ${a.usu_apellido}`.localeCompare(`${b.usu_nombre} ${b.usu_apellido}`, "es")
            );

            document.getElementById("totalUsuarios").textContent = usuarios.length;
            document.getElementById("usuariosActivos").textContent = usuarios.filter(usuarioActivo).length;
            document.getElementById("usuariosInactivos").textContent =
                usuarios.filter(u => !usuarioActivo(u)).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="8">Error al cargar los usuarios.</td></tr>`;
        }
    }

    async function cargarTodo() {
        await cargarPerfiles();
        await cargarUsuarios();
    }

    async function guardarUsuario(id, datos) {
        const respuesta = await fetch(id ? `${API_USUARIOS}/${id}` : API_USUARIOS, {
            method: id ? "PUT" : "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(datos)
        });

        const resultado = await respuesta.text();

        if (!respuesta.ok) {
            throw new Error(resultado || "No se pudo guardar el usuario.");
        }

        return resultado;
    }

    /** Devuelve el mensaje de error o null si el formulario es válido. */
    function validar(datos, editando) {
        const obligatorios = [
            datos.usu_nombre, datos.usu_apellido, datos.usu_direccion,
            datos.usu_telefono, datos.usu_correo, datos.usu_login, datos.usu_estado
        ];

        if (obligatorios.some(valor => !valor)) {
            return "Completa todos los campos obligatorios.";
        }

        if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(datos.usu_correo)) {
            return "Ingresa un correo válido.";
        }

        if (!/^[0-9+\-\s()]{7,20}$/.test(datos.usu_telefono)) {
            return "Ingresa un teléfono válido (solo números, espacios, +, - o paréntesis).";
        }

        if (!/^[A-Za-z0-9._-]{3,30}$/.test(datos.usu_login)) {
            return "El login debe tener entre 3 y 30 caracteres: letras, números, punto, guion o guion bajo.";
        }

        const loginRepetido = usuarios.some(u =>
            String(u.usu_id) !== String(formulario.dataset.id ?? "") &&
            String(u.usu_login ?? "").toLowerCase() === datos.usu_login.toLowerCase()
        );

        if (loginRepetido) {
            return "Ya existe otro usuario con ese login.";
        }

        if (!datos.perf_id) {
            return "Selecciona el rol del usuario.";
        }

        const pass = campoPass.value;
        const confirmar = campoPassConfirmar.value;

        if (!editando || pass || confirmar) {
            if (pass.length < PASS_MIN) {
                return `La contraseña debe tener al menos ${PASS_MIN} caracteres.`;
            }

            if (pass !== confirmar) {
                return "Las contraseñas no coinciden.";
            }
        }

        return null;
    }

    document.getElementById("btnNuevoUsuario").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario();
    });

    document.getElementById("btnCerrarUsuario").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarUsuario").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarUsuarios").addEventListener("click", cargarTodo);

    buscar.addEventListener("input", pintarTabla);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = formulario.dataset.id ?? null;
        const editando = Boolean(id);

        const datos = {
            usu_nombre: campos.nombre.value.trim(),
            usu_apellido: campos.apellido.value.trim(),
            usu_direccion: campos.direccion.value.trim(),
            usu_telefono: campos.telefono.value.trim(),
            usu_correo: campos.correo.value.trim(),
            perf_id: selectPerfil.value ? Number(selectPerfil.value) : null,
            usu_login: campos.login.value.trim(),
            usu_pass: campoPass.value,
            usu_estado: campos.estado.value
        };

        const error = validar(datos, editando);

        if (error) {
            mostrarMensaje(mensajeFormulario, error, "error");
            return;
        }

        botonGuardar.disabled = true;

        try {
            const resultado = await guardarUsuario(id, datos);

            ocultarFormulario();
            await cargarUsuarios();
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

        try {
            const respuesta = await fetch(`${API_USUARIOS}/${id}`);

            if (!respuesta.ok) {
                throw new Error("No se pudo consultar el usuario.");
            }

            const usuario = await respuesta.json();

            if (botonEditar) {
                limpiarFormulario();

                campos.nombre.value = usuario.usu_nombre ?? "";
                campos.apellido.value = usuario.usu_apellido ?? "";
                campos.direccion.value = usuario.usu_direccion ?? "";
                campos.telefono.value = usuario.usu_telefono ?? "";
                campos.correo.value = usuario.usu_correo ?? "";
                campos.login.value = usuario.usu_login ?? "";
                campos.estado.value = usuarioActivo(usuario) ? "Activo" : "Inactivo";
                llenarPerfiles(usuario.perf_id);

                etiquetaPass.textContent = "Nueva contraseña (opcional)";
                campoPass.placeholder = "Dejar vacía para conservarla";
                ayudaPass.textContent = AYUDA_EDICION;

                formulario.dataset.id = usuario.usu_id;
                tituloFormulario.textContent =
                    `Editar usuario: ${usuario.usu_nombre ?? ""} ${usuario.usu_apellido ?? ""}`.trim();

                mostrarFormulario();
                return;
            }

            const nuevoEstado = usuarioActivo(usuario) ? "Inactivo" : "Activo";

            const esYo = Number(usuario.usu_id) === idSesion();
            const aviso = esYo
                ? "¿Inactivar tu propio usuario? Al cerrar sesión ya no podrás volver a entrar hasta que otro administrador te active."
                : `¿Inactivar a ${usuario.usu_nombre} ${usuario.usu_apellido}? No podrá iniciar sesión hasta que lo actives de nuevo.`;

            if (nuevoEstado === "Inactivo" && !confirm(aviso)) {
                return;
            }

            await guardarUsuario(usuario.usu_id, {
                ...usuario,
                usu_pass: "",
                usu_estado: nuevoEstado
            });

            await cargarUsuarios();
            avisarListado(`${usuario.usu_nombre} ${usuario.usu_apellido} quedó ${nuevoEstado.toLowerCase()}.`);

        } catch (error) {
            avisarListado(error.message, "error");
        }
    });

    limpiarFormulario();
    cargarTodo();
};
