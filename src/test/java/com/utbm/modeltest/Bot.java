package test.java.com.utbm.modeltest;

import main.java.com.utbm.llama.model.enums.Difficulty;

public class Bot extends Player {
    private Difficulty difficulty;
    public Bot(String name, Difficulty d) { super(name); this.difficulty = d; }
    public Difficulty getDifficulty() { return difficulty; }
}
