package main.java.com.utbm.lama.model.enums;

/**
 * Niveau de difficulté d'un Bot.
 * EASY   — Joue aléatoirement parmi les coups légaux.
 * MEDIUM — Applique quelques heuristiques simples
 *          (préfère jouer une carte plutôt que piocher,
 *           se couche si la main est trop chargée).
 * HARD   — Stratégie avancée : tient compte des cartes
 *           visibles et du score des adversaires.
 */
public enum Difficulty {
    EASY,
    MEDIUM,
    HARD
}