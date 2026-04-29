package main.java.com.utbm.llama.model.enums;

/**
 * State of a player during a round.
 * PLAYING  → the player is active and must play in turn
 * QUITTING → the player has passed the round (QUIT_ROUND)
 * he’s waiting for the end of the round without playing
 * Allowed Transitions:
 * PLAYING → QUITTING  (action QUIT_ROUND, irreversible in the round)
 * QUITTING → PLAYING  (only at the beginning of the next round)
 */
public enum State {

    /**
     * The player actively participates in the current round.
     */
    PLAYING,

    /**
     * The player passed his sleeve.
     * He no longer plays, but his remaining cards will be deducted from him at the end of the round.
     */
    QUITTING;

    @Override
    public String toString() {
        return this == PLAYING ? "En jeu" : "A passé";
    }
}