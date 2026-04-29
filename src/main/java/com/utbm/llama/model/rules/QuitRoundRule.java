package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.enums.State;

/**
 * Rule: PASS ROUND (QUIT_ROUND)
 * Check the only condition to pass:
 * —1. The player is PLAYING (not already QUITTING)
 * Passing is always allowed for an active player, even if he has
 * Playable cards—this is a valid strategic choice.
 * Business consequence (handled by Round.endCurrentRound()):
 * —- The remaining cards in hand are deducted from the credits at the end of the round.
 * —- If the loss is ≥ 20 credits → summoning the jury
 */
public class QuitRoundRule implements Rule {
    /**
     * Determine if this rule should be triggered based on the move type.
     * This rule specifically handles the action of quitting or passing the round.
     *
     * @param move the move to evaluate
     * @param game the current game state
     *             * @return {@code true} if the move type is QUIT_ROUND, {@code false} otherwise
     */
    @Override
    public boolean isApplicable(Move move, Game game) {
        return move.getType() == MoveType.QUIT_ROUND;
    }

    /**
     * Validate if the player is allowed to quit the round.
     * Checks if the player hasn't already quit and is not currently suspended.
     *
     * @param move the move containing the player's request to pass
     * @param game the current state of the game board
     *             * @return {@code true} if the player can legally pass, {@code false} if they are already QUITTING or suspended
     */
    @Override
    public boolean validate(Move move, Game game) {
        if (move.getPlayer().getState() == State.QUITTING) {
            logRefusal(move, "le joueur a déjà passé cette manche");
            return false;
        }

        if (move.getPlayer().isSuspended()) {
            logRefusal(move, "le joueur est en semestre de césure");
            return false;
        }

        return true;
    }

    /**
     * Apply the consequences of quitting the round to the game state.
     * Note: For this specific rule, the state change is usually handled by the Move itself or the Game engine.
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
     * @return the string "QuitRoundRule"
     */
    @Override
    public String getName() {
        return "QuitRoundRule";
    }

    /**
     * Log a detailed reason why the quit round action was refused.
     *
     * @param move   the move that failed validation
     * @param reason the description of the validation failure
     */
    private void logRefusal(Move move, String reason) {
        System.out.println("[" + getName() + "] ✗ " + move.getPlayer().getName()
                + " ne peut pas passer — " + reason);
    }
}