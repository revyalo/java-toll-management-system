package com.revyalo.toll.service;

import com.revyalo.toll.domain.ActivePassage;
import com.revyalo.toll.domain.AuditEvent;
import com.revyalo.toll.domain.PassageCompletion;
import com.revyalo.toll.domain.SpeedFinePolicy;
import com.revyalo.toll.domain.TollRatePolicy;
import com.revyalo.toll.domain.TollTicket;
import com.revyalo.toll.domain.TrafficFine;
import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.exception.EntityNotFoundException;
import com.revyalo.toll.exception.InvalidVehicleException;
import com.revyalo.toll.repository.TollRepository;
import com.revyalo.toll.security.RoleAuthorizer;
import com.revyalo.toll.security.UserPrincipal;
import com.revyalo.toll.validation.InputValidator;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TollService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TollService.class);
    private final TollRepository repository;
    private final TollRatePolicy tollRatePolicy;
    private final SpeedFinePolicy speedFinePolicy;
    private final RoleAuthorizer authorizer;
    private final HistoryExporter historyExporter;
    private final Clock clock;

    public TollService(
        TollRepository repository,
        TollRatePolicy tollRatePolicy,
        SpeedFinePolicy speedFinePolicy,
        RoleAuthorizer authorizer,
        HistoryExporter historyExporter,
        Clock clock
    ) {
        this.repository = repository;
        this.tollRatePolicy = tollRatePolicy;
        this.speedFinePolicy = speedFinePolicy;
        this.authorizer = authorizer;
        this.historyExporter = historyExporter;
        this.clock = clock;
    }

    public void registerEntry(UserPrincipal principal, String licensePlate, LocalDateTime enteredAt) {
        authorizer.requireAny(principal, UserRole.OPERATOR);
        String plate = InputValidator.normalizeLicensePlate(licensePlate);
        if (enteredAt == null) {
            throw new InvalidVehicleException("La fecha de entrada es obligatoria");
        }
        repository.createActivePassage(
            new ActivePassage(plate, enteredAt),
            audit(principal, "REGISTER_ENTRY", "ACTIVE_PASSAGE", "plate=" + plate)
        );
        LOGGER.info("Entrada de vehículo registrada por {}", principal.username());
    }

    public PassageCompletion registerExit(
        UserPrincipal principal,
        String licensePlate,
        LocalDateTime exitedAt,
        double vehicleSize
    ) {
        authorizer.requireAny(principal, UserRole.OPERATOR);
        String plate = InputValidator.normalizeLicensePlate(licensePlate);
        double size = InputValidator.requireVehicleSize(vehicleSize);
        ActivePassage passage = repository.findActivePassage(plate)
            .orElseThrow(() -> new EntityNotFoundException(
                "No existe una entrada activa para " + plate
            ));
        InputValidator.requireChronological(passage.enteredAt(), exitedAt);

        TollTicket ticket = new TollTicket(
            0,
            plate,
            size,
            passage.enteredAt(),
            exitedAt,
            tollRatePolicy.calculate(size, exitedAt)
        );
        Optional<TrafficFine> sectionFine = speedFinePolicy.sectionFine(passage, exitedAt);
        PassageCompletion result = repository.completePassage(
            ticket,
            sectionFine,
            audit(principal, "REGISTER_EXIT", "TICKET", "plate=" + plate)
        );
        LOGGER.info("Salida registrada por {}; multa de tramo={}",
            principal.username(), result.sectionFine().isPresent());
        return result;
    }

    public Optional<TrafficFine> issueMobileFine(
        UserPrincipal principal,
        String licensePlate,
        double speedKph,
        LocalDateTime detectedAt
    ) {
        authorizer.requireAny(principal, UserRole.AGENT);
        Optional<TrafficFine> candidate = speedFinePolicy.mobileFine(
            licensePlate,
            speedKph,
            detectedAt
        );
        if (candidate.isEmpty()) {
            return Optional.empty();
        }
        TrafficFine saved = repository.createFine(
            candidate.orElseThrow(),
            audit(principal, "ISSUE_FINE", "FINE", "radar=MOBILE")
        );
        LOGGER.info("Multa móvil emitida por {}", principal.username());
        return Optional.of(saved);
    }

    public List<TollTicket> listTickets(UserPrincipal principal, String licensePlate) {
        String plate = InputValidator.normalizeLicensePlate(licensePlate);
        authorizer.requirePlateAccess(principal, plate, UserRole.OPERATOR);
        return repository.findTicketsByPlate(plate);
    }

    public List<TrafficFine> listFines(UserPrincipal principal, String licensePlate) {
        String plate = InputValidator.normalizeLicensePlate(licensePlate);
        authorizer.requirePlateAccess(principal, plate, UserRole.OPERATOR, UserRole.AGENT);
        return repository.findFinesByPlate(plate);
    }

    public void payFine(UserPrincipal principal, long fineId) {
        authorizer.requireAny(principal, UserRole.DRIVER);
        String plate = InputValidator.normalizeLicensePlate(principal.licensePlate());
        if (fineId < 1) {
            throw new InvalidVehicleException("El identificador de multa debe ser positivo");
        }
        boolean paid = repository.payFine(
            fineId,
            plate,
            audit(principal, "PAY_FINE", "FINE", "fine_id=" + fineId)
        );
        if (!paid) {
            throw new EntityNotFoundException("La multa no existe o ya estaba pagada");
        }
        LOGGER.info("Multa abonada por el conductor autenticado");
    }

    public Path exportHistory(UserPrincipal principal, String licensePlate) {
        authorizer.requireAny(principal, UserRole.OPERATOR);
        String plate = InputValidator.normalizeLicensePlate(licensePlate);
        Path output = historyExporter.export(
            plate,
            repository.findTicketsByPlate(plate),
            repository.findFinesByPlate(plate)
        );
        LOGGER.info("Historial exportado por {}", principal.username());
        return output;
    }

    public List<AuditEvent> recentAudit(UserPrincipal principal, int limit) {
        authorizer.requireAny(principal, UserRole.OPERATOR);
        return repository.findRecentAuditEvents(limit);
    }

    private AuditEvent audit(
        UserPrincipal principal,
        String action,
        String entityType,
        String detail
    ) {
        return new AuditEvent(
            LocalDateTime.now(clock),
            principal.username(),
            principal.role(),
            action,
            entityType,
            detail
        );
    }
}
