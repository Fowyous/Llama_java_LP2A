package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Represents a pile of cards (draw or discard).
 * Implemented as a Deque:
 * - We draw from the TOP (pollFirst)
 * - We add to the TOP (addFirst = discard) or at the bottom (addLast = filling)
 * Usage:
 * Deck draw   = Deck.createFull();
 * Deck discard = Deck.empty();
 */
public class Deck {

    private final Deque<CardType> cards = new ArrayDeque<>();

    /**
     * Creates a full draw pile (8 copies of each card = 56 cards)
     * and the mixture.
     */
    public static Deck createFull() {
        Deck deck = new Deck();
        for (int i = 0; i < 8; i++) {
            for (CardType ct : CardType.values()) {
                deck.cards.addLast(ct);
            }
        }
        deck.shuffle();
        return deck;
    }

    /**
     * Creates an empty stack (used for the discard at startup).
     */
    public static Deck empty() {
        return new Deck();
    }

    private Deck() {
    }

    /**
     * Shuffle randomly all cards in the pile.
     */
    public void shuffle() {
        List<CardType> list = new ArrayList<>(cards);
        Collections.shuffle(list);
        cards.clear();
        cards.addAll(list);
    }

    /**
     * Remove and turn over the top card.
     *
     * @return the top card, or {@code null} if the heap is empty
     */
    public CardType draw() {
        return cards.pollFirst();
    }

    /**
     * Turn over the top card WITHOUT removing it.
     *
     * @return the top card, or {@code null} if the heap is empty
     */
    public CardType peek() {
        return cards.peekFirst();
    }

    /**
     * Add a card ABOVE the pile (for discard).
     *
     * @param card the card to put on
     */
    public void add(CardType card) {
        if (card == null) throw new IllegalArgumentException("Impossible d'ajouter une carte null");
        cards.addFirst(card);
    }

    /**
     * @return true if the heap does not contain any cards
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * @return the number of cards in the pile
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns all cards as a list (defensive copy).
     * The first card in the list is the one on top of the pile.
     */
    public List<CardType> getCards() {
        return new ArrayList<>(cards);
    }

    /**
     * Empty the pile completely.
     * Used at the beginning of the round to reset the discard.
     */
    public void clear() {
        cards.clear();
    }

    @Override
    public String toString() {
        return "Deck[" + cards.size() + " cartes, dessus=" + peek() + "]";
    }
}