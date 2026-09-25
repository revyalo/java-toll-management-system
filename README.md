# Sistema de Gestión de Peajes en Java

Aplicación de escritorio desarrollada como práctica académica de Programación Orientada a Objetos. Simula un sistema de peajes con registro de vehículos, generación de tickets, gestión de multas, radares de tramo/móviles, perfiles de usuario y persistencia local de datos.

El objetivo del repositorio es presentar la práctica como un proyecto de Java orientado a objetos, con interfaz gráfica Swing, modelado de entidades y organización del código por responsabilidades.

## Funcionalidades

- Interfaz gráfica en Java Swing para operar el sistema.
- Flujo separado para conductor, agente y operario.
- Registro de entrada y salida de vehículos.
- Generación de tickets de peaje según tamaño del vehículo y franja horaria.
- Cálculo de multas por radar móvil y radar de tramo.
- Consulta y pago de multas.
- Exportación de historiales por matrícula.
- Persistencia local mediante serialización de objetos.
- Datos de prueba sintéticos para validar la aplicación.

## Conceptos trabajados

- Programación Orientada a Objetos en Java.
- Modelado de entidades y relaciones del dominio.
- Herencia, composición y encapsulación.
- Separación de responsabilidades mediante paquetes.
- Diseño de interfaces gráficas con Swing.
- Gestión de distintos tipos de usuario y flujos de operación.
- Persistencia y recuperación de estado.
- Validación funcional de datos introducidos por la interfaz.
- Generación y consulta de historiales.
- Pruebas básicas de regresión.

## Estructura

```text
FicherosFuente/
  CargarInformacion/   Datos de prueba sintéticos
  Datos/               Persistencia del sistema
  Enumeraciones/       Tipos auxiliares
  Escaneres/           Cámaras y radares
  IU/                  Interfaz gráfica Swing
  Objetos/             Entidades principales
  Sistema/             Núcleo de negocio
  Usuarios/            Tipos de usuario
docs/
  security-notes.md    Notas adicionales sobre seguridad y trazabilidad
  usage.md             Guía rápida de ejecución y uso
  uml.pdf              Diagrama UML original
media/
  demo-pruebas.mp4     Vídeo de pruebas de la práctica
```

## Ejecución

Requisitos:

- Java JDK 17 o superior.
- Terminal compatible con `find` y `xargs`, o un IDE Java como NetBeans.

Compilar desde la raíz del repositorio:

```bash
mkdir -p build/classes
find FicherosFuente -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -d build/classes
```

Ejecutar la aplicación:

```bash
java -cp build/classes practicapeajes.PracticaPeajes
```

También se puede abrir como proyecto Java en NetBeans y ejecutar la clase principal `practicapeajes.PracticaPeajes`.

Ejecutar pruebas básicas de regresión:

```bash
java -cp build/classes practicapeajes.Pruebas.PruebasSistema
```

## Documentación y demo

- [Guía de uso](docs/usage.md)
- [Diagrama UML](docs/uml.pdf)
- [Vídeo de pruebas](media/demo-pruebas.mp4)
- [Notas adicionales de seguridad](docs/security-notes.md)

## Datos

Los datos de prueba incluidos en el código son sintéticos y no corresponden a vehículos reales. Los archivos generados en ejecución, como `peajes.dat` o historiales exportados, se excluyen del control de versiones para evitar subir estado local al repositorio.

## Mejoras futuras

- Sustituir la serialización binaria por JSON, SQLite o una base de datos embebida.
- Añadir tests unitarios para tarifas, multas y búsquedas.
- Centralizar validaciones de entrada.
- Separar mejor la lógica de negocio de la interfaz Swing.
- Mejorar la persistencia y la gestión de configuración.
