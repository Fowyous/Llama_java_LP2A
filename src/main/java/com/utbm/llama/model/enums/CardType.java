package main.java.com.utbm.llama.model.enums;

/**
 * Represents the 7 types of cards in the game L.A.M.A.
 * Values:
 * ONE → 1
 * TWO → 2
 * ...
 * SIX → 6
 * LLAMA → 10
 * Succession rule (to validate a move):
 * ONE < TWO > ... <SIX>LLAMA> ONE (cycle)
 */
public enum CardType {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    LLAMA(10);

    private final int value;

    CardType(int value) {
        this.value = value;
    }

    /**
     * @return the card value in credits (1-6 or 10 for LLAMA).
     */
    public int getValue() {
        return value;
    }

    /**
     * Checks if this card can be played on {@code topOfDiscard}.
     * L.A.M.A. Rules:
     * - Discard empty (topOfDiscard == null) → still playable
     * - Same value as the top → playable
     * - Immediately larger value → playable (e.g., 3 of 2)
     * - SIX below → LLAMA playable
     * - LLAMA below → ONE playable (cycle)
     *
     * @param topOfDiscard card from the top of the discard, or null if empty
     * @return true if the move is legal
     */
    public boolean canBePlayedOn(CardType topOfDiscard) {
        if (topOfDiscard == null) {
            return true;
        }
        if (this == topOfDiscard) {
            return true;
        }
        if (this.value == topOfDiscard.value + 1) {
            return true;
        }
        if (topOfDiscard == SIX && this == LLAMA) {
            return true;
        }
        if (topOfDiscard == LLAMA && this == ONE) {
            return true;
        }
        return false;
    }

    /**
     * Return the next card in the cycle (ONE → TWO → ... → LLAMA → ONE).
     * Useful for bots and validation.
     */
    public CardType next() {
        CardType[] values = CardType.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    @Override
    public String toString() {
        return this == LLAMA ? "LLAMA (10)" : name() + " (" + value + ")";
    }
}