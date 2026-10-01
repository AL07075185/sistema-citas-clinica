package mx.clinica;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mx.clinica.gestores.GestorCitas;
import mx.clinica.gestores.GestorDoctores;
import mx.clinica.gestores.GestorPacientes;
import mx.clinica.modelo.Administrador;
import mx.clinica.modelo.Cita;
import mx.clinica.modelo.Doctor;
import mx.clinica.modelo.Paciente;
import mx.clinica.persistencia.ArchivoCsv;
import mx.clinica.persistencia.Persistible;
import mx.clinica.util.Consola;
import mx.clinica.util.ValidacionException;

/** Coordina gestores, control de acceso y menús del sistema. */
public class SistemaClinica {

    private static final String ARCHIVO_ADMINS = ArchivoCsv.rutaEnDb("administradores.csv");
    private static final String ENCABEZADO_ADMINS = "id,contrasena";
    private static final int MAX_INTENTOS_LOGIN = 3;

    private final GestorDoctores gestorDoctores;
    private final GestorPacientes gestorPacientes;
    private final GestorCitas gestorCitas;
    private final List<Administrador> administradores = new ArrayList<>();
    private final Consola consola;
    private boolean autenticado;
    private String adminActual;

    public SistemaClinica(Consola consola) {
        this.consola = consola;
        this.gestorDoctores = new GestorDoctores(ArchivoCsv.rutaEnDb("doctores.csv"));
        this.gestorPacientes = new GestorPacientes(ArchivoCsv.rutaEnDb("pacientes.csv"));
        this.gestorCitas = new GestorCitas(ArchivoCsv.rutaEnDb("citas.csv"), gestorDoctores, gestorPacientes);
    }

    /** Valida la carpeta db y carga todos los archivos (las citas al final). */
    public void inicializar() throws IOException {
        ArchivoCsv.asegurarCarpetaDb();
        cargarAdministradores();
        for (Persistible<?> gestor : getGestores()) {
            gestor.cargar(gestor.getRutaArchivo());
        }
        System.out.println("[INFO] Datos cargados: "
                + gestorDoctores.listarDoctores().size() + " doctores, "
                + gestorPacientes.listarPacientes().size() + " pacientes, "
                + gestorCitas.listarCitas().size() + " citas.");
    }

    private List<Persistible<?>> getGestores() {
        return Arrays.asList(gestorDoctores, gestorPacientes, gestorCitas);
    }

    /** Guarda toda la información del sistema. */
    public void guardarTodo() {
        for (Persistible<?> gestor : getGestores()) {
            guardarGestor(gestor);
        }
        guardarAdministradores();
    }

    /** Guarda un gestor; si falla muestra el error y devuelve false. */
    private boolean guardarGestor(Persistible<?> gestor) {
        try {
            gestor.guardar(gestor.getRutaArchivo());
            return true;
        } catch (IOException e) {
            System.out.println("!Error al guardar " + gestor.getRutaArchivo() + ": " + e.getMessage());
            return false;
        }
    }

    /** Carga los administradores; si no hay ninguno crea admin / 1234. */
    private void cargarAdministradores() throws IOException {
        boolean regenerado = ArchivoCsv.asegurarArchivo(ARCHIVO_ADMINS, ENCABEZADO_ADMINS);
        administradores.clear();
        for (String[] campos : ArchivoCsv.leer(ARCHIVO_ADMINS)) {
            if (campos.length >= 2 && !campos[0].trim().isEmpty()) {
                administradores.add(new Administrador(campos[0].trim(), campos[1].trim()));
            }
        }
        if (administradores.isEmpty()) {
            administradores.add(new Administrador("admin", "1234"));
            guardarAdministradores();
            System.out.println("[INFO] " + (regenerado ? "Archivo de administradores regenerado. " : "")
                    + "Administrador por defecto: admin / 1234");
        }
    }

    private boolean guardarAdministradores() {
        List<String[]> filas = new ArrayList<>();
        for (Administrador a : administradores) {
            filas.add(a.aCamposCsv());
        }
        try {
            ArchivoCsv.escribir(ARCHIVO_ADMINS, ENCABEZADO_ADMINS, filas);
            return true;
        } catch (IOException e) {
            System.out.println("!Error al guardar administradores: " + e.getMessage());
            return false;
        }
    }

