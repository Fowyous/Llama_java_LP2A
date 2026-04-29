package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Show the top card of the discard pile.
 */
class DiscardPileView extends JPanel {

    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color SUB = Color.decode("#8A8680");

    private CardView topCardView;
    private final JLabel label;
    private final JPanel cardHolder;

    /**
     * Initializes the discard pile component, setting up a card holder container and the "DÉFAUSSE" label with a default empty state.
     */
    public DiscardPileView() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        cardHolder = new JPanel(new GridBagLayout());
        cardHolder.setOpaque(false);
        cardHolder.setPreferredSize(new Dimension(CardView.CARD_W + 8, CardView.CARD_H + 8));
        cardHolder.setMinimumSize(cardHolder.getPreferredSize());
        cardHolder.setMaximumSize(cardHolder.getPreferredSize());
        cardHolder.setAlignmentX(Component.CENTER_ALIGNMENT);

        label = new JLabel("DÉFAUSSE", SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.BOLD, 10));
        label.setForeground(ACCENT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel empty = new JLabel("(vide)", SwingConstants.CENTER);
        empty.setFont(new Font("Serif", Font.ITALIC, 12));
        empty.setForeground(SUB);
        cardHolder.add(empty);

        add(cardHolder);
        add(Box.createVerticalStrut(6));
        add(label);
    }

    /**
     * Updates the card displayed on the discard.
     *
     * @param topCard null if the discard is empty
     */
    public void render(CardType topCard) {
        cardHolder.removeAll();

        if (topCard == null) {
            JLabel empty = new JLabel("(vide)", SwingConstants.CENTER);
            empty.setFont(new Font("Serif", Font.ITALIC, 12));
            empty.setForeground(SUB);
            cardHolder.add(empty);
        } else {
            topCardView = new CardView(topCard);
            cardHolder.add(topCardView);
        }

        cardHolder.revalidate();
        cardHolder.repaint();
    }

    /**
     * Retrieves the CardView instance currently displayed at the top of the discard pile.
     */
    public CardView getTopCardView() {
        return topCardView;
    }
}