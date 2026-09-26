# Seguridad y límites

## Controles implementados

- PBKDF2-HMAC-SHA256 con sal aleatoria por usuario y 120.000 iteraciones.
- Mensaje de autenticación uniforme para usuario inexistente y contraseña incorrecta.
- Derivación ficticia cuando el usuario no existe para reducir diferencias temporales triviales.
- Borrado de arrays de contraseña después de usarlos.
- Autorización en la capa de servicio mediante `UserPrincipal` y `UserRole`.
- Restricción de conductores a su matrícula vinculada.
- Consultas SQL preparadas en todas las operaciones.
- Transacciones para pasos, tickets, multas y eventos de auditoría relacionados.
- Lista blanca y normalización de matrículas antes de usarlas en búsquedas o nombres de exportación.
- Logs sin contraseñas ni hashes.
- Exclusión de base de datos, logs y exportaciones del control de versiones.
- Eliminación de la deserialización binaria Java.

## Modelo de permisos

| Operación | Operario | Agente | Conductor |
| --- | :---: | :---: | :---: |
| Registrar entrada/salida | Sí | No | No |
| Emitir multa móvil | No | Sí | No |
| Consultar tickets | Cualquier matrícula | No | Solo propia |
| Consultar multas | Cualquier matrícula | Cualquier matrícula | Solo propia |
| Pagar multa | No | No | Solo propia |
| Exportar historial/auditoría | Sí | No | No |

## Alcance

Es una aplicación local de portfolio, no un sistema de cobro real. No implementa:

- integración con un proveedor de identidad;
- MFA, recuperación de cuenta o rotación de credenciales;
- cifrado de SQLite en reposo;
- firma de eventos de auditoría;
- autorización multi-tenant;
- procesamiento de tarjetas o datos bancarios.

Las credenciales demo deben reemplazarse antes de cualquier despliegue distinto de una demostración local.
