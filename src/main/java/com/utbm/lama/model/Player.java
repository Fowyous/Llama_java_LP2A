package main.java.com.utbm.lama.model;

import main.java.com.utbm.lama.model.enums.CardType;
import main.java.com.utbm.lama.model.enums.State;
import main.java.com.utbm.lama.model.enums.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Représente un joueur humain dans la partie.
 * Responsabilités :
 *   - Gérer sa main (cartes en main)
 *   - Gérer ses jetons personnels (pénalités accumulées)
 *   - Calculer son score courant
 *   - Suivre son état dans la manche (PLAYING / QUITTING)
 * Note : Player ne contient aucune logique de jeu.
 * C'est Game + RuleEngine qui décident quand et comment
 * appeler les méthodes de Player.
 */
public class Player {

    private final String name;
    private final List<CardType> hand;
    private final Map<TokenType, Integer> tokens;
    private State state;

    public Player(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du joueur ne peut pas être vide");
        }
        this.name   = name;
        this.hand   = new ArrayList<>();
        this.tokens = new EnumMap<>(TokenType.class);
        for (TokenType t : TokenType.values()) tokens.put(t, 0);
        this.state  = State.PLAYING;
    }

    public void addCard(CardType card) {
        if (card == null) throw new IllegalArgumentException("La carte ne peut pas être null");
        hand.add(card);
    }

    public boolean removeCard(CardType card) {
        return hand.remove(card);
    }

    public List<CardType> getHand() {
        return Collections.unmodifiableList(hand);
    }

    public boolean hasEmptyHand() {
        return hand.isEmpty();
    }

    public boolean hasCard(CardType card) {
        return hand.contains(card);
    }

    public void clearHand() {
        hand.clear();
    }

    public void addToken(TokenType token) {
        tokens.put(token, tokens.get(token) + 1);
    }

    public boolean removeToken(TokenType token) {
        int current = tokens.getOrDefault(token, 0);
        if (current <= 0) return false;
        tokens.put(token, current - 1);
        return true;
    }

    /**
     * Échange 10 blancs contre 1 noir (optimisation du score).
     * @return true si l'échange a eu lieu
     */
    public boolean exchangeWhiteForBlack() {
        if (tokens.getOrDefault(TokenType.WHITE, 0) >= 10) {
            tokens.put(TokenType.WHITE, tokens.get(TokenType.WHITE) - 10);
            tokens.put(TokenType.BLACK, tokens.get(TokenType.BLACK) + 1);
            return true;
        }
        return false;
    }

    public int getTokenCount(TokenType token) {
        return tokens.getOrDefault(token, 0);
    }

    /**
     * score = (nb BLACK × 10) + (nb WHITE × 1)
     * Plus le score est bas, mieux c'est.
     */
    public int computeScore() {
        int total = 0;
        for (TokenType t : TokenType.values()) {
            total += tokens.getOrDefault(t, 0) * t.getPenaltyValue();
        }
        return total;
    }

    public State getState() { return state; }

    public void changeState(State state) {
        if (state == null) throw new IllegalArgumentException("L'état ne peut pas être null");
        this.state = state;
    }

    public void resetForNewRound() { this.state = State.PLAYING; }

    public boolean isPlaying() { return state == State.PLAYING; }

    public String getName() { return name; }

    @Override
    public String toString() {
        return String.format("Player{name='%s', hand=%s, score=%d, state=%s}",
                name, hand, computeScore(), state);
    }
}
