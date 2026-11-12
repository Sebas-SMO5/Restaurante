
const API_FORMAS_PAGO = "/api/formas-pago";

window.inicializarFormasPago = function () {
    const vista = document.getElementById("vistaFormasPago");

    if (!vista) return;

    const formulario = document.getElementById("formaPagoForm");
    const seccionFormulario = document.getElementById("formaPagoFormSection");
    const tabla = document.getElementById("tablaFormasPago");
    const mensaje = document.getElementById("formaPagoMensaje");
    const buscar = document.getElementById("buscarFormaPago");
    const campoId = document.getElementById("formaPagoId");

    if (vista.dataset.inicializada === "true") {
        cargarFormasPago();
        return;
    }

    vista.dataset.inicializada = "true";

    const mostrarMensaje = (texto, error = false) => {
        mensaje.textContent = texto;
        mensaje.style.color = error ? "#dc2626" : "#15803d";
    };

    const limpiarFormulario = () => {
        formulario.reset();
        campoId.readOnly = false;
        formulario.dataset.edicion = "false";
        document.getElementById("tituloFormularioFormaPago").textContent = "Nueva forma de pago";
        mensaje.textContent = "";
    };

    const mostrarFormulario = () => {
        seccionFormulario.hidden = false;
    };

    const ocultarFormulario = () => {
        seccionFormulario.hidden = true;
        limpiarFormulario();
    };

    async function cargarFormasPago() {
        try {
            const respuesta = await fetch(API_FORMAS_PAGO);

            if (!respuesta.ok) {
                throw new Error("No se pudieron cargar las formas de pago.");
            }

            const formasPago = await respuesta.json();

            document.getElementById("totalFormasPago").textContent = formasPago.length;

            document.getElementById("formasPagoActivas").textContent =
                formasPago.filter(fp => fp.fp_estado?.toLowerCase() === "activo").length;

            document.getElementById("formasPagoInactivas").textContent =
                formasPago.filter(fp => fp.fp_estado?.toLowerCase() === "inactivo").length;

            const texto = buscar.value.trim().toLowerCase();

            const filtradas = formasPago.filter(fp =>
                String(fp.fp_id).includes(texto) ||
                fp.fp_descripcion.toLowerCase().includes(texto) ||
                fp.fp_estado.toLowerCase().includes(texto)
            );

            tabla.innerHTML = filtradas.length
                ? filtradas.map(fp => `
                    <tr>
                        <td>${fp.fp_id}</td>
                        <td>${fp.fp_descripcion}</td>
                        <td>${fp.fp_estado}</td>
                        <td>
                            <button type="button" data-editar="${fp.fp_id}">Editar</button>
                            <button type="button" data-estado="${fp.fp_id}">
                                ${fp.fp_estado.toLowerCase() === "activo" ? "Inactivar" : "Activar"}
                            </button>
                        </td>
                    </tr>
                `).join("")
                : `<tr><td colspan="4">No hay formas de pago registradas.</td></tr>`;

        } catch (error) {
            console.error(error);
            tabla.innerHTML = `<tr><td colspan="4">Error al cargar las formas de pago.</td></tr>`;
        }
    }

    document.getElementById("btnNuevaFormaPago").addEventListener("click", () => {
        limpiarFormulario();
        mostrarFormulario();
    });

    document.getElementById("btnCancelarFormaPago").addEventListener("click", ocultarFormulario);

    buscar.addEventListener("input", cargarFormasPago);

    formulario.addEventListener("submit", async (evento) => {
        evento.preventDefault();

        const id = campoId.value.trim();

        const datos = {
            fp_id: Number(id),
            fp_descripcion: document.getElementById("formaPagoDescripcion").value.trim(),
            fp_estado: document.getElementById("formaPagoEstado").value
        };

        if (!id || !Number.isInteger(datos.fp_id) || datos.fp_id <= 0) {
            mostrarMensaje("Ingresa un código válido.", true);
            return;
        }

        if (!datos.fp_descripcion || !datos.fp_estado) {
            mostrarMensaje("Completa todos los campos.", true);
            return;
        }

        const editando = formulario.dataset.edicion === "true";

        try {
            const respuesta = await fetch(
                editando ? `${API_FORMAS_PAGO}/${id}` : API_FORMAS_PAGO,
                {
                    method: editando ? "PUT" : "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(datos)
                }
            );

            const resultado = await respuesta.text();

            if (!respuesta.ok) {
                throw new Error(resultado);
            }

            ocultarFormulario();
            await cargarFormasPago();
            alert(resultado);

        } catch (error) {
            mostrarMensaje(error.message || "No se pudo guardar la forma de pago.", true);
        }
    });

    tabla.addEventListener("click", async (evento) => {
        const botonEditar = evento.target.closest("[data-editar]");
        const botonEstado = evento.target.closest("[data-estado]");

        if (botonEditar) {
            try {
                const id = botonEditar.dataset.editar;
                const respuesta = await fetch(`${API_FORMAS_PAGO}/${id}`);

                if (!respuesta.ok) {
                    throw new Error("No se pudo consultar la forma de pago.");
                }

                const formaPago = await respuesta.json();

                campoId.value = formaPago.fp_id;
                campoId.readOnly = true;

                document.getElementById("formaPagoDescripcion").value = formaPago.fp_descripcion;
                document.getElementById("formaPagoEstado").value = formaPago.fp_estado;

                formulario.dataset.edicion = "true";
                document.getElementById("tituloFormularioFormaPago").textContent = "Editar forma de pago";

                mensaje.textContent = "";
                mostrarFormulario();

            } catch (error) {
                alert(error.message);
            }
        }

        if (botonEstado) {
            try {
                const id = botonEstado.dataset.estado;
                const consulta = await fetch(`${API_FORMAS_PAGO}/${id}`);

                if (!consulta.ok) {
                    throw new Error("No se pudo consultar la forma de pago.");
                }

                const formaPago = await consulta.json();

                const nuevoEstado = formaPago.fp_estado.toLowerCase() === "activo"
                    ? "Inactivo"
                    : "Activo";

                const respuesta = await fetch(`${API_FORMAS_PAGO}/${id}`, {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json"
                    },
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

            } catch (error) {
                alert(error.message);
            }
        }
    });

    cargarFormasPago();
};
