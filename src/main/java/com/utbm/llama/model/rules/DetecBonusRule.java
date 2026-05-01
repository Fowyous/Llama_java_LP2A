package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.CreditLedger;
import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;

/**
 * Rule: DEUTEC BONUS
 * Context: END OF ROUND 4 rule, only in LONG mode.
 * Condition:
 * At the end of round 4 (LONG mode only),
 * any player with ≥ 120 credits receives a bonus of +30 credits.
 * This bonus celebrates the validation of the "DETEC" (equivalent to a 2-year degree at the UTBM).
 * Features:
 * Applicable ONLY ONCE per game (round 4 in LONG mode)
 * Applies to ALL players reaching the threshold, not just the best
 * Not accumulative (if the player already had 120+ credits in round 3,
 * he’s still waiting for round 4)
 * The game continues as usual afterwards, the rules remain the same.
 * Order in the endgame sequence:
 * 1. deductHandPenalties()
 * 2. JuryRule (if necessary)
 * 3. CesureRule (if necessary)
 * 4. StudyBoardRule
 * 5. → DetecBonusRule ← applied LAST to sleeve 4
 * isApplicable() returns false during game play (end-of-round rule).
 */
public class DetecBonusRule implements Rule {
    /**
     * Determine if this rule should be triggered during standard gameplay turns.
     * Since the DETEC bonus is an end-of-round event, this returns false for normal moves.
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
     * Note: For DetecBonusRule, the application is handled via specific static methods during the round finalization.
     *
     * @param move the move to apply
     * @param game the game state to update
     */
    @Override
    public void apply(Move move, Game game) {
    }

    /**
     * Checks whether the DEUTEC bonus should be checked in the current context.
     *
     * @param game the game state
     * @return true if you’re at the end of round 4 in LONG mode
     */
    public static boolean isApplicableContext(Game game) {
        return game.getGameMode().hasDetecBonus()
                && game.getCurrentRoundNumber() == GameMode.DEUTEC_ROUND;
    }

    /**
     * Check if a player is eligible for the DETEC bonus.
     *
     * @param player the player to check
     * @return true if the player has ≥ 120 credits
     */
    public static boolean isEligible(Player player) {
        return player.getCredits() >= GameMode.DEUTEC_THRESHOLD;
    }

    /**
     * Applies the DETEC bonus to all eligible players.
     * Saves the winnings in CreditLedger.
     *
     * @param game   the game state
     * @param ledger the accounting register
     */
    public static void applyIfEligible(Game game, CreditLedger ledger) {
        if (!isApplicableContext(game)) return;

        int roundNumber = game.getCurrentRoundNumber();
        boolean anyBonus = false;

        for (Player p : game.getPlayers()) {
            if (isEligible(p)) {
                ledger.record(
                        roundNumber,
                        p,
                        CreditLedger.Reason.DETEC_BONUS,
                        GameMode.DEUTEC_BONUS
                );
                System.out.println("[DetecBonusRule] ✓ " + p.getName()
                        + " valide le DETEC ! +" + GameMode.DEUTEC_BONUS
                        + " crédits → " + p.getCredits());
                anyBonus = true;
            }
        }

        if (!anyBonus) {
            System.out.println("[DetecBonusRule] Aucun joueur n'atteint les "
                    + GameMode.DEUTEC_THRESHOLD + " crédits à la manche 4.");
        }
    }

    /**
     * Get the formal name of this rule for identification and debugging purposes.
     *
     * @return the string "DetecBonusRule"
     */
    @Override
    public String getName() {
        return "DetecBonusRule";
    }
}