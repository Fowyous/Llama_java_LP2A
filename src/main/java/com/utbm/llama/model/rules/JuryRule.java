package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.model.Round;

/**
 * Rule: SUMMONING THE JURY
 * Context: END OF ROUND rule, no hit validation.
 * Condition:
 * —A player who loses ≥ 20 credits during the end-of-inning deduction
 * —is summoned before the jury.
 * The jury is a mini-game (see model.Jury and controller.JuryController):
 * —- 7 cards face down (ONE to LLAMA)
 * —- The player chooses one
 * —- It earns its value in credits (1 to 10)
 * Order in the endgame sequence:
 * —1. deductHandPenalties()   ← identifies the candidates
 * —2. → JuryRule   ← checks and triggers the jury
 * —3. → CesureRule ← checks after the jury
 * —4. checkStudyAbroad()
 * —5. checkDetecBonus()
 * isApplicable() returns false during game play (end-of-round rule).
 */
public class JuryRule implements Rule {

    public static final int JURY_TRIGGER = Round.JURY_TRIGGER_THRESHOLD;

    /**
     * Determine if this rule should be triggered during standard gameplay turns.
     * Since the Jury is an end-of-round event, this returns false for normal moves.
     *
     * @param move the move to evaluate
     * @param game the current game state
     * @return {@code false} as this rule is not applicable during active turns
     */
    @Override
    public boolean isApplicable(Move move, Game game) {
        return false;
    }

    /**
     * Validate the legality of a move in the context of this rule.
     * As this is an automated end-of-round rule, it always returns true.
     *
     * @param move the move to validate
     * @param game the current game state
     * @return {@code true} consistently
     */
    @Override
    public boolean validate(Move move, Game game) {
        return true;
    }

    /**
     * Apply the consequences of the rule to the game state.
     * Note: For JuryRule, the application is handled via specific triggers in the round sequence.
     *
     * @param move the move to apply
     * @param game the game state to update
     */
    @Override
    public void apply(Move move, Game game) {
    }

    /**
     * Check if a player should go before the jury.
     *
     * @param player      the player to check
     * @param creditsLost credits lost during the deduction of end of round
     * @return true if the jury must be triggered
     */
    public static boolean shouldTrigger(Player player, int creditsLost) {
        return creditsLost >= JURY_TRIGGER;
    }

    /**
     * Check if a player should go before the jury
     * using its own internal counter.
     *
     * @param player the player to check
     * @return true if the jury must be triggered
     */
    public static boolean shouldTrigger(Player player) {
        return player.getCreditsLostThisRound() >= JURY_TRIGGER;
    }

    /**
     * Get the formal name of this rule for identification and debugging purposes.
     *
     * @return the string "JuryRule"
     */
    @Override
    public String getName() {
        return "JuryRule";
    }
}
