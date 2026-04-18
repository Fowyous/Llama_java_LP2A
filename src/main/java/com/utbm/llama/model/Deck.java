package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Représente un paquet de cartes (pioche ou défausse).
 * Le jeu L.A.M.A. contient 56 cartes : 8 exemplaires de chaque
 * valeur (ONE à LLAMA), soit 7 × 8 = 56 cartes.
 * Cette classe est utilisée à deux endroits différents dans Game :
 *   - drawPile  (pioche) — on pioche depuis le dessus
 *   - discardPile (défausse) — on pose sur le dessus, on consulte le dessus
 * L'implémentation interne utilise une ArrayDeque pour des
 * opérations O(1) en tête et en queue.
 */
public class Deck {

    /** Nombre d'exemplaires de chaque carte dans un paquet complet. */
    public static final int COPIES_PER_CARD = 8;

    private final ArrayDeque<CardType> cards;

    /**
     * Crée un paquet vide.
     */
    public Deck() {
        this.cards = new ArrayDeque<>();
    }

    /**
     * Crée un paquet plein (56 cartes) non mélangé.
     * Appeler {@link #shuffle()} après la création pour l'utiliser comme pioche.
     */
    public static Deck createFull() {
        Deck deck = new Deck();
        for (CardType type : CardType.values()) {
            for (int i = 0; i < COPIES_PER_CARD; i++) {
                deck.cards.addLast(type);
            }
        }
        return deck;
    }

    /**
     * Mélange aléatoirement les cartes du paquet.
     * Utilise {@link Collections#shuffle} — peut être seedé en test
     * en passant par la version surchargée.
     */
    public void shuffle() {
        List<CardType> list = new ArrayList<>(cards);
        Collections.shuffle(list);
        cards.clear();
        cards.addAll(list);
    }

    /**
     * Mélange avec un générateur aléatoire fixe (utile pour les tests).
     *
     * @param random instance de java.util.Random à utiliser
     */
    public void shuffle(java.util.Random random) {
        List<CardType> list = new ArrayList<>(cards);
        Collections.shuffle(list, random);
        cards.clear();
        cards.addAll(list);
    }

    /**
     * Pioche la carte du dessus du paquet.
     *
     * @return la carte piochée
     * @throws java.util.NoSuchElementException si le paquet est vide
     */
    public CardType draw() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException("La pioche est vide");
        }
        return cards.removeFirst();
    }

    /**
     * Consulte la carte du dessus sans la retirer.
     *
     * @return la carte du dessus, ou null si le paquet est vide
     */
    public CardType peek() {
        return cards.peekFirst();
    }

    /**
     * Pose une carte sur le dessus du paquet (défausse).
     *
     * @param card carte à ajouter
     */
    public void add(CardType card) {
        if (card == null) throw new IllegalArgumentException("Impossible d'ajouter une carte null");
        cards.addFirst(card);
    }

    /**
     * @return true si le paquet ne contient aucune carte
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * @return le nombre de cartes restantes dans le paquet
     */
    public int size() {
        return cards.size();
    }

    @Override
    public String toString() {
        return "Deck{taille=" + cards.size() +
                ", dessus=" + (isEmpty() ? "vide" : peek()) + "}";
    }
}
