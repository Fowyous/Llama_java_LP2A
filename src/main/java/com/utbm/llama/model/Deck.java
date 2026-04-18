package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Représente un tas de cartes (pioche ou défausse).
 * Implémenté comme une Deque :
 *  - On pioche depuis le HAUT (pollFirst)
 *  - On ajoute au HAUT (addFirst = défausse) ou en bas (addLast = remplissage)
 * Usage :
 *  Deck draw    = Deck.createFull();   // pioche complète mélangée
 *  Deck discard = Deck.empty();        // défausse vide
 */
public class Deck {

    private final Deque<CardType> cards = new ArrayDeque<>();

    /**
     * Crée une pioche complète (8 exemplaires de chaque carte = 56 cartes)
     * et la mélange.
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

    /** Crée une pile vide (utilisée pour la défausse au démarrage). */
    public static Deck empty() {
        return new Deck();
    }

    private Deck() {}

    /**
     * Mélange aléatoirement toutes les cartes du tas.
     */
    public void shuffle() {
        List<CardType> list = new ArrayList<>(cards);
        Collections.shuffle(list);
        cards.clear();
        cards.addAll(list);
    }

    /**
     * Retire et retourne la carte du dessus.
     *
     * @return la carte du dessus, ou {@code null} si le tas est vide
     */
    public CardType draw() {
        return cards.pollFirst();
    }

    /**
     * Retourne la carte du dessus SANS la retirer.
     *
     * @return la carte du dessus, ou {@code null} si le tas est vide
     */
    public CardType peek() {
        return cards.peekFirst();
    }

    /**
     * Ajoute une carte AU DESSUS du tas (pour la défausse).
     *
     * @param card la carte à poser
     */
    public void add(CardType card) {
        if (card == null) throw new IllegalArgumentException("Impossible d'ajouter une carte null");
        cards.addFirst(card);
    }

    /**
     * @return true si le tas ne contient aucune carte
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * @return le nombre de cartes dans le tas
     */
    public int size() {
        return cards.size();
    }

    /**
     * Retourne toutes les cartes sous forme de liste (copie défensive).
     * La première carte de la liste est celle du dessus du tas.
     */
    public List<CardType> getCards() {
        return new ArrayList<>(cards);
    }

    /**
     * Vide complètement le tas.
     * Utilisé en début de manche pour réinitialiser la défausse.
     */
    public void clear() {
        cards.clear();
    }

    @Override
    public String toString() {
        return "Deck[" + cards.size() + " cartes, dessus=" + peek() + "]";
    }
}