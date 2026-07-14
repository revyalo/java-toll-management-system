# Guia de uso

## Arranque

Compila y ejecuta desde la raiz del repositorio:

```bash
mkdir -p build/classes
find FicherosFuente -name "*.java" -print0 | xargs -0 javac -encoding UTF-8 -d build/classes
java -cp build/classes practicapeajes.PracticaPeajes
```

La aplicacion abre una interfaz Swing desde la que se accede a los perfiles disponibles.

## Roles

### Operario

Permite registrar entradas y salidas de vehiculos, generar tickets, consultar multas y exportar historiales de peaje asociados a una matricula.

Flujo recomendado:

1. Registrar la entrada del vehiculo con su matricula.
2. Registrar la salida con la misma matricula y el tamano del vehiculo.
3. Generar o consultar el ticket correspondiente.

### Agente

Permite generar multas asociadas a una matricula cuando se detecta un exceso de velocidad.

### Conductor

Permite consultar multas asociadas a una matricula y pagar multas pendientes mediante su identificador.

## Formato de datos

- En campos numericos con decimales se debe usar punto (`.`), no coma.
- Las matriculas usadas en los datos de prueba son ficticias.
- Los historiales exportados se guardan en `FicherosDatos/`.
- El estado persistente se guarda en `FicherosDatos/peajes.dat`.

## Datos de prueba

La clase `practicapeajes.Ficheros.CargarInformacion` genera datos sinteticos para probar tickets, multas pagadas, multas pendientes y consultas por matricula.

Para ejecutarla despues de compilar:

```bash
java -cp build/classes practicapeajes.Ficheros.CargarInformacion
```
