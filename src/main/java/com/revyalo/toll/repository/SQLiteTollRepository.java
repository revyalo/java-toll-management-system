package com.revyalo.toll.repository;

import com.revyalo.toll.domain.ActivePassage;
import com.revyalo.toll.domain.AuditEvent;
import com.revyalo.toll.domain.PassageCompletion;
import com.revyalo.toll.domain.RadarType;
import com.revyalo.toll.domain.TollTicket;
import com.revyalo.toll.domain.TrafficFine;
import com.revyalo.toll.domain.UserAccount;
import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.exception.DuplicateActivePassageException;
import com.revyalo.toll.exception.EntityNotFoundException;
import com.revyalo.toll.exception.PersistenceException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class SQLiteTollRepository implements TollRepository {
    private final String jdbcUrl;

    public SQLiteTollRepository(String jdbcUrl) {
        if (jdbcUrl == null || !jdbcUrl.startsWith("jdbc:sqlite:")) {
            throw new IllegalArgumentException("Se esperaba una URL JDBC de SQLite");
        }
        this.jdbcUrl = jdbcUrl;
    }

    @Override
    public void initialize() {
        try (Connection connection = open(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL COLLATE NOCASE UNIQUE,
                    password_hash TEXT NOT NULL,
                    password_salt TEXT NOT NULL,
                    role TEXT NOT NULL CHECK (role IN ('OPERATOR', 'AGENT', 'DRIVER')),
                    license_plate TEXT
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS active_passages (
                    license_plate TEXT PRIMARY KEY COLLATE NOCASE,
                    entered_at TEXT NOT NULL
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tickets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    license_plate TEXT NOT NULL COLLATE NOCASE,
                    vehicle_size REAL NOT NULL CHECK (vehicle_size > 0),
                    entered_at TEXT NOT NULL,
                    exited_at TEXT NOT NULL,
                    amount_cents INTEGER NOT NULL CHECK (amount_cents >= 0)
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS fines (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    license_plate TEXT NOT NULL COLLATE NOCASE,
                    detected_at TEXT NOT NULL,
                    speed_kph REAL NOT NULL CHECK (speed_kph > 0),
                    amount_cents INTEGER NOT NULL CHECK (amount_cents >= 0),
                    radar_type TEXT NOT NULL CHECK (radar_type IN ('SECTION', 'MOBILE')),
                    paid INTEGER NOT NULL DEFAULT 0 CHECK (paid IN (0, 1))
                )
                """);
            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS audit_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    occurred_at TEXT NOT NULL,
                    username TEXT NOT NULL,
                    role TEXT NOT NULL,
                    action TEXT NOT NULL,
                    entity_type TEXT NOT NULL,
                    detail TEXT NOT NULL
                )
                """);
            statement.executeUpdate(
                "CREATE INDEX IF NOT EXISTS idx_tickets_plate ON tickets(license_plate)"
            );
            statement.executeUpdate(
                "CREATE INDEX IF NOT EXISTS idx_fines_plate_paid ON fines(license_plate, paid)"
            );
            statement.executeUpdate(
                "CREATE INDEX IF NOT EXISTS idx_audit_time ON audit_events(occurred_at DESC)"
            );
            statement.execute("PRAGMA user_version = 1");
        } catch (SQLException exception) {
            throw persistence("No se pudo inicializar la base de datos", exception);
        }
    }

    @Override
    public boolean hasUsers() {
        String sql = "SELECT EXISTS(SELECT 1 FROM users)";
        try (Connection connection = open();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            return result.next() && result.getInt(1) == 1;
        } catch (SQLException exception) {
            throw persistence("No se pudo consultar usuarios", exception);
        }
    }

    @Override
    public void saveUser(UserAccount account) {
        String sql = """
            INSERT INTO users(username, password_hash, password_salt, role, license_plate)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (Connection connection = open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, account.username());
            statement.setString(2, account.passwordHash());
            statement.setString(3, account.passwordSalt());
            statement.setString(4, account.role().name());
            statement.setString(5, account.licensePlate());
            statement.executeUpdate();
        } catch (SQLException exception) {
            throw persistence("No se pudo guardar el usuario", exception);
        }
    }

    @Override
    public Optional<UserAccount> findUserByUsername(String username) {
        String sql = """
            SELECT id, username, password_hash, password_salt, role, license_plate
            FROM users WHERE username = ? COLLATE NOCASE
            """;
        try (Connection connection = open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                return Optional.of(new UserAccount(
                    result.getLong("id"),
                    result.getString("username"),
                    result.getString("password_hash"),
                    result.getString("password_salt"),
                    UserRole.valueOf(result.getString("role")),
                    result.getString("license_plate")
                ));
            }
        } catch (SQLException exception) {
            throw persistence("No se pudo consultar el usuario", exception);
        }
    }

    @Override
    public void createActivePassage(ActivePassage passage, AuditEvent auditEvent) {
        transaction(connection -> {
            String sql = "INSERT INTO active_passages(license_plate, entered_at) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, passage.licensePlate());
                statement.setString(2, passage.enteredAt().toString());
                statement.executeUpdate();
            } catch (SQLException exception) {
                if (exception.getErrorCode() == 19) {
                    throw new DuplicateActivePassageException(
                        "Ya existe una entrada activa para " + passage.licensePlate()
                    );
                }
                throw exception;
            }
            insertAudit(connection, auditEvent);
            return null;
        });
    }

    @Override
    public Optional<ActivePassage> findActivePassage(String licensePlate) {
        String sql = "SELECT license_plate, entered_at FROM active_passages WHERE license_plate = ?";
        try (Connection connection = open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, licensePlate);
            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return Optional.empty();
                }
                return Optional.of(new ActivePassage(
                    result.getString("license_plate"),
                    LocalDateTime.parse(result.getString("entered_at"))
                ));
            }
        } catch (SQLException exception) {
            throw persistence("No se pudo consultar la entrada activa", exception);
        }
    }

    @Override
    public PassageCompletion completePassage(
        TollTicket ticket,
        Optional<TrafficFine> sectionFine,
        AuditEvent auditEvent
    ) {
        return transaction(connection -> {
            try (PreparedStatement delete = connection.prepareStatement(
                "DELETE FROM active_passages WHERE license_plate = ?"
            )) {
                delete.setString(1, ticket.licensePlate());
                if (delete.executeUpdate() != 1) {
                    throw new EntityNotFoundException(
                        "No existe una entrada activa para " + ticket.licensePlate()
                    );
                }
            }
            TollTicket savedTicket = insertTicket(connection, ticket);
            Optional<TrafficFine> savedFine = sectionFine.map(fine -> insertFine(connection, fine));
            insertAudit(connection, auditEvent);
            return new PassageCompletion(savedTicket, savedFine);
        });
    }

    @Override
    public TrafficFine createFine(TrafficFine fine, AuditEvent auditEvent) {
        return transaction(connection -> {
            TrafficFine saved = insertFine(connection, fine);
            insertAudit(connection, auditEvent);
            return saved;
        });
    }

    @Override
    public List<TollTicket> findTicketsByPlate(String licensePlate) {
        String sql = """
            SELECT id, license_plate, vehicle_size, entered_at, exited_at, amount_cents
            FROM tickets WHERE license_plate = ? ORDER BY exited_at DESC, id DESC
            """;
        List<TollTicket> tickets = new ArrayList<>();
        try (Connection connection = open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, licensePlate);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    tickets.add(mapTicket(result));
                }
            }
            return List.copyOf(tickets);
        } catch (SQLException exception) {
            throw persistence("No se pudieron consultar los tickets", exception);
        }
    }

    @Override
    public List<TrafficFine> findFinesByPlate(String licensePlate) {
        String sql = """
            SELECT id, license_plate, detected_at, speed_kph, amount_cents, radar_type, paid
            FROM fines WHERE license_plate = ? ORDER BY paid ASC, detected_at DESC, id DESC
            """;
        List<TrafficFine> fines = new ArrayList<>();
        try (Connection connection = open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, licensePlate);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    fines.add(mapFine(result));
                }
            }
            return List.copyOf(fines);
        } catch (SQLException exception) {
            throw persistence("No se pudieron consultar las multas", exception);
        }
    }

    @Override
    public boolean payFine(long fineId, String licensePlate, AuditEvent auditEvent) {
        return transaction(connection -> {
            String sql = "UPDATE fines SET paid = 1 WHERE id = ? AND license_plate = ? AND paid = 0";
            int updated;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setLong(1, fineId);
                statement.setString(2, licensePlate);
                updated = statement.executeUpdate();
            }
            if (updated == 1) {
                insertAudit(connection, auditEvent);
                return true;
            }
            return false;
        });
    }

    @Override
    public List<AuditEvent> findRecentAuditEvents(int limit) {
        if (limit < 1 || limit > 1_000) {
            throw new IllegalArgumentException("El límite de auditoría debe estar entre 1 y 1000");
        }
        String sql = """
            SELECT occurred_at, username, role, action, entity_type, detail
            FROM audit_events ORDER BY occurred_at DESC, id DESC LIMIT ?
            """;
        List<AuditEvent> events = new ArrayList<>();
        try (Connection connection = open(); PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, limit);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    events.add(new AuditEvent(
                        LocalDateTime.parse(result.getString("occurred_at")),
                        result.getString("username"),
                        UserRole.valueOf(result.getString("role")),
                        result.getString("action"),
                        result.getString("entity_type"),
                        result.getString("detail")
                    ));
                }
            }
            return List.copyOf(events);
        } catch (SQLException exception) {
            throw persistence("No se pudo consultar la auditoría", exception);
        }
    }

    private TollTicket insertTicket(Connection connection, TollTicket ticket) throws SQLException {
        String sql = """
            INSERT INTO tickets(license_plate, vehicle_size, entered_at, exited_at, amount_cents)
            VALUES (?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, ticket.licensePlate());
            statement.setDouble(2, ticket.vehicleSize());
            statement.setString(3, ticket.enteredAt().toString());
            statement.setString(4, ticket.exitedAt().toString());
            statement.setLong(5, toCents(ticket.amount()));
            statement.executeUpdate();
            return ticket.withId(generatedId(statement));
        }
    }

    private TrafficFine insertFine(Connection connection, TrafficFine fine) {
        String sql = """
            INSERT INTO fines(license_plate, detected_at, speed_kph, amount_cents, radar_type, paid)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, fine.licensePlate());
            statement.setString(2, fine.detectedAt().toString());
            statement.setDouble(3, fine.speedKph());
            statement.setLong(4, toCents(fine.amount()));
            statement.setString(5, fine.radarType().name());
            statement.setInt(6, fine.paid() ? 1 : 0);
            statement.executeUpdate();
            return fine.withId(generatedId(statement));
        } catch (SQLException exception) {
            throw persistence("No se pudo guardar la multa", exception);
        }
    }

    private void insertAudit(Connection connection, AuditEvent event) throws SQLException {
        String sql = """
            INSERT INTO audit_events(occurred_at, username, role, action, entity_type, detail)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, event.occurredAt().toString());
            statement.setString(2, event.username());
            statement.setString(3, event.role().name());
            statement.setString(4, event.action());
            statement.setString(5, event.entityType());
            statement.setString(6, event.detail());
            statement.executeUpdate();
        }
    }

    private TollTicket mapTicket(ResultSet result) throws SQLException {
        return new TollTicket(
            result.getLong("id"),
            result.getString("license_plate"),
            result.getDouble("vehicle_size"),
            LocalDateTime.parse(result.getString("entered_at")),
            LocalDateTime.parse(result.getString("exited_at")),
            fromCents(result.getLong("amount_cents"))
        );
    }

    private TrafficFine mapFine(ResultSet result) throws SQLException {
        return new TrafficFine(
            result.getLong("id"),
            result.getString("license_plate"),
            LocalDateTime.parse(result.getString("detected_at")),
            result.getDouble("speed_kph"),
            fromCents(result.getLong("amount_cents")),
            RadarType.valueOf(result.getString("radar_type")),
            result.getInt("paid") == 1
        );
    }

    private Connection open() throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try {
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
                statement.execute("PRAGMA busy_timeout = 5000");
            }
            return connection;
        } catch (SQLException exception) {
            try {
                connection.close();
            } catch (SQLException closeFailure) {
                exception.addSuppressed(closeFailure);
            }
            throw exception;
        }
    }

    private long generatedId(PreparedStatement statement) throws SQLException {
        try (ResultSet keys = statement.getGeneratedKeys()) {
            if (!keys.next()) {
                throw new SQLException("SQLite no devolvió la clave generada");
            }
            return keys.getLong(1);
        }
    }

    private long toCents(BigDecimal amount) {
        return amount.movePointRight(2).longValueExact();
    }

    private BigDecimal fromCents(long cents) {
        return BigDecimal.valueOf(cents, 2);
    }

    private <T> T transaction(SqlWork<T> work) {
        try (Connection connection = open()) {
            connection.setAutoCommit(false);
            try {
                T result = work.execute(connection);
                connection.commit();
                return result;
            } catch (SQLException exception) {
                rollback(connection, exception);
                throw persistence("Falló una transacción SQLite", exception);
            } catch (RuntimeException exception) {
                rollback(connection, exception);
                throw exception;
            }
        } catch (SQLException exception) {
            throw persistence("No se pudo abrir la transacción SQLite", exception);
        }
    }

    private void rollback(Connection connection, Exception original) {
        try {
            connection.rollback();
        } catch (SQLException rollbackFailure) {
            original.addSuppressed(rollbackFailure);
        }
    }

    private PersistenceException persistence(String message, SQLException exception) {
        return new PersistenceException(message + ": " + exception.getMessage(), exception);
    }

    @FunctionalInterface
    private interface SqlWork<T> {
        T execute(Connection connection) throws SQLException;
    }
}
