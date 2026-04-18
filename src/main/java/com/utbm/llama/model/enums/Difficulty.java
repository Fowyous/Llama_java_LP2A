package main.java.com.utbm.llama.model.enums;

/**
 * Niveau de difficulté d'un Bot.
 * EASY   — Plays randomly among legal moves.
 * MEDIUM — Applies some simple heuristics
 *          (prefers playing a card rather than drawing,
 *                folds if the hand is too full).
 * HARD   —Advanced strategy: takes into account visible cards
 * 			      and opponents' scores.
 */
public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
}
