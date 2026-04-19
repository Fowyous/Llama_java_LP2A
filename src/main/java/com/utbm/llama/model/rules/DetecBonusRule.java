package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.CreditLedger;
import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;

/**
 * Règle : BONUS DETEC
 * Contexte : règle de FIN DE MANCHE 4, uniquement en mode LONG.
 * Condition :
 *  À la fin de la manche 4 (mode LONG uniquement),
 *  tout joueur ayant ≥ 120 crédits reçoit un bonus de +30 crédits.
 *  Ce bonus célèbre la validation du "DETEC" (équivalent d'un Bac+2 à l'UTBM).
 * Caractéristiques :
 *  - Applicable UNE SEULE FOIS par partie (manche 4 en mode LONG)
 *  - S'applique à TOUS les joueurs atteignant le seuil, pas seulement au meilleur
 *  - Non cumulable (si le joueur avait déjà 120+ crédits à la manche 3,
 *    il attend quand même la manche 4)
 *  - Le jeu continue normalement après, les règles restent identiques
 * Ordre dans la séquence de fin de manche :
 *  1. deductHandPenalties()
 *  2. JuryRule (si nécessaire)
 *  3. CesureRule (si nécessaire)
 *  4. StudyBoardRule
 *  5. → DetecBonusRule ← appliquée EN DERNIER à la manche 4
 * isApplicable() retourne false pendant le jeu (règle de fin de manche).
 */
public class DetecBonusRule implements Rule {

    @Override
    public boolean isApplicable(Move move, Game game) {
        return false;
    }

    @Override
    public boolean validate(Move move, Game game) {
        return true;
    }

    @Override
    public void apply(Move move, Game game) {
    }

    /**
     * Vérifie si le bonus DETEC doit être vérifié dans le contexte actuel.
     *
     * @param game l'état du jeu
     * @return true si on est à la fin de la manche 4 en mode LONG
     */
    public static boolean isApplicableContext(Game game) {
        return game.getGameMode().hasDetecBonus()
                && game.getCurrentRoundNumber() == GameMode.DETEC_ROUND;
    }

    /**
     * Vérifie si un joueur est éligible au bonus DETEC.
     *
     * @param player le joueur à vérifier
     * @return true si le joueur a ≥ 120 crédits
     */
    public static boolean isEligible(Player player) {
        return player.getCredits() >= GameMode.DETEC_THRESHOLD;
    }

    /**
     * Applique le bonus DETEC à tous les joueurs éligibles.
     * Enregistre les gains dans le CreditLedger.
     *
     * @param game   l'état du jeu
     * @param ledger le registre comptable
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
                        GameMode.DETEC_BONUS
                );
                System.out.println("[DetecBonusRule] ✓ " + p.getName()
                        + " valide le DETEC ! +" + GameMode.DETEC_BONUS
                        + " crédits → " + p.getCredits());
                anyBonus = true;
            }
        }

        if (!anyBonus) {
            System.out.println("[DetecBonusRule] Aucun joueur n'atteint les "
                    + GameMode.DETEC_THRESHOLD + " crédits à la manche 4.");
        }
    }

    @Override
    public String getName() { return "DetecBonusRule"; }
}