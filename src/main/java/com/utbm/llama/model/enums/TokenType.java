package main.java.com.utbm.llama.model.enums;

public enum TokenType {
    BLACK,
    WHITE;

    public Integer getPenaltyValue() {
        return this.ordinal();
    }
}
