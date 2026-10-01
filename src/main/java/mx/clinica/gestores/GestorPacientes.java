package mx.clinica.gestores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mx.clinica.modelo.Paciente;
import mx.clinica.persistencia.ArchivoCsv;
import mx.clinica.persistencia.Persistible;
import mx.clinica.util.ValidacionException;

/** Registro, búsqueda y persistencia de pacientes. */
public class GestorPacientes implements Persistible<Paciente> {

    public static final String ENCABEZADO = "id,nombre";

    private final List<Paciente> pacientes = new ArrayList<>();
    private final String rutaArchivo;

    public GestorPacientes(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /** Agrega un paciente validando que el ID no esté vacío ni repetido. */
    public void agregarPaciente(Paciente paciente) throws ValidacionException {
        if (paciente.getId() == null || paciente.getId().trim().isEmpty()) {
            throw new ValidacionException("el ID no puede estar vacío.");
        }
        if (existeId(paciente.getId())) {
            throw new ValidacionException("ya existe un paciente con el ID '" + paciente.getId() + "'.");
        }
        pacientes.add(paciente);
    }

    public List<Paciente> listarPacientes() {
        return Collections.unmodifiableList(pacientes);
    }

    /** Busca un paciente por ID; devuelve null si no existe. */
    public Paciente buscarPorId(String id) {
        for (Paciente p : pacientes) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
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
        for (Paciente p : pacientes) {
            filas.add(p.aCamposCsv());
        }
        ArchivoCsv.escribir(ruta, ENCABEZADO, filas);
    }

    @Override
    public List<Paciente> cargar(String ruta) throws IOException {
        if (ArchivoCsv.asegurarArchivo(ruta, ENCABEZADO)) {
            System.out.println("[INFO] Archivo " + ruta + " no encontrado: se regeneró vacío.");
        }
        pacientes.clear();
        boolean hayErrores = false;
        int linea = 1;
        for (String[] campos : ArchivoCsv.leer(ruta)) {
            linea++;
            try {
                if (campos.length < 2) {
                    throw new ValidacionException("línea incompleta.");
                }
                agregarPaciente(new Paciente(campos[0].trim(), campos[1].trim()));
            } catch (ValidacionException e) {
                System.out.println("[AVISO] " + ruta + " línea " + linea + ": " + e.getMessage());
                hayErrores = true;
            }
        }
        if (hayErrores) {
            ArchivoCsv.respaldar(ruta);
        }
        return listarPacientes();
    }

    @Override
    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
