package main.java.com.utbm.llama.model;

public class Game {
    Player player1;
    Player player2;
    Player player3;
    Player player4;
    Deck drawPile;
    Deck discardPile;
    Tokens tokens;
    Round round;

    public Deck getDrawPile(){
	    return drawPile;
    }
    public Deck getDiscardPile(){
	    return discardPile;
    }
}
