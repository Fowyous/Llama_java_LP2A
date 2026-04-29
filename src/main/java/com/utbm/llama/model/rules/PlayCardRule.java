package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.enums.State;

/**
 * Rule: PLAY CARD (PLAY_CARD)
 * Checks the three conditions necessary to place a card:
 * —1. The player is in PLAYING state (not QUITTING)
 * —2. The player does indeed have this card in their hand
 * —3. The card complies with the L.A.M.A inheritance rules:
 * diction - Discard empty → any card is playable
 * —- Same value as the top → playable
 * —- Immediately greater value of 1 → playable
 * —- SIX at the top + LLAMA → playable
 * —- LLAMA at top + ONE → playable (cycle)
 * Applied effect: removes the card from the hand and puts it on the discard.
 * (In practice, Game.applyMove() already does that—apply() here is a no-op
 * —to respect the pattern, the actual application is in Game. )
 */
public class PlayCardRule implements Rule {
    /**
     * Determine if this rule should be triggered based on the move type.
     * This rule specifically handles the logic for playing a card on the discard pile.
     *
     * @param move the move to evaluate
     * @param game the current game state
     * @return {@code true} if the move type is PLAY_CARD, {@code false} otherwise
     */
    @Override
    public boolean isApplicable(Move move, Game game) {
        return move.getType() == MoveType.PLAY_CARD;
    }

    /**
     * Validate if the card play move is legal according to game rules.
     * Verifies that the player is active, possesses the card, and that the card
     * follows the L.A.M.A. numerical sequence relative to the discard pile.
     *
     * @param move the move containing the player and the card to be played
     * @param game the current state of the game board and piles
     * @return {@code true} if the move is legal, {@code false} if the player has quit,
     * doesn't have the card, or if the move is numerically invalid.
     */
    @Override
    public boolean validate(Move move, Game game) {
        if (move.getPlayer().getState() == State.QUITTING) {
            logRefusal(move, "le joueur a déjà passé la manche");
            return false;
        }

        if (!move.getPlayer().getHand().contains(move.getCard())) {
            logRefusal(move, "la carte " + move.getCard() + " n'est pas dans la main du joueur");
            return false;
        }

        CardType topOfDiscard = game.getDiscardPile().peek();
        if (!move.getCard().canBePlayedOn(topOfDiscard)) {
            logRefusal(move, move.getCard() + " ne peut pas être joué sur " + topOfDiscard);
            return false;
        }

        return true;
    }

    /**
     * Apply the effects of playing a card to the game state.
     * Note: In the current architecture, this is a no-op as the physical move
     * is handled by the core game engine.
     *
     * @param move the validated move to apply
     * @param game the game state to update
     */
    @Override
    public void apply(Move move, Game game) {

    }

    /**
     * Get the formal name of this rule for identification and debugging purposes.
     *
     * @return the string "PlayCardRule"
     */
    @Override
    public String getName() {
        return "PlayCardRule";
    }

    /**
     * Log a detailed reason why the card play action was refused.
     *
     * @param move   the move that failed validation
     * @param reason the description of why the play is illegal
     */
    private void logRefusal(Move move, String reason) {
        System.out.println("[" + getName() + "] ✗ " + move.getPlayer().getName()
                + " ne peut pas jouer " + move.getCard() + " — " + reason);
    }
}