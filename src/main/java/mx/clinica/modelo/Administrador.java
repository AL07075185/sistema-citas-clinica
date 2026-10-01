package mx.clinica.modelo;

/** Usuario con permiso de acceso al sistema. */
public class Administrador {

    private String id;
    private String contrasena;

    public Administrador(String id, String contrasena) {
        this.id = id;
        this.contrasena = contrasena;
    }

    public String getId() {
        return id;
    }

    public String getContrasena() {
        return contrasena;
    }

    /** Devuelve true si el ID y la contraseña coinciden. */
    public boolean autenticar(String id, String contrasena) {
        return this.id.equals(id) && this.contrasena.equals(contrasena);
    }

    public String[] aCamposCsv() {
        return new String[]{id, contrasena};
    }
}
