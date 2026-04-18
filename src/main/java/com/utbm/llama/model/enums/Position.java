package main.java.com.utbm.lama.model.enums;

/**
 * Position d'affichage d'un joueur autour du plateau.
 * Le joueur humain principal occupe toujours SOUTH.
 * Les joueurs suivants sont assignés dans le sens horaire :
 *          NORTH
 *            |
 *  WEST -----+----- EAST
 *            |
 *          SOUTH  ← joueur 1 (humain)
 * Règles d'attribution selon le nombre de joueurs :
 *   2 joueurs  → SOUTH, NORTH
 *   3 joueurs  → SOUTH, NORTH, EAST
 *   4 joueurs  → SOUTH, NORTH, EAST, WEST
 * La position détermine aussi l'orientation visuelle de la main
 * (cartes horizontales pour SOUTH/NORTH, verticales pour EAST/WEST).
 */
public enum Position {

    SOUTH,
    NORTH,
    EAST,
    WEST;

    /**
     * Retourne les positions dans l'ordre d'attribution
     * pour {@code playerCount} joueurs.
     *
     * @param playerCount nombre total de joueurs (1 à 4)
     * @return tableau de positions dans l'ordre d'assignation
     * @throws IllegalArgumentException si playerCount est hors [1,4]
     */
    public static Position[] forPlayerCount(int playerCount) {
        if (playerCount < 1 || playerCount > 4) {
            throw new IllegalArgumentException(
                    "Le nombre de joueurs doit être compris entre 1 et 4, reçu : " + playerCount
            );
        }
        Position[] all = {SOUTH, NORTH, EAST, WEST};
        Position[] result = new Position[playerCount];
        System.arraycopy(all, 0, result, 0, playerCount);
        return result;
    }

    /**
     * Indique si la main doit s'afficher à la verticale (joueurs latéraux).
     */
    public boolean isVertical() {
        return this == EAST || this == WEST;
    }
}
