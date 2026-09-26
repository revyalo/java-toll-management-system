package com.revyalo.toll.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.revyalo.toll.domain.PassageCompletion;
import com.revyalo.toll.domain.SpeedFinePolicy;
import com.revyalo.toll.domain.TollRatePolicy;
import com.revyalo.toll.domain.TrafficFine;
import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.exception.EntityNotFoundException;
import com.revyalo.toll.exception.UnauthorizedOperationException;
import com.revyalo.toll.repository.SQLiteTollRepository;
import com.revyalo.toll.security.RoleAuthorizer;
import com.revyalo.toll.security.UserPrincipal;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TollServiceTest {
    @TempDir
    Path temporaryDirectory;

    private TollService service;
    private UserPrincipal operator;
    private UserPrincipal agent;
    private UserPrincipal driver;
    private UserPrincipal otherDriver;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        SQLiteTollRepository repository = new SQLiteTollRepository(
            "jdbc:sqlite:" + temporaryDirectory.resolve("service.db")
        );
        repository.initialize();
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"), ZoneOffset.UTC);
        service = new TollService(
            repository,
            new TollRatePolicy(),
            new SpeedFinePolicy(),
            new RoleAuthorizer(),
            new CsvHistoryExporter(temporaryDirectory.resolve("exports"), clock),
            clock
        );
        operator = new UserPrincipal("operator", UserRole.OPERATOR, null);
        agent = new UserPrincipal("agent", UserRole.AGENT, null);
        driver = new UserPrincipal("driver", UserRole.DRIVER, "1234ABC");
        otherDriver = new UserPrincipal("other", UserRole.DRIVER, "9999ZZZ");
        now = LocalDateTime.of(2026, 1, 1, 12, 0);
    }

    @Test
    void operatorRegistersEntryAndExitAtomically() {
        service.registerEntry(operator, "1234abc", now.minusHours(1));

        PassageCompletion completion = service.registerExit(operator, "1234ABC", now, 8.0);

        assertTrue(completion.ticket().id() > 0);
        assertEquals("3.00", completion.ticket().amount().toPlainString());
        assertEquals(1, service.listTickets(operator, "1234ABC").size());
        assertFalse(completion.sectionFine().isPresent());
    }

    @Test
    void fastSectionCreatesFineWithTicket() {
        service.registerEntry(operator, "1234ABC", now.minusMinutes(30));

        PassageCompletion completion = service.registerExit(operator, "1234ABC", now, 8.0);

        assertTrue(completion.sectionFine().isPresent());
        assertEquals(1, service.listFines(operator, "1234ABC").size());
    }

    @Test
    void exitWithoutEntryFailsWithoutCreatingTicket() {
        assertThrows(
            EntityNotFoundException.class,
            () -> service.registerExit(operator, "1234ABC", now, 8.0)
        );
        assertTrue(service.listTickets(operator, "1234ABC").isEmpty());
    }

    @Test
    void agentCanIssueMobileFineOnlyAboveLimit() {
        Optional<TrafficFine> noFine = service.issueMobileFine(agent, "1234ABC", 120.0, now);
        Optional<TrafficFine> fine = service.issueMobileFine(agent, "1234ABC", 130.1, now);

        assertTrue(noFine.isEmpty());
        assertTrue(fine.isPresent());
        assertEquals(1, service.listFines(agent, "1234ABC").size());
    }

    @Test
    void rolePermissionsAreEnforcedByServiceLayer() {
        assertThrows(
            UnauthorizedOperationException.class,
            () -> service.registerEntry(agent, "1234ABC", now)
        );
        assertThrows(
            UnauthorizedOperationException.class,
            () -> service.issueMobileFine(operator, "1234ABC", 140.0, now)
        );
    }

    @Test
    void driverCanOnlySeeAndPayOwnFine() {
        TrafficFine fine = service.issueMobileFine(agent, "1234ABC", 140.0, now).orElseThrow();

        assertThrows(
            UnauthorizedOperationException.class,
            () -> service.listFines(otherDriver, "1234ABC")
        );
        service.payFine(driver, fine.id());
        assertTrue(service.listFines(driver, "1234ABC").get(0).paid());
        assertThrows(EntityNotFoundException.class, () -> service.payFine(driver, fine.id()));
    }

    @Test
    void operatorExportsCsvHistory() {
        service.registerEntry(operator, "1234ABC", now.minusHours(1));
        service.registerExit(operator, "1234ABC", now, 8.0);

        Path export = service.exportHistory(operator, "1234ABC");

        assertTrue(java.nio.file.Files.isRegularFile(export));
    }
}
