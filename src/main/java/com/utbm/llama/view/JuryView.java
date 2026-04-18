package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Vue du mini-jeu "Jury".
 * Contexte : Le joueur a perdu ≥ 20 crédits dans la manche.
 * Il voit 7 cartes face cachée, en choisit une,
 * et gagne la valeur de cette carte (1 à 10) en crédits.
 * Layout :
 *  ┌──────────────────────────────────────────────────────┐
 *  │  En-tête dramatique (titre + contexte)               │
 *  │  Crédits perdus                                      │
 *  │  7 cartes face cachée                                │
 *  │  [CONFIRMER MON CHOIX]                               │
 *  └──────────────────────────────────────────────────────┘
 */
public class JuryView extends JPanel {

    private static final Color BG       = Color.decode("#0D0D0D");
    private static final Color PANEL_BG = Color.decode("#111111");
    private static final Color ACCENT   = Color.decode("#C8A84B");
    private static final Color RED      = Color.decode("#D4526E");
    private static final Color TEXT     = Color.decode("#F0EDE6");
    private static final Color SUB      = Color.decode("#8A8680");

    private final List<CardView> hiddenCards = new ArrayList<>();
    private int                  selectedIndex = -1;
    private Consumer<Integer>    onCardPicked;

    private final JLabel  playerNameLabel;
    private final JLabel  creditsLostLabel;
    private final JLabel  currentCreditsLabel;
    private final JPanel  cardsPanel;
    private final JButton btnConfirm;
    private final JLabel  resultLabel;

    public JuryView() {
        setBackground(BG);
        setLayout(new BorderLayout());

        playerNameLabel    = buildLabel("",              20, Font.BOLD, TEXT);
        creditsLostLabel   = buildLabel("",              15, Font.PLAIN, RED);
        currentCreditsLabel = buildLabel("",             13, Font.ITALIC, SUB);

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        cardsPanel.setBackground(BG);

        btnConfirm = new JButton("VALIDER MON CHOIX");
        btnConfirm.setFont(new Font("Monospaced", Font.BOLD, 14));
        btnConfirm.setBackground(ACCENT);
        btnConfirm.setForeground(Color.decode("#0D0D0D"));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(260, 50));
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirm.setEnabled(false);

        resultLabel = buildLabel("", 18, Font.BOLD, ACCENT);
        resultLabel.setVisible(false);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);

        buildHiddenCards();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.decode("#0A0A0A"));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#2E2E2E")),
                new EmptyBorder(32, 40, 24, 40)
        ));

        JLabel title = new JLabel("⚖  PASSAGE DEVANT LE JURY");
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(RED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Vous avez perdu trop de crédits cette manche.");
        subtitle.setFont(new Font("Serif", Font.ITALIC, 15));
        subtitle.setForeground(SUB);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(6, 0, 0, 0));

        playerNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerNameLabel.setBorder(new EmptyBorder(16, 0, 4, 0));

        creditsLostLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        currentCreditsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(title);
        header.add(subtitle);
        header.add(playerNameLabel);
        header.add(creditsLostLabel);
        header.add(currentCreditsLabel);

        return header;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);

        JLabel instruction = buildLabel(
                "Choisissez UNE carte parmi les 7 — vous gagnerez sa valeur en crédits.",
                14, Font.ITALIC, SUB
        );
        center.add(instruction, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(10, 0, 20, 0);
        center.add(cardsPanel, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 10, 0);
        center.add(resultLabel, gbc);

        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(0, 0, 32, 0));
        footer.add(btnConfirm);
        return footer;
    }

    private void buildHiddenCards() {
        cardsPanel.removeAll();
        hiddenCards.clear();
        selectedIndex = -1;

        CardType[] allTypes = CardType.values();

        for (int i = 0; i < 7; i++) {
            final int idx = i;
            CardView cv = new CardView();
            cv.setSelectable(true);

            cv.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    hiddenCards.forEach(c -> c.setSelected(false));
                    cv.setSelected(true);
                    selectedIndex = idx;
                    btnConfirm.setEnabled(true);
                }
            });

            hiddenCards.add(cv);
            cardsPanel.add(cv);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    /**
     * Révèle la carte choisie et affiche le résultat.
     *
     * @param index    index de la carte choisie (0-6)
     * @param revealed le type de carte révélé
     */
    public void revealCard(int index, CardType revealed) {
        if (index < 0 || index >= hiddenCards.size()) return;

        cardsPanel.remove(hiddenCards.get(index));
        CardView visible = new CardView(revealed);
        cardsPanel.add(visible, index);
        hiddenCards.set(index, visible);

        int gained = revealed.getValue();
        resultLabel.setText("🎲 Vous gagnez " + gained + " crédit" + (gained > 1 ? "s" : "") + " !");
        resultLabel.setVisible(true);

        btnConfirm.setEnabled(false);

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    /**
     * Initialise la vue pour le joueur passant devant le jury.
     *
     * @param playerName     nom du joueur
     * @param creditsLost    crédits perdus cette manche
     * @param currentCredits crédits actuels avant le jury
     */
    public void setup(String playerName, int creditsLost, int currentCredits) {
        playerNameLabel.setText(playerName + " est convoqué·e devant le jury");
        creditsLostLabel.setText("Perte cette manche : −" + creditsLost + " crédits");
        currentCreditsLabel.setText("Crédits actuels : " + currentCredits);
        resultLabel.setVisible(false);
        buildHiddenCards();
        btnConfirm.setEnabled(false);
    }

    /** Callback appelé quand le joueur valide son choix — reçoit l'index (0-6). */
    public void setOnCardPicked(Consumer<Integer> callback) {
        this.onCardPicked = callback;
        btnConfirm.addActionListener(e -> {
            if (selectedIndex >= 0 && callback != null) {
                callback.accept(selectedIndex);
            }
        });
    }

    public void addConfirmListener(ActionListener l) { btnConfirm.addActionListener(l); }
    public int  getSelectedIndex() { return selectedIndex; }


    private JLabel buildLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", style, size));
        lbl.setForeground(color);
        return lbl;
    }
}
