package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.model.Jury;
import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.view.JuryView;
import main.java.com.utbm.llama.view.MainFrame;

import javax.swing.*;

/**
 * Controller of the "Jury" mini-game.
 * Context: triggered when a player loses ≥ 20 credits in a round.
 * The player chooses 1 card among 7 hidden and wins its value in credits.
 * Responsibilities:
 * - Initialize the JuryView with the player’s data
 * - Manage card selection and reveal
 * - Apply the credit gain to the Player model
 * - Check if the player is still in negative after the jury
 * (→ then triggers the CésureController)
 * - Notify the BoardController once the jury has finished
 */
public class JuryController {

    private final MainFrame mainFrame;
    private JuryView juryView;
    private BoardController boardController;

    private Player targetPlayer;
    private int creditsLostThisRound;
    private Runnable onJuryFinished;

    private Jury juryModel;

    public JuryController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    /**
     * Starts the jury for a given player.
     *
     * @param player      the player summoned before the jury
     * @param creditsLost lost credits this round (≥ 20)
     * @param onFinished  callback called when the jury is finished
     */
    public void startJury(Player player, int creditsLost, Runnable onFinished) {
        this.targetPlayer = player;
        this.creditsLostThisRound = creditsLost;
        this.onJuryFinished = onFinished;

        this.juryModel = new Jury(player, creditsLost);

        juryView = new JuryView();
        juryView.setup(player.getName(), creditsLost, player.getCredits());
        juryView.addConfirmListener(e -> handleCardPicked(juryView.getSelectedIndex()));
        mainFrame.showJury(juryView);
    }

    /**
     * Processes the player’s card choice.
     *
     * @param index index of the chosen map (0-6)
     */
    private void handleCardPicked(int index) {
        if (index < 0) return;

        CardType revealed = juryModel.pickCard(index);

        juryView.revealCard(index, revealed);

        System.out.println("[JURY] " + targetPlayer.getName()
                + " gagne " + revealed.getValue() + " crédits → total : "
                + targetPlayer.getCredits());

        javax.swing.Timer delay = new javax.swing.Timer(1800, evt -> {
            ((javax.swing.Timer) evt.getSource()).stop();
            evaluatePostJury();
        });
        delay.setRepeats(false);
        delay.start();
    }

    /**
     * Check if the player is still in negative after the jury.
     * - If so → gap semester
     * - If not → return to the set
     */
    private void evaluatePostJury() {
        if (juryModel.requiresCesure()) {
            boardController.triggerCesure(targetPlayer, onJuryFinished);
        } else {
            notifyFinished();
        }
    }

    /**
     * Finalise la séquence du jury en revenant à la vue du plateau de jeu et en déclenchant le callback de fin pour passer au candidat suivant.
     */
    private void notifyFinished() {
        SwingUtilities.invokeLater(() -> {
            if (boardController.getBoardView() != null) {
                mainFrame.showGame(boardController.getBoardView());
            }
            if (onJuryFinished != null) {
                onJuryFinished.run();
            }
        });
    }

    /**
     * Établit la connexion avec le contrôleur du plateau pour permettre la navigation entre le mini-jeu et la partie principale.
     */
    public void setBoardController(BoardController bc) {
        this.boardController = bc;
    }

    /**
     * Récupère l'instance de la vue associée au jury pour l'affichage dans la fenêtre principale.
     */
    public JuryView getJuryView() {
        return juryView;
    }
}