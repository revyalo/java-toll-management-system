package com.revyalo.toll.ui;

import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

final class UiTheme {
    static final Color NAVY = new Color(15, 23, 42);
    static final Color SLATE = new Color(51, 65, 85);
    static final Color MUTED = new Color(100, 116, 139);
    static final Color BLUE = new Color(37, 99, 235);
    static final Color SKY = new Color(14, 165, 233);
    static final Color SURFACE = new Color(248, 250, 252);
    static final Color WHITE = Color.WHITE;
    static final Color DANGER = new Color(185, 28, 28);
    static final Font TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 28);
    static final Font HEADING = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    static final Font BODY = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

    private UiTheme() {
    }

    static void install() {
        UIManager.put("Label.font", BODY);
        UIManager.put("Button.font", BODY.deriveFont(Font.BOLD));
        UIManager.put("TextField.font", BODY);
        UIManager.put("PasswordField.font", BODY);
        UIManager.put("TextArea.font", new Font(Font.MONOSPACED, Font.PLAIN, 13));
    }

    static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(24, 24, 24, 24)
        );
    }

    static void primaryButton(JButton button) {
        button.setBackground(BLUE);
        button.setForeground(WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
    }

    static void field(JComponent component) {
        component.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }
}
