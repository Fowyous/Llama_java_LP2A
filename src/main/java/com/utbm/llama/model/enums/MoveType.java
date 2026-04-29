package main.java.com.utbm.llama.model.enums;

/**
 * The three actions a player can perform during their turn.
 * PLAY_CARD  → play a card from your hand on the discard
 * DRAW_CARD  → draw a card (ends the turn)
 * QUIT_ROUND → pass the rest of the round (irreversible)
 * Note: the automatic actions at the end of the round (jury, caesura,
 * credit distribution) are NOT MoveType—they are
 * managed directly by Round and RuleEngine.
 */
public enum MoveType {

    /**
     * The player places a card from his hand on the discard pile.
     */
    PLAY_CARD,

    /**
     * The player draws a card from the draw pile. Their turn ends.
     */
    DRAW_CARD,

    /**
     * The player abandons the rest of the round.
     * He will not play again until the beginning of the next round.
     * His credits will be deducted from the value of the cards still in hand.
     */
    QUIT_ROUND;

    @Override
    public String toString() {
        return switch (this) {
            case PLAY_CARD -> "Jouer une carte";
            case DRAW_CARD -> "Piocher";
            case QUIT_ROUND -> "Passer la manche";
        };
    }
}