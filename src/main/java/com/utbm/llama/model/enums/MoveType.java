package main.java.com.utbm.llama.model.enums;

/**
 * Les trois actions qu'un joueur peut effectuer pendant son tour.
 * PLAY_CARD  → jouer une carte de sa main sur la défausse
 * DRAW_CARD  → piocher une carte (met fin au tour)
 * QUIT_ROUND → passer le reste de la manche (irréversible)
 * Note : les actions automatiques de fin de manche (jury, césure,
 * distribution de crédits) ne sont PAS des MoveType — elles sont
 * gérées directement par Round et RuleEngine.
 */
public enum MoveType {

    /** Le joueur pose une carte de sa main sur la défausse. */
    PLAY_CARD,

    /** Le joueur pioche une carte depuis la pioche. Son tour se termine. */
    DRAW_CARD,

    /**
     * Le joueur abandonne le reste de la manche.
     * Il ne jouera plus jusqu'au début de la manche suivante.
     * Ses crédits seront déduits de la valeur des cartes encore en main.
     */
    QUIT_ROUND;

    @Override
    public String toString() {
        return switch (this) {
            case PLAY_CARD  -> "Jouer une carte";
            case DRAW_CARD  -> "Piocher";
            case QUIT_ROUND -> "Passer la manche";
        };
    }
}