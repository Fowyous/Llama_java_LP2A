package main.java.com.utbm.llama.model.enums;

/**
 * Level of difficulty for bots.
 * EASY → plays randomly among the legal moves
 * MEDIUM → prefers to play a card rather than draw,
 * passes the sleeve if no shot is possible
 * HARD → minimizes the value of the remaining cards in hand,
 * anticipates the played cards and strategically passes
 */
public enum Difficulty {

    EASY,
    MEDIUM,
    HARD;

    @Override
    public String toString() {
        return switch (this) {
            case EASY -> "Facile";
            case MEDIUM -> "Moyen";
            case HARD -> "Difficile";
        };
    }
}