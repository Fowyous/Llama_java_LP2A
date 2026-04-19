package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.model.*;
import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;
import main.java.com.utbm.llama.view.BoardView;
import main.java.com.utbm.llama.view.CesureView;
import main.java.com.utbm.llama.view.HandView;
import main.java.com.utbm.llama.view.MainFrame;

import javax.swing.*;
import java.util.List;

/**
 * Contrôleur du plateau de jeu.
 * C'est le contrôleur central : il orchestre le déroulement complet
 * d'une partie, manche par manche, tour par tour.
 * Responsabilités :
 * ┌─ Actions du joueur local ───────────────────────────────────────────────┐
 * │  - Jouer une carte (PLAY_CARD)                                          │
 * │  - Piocher une carte (DRAW_CARD)                                        │
 * │  - Passer la manche (QUIT_ROUND)                                        │
 * └─────────────────────────────────────────────────────────────────────────┘
 * ┌─ Tour des bots ─────────────────────────────────────────────────────────┐
 * │  - Déclencher le coup du bot avec un délai visuel                       │
 * └─────────────────────────────────────────────────────────────────────────┘
 * ┌─ Fin de manche / post-manche ───────────────────────────────────────────┐
 * │  - Déduire les crédits (valeur des cartes restantes)                    │
 * │  - Distribuer +35 crédits en début de manche suivante                   │
 * │  - Vérifier : perte ≥ 20 → Jury                                         │
 * │  - Vérifier : crédits < 0 après jury → Césure                           │
 * │  - Vérifier : main vide → Semestre à l'étranger (4 cartes)              │
 * │  - Vérifier : bonus DETEC (manche 4, mode LONG, ≥ 120 crédits)          │
 * │  - Vérifier : fin de partie (nb manches atteint)                        │
 * └─────────────────────────────────────────────────────────────────────────┘
 */
public class BoardController {

    private final MainFrame mainFrame;
    private Game game;
    private Player localPlayer;
    private BoardView boardView;
    private JuryController juryController;

    private boolean roundInProgress = false;
    private int currentRoundNumber = 1;

    private static final int BOT_DELAY_MS = 1200;

    public BoardController(MainFrame mainFrame, Game game, Player localPlayer, JuryController juryController) {
        this.mainFrame = mainFrame;
        this.game = game;
        this.localPlayer = localPlayer;
        this.juryController = juryController;

        if (juryController != null) {
            juryController.setBoardController(this);
        }
    }

    /**
     * Initialise et affiche le plateau, distribue les premières cartes,
     * puis démarre le premier tour.
     */
    public void initBoard() {
        boardView = new BoardView();
        bindBoardListeners();

        mainFrame.showGame(boardView);
        startRound();
    }

    private void bindBoardListeners() {

        boardView.addDrawListener(e -> {
            if (isLocalPlayerTurn()) handleDrawCard();
        });

        boardView.addQuitRoundListener(e -> {
            if (isLocalPlayerTurn()) handleQuitRound();
        });
    }

    private void bindCardPlayedListener() {
        PlayerViewRef lpv = safeGetLocalPlayerView();
        if (lpv == null) return;

        lpv.handView().setOnCardPlayed(card -> {
            if (isLocalPlayerTurn()) handlePlayCard(card);
        });
    }

    /**
     * Le joueur joue une carte de sa main sur la défausse.
     * Règle : la carte jouée doit être ≥ à la carte du dessus de la défausse,
     * ou être un LLAMA si le dessus est un SIX, ou le dessus est vide.
     */
    public void handlePlayCard(CardType card) {
        if (!validatePlayCard(card)) {
            System.out.println("[BOARD] Coup invalide : " + card.name());
            updateView();
            return;
        }

        localPlayer.removeCard(card);
        game.getDiscardPile().add(card);

        System.out.println("[BOARD] " + localPlayer.getName() + " joue " + card.name());

        updateView();
        checkRoundOver();

        if (roundInProgress) {
            game.nextTurn();
            updateView();
            checkBotTurn();
        }
    }

