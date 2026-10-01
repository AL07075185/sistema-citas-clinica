package mx.clinica.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/** Lectura y escritura de los archivos CSV de la carpeta db. */
public final class ArchivoCsv {

    public static final String CARPETA_DB = "db";

    private ArchivoCsv() {
    }

    /** Devuelve la ruta de un archivo dentro de la carpeta db. */
    public static String rutaEnDb(String nombreArchivo) {
        return Paths.get(CARPETA_DB, nombreArchivo).toString();
    }

    /** Crea la carpeta db y su .gitignore si no existen. */
    public static void asegurarCarpetaDb() throws IOException {
        Path carpeta = Paths.get(CARPETA_DB);
        if (!Files.isDirectory(carpeta)) {
            Files.createDirectories(carpeta);
            System.out.println("[INFO] Carpeta '" + CARPETA_DB + "' creada.");
        }
        Path gitignore = carpeta.resolve(".gitignore");
        if (!Files.exists(gitignore)) {
            Files.write(gitignore, "*\n!.gitignore\n".getBytes(StandardCharsets.UTF_8));
        }
    }

    /** Crea el archivo con su encabezado si no existe; devuelve true si lo creó. */
    public static boolean asegurarArchivo(String ruta, String encabezado) throws IOException {
        if (Files.exists(Paths.get(ruta))) {
            return false;
        }
        escribir(ruta, encabezado, new ArrayList<>());
        return true;
    }

    /** Copia el archivo a ".respaldo" para no perder líneas que no se pudieron cargar. */
    public static void respaldar(String ruta) {
        Path respaldo = Paths.get(ruta + ".respaldo");
        try {
            Files.copy(Paths.get(ruta), respaldo, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[AVISO] Copia del archivo original guardada en " + respaldo);
        } catch (IOException e) {
            System.out.println("[AVISO] No se pudo respaldar " + ruta + ": " + e.getMessage());
        }
    }

    /** Lee las filas de datos del archivo, sin encabezado ni líneas vacías. */
    public static List<String[]> leer(String ruta) throws IOException {
        List<String[]> filas = new ArrayList<>();
        try (BufferedReader lector = Files.newBufferedReader(Paths.get(ruta), StandardCharsets.UTF_8)) {
            String linea = lector.readLine();
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    filas.add(separarLinea(linea));
                }
            }
        }
        return filas;
    }

    /** Escribe encabezado y filas usando un archivo temporal para no perder datos. */
    public static void escribir(String ruta, String encabezado, List<String[]> filas) throws IOException {
        Path temporal = Paths.get(ruta + ".tmp");
        try (BufferedWriter escritor = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
            escritor.write(encabezado);
            escritor.newLine();
            for (String[] fila : filas) {
                escritor.write(unirCampos(fila));
                escritor.newLine();
            }
        }
        Files.move(temporal, Paths.get(ruta), StandardCopyOption.REPLACE_EXISTING);
    }

    /** Une los campos en una línea CSV, entrecomillando los que tienen comas o comillas. */
    static String unirCampos(String[] campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            String campo = campos[i] == null ? "" : campos[i];
            if (campo.contains(",") || campo.contains("\"")) {
                sb.append('"').append(campo.replace("\"", "\"\"")).append('"');
            } else {
                sb.append(campo);
            }
        }
        return sb.toString();
    }

    /** Separa una línea CSV respetando los campos entre comillas. */
    static String[] separarLinea(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (entreComillas) {
                if (c == '"' && i + 1 < linea.length() && linea.charAt(i + 1) == '"') {
                    actual.append('"');
                    i++;
                } else if (c == '"') {
                    entreComillas = false;
                } else {
                    actual.append(c);
                }
            } else if (c == '"') {
                entreComillas = true;
            } else if (c == ',') {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        campos.add(actual.toString());
        return campos.toArray(new String[0]);
    }
}
