package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.MoveType;

/**
 * Représente un coup joué par un joueur.
 * Move est un pur objet de données (pas de logique métier).
 * L'exécution et la validation appartiennent à Game/RuleEngine.
 * Exemples de construction :
 * Move.playCard(player, CardType.THREE)   → joue un 3
 * Move.drawCard(player)                   → pioche
 * Move.quitRound(player)                  → passe la manche
 */
public class Move {

    private final Player player;
    private final MoveType type;
    private final CardType card;

    private Move(Player player, MoveType type, CardType card) {
        if (player == null) throw new IllegalArgumentException("Player ne peut pas être null");
        if (type == null) throw new IllegalArgumentException("MoveType ne peut pas être null");
        if (type == MoveType.PLAY_CARD && card == null)
            throw new IllegalArgumentException("Une carte est requise pour PLAY_CARD");

        this.player = player;
        this.type = type;
        this.card = card;
    }

    /**
     * Crée un coup "jouer une carte".
     *
     * @param player le joueur qui joue
     * @param card   la carte jouée
     */
    public static Move playCard(Player player, CardType card) {
        return new Move(player, MoveType.PLAY_CARD, card);
    }

    /**
     * Crée un coup "piocher une carte".
     *
     * @param player le joueur qui pioche
     */
    public static Move drawCard(Player player) {
        return new Move(player, MoveType.DRAW_CARD, null);
    }

    /**
     * Crée un coup "passer la manche".
     *
     * @param player le joueur qui passe
     */
    public static Move quitRound(Player player) {
        return new Move(player, MoveType.QUIT_ROUND, null);
    }

    public Player getPlayer() {
        return player;
    }

    public MoveType getType() {
        return type;
    }

    /**
     * @return la carte jouée, ou {@code null} si le type n'est pas PLAY_CARD
     */
    public CardType getCard() {
        return card;
    }

    /**
     * @return true si ce coup implique de jouer une carte spécifique
     */
    public boolean isPlayCard() {
        return type == MoveType.PLAY_CARD;
    }

    /**
     * @return true si ce coup est une pioche
     */
    public boolean isDrawCard() {
        return type == MoveType.DRAW_CARD;
    }

    /**
     * @return true si le joueur passe la manche
     */
    public boolean isQuitRound() {
        return type == MoveType.QUIT_ROUND;
    }

    @Override
    public String toString() {
        return switch (type) {
            case PLAY_CARD -> player.getName() + " joue " + card;
            case DRAW_CARD -> player.getName() + " pioche";
            case QUIT_ROUND -> player.getName() + " passe la manche";
        };
    }
}