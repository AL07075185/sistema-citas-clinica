package mx.clinica.modelo;

/** Doctor del consultorio con su especialidad. */
public class Doctor extends Persona {

    private String especialidad;

    public Doctor(String id, String nombre, String especialidad) {
        super(id, nombre);
        this.especialidad = especialidad;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String getTipo() {
        return "Doctor";
    }

    @Override
    public String[] aCamposCsv() {
        return new String[]{getId(), getNombre(), especialidad};
    }

    @Override
    public String toString() {
        return super.toString() + " | " + especialidad;
    }
}
