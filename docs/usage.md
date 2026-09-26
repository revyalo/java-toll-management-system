# Guía de uso

## Inicio

```bash
mvn clean verify
mvn exec:java -Dexec.mainClass=com.revyalo.toll.TollApplication
```

El primer arranque crea la base SQLite y los usuarios de demostración documentados en el README.

## Flujo de operario

1. Inicia sesión como `operator`.
2. Registra una entrada indicando matrícula.
3. Registra la salida posterior indicando matrícula y tamaño.
4. El servicio genera el ticket y, si el trayecto de 100 km implica más de 120 km/h, una multa de tramo.
5. Consulta los registros o exporta el historial CSV.

## Flujo de agente

1. Inicia sesión como `agent`.
2. Introduce matrícula y velocidad detectada.
3. Si la velocidad supera 120 km/h se crea una multa móvil; en caso contrario no se altera la base de datos.

## Flujo de conductor

1. Inicia sesión como `driver`.
2. Consulta tickets o multas de la matrícula asociada.
3. Introduce el ID de una multa pendiente para abonarla.

El conductor no puede consultar matrículas distintas aunque se invoque el servicio fuera de Swing.

## Archivos runtime

```text
data/
├── tolls.db
├── exports/
│   └── 1234ABC-YYYYMMDD-HHMMSS.csv
└── logs/
    └── toll-system.log
```

Todo el directorio se ignora en Git.
