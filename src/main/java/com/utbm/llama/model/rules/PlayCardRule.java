package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.enums.State;

/**
 * Règle : JOUER UNE CARTE (PLAY_CARD)
 * Vérifie les trois conditions nécessaires pour poser une carte :
 *  1. Le joueur est en état PLAYING (pas QUITTING)
 *  2. Le joueur possède bien cette carte dans sa main
 *  3. La carte respecte les règles de succession L.A.M.A :
 *       - Défausse vide → toute carte est jouable
 *       - Même valeur que le dessus → jouable
 *       - Valeur immédiatement supérieure de 1 → jouable
 *       - SIX en haut + LLAMA → jouable
 *       - LLAMA en haut + ONE → jouable (cycle)
 * Effet appliqué : retire la carte de la main et la pose sur la défausse.
 * (En pratique, Game.applyMove() fait déjà ça — apply() ici est un no-op
 *  pour respecter le pattern, l'application réelle est dans Game.)
 */
public class PlayCardRule implements Rule {

    @Override
    public boolean isApplicable(Move move, Game game) {
        return move.getType() == MoveType.PLAY_CARD;
    }

    @Override
    public boolean validate(Move move, Game game) {
        if (move.getPlayer().getState() == State.QUITTING) {
            logRefusal(move, "le joueur a déjà passé la manche");
            return false;
        }

        if (!move.getPlayer().getHand().contains(move.getCard())) {
            logRefusal(move, "la carte " + move.getCard() + " n'est pas dans la main du joueur");
            return false;
        }

        CardType topOfDiscard = game.getDiscardPile().peek();
        if (!move.getCard().canBePlayedOn(topOfDiscard)) {
            logRefusal(move, move.getCard() + " ne peut pas être joué sur " + topOfDiscard);
            return false;
        }

        return true;
    }

    @Override
    public void apply(Move move, Game game) {

    }

    @Override
    public String getName() { return "PlayCardRule"; }

    private void logRefusal(Move move, String reason) {
        System.out.println("[" + getName() + "] ✗ " + move.getPlayer().getName()
                + " ne peut pas jouer " + move.getCard() + " — " + reason);
    }
}