# Notas de seguridad y portfolio

Este repositorio esta pensado como practica academica de Java/POO. Para presentarlo dentro de un portfolio orientado a ciberseguridad conviene explicarlo desde la perspectiva de diseno de sistemas, trazabilidad y gestion de roles.

## Que demuestra

- Modelado de usuarios con responsabilidades distintas: conductor, agente y operario.
- Registro de eventos relevantes: entradas, salidas, tickets, multas y pagos.
- Generacion de historiales consultables por matricula.
- Persistencia local del estado de la aplicacion.
- Separacion entre entidades, logica de negocio, interfaz y carga de datos.

## Relacion con ciberseguridad

El proyecto no implementa hacking, pentesting ni defensa de red. Su relacion con ciberseguridad esta en conceptos base que aparecen en aplicaciones reales:

- Control funcional por rol.
- Auditabilidad de acciones.
- Conservacion de evidencias operativas.
- Validacion de entradas de usuario.
- Proteccion frente a subida accidental de datos runtime mediante `.gitignore`.

## Riesgos conocidos

- La persistencia usa serializacion binaria de Java, valida para una practica academica pero no recomendable como formato principal en sistemas reales.
- No existe autenticacion real de usuarios.
- No hay cifrado ni firma de los datos persistidos.
- La interfaz y la logica de negocio todavia estan bastante acopladas.
- Las validaciones dependen en parte de los formularios Swing.

## Como contarlo en GitHub o CV

Texto corto recomendado:

> Aplicacion Java Swing desarrollada como practica de POO para simular un sistema de peajes con roles, tickets, multas, radares y persistencia local. El proyecto enfatiza separacion de responsabilidades, trazabilidad de eventos y gestion basica de datos.

Texto mas orientado a ciberseguridad:

> Proyecto academico en Java centrado en modelado de roles, trazabilidad operativa y persistencia de eventos dentro de un sistema simulado de peajes. Incluye flujos diferenciados para operario, agente y conductor, generacion de historiales y control de datos generados en ejecucion.
