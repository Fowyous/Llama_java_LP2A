package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;

/**
 *
 * Contrat qu'implémente chaque règle du jeu.
 * Le pattern Strategy est utilisé ici : chaque règle est un objet
 * indépendant que RuleEngine agrège. Pour ajouter une règle UTBM,
 * il suffit d'implémenter cette interface et de l'enregistrer
 * dans RuleEngine — aucune modification du code existant n'est nécessaire.
 * Cycle de vie d'une règle dans RuleEngine :
 *   1. isApplicable() — cette règle concerne-t-elle ce type de coup ?
 *   2. validate()     — le coup respecte-t-il la règle ?
 *   3. apply()        — si valide, appliquer les effets de la règle.
 * Séparation validation / application :
 *   validate() ne modifie JAMAIS l'état du jeu.
 *   apply()    est appelé uniquement si validate() retourne true.
 */
public interface Rule {

    /**
     * Indique si cette règle s'applique au coup donné.
     * Permet à RuleEngine de filtrer rapidement les règles pertinentes.
     *
     * @param move le coup à évaluer
     * @param game l'état courant du jeu
     * @return true si cette règle doit être consultée pour ce coup
     */
    boolean isApplicable(Move move, Game game);

    /**
     * Vérifie que le coup respecte cette règle sans modifier l'état du jeu.
     *
     * @param move le coup à valider
     * @param game l'état courant du jeu (lecture seule dans cette méthode)
     * @return true si le coup est valide selon cette règle
     */
    boolean validate(Move move, Game game);

    /**
     * Applique les effets de la règle sur l'état du jeu.
     * N'est appelé que si {@link #validate} a retourné true.
     *
     * @param move le coup validé à appliquer
     * @param game l'état du jeu à modifier
     */
    void apply(Move move, Game game);

    /**
     * Retourne un message d'erreur lisible quand validate() échoue.
     * Utilisé pour les logs et les retours à l'interface graphique.
     *
     * @param move le coup invalide
     * @param game l'état courant du jeu
     * @return message décrivant pourquoi le coup est refusé
     */
    default String getErrorMessage(Move move, Game game) {
        return "Invalid move : " + move + " (rule : " + getClass().getSimpleName() + ")";
    }
}
