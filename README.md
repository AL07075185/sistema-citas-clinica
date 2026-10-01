# Sistema de Administración de Citas – Consultorio Clínico

Programa de consola en Java que simula la administración de citas de un consultorio clínico. Permite dar de alta doctores y pacientes, crear citas con fecha y hora, relacionar cada cita con un doctor y un paciente, y controlar el acceso mediante administradores. Toda la información se guarda en archivos CSV dentro de la carpeta `db`.

Proyecto desarrollado para la evidencia final del curso **Computación en Java** de la Universidad Tecmilenio.

## Instalación y configuración

### Requisitos

| Herramienta | Versión |
|---|---|
| JDK (Java Development Kit) | 11 o superior |
| Apache NetBeans (opcional) | Cualquier versión reciente, incluye Maven |
| Git | Cualquier versión reciente |

### Obtener el código

```bash
git clone https://github.com/AL07075185/sistema-citas-clinica.git
cd sistema-citas-clinica
```

### Abrir y compilar en NetBeans

1. Abrir NetBeans y seleccionar **File > Open Project**.
2. Elegir la carpeta `sistema-citas-clinica` (NetBeans la reconoce como proyecto Maven por el archivo `pom.xml`).
3. Para mostrar acentos y ñ en la ventana Output, ir a **Project Properties > Run > VM Options** y agregar:
   ```
   -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8
   ```
4. Para generar el ejecutable, dar clic derecho al proyecto y elegir **Clean and Build**. El archivo se genera en:
   ```
   target/SistemaCitasClinica.jar
   ```

### Compilar sin NetBeans

Con Maven instalado:

```bash
mvn clean package
```

Solo con el JDK (Windows):

```bat
dir /s /b src\main\java\*.java > fuentes.txt
javac --release 11 -encoding UTF-8 -d build\classes @fuentes.txt
jar cfe SistemaCitasClinica.jar mx.clinica.Main -C build\classes .
```

## Uso del programa

### Ejecución

```bash
java -jar target/SistemaCitasClinica.jar
```

La carpeta `db` se crea en el directorio desde el que se ejecuta el comando. En la primera ejecución el sistema crea la carpeta y los archivos necesarios, y genera el administrador por defecto:

| Usuario | Contraseña |
|---|---|
| `admin` | `1234` |

### Menú principal

| Opción | Función |
|---|---|
| 1. Gestionar doctores | Listar y registrar doctores (ID, nombre completo y especialidad) |
| 2. Gestionar pacientes | Listar y registrar pacientes (ID y nombre completo) |
| 3. Gestionar citas | Listar citas ordenadas por fecha y crear citas relacionadas con un doctor y un paciente |
| 4. Gestionar administradores | Listar y registrar usuarios con acceso al sistema |
| 5. Salir | Guardar la información y cerrar el programa |

### Reglas y validaciones

- Los ID no pueden estar vacíos ni repetirse.
- La fecha de la cita se captura con el formato `DD/MM/AAAA HH:MM` y debe ser futura.
- Un doctor o un paciente no pueden tener dos citas en la misma fecha y hora.
- Se permiten tres intentos de inicio de sesión.
- Si se escribe un dato inválido, el programa muestra el error y continúa.

### Archivos de datos

| Archivo | Contenido |
|---|---|
| `db/doctores.csv` | `id,nombre,especialidad` |
| `db/pacientes.csv` | `id,nombre` |
| `db/citas.csv` | `id,fechaHora,motivo,idDoctor,idPaciente` |
| `db/administradores.csv` | `id,contrasena` |

Los archivos de datos no se suben al repositorio (los excluye `db/.gitignore`). Si alguno no existe, el programa lo regenera automáticamente.

### Estructura del proyecto

```
sistema-citas-clinica
├── pom.xml                  Configuración de Maven y del FAT JAR
├── db/                      Archivos de datos (se regeneran si faltan)
├── Diagramas/               Diagramas de flujo y de clases
├── Pseint/                  Pseudocódigo del avance
└── src/main/java/mx/clinica
    ├── Main.java            Punto de entrada
    ├── SistemaClinica.java  Control de acceso y menús
    ├── modelo/              Persona, Doctor, Paciente, Cita, Administrador
    ├── gestores/            GestorDoctores, GestorPacientes, GestorCitas
    ├── persistencia/        Persistible, ArchivoCsv
    └── util/                Consola, ValidacionException
```

La documentación completa (descripción de clases, diagramas y guías) se encuentra en la [Wiki del proyecto](https://github.com/AL07075185/sistema-citas-clinica/wiki).

## Créditos

- **Autor:** AL07075185 – Matrícula AL07075185
- **Curso:** Computación en Java – Universidad Tecmilenio
- **Herramientas:** Java, Apache NetBeans, Apache Maven, Git y GitHub

## Licencia

Este proyecto se distribuye bajo la licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.
