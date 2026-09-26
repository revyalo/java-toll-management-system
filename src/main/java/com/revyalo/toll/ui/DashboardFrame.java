package com.revyalo.toll.ui;

import com.revyalo.toll.security.UserPrincipal;
import com.revyalo.toll.service.TollService;
import javax.swing.JFrame;

public final class DashboardFrame extends JFrame {
    public DashboardFrame(UserPrincipal principal, TollService tollService) {
        super("Vía Segura · " + principal.role().displayName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(new DashboardPanel(principal, tollService));
        setSize(1080, 680);
        setMinimumSize(new java.awt.Dimension(900, 600));
        setLocationRelativeTo(null);
    }
}
