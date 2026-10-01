package mx.clinica.gestores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mx.clinica.modelo.Doctor;
import mx.clinica.persistencia.ArchivoCsv;
import mx.clinica.persistencia.Persistible;
import mx.clinica.util.ValidacionException;

/** Registro, búsqueda y persistencia de doctores. */
public class GestorDoctores implements Persistible<Doctor> {

    public static final String ENCABEZADO = "id,nombre,especialidad";

    private final List<Doctor> doctores = new ArrayList<>();
    private final String rutaArchivo;

    public GestorDoctores(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    /** Agrega un doctor validando que el ID no esté vacío ni repetido. */
    public void agregarDoctor(Doctor doctor) throws ValidacionException {
        if (doctor.getId() == null || doctor.getId().trim().isEmpty()) {
            throw new ValidacionException("el ID no puede estar vacío.");
        }
        if (existeId(doctor.getId())) {
            throw new ValidacionException("ya existe un doctor con el ID '" + doctor.getId() + "'.");
        }
        doctores.add(doctor);
    }

    public List<Doctor> listarDoctores() {
        return Collections.unmodifiableList(doctores);
    }

    /** Busca un doctor por ID; devuelve null si no existe. */
    public Doctor buscarPorId(String id) {
        for (Doctor d : doctores) {
            if (d.getId().equalsIgnoreCase(id)) {
                return d;
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
        for (Doctor d : doctores) {
            filas.add(d.aCamposCsv());
        }
        ArchivoCsv.escribir(ruta, ENCABEZADO, filas);
    }

    @Override
    public List<Doctor> cargar(String ruta) throws IOException {
        if (ArchivoCsv.asegurarArchivo(ruta, ENCABEZADO)) {
            System.out.println("[INFO] Archivo " + ruta + " no encontrado: se regeneró vacío.");
        }
        doctores.clear();
        boolean hayErrores = false;
        int linea = 1;
        for (String[] campos : ArchivoCsv.leer(ruta)) {
            linea++;
            try {
                if (campos.length < 3) {
                    throw new ValidacionException("línea incompleta.");
                }
                agregarDoctor(new Doctor(campos[0].trim(), campos[1].trim(), campos[2].trim()));
            } catch (ValidacionException e) {
                System.out.println("[AVISO] " + ruta + " línea " + linea + ": " + e.getMessage());
                hayErrores = true;
            }
        }
        if (hayErrores) {
            ArchivoCsv.respaldar(ruta);
        }
        return listarDoctores();
    }

    @Override
    public String getRutaArchivo() {
        return rutaArchivo;
    }
}
