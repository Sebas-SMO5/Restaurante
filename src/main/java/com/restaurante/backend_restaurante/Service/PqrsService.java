package com.restaurante.backend_restaurante.Service;

import com.restaurante.backend_restaurante.DAO.PqrsGestionDAO;
import com.restaurante.backend_restaurante.Model.PqrsVista;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Reglas de PQRS:
 *  - Se radican como "Pendiente" desde el sistema (origen Interno) o desde la página pública (origen Web).
 *  - Al responderse pasan a "Atendida" y se guarda quién y cuándo respondió.
 *  - La página pública tiene límite de envíos por IP y un campo trampa contra robots.
 */
@Service
public class PqrsService {

    public static final String ORIGEN_INTERNO = "Interno";
    public static final String ORIGEN_WEB = "Web";

    private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9+\\-\\s()]{7,20}$");
    private static final long UNA_HORA_MS = 60L * 60 * 1000;

    private final PqrsGestionDAO dao;
    private final int maximoPorHora;

    /** Envíos recientes por IP (solo en memoria; se reinicia con el servidor). */
    private final Map<String, Deque<Long>> enviosPorIp = new ConcurrentHashMap<>();

    public PqrsService(PqrsGestionDAO dao, @Value("${app.pqrs.publico.maximo-por-hora:5}") int maximoPorHora) {
        this.dao = dao;
        this.maximoPorHora = maximoPorHora;
    }

    public int radicarInterna(PqrsVista.Nueva datos) {
        return dao.insertar(validar(datos), ORIGEN_INTERNO);
    }

    public int radicarPublica(PqrsVista.Nueva datos, String ip) {

        // Campo trampa lleno: se responde como si todo estuviera bien, pero no se guarda.
        if (datos != null && datos.sitio_web() != null && !datos.sitio_web().isBlank()) {
            return 0;
        }

        PqrsVista.Nueva limpia = validar(datos);
        registrarEnvio(ip);

        return dao.insertar(limpia, ORIGEN_WEB);
    }

    public void responder(int id, PqrsVista.Respuesta datos, int usuId) {
        PqrsVista.Pqrs pqrs = dao.obtener(id);

        if (pqrs == null) {
            throw new ReglaNegocioException("No se encontró la PQRS.");
        }

        if (pqrs.est_id() == dao.estadoAtendida()) {
            throw new ReglaNegocioException("La PQRS #" + id + " ya fue respondida.");
        }

        String respuesta = datos == null || datos.pqrs_respuesta() == null ? "" : datos.pqrs_respuesta().trim();

        if (respuesta.length() < 10) {
            throw new ReglaNegocioException("Escribe una respuesta de al menos 10 caracteres.");
        }

        if (respuesta.length() > 2000) {
            throw new ReglaNegocioException("La respuesta admite máximo 2000 caracteres.");
        }

        dao.responder(id, respuesta, usuId);
    }

    public PqrsVista.Consulta consultar(Integer id, String correo) {
        if (id == null || id <= 0 || correo == null || correo.isBlank()) {
            throw new ReglaNegocioException("Escribe el número de radicado y el correo con el que la registraste.");
        }

        PqrsVista.Consulta consulta = dao.consultaPublica(id, correo.trim());

        if (consulta == null) {
            throw new ReglaNegocioException("No encontramos una PQRS con ese número y correo.");
        }

        return consulta;
    }

    // ---------------------------------------------------------------

    private PqrsVista.Nueva validar(PqrsVista.Nueva d) {

        if (d == null || d.tpqrs_id() == null || !dao.tipoActivo(d.tpqrs_id())) {
            throw new ReglaNegocioException("Selecciona el tipo de PQRS.");
        }

        String nombre = texto(d.pqrs_nombre());
        String correo = texto(d.pqrs_correo());
        String telefono = texto(d.pqrs_telefono());
        String descripcion = texto(d.pqrs_descripcion());

        if (nombre.length() < 3 || nombre.length() > 80) {
            throw new ReglaNegocioException("Escribe el nombre (entre 3 y 80 caracteres).");
        }

        if (correo.length() > 60 || !CORREO.matcher(correo).matches()) {
            throw new ReglaNegocioException("Escribe un correo válido (máximo 60 caracteres).");
        }

        if (!TELEFONO.matcher(telefono).matches()) {
            throw new ReglaNegocioException("Escribe un teléfono válido (entre 7 y 20 caracteres).");
        }

        if (descripcion.length() < 15 || descripcion.length() > 2000) {
            throw new ReglaNegocioException("Describe la PQRS con al menos 15 caracteres (máximo 2000).");
        }

        return new PqrsVista.Nueva(d.tpqrs_id(), nombre, correo.toLowerCase(), telefono, descripcion, null);
    }

    private void registrarEnvio(String ip) {
        long ahora = System.currentTimeMillis();
        Deque<Long> envios = enviosPorIp.computeIfAbsent(ip == null ? "desconocida" : ip, k -> new ArrayDeque<>());

        synchronized (envios) {
            while (!envios.isEmpty() && ahora - envios.peekFirst() > UNA_HORA_MS) {
                envios.pollFirst();
            }

            if (envios.size() >= maximoPorHora) {
                throw new ReglaNegocioException(
                        "Has enviado varias PQRS en poco tiempo. Inténtalo de nuevo en una hora o comunícate con el restaurante.");
            }

            envios.addLast(ahora);
        }
    }

    private String texto(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
