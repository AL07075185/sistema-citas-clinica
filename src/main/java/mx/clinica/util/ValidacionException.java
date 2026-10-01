package mx.clinica.util;

/** Excepción para datos inválidos (ID vacío, duplicado, horario ocupado, etc.). */
public class ValidacionException extends Exception {

    private static final long serialVersionUID = 1L;

    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
