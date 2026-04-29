package main.java.com.utbm.llama.model.enums;

/**
 * Represent the types of credit tokens in the game (BLACK or WHITE).
 * These tokens typically correspond to different point values or categories.
 */
public enum TokenType {
    BLACK, WHITE;

    /**
     * Get the penalty value associated with the token type.
     * Based on the ordinal position of the enum constant.
     *
     * @return the numerical penalty value as an Integer
     */
    public Integer getPenaltyValue() {
        return this.ordinal();
    }
}
