package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.enums.State;

/**
 * Rule: DRAW A CARD (DRAW_CARD)
 * Check the two conditions to draw:
 * 1. The player is in PLAYING state (not QUITTING)
 * 2. The pickaxe is not empty
 * Effect: Game.applyMove() removes a card from the draw pile and gives it to the player.
 * Strategy note:
 * Drawing is still legal if both conditions are met.
 * It’s up to the player (or the bot) to decide if it makes sense.
 */
public class DrawCardRule implements Rule {
    /**
     * Determine if this rule should be triggered based on the move type.
     * This rule specifically handles the logic for drawing a card from the draw pile.
     *
     * @param move the move to evaluate
     * @param game the current game state
     * @return {@code true} if the move type is DRAW_CARD, {@code false} otherwise
     */
    @Override
    public boolean isApplicable(Move move, Game game) {
        return move.getType() == MoveType.DRAW_CARD;
    }

    /**
     * Validate if the draw card move is legal according to game rules.
     * Verifies that the player is active (has not quit the round) and that
     * the draw pile contains at least one card.
     *
     * @param move the move containing the player's request to draw
     * @param game the current state of the game, used to check the draw pile
     * @return {@code true} if the draw is legal, {@code false} if the player has quit
     * or if the draw pile is empty.
     */
    @Override
    public boolean validate(Move move, Game game) {
        if (move.getPlayer().getState() == State.QUITTING) {
            logRefusal(move, "le joueur a déjà passé la manche");
            return false;
        }

        if (game.getDrawPile().isEmpty()) {
            logRefusal(move, "la pioche est vide");
            return false;
        }

        return true;
    }

    /**
     * Apply the effects of drawing a card to the game state.
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
     * @return the string "DrawCardRule"
     */
    @Override
    public String getName() {
        return "DrawCardRule";
    }

    /**
     * Log a detailed reason why the draw card action was refused.
     *
     * @param move   the move that failed validation
     * @param reason the description of why the draw is illegal
     */
    private void logRefusal(Move move, String reason) {
        System.out.println("[" + getName() + "] ✗ " + move.getPlayer().getName()
                + " ne peut pas piocher — " + reason);
    }
}