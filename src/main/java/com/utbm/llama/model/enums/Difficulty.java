package main.java.com.utbm.llama.model.enums;

/**
 * Niveau de difficulté des bots.
 * EASY   → joue aléatoirement parmi les coups légaux
 * MEDIUM → préfère jouer une carte plutôt que piocher,
 * passe la manche si aucun coup n'est possible
 * HARD   → minimise la valeur des cartes restantes en main,
 * anticipe les cartes jouées et passe stratégiquement
 */
public enum Difficulty {

    EASY,
    MEDIUM,
    HARD;

    @Override
    public String toString() {
        return switch (this) {
            case EASY -> "Facile";
            case MEDIUM -> "Moyen";
            case HARD -> "Difficile";
        };
    }
}