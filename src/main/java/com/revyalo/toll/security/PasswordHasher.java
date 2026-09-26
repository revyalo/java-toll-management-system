package com.revyalo.toll.security;

import com.revyalo.toll.exception.TollSystemException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;
    private static final int SALT_BYTES = 16;
    private final SecureRandom random;

    public PasswordHasher() {
        this(new SecureRandom());
    }

    PasswordHasher(SecureRandom random) {
        this.random = random;
    }

    public PasswordDigest hash(char[] password) {
        try {
            validatePassword(password);
            byte[] salt = new byte[SALT_BYTES];
            random.nextBytes(salt);
            byte[] hash = derive(password, salt);
            return new PasswordDigest(
                Base64.getEncoder().encodeToString(hash),
                Base64.getEncoder().encodeToString(salt)
            );
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    public boolean verify(char[] password, String expectedHash, String encodedSalt) {
        if (password == null || expectedHash == null || encodedSalt == null) {
            return false;
        }
        byte[] actual = derive(password, Base64.getDecoder().decode(encodedSalt));
        byte[] expected = Base64.getDecoder().decode(expectedHash);
        boolean matches = MessageDigest.isEqual(actual, expected);
        Arrays.fill(actual, (byte) 0);
        return matches;
    }

    private byte[] derive(char[] password, byte[] salt) {
        PBEKeySpec specification = new PBEKeySpec(password, salt, ITERATIONS, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(specification)
                .getEncoded();
        } catch (GeneralSecurityException exception) {
            throw new TollSystemException("No se pudo derivar la credencial", exception);
        } finally {
            specification.clearPassword();
        }
    }

    private void validatePassword(char[] password) {
        if (password == null || password.length < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
    }

    public record PasswordDigest(String hash, String salt) {
    }
}
