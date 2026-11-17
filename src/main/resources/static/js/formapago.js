
const API_FORMAS_PAGO = "/api/formas-pago";

window.inicializarFormasPago = function () {
    const vista = document.getElementById("vistaFormasPago");

    if (!vista) return;

    const formulario = document.getElementById("formaPagoForm");
    const seccionFormulario = document.getElementById("formaPagoFormSection");
    const tituloFormulario = document.getElementById("tituloFormularioFormaPago");
    const tabla = document.getElementById("tablaFormasPago");
    const mensajeFormulario = document.getElementById("formaPagoMensaje");
    const mensajeListado = document.getElementById("formasPagoListadoMensaje");
    const buscar = document.getElementById("buscarFormaPago");
    const campoId = document.getElementById("formaPagoId");
    const campoDescripcion = document.getElementById("formaPagoDescripcion");
    const campoEstado = document.getElementById("formaPagoEstado");

    if (vista.dataset.inicializada === "true") {
        cargarFormasPago();
        return;
    }

    vista.dataset.inicializada = "true";

    let formasPago = [];
    let temporizadorMensaje = null;

    const escapar = (valor) => String(valor ?? "")
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");

    const esActivo = (fp) => fp.fp_estado?.toLowerCase() === "activo";

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
        campoId.readOnly = false;
        campoEstado.value = "Activo";
        formulario.dataset.edicion = "false";
        tituloFormulario.textContent = "Registrar forma de pago";
        ocultarMensaje(mensajeFormulario);
    };

    const mostrarFormulario = (campoFoco) => {
        seccionFormulario.classList.remove("hidden");
        seccionFormulario.scrollIntoView({ behavior: "smooth", block: "start" });
        campoFoco.focus({ preventScroll: true });
    };

    const ocultarFormulario = () => {
        seccionFormulario.classList.add("hidden");
        limpiarFormulario();
    };

    const consultarFormaPago = async (id) => {
        const respuesta = await fetch(`${API_FORMAS_PAGO}/${id}`);

        if (!respuesta.ok) {
            throw new Error("No se pudo consultar la forma de pago.");
        }

        return respuesta.json();
    };

    function pintarTabla() {
        const texto = buscar.value.trim().toLowerCase();

        const filtradas = formasPago.filter(fp =>
            String(fp.fp_id).includes(texto) ||
            (fp.fp_descripcion ?? "").toLowerCase().includes(texto) ||
            (fp.fp_estado ?? "").toLowerCase().includes(texto)
        );

        if (!filtradas.length) {
            tabla.innerHTML = `<tr><td colspan="4">${formasPago.length
                ? "No hay coincidencias con la búsqueda."
                : "No hay formas de pago registradas."}</td></tr>`;
            return;
        }

        tabla.innerHTML = filtradas.map(fp => `
            <tr>
                <td>${escapar(fp.fp_id)}</td>
                <td>${escapar(fp.fp_descripcion)}</td>
                <td>
                    <span class="badge ${esActivo(fp) ? "badge-success" : "badge-muted"}">
                        ${escapar(fp.fp_estado)}
                    </span>
                </td>
                <td>
                    <div class="actions">
                        <button class="action-button" type="button"
                            data-editar="${escapar(fp.fp_id)}">
                            Editar
                        </button>
                        <button class="action-button action-button-light" type="button"
                            data-estado="${escapar(fp.fp_id)}">
                            ${esActivo(fp) ? "Inactivar" : "Activar"}
                        </button>
                    </div>
                </td>
            </tr>
        `).join("");
    }

    async function cargarFormasPago() {
        try {
            const respuesta = await fetch(API_FORMAS_PAGO);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar las formas de pago.");
            }

            formasPago = await respuesta.json();
            formasPago.sort((a, b) => a.fp_id - b.fp_id);

            document.getElementById("totalFormasPago").textContent = formasPago.length;
            document.getElementById("formasPagoActivas").textContent =
                formasPago.filter(esActivo).length;
            document.getElementById("formasPagoInactivas").textContent =
                formasPago.filter(fp => !esActivo(fp)).length;

            pintarTabla();

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="4">Error al cargar las formas de pago.</td></tr>`;
        }
    }

    document.getElementById("btnNuevaFormaPago").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario(campoId);
    });

    document.getElementById("btnCerrarFormaPago").addEventListener("click", ocultarFormulario);
    document.getElementById("btnCancelarFormaPago").addEventListener("click", ocultarFormulario);
    document.getElementById("btnActualizarFormasPago").addEventListener("click", cargarFormasPago);

    buscar.addEventListener("input", pintarTabla);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campoId.value.trim();
        const editando = formulario.dataset.edicion === "true";

        const datos = {
            fp_id: Number(id),
            fp_descripcion: campoDescripcion.value.trim(),
            fp_estado: campoEstado.value
        };

        if (!id || !Number.isInteger(datos.fp_id) || datos.fp_id <= 0 || datos.fp_id > 99) {
            mostrarMensaje(mensajeFormulario, "Ingresa un código válido entre 1 y 99.", "error");
            return;
        }

        if (!datos.fp_descripcion || !datos.fp_estado) {
            mostrarMensaje(mensajeFormulario, "Completa todos los campos.", "error");
            return;
        }

        if (!editando && formasPago.some(fp => fp.fp_id === datos.fp_id)) {
            mostrarMensaje(mensajeFormulario, "Ya existe una forma de pago con ese código.", "error");
            return;
        }

        try {
            const respuesta = await fetch(
                editando ? `${API_FORMAS_PAGO}/${id}` : API_FORMAS_PAGO,
                {
                    method: editando ? "PUT" : "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(datos)
                }
            );

            const resultado = await respuesta.text();

            if (!respuesta.ok) {
                throw new Error(resultado);
            }

            ocultarFormulario();
            await cargarFormasPago();
            avisarListado(resultado);

        } catch (error) {
            mostrarMensaje(mensajeFormulario,
                error.message || "No se pudo guardar la forma de pago.", "error");
        }
    });

    tabla.addEventListener("click", async (evento) => {
        const botonEditar = evento.target.closest("[data-editar]");
        const botonEstado = evento.target.closest("[data-estado]");

        if (botonEditar) {
            try {
                const formaPago = await consultarFormaPago(botonEditar.dataset.editar);

                limpiarFormulario();
                campoId.value = formaPago.fp_id;
                campoId.readOnly = true;
                campoDescripcion.value = formaPago.fp_descripcion;
                campoEstado.value = esActivo(formaPago) ? "Activo" : "Inactivo";

                formulario.dataset.edicion = "true";
                tituloFormulario.textContent = "Editar forma de pago";

                mostrarFormulario(campoDescripcion);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }

        if (botonEstado) {
            try {
                const formaPago = await consultarFormaPago(botonEstado.dataset.estado);
                const nuevoEstado = esActivo(formaPago) ? "Inactivo" : "Activo";

                const respuesta = await fetch(`${API_FORMAS_PAGO}/${formaPago.fp_id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        fp_id: formaPago.fp_id,
                        fp_descripcion: formaPago.fp_descripcion,
                        fp_estado: nuevoEstado
                    })
                });

                const resultado = await respuesta.text();

                if (!respuesta.ok) {
                    throw new Error(resultado);
                }

                await cargarFormasPago();
                avisarListado(`"${formaPago.fp_descripcion}" quedó ${nuevoEstado.toLowerCase()}.`);

            } catch (error) {
                avisarListado(error.message, "error");
            }
        }
    });

    limpiarFormulario();
    cargarFormasPago();
};
