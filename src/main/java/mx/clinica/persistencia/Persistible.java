package mx.clinica.persistencia;

import java.io.IOException;
import java.util.List;

/** Operaciones de guardado y carga en archivo que implementan los gestores. */
public interface Persistible<T> {

    /** Guarda la colección completa en el archivo. */
    void guardar(String ruta) throws IOException;

    /** Carga la colección desde el archivo y la devuelve. */
    List<T> cargar(String ruta) throws IOException;

    /** Devuelve la ruta del archivo CSV del gestor. */
    String getRutaArchivo();
}
