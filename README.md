# Sistema de Gestion de Peajes en Java

Aplicacion de escritorio desarrollada como practica academica de Programacion Orientada a Objetos. Simula un sistema de peajes con registro de vehiculos, generacion de tickets, gestion de multas, radares de tramo/moviles, perfiles de usuario y persistencia local de datos.

El objetivo del repositorio es presentar la practica como un proyecto de portfolio: codigo Java organizado por responsabilidades, interfaz grafica Swing y una vision basica de trazabilidad operativa.

## Funcionalidades

- Interfaz grafica en Java Swing para operar el sistema.
- Flujo separado para conductor, agente y operario.
- Registro de entrada y salida de vehiculos.
- Generacion de tickets de peaje segun tamano del vehiculo y franja horaria.
- Calculo de multas por radar movil y radar de tramo.
- Consulta y pago de multas.
- Exportacion de historiales por matricula.
- Persistencia local mediante serializacion de objetos.
- Datos de prueba sinteticos para validar la aplicacion.

## Enfoque de seguridad

Este no es un proyecto de ciberseguridad ofensiva. Su valor para un perfil orientado a ciberseguridad esta en que modela conceptos que aparecen en sistemas reales:

- Separacion de roles y responsabilidades.
- Trazabilidad de eventos: entradas, salidas, tickets y multas.
- Persistencia y recuperacion de estado.
- Validacion funcional de datos introducidos por la interfaz.
- Generacion de historiales auditables.

Mas detalles en [docs/security-notes.md](docs/security-notes.md).

## Estructura

```text
FicherosFuente/
  CargarInformacion/   Datos de prueba sinteticos
  Datos/               Persistencia del sistema
  Enumeraciones/       Tipos auxiliares
  Escaneres/           Camaras y radares
  IU/                  Interfaz grafica Swing
  Objetos/             Entidades principales
  Sistema/             Nucleo de negocio
  Usuarios/            Roles de uso
docs/
  security-notes.md    Lectura del proyecto desde un perfil cyber
  usage.md             Guia rapida de ejecucion y uso
  uml.pdf              Diagrama UML original
media/
  demo-pruebas.mp4     Video de pruebas de la practica
```

## Ejecucion

Requisitos:

- Java JDK 17 o superior.
- Terminal compatible con `find` y `xargs`, o un IDE Java como NetBeans.

Compilar desde la raiz del repositorio:

```bash
mkdir -p build/classes
find FicherosFuente -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -d build/classes
```

Ejecutar la aplicacion:

```bash
java -cp build/classes practicapeajes.PracticaPeajes
```

Tambien se puede abrir como proyecto Java en NetBeans y ejecutar la clase principal `practicapeajes.PracticaPeajes`.

## Documentacion y demo

- [Guia de uso](docs/usage.md)
- [Notas de seguridad y portfolio](docs/security-notes.md)
- [Diagrama UML](docs/uml.pdf)
- [Video de pruebas](media/demo-pruebas.mp4)

## Datos

Los datos de prueba incluidos en el codigo son sinteticos y no corresponden a vehiculos reales. Los archivos generados en ejecucion, como `peajes.dat` o historiales exportados, se excluyen del control de versiones para evitar subir estado local al repositorio.

## Mejoras futuras

- Sustituir la serializacion binaria por JSON, SQLite o una base de datos embebida.
- Anadir tests unitarios para tarifas, multas y busquedas.
- Centralizar validaciones de entrada.
- Separar mejor la logica de negocio de la interfaz Swing.
- Incorporar autenticacion real por rol si el sistema se ampliase.
