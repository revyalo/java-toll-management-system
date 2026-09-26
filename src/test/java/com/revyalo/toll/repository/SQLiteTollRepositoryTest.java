package com.revyalo.toll.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.revyalo.toll.domain.ActivePassage;
import com.revyalo.toll.domain.AuditEvent;
import com.revyalo.toll.domain.PassageCompletion;
import com.revyalo.toll.domain.RadarType;
import com.revyalo.toll.domain.TollTicket;
import com.revyalo.toll.domain.TrafficFine;
import com.revyalo.toll.domain.UserAccount;
import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.exception.DuplicateActivePassageException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SQLiteTollRepositoryTest {
    @TempDir
    Path temporaryDirectory;

    private SQLiteTollRepository repository;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        repository = new SQLiteTollRepository(
            "jdbc:sqlite:" + temporaryDirectory.resolve("test.db")
        );
        repository.initialize();
        now = LocalDateTime.of(2026, 1, 1, 12, 0);
    }

    @Test
    void persistsUsersAcrossRepositoryInstances() {
        repository.saveUser(new UserAccount(
            0, "operator", "hash", "salt", UserRole.OPERATOR, null
        ));

        SQLiteTollRepository reopened = new SQLiteTollRepository(
            "jdbc:sqlite:" + temporaryDirectory.resolve("test.db")
        );

        assertTrue(reopened.hasUsers());
        assertEquals(UserRole.OPERATOR,
            reopened.findUserByUsername("OPERATOR").orElseThrow().role());
    }

    @Test
    void entryTicketFinePaymentAndAuditAreTransactional() {
        ActivePassage passage = new ActivePassage("1234ABC", now.minusHours(1));
        repository.createActivePassage(passage, audit("ENTRY"));

        PassageCompletion completion = repository.completePassage(
            new TollTicket(
                0,
                "1234ABC",
                8.0,
                passage.enteredAt(),
                now,
                new BigDecimal("2.50")
            ),
            Optional.of(new TrafficFine(
                0,
                "1234ABC",
                now,
                140.0,
                new BigDecimal("300.00"),
                RadarType.SECTION,
                false
            )),
            audit("EXIT")
        );

        assertTrue(completion.ticket().id() > 0);
        assertTrue(completion.sectionFine().orElseThrow().id() > 0);
        assertTrue(repository.findActivePassage("1234ABC").isEmpty());
        assertEquals(1, repository.findTicketsByPlate("1234ABC").size());
        assertEquals(1, repository.findFinesByPlate("1234ABC").size());
        assertTrue(repository.payFine(
            completion.sectionFine().orElseThrow().id(),
            "1234ABC",
            audit("PAY")
        ));
        assertFalse(repository.payFine(
            completion.sectionFine().orElseThrow().id(),
            "1234ABC",
            audit("PAY_AGAIN")
        ));
        assertTrue(repository.findFinesByPlate("1234ABC").get(0).paid());
        assertEquals(3, repository.findRecentAuditEvents(10).size());
    }

    @Test
    void rejectsTwoSimultaneousEntriesForSamePlate() {
        ActivePassage passage = new ActivePassage("1234ABC", now);
        repository.createActivePassage(passage, audit("ENTRY"));

        assertThrows(
            DuplicateActivePassageException.class,
            () -> repository.createActivePassage(passage, audit("ENTRY"))
        );
    }

    private AuditEvent audit(String action) {
        return new AuditEvent(
            now,
            "test",
            UserRole.OPERATOR,
            action,
            "TEST",
            "test-event"
        );
    }
}
