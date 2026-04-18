package main.java.com.utbm.llama.model.enums;

/**
 * État d'un joueur au cours d'une manche.
 * PLAYING  → le joueur est actif et doit jouer à son tour
 * QUITTING → le joueur a passé la manche (QUIT_ROUND)
 * il attend la fin de manche sans jouer
 * Transitions autorisées :
 * PLAYING → QUITTING  (action QUIT_ROUND, irréversible dans la manche)
 * QUITTING → PLAYING  (uniquement au début de la manche suivante)
 */
public enum State {

    /**
     * Le joueur participe activement à la manche en cours.
     */
    PLAYING,

    /**
     * Le joueur a passé sa manche.
     * Il ne joue plus mais ses cartes restantes lui seront déduites en fin de manche.
     */
    QUITTING;

    @Override
    public String toString() {
        return this == PLAYING ? "En jeu" : "A passé";
    }
}