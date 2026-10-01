package mx.clinica.util;

import java.io.Console;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/** Lectura de datos del teclado con validación y utilidades de pantalla. */
public class Consola {

    private static final int LINEAS_LIMPIAR = 40;

    private final Scanner scanner;

    public Consola() {
        this.scanner = new Scanner(System.in, codificacionConsola());
    }

    /** Detecta la codificación de la consola (CMD, PowerShell o NetBeans) para leer acentos y ñ. */
    public static Charset codificacionConsola() {
        String[] propiedades = {"stdout.encoding", "sun.stdout.encoding", "stdin.encoding"};
        for (String propiedad : propiedades) {
            String valor = System.getProperty(propiedad);
            if (valor != null && Charset.isSupported(valor)
                    && !Charset.forName(valor).equals(StandardCharsets.US_ASCII)) {
                return Charset.forName(valor);
            }
        }
        return StandardCharsets.UTF_8;
    }

    /** Lee una línea de texto sin espacios al inicio ni al final. */
    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            throw new EntradaFinalizadaException();
        }
        return scanner.nextLine().trim();
    }

    /** Lee un texto que no puede quedar vacío. */
    public String leerTextoObligatorio(String mensaje, String nombreCampo) {
        while (true) {
            String valor = leerTexto(mensaje);
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("Error: " + nombreCampo + " no puede estar vacío.");
        }
    }

    /** Lee un número entero; devuelve -1 si el texto no es un número. */
    public int leerEntero(String mensaje) {
        String texto = leerTexto(mensaje);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            System.out.println("Error: '" + texto + "' no es un número válido.");
            return -1;
        }
    }

    /** Lee una fecha y hora con el formato indicado hasta que sea válida. */
    public LocalDateTime leerFechaHora(String mensaje, DateTimeFormatter formato) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return LocalDateTime.parse(texto, formato);
            } catch (DateTimeParseException e) {
                System.out.println("Error: fecha u hora inválida. Use DD/MM/AAAA HH:MM (ej. 15/10/2026 09:30).");
            }
        }
    }

    /** Pregunta S/N y devuelve true si la respuesta es S. */
    public boolean confirmar(String mensaje) {
        return leerTexto(mensaje).equalsIgnoreCase("S");
    }

    /** Lee la contraseña ocultándola cuando se ejecuta en una terminal real. */
    public String leerContrasena(String mensaje) {
        if (esTerminal()) {
            char[] pass = System.console().readPassword(mensaje);
            if (pass == null) {
                throw new EntradaFinalizadaException();
            }
            return new String(pass).trim();
        }
        return leerTexto(mensaje);
    }

    public void pausar() {
        leerTexto("\nPresione Enter para continuar...");
    }

    /** Limpia la pantalla: cls en Windows, código ANSI en Linux/macOS, saltos de línea en el IDE. */
    public void limpiarPantalla() {
        if (esTerminal()) {
            try {
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
                } else {
                    System.out.print("\033[H\033[2J");
                    System.out.flush();
                }
                return;
            } catch (IOException e) {
                System.out.println();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        for (int i = 0; i < LINEAS_LIMPIAR; i++) {
            System.out.println();
        }
    }

    /** Devuelve true si el programa corre en una terminal real y no en la consola del IDE. */
    private static boolean esTerminal() {
        Console consola = System.console();
        if (consola == null) {
            return false;
        }
        try {
            Method isTerminal = Console.class.getMethod("isTerminal");
            return (Boolean) isTerminal.invoke(consola);
        } catch (NoSuchMethodException e) {
            return true;
        } catch (ReflectiveOperationException e) {
            return false;
        }
    }

    /** Se lanza cuando ya no hay más entrada disponible. */
    public static class EntradaFinalizadaException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public EntradaFinalizadaException() {
            super("No hay más datos de entrada.");
        }
    }
}
