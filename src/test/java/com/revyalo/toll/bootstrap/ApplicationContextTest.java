package com.revyalo.toll.bootstrap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.security.UserPrincipal;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ApplicationContextTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void firstStartMigratesDatabaseAndSeedsDemoAccounts() {
        AppConfig config = new AppConfig(
            temporaryDirectory.resolve("app.db"),
            temporaryDirectory.resolve("exports")
        );

        ApplicationContext context = ApplicationContext.create(config);
        UserPrincipal driver = context.authenticationService()
            .authenticate("driver", "demo1234".toCharArray());

        assertEquals(UserRole.DRIVER, driver.role());
        assertEquals(1, context.tollService().listFines(driver, "1234ABC").size());
    }
}
