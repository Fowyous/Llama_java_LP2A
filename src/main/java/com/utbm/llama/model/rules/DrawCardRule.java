package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.enums.State;

/**
 * Règle : PIOCHER UNE CARTE (DRAW_CARD)
 * Vérifie les deux conditions pour piocher :
 *  1. Le joueur est en état PLAYING (pas QUITTING)
 *  2. La pioche n'est pas vide
 * Effet : Game.applyMove() retire une carte de la pioche et la donne au joueur.
 * Note sur la stratégie :
 *  Piocher est toujours légal si les deux conditions sont remplies.
 *  C'est au joueur (ou au bot) de décider si c'est judicieux.
 */
public class DrawCardRule implements Rule {

    @Override
    public boolean isApplicable(Move move, Game game) {
        return move.getType() == MoveType.DRAW_CARD;
    }

    @Override
    public boolean validate(Move move, Game game) {
        if (move.getPlayer().getState() == State.QUITTING) {
            logRefusal(move, "le joueur a déjà passé la manche");
            return false;
        }

        if (game.getDrawPile().isEmpty()) {
            logRefusal(move, "la pioche est vide");
            return false;
        }

        return true;
    }

    @Override
    public void apply(Move move, Game game) {
    }

    @Override
    public String getName() { return "DrawCardRule"; }

    private void logRefusal(Move move, String reason) {
        System.out.println("[" + getName() + "] ✗ " + move.getPlayer().getName()
                + " ne peut pas piocher — " + reason);
    }
}