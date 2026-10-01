package mx.clinica.gestores;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import mx.clinica.modelo.Cita;
import mx.clinica.modelo.Doctor;
import mx.clinica.modelo.Paciente;
import mx.clinica.persistencia.ArchivoCsv;
import mx.clinica.persistencia.Persistible;
import mx.clinica.util.ValidacionException;

/** Registro, búsqueda y persistencia de citas. */
public class GestorCitas implements Persistible<Cita> {

    public static final String ENCABEZADO = "id,fechaHora,motivo,idDoctor,idPaciente";

    private final List<Cita> citas = new ArrayList<>();
    private final String rutaArchivo;
    private final GestorDoctores gestorDoctores;
    private final GestorPacientes gestorPacientes;

    public GestorCitas(String rutaArchivo, GestorDoctores gestorDoctores, GestorPacientes gestorPacientes) {
        this.rutaArchivo = rutaArchivo;
        this.gestorDoctores = gestorDoctores;
        this.gestorPacientes = gestorPacientes;
    }

    /** Agrega una cita validando ID único y que doctor y paciente estén libres en ese horario. */
    public void agregarCita(Cita cita) throws ValidacionException {
        if (cita.getId() == null || cita.getId().trim().isEmpty()) {
            throw new ValidacionException("el ID no puede estar vacío.");
        }
        if (existeId(cita.getId())) {
            throw new ValidacionException("ya existe una cita con el ID '" + cita.getId() + "'.");
        }
        if (cita.getDoctor() == null || cita.getPaciente() == null) {
            throw new ValidacionException("la cita debe tener doctor y paciente.");
        }
        for (Cita c : citas) {
            if (!c.getFechaHora().equals(cita.getFechaHora())) {
                continue;
            }
            if (c.getDoctor().getId().equalsIgnoreCase(cita.getDoctor().getId())) {
                throw new ValidacionException("el doctor " + cita.getDoctor().getNombre()
                        + " ya tiene la cita " + c.getId() + " el " + c.getFechaHoraTexto() + ".");
            }
            if (c.getPaciente().getId().equalsIgnoreCase(cita.getPaciente().getId())) {
                throw new ValidacionException("el paciente " + cita.getPaciente().getNombre()
                        + " ya tiene la cita " + c.getId() + " el " + c.getFechaHoraTexto() + ".");
            }
        }
        citas.add(cita);
    }

    /** Devuelve las citas ordenadas por fecha y hora. */
    public List<Cita> listarCitas() {
        List<Cita> ordenadas = new ArrayList<>(citas);
        ordenadas.sort(Comparator.comparing(Cita::getFechaHora));
        return Collections.unmodifiableList(ordenadas);
    }

    /** Busca una cita por ID; devuelve null si no existe. */
    public Cita buscarPorId(String id) {
        for (Cita c : citas) {
            if (c.getId().equalsIgnoreCase(id)) {
                return c;
            }
        }
        return null;
    }

    public boolean existeId(String id) {
        return buscarPorId(id) != null;
    }

    @Override
    public void guardar(String ruta) throws IOException {
        List<String[]> filas = new ArrayList<>();
        for (Cita c : citas) {
            filas.add(c.aCamposCsv());
        }
        ArchivoCsv.escribir(ruta, ENCABEZADO, filas);
    }

    /** Carga las citas; requiere que doctores y pacientes ya estén cargados. */
    @Override
    public List<Cita> cargar(String ruta) throws IOException {
        if (ArchivoCsv.asegurarArchivo(ruta, ENCABEZADO)) {
            System.out.println("[INFO] Archivo " + ruta + " no encontrado: se regeneró vacío.");
        }
        citas.clear();
        boolean hayErrores = false;
        int linea = 1;
        for (String[] campos : ArchivoCsv.leer(ruta)) {
            linea++;
            try {
                if (campos.length < 5) {
                    throw new ValidacionException("línea incompleta.");
                }
                LocalDateTime fecha = LocalDateTime.parse(campos[1].trim(), Cita.FORMATO_FECHA);
                Doctor doctor = gestorDoctores.buscarPorId(campos[3].trim());
                Paciente paciente = gestorPacientes.buscarPorId(campos[4].trim());
                if (doctor == null) {
                    throw new ValidacionException("el doctor '" + campos[3] + "' no existe.");
                }
                if (paciente == null) {
                    throw new ValidacionException("el paciente '" + campos[4] + "' no existe.");
                }
                agregarCita(new Cita(campos[0].trim(), fecha, campos[2].trim(), doctor, paciente));
            } catch (DateTimeParseException e) {
                System.out.println("[AVISO] " + ruta + " línea " + linea + ": fecha inválida '" + campos[1] + "'.");
                hayErrores = true;
            } catch (ValidacionException e) {
                System.out.println("[AVISO] " + ruta + " línea " + linea + ": " + e.getMessage());
                hayErrores = true;
            }
        }
        if (hayErrores) {
            ArchivoCsv.respaldar(ruta);
        }
        return listarCitas();
    }

    @Override
    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
