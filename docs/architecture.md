# Arquitectura y UML

## Dependencias

La regla principal es que las capas interiores no conocen Swing ni SQLite:

```mermaid
flowchart TD
    UI[ui] --> SERVICES[service]
    BOOT[bootstrap] --> UI
    BOOT --> SERVICES
    BOOT --> REPOSITORY[repository]
    SERVICES --> DOMAIN[domain]
    SERVICES --> SECURITY[security]
    SERVICES --> REPOSITORY
    SECURITY --> DOMAIN
    REPOSITORY --> DOMAIN
    VALIDATION[validation] --> DOMAIN
    SERVICES --> VALIDATION
```

## Diagrama de clases principal

```mermaid
classDiagram
    class TollApplication
    class LoginFrame
    class DashboardPanel
    class AuthenticationService {
        +authenticate(username, password) UserPrincipal
    }
    class TollService {
        +registerEntry(principal, plate, time)
        +registerExit(principal, plate, time, size) PassageCompletion
        +issueMobileFine(principal, plate, speed, time) Optional~TrafficFine~
        +listTickets(principal, plate) List~TollTicket~
        +listFines(principal, plate) List~TrafficFine~
        +payFine(principal, fineId)
        +exportHistory(principal, plate) Path
    }
    class RoleAuthorizer
    class TollRepository {
        <<interface>>
    }
    class SQLiteTollRepository
    class TollRatePolicy
    class SpeedFinePolicy
    class TollTicket
    class TrafficFine
    class ActivePassage
    class AuditEvent
    class UserAccount
    class UserPrincipal

    TollApplication --> LoginFrame
    LoginFrame --> AuthenticationService
    LoginFrame --> DashboardPanel
    DashboardPanel --> TollService
    AuthenticationService --> TollRepository
    TollService --> TollRepository
    TollService --> RoleAuthorizer
    TollService --> TollRatePolicy
    TollService --> SpeedFinePolicy
    SQLiteTollRepository ..|> TollRepository
    TollRepository --> TollTicket
    TollRepository --> TrafficFine
    TollRepository --> ActivePassage
    TollRepository --> AuditEvent
    TollRepository --> UserAccount
    AuthenticationService --> UserPrincipal
```

## Modelo relacional

```mermaid
erDiagram
    USERS {
        integer id PK
        text username UK
        text password_hash
        text password_salt
        text role
        text license_plate
    }
    ACTIVE_PASSAGES {
        text license_plate PK
        text entered_at
    }
    TICKETS {
        integer id PK
        text license_plate
        real vehicle_size
        text entered_at
        text exited_at
        integer amount_cents
    }
    FINES {
        integer id PK
        text license_plate
        text detected_at
        real speed_kph
        integer amount_cents
        text radar_type
        integer paid
    }
    AUDIT_EVENTS {
        integer id PK
        text occurred_at
        text username
        text role
        text action
        text entity_type
        text detail
    }
```

Los importes se almacenan como céntimos enteros para evitar errores de coma flotante. Las fechas se guardan en ISO-8601 y se convierten a `LocalDateTime` en el borde JDBC.

## Decisiones relevantes

- **SQLite en lugar de serialización:** esquema explícito, transacciones, consultas preparadas e inspección con herramientas estándar.
- **Repositorio como interfaz:** los servicios no dependen de JDBC y pueden probarse con una base temporal.
- **Autorización en servicios:** la UI no constituye una frontera de seguridad fiable.
- **Políticas puras:** tarifas y radares no conocen persistencia ni interfaz, por lo que sus límites se prueban directamente.
- **Composición manual:** el proyecto es suficientemente pequeño para evitar un framework de inyección; `ApplicationContext` hace explícito el cableado.
- **Swing sin Spring Boot:** conserva variedad tecnológica dentro del portfolio y no introduce un servidor innecesario.
