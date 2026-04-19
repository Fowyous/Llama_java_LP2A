package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;

/**
 * Interface commune à toutes les règles du jeu LAMA UTBM.
 * Une Rule encapsule une règle métier unique et répond à deux questions :
 *  1. Cette règle s'applique-t-elle à ce coup dans ce contexte ? (isApplicable)
 *  2. Ce coup est-il valide selon cette règle ?                   (validate)
 *  3. Quels effets cette règle produit-elle sur le jeu ?          (apply)
 * Principe de responsabilité unique :
 *  Chaque implémentation ne traite QU'UNE seule règle.
 *  RuleEngine les agrège et les orchestre.
 * Exemple d'utilisation par RuleEngine :
 *  for (Rule rule : rules) {
 *      if (rule.isApplicable(move, game)) {
 *          if (!rule.validate(move, game)) return false;
 *      }
 *  }
 */
public interface Rule {

    /**
     * Détermine si cette règle s'applique au coup donné dans le contexte actuel.
     * Permet à RuleEngine de n'évaluer que les règles pertinentes,
     * et évite de valider PlayCardRule sur un DRAW_CARD par exemple.
     *
     * @param move le coup à évaluer
     * @param game l'état courant du jeu
     * @return true si cette règle doit être évaluée pour ce coup
     */
    boolean isApplicable(Move move, Game game);

    /**
     * Vérifie si le coup respecte cette règle.
     * N'est appelé que si {@link #isApplicable} retourne true.
     *
     * @param move le coup à valider
     * @param game l'état courant du jeu
     * @return true si le coup est valide selon cette règle
     */
    boolean validate(Move move, Game game);

    /**
     * Applique les effets de cette règle sur le jeu.
     * N'est appelé que si le coup est valide et que la règle est applicable.
     * Certaines règles n'ont pas d'effet à appliquer (validation pure) —
     * dans ce cas, cette méthode peut rester vide.
     *
     * @param move le coup validé
     * @param game l'état du jeu à modifier
     */
    void apply(Move move, Game game);

    /**
     * Nom lisible de la règle (pour les logs et le debug).
     *
     * @return nom court de la règle
     */
    String getName();
}