package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * View of the "Jury" mini-game.
 * Context: The player lost ≥ 20 credits in the round.
 * He sees 7 cards face down, chooses one,
 * and wins the value of this card (1 to 10) in credits.
 * Layout:
 * ┌──────────────────────────────────────────────────────┐
 * │           Dramatic header (title + context)          │
 * │                    Lost credits                      │
 * │                  7 cards face down                   │
 * │                 [CONFIRM MY CHOICE]                  │
 * └──────────────────────────────────────────────────────┘
 */
public class JuryView extends JPanel implements LocaleChangeListener{

    private static final Color BG = Color.decode("#0D0D0D");
    private static final Color PANEL_BG = Color.decode("#111111");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color RED = Color.decode("#D4526E");
    private static final Color TEXT = Color.decode("#F0EDE6");
    private static final Color SUB = Color.decode("#8A8680");

    // Locale and bundle
    private Locale currentLocale;
    private ResourceBundle bundle;
    
    private final List<CardView> hiddenCards = new ArrayList<>();
    private int selectedIndex = -1;
    private Consumer<Integer> onCardPicked;

    private final JLabel playerNameLabel;
    private final JLabel creditsLostLabel;
    private final JLabel currentCreditsLabel;
    private final JPanel cardsPanel;
    private final JButton btnConfirm;
    private final JLabel resultLabel;

    private final JLabel instructionLabel;

    // Data for re-rendering on locale change
    private String lastPlayerName;
    private int lastCreditsLost;
    private int lastCurrentCredits;
    private CardType lastRevealedCard;
    private int lastRevealedIndex;
    private boolean hasRevealed = false;
    
    /**
     * Initializes the mini-game interface, setting up the dramatic dark theme,
     * building the layout structure, and preparing the initial set of hidden cards.
     * @param mainFrame to add the locale change listener.
     */
    public JuryView(MainFrame mainFrame) {
        this.currentLocale = mainFrame.getCurrentLocale();
        this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);
        mainFrame.addLocaleChangeListener(this);

        setBackground(BG);
        setLayout(new BorderLayout());

        playerNameLabel = buildLabel("", 20, Font.BOLD, TEXT);
        creditsLostLabel = buildLabel("", 15, Font.PLAIN, RED);
        currentCreditsLabel = buildLabel("", 13, Font.ITALIC, SUB);

        cardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 8));
        cardsPanel.setBackground(BG);

        btnConfirm = new JButton(bundle.getString("jury.button_confirm"));
        btnConfirm.setFont(new Font("Monospaced", Font.BOLD, 14));
        btnConfirm.setBackground(ACCENT);
        btnConfirm.setForeground(Color.decode("#0D0D0D"));
        btnConfirm.setFocusPainted(false);
        btnConfirm.setPreferredSize(new Dimension(260, 50));
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirm.setEnabled(false);

        instructionLabel = buildLabel("", 14, Font.ITALIC, SUB);

        resultLabel = buildLabel("", 18, Font.BOLD, ACCENT);
        resultLabel.setVisible(false);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        buildHiddenCards();
    }

    /**
     * Constructs the top panel containing the jury title,
     * contextual subtitles, and the current player's financial status.
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.decode("#0A0A0A"));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#2E2E2E")),
                new EmptyBorder(32, 40, 24, 40)
        ));

        JLabel title = new JLabel("⚖  " + bundle.getString("jury.title"));
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(RED);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(bundle.getString("jury.subtitle"));
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

    /**
     * Sets up the central gameplay area where the instruction label,
     * the card selection panel, and the eventual result label are positioned.
     */
    private JPanel buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 10, 0);

        instructionLabel.setText(bundle.getString("jury.instruction"));
        center.add(instructionLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 20, 0);
        center.add(cardsPanel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        center.add(resultLabel, gbc);

        return center;
    }

    /**
     * Creates the bottom action area containing the confirmation button for the player's card choice.
     */
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(0, 0, 32, 0));
        footer.add(btnConfirm);
        return footer;
    }

    /**
     * Generates seven interactive face-down cards, clears previous selections,
     * and attaches click listeners to handle card selection and UI highlighting.
     */
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
     * Reveals the chosen card and displays the result.
     *
     * @param index    index of the chosen map (0-6)
     * @param revealed the type of map revealed
     */
    public void revealCard(int index, CardType revealed) {
        if (index < 0 || index >= hiddenCards.size()) return;
        
        // Store reveal data for potential re-rendering
        this.lastRevealedIndex = index;
        this.lastRevealedCard = revealed;
        this.hasRevealed = true;

        cardsPanel.remove(hiddenCards.get(index));
        CardView visible = new CardView(revealed);
        cardsPanel.add(visible, index);
        hiddenCards.set(index, visible);

        int gained = revealed.getValue();
        String creditWord = bundle.getString("jury.credit");
        resultLabel.setText("🎲 " + bundle.getString("jury.you_won") + gained + creditWord + (gained > 1 ? "s" : "") + " !");
        resultLabel.setVisible(true);

        btnConfirm.setEnabled(false);

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    /**
     * Initializes the view for the player passing in front of the jury.
     *
     * @param playerName     name
     * @param creditsLost    lost credits this round
     * @param currentCredits current credits before the jury
     */
    public void setup(String playerName, int creditsLost, int currentCredits) {
        // Store data for re-rendering on locale change
        this.lastPlayerName = playerName;
        this.lastCreditsLost = creditsLost;
        this.lastCurrentCredits = currentCredits;
        this.hasRevealed = false;

        playerNameLabel.setText(playerName + " " + bundle.getString("jury.player_summoned"));
        creditsLostLabel.setText(bundle.getString("jury.loss_this_round") + " −" + creditsLost + bundle.getString("jury.credit"));
        currentCreditsLabel.setText(bundle.getString("jury.current_credits") + currentCredits);
        resultLabel.setVisible(false);
        buildHiddenCards();
        btnConfirm.setEnabled(false);
    }

    /**
     * Callback called when the player validates their choice—receives the index (0-6).
     */
    public void setOnCardPicked(Consumer<Integer> callback) {
        this.onCardPicked = callback;
        btnConfirm.addActionListener(e -> {
            if (selectedIndex >= 0 && callback != null) {
                callback.accept(selectedIndex);
            }
        });
    }

    /**
     * Attaches a standard ActionListener to the confirmation button for external controller management.
     */
    public void addConfirmListener(ActionListener l) {
        btnConfirm.addActionListener(l);
    }

    /**
     * Returns the index of the currently selected card (0 through 6).
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Utility helper to create a stylized JLabel with specific font properties and alignment for a consistent UI look.
     */
    private JLabel buildLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", style, size));
        lbl.setForeground(color);
        return lbl;
    }
    

    @Override 
    public void onLocaleChange(Locale locale) {        
    	this.currentLocale = locale;        
    	this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);
    	
    	// Update all locale-dependent UI elements        
    	btnConfirm.setText(bundle.getString("jury.button_confirm"));        
    	instructionLabel.setText(bundle.getString("jury.instruction"));
    	
        // Re-render the entire jury view with new locale if data exists        
    	if (lastPlayerName != null) {            
    		setup(lastPlayerName, lastCreditsLost, lastCurrentCredits);
    	}
    	// Re-reveal the card if it was already revealed            
    	if (hasRevealed && lastRevealedCard != null) {                
    		revealCard(lastRevealedIndex, lastRevealedCard);            
    	}        
    }
}
