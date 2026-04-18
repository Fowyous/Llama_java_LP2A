package main.java.com.utbm.llama.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Vue du "Semestre de Césure".
 * Contexte : le joueur a des crédits négatifs après le jury.
 * Il passe toute une manche sans jouer mais reçoit quand même
 * les 35 crédits au début de la manche suivante.
 * En 1v1 : les deux joueurs passent la manche simultanément.
 */
public class CesureView extends JPanel {

    private static final Color BG = Color.decode("#0D0D0D");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color BLUE = Color.decode("#5B9BD5");
    private static final Color TEXT = Color.decode("#F0EDE6");
    private static final Color SUB = Color.decode("#8A8680");
    private static final Color RED = Color.decode("#D4526E");

    private final JLabel playerNameLabel;
    private final JLabel creditsLabel;
    private final JLabel descriptionLabel;
    private final JLabel roundInfoLabel;
    private final JButton btnContinue;

    public CesureView() {
        setBackground(BG);
        setLayout(new GridBagLayout());

        playerNameLabel = buildLabel("", 22, Font.BOLD, TEXT);
        creditsLabel = buildLabel("", 16, Font.BOLD, RED);
        descriptionLabel = buildLabel("", 14, Font.ITALIC, SUB);
        roundInfoLabel = buildLabel("", 13, Font.PLAIN, BLUE);

        btnContinue = new JButton("CONTINUER →");
        btnContinue.setFont(new Font("Monospaced", Font.BOLD, 14));
        btnContinue.setBackground(ACCENT);
        btnContinue.setForeground(Color.decode("#0D0D0D"));
        btnContinue.setFocusPainted(false);
        btnContinue.setPreferredSize(new Dimension(240, 50));
        btnContinue.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 24, 0);
        add(buildIconPanel(), gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        add(playerNameLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 16, 0);
        add(creditsLabel, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 8, 0);
        add(buildInfoCard(), gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(12, 0, 0, 0);
        add(roundInfoLabel, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(32, 0, 0, 0);
        add(btnContinue, gbc);
    }

    private JPanel buildIconPanel() {
        JPanel icon = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(91, 155, 213, 40));
                g2.fillOval(0, 0, 100, 100);

                g2.setColor(BLUE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(1, 1, 98, 98);

                g2.setFont(new Font("Serif", Font.PLAIN, 48));
                FontMetrics fm = g2.getFontMetrics();
                String emoji = "✈";
                g2.setColor(BLUE);
                g2.drawString(emoji, (100 - fm.stringWidth(emoji)) / 2, 62);

                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(100, 100);
            }
        };
        icon.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JLabel title = buildLabel("SEMESTRE DE CÉSURE", 28, Font.BOLD, TEXT);
        title.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.add(icon);

        wrapper.add(title, BorderLayout.NORTH);
        wrapper.add(row, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel buildInfoCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#111111"));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.setColor(Color.decode("#2E2E2E"));
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(20, 32, 20, 32));
        card.setMaximumSize(new Dimension(560, 200));

        String[] lines = {
                "Vous ne jouez PAS la prochaine manche.",
                "Vous recevrez quand même 35 crédits au début de la manche suivante.",
                "En cas de 1v1, les deux joueurs passent la manche simultanément."
        };

        for (String line : lines) {
            JLabel lbl = buildLabel("• " + line, 13, Font.PLAIN, SUB);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            lbl.setBorder(new EmptyBorder(3, 0, 3, 0));
            card.add(lbl);
        }

        card.add(Box.createVerticalStrut(12));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(descriptionLabel);

        return card;
    }

    /**
     * Configure la vue pour un joueur spécifique.
     *
     * @param playerName     nom du joueur en césure
     * @param currentCredits crédits actuels (négatifs)
     * @param nextRound      numéro de la manche sautée
     * @param isBothSkipping true si les deux joueurs sautent (1v1)
     */
    public void setup(String playerName, int currentCredits, int nextRound, boolean isBothSkipping) {
        playerNameLabel.setText(playerName + " part en césure");
        creditsLabel.setText("Crédits actuels : " + currentCredits + " (négatif)");
        roundInfoLabel.setText("Manche " + nextRound + " sautée — +35 crédits au retour");

        if (isBothSkipping) {
            descriptionLabel.setText("Mode 1v1 : les deux joueurs sautent la manche " + nextRound + ".");
            descriptionLabel.setForeground(ACCENT);
        } else {
            descriptionLabel.setText("");
        }
    }

    public void addContinueListener(ActionListener l) {
        btnContinue.addActionListener(l);
    }

    private JLabel buildLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", style, size));
        lbl.setForeground(color);
        return lbl;
    }
}
