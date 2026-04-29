package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;

/**
 * Règle : SEMESTRE DE CÉSURE
 * Contexte : règle de FIN DE MANCHE, après résolution du jury.
 * Condition :
 * Un joueur dont les crédits sont NÉGATIFS après le jury
 * est contraint au semestre de césure.
 * Effets du semestre de césure :
 * - Le joueur NE JOUE PAS la manche suivante
 * - Il reçoit quand même les +35 crédits du début de manche suivante
 * - Sa suspension est levée au début de la manche après
 * Cas particulier 1v1 :
 * Si les DEUX joueurs sont en césure, ils sautent TOUS LES DEUX la manche
 * et reçoivent quand même les 35 crédits chacun.
 * (Géré par Round.allPlayersSuspended() + BoardController)
 * Ordre dans la séquence de fin de manche :
 * 1. deductHandPenalties()
 * 2. → JuryRule (si perte ≥ 20)
 * 3. → CesureRule ← s'applique si crédits < 0 après le jury
 * 4. checkStudyAbroad()
 * 5. checkDetecBonus()
 * isApplicable() retourne false pendant le jeu (règle de fin de manche).
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