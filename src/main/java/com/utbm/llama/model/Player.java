package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a human player in the game LAMA UTBM.
 * <p>
 * Managed Data:
 * - Card hand
 * - Credits (replaces points from the original game)
 * - State in the sleeve (PLAYING / QUITTING)
 * - Special flags: semester abroad, gap semester
 * - Tracking lost credits in the round (to trigger the jury)
 */
public class Player {

    private final String name;

    private final List<CardType> hand = new ArrayList<>();

    /**
     * Player’s current credits. May be negative.
     */
    private int credits = 0;

    /**
     * Credits lost during the current round.
     * Reset to 0 at the beginning of each round.
     * Used to trigger the jury (threshold ≥ 20).
     */
    private int creditsLostThisRound = 0;

    /**
     * Number of cards dealt at the beginning of the round.
     * Is 6 by default, 4 if the player benefits from the semester abroad.
     */
    private int startingHandSize = 6;

    /**
     * true → the player emptied his hand in the previous round.
     * He starts the next round with 4 cards instead of 6.
     * This flag is reset to false at the beginning of the round where it applies.
     * Not cumulative.
     */
    private boolean studyAbroad = false;

    /**
     * true → the player is in a gap semester.
     * He skips the next round but still gets the +35 credits.
     * Reset to false at the beginning of the round where it starts again.
     */
    private boolean suspended = false;

    private State state = State.PLAYING;

    public Player(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Le nom du joueur ne peut pas être vide");
        this.name = name;
    }

    /**
     * Adds a card to the player’s hand.
     *
     * @param card the card to add
     */
    public void addCard(CardType card) {
        if (card == null) throw new IllegalArgumentException("Carte null impossible");
        hand.add(card);
    }

    /**
     * Removes a card from the player’s hand.
     *
     * @param card the card to be withdrawn
     * @throws IllegalStateException if the card is not in the hand
     */
    public void removeCard(CardType card) {
        if (!hand.remove(card))
            throw new IllegalStateException(name + " n'a pas la carte " + card + " en main");
    }

    /**
     * Completely empties the player’s hand.
     * Used between two rounds.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Returns the hand as a non-editable list.
     */
    public List<CardType> getHand() {
        return Collections.unmodifiableList(hand);
    }

    /**
     * Calculates the total value of cards still in hand.
     * This is the amount that will be deducted from the credits at the end of the round.
     *
     * @return sum of the values of the cards in hand
     */
    public int computeHandValue() {
        return hand.stream()
                .mapToInt(CardType::getValue)
                .sum();
    }

    /**
     * Adds (or subtracts if negative) a number of credits.
     *
     * @param amount to add (can be negative)
     */
    public void addCredits(int amount) {
        if (amount < 0) {
            creditsLostThisRound += Math.abs(amount);
        }
        credits += amount;
    }

    /**
     * @return current credits (can be negative)
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Forces the value of credits (use with caution).
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * @return the credits lost during the current round (≥ 0)
     */
    public int getCreditsLostThisRound() {
        return creditsLostThisRound;
    }

    /**
     * Reset the loss counter to 0 (called at the beginning of each round).
     */
    public void resetCreditsLostThisRound() {
        creditsLostThisRound = 0;
    }

    /**
     * @return true if the player benefits from the semester abroad (4 cards).
     */
    public boolean hasStudyAbroad() {
        return studyAbroad;
    }

    /**
     * Activates or deactivates the flag semester abroad.
     * When true, startingHandSize is automatically set to 4.
     */
    public void setStudyAbroad(boolean studyAbroad) {
        this.studyAbroad = studyAbroad;
        this.startingHandSize = studyAbroad ? 4 : 6;
    }

    /**
     * @return true if the player is in a gap semester (skips a round).
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * Enables or disables suspension (gap semester).
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    /**
     * @return the number of cards to deal at the beginning of the round (4 or 6).
     */
    public int getStartingHandSize() {
        return startingHandSize;
    }

    public State getState() {
        return state;
    }

    public void changeState(State newState) {
        if (newState == null) throw new IllegalArgumentException("State null impossible");
        this.state = newState;
    }

    public String getName() {
        return name;
    }

    /**
     * Restores the player to PLAYING state and resets credit tracking.
     * Called by Round.startRound() for each active player.
     */
    public void resetForNewRound() {
        state = State.PLAYING;
        creditsLostThisRound = 0;
    }

    @Override
    public String toString() {
        return String.format("Player[%s | %d crédits | %d cartes | %s%s%s]",
                name, credits, hand.size(), state,
                studyAbroad ? " | ABROAD" : "",
                suspended ? " | CÉSURE" : "");
    }

    /**
     * Updates the amount of credits lost by the player during the current round,
     * generally calculated from the cards remaining in hand.
     */
    public void setCreditsLostThisRound(int creditsLostThisRound) {
        this.creditsLostThisRound = creditsLostThisRound;
    }
}