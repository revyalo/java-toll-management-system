package com.revyalo.toll.tools;

import com.revyalo.toll.bootstrap.AppConfig;
import com.revyalo.toll.bootstrap.ApplicationContext;
import com.revyalo.toll.domain.UserRole;
import com.revyalo.toll.security.UserPrincipal;
import com.revyalo.toll.ui.DashboardPanel;
import com.revyalo.toll.ui.LoginPanel;
import com.revyalo.toll.ui.UiThemeAccess;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public final class UiScreenshotGenerator {
    private UiScreenshotGenerator() {
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        Path outputDirectory = Path.of("docs", "screenshots");
        Files.createDirectories(outputDirectory);
        Path temporaryDirectory = Files.createTempDirectory("toll-ui-preview-");
        ApplicationContext context = ApplicationContext.create(new AppConfig(
            temporaryDirectory.resolve("preview.db"),
            temporaryDirectory.resolve("exports")
        ));

        SwingUtilities.invokeAndWait(() -> {
            try {
                UiThemeAccess.install();
                render(
                    new LoginPanel(context.authenticationService(), principal -> { }),
                    outputDirectory.resolve("login.png"),
                    960,
                    600
                );
                render(
                    new DashboardPanel(
                        new UserPrincipal("operator", UserRole.OPERATOR, null),
                        context.tollService()
                    ),
                    outputDirectory.resolve("operator-dashboard.png"),
                    1080,
                    680
                );
            } catch (Exception exception) {
                throw new IllegalStateException(exception);
            }
        });
    }

    private static void render(JComponent component, Path output, int width, int height)
        throws Exception {
        component.setSize(width, height);
        layoutTree(component);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        component.printAll(graphics);
        graphics.dispose();
        ImageIO.write(image, "png", output.toFile());
    }

    private static void layoutTree(Container container) {
        container.doLayout();
        for (Component child : container.getComponents()) {
            if (child instanceof Container childContainer) {
                layoutTree(childContainer);
            }
        }
    }
}
