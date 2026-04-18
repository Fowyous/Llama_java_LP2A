package main.java.com.utbm.lama.model.enums;

public enum TokenType {
    BLACK,
    WHITE;

    public Integer getPenaltyValue() {
        return this.ordinal();
    }
}
