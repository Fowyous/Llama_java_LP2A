package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Affiche les cartes en main d'un joueur.
 * Gère l'affichage en éventail et la sélection d'une carte à jouer.
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
     * Recharge la main à partir d'une liste de types de cartes.
     *
     * @param cards  cartes en main
     * @param active si true, les cartes sont cliquables (tour du joueur)
     */
    // ✅ APRÈS — TOUJOURS ajouter le listener, vérifier les conditions AU MOMENT DU CLIC
    public void updateHand(List<CardType> cards, boolean active) {
        removeAll();
        cardViews.clear();

        for (CardType ct : cards) {
            CardView cv = new CardView(ct);
            cv.setSelectable(active && interactive);  // visuel seulement

            // TOUJOURS ajouter le listener — pas de condition ici
            cv.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // Vérifie les conditions AU MOMENT du clic, pas à la création
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
     * Masque toutes les cartes (dos visible) — utile pour les adversaires.
     *
     * @param count nombre de cartes à afficher face cachée
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
     * @return la carte actuellement sélectionnée, ou null si aucune.
     */
    public CardType getSelectedCard() {
        return cardViews.stream()
                .filter(CardView::isSelected)
                .map(CardView::getCardType)
                .findFirst()
                .orElse(null);
    }

    /**
     * Enregistre le callback appelé quand le joueur clique sur une carte.
     */
    public void setOnCardPlayed(Consumer<CardType> callback) {
        this.onCardPlayed = callback;
    }

    /**
     * Active ou désactive l'interactivité de la main.
     */
    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
        cardViews.forEach(cv -> cv.setSelectable(interactive));
    }

    /**
     * Retourne le nombre de cartes affichées.
     */
    public int getCardCount() {
        return cardViews.size();
    }
}