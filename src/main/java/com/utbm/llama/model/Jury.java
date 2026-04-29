package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Jury’s mini-game.
 * Context: triggered when a player loses ≥ 20 credits in a round.
 * Process:
 * 1. The 7 types of cards (ONE to LLAMA) are mixed and presented face down.
 * 2. The player chooses exactly ONE card (by his index 0-6).
 * 3. The card is revealed: the player wins its credit value (1 to 10).
 * 4. After the jury:
 * - if credits still lower than 0 → gap semester
 * - otherwise → return to normal gameplay
 * Note: the choice belongs to the view/controller.
 * Jury (template) only stores the cards and applies the result.
 */
public class Jury {


    /**
     * The player who passes in front of the jury.
     */
    private final Player targetPlayer;

    /**
     * Lost credits this round (for info/log).
     */
    private final int creditsLostThisRound;

    /**
     * The 7 cards in random order.
     * Only the index is visible to the player (cards are face down).
     */
    private final List<CardType> hiddenCards;

    /**
     * The card chosen by the player, null as long as the jury is not resolved.
     */
    private CardType chosenCard = null;

    /**
     * true when the player has made his choice.
     */
    private boolean resolved = false;

    /**
     * Creates a jury for a given player.
     * Shuffle the 7 cards randomly.
     *
     * @param player the player summoned
     * @param creditsLostThisRound lost credits this round (≥ 20)
     */
    public Jury(Player player, int creditsLostThisRound) {
        if (player == null)
            throw new IllegalArgumentException("Le joueur du jury ne peut pas être null");
        if (creditsLostThisRound < 20)
            throw new IllegalArgumentException(
                    "Le jury se déclenche uniquement pour une perte ≥ 20 crédits (reçu : "
                            + creditsLostThisRound + ")");

        this.targetPlayer = player;
        this.creditsLostThisRound = creditsLostThisRound;

        List<CardType> cards = new ArrayList<>(Arrays.asList(CardType.values()));
        Collections.shuffle(cards);
        this.hiddenCards = Collections.unmodifiableList(cards);
    }

    /**
     * The player chooses a card by his index finger (0 to 6).
     * Applies the credit gain to the Player template immediately.
     *
     * @param index index of the chosen map (0-6)
     * @return the revealed card
     * @throws IllegalStateException if the jury is already resolved
     * @throws IllegalArgumentException if the index is invalid
     */
    public CardType pickCard(int index) {
        if (resolved)
            throw new IllegalStateException("Le jury a déjà été résolu pour " + targetPlayer.getName());
        if (index < 0 || index >= hiddenCards.size())
            throw new IllegalArgumentException("Index invalide : " + index + " (doit être 0-6)");

        chosenCard = hiddenCards.get(index);
        int gained = chosenCard.getValue();

        targetPlayer.addCredits(gained);

        resolved = true;

        System.out.println("[JURY] " + targetPlayer.getName()
                + " choisit la carte index " + index
                + " → " + chosenCard.name()
                + " | +" + gained + " crédits"
                + " | Total : " + targetPlayer.getCredits());

        return chosenCard;
    }

    /**
     * Indicates whether the player must leave for the gap semester after the jury.
     * Condition: credits are always negative after the jury win.
     *
     * @return true if a break is needed
     * @throws IllegalStateException if the jury is not yet resolved
     */
    public boolean requiresCesure() {
        if (!resolved)
            throw new IllegalStateException("Le jury doit être résolu avant de vérifier la césure");
        return targetPlayer.getCredits() < 0;
    }

    /**
     * @return the player passing in front of the jury
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * @return the lost credits that triggered the jury
     */
    public int getCreditsLostThisRound() {
        return creditsLostThisRound;
    }

    /**
     * @return the list of the 7 cards in their mixed order.
     * The view shows only the index finger (visible back), not the type.
     */
    public List<CardType> getHiddenCards() {
        return hiddenCards;
    }

    /**
     * @return the chosen card, or null if the jury is not yet resolved
     */
    public CardType getChosenCard() {
        return chosenCard;
    }

    /**
     * @return the gain obtained, or 0 if not yet resolved
     */
    public int getGained() {
        return chosenCard != null ? chosenCard.getValue() : 0;
    }

    /**
     * @return true if the player goes through the round
     */
    public boolean isResolved() {
        return resolved;
    }

    @Override
    public String toString() {
        return String.format("Jury[%s | perdu=%d | résolu=%s | carte=%s]",
                targetPlayer.getName(), creditsLostThisRound, resolved,
                chosenCard != null ? chosenCard.name() : "—");
    }
}
