package com.revyalo.toll;

import com.revyalo.toll.bootstrap.AppConfig;
import com.revyalo.toll.bootstrap.ApplicationContext;
import com.revyalo.toll.ui.LoginFrame;
import com.revyalo.toll.ui.UiThemeAccess;
import java.awt.EventQueue;
import java.util.Arrays;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TollApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(TollApplication.class);

    private TollApplication() {
    }

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--health-check")) {
            ApplicationContext.create(AppConfig.fromEnvironment());
            LOGGER.info("Health check completado correctamente");
            return;
        }
        EventQueue.invokeLater(() -> {
            try {
                UiThemeAccess.install();
                ApplicationContext context = ApplicationContext.create(AppConfig.fromEnvironment());
                new LoginFrame(
                    context.authenticationService(),
                    context.tollService()
                ).setVisible(true);
            } catch (RuntimeException exception) {
                LOGGER.error("No se pudo iniciar la aplicación", exception);
                JOptionPane.showMessageDialog(
                    null,
                    "No se pudo iniciar la aplicación: " + exception.getMessage(),
                    "Error de inicio",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
