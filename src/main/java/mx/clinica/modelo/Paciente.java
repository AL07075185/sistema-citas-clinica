package mx.clinica.modelo;

/** Paciente que acude al consultorio. */
public class Paciente extends Persona {

    public Paciente(String id, String nombre) {
        super(id, nombre);
    }

    @Override
    public String getTipo() {
        return "Paciente";
    }

    @Override
    public String[] aCamposCsv() {
        return new String[]{getId(), getNombre()};
    }
}
