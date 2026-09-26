package com.revyalo.toll.ui;

import com.revyalo.toll.domain.AuditEvent;
import com.revyalo.toll.domain.PassageCompletion;
import com.revyalo.toll.domain.TollTicket;
import com.revyalo.toll.domain.TrafficFine;
import com.revyalo.toll.exception.TollSystemException;
import com.revyalo.toll.security.UserPrincipal;
import com.revyalo.toll.service.TollService;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DashboardPanel extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardPanel.class);
    private static final DateTimeFormatter DISPLAY_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final UserPrincipal principal;
    private final TollService tollService;
    private final JTextArea output = new JTextArea();
    private final JLabel status = new JLabel("Sesión lista");

    public DashboardPanel(UserPrincipal principal, TollService tollService) {
        this.principal = principal;
        this.tollService = tollService;
        setLayout(new BorderLayout());
        setBackground(UiTheme.SURFACE);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        add(buildStatus(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UiTheme.NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(18, 28, 18, 28));

        JLabel title = new JLabel("VÍA SEGURA");
        title.setForeground(UiTheme.WHITE);
        title.setFont(UiTheme.HEADING.deriveFont(20f));

        JLabel session = new JLabel(
            principal.username() + "  ·  " + principal.role().displayName()
        );
        session.setForeground(new java.awt.Color(186, 230, 253));
        session.setFont(UiTheme.BODY.deriveFont(Font.BOLD));
        header.add(title, BorderLayout.WEST);
        header.add(session, BorderLayout.EAST);
        return header;
    }

    private Component buildContent() {
        JPanel actions = switch (principal.role()) {
            case OPERATOR -> buildOperatorActions();
            case AGENT -> buildAgentActions();
            case DRIVER -> buildDriverActions();
        };

        output.setEditable(false);
        output.setLineWrap(false);
        output.setMargin(new Insets(14, 14, 14, 14));
        output.setText("Selecciona una operación para comenzar.");
        JScrollPane results = new JScrollPane(output);
        results.setBorder(UiTheme.cardBorder());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(actions), results);
        split.setResizeWeight(0.44);
        split.setDividerSize(6);
        split.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        split.setBackground(UiTheme.SURFACE);
        return split;
    }

    private JPanel buildOperatorActions() {
        JPanel container = actionContainer("Operaciones de peaje");
        JTextField entryPlate = field("Matrícula");
        JButton entry = primary("Registrar entrada");
        entry.addActionListener(ignored -> execute(() -> {
            tollService.registerEntry(principal, entryPlate.getText(), LocalDateTime.now());
            return "Entrada registrada correctamente para " + entryPlate.getText().trim();
        }));
        container.add(formCard("Nueva entrada", entryPlate, entry));

        JTextField exitPlate = field("Matrícula");
        JTextField size = field("Tamaño del vehículo");
        JButton exit = primary("Registrar salida");
        exit.addActionListener(ignored -> execute(() -> {
            PassageCompletion result = tollService.registerExit(
                principal,
                exitPlate.getText(),
                LocalDateTime.now(),
                parsePositive(size.getText(), "tamaño")
            );
            String fine = result.sectionFine()
                .map(value -> "\nMulta de tramo: " + formatFine(value))
                .orElse("\nSin exceso de velocidad en el tramo.");
            return "Ticket generado:\n" + formatTicket(result.ticket()) + fine;
        }));
        container.add(formCard("Registrar salida", exitPlate, size, exit));

        JTextField queryPlate = field("Matrícula para consulta");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        JButton tickets = secondary("Ver tickets");
        tickets.addActionListener(ignored -> execute(
            () -> formatTickets(tollService.listTickets(principal, queryPlate.getText()))
        ));
        JButton fines = secondary("Ver multas");
        fines.addActionListener(ignored -> execute(
            () -> formatFines(tollService.listFines(principal, queryPlate.getText()))
        ));
        JButton export = secondary("Exportar CSV");
        export.addActionListener(ignored -> execute(() -> {
            Path path = tollService.exportHistory(principal, queryPlate.getText());
            return "Historial exportado en:\n" + path.toAbsolutePath();
        }));
        buttons.add(tickets);
        buttons.add(fines);
        buttons.add(export);
        container.add(formCard("Consulta e historial", queryPlate, buttons));

        JButton audit = secondary("Mostrar últimos 25 eventos");
        audit.addActionListener(ignored -> execute(
            () -> formatAudit(tollService.recentAudit(principal, 25))
        ));
        container.add(formCard("Auditoría", audit));
        return container;
    }

    private JPanel buildAgentActions() {
        JPanel container = actionContainer("Control de velocidad");
        JTextField plate = field("Matrícula");
        JTextField speed = field("Velocidad detectada (km/h)");
        JButton createFine = primary("Evaluar y registrar multa");
        createFine.addActionListener(ignored -> execute(() -> tollService.issueMobileFine(
            principal,
            plate.getText(),
            parsePositive(speed.getText(), "velocidad"),
            LocalDateTime.now()
        ).map(value -> "Multa creada:\n" + formatFine(value))
            .orElse("La velocidad no supera el límite; no se genera multa.")));
        container.add(formCard("Radar móvil", plate, speed, createFine));

        JTextField queryPlate = field("Matrícula para consulta");
        JButton list = secondary("Consultar multas");
        list.addActionListener(ignored -> execute(
            () -> formatFines(tollService.listFines(principal, queryPlate.getText()))
        ));
        container.add(formCard("Consulta", queryPlate, list));
        return container;
    }

    private JPanel buildDriverActions() {
        JPanel container = actionContainer("Área del conductor");
        JLabel plate = new JLabel("Matrícula asociada: " + principal.licensePlate());
        plate.setFont(UiTheme.HEADING);
        plate.setForeground(UiTheme.NAVY);
        container.add(formCard("Vehículo", plate));

        JPanel queryButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        queryButtons.setOpaque(false);
        JButton fines = secondary("Mis multas");
        fines.addActionListener(ignored -> execute(
            () -> formatFines(tollService.listFines(principal, principal.licensePlate()))
        ));
        JButton tickets = secondary("Mis tickets");
        tickets.addActionListener(ignored -> execute(
            () -> formatTickets(tollService.listTickets(principal, principal.licensePlate()))
        ));
        queryButtons.add(fines);
        queryButtons.add(tickets);
        container.add(formCard("Consultas", queryButtons));

        JTextField fineId = field("ID de multa pendiente");
        JButton pay = primary("Pagar multa");
        pay.addActionListener(ignored -> execute(() -> {
            tollService.payFine(principal, parsePositiveLong(fineId.getText(), "ID de multa"));
            return "Multa abonada correctamente.";
        }));
        container.add(formCard("Pago", fineId, pay));
        return container;
    }

    private JPanel actionContainer(String heading) {
        JPanel outer = new JPanel();
        outer.setBackground(UiTheme.SURFACE);
        outer.setBorder(BorderFactory.createEmptyBorder(2, 2, 18, 10));
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(heading);
        label.setFont(UiTheme.TITLE);
        label.setForeground(UiTheme.NAVY);
        label.setAlignmentX(LEFT_ALIGNMENT);
        outer.add(label);
        outer.add(Box.createVerticalStrut(14));
        return outer;
    }

    private JPanel formCard(String title, Component... components) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(UiTheme.WHITE);
        card.setBorder(UiTheme.cardBorder());
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 0, 12, 0);
        JLabel heading = new JLabel(title);
        heading.setFont(UiTheme.HEADING);
        heading.setForeground(UiTheme.NAVY);
        card.add(heading, constraints);

        for (Component component : components) {
            constraints.gridy++;
            constraints.insets = new Insets(4, 0, 4, 0);
            card.add(component, constraints);
        }
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 245));
        wrapper.add(card, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        return wrapper;
    }

    private JTextField field(String placeholder) {
        JTextField field = new JTextField();
        field.putClientProperty("JTextField.placeholderText", placeholder);
        field.setToolTipText(placeholder);
        field.setPreferredSize(new Dimension(280, 40));
        UiTheme.field(field);
        field.setBorder(BorderFactory.createTitledBorder(
            field.getBorder(),
            placeholder,
            javax.swing.border.TitledBorder.LEADING,
            javax.swing.border.TitledBorder.TOP,
            UiTheme.BODY.deriveFont(11f),
            UiTheme.MUTED
        ));
        return field;
    }

    private JButton primary(String label) {
        JButton button = new JButton(label);
        UiTheme.primaryButton(button);
        return button;
    }

    private JButton secondary(String label) {
        JButton button = new JButton(label);
        button.setForeground(UiTheme.BLUE);
        button.setBackground(UiTheme.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new java.awt.Color(147, 197, 253)),
            BorderFactory.createEmptyBorder(9, 13, 9, 13)
        ));
        return button;
    }

    private JPanel buildStatus() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(UiTheme.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new java.awt.Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));
        status.setForeground(UiTheme.MUTED);
        footer.add(status, BorderLayout.WEST);
        return footer;
    }

    private void execute(Supplier<String> operation) {
        try {
            output.setText(operation.get());
            output.setCaretPosition(0);
            status.setForeground(new java.awt.Color(21, 128, 61));
            status.setText("Operación completada · " + LocalDateTime.now().format(DISPLAY_TIME));
        } catch (TollSystemException | IllegalArgumentException exception) {
            output.setText("No se pudo completar la operación.\n\n" + exception.getMessage());
            status.setForeground(UiTheme.DANGER);
            status.setText("Operación rechazada");
        } catch (RuntimeException exception) {
            LOGGER.error("Fallo inesperado en una acción de interfaz", exception);
            output.setText("Se produjo un error inesperado. Consulta el log de la aplicación.");
            status.setForeground(UiTheme.DANGER);
            status.setText("Error interno");
        }
    }

    private String formatTickets(List<TollTicket> tickets) {
        if (tickets.isEmpty()) {
            return "No hay tickets para la matrícula indicada.";
        }
        StringBuilder text = new StringBuilder("TICKETS\n=======\n\n");
        tickets.forEach(ticket -> text.append(formatTicket(ticket)).append("\n\n"));
        return text.toString();
    }

    private String formatFines(List<TrafficFine> fines) {
        if (fines.isEmpty()) {
            return "No hay multas para la matrícula indicada.";
        }
        StringBuilder text = new StringBuilder("MULTAS\n======\n\n");
        fines.forEach(fine -> text.append(formatFine(fine)).append("\n\n"));
        return text.toString();
    }

    private String formatAudit(List<AuditEvent> events) {
        if (events.isEmpty()) {
            return "Todavía no existen eventos de auditoría.";
        }
        StringBuilder text = new StringBuilder("AUDITORÍA RECIENTE\n==================\n\n");
        events.forEach(event -> text
            .append(event.occurredAt().format(DISPLAY_TIME)).append(" | ")
            .append(event.username()).append(" [").append(event.role()).append("] | ")
            .append(event.action()).append(" | ").append(event.detail()).append('\n'));
        return text.toString();
    }

    private String formatTicket(TollTicket ticket) {
        return String.format(
            java.util.Locale.ROOT,
            "#%d · %s · salida %s · tamaño %.2f · %s €",
            ticket.id(),
            ticket.licensePlate(),
            ticket.exitedAt().format(DISPLAY_TIME),
            ticket.vehicleSize(),
            ticket.amount().toPlainString()
        );
    }

    private String formatFine(TrafficFine fine) {
        return String.format(
            java.util.Locale.ROOT,
            "#%d · %s · %s · %.2f km/h · %s € · %s",
            fine.id(),
            fine.licensePlate(),
            fine.radarType(),
            fine.speedKph(),
            fine.amount().toPlainString(),
            fine.paid() ? "PAGADA" : "PENDIENTE"
        );
    }

    private double parsePositive(String value, String label) {
        try {
            double parsed = Double.parseDouble(value.trim());
            if (!Double.isFinite(parsed) || parsed <= 0.0) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Introduce un valor positivo para " + label, exception);
        }
    }

    private long parsePositiveLong(String value, String label) {
        try {
            long parsed = Long.parseLong(value.trim());
            if (parsed < 1) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Introduce un valor positivo para " + label, exception);
        }
    }
}
