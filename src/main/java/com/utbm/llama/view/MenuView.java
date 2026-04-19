package main.java.com.utbm.llama.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Écran d'accueil de LAMA UTBM.
 * Présente le titre du jeu et les trois actions principales.
 */
public class MenuView extends JPanel {

    private static final Color BG = Color.decode("#0D0D0D");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color TEXT_MAIN = Color.decode("#F0EDE6");
    private static final Color TEXT_SUB = Color.decode("#8A8680");
    private static final Color BTN_BG = Color.decode("#1A1A1A");
    private static final Color BTN_HOVER = Color.decode("#252525");
    private static final Color BTN_BORDER = Color.decode("#2E2E2E");

    private final JButton btnStart;
    private final JButton btnSettings;
    private final JButton btnQuit;

    public MenuView() {
        setLayout(new BorderLayout());
        setBackground(BG);
        setBorder(new EmptyBorder(0, 0, 0, 0));

        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        btnStart = buildMenuButton("▶  NOUVELLE PARTIE", true);
        btnSettings = buildMenuButton("⚙  PARAMÈTRES", false);
        btnQuit = buildMenuButton("✕  QUITTER", false);

        removeAll();
        add(buildCenterPanelWithButtons(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildCenterPanelWithButtons() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(80, 0, 80, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("L.A.M.A", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 96));
        titleLabel.setForeground(ACCENT);

        JLabel subtitleLabel = new JLabel("UTBM EDITION", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Monospaced", Font.PLAIN, 16));
        subtitleLabel.setForeground(TEXT_SUB);
        subtitleLabel.setBorder(new EmptyBorder(0, 0, 16, 0));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(ACCENT);
        sep.setPreferredSize(new Dimension(300, 1));
        sep.setMaximumSize(new Dimension(300, 1));

        JLabel taglineLabel = new JLabel("Survivre au cursus, c'est tout un art.", SwingConstants.CENTER);
        taglineLabel.setFont(new Font("Serif", Font.ITALIC, 18));
        taglineLabel.setForeground(TEXT_SUB);
        taglineLabel.setBorder(new EmptyBorder(16, 0, 60, 0));

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panel.add(subtitleLabel, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(sep, gbc);
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridy = 3;
        panel.add(taglineLabel, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(btnStart, gbc);

        gbc.gridy = 5;
        panel.add(btnSettings, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(24, 0, 0, 0);
        panel.add(btnQuit, gbc);

        return panel;
    }

    /**
     * Placeholder — remplacé dans le constructeur.
     */
    private JPanel buildCenterPanel() {
        JPanel p = new JPanel();
        p.setBackground(BG);
        return p;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel version = new JLabel("v1.0.0 — UTBM Game Studio");
        version.setFont(new Font("Monospaced", Font.PLAIN, 11));
        version.setForeground(TEXT_SUB);
        footer.add(version);

        return footer;
    }

    private JButton buildMenuButton(String text, boolean isPrimary) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = isPrimary
                        ? (getModel().isRollover() ? ACCENT.darker() : ACCENT)
                        : (getModel().isRollover() ? BTN_HOVER : BTN_BG);

                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));

                if (!isPrimary) {
                    g2.setColor(BTN_BORDER);
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 8, 8));
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setForeground(isPrimary ? Color.decode("#0D0D0D") : TEXT_MAIN);
        btn.setPreferredSize(new Dimension(320, 52));
        btn.setMaximumSize(new Dimension(320, 52));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }

    public void addStartListener(ActionListener l) {
        btnStart.addActionListener(l);
    }

    public void addSettingsListener(ActionListener l) {
        btnSettings.addActionListener(l);
    }

    public void addQuitListener(ActionListener l) {
        btnQuit.addActionListener(l);
    }

    public JButton getBtnStart() {
        return btnStart;
    }

    public JButton getBtnSettings() {
        return btnSettings;
    }

    public JButton getBtnQuit() {
        return btnQuit;
    }
}
