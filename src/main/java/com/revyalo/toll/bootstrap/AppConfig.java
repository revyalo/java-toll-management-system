package com.revyalo.toll.bootstrap;

import com.revyalo.toll.exception.PersistenceException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public record AppConfig(Path databasePath, Path exportDirectory) {
    public static AppConfig fromEnvironment() {
        String configuredPath = System.getProperty("toll.db.path");
        if (configuredPath == null || configuredPath.isBlank()) {
            configuredPath = System.getenv().getOrDefault("TOLL_DB_PATH", "data/tolls.db");
        }
        Path database = Path.of(configuredPath).toAbsolutePath().normalize();
        Path parent = database.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException exception) {
            throw new PersistenceException("No se pudo crear el directorio de datos", exception);
        }
        Path exports = (parent == null ? Path.of("exports") : parent.resolve("exports"));
        return new AppConfig(database, exports);
    }

    public String jdbcUrl() {
        return "jdbc:sqlite:" + databasePath;
    }
}
