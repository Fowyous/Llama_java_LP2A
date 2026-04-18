package test.java.com.utbm.modeltest;

import main.java.com.utbm.llama.model.enums.State;

import java.util.List;

public class Round {
    private int          roundNumber;
    private List<Player> activePlayers;

    public Round(int n, List<Player> players) {
        this.roundNumber   = n;
        this.activePlayers = players;
    }

    public int          getRoundNumber()  { return roundNumber; }
    public List<Player> getActivePlayers(){ return activePlayers; }
    public boolean      isOver()         { return activePlayers.stream().allMatch(p -> p.getState() == State.QUITTING || p.getHand().isEmpty()); }
}