package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Shows a player’s cards in hand.
 * Handles fanning and selecting a playing card.
 */
public class HandView extends JPanel {

    private static final Color BG = new Color(0, 0, 0, 0);

    private final List<CardView> cardViews = new ArrayList<>();
    private boolean interactive = true;
    private Consumer<CardType> onCardPlayed;

    public HandView() {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 8, 4));
    }

    /**
     * Reload the hand from a list of card types.
     *
     * @param cards  cards in hand
     * @param active if true, the cards are clickable (player’s turn)
     */
    public void updateHand(List<CardType> cards, boolean active) {
        removeAll();
        cardViews.clear();

        for (CardType ct : cards) {
            CardView cv = new CardView(ct);
            cv.setSelectable(active && interactive);

            cv.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (!interactive) return;
                    if (onCardPlayed == null) return;

                    deselectAll();
                    cv.setSelected(true);
                    onCardPlayed.accept(ct);
                }
            });

            cardViews.add(cv);
            add(cv);
        }

        revalidate();
        repaint();
    }

    /**
     * Hide all cards (visible back) — useful for opponents.
     *
     * @param count number of cards to display face down
     */
    public void showHidden(int count) {
        removeAll();
        cardViews.clear();

        for (int i = 0; i < count; i++) {
            CardView cv = new CardView();
            cardViews.add(cv);
            add(cv);
        }

        revalidate();
        repaint();
    }


    public void deselectAll() {
        cardViews.forEach(cv -> cv.setSelected(false));
    }

    /**
     * @return the currently selected card, or null if none.
     */
    public CardType getSelectedCard() {
        return cardViews.stream()
                .filter(CardView::isSelected)
                .map(CardView::getCardType)
                .findFirst()
                .orElse(null);
    }

    /**
     * Save the callback called when the player clicks on a card.
     */
    public void setOnCardPlayed(Consumer<CardType> callback) {
        this.onCardPlayed = callback;
    }

    /**
     * Enables or disables hand interactivity.
     */
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
        cardViews.forEach(cv -> cv.setSelectable(interactive));
    }

    /**
     * Returns the number of cards displayed.
     */
    public int getCardCount() {
        return cardViews.size();
    }
}