    /** Verifica las credenciales contra la lista de administradores. */
    public boolean autenticar(String id, String contrasena) {
        for (Administrador a : administradores) {
            if (a.autenticar(id, contrasena)) {
                autenticado = true;
                adminActual = a.getId();
                return true;
            }
        }
        autenticado = false;
        return false;
    }

    /** Pantalla de inicio de sesión con máximo de intentos. */
    public boolean login() {
        for (int intento = 1; intento <= MAX_INTENTOS_LOGIN; intento++) {
            if (intento > 1) {
                consola.limpiarPantalla();
            }
            System.out.println("\n~~~ « Inicio de Sesión » ~~~");
            String id = consola.leerTexto("ID de administrador: ");
            String pass = consola.leerContrasena("Contraseña: ");
            if (autenticar(id, pass)) {
                return true;
            }
            System.out.println("!Error: acceso denegado. Intentos restantes: " + (MAX_INTENTOS_LOGIN - intento));
            if (intento < MAX_INTENTOS_LOGIN) {
                consola.pausar();
            }
        }
        System.out.println("Se agotaron los intentos. El programa se cerrará.");
        return false;
    }

    public boolean isAutenticado() {
        return autenticado;
    }

    /** Menú principal del sistema. */
    public void mostrarMenu() {
        int opcion = 0;
        while (opcion != 5) {
            consola.limpiarPantalla();
            System.out.println("~~~ « Menú Principal » ~~~ (usuario: " + adminActual + ")");
            System.out.println("1. Gestionar doctores");
            System.out.println("2. Gestionar pacientes");
            System.out.println("3. Gestionar citas");
            System.out.println("4. Gestionar administradores");
            System.out.println("5. Salir");
            opcion = consola.leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1:
                        menuDoctores();
                        break;
                    case 2:
                        menuPacientes();
                        break;
                    case 3:
                        menuCitas();
                        break;
                    case 4:
                        menuAdministradores();
                        break;
                    case 5:
                        consola.limpiarPantalla();
                        System.out.println("« Sesión cerrada. Guardando información... »");
                        guardarTodo();
                        break;
                    default:
                        System.out.println("Opción no válida.");
                        consola.pausar();
                }
            } catch (Consola.EntradaFinalizadaException e) {
                throw e;
            } catch (RuntimeException e) {
                System.out.println("!Error inesperado: " + e.getMessage());
                consola.pausar();
            }
        }
    }

    /** Muestra un submenú estándar (listar / registrar / regresar) y devuelve la opción. */
    private int leerOpcionSubmenu(String titulo, String listar, String registrar) {
        consola.limpiarPantalla();
        System.out.println("~~~ « " + titulo + " » ~~~");
        System.out.println("1. " + listar);
        System.out.println("2. " + registrar);
        System.out.println("3. Regresar al menú principal");
        int opc = consola.leerEntero("Seleccione una opción: ");
        if (opc < 1 || opc > 3) {
            System.out.println("!Opción no válida.");
            consola.pausar();
        }
        return opc;
    }

    public void menuDoctores() {
        int opc = 0;
        while (opc != 3) {
            opc = leerOpcionSubmenu("Módulo Doctores", "Listar doctores", "Registrar doctor");
            if (opc == 1) {
                consola.limpiarPantalla();
                listarDoctores();
                consola.pausar();
            } else if (opc == 2) {
                registrarDoctores();
            }
        }
    }

    private void listarDoctores() {
        System.out.println("~~~ « Lista de Doctores » ~~~");
        List<Doctor> lista = gestorDoctores.listarDoctores();
        if (lista.isEmpty()) {
            System.out.println("No hay doctores registrados.");
            return;
        }
        System.out.println("ID | Nombre | Especialidad");
        for (Doctor d : lista) {
            System.out.println(d);
        }
    }

    private void registrarDoctores() {
        boolean continuar = true;
        while (continuar) {
            consola.limpiarPantalla();
            System.out.println("~~~ « Registrar Doctor » ~~~");
            String id = leerIdNuevo("Ingrese ID único del doctor: ", gestorDoctores::existeId);
            String nombre = consola.leerTextoObligatorio("Ingrese nombre completo: ", "el nombre");
            String especialidad = consola.leerTextoObligatorio("Ingrese especialidad: ", "la especialidad");
            try {
                Doctor doctor = new Doctor(id, nombre, especialidad);
                gestorDoctores.agregarDoctor(doctor);
                if (guardarGestor(gestorDoctores)) {
                    System.out.println("\nDoctor registrado exitosamente: " + doctor);
                }
            } catch (ValidacionException e) {
                System.out.println("!Error: " + e.getMessage());
            }
            continuar = consola.confirmar("\n¿Registrar otro doctor? (S/N): ");
        }
    }

    public void menuPacientes() {
        int opc = 0;
        while (opc != 3) {
            opc = leerOpcionSubmenu("Módulo Pacientes", "Listar pacientes", "Registrar paciente");
            if (opc == 1) {
                consola.limpiarPantalla();
                listarPacientes();
                consola.pausar();
            } else if (opc == 2) {
                registrarPacientes();
            }
        }
    }

    private void listarPacientes() {
        System.out.println("~~~ « Lista de Pacientes » ~~~");
        List<Paciente> lista = gestorPacientes.listarPacientes();
        if (lista.isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        System.out.println("ID | Nombre");
        for (Paciente p : lista) {
            System.out.println(p);
        }
    }

    private void registrarPacientes() {
        boolean continuar = true;
        while (continuar) {
            consola.limpiarPantalla();
            System.out.println("~~~ « Registrar Paciente » ~~~");
            String id = leerIdNuevo("Ingrese ID único del paciente: ", gestorPacientes::existeId);
            String nombre = consola.leerTextoObligatorio("Ingrese nombre completo: ", "el nombre");
            try {
                Paciente paciente = new Paciente(id, nombre);
                gestorPacientes.agregarPaciente(paciente);
                if (guardarGestor(gestorPacientes)) {
                    System.out.println("\nPaciente registrado exitosamente: " + paciente);
                }
            } catch (ValidacionException e) {
                System.out.println("!Error: " + e.getMessage());
            }
            continuar = consola.confirmar("\n¿Registrar otro paciente? (S/N): ");
        }
    }

    public void menuCitas() {
        int opc = 0;
        while (opc != 3) {
            opc = leerOpcionSubmenu("Módulo Citas", "Listar citas", "Crear cita");
            if (opc == 1) {
                consola.limpiarPantalla();
                listarCitas();
                consola.pausar();
            } else if (opc == 2) {
                crearCitas();
            }
        }
    }

    private void listarCitas() {
        System.out.println("~~~ « Lista de Citas » ~~~");
        List<Cita> lista = gestorCitas.listarCitas();
        if (lista.isEmpty()) {
            System.out.println("No hay citas registradas.");
            return;
        }
        System.out.println("ID | Fecha y hora | Motivo | Doctor | Paciente");
        for (Cita c : lista) {
            System.out.println(c);
        }
    }

    private void crearCitas() {
        if (gestorDoctores.listarDoctores().isEmpty()) {
            System.out.println("!Error: no hay doctores registrados. Registre un doctor primero.");
            consola.pausar();
            return;
        }
        if (gestorPacientes.listarPacientes().isEmpty()) {
            System.out.println("!Error: no hay pacientes registrados. Registre un paciente primero.");
            consola.pausar();
            return;
        }
        boolean continuar = true;
        while (continuar) {
            consola.limpiarPantalla();
            System.out.println("~~~ « Crear Cita » ~~~");
            String id = leerIdNuevo("Ingrese ID único de la cita: ", gestorCitas::existeId);
            LocalDateTime fecha = leerFechaFutura();
            String motivo = consola.leerTextoObligatorio("Ingrese motivo de la cita: ", "el motivo");
            System.out.println();
            listarDoctores();
            Doctor doctor = seleccionarDoctor();
            Paciente paciente = null;
            if (doctor != null) {
                System.out.println();
                listarPacientes();
                paciente = seleccionarPaciente();
            }
            if (doctor == null || paciente == null) {
                System.out.println("Creación de cita cancelada.");
            } else {
                try {
                    Cita cita = new Cita(id, fecha, motivo, doctor, paciente);
                    gestorCitas.agregarCita(cita);
                    if (guardarGestor(gestorCitas)) {
                        System.out.println("\nCita creada exitosamente:\n" + cita);
                    }
                } catch (ValidacionException e) {
                    System.out.println("!Error: " + e.getMessage());
                }
            }
            continuar = consola.confirmar("\n¿Crear otra cita? (S/N): ");
        }
    }

    /** Pide la fecha y hora hasta que sea válida y futura. */
    private LocalDateTime leerFechaFutura() {
        while (true) {
            LocalDateTime fecha = consola.leerFechaHora("Ingrese fecha y hora (DD/MM/AAAA HH:MM): ", Cita.FORMATO_FECHA);
            if (fecha.isAfter(LocalDateTime.now())) {
                return fecha;
            }
            System.out.println("!Error: la cita debe programarse en una fecha y hora futura.");
        }
    }

    /** Pide el ID del doctor hasta encontrarlo; Enter vacío cancela. */
    private Doctor seleccionarDoctor() {
        while (true) {
            String id = consola.leerTexto("Ingrese ID del doctor (Enter para cancelar): ");
            if (id.isEmpty()) {
                return null;
            }
            Doctor d = gestorDoctores.buscarPorId(id);
            if (d != null) {
                return d;
            }
            System.out.println("!Error: doctor no encontrado. Intente de nuevo.");
        }
    }

    /** Pide el ID del paciente hasta encontrarlo; Enter vacío cancela. */
    private Paciente seleccionarPaciente() {
        while (true) {
            String id = consola.leerTexto("Ingrese ID del paciente (Enter para cancelar): ");
            if (id.isEmpty()) {
                return null;
            }
            Paciente p = gestorPacientes.buscarPorId(id);
            if (p != null) {
                return p;
            }
            System.out.println("!Error: paciente no encontrado. Intente de nuevo.");
        }
    }

    public void menuAdministradores() {
        int opc = 0;
        while (opc != 3) {
            opc = leerOpcionSubmenu("Módulo Administradores", "Listar administradores", "Registrar administrador");
            if (opc == 1) {
                consola.limpiarPantalla();
                System.out.println("~~~ « Administradores » ~~~");
                for (Administrador a : administradores) {
                    System.out.println(a.getId());
                }
                consola.pausar();
            } else if (opc == 2) {
                consola.limpiarPantalla();
                registrarAdministrador();
                consola.pausar();
            }
        }
    }

    private void registrarAdministrador() {
        System.out.println("~~~ « Registrar Administrador » ~~~");
        String id = leerIdNuevo("Ingrese ID del nuevo administrador: ", this::existeAdministrador);
        String pass = consola.leerTextoObligatorio("Ingrese contraseña: ", "la contraseña");
        if (pass.length() < 4) {
            System.out.println("!Error: la contraseña debe tener al menos 4 caracteres.");
            return;
        }
        administradores.add(new Administrador(id, pass));
        if (guardarAdministradores()) {
            System.out.println("Administrador '" + id + "' registrado exitosamente.");
        }
    }

    private boolean existeAdministrador(String id) {
        for (Administrador a : administradores) {
            if (a.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    /** Interfaz funcional para validar si un ID ya existe en cualquier módulo. */
    private interface VerificadorId {
        boolean existe(String id);
    }

    /** Pide un ID hasta que no esté vacío, no tenga comas y no esté repetido. */
    private String leerIdNuevo(String mensaje, VerificadorId verificador) {
        while (true) {
            String id = consola.leerTexto(mensaje);
            if (id.isEmpty()) {
                System.out.println("!Error: el ID no puede estar vacío.");
            } else if (id.contains(",")) {
                System.out.println("!Error: el ID no puede contener comas.");
            } else if (verificador.existe(id)) {
                System.out.println("!Error: ID duplicado. Intente con otro.");
            } else {
                return id;
            }
        }
    }
}
