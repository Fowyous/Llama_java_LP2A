package main.java.com.utbm.llama.model.enums;

/**
 * Représente les 7 valeurs de cartes du jeu L.A.M.A.
 * La carte LLAMA joue le rôle de joker : elle peut être posée
 * sur un 6, et un 1 peut être posé sur elle (boucle circulaire).
 */
public enum CardType {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    LLAMA(7);

    private final int value;

    CardType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    /**
     * Indique si {@code other} peut être posée sur {@code this}.
     * Règle : on peut poser la même valeur, ou la valeur suivante.
     * La succession est circulaire : LLAMA → ONE est valide.
     *
     * @param other la carte que l'on souhaite poser
     * @return true si le coup est légal
     */
    public boolean accepts(CardType other) {
        if (this == other) return true;
        int next = (this.value % 7) + 1;
        return other.value == next;
    }

    /**
     * Retourne le nom d'affichage lisible (utile pour les logs et la console).
     */
    @Override
    public String toString() {
        return this == LLAMA ? "Lama" : String.valueOf(value);
    }
}
