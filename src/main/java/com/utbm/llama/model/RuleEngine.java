package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.rules.*;

import java.util.List;
import java.util.ArrayList;

/**
 * LAMA UTBM game rules engine.
 * Role:
 * - Aggregate all the hit validation rules (PlayCard, Draw, Quit)
 * - Outlines two main methods:
 * * validateMove()  → verifies that a move is legal
 * * applyRules()   → applies the effects of rules after a valid move
 * - Exposes utility methods for endgame rules
 * * processEndOfRound() → complete end-of-inning sequence
 * What RuleEngine does NOT do:
 * - It does not directly modify the template (that’s Game.applyMove())
 * - He does not manage the jury or the trial asynchronously.
 * (these elements require a UI interaction → BoardController)
 * Typical usage in BoardController:
 * Move move = Move.playCard(player, card);
 * if (ruleEngine.validateMove(move, game)) {
 * game.applyMove(move);
 * ruleEngine.applyRules(move, game);
 * }
 * List<Round.JuryCandidate> candidates = ruleEngine.processEndOfRound(game);
 * ruleEngine.processPostJury(game);
 */
public class RuleEngine {

    private final List<Rule> rules = new ArrayList<>();

    private final StudyBoardRule studyBroadRule = new StudyBoardRule();
    private final JuryRule juryRule = new JuryRule();
    private final CesureRule cesureRule = new CesureRule();
    private final DetecBonusRule detecBonusRule = new DetecBonusRule();


    /**
     * Creates the rule engine with all saved move rules.
     */
    public RuleEngine() {
        rules.add(new PlayCardRule());
        rules.add(new DrawCardRule());
        rules.add(new QuitRoundRule());
    }

    /**
     * Checks if a hit is legal according to all applicable rules.
     * Only rules that isApplicable() returns true are evaluated.
     * If only one rule fails, the move is rejected.
     *
     * @param move the move to validate
     * @param game the current state of the game
     * @return true if the move is legal
     */
    public boolean validateMove(Move move, Game game) {
        if (move == null || game == null) return false;

        for (Rule rule : rules) {
            if (rule.isApplicable(move, game)) {
                if (!rule.validate(move, game)) {
                    System.out.println("[RuleEngine] Coup refusé par " + rule.getName());
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Applies rule effects after a valid move is executed.
     * Called AFTER Game.applyMove().
     *
     * @param move the move that just was applied
     * @param game the state of the game after the coup
     */
    public void applyRules(Move move, Game game) {
        for (Rule rule : rules) {
            if (rule.isApplicable(move, game)) {
                rule.apply(move, game);
            }
        }
    }

    /**
     * Execute end-of-inning phase 1:
     * 1. Deducts the penalties from the hand (via Game.endCurrentRound())
     * 2. Identifies the players to be judged by the jury (loss ≥ 20)
     * After this method, BoardController must:
     * - Trigger juries one by one (JuryController.startJury())
     * - Call processPostJury() once all juries are resolved
     *
     * @param game the state of play at the end of the round
     * @return list of players summoned to the jury (can be empty)
     */
    public List<Round.JuryCandidate> processEndOfRound(Game game) {
        System.out.println("[RuleEngine] === Début de la séquence de fin de manche ===");

        List<Round.JuryCandidate> candidates = game.endCurrentRound();

        System.out.println("[RuleEngine] Pénalités appliquées | "
                + candidates.size() + " candidat(s) au jury");

        return candidates;
    }



    /**
     * Execute the end-of-inning phase 2, AFTER resolving all juries:
     * 1. Check the gap for each player still in negative
     * 2. Activate the semester abroad for those who have emptied their hand
     * 3. Applies the DETEC bonus if the conditions are met
     *
     * @param game the state of the game after jury resolutions
     * @return list of players leaving for gap semesters
     */
    public List<Player> processPostJury(Game game) {
        List<Player> cesurePlayers = new ArrayList<>();

        for (Player p : game.getCurrentRound().getActivePlayers()) {
            if (CesureRule.shouldTrigger(p)) {
                CesureRule.applyTo(p, game);
                cesurePlayers.add(p);
            }
        }

        for (Player p : game.getCurrentRound().getActivePlayers()) {
            StudyBoardRule.applyTo(p);
        }

        DetecBonusRule.applyIfEligible(game, game.getLedger());

        game.endRoundPostJury();

        System.out.println("[RuleEngine] === Fin de manche traitée | "
                + cesurePlayers.size() + " césure(s) ===");

        return cesurePlayers;
    }

    /**
     * Check if a player can still play in the hand (has a playable card).
     * Useful for bots and view display.
     *
     * @param player the player to check
     * @param game the game state
     * @return true if the player has at least one playable card
     */
    public boolean hasPlayableCard(Player player, Game game) {
        CardType top = game.getDiscardPile().peek();
        return player.getHand().stream().anyMatch(c -> c.canBePlayedOn(top));
    }

    /**
     * Returns the list of playable cards in a player’s hand.
     *
     * @param player the player
     * @param game the game state
     * @return list of legally playable cards (can be empty)
     */
    public List<CardType> getPlayableCards(Player player, Game game) {
        CardType top = game.getDiscardPile().peek();
        return player.getHand().stream()
                .filter(c -> c.canBePlayedOn(top))
                .toList();
    }

    /**
     * Check if the current round is finished
     * (all active players have passed or one has emptied their hand).
     *
     * @param game the game state
     * @return true if the round should end
     */
    public boolean isRoundOver(Game game) {
        return game.getCurrentRound() != null && game.getCurrentRound().isOver();
    }

    /**
     * Adds a custom rule to the engine.
     * Useful for extensions or tests.
     *
     * @param rule the rule to add
     */
    public void addRule(Rule rule) {
        if (rule != null) rules.add(rule);
    }

    /**
     * @return uneditable list of all saved rules
     */
    public List<Rule> getRules() {
        return java.util.Collections.unmodifiableList(rules);
    }
}