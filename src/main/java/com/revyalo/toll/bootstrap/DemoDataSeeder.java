package com.revyalo.toll.bootstrap;

import com.revyalo.toll.domain.ActivePassage;
import com.revyalo.toll.domain.AuditEvent;
import com.revyalo.toll.domain.TollTicket;
import com.revyalo.toll.domain.TrafficFine;
import com.revyalo.toll.domain.RadarType;
import com.revyalo.toll.domain.UserAccount;
import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.repository.TollRepository;
import com.revyalo.toll.security.PasswordHasher;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DemoDataSeeder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoDataSeeder.class);
    private static final char[] DEMO_PASSWORD = "demo1234".toCharArray();
    private final TollRepository repository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public DemoDataSeeder(TollRepository repository, PasswordHasher passwordHasher, Clock clock) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.clock = clock;
    }

    public void seedIfEmpty() {
        if (repository.hasUsers()) {
            return;
        }
        saveAccount("operator", UserRole.OPERATOR, null);
        saveAccount("agent", UserRole.AGENT, null);
        saveAccount("driver", UserRole.DRIVER, "1234ABC");

        LocalDateTime now = LocalDateTime.now(clock).withNano(0);
        ActivePassage passage = new ActivePassage("1234ABC", now.minusDays(2).minusMinutes(45));
        repository.createActivePassage(passage, systemAudit("SEED_ENTRY", "ACTIVE_PASSAGE"));
        repository.completePassage(
            new TollTicket(
                0,
                passage.licensePlate(),
                8.0,
                passage.enteredAt(),
                now.minusDays(2),
                new BigDecimal("2.50")
            ),
            Optional.empty(),
            systemAudit("SEED_EXIT", "TICKET")
        );
        repository.createFine(
            new TrafficFine(
                0,
                "1234ABC",
                now.minusDays(1),
                135.0,
                new BigDecimal("150.00"),
                RadarType.MOBILE,
                false
            ),
            systemAudit("SEED_FINE", "FINE")
        );
        LOGGER.info("Datos de demostración inicializados");
    }

    private void saveAccount(String username, UserRole role, String licensePlate) {
        PasswordHasher.PasswordDigest digest = passwordHasher.hash(DEMO_PASSWORD.clone());
        repository.saveUser(new UserAccount(
            0,
            username,
            digest.hash(),
            digest.salt(),
            role,
            licensePlate
        ));
    }

    private AuditEvent systemAudit(String action, String entityType) {
        return new AuditEvent(
            LocalDateTime.now(clock),
            "system",
            UserRole.OPERATOR,
            action,
            entityType,
            "demo_seed"
        );
    }
}
