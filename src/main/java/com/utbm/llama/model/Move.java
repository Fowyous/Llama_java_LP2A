package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.MoveType;

/**
 * Represents a move played by a player.
 * Move is a pure data object (no business logic).
 * Execution and validation belong to Game/RuleEngine.
 * Construction examples:
 * Move.playCard(player, CardType.THREE)   → plays a 3
 * Move.drawCard(player)   → draw
 * Move.quitRound(player)   → completes the round
 */
public class Move {

    private final Player player;
    private final MoveType type;
    private final CardType card;

    /**
     * Private constructor to initialize a new game move.
     * Validates the move consistency based on the action type.
     *
     * @param player the player performing the action
     * @param type   the category of the move (PLAY_CARD, DRAW_CARD, or QUIT_ROUND)
     * @param card   the specific card being played, or {@code null} for other move types
     *               * @throws IllegalArgumentException if player or type is null, or if card is null for a PLAY_CARD move
     */
    private Move(Player player, MoveType type, CardType card) {
        if (player == null) throw new IllegalArgumentException("Player ne peut pas être null");
        if (type == null) throw new IllegalArgumentException("MoveType ne peut pas être null");
        if (type == MoveType.PLAY_CARD && card == null)
            throw new IllegalArgumentException("Une carte est requise pour PLAY_CARD");

        this.player = player;
        this.type = type;
        this.card = card;
    }

    /**
     * Create a "play a card" move.
     *
     * @param player the player who plays
     * @param card   the played card
     */
    public static Move playCard(Player player, CardType card) {
        return new Move(player, MoveType.PLAY_CARD, card);

    }

    /**
     * Creates a "draw a card" move.
     *
     * @param player the player who draws
     */
    public static Move drawCard(Player player) {
        return new Move(player, MoveType.DRAW_CARD, null);
    }

    /**
     * Create a "pass the round" move.
     *
     * @param player the passing player
     */
    public static Move quitRound(Player player) {
        return new Move(player, MoveType.QUIT_ROUND, null);
    }

    /**
     * Retrieve the player associated with this move.
     *
     * @return the player performing the action
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the type of the move performed.
     *
     * @return the category of the action (PLAY_CARD, DRAW_CARD, or QUIT_ROUND)
     */
    public MoveType getType() {
        return type;
    }

    /**
     * @return the played card, or {@code null} if the type is not PLAY_CARD
     */
    public CardType getCard() {
        return card;
    }

    /**
     * @return true if this move involves playing a specific card
     */
    public boolean isPlayCard() {
        return type == MoveType.PLAY_CARD;
    }

    /**
     * @return true if this move is a pickaxe
     */
    public boolean isDrawCard() {
        return type == MoveType.DRAW_CARD;
    }

    /**
     * @return true if the player goes through the round
     */
    public boolean isQuitRound() {
        return type == MoveType.QUIT_ROUND;
    }

    @Override
    public String toString() {
        return switch (type) {
            case PLAY_CARD -> player.getName() + " joue " + card;
            case DRAW_CARD -> player.getName() + " pioche";
            case QUIT_ROUND -> player.getName() + " passe la manche";
        };
    }
}