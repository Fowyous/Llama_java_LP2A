package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.Difficulty;

import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Player controlled by AI.
 * Inherits from Player—a Bot is a Player with automatic decision logic.
 * ┌─ EASY ──────────────────────────────────────────────────────────────────────┐
 * │                 Play a legal card at random.                                │
 * │                If no playable cards → draw.                                 │
 * │            Never pass the sleeve willingly (unless forced).                 │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ MEDIUM ────────────────────────────────────────────────────────────────────┐
 * │        Prefers to play a card rather than draw it.                          │
 * │        Choose the HIGHEST value playable card (empty hand faster).          │
 * │        Draw if no playable card and the draw isn’t empty.                   │
 * │        Pass the sleeve if no shot is possible.                              │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ HARD ──────────────────────────────────────────────────────────────────────┐
 * │  Minimizes the value of the remaining cards in hand.                        │
 * │  Play the SMALLEST value card among the playable ones (keep the large ones  │
 * │  cards for later if possible).                                              │
 * │  Pass the round if the remaining hand is worth > 15 credits AND he can’t    │
 * │  nothing to play (limits the damage).                                       │
 * │  NEVER draw if the hand already contains ≥ 4 cards.                         │
 * └─────────────────────────────────────────────────────────────────────────────┘
 */
public class Bot extends Player {

    private final Difficulty difficulty;
    private final Random rng = new Random();

    /**
     * Initialize a new Bot with a specific name and difficulty level.
     *
     * @param name       the name of the bot
     * @param difficulty the AI behavior level (EASY, MEDIUM, HARD)
     *                   * @throws IllegalArgumentException if the difficulty is null
     */
    public Bot(String name, Difficulty difficulty) {
        super(name);
        if (difficulty == null) throw new IllegalArgumentException("La difficulté d'un bot ne peut pas être null");
        this.difficulty = difficulty;
    }

    /**
     * Decide the best move to play depending on difficulty.
     *
     * @param game the current state of the game (read only for decision-making)
     * @return a valid Move for this bot
     */
    public Move decideMove(Game game) {
        return switch (difficulty) {
            case EASY -> decideMoveEasy(game);
            case MEDIUM -> decideMovemedium(game);
            case HARD -> decideMoveHard(game);
        };
    }

    /**
     * Determine a move using Easy logic: play a random legal card, or draw if none available.
     * Never quits the round voluntarily if drawing is an option.
     *
     * @param game the current state of the game
     *             * @return the chosen move (PLAY_CARD, DRAW_CARD, or QUIT_ROUND)
     */
    private Move decideMoveEasy(Game game) {
        List<CardType> playable = getPlayableCards(game);

        if (!playable.isEmpty()) {
            CardType chosen = playable.get(rng.nextInt(playable.size()));
            return Move.playCard(this, chosen);
        }

        if (!game.getDrawPile().isEmpty()) {
            return Move.drawCard(this);
        }

        return Move.quitRound(this);
    }

    /**
     * Determine a move using Medium logic: prefer playing the highest value card.
     * Quits the round only if no cards are playable and the draw pile is empty.
     *
     * @param game the current state of the game
     *             * @return the chosen move (PLAY_CARD, DRAW_CARD, or QUIT_ROUND)
     */
    private Move decideMovemedium(Game game) {
        List<CardType> playable = getPlayableCards(game);

        if (!playable.isEmpty()) {
            CardType best = playable.stream().max(Comparator.comparingInt(CardType::getValue)).orElseThrow();
            return Move.playCard(this, best);
        }

        if (!game.getDrawPile().isEmpty()) {
            return Move.drawCard(this);
        }

        return Move.quitRound(this);
    }

    /**
     * Determine a move using Hard logic: play the smallest card or quit to minimize point loss.
     * Stops drawing if the hand is too large and quits if the hand value is dangerously high.
     *
     * @param game the current state of the game
     *             * @return the chosen move (PLAY_CARD, DRAW_CARD, or QUIT_ROUND)
     */
    private Move decideMoveHard(Game game) {
        List<CardType> playable = getPlayableCards(game);

        if (!playable.isEmpty()) {
            CardType safest = playable.stream().min(Comparator.comparingInt(CardType::getValue)).orElseThrow();
            return Move.playCard(this, safest);
        }

        int handValue = computeHandValue();

        if (!game.getDrawPile().isEmpty() && getHand().size() < 4) {
            return Move.drawCard(this);
        }

        if (handValue > 15) {
            return Move.quitRound(this);
        }

        if (!game.getDrawPile().isEmpty()) {
            return Move.drawCard(this);
        }

        return Move.quitRound(this);
    }

    /**
     * Returns the list of playable hand cards to the current discard pile.
     *
     * @param game game state
     * @return playable cards (can be empty)
     */
    private List<CardType> getPlayableCards(Game game) {
        CardType top = game.getDiscardPile().peek();
        return getHand().stream().filter(card -> card.canBePlayedOn(top)).toList();
    }

    /**
     * Get the difficulty setting of this bot.
     *
     * @return the difficulty level (EASY, MEDIUM, or HARD)
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Return a string representation of the Bot for debugging or display.
     *
     * @return a string containing name, difficulty, and credits
     */
    @Override
    public String toString() {
        return "Bot[" + getName() + " | " + difficulty + " | " + getCredits() + " crédits]";
    }
}