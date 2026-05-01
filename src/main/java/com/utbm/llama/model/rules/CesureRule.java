package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;

/**
 * Rule: GAP SEMESTER
 * Context: end-of-round rule, after jury resolution.
 * Condition:
 * A player whose credits are NEGATIVE after the jury
 * is forced into a gap semester.
 * Effects of the gap semester:
 * - The player DOES NOT PLAY the next round
 * - They still receive the +35 credits at the start of the next round
 * - Their suspension is lifted at the start of the following round
 * Special case 1v1:
 * If BOTH players are in gap semester, they BOTH skip the round
 * and still each receive the 35 credits.
 * (Handled by Round.allPlayersSuspended() + BoardController)
 * Order in the end-of-round sequence:
 * 1. deductHandPenalties()
 * 2. → JuryRule (if loss ≥ 20)
 * 3. → GapSemesterRule ← applies if credits < 0 after the jury
 * 4. checkStudyAbroad()
 * 5. checkDetecBonus()
 * isApplicable() returns false during play (end-of-round rule).
 */

public class CesureRule implements Rule {
    /**
     * Determine if this rule should be triggered during standard gameplay turns.
     * Since the Gap Semester (Césure) is an end-of-round event, this returns false for normal moves.
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
     * Note: For CesureRule, the logic is handled via static triggers during the round finalization.
     *
     * @param move the move to apply
     * @param game the game state to update
     */
    @Override
    public void apply(Move move, Game game) {
    }

    /**
     * Vérifie si un joueur doit partir en semestre de césure.
     *
     * @param player le joueur à vérifier (après résolution du jury)
     * @return true si les crédits sont négatifs
     */
    public static boolean shouldTrigger(Player player) {
        return player.getCredits() < 0;
    }

    /**
     * Applique la suspension au joueur.
     * Appelé par Round.applyCesure() ou directement par BoardController.
     *
     * @param player le joueur à suspendre
     * @param game   l'état du jeu (pour log et vérification 1v1)
     */
    public static void applyTo(Player player, Game game) {
        player.setSuspended(true);

        boolean allSuspended = game.getPlayers().stream().allMatch(Player::isSuspended);

        System.out.println("[CesureRule] ✓ " + player.getName()
                + " → Semestre de césure (crédits : " + player.getCredits() + ")"
                + (allSuspended ? " |  1v1 — les deux joueurs sautent la manche" : ""));
    }

    /**
     * Get the formal name of this rule for identification and debugging purposes.
     *
     * @return the string "CesureRule"
     */
    @Override
    public String getName() {
        return "CesureRule";
    }
}