    /**
     * Le joueur pioche une carte.
     */
    public void handleDrawCard() {
        if (game.getDrawPile().isEmpty()) {
            System.out.println("[BOARD] Pioche vide !");
            return;
        }

        CardType drawn = game.getDrawPile().draw();
        localPlayer.addCard(drawn);
        System.out.println("[BOARD] " + localPlayer.getName() + " pioche " + drawn.name());

        updateView();

        game.nextTurn();
        updateView();
        checkBotTurn();
    }

    /**
     * Le joueur passe la manche (ne joue plus jusqu'à la fin).
     */
    public void handleQuitRound() {
        localPlayer.changeState(State.QUITTING);
        System.out.println("[BOARD] " + localPlayer.getName() + " passe la manche");

        updateView();
        checkRoundOver();

        if (roundInProgress) {
            game.nextTurn();
            updateView();
            checkBotTurn();
        }
    }

    /**
     * Si le joueur actuel est un bot, déclenche son tour après un délai visuel.
     */
    private void checkBotTurn() {
        Player current = game.getCurrentPlayer();
        if (!(current instanceof Bot) || current.getState() == State.QUITTING) {
            bindCardPlayedListener();
            return;
        }

        setLocalActionsEnabled(false);

        Timer botTimer = new Timer(BOT_DELAY_MS, e -> {
            ((Timer) e.getSource()).stop();
            executeBotTurn((Bot) current);
        });
        botTimer.setRepeats(false);
        botTimer.start();
    }

    /**
     * Exécute le coup décidé par le bot.
     */
    private void executeBotTurn(Bot bot) {
        if (!roundInProgress) return;

        Move move = bot.decideMove(game);

        switch (move.getType()) {
            case PLAY_CARD -> {
                bot.removeCard(move.getCard());
                game.getDiscardPile().add(move.getCard());
                System.out.println("[BOARD] " + bot.getName() + " joue " + move.getCard().name());
            }
            case DRAW_CARD -> {
                if (!game.getDrawPile().isEmpty()) {
                    CardType drawn = game.getDrawPile().draw();
                    bot.addCard(drawn);
                    System.out.println("[BOARD] " + bot.getName() + " pioche");
                }
            }
            case QUIT_ROUND -> {
                bot.changeState(State.QUITTING);
                System.out.println("[BOARD] " + bot.getName() + " passe la manche");
            }
        }

        updateView();
        checkRoundOver();

        if (roundInProgress) {
            game.nextTurn();
            updateView();
            checkBotTurn();
        }
    }

    /**
     * Vérifie si une carte peut être jouée sur la défausse actuelle.
     * Règles L.A.M.A :
     * - Défausse vide → toute carte est jouable
     * - Même valeur que le dessus → jouable
     * - Valeur supérieure de 1 → jouable
     * - SIX en haut → LLAMA jouable
     * - LLAMA en haut → ONE jouable (cycle)
     */
    private boolean validatePlayCard(CardType card) {
        if (game.getDiscardPile().isEmpty()) return true;

        CardType top = game.getDiscardPile().peek();

        if (card == top) return true;
        if (card.getValue() == top.getValue() + 1) return true;
        if (top == CardType.SIX && card == CardType.LLAMA) return true;
        if (top == CardType.LLAMA && card == CardType.ONE) return true;

        return false;
    }

    private boolean isLocalPlayerTurn() {
        return game.getCurrentPlayer().equals(localPlayer) && localPlayer.getState() == State.PLAYING && roundInProgress;
    }

