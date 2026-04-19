package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.model.Jury;
import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.view.JuryView;
import main.java.com.utbm.llama.view.MainFrame;

import javax.swing.*;

/**
 * Contrôleur du mini-jeu "Jury".
 * Contexte : déclenché quand un joueur perd ≥ 20 crédits dans une manche.
 * Le joueur choisit 1 carte parmi 7 cachées et gagne sa valeur en crédits.
 * Responsabilités :
 * - Initialiser la JuryView avec les données du joueur concerné
 * - Gérer la sélection et la révélation de la carte
 * - Appliquer le gain de crédits sur le modèle Player
 * - Vérifier si le joueur est toujours en négatif après le jury
 * (→ déclenche alors la CésureController)
 * - Notifier le BoardController une fois le jury terminé
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
     * Démarre le jury pour un joueur donné.
     *
     * @param player      le joueur convoqué devant le jury
     * @param creditsLost crédits perdus cette manche (≥ 20)
     * @param onFinished  callback appelé quand le jury est terminé
     */
    public void startJury(Player player, int creditsLost, Runnable onFinished) {
        this.targetPlayer         = player;
        this.creditsLostThisRound = creditsLost;
        this.onJuryFinished       = onFinished;

        this.juryModel = new Jury(player, creditsLost);

        juryView = new JuryView();
        juryView.setup(player.getName(), creditsLost, player.getCredits());
        juryView.addConfirmListener(e -> handleCardPicked(juryView.getSelectedIndex()));
        mainFrame.showJury(juryView);
    }

    /**
     * Traite le choix de carte du joueur.
     *
     * @param index index de la carte choisie (0-6)
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
     * Vérifie si le joueur est toujours en négatif après le jury.
     * - Si oui  → semestre de césure
     * - Si non  → retour au plateau
     */
    private void evaluatePostJury() {
        if (juryModel.requiresCesure()) {
            boardController.triggerCesure(targetPlayer, onJuryFinished);
        } else {
            notifyFinished();
        }
    }

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

    public void setBoardController(BoardController bc) {
        this.boardController = bc;
    }

    public JuryView getJuryView() {
        return juryView;
    }
}