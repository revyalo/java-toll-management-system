package com.revyalo.toll.ui;

import com.revyalo.toll.security.UserPrincipal;
import com.revyalo.toll.service.AuthenticationService;
import com.revyalo.toll.service.TollService;
import javax.swing.JFrame;

public final class LoginFrame extends JFrame {
    private final TollService tollService;

    public LoginFrame(AuthenticationService authenticationService, TollService tollService) {
        super("Vía Segura · Acceso");
        this.tollService = tollService;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new LoginPanel(authenticationService, this::openDashboard));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void openDashboard(UserPrincipal principal) {
        DashboardFrame dashboard = new DashboardFrame(principal, tollService);
        dashboard.setVisible(true);
        dispose();
    }
}
