package test.java.com.utbm.modeltest;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.State;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String        name;
    private List<CardType> hand           = new ArrayList<>();
    private int           credits         = 35;
    private int           startingHandSize = 6;
    private boolean       hasStudyAbroad  = false;
    private boolean       suspended       = false;
    private int           creditsLostThisRound = 0;
    private State state           = State.PLAYING;

    public Player(String name) { this.name = name; }

    // Hand
    public void addCard(CardType c)    { hand.add(c); }
    public void removeCard(CardType c) { hand.remove(c); }
    public List<CardType> getHand()    { return hand; }

    // Credits
    public int  getCredits()           { return credits; }
    public void setCredits(int c)      { credits = c; }
    public void addCredits(int n)      { credits += n; }

    // Flags
    public boolean hasStudyAbroad()         { return hasStudyAbroad; }
    public void    setStudyAbroad(boolean b) { hasStudyAbroad = b; }
    public boolean isSuspended()            { return suspended; }
    public void    setSuspended(boolean b)  { suspended = b; }
    public int     getCreditsLostThisRound()       { return creditsLostThisRound; }
    public void    setCreditsLostThisRound(int n)  { creditsLostThisRound = n; }
    public int     getStartingHandSize()            { return startingHandSize; }
    public void    setStartingHandSize(int n)       { startingHandSize = n; }

    // State
    public State getState()             { return state; }
    public void  changeState(State s)   { state = s; }

    public String getName() { return name; }

    public int computeCredits() {
        return hand.stream().mapToInt(CardType::getValue).sum();
    }
}