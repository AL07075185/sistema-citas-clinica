package mx.clinica;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import mx.clinica.util.Consola;

/** Punto de entrada del Sistema de Administración de Citas. */
public class Main {

    public static void main(String[] args) {
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, Consola.codificacionConsola()));
        System.out.println("|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");
        System.out.println("   « Sistema de Administración de Citas »");
        System.out.println("   « Consultorio Clínico »");
        System.out.println("|~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~|");

        SistemaClinica sistema = new SistemaClinica(new Consola());
        try {
            sistema.inicializar();
        } catch (IOException e) {
            System.out.println("!Error al preparar la carpeta db: " + e.getMessage());
            return;
        }

        try {
            if (sistema.login()) {
                sistema.mostrarMenu();
            }
        } catch (Consola.EntradaFinalizadaException e) {
            System.out.println("\n « Entrada finalizada. Guardando información...»");
            sistema.guardarTodo();
        }
        System.out.println("« Programa finalizado.»");
    }
}
