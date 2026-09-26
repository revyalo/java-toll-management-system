package com.revyalo.toll.service;

import com.revyalo.toll.domain.UserAccount;
import com.revyalo.toll.exception.InvalidCredentialsException;
import com.revyalo.toll.repository.TollRepository;
import com.revyalo.toll.security.PasswordHasher;
import com.revyalo.toll.security.UserPrincipal;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AuthenticationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);
    private final TollRepository repository;
    private final PasswordHasher passwordHasher;
    private final PasswordHasher.PasswordDigest dummyDigest;

    public AuthenticationService(TollRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
        this.dummyDigest = passwordHasher.hash("invalid-credential-placeholder".toCharArray());
    }

    public UserPrincipal authenticate(String username, char[] password) {
        try {
            String normalized = normalizeUsername(username);
            Optional<UserAccount> found = repository.findUserByUsername(normalized);
            if (found.isEmpty()) {
                passwordHasher.verify(password, dummyDigest.hash(), dummyDigest.salt());
                throw new InvalidCredentialsException();
            }
            UserAccount account = found.orElseThrow();
            if (!passwordHasher.verify(password, account.passwordHash(), account.passwordSalt())) {
                LOGGER.warn("Intento de autenticación fallido para usuario existente");
                throw new InvalidCredentialsException();
            }
            LOGGER.info("Sesión iniciada con rol {}", account.role());
            return new UserPrincipal(account.username(), account.role(), account.licensePlate());
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
        }
    }

    private String normalizeUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new InvalidCredentialsException();
        }
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
