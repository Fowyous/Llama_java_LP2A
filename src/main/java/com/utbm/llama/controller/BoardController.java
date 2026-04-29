package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.model.*;
import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;
import main.java.com.utbm.llama.view.*;

import javax.swing.*;
import java.util.List;
import java.util.Locale;

/**
 * Game board controller.
 * This is the central controller: he orchestrates the entire process.
 * of a game, round by round, turn by turn.
 * Responsibilities:
 * ┌─ Actions du joueur local ───────────────────────────────────────────────┐
 * │                    - Play a card (PLAY_CARD)                            │
 * │                    - Draw a card (DRAW_CARD)                            │
 * │                    - Pass the round (QUIT_ROUND)                        │
 * └─────────────────────────────────────────────────────────────────────────┘
 * ┌─ Tour des bots ─────────────────────────────────────────────────────────┐
 * │                - Trigger the bot move with a visual delay               │
 * └─────────────────────────────────────────────────────────────────────────┘
 * ┌─ Fin de manche / post-manche ───────────────────────────────────────────┐
 * │            - Deduct credits (value of remaining cards)                  │
 * │            - Distribute +35 credits at the beginning of the next round  │
 * │            - Check: loss ≥ 20 → Jury                                    │
 * │            - Check: credits > 0 after jury → Gap                        │
 * │            - Check: empty hand → Semester abroad (4 cards)              │
 * │            - Check: DETEC bonus (round 4, long mode, ≥ 120 credits)     │
 * │            - Check: end of game (number of innings reached)             │
 * └─────────────────────────────────────────────────────────────────────────┘
 */
public class BoardController {

    private final MainFrame mainFrame;
    private Game game;
    private Player localPlayer;
    private BoardView boardView;
    private JuryController juryController;
    private RuleEngine ruleEngine;

    private boolean roundInProgress = false;
    private int currentRoundNumber = 1;

    private static final int BOT_DELAY_MS = 1200;

    private final Locale locale;

    /**
     * Initializes the central game controller, links the rule engine, and establishes the connection with the Jury controller for post-round events.
     */
    public BoardController(MainFrame mainFrame, Game game, Player localPlayer, JuryController juryController, Locale locale) {
        this.locale = locale;

        this.mainFrame = mainFrame;
        this.game = game;
        this.localPlayer = localPlayer;
        this.juryController = juryController;
        this.ruleEngine = new RuleEngine();
        if (juryController != null) {
            juryController.setBoardController(this);
        }
    }

    /**
     * Initializes and displays the board, deals the first cards,
     * then starts the first round.
     */
    public void initBoard() {
        boardView = new BoardView(locale);
        bindBoardListeners();

        mainFrame.showGame(boardView);
        startRound();
    }

    /**
     * Connects the draw and quit buttons in the board view to their respective logic handlers for the local player.
     */
    private void bindBoardListeners() {

        boardView.addDrawListener(e -> {
            if (isLocalPlayerTurn()) handleDrawCard();
        });

        boardView.addQuitRoundListener(e -> {
            if (isLocalPlayerTurn()) handleQuitRound();
        });
    }

    /**
     * Attaches a callback to the local player's hand view to process playing a card when it is their turn.
     */
    private void bindCardPlayedListener() {
        PlayerViewRef lpv = safeGetLocalPlayerView();
        if (lpv == null) return;

        lpv.handView().setOnCardPlayed(card -> {
            if (isLocalPlayerTurn()) handlePlayCard(card);
        });
    }

    /**
     * The player plays a card from their hand on the discard pile.
     * Rule: the played card must be ≥ on the top card of the discard pile,
     * or be a LLAMA if the top is a SIX, or the top is empty.
     */
    public void handlePlayCard(CardType card) {
        Move move = Move.playCard(localPlayer, card);

        if (!ruleEngine.validateMove(move, game)) {
            System.out.println("[BOARD] Coup invalide : " + card.name());
            updateView();
            return;
        }
        game.applyMove(move);

        ruleEngine.applyRules(move, game);
        System.out.println("[BOARD] " + localPlayer.getName() + " joue " + card.name());

        updateView();
        checkRoundOver();

        if (roundInProgress) {
            game.nextTurn();
            updateView();
            checkBotTurn();
        }
  /*		// a effacer j'ai laisser ca pour alban
        if (!validatePlayCard(card)) {// Il faut utiliser le rule engine
            System.out.println("[BOARD] Coup invalide : " + card.name());
            updateView();
            return;
        }

        localPlayer.removeCard(card);//fait par game.applyMove(move)
        game.getDiscardPile().add(card);//fait par game.applyMove(move)

        System.out.println("[BOARD] " + localPlayer.getName() + " joue " + card.name());

        updateView();
        checkRoundOver();

        if (roundInProgress) {
            game.nextTurn();
            updateView();
            checkBotTurn();
        }
        */
    }

