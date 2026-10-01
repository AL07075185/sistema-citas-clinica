package mx.clinica.modelo;

/** Clase abstracta con los datos comunes de Doctor y Paciente. */
public abstract class Persona {

    private String id;
    private String nombre;

    protected Persona(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** Devuelve el tipo de persona (Doctor o Paciente). */
    public abstract String getTipo();

    /** Devuelve los campos que se guardan en el archivo CSV. */
    public abstract String[] aCamposCsv();

    @Override
    public String toString() {
        return id + " | " + nombre;
    }
}
