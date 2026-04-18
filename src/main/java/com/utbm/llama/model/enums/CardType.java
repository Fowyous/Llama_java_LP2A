package main.java.com.utbm.llama.model.enums;

/**
 * Représente les 7 types de cartes du jeu L.A.M.A.
 * Valeurs :
 * ONE  → 1
 * TWO  → 2
 * ...
 * SIX  → 6
 * LLAMA → 10
 * Règle de succession (pour valider un coup) :
 * ONE < TWO < ... < SIX < LLAMA < ONE  (cycle)
 */
public enum CardType {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    LLAMA(10);

    private final int value;

    CardType(int value) {
        this.value = value;
    }

    /**
     * @return la valeur de la carte en crédits (1-6 ou 10 pour LLAMA).
     */
    public int getValue() {
        return value;
    }

    /**
     * Vérifie si cette carte peut être jouée sur {@code topOfDiscard}.
     * Règles L.A.M.A :
     * - Défausse vide (topOfDiscard == null)  → toujours jouable
     * - Même valeur que le dessus             → jouable
     * - Valeur immédiatement supérieure       → jouable  (ex: 3 sur 2)
     * - SIX en dessous → LLAMA jouable
     * - LLAMA en dessous → ONE jouable  (cycle)
     *
     * @param topOfDiscard carte du dessus de la défausse, ou null si vide
     * @return true si le coup est légal
     */
    public boolean canBePlayedOn(CardType topOfDiscard) {
        if (topOfDiscard == null) {
            return true;
        }
        if (this == topOfDiscard) {
            return true;
        }
        if (this.value == topOfDiscard.value + 1) {
            return true;
        }
        if (topOfDiscard == SIX && this == LLAMA) {
            return true;
        }
        if (topOfDiscard == LLAMA && this == ONE) {
            return true;
        }
        return false;
    }

    /**
     * Retourne la carte suivante dans le cycle (ONE → TWO → ... → LLAMA → ONE).
     * Utile pour les bots et la validation.
     */
    public CardType next() {
        CardType[] values = CardType.values();
        return values[(this.ordinal() + 1) % values.length];
    }

    @Override
    public String toString() {
        return this == LLAMA ? "LLAMA (10)" : name() + " (" + value + ")";
    }
}