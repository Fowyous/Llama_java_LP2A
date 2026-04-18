package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.MoveType;

/**
 * Représente un coup joué par un joueur.
 * Move est un objet de données pur (POJO) : il ne contient
 * aucune logique d'exécution. C'est le RuleEngine qui valide
 * le coup, et Game qui l'applique.
 * Champs :
 *   player   — joueur qui effectue le coup (jamais null)
 *   type     — nature du coup : PLAY_CARD, DRAW_CARD ou QUIT_ROUND
 *   card     — carte jouée, uniquement renseignée pour PLAY_CARD
 *              (null pour DRAW_CARD et QUIT_ROUND)
 */
public class Move {

    private final Player player;
    private final MoveType type;
    private final CardType card;

    /**
     * Crée un coup "jouer une carte".
     *
     * @param player joueur qui joue
     * @param card   carte posée sur la défausse
     */
    public static Move playCard(Player player, CardType card) {
        if (card == null) {
            throw new IllegalArgumentException("La carte ne peut pas être null pour PLAY_CARD");
        }
        return new Move(player, MoveType.PLAY_CARD, card);
    }

    /**
     * Crée un coup "piocher une carte".
     *
     * @param player joueur qui pioche
     */
    public static Move drawCard(Player player) {
        return new Move(player, MoveType.DRAW_CARD, null);
    }

    /**
     * Crée un coup "se coucher" (quitter la manche).
     *
     * @param player joueur qui se couche
     */
    public static Move quitRound(Player player) {
        return new Move(player, MoveType.QUIT_ROUND, null);
    }

    private Move(Player player, MoveType type, CardType card) {
        if (player == null) throw new IllegalArgumentException("Le joueur ne peut pas être null");
        if (type   == null) throw new IllegalArgumentException("Le type de coup ne peut pas être null");
        this.player = player;
        this.type   = type;
        this.card   = card;
    }

    public Player getPlayer()  {
        return player;
    }

    public MoveType getType()  {
        return type;
    }

    /**
     * @return la carte jouée, ou null si le type n'est pas PLAY_CARD
     */
    public CardType getCard()  {
        return card;
    }

    @Override
    public String toString() {
        return switch (type) {
            case PLAY_CARD  -> player.getName() + " joue " + card;
            case DRAW_CARD  -> player.getName() + " pioche";
            case QUIT_ROUND -> player.getName() + " se couche";
        };
    }
}
