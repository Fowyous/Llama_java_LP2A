package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;

/**
 * Règle : SEMESTRE À L'ÉTRANGER
 * Contexte : règle de FIN DE MANCHE, pas de validation de coup.
 * Condition :
 * Un joueur qui termine la manche avec une main VIDE obtient le
 * "Semestre à l'étranger" : il commence la manche suivante avec
 * 4 cartes au lieu de 6.
 * Règles supplémentaires :
 * - Non cumulable : si le joueur vide à nouveau sa main lors du semestre
 * à l'étranger, l'avantage ne s'additionne pas (toujours 4 cartes max).
 * - Temporaire : l'avantage dure UNE SEULE manche, puis revient à 6 cartes.
 * isApplicable() / validate() :
 * Cette règle n'est pas une règle de coup — elle ne valide rien pendant
 * le jeu. Elle s'applique automatiquement lors de Round.checkStudyAbroad().
 * isApplicable() retourne toujours false pour ne pas interférer avec
 * la validation normale des coups.
 * apply() :
 * Utilisé par RuleEngine en fin de manche pour activer le flag sur le joueur.
 */
public class StudyBoardRule implements Rule {

    /**
     * Cette règle ne s'applique à AUCUN coup pendant la manche.
     * Elle est déclenchée explicitement par Round.checkStudyAbroad().
     */
    @Override
    public boolean isApplicable(Move move, Game game) {
        return false;
    }

    @Override
    public boolean validate(Move move, Game game) {
        return true;
    }

    /**
     * Active le semestre à l'étranger sur le joueur du coup.
     * Appelé explicitement par Round après la fin d'une manche.
     *
     * @param move coup dont le joueur a vidé sa main
     * @param game état du jeu
     */
    @Override
    public void apply(Move move, Game game) {
        if (move.getPlayer().getHand().isEmpty()) {
            move.getPlayer().setStudyAbroad(true);
            System.out.println("[" + getName() + "] ✓ " + move.getPlayer().getName()
                    + " → Semestre à l'étranger activé ! (4 cartes à la manche suivante)");
        }
    }

    /**
     * Vérifie si un joueur est éligible au semestre à l'étranger.
     * Peut être appelée directement sans passer par RuleEngine.
     *
     * @param player le joueur à vérifier
     * @return true si la main est vide
     */
    public static boolean isEligible(Player player) {
        return player.getHand().isEmpty();
    }

    /**
     * Applique directement le semestre à l'étranger sur un joueur.
     * Méthode utilitaire appelée par Round.checkStudyAbroad().
     *
     * @param player le joueur éligible
     */
    public static void applyTo(Player player) {
        if (isEligible(player)) {
            player.setStudyAbroad(true);
            System.out.println("[StudyBoardRule] ✓ " + player.getName()
                    + " → Semestre à l'étranger activé !");
        }
    }

    @Override
    public String getName() {
        return "StudyBoardRule";
    }
}