    /**
     * Vérifie si la manche est terminée.
     * La manche se termine quand tous les joueurs actifs ont passé
     * ou quand un joueur a vidé sa main.
     */
    private void checkRoundOver() {
        boolean allQuit = game.getPlayers().stream().filter(p -> !p.isSuspended()).allMatch(p -> p.getState() == State.QUITTING || p.getHand().isEmpty());

        boolean anyEmptyHand = game.getPlayers().stream().anyMatch(p -> p.getHand().isEmpty() && !p.isSuspended());

        if (allQuit || anyEmptyHand) {
            roundInProgress = false;
            System.out.println("[BOARD] Fin de manche " + currentRoundNumber);
            endRound();
        }
    }

    /**
     * Séquence de fin de manche pour chaque joueur actif :
     * 1. Déduit la valeur des cartes restantes en main
     * 2. Vérifie perte ≥ 20 → Jury
     * 3. Vérifie crédits < 0 → Césure
     * 4. Vérifie main vide → Semestre à l'étranger
     * 5. Vérifie bonus DETEC (manche 4, LONG, ≥ 120 crédits)
     * 6. Prépare la manche suivante ou termine la partie
     */
    private void endRound() {
        List<Player> activePlayers = game.getPlayers().stream().filter(p -> !p.isSuspended()).toList();

        for (Player p : activePlayers) {
            int handValue = p.getHand().stream().mapToInt(CardType::getValue).sum();

            int creditsBefore = p.getCredits();
            p.addCredits(-handValue);
            int lost = creditsBefore - p.getCredits();
            p.setCreditsLostThisRound(Math.max(0, lost));

            System.out.println("[ROUND END] " + p.getName() + " | main = " + handValue + " | crédits avant = " + creditsBefore + " | crédits après = " + p.getCredits());

            if (p.getHand().isEmpty()) {
                p.setStudyAbroad(true);
                System.out.println("[ROUND END] " + p.getName() + " → Semestre à l'étranger !");
            }
        }
        processPostRoundCascade(activePlayers, 0, this::prepareNextRound);
    }

    /**
     * Traite en cascade le post-manche pour chaque joueur.
     * La cascade est nécessaire car jury et césure sont asynchrones (vue + interaction).
     */
    private void processPostRoundCascade(List<Player> players, int index, Runnable onAllDone) {
        if (index >= players.size()) {
            onAllDone.run();
            return;
        }

        Player p = players.get(index);
        Runnable next = () -> processPostRoundCascade(players, index + 1, onAllDone);

        if (p.getCreditsLostThisRound() >= 20) {
            juryController.startJury(p, p.getCreditsLostThisRound(), next);
        } else {
            if (p.getCredits() < 0) {
                triggerCesure(p, next);
            } else {
                next.run();
            }
        }
    }

    /**
     * Déclenche l'écran de césure pour un joueur.
     * Appelé depuis JuryController ou directement si crédits < 0.
     */
    public void triggerCesure(Player player, Runnable onDone) {
        player.setSuspended(true);

        boolean allSuspended = game.getPlayers().stream().allMatch(Player::isSuspended);

        CesureView cesureView = new CesureView();
        cesureView.setup(player.getName(), player.getCredits(), currentRoundNumber + 1, allSuspended);

        cesureView.addContinueListener(e -> {
            mainFrame.showGame(boardView);
            if (onDone != null) {
                SwingUtilities.invokeLater(onDone::run);
            }
        });

        mainFrame.showCesure(cesureView);
        System.out.println("[CESURE] " + player.getName() + " suspend pour la manche " + (currentRoundNumber + 1));
    }

    /**
     * Prépare et lance la manche suivante.
     * Vérifie d'abord si la partie est terminée.
     */
    private void prepareNextRound() {
        if (game.isOver()) {
            endGame();
            return;
        }
        boolean started = game.beginNextRound();
        if (started) {
            startRound();
            SwingUtilities.invokeLater(() -> {
                if (boardView != null) {
                    mainFrame.showGame(boardView);
                    updateView();
                }
            });
        } else {
            endGame();
        }
    }

