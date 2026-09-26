package com.revyalo.toll.ui;

import com.revyalo.toll.exception.TollSystemException;
import com.revyalo.toll.security.UserPrincipal;
import com.revyalo.toll.service.AuthenticationService;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public final class LoginPanel extends JPanel {
    private final AuthenticationService authenticationService;
    private final Consumer<UserPrincipal> onAuthenticated;
    private final JTextField username = new JTextField(22);
    private final JPasswordField password = new JPasswordField(22);
    private final JLabel error = new JLabel(" ", SwingConstants.CENTER);

    public LoginPanel(
        AuthenticationService authenticationService,
        Consumer<UserPrincipal> onAuthenticated
    ) {
        this.authenticationService = authenticationService;
        this.onAuthenticated = onAuthenticated;
        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(960, 600));
        add(buildCard(), new GridBagConstraints());
    }

    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(430, 430));
        card.setBackground(UiTheme.WHITE);
        card.setBorder(UiTheme.cardBorder());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel eyebrow = new JLabel("VÍA SEGURA · CONTROL DE PEAJES");
        eyebrow.setForeground(UiTheme.BLUE);
        eyebrow.setFont(UiTheme.BODY.deriveFont(Font.BOLD, 12));
        eyebrow.setAlignmentX(CENTER_ALIGNMENT);

        JLabel title = new JLabel("Acceso al sistema");
        title.setFont(UiTheme.TITLE);
        title.setForeground(UiTheme.NAVY);
        title.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Operaciones autorizadas y trazables por rol");
        subtitle.setForeground(UiTheme.MUTED);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);

        UiTheme.field(username);
        UiTheme.field(password);
        username.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        password.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        JButton login = new JButton("Iniciar sesión");
        UiTheme.primaryButton(login);
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        login.setAlignmentX(CENTER_ALIGNMENT);
        login.addActionListener(this::authenticate);
        password.addActionListener(this::authenticate);

        error.setForeground(UiTheme.DANGER);
        error.setFont(UiTheme.BODY.deriveFont(12f));
        error.setAlignmentX(CENTER_ALIGNMENT);

        JPanel userField = labeledField("Usuario", username);
        JPanel passwordField = labeledField("Contraseña", password);
        card.add(eyebrow);
        card.add(Box.createVerticalStrut(18));
        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(userField);
        card.add(Box.createVerticalStrut(14));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(22));
        card.add(login);
        card.add(Box.createVerticalStrut(14));
        card.add(error);
        return card;
    }

    private JPanel labeledField(String label, javax.swing.JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        JLabel caption = new JLabel(label);
        caption.setForeground(UiTheme.SLATE);
        caption.setFont(UiTheme.BODY.deriveFont(Font.BOLD));
        panel.add(caption, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private void authenticate(ActionEvent ignored) {
        error.setText(" ");
        try {
            UserPrincipal principal = authenticationService.authenticate(
                username.getText(),
                password.getPassword()
            );
            password.setText("");
            onAuthenticated.accept(principal);
        } catch (TollSystemException | IllegalArgumentException exception) {
            password.setText("");
            error.setText(exception.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics.create();
        LinearGradientPaint gradient = new LinearGradientPaint(
            0,
            0,
            getWidth(),
            getHeight(),
            new float[]{0f, 1f},
            new Color[]{UiTheme.NAVY, new Color(30, 64, 175)}
        );
        graphics2D.setPaint(gradient);
        graphics2D.fillRect(0, 0, getWidth(), getHeight());
        graphics2D.dispose();
    }
}
