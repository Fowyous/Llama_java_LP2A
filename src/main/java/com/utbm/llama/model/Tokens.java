package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.TokenType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Stock central de jetons de la partie.
 * Le jeu L.A.M.A. de base contient :
 *   - 40 jetons blancs (valeur 1)
 *   - 20 jetons noirs  (valeur 10)
 * Cette classe représente la "banque" commune, pas les jetons
 * détenus par un joueur (ceux-ci sont dans Player).
 * Les constantes INITIAL_WHITE et INITIAL_BLACK sont modifiables
 * pour adapter les règles UTBM sans toucher à la logique.
 */
public class Tokens {

    public static final int INITIAL_WHITE = 40;
    public static final int INITIAL_BLACK = 20;

    private final Map<TokenType, Integer> stock;

    /**
     * Crée un stock vide.
     */
    public Tokens() {
        stock = new EnumMap<>(TokenType.class);
        for (TokenType t : TokenType.values()) {
            stock.put(t, 0);
        }
    }

    /**
     * Crée un stock plein selon les quantités initiales du jeu.
     */
    public static Tokens createFull() {
        Tokens tokens = new Tokens();
        tokens.stock.put(TokenType.WHITE, INITIAL_WHITE);
        tokens.stock.put(TokenType.BLACK, INITIAL_BLACK);
        return tokens;
    }

    /**
     * @return le nombre de jetons disponibles pour ce type
     */
    public int getCount(TokenType type) {
        return stock.getOrDefault(type, 0);
    }

    /**
     * @return true si ce type de jeton est épuisé dans le stock
     */
    public boolean isEmpty(TokenType type) {
        return getCount(type) == 0;
    }

    /**
     * @return true si le stock est entièrement vide (tous types confondus)
     */
    public boolean isFullyEmpty() {
        return stock.values().stream().allMatch(n -> n == 0);
    }

    /**
     * Retire un jeton du stock et le retourne au joueur.
     * Si ce type est épuisé, tente d'utiliser l'équivalent en jetons
     * de l'autre type (un BLACK vaut 10 WHITE).
     *
     * @param type type du jeton demandé
     * @return true si un jeton a pu être fourni, false si le stock est épuisé
     */
    public boolean takeToken(TokenType type) {
        if (!isEmpty(type)) {
            stock.put(type, stock.get(type) - 1);
            return true;
        }
        if (type == TokenType.BLACK && getCount(TokenType.WHITE) >= 10) {
            stock.put(TokenType.WHITE, stock.get(TokenType.WHITE) - 10);
            return true;
        }
        return false;
    }

    /**
     * Ajoute un jeton au stock (retour de fin de manche).
     *
     * @param type type du jeton rendu
     */
    public void returnToken(TokenType type) {
        stock.put(type, stock.get(type) + 1);
    }

    /**
     * Ajoute {@code amount} jetons d'un type au stock.
     *
     * @param type   type de jeton
     * @param amount quantité à ajouter (doit être >= 0)
     */
    public void returnTokens(TokenType type, int amount) {
        if (amount < 0) throw new IllegalArgumentException("Le montant ne peut pas être négatif");
        stock.put(type, stock.get(type) + amount);
    }

    @Override
    public String toString() {
        return "Tokens{blanc=" + getCount(TokenType.WHITE) +
                ", noir=" + getCount(TokenType.BLACK) + "}";
    }
}
