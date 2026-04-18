package main.java.com.utbm.llama.model.enums;

/**
 * Mode de jeu choisi avant le début de la partie.
 * SHORT → 6 manches, seuil honorifique : 180 crédits
 * LONG  → 10 manches, seuil honorifique : 300 crédits
 * + bonus DETEC de +30 crédits si ≥ 120 crédits à la fin de la manche 4
 * Dans les deux modes, le vrai gagnant est celui qui a le plus de crédits
 * à la fin de la dernière manche. Les seuils sont purement honorifiques
 * (équivalent d'être "diplômé").
 */
public enum GameMode {

    /**
     * Mode court — 6 manches.
     * Seuil honorifique : 180 crédits (= valider sa LP à l'UTBM).
     */
    SHORT(6, 180),

    /**
     * Mode long — 10 manches.
     * Seuil honorifique : 300 crédits (= valider son master à l'UTBM).
     * Bonus DETEC disponible à la fin de la manche 4.
     */
    LONG(10, 300);

    private final int maxRounds;
    private final int graduationThreshold;

    GameMode(int maxRounds, int graduationThreshold) {
        this.maxRounds = maxRounds;
        this.graduationThreshold = graduationThreshold;
    }

    /**
     * @return le nombre total de manches dans ce mode.
     */
    public int getMaxRounds() {
        return maxRounds;
    }

    /**
     * @return le seuil de crédits honorifique (diplôme).
     */
    public int getGraduationThreshold() {
        return graduationThreshold;
    }

    /**
     * @return true si le bonus DETEC est applicable dans ce mode.
     * (uniquement en mode LONG, à la fin de la manche 4)
     */
    public boolean hasDetecBonus() {
        return this == LONG;
    }

    /**
     * Numéro de la manche après laquelle le bonus DETEC est vérifié.
     */
    public static final int DETEC_ROUND = 4;

    /**
     * Seuil de crédits pour obtenir le bonus DETEC.
     */
    public static final int DETEC_THRESHOLD = 120;

    /**
     * Valeur du bonus DETEC en crédits.
     */
    public static final int DETEC_BONUS = 30;

    @Override
    public String toString() {
        return this == SHORT
                ? "Court (6 manches — 180 crédits)"
                : "Long (10 manches — 300 crédits)";
    }
}
