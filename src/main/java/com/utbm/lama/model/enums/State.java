package main.java.com.utbm.lama.model.enums;

/**
 * État courant d'un joueur dans la manche.
 * PLAYING  — Le joueur est encore actif : il joue son tour normalement.
 * QUITTING — Le joueur s'est couché (QUIT_ROUND) : il ne joue plus
 *            cette manche mais attend la fin pour récupérer ses pénalités.
 */
public enum State {
    PLAYING,
    QUITTING
}