    /**
     * The player draws a card.
     */
    public void handleDrawCard() {
        Move move = Move.drawCard(localPlayer);

        if (!ruleEngine.validateMove(move, game)) {
            System.out.println("[BOARD] Coup invalide : impossible de piocher");
            updateView();
            return;
        }

        game.applyMove(move);
        ruleEngine.applyRules(move, game);

        System.out.println("[BOARD] " + localPlayer.getName() + " pioche");

        game.applyMove(move);

        ruleEngine.applyRules(move, game);

        updateView();

        game.nextTurn();
        updateView();
        checkBotTurn();
    }

    /**
     * The player passes the sleeve (no longer plays until the end).
     */
    public void handleQuitRound() {
        Move move = Move.quitRound(localPlayer);

        if (!ruleEngine.validateMove(move, game)) {
            System.out.println("[BOARD] Coup invalide : impossible de passer la manche");
            updateView();
            return;
        }

        game.applyMove(move);
        //localPlayer.changeState(State.QUITTING);
        //System.out.println("[BOARD] " + localPlayer.getName() + " passe la manche");

        updateView();
        checkRoundOver();

        if (roundInProgress) {
            game.nextTurn();
            updateView();
            checkBotTurn();
        }
    }

    /**
     * If the current player is a bot, triggers his turn after a visual delay.
     */
    private void checkBotTurn() {
        Player current = game.getCurrentPlayer();

        if (!(current instanceof Bot) || current.getState() == State.QUITTING) {
            if (boardView != null && boardView.getLocalPlayerView() != null) {
                boardView.getLocalPlayerView().getHandView()
                        .setInteractive(isLocalPlayerTurn());
            }
            updateView();
            return;
        }

        if (boardView != null && boardView.getLocalPlayerView() != null) {
            boardView.getLocalPlayerView().getHandView().setInteractive(false);
        }

        Timer botTimer = new Timer(BOT_DELAY_MS, e -> {
            ((Timer) e.getSource()).stop();
            executeBotTurn((Bot) current);
        });
        botTimer.setRepeats(false);
        botTimer.start();
    }

    /**
     * Execute the move decided by the bot.
     */
    private void executeBotTurn(Bot bot) {
        if (!roundInProgress) return;

        Move move = bot.decideMove(game);

        if (!ruleEngine.validateMove(move, game)) {
            System.out.println("[BOARD] Coup du bot invalide : " + bot.getName());
            return;
        }

        game.applyMove(move);

        updateView();
        checkRoundOver();

        if (roundInProgress) {
            game.nextTurn();
            updateView();
            checkBotTurn();
        }
    }

    /**
     * Returns true if the human player is currently active, has not quit the round, and the round is still in progress.
     */
    private boolean isLocalPlayerTurn() {
        return game.getCurrentPlayer().equals(localPlayer) && localPlayer.getState() == State.PLAYING && roundInProgress;
    }

    /**
     * Check if the round is finished.
     * The round ends when all active players have passed
     * or when a player has emptied his hand.
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
     * Endgame sequence for each active player:
     * 1. Deduct the value of the remaining cards in hand
     * 2. Check loss ≥ 20 → Jury
     * 3. Checks credits > 0 → Gap
     * 4. Checks empty hand → Semester abroad
     * 5. Checks DETEC bonus (round 4, LONG, ≥ 120 credits)
     * 6. Prepare for the next round or finish the game
     */
    private void endRound() {
        roundInProgress = false;
        System.out.println("[BOARD] Fin de manche " + game.getCurrentRoundNumber());

        java.util.Map<Player, Integer> creditsBefore = new java.util.HashMap<>();
        for (Player p : game.getPlayers()) {
            creditsBefore.put(p, p.getCredits());
        }

        List<Round.JuryCandidate> candidates = game.endCurrentRound();

        java.util.Map<Player, Integer> creditsLostMap = new java.util.HashMap<>();
        for (Player p : game.getPlayers()) {
            int before = creditsBefore.get(p);
            int after = p.getCredits();
            creditsLostMap.put(p, Math.max(0, before - after));
        }

        processPostRoundCascade(
                new java.util.ArrayList<>(candidates),
                0,
                () -> {
                    boolean detecApplied = checkDetecApplied();
                    game.endRoundPostJury();

                    java.util.Map<Player, Integer> creditsGainedMap = new java.util.HashMap<>();
                    for (Player p : game.getPlayers()) {
                        int currentCredits = p.getCredits();
                        int afterPenalty = creditsBefore.get(p) - creditsLostMap.get(p);
                        creditsGainedMap.put(p, Math.max(0, currentCredits - afterPenalty));
                    }

                    showRoundSummary(creditsLostMap, creditsGainedMap, detecApplied);
                }
        );
    }

