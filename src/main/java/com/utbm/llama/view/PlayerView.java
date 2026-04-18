package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.State;
import test.java.com.utbm.modeltest.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Panneau représentant un joueur (humain ou bot) sur le plateau.
 * Affiche :
 * - le nom et le type (bot/humain)
 * - les crédits actuels
 * - la main (visible pour le joueur local, cachée pour les autres)
 * - les états spéciaux : "Semestre à l'étranger", "Jury", "Césure"
 * - un indicateur "tour actif"
 */
public class PlayerView extends JPanel {

    private static final Color BG_IDLE = Color.decode("#141414");
    private static final Color BG_ACTIVE = Color.decode("#1C1C1C");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color TEXT_MAIN = Color.decode("#F0EDE6");
    private static final Color TEXT_SUB = Color.decode("#8A8680");
    private static final Color BORDER_ACT = Color.decode("#C8A84B");
    private static final Color BORDER_IDL = Color.decode("#2E2E2E");
    private static final Color GREEN = Color.decode("#4CAF7D");
    private static final Color RED = Color.decode("#D4526E");
    private static final Color BLUE = Color.decode("#5B9BD5");

    private final String playerName;
    private final boolean isBot;

    private final JLabel nameLabel;
    private final JLabel creditsLabel;
    private final JLabel statusBadge;
    private final HandView handView;
    private final JLabel activeIndicator;

    public PlayerView(String playerName, boolean isBot) {
        this.playerName = playerName;
        this.isBot = isBot;

        setBackground(BG_IDLE);
        setBorder(new CompoundBorder(
                new LineBorder(BORDER_IDL, 1),
                new EmptyBorder(12, 14, 12, 14)
        ));
        setLayout(new BorderLayout(8, 8));

        activeIndicator = new JLabel("▶", SwingConstants.CENTER);
        activeIndicator.setFont(new Font("Monospaced", Font.BOLD, 12));
        activeIndicator.setForeground(ACCENT);
        activeIndicator.setVisible(false);
        activeIndicator.setPreferredSize(new Dimension(18, 18));

        nameLabel = new JLabel(buildNameText());
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_MAIN);

        creditsLabel = new JLabel("35 crédits");
        creditsLabel.setFont(new Font("Serif", Font.BOLD, 20));
        creditsLabel.setForeground(ACCENT);

        statusBadge = new JLabel();
        statusBadge.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusBadge.setBorder(new EmptyBorder(2, 6, 2, 6));
        statusBadge.setVisible(false);

        JPanel topLeft = new JPanel();
        topLeft.setOpaque(false);
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));
        topLeft.add(nameLabel);
        topLeft.add(statusBadge);

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        header.add(activeIndicator, BorderLayout.WEST);
        header.add(topLeft, BorderLayout.CENTER);
        header.add(creditsLabel, BorderLayout.EAST);

        handView = new HandView();

        add(header, BorderLayout.NORTH);
        add(handView, BorderLayout.CENTER);
    }

    /**
     * Met à jour l'intégralité du panneau depuis le modèle Player.
     *
     * @param player   le modèle à afficher
     * @param isLocal  true si c'est le joueur local (main visible)
     * @param isActive true si c'est le tour de ce joueur
     */
    public void update(Player player, boolean isLocal, boolean isActive) {
        updateCredits(player.getCredits());
        updateActive(isActive);
        updateStatus(player);

        if (isLocal) {
            handView.updateHand(player.getHand(), isActive);
        } else {
            handView.showHidden(player.getHand().size());
        }
    }

    /**
     * Met à jour l'affichage des crédits avec couleur selon la valeur.
     */
    public void updateCredits(int credits) {
        creditsLabel.setText(credits + " crédits");
        if (credits >= 180) {
            creditsLabel.setForeground(GREEN);
        } else if (credits < 0) {
            creditsLabel.setForeground(RED);
        } else if (credits < 20) {
            creditsLabel.setForeground(Color.decode("#E07B54"));
        } else {
            creditsLabel.setForeground(ACCENT);
        }
    }

    /**
     * Bascule l'indicateur de tour actif.
     */
    public void updateActive(boolean active) {
        activeIndicator.setVisible(active);
        setBackground(active ? BG_ACTIVE : BG_IDLE);
        setBorder(new CompoundBorder(
                new LineBorder(active ? BORDER_ACT : BORDER_IDL, active ? 2 : 1),
                new EmptyBorder(12, 14, 12, 14)
        ));
    }

    /**
     * Met à jour les badges d'état spéciaux.
     */
    public void updateStatus(Player player) {
        if (player.isSuspended()) {
            showBadge("CÉSURE EN COURS", RED);
        } else if (player.hasStudyAbroad()) {
            showBadge("SEMESTRE À L'ÉTRANGER — 4 cartes", BLUE);
        } else if (player.getState() == State.QUITTING) {
            showBadge("A PASSÉ LA MANCHE", TEXT_SUB);
        } else {
            statusBadge.setVisible(false);
        }
    }

    private void showBadge(String text, Color color) {
        statusBadge.setText(text);
        statusBadge.setForeground(color);
        statusBadge.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80), 1),
                new EmptyBorder(2, 6, 2, 6)
        ));
        statusBadge.setVisible(true);
    }

    public HandView getHandView() {
        return handView;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isBot() {
        return isBot;
    }

    private String buildNameText() {
        return playerName + (isBot ? "  🤖" : "");
    }
}