    /**
     * Initialise une manche :
     * - +35 crédits pour tous (y compris les joueurs en césure)
     * - Remet les joueurs en état PLAYING
     * - Lève la suspension des joueurs en césure
     * - Distribue les cartes (6 par défaut, 4 si semestre à l'étranger)
     * - Mélange la pioche
     */
    private void startRound() {
        roundInProgress = true;

        /*if (game.getDrawPile().size() < game.getPlayers().size() * 6) {
            game.getDrawPile().shuffle();
        }

        for (Player p : game.getPlayers()) {
            p.addCredits(35);
            p.setCreditsLostThisRound(0);

            if (p.isSuspended()) {
                p.setSuspended(false);
                System.out.println("[ROUND START] " + p.getName() + " reprend après césure (+35 crédits)");
                continue;
            }

            p.changeState(State.PLAYING);

            while (!p.getHand().isEmpty()) p.removeCard(p.getHand().get(0));

            int handSize = p.hasStudyAbroad() ? 4 : 6;
            for (int i = 0; i < handSize; i++) {
                CardType drawn = game.getDrawPile().draw();
                if (drawn != null) p.addCard(drawn);
            }

            p.setStudyAbroad(false);

            System.out.println("[ROUND START] " + p.getName() + " | " + p.getHand().size() + " cartes | " + p.getCredits() + " crédits");
        }*/

        updateView();
        bindCardPlayedListener();
        checkBotTurn();
    }

    /**
     * Applique le bonus DETEC : +30 crédits pour les joueurs
     * ayant ≥ 120 crédits à la fin de la manche 4 (mode LONG).
     */
    private void applyDetecBonus() {
        for (Player p : game.getPlayers()) {
            if (p.getCredits() >= 120) {
                p.addCredits(30);
                System.out.println("[DETEC] " + p.getName() + " valide le DETEC ! +30 crédits → " + p.getCredits());
            }
        }
    }

    /**
     * Détermine le vainqueur et affiche l'écran de fin.
     * Gagnant = joueur avec le plus de crédits.
     * Les seuils 180/300 sont honorifiques.
     */
    private void endGame() {
        Player winner = game.getPlayers().stream().max((a, b) -> Integer.compare(a.getCredits(), b.getCredits())).orElse(localPlayer);

        System.out.println("=== FIN DE PARTIE ===");
        for (Player p : game.getPlayers()) {
            boolean graduated = game.isGraduated(p);
            System.out.println(p.getName() + " : " + p.getCredits() + " crédits" + (graduated ? " ★ DIPLÔMÉ" : ""));
        }
        System.out.println("Vainqueur : " + winner.getName());

        javax.swing.SwingUtilities.invokeLater(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("═══ FIN DE PARTIE ═══\n\n");
            for (Player p : game.getPlayers()) {
                sb.append(p.getName()).append(" : ").append(p.getCredits()).append(" crédits");
                if (game.isGraduated(p)) sb.append("  ★ DIPLÔMÉ");
                sb.append("\n");
            }
            sb.append("\n🏆 Vainqueur : ").append(winner.getName());

            javax.swing.JOptionPane.showMessageDialog(mainFrame, sb.toString(), "Résultats", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            mainFrame.showMenu();
        });
    }

    /**
     * Met à jour la vue du plateau depuis le modèle.
     */
    private void updateView() {
        if (boardView != null) {
            boardView.updateBoard(game, localPlayer);
            bindCardPlayedListener();
        }
    }

    /**
     * Active ou désactive les boutons d'action du joueur local.
     */
    private void setLocalActionsEnabled(boolean enabled) {
        updateView();
    }

    /**
     * Accès sûr à la PlayerView locale.
     */
    private PlayerViewRef safeGetLocalPlayerView() {
        if (boardView == null || boardView.getLocalPlayerView() == null) return null;
        return new PlayerViewRef(boardView.getLocalPlayerView().getHandView());
    }

    private record PlayerViewRef(HandView handView) {

    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setLocalPlayer(Player p) {
        this.localPlayer = p;
    }

    public BoardView getBoardView() {
        return boardView;
    }
}