    /**
     * Cascade the post-round for each player.
     * The cascade is necessary because jury and caesura are asynchronous (view + interaction).
     */
    private void processPostRoundCascade(
            List<Round.JuryCandidate> candidates,
            int index,
            Runnable onAllDone) {

        if (index >= candidates.size()) {
            onAllDone.run();
            return;
        }

        Round.JuryCandidate candidate = candidates.get(index);
        Runnable next = () -> processPostRoundCascade(candidates, index + 1, onAllDone);

        juryController.startJury(candidate.player(), candidate.creditsLost(), next);
    }

    /**
     * Triggers a player’s gap screen.
     * Called from JuryController or directly if credits > 0.
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
     * Prepare and throw the next round.
     * First check if the game is over.
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
     * Initializes a round:
     * - +35 credits for all (including players on a break)
     * - Restores player status to PLAYING
     * - Lifts the suspension of players on a hiatus
     * - Deals the cards (6 by default, 4 if semester abroad)
     * - Mix the pickaxe
     */
    private void startRound() {
        roundInProgress = true;

        SwingUtilities.invokeLater(() -> {
            mainFrame.showGame(boardView);

            boardView.updateBoard(game, localPlayer);

            PlayerView lpv = boardView.getLocalPlayerView();
            if (lpv != null) {
                lpv.getHandView().setOnCardPlayed(card -> {
                    if (isLocalPlayerTurn()) handlePlayCard(card);
                });
                lpv.getHandView().setInteractive(isLocalPlayerTurn());
            }

            checkBotTurn();
        });
    }

    /**
     * Applies the DETEC bonus: +30 credits for players
     * having ≥ 120 credits at the end of round 4 (LONG mode).
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
     * Determines the winner and displays the end screen.
     * Winner = player with the most credits.
     * The thresholds 180/300 are honorary.
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
     * Updates the board view from the template.
     */
    private void updateView() {
        if (boardView == null) return;

        boardView.updateBoard(game, localPlayer);

        PlayerView lpv = boardView.getLocalPlayerView();
        if (lpv != null) {
            lpv.getHandView().setOnCardPlayed(card -> {
                if (isLocalPlayerTurn()) handlePlayCard(card);
            });
            lpv.getHandView().setInteractive(isLocalPlayerTurn());
        }
    }

    /**
     * Returns true if the current game state satisfies the conditions for the DETEC round bonus.
     */
    private boolean checkDetecApplied() {
        return game.getGameMode() == GameMode.LONG
                && game.getCurrentRoundNumber() == GameMode.DETEC_ROUND;
    }

    /**
     * Displays the detailed results screen showing credit gains and losses for all players after a round is finalized.
     */
    private void showRoundSummary(
            java.util.Map<Player, Integer> creditsLostMap,
            java.util.Map<Player, Integer> creditsGainedMap,
            boolean detecApplied) {

        SwingUtilities.invokeLater(() -> {
            int maxRounds = game.getGameMode().getMaxRounds();

            RoundSummaryView summaryView = new RoundSummaryView();
            summaryView.setup(
                    game.getPlayers(),
                    game.getCurrentRoundNumber(),
                    maxRounds,
                    game.getGameMode(),
                    creditsLostMap,
                    creditsGainedMap,
                    detecApplied
            );

            summaryView.addNextRoundListener(e -> {
                SwingUtilities.invokeLater(this::prepareNextRound);
            });

            mainFrame.showRoundSummary(summaryView);
        });
    }

    /**
     * Enables or disables local player action buttons.
     */
    private void setLocalActionsEnabled(boolean enabled) {
        updateView();
    }

    /**
     * Secure access to the local PlayerView.
     */
    private PlayerViewRef safeGetLocalPlayerView() {
        if (boardView == null || boardView.getLocalPlayerView() == null) return null;
        return new PlayerViewRef(boardView.getLocalPlayerView().getHandView());
    }

    private record PlayerViewRef(HandView handView) {

    }

    /**
     * Updates the game model reference held by the controller.
     */
    public void setGame(Game game) {
        this.game = game;
    }

    /**
     * Sets which player is considered the "local" human user for this controller.
     */
    public void setLocalPlayer(Player p) {
        this.localPlayer = p;
    }

    /**
     * Retrieves the current BoardView instance managed by this controller.
     */
    public BoardView getBoardView() {
        return boardView;
    }
}