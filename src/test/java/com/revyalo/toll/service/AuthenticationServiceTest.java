package com.revyalo.toll.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.revyalo.toll.domain.UserAccount;
import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.exception.InvalidCredentialsException;
import com.revyalo.toll.repository.SQLiteTollRepository;
import com.revyalo.toll.security.PasswordHasher;
import com.revyalo.toll.security.UserPrincipal;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AuthenticationServiceTest {
    @TempDir
    Path temporaryDirectory;

    private AuthenticationService service;

    @BeforeEach
    void setUp() {
        SQLiteTollRepository repository = new SQLiteTollRepository(
            "jdbc:sqlite:" + temporaryDirectory.resolve("auth.db")
        );
        repository.initialize();
        PasswordHasher hasher = new PasswordHasher();
        PasswordHasher.PasswordDigest digest = hasher.hash("correct-password".toCharArray());
        repository.saveUser(new UserAccount(
            0,
            "driver",
            digest.hash(),
            digest.salt(),
            UserRole.DRIVER,
            "1234ABC"
        ));
        service = new AuthenticationService(repository, hasher);
    }

    @Test
    void authenticatesAndReturnsRoleContext() {
        UserPrincipal principal = service.authenticate(" DRIVER ", "correct-password".toCharArray());

        assertEquals(UserRole.DRIVER, principal.role());
        assertEquals("1234ABC", principal.licensePlate());
    }

    @Test
    void rejectsWrongPasswordWithoutRevealingWhetherUserExists() {
        InvalidCredentialsException wrongPassword = assertThrows(
            InvalidCredentialsException.class,
            () -> service.authenticate("driver", "wrong-password".toCharArray())
        );
        InvalidCredentialsException unknownUser = assertThrows(
            InvalidCredentialsException.class,
            () -> service.authenticate("unknown", "wrong-password".toCharArray())
        );

        assertEquals(wrongPassword.getMessage(), unknownUser.getMessage());
    }
}
