package com.revyalo.toll.repository;

import com.revyalo.toll.domain.ActivePassage;
import com.revyalo.toll.domain.AuditEvent;
import com.revyalo.toll.domain.PassageCompletion;
import com.revyalo.toll.domain.TollTicket;
import com.revyalo.toll.domain.TrafficFine;
import com.revyalo.toll.domain.UserAccount;
import java.util.List;
import java.util.Optional;

public interface TollRepository {
    void initialize();

    boolean hasUsers();

    void saveUser(UserAccount account);

    Optional<UserAccount> findUserByUsername(String username);

    void createActivePassage(ActivePassage passage, AuditEvent auditEvent);

    Optional<ActivePassage> findActivePassage(String licensePlate);

    PassageCompletion completePassage(
        TollTicket ticket,
        Optional<TrafficFine> sectionFine,
        AuditEvent auditEvent
    );

    TrafficFine createFine(TrafficFine fine, AuditEvent auditEvent);

    List<TollTicket> findTicketsByPlate(String licensePlate);

    List<TrafficFine> findFinesByPlate(String licensePlate);

    boolean payFine(long fineId, String licensePlate, AuditEvent auditEvent);

    List<AuditEvent> findRecentAuditEvents(int limit);
}
