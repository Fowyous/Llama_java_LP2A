package main.java.com.utbm.llama.model.rules;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.enums.MoveType;
import main.java.com.utbm.llama.model.enums.State;

/**
 * Règle : PASSER LA MANCHE (QUIT_ROUND)
 * Vérifie la seule condition pour passer :
 *  1. Le joueur est en état PLAYING (pas déjà QUITTING)
 * Passer est toujours autorisé pour un joueur actif, même s'il a
 * des cartes jouables — c'est un choix stratégique valide.
 * Conséquence métier (gérée par Round.endCurrentRound()) :
 *  - Les cartes restantes en main sont déduites des crédits en fin de manche
 *  - Si la perte est ≥ 20 crédits → convocation au jury
 */
public class QuitRoundRule implements Rule {

    @Override
    public boolean isApplicable(Move move, Game game) {
        return move.getType() == MoveType.QUIT_ROUND;
    }

    @Override
    public boolean validate(Move move, Game game) {
        if (move.getPlayer().getState() == State.QUITTING) {
            logRefusal(move, "le joueur a déjà passé cette manche");
            return false;
        }

        if (move.getPlayer().isSuspended()) {
            logRefusal(move, "le joueur est en semestre de césure");
            return false;
        }

        return true;
    }

    @Override
    public void apply(Move move, Game game) {
    }

    @Override
    public String getName() { return "QuitRoundRule"; }

    private void logRefusal(Move move, String reason) {
        System.out.println("[" + getName() + "] ✗ " + move.getPlayer().getName()
                + " ne peut pas passer — " + reason);
    }
}