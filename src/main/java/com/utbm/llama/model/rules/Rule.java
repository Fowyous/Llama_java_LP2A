package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;

/**
 * Interface common to all the rules of the LAMA UTBM game.
 * A Rule encapsulates a single business rule and answers two questions:
 * —1. Does this rule apply to this case in this context? (isApplicable)
 * —2. Is this move valid according to this rule?   (validate)
 * —3. What effects does this rule have on the game?   (apply)
 * Single point of accountability:
 * —Each implementation processes ONLY ONE rule.
 * —RuleEngine aggregates and orchestrates them.
 */
public interface Rule {

    /**
     * Determines whether this rule applies to the hit in the current context.
     * Allows RuleEngine to evaluate only relevant rules,
     * and avoids validating PlayCardRule on a DRAW_CARD for example.
     *
     * @param move the move to evaluate
     * @param game the current state of the game
     * @return true if this rule needs to be evaluated for this move
     */
    boolean isApplicable(Move move, Game game);

    /**
     * Check if the move respects this rule.
     * Is only called if {@link #isApplicable} returns true.
     *
     * @param move the move to validate
     * @param game the current state of the game
     * @return true if the move is valid according to this rule
     */
    boolean validate(Move move, Game game);

    /**
     * Applies the effects of this rule to the game.
     * Is called only if the move is valid and the rule applies.
     * Some rules have no effect to apply (pure validation) —
     * In this case, this method can remain empty.
     *
     * @param move the validated shot
     * @param game the state of the game to be modified
     */
    void apply(Move move, Game game);

    /**
     * Readable name of the rule (for logs and debug).
     *
     * @return rule short name
     */
    String getName();
}