package test.java.com.utbm.modeltest;

import main.java.com.utbm.llama.model.enums.GameMode;

import java.util.List;

public class Game {
    private List<Player> players;
    private Deck         drawPile;
    private Deck         discardPile;
    private GameMode gameMode;
    private Round        currentRound;
    private int          currentPlayerIndex = 0;

    public Game(List<Player> players, GameMode mode) {
        this.players     = players;
        this.gameMode    = mode;
        this.drawPile    = Deck.createFull();
        this.discardPile = Deck.empty();
        this.currentRound = new Round(1, players);
    }

    public List<Player> getPlayers()      { return players; }
    public Deck         getDrawPile()     { return drawPile; }
    public Deck         getDiscardPile()  { return discardPile; }
    public GameMode     getGameMode()     { return gameMode; }
    public Round        getCurrentRound() { return currentRound; }
    public Player       getCurrentPlayer(){ return players.get(currentPlayerIndex); }

    public int  getGraduationThreshold()  { return gameMode == GameMode.SHORT ? 180 : 300; }
    public boolean isGraduated(Player p)  { return p.getCredits() >= getGraduationThreshold(); }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }
}