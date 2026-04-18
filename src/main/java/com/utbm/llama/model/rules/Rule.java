package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;

/**
 * Contract implemented by each game rule.
 * The Strategy pattern is used here: each rule is an independent object
 * that RuleEngine aggregates. To add a UTBM rule, implement this interface
 * and register it with RuleEngine — no changes to existing code are required.
 * Lifecycle of a rule within RuleEngine:
 * 1. isApplicable() — does this rule concern this type of move?
 * 2. validate()     — does the move comply with the rule?
 * 3. apply()        — if valid, apply the rule's effects.
 * Separation of validation / application:
 * validate() must NEVER modify game state.
 * apply() is called only if validate() returns true.
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
     * @param move the move that is going to be validated.
     * @param game the current game (read only in this method).
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
