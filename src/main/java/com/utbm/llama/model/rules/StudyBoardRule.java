package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;

/**
 * Rule: SEMESTER ABROAD
 * Context: END OF ROUND rule, no hit validation.
 * Condition:
 * A player who ends the round with an EMPTY hand gets the
 * "Semester abroad": he starts the next round with
 * 4 cards instead of 6.
 * Additional rules:
 * - Not accumulative: if the player empties his hand again during the semester
 * Abroad, the advantage does not add up (always 4 cards max).
 * - Temporary: the advantage lasts ONE round, then comes back to 6 cards.
 * isApplicable() / validate():
 * This rule is not a hit rule—it does not validate anything during
 * the game. It is automatically applied during Round.checkStudyAbroad().
 * isApplicable() always returns false so as not to interfere with
 * the normal validation of the moves.
 * apply():
 * Used by RuleEngine at the end of the inning to activate the flag on the player.
 */
public class StudyBoardRule implements Rule {

    /**
     * This rule does not apply to ANY moves during the round.
     * It is explicitly triggered by Round.checkStudyAbroad().
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
     * Activates the semester abroad on the player of the coup.
     * Explicitly called by Round after the end of a round.
     *
     * @param move whose player has emptied his hand
     * @param game game state
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
     * Check if a player is eligible for the semester abroad.
     * Can be called directly without going through RuleEngine.
     *
     * @param player the player to check
     * @return true if the hand is empty
     */
    public static boolean isEligible(Player player) {
        return player.getHand().isEmpty();
    }

    /**
     * Applies the semester abroad directly to a player.
     * Utility method called by Round.checkStudyAbroad().
     *
     * @param player the eligible player
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