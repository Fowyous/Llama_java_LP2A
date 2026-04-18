package test.java.com.utbm.modeltest;

import main.java.com.utbm.llama.model.enums.CardType;

import java.util.*;

public class Deck {
    private final Deque<CardType> cards = new ArrayDeque<>();

    public static Deck createFull() {
        Deck d = new Deck();
        for (int i = 0; i < 8; i++)
            for (CardType ct : CardType.values())
                d.cards.addLast(ct);
        Collections.shuffle((List<?>) new ArrayList<>(d.cards));
        return d;
    }

    public static Deck empty() { return new Deck(); }

    public void    shuffle()        { List<CardType> l = new ArrayList<>(cards); Collections.shuffle(l); cards.clear(); cards.addAll(l); }
    public CardType draw()          { return cards.isEmpty() ? null : cards.pollFirst(); }
    public CardType peek()          { return cards.peekFirst(); }
    public boolean  isEmpty()       { return cards.isEmpty(); }
    public void     add(CardType c) { cards.addLast(c); }
    public int      size()          { return cards.size(); }
}
