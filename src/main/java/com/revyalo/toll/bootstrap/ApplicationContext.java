package com.revyalo.toll.bootstrap;

import com.revyalo.toll.domain.SpeedFinePolicy;
import com.revyalo.toll.domain.TollRatePolicy;
import com.revyalo.toll.repository.SQLiteTollRepository;
import com.revyalo.toll.repository.TollRepository;
import com.revyalo.toll.security.PasswordHasher;
import com.revyalo.toll.security.RoleAuthorizer;
import com.revyalo.toll.service.AuthenticationService;
import com.revyalo.toll.service.CsvHistoryExporter;
import com.revyalo.toll.service.TollService;
import java.time.Clock;

public final class ApplicationContext {
    private final AuthenticationService authenticationService;
    private final TollService tollService;

    private ApplicationContext(
        AuthenticationService authenticationService,
        TollService tollService
    ) {
        this.authenticationService = authenticationService;
        this.tollService = tollService;
    }

    public static ApplicationContext create(AppConfig config) {
        Clock clock = Clock.systemDefaultZone();
        TollRepository repository = new SQLiteTollRepository(config.jdbcUrl());
        repository.initialize();
        PasswordHasher passwordHasher = new PasswordHasher();
        new DemoDataSeeder(repository, passwordHasher, clock).seedIfEmpty();

        AuthenticationService authentication = new AuthenticationService(repository, passwordHasher);
        TollService tolls = new TollService(
            repository,
            new TollRatePolicy(),
            new SpeedFinePolicy(),
            new RoleAuthorizer(),
            new CsvHistoryExporter(config.exportDirectory(), clock),
            clock
        );
        return new ApplicationContext(authentication, tolls);
    }

    public AuthenticationService authenticationService() {
        return authenticationService;
    }

    public TollService tollService() {
        return tollService;
    }
}
