package main.java.com.utbm.llama.model.enums;

/**
 * Les trois actions qu'un joueur peut effectuer lors de son tour.
 * PLAY_CARD  — Jouer une carte de sa main sur la défausse.
 * DRAW_CARD  — Piocher une carte (si la pioche n'est pas vide).
 * QUIT_ROUND — Se coucher : le joueur quitte la manche en cours.
 *              Il récupérera des jetons de pénalité en fin de manche.
 */
public enum MoveType {
    PLAY_CARD,
    DRAW_CARD,
    QUIT_ROUND
}
