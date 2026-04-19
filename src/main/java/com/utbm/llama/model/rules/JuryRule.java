package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.model.Round;

/**
 * Règle : CONVOCATION AU JURY
 * Contexte : règle de FIN DE MANCHE, pas de validation de coup.
 * Condition :
 *  Un joueur qui perd ≥ 20 crédits lors de la déduction de fin de manche
 *  est convoqué devant le jury.
 * Le jury est un mini-jeu (voir model.Jury et controller.JuryController) :
 *  - 7 cartes face cachée (ONE à LLAMA)
 *  - Le joueur en choisit une
 *  - Il gagne sa valeur en crédits (1 à 10)
 * Ordre dans la séquence de fin de manche :
 *  1. deductHandPenalties()    ← identifie les candidats
 *  2. → JuryRule               ← vérifie et déclenche le jury
 *  3. → CesureRule             ← vérifie après le jury
 *  4. checkStudyAbroad()
 *  5. checkDetecBonus()
 * isApplicable() retourne false pendant le jeu (règle de fin de manche).
 */
public class JuryRule implements Rule {

    /** Seuil de perte déclenchant le jury (en crédits). */
    public static final int JURY_TRIGGER = Round.JURY_TRIGGER_THRESHOLD;

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
     * Vérifie si un joueur doit passer devant le jury.
     *
     * @param player          le joueur à vérifier
     * @param creditsLost     crédits perdus lors de la déduction de fin de manche
     * @return true si le jury doit être déclenché
     */
    public static boolean shouldTrigger(Player player, int creditsLost) {
        return creditsLost >= JURY_TRIGGER;
    }

    /**
     * Vérifie si un joueur doit passer devant le jury
     * en utilisant son propre compteur interne.
     *
     * @param player le joueur à vérifier
     * @return true si le jury doit être déclenché
     */
    public static boolean shouldTrigger(Player player) {
        return player.getCreditsLostThisRound() >= JURY_TRIGGER;
    }

    @Override
    public String getName() { return "JuryRule"; }
}
