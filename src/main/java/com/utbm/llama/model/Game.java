package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Central model of the LAMA UTBM game.
 * Game is the single point of entry to access the full state of a game.
 * He coordinates the interactions between:
 * - The players (list + current player)
 * - The pickaxe and the discard
 * - The current round
 * - The credit register (CreditLedger)
 * - The game mode (SHORT or LONG)
 * What Game does:
 * - Start the game
 * - Go to the next round (nextTurn)
 * - Apply a move (applyMove) via validation
 * - Start/end a round
 * - Check if the game is finished
 * - Identify the winner
 * What Game DOESN’T do (delegated to Round):
 * - The detailed logic for the beginning/end of the round
 * - The triggering of the jury (asynchronous, managed by the Controller)
 * - The display
 */
public class Game {

    private final List<Player> players;
    private final GameMode gameMode;
    private final CreditLedger ledger;

    private final Deck drawPile;
    private final Deck discardPile;

    private Round currentRound;
    private int currentRoundNumber = 0;
    private int currentPlayerIndex = 0;

    private boolean gameOver = false;

    /**
     * Crée une nouvelle partie.
     *
     * @param players  list of players (index 0 = humain local)
     * @param gameMode SHORT (6 manches) ou LONG (10 manches)
     */
    public Game(List<Player> players, GameMode gameMode) {
        if (players == null || players.size() < 2)
            throw new IllegalArgumentException("Il faut au minimum 2 joueurs");
        if (gameMode == null)
            throw new IllegalArgumentException("Le mode de jeu ne peut pas être null");

        this.players = new ArrayList<>(players);
        this.gameMode = gameMode;
        this.ledger = new CreditLedger();
        this.drawPile = Deck.createFull();
        this.discardPile = Deck.empty();
    }

    /**
     * Start the game: initializes the first round.
     */
    public void start() {
        System.out.println("=== DÉBUT DE PARTIE | " + gameMode
                + " | " + players.size() + " joueurs ===");
        beginNextRound();
    }

    /**
     * Prepare and start the next round.
     * First check if the game is over.
     *
     * @return true if a new round has been started,
     * false if the game is over
     */
    public boolean beginNextRound() {
        if (currentRoundNumber >= gameMode.getMaxRounds()) {
            gameOver = true;
            return false;
        }

        currentRoundNumber++;
        currentPlayerIndex = 0;

        rebuildDrawPile();

        discardPile.clear();

        currentRound = new Round(currentRoundNumber, players, gameMode, ledger);
        currentRound.startRound(drawPile);

        CardType firstCard = drawPile.draw();
        if (firstCard != null) {
            discardPile.add(firstCard);
            System.out.println("[GAME] Carte initiale défausse : " + firstCard);
        }

        System.out.println("=== MANCHE " + currentRoundNumber
                + " / " + gameMode.getMaxRounds() + " démarrée ===");

        return true;
    }

    /**
     * Recreate a mixed complete pickaxe.
     * Called when the draw pile is too small to deal the cards.
     */
    private void rebuildDrawPile() {

        Deck fresh = Deck.createFull();

        drawPile.clear();
        for (CardType c : fresh.getCards()) {
            drawPile.add(c);
        }

        System.out.println("[GAME] Pioche reconstruite : " + drawPile.size() + " cartes");
    }

    /**
     * Passes to the next player.
     * Automatically jumps players into the QUITTING or SUSPENDED state.
     */
    public void nextTurn() {
        int attempts = 0;
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            attempts++;
            if (attempts > players.size()) break;
        } while (shouldSkipCurrentPlayer());
    }

    /**
     * @return true if the running player needs to be jumped.
     */
    private boolean shouldSkipCurrentPlayer() {
        Player current = getCurrentPlayer();
        return current.getState() == State.QUITTING
                || current.isSuspended();
    }

    /**
     * applies the movement by making the necessary changes to the game and the players.
     * No validation occurs here, the validation is carried out by the rule engine
     *
     * @param move the movement so that it is applied
     * @throws IllegalArgumentException if the blow is malformed
     */

    public void applyMove(Move move) {
        if (move == null) throw new IllegalArgumentException("Move null impossible");

        Player player = move.getPlayer();

        switch (move.getType()) {
            case PLAY_CARD -> {
                player.removeCard(move.getCard());
                discardPile.add(move.getCard());
                System.out.println("[GAME] " + move);
            }
            case DRAW_CARD -> {
                if (!drawPile.isEmpty()) {
                    CardType drawn = drawPile.draw();
                    player.addCard(drawn);
                    System.out.println("[GAME] " + move + " → pioche " + drawn);
                } else {
                    System.out.println("[GAME] ⚠ Pioche vide — impossible de piocher");
                }
            }
            case QUIT_ROUND -> {
                player.changeState(State.QUITTING);
                System.out.println("[GAME] " + move);
            }
        }
    }

    /**
     * Execute the end-of-inning sequence (round phase 3).
     * Return the list of players to be judged by the jury.
     * The Controller is responsible for orchestrating juries (asynchronous)
     * then call checkStudyAbroad(), checkDetecBonus() and beginNextRound().
     * MANDATORY order respected:
     * 1. Deducts the penalties from hand
     * 2. Return the candidates to the jury
     * (3. Jury → managed by JuryController)
     * (4. Gap → managed by BoardController)
     * 5. Semester abroad → called by endRoundPostJury()
     * 6. DETEC bonus → called by endRoundPostJury()
     *
     * @return list of jury candidates (can be empty)
     */
    public List<Round.JuryCandidate> endCurrentRound() {
        currentRound.markEnded();
        return currentRound.deductHandPenalties();
    }

    /**
     * Finalize the end of the round after all juries have been resolved.
     * Check the semester abroad and the DETEC bonus.
     * Must be called by BoardController once all juries have been completed.
     */
    public void endRoundPostJury() {
        currentRound.checkStudyAbroad();
        currentRound.checkDetecBonus();
    }

    /**
     * @return true if the game is over (all rounds played)
     */
    public boolean isOver() {
        return gameOver || currentRoundNumber >= gameMode.getMaxRounds();
    }

    /**
     * Determines the winner: the player with the most credits.
     * In case of a tie, returns the first one from the list.
     *
     * @return the winning player
     */
    public Player getWinner() {
        return players.stream()
                .max((a, b) -> Integer.compare(a.getCredits(), b.getCredits()))
                .orElse(players.get(0));
    }

    /**
     * @return true if the player reached the honorary threshold (diploma)
     */
    public boolean isGraduated(Player player) {
        return player.getCredits() >= gameMode.getGraduationThreshold();
    }

    /**
     * @return the honorary credit threshold (180 or 300)
     */
    public int getGraduationThreshold() {
        return gameMode.getGraduationThreshold();
    }

    /**
     * Displays a full game summary in the console.
     */
    public void printSummary() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         RÉSULTATS DE LA PARTIE        ║");
        System.out.println("╠══════════════════════════════════════╣");

        Player winner = getWinner();
        for (Player p : players) {
            boolean graduated = isGraduated(p);
            boolean isWinner = p.equals(winner);
            System.out.printf("║  %-15s %5d crédits  %s%s%n",
                    p.getName(),
                    p.getCredits(),
                    graduated ? "★ DIPLÔMÉ " : "          ",
                    isWinner ? "🏆" : "  ");
        }

        System.out.println("╠══════════════════════════════════════╣");
        System.out.println("║  Vainqueur : " + winner.getName());
        System.out.println("╚══════════════════════════════════════╝\n");

        ledger.printFull();
    }

    /**
     * Retrieve the list of all players participating in the game.
     *
     * @return the list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Get the current game mode (SHORT or LONG).
     *
     * @return the selected game mode
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * Get the ledger responsible for tracking player credits.
     *
     * @return the credit ledger instance
     */
    public CreditLedger getLedger() {
        return ledger;
    }

    /**
     * Get the deck representing the draw pile.
     *
     * @return the draw pile deck
     */
    public Deck getDrawPile() {
        return drawPile;
    }

    /**
     * Get the deck representing the discard pile.
     *
     * @return the discard pile deck
     */
    public Deck getDiscardPile() {
        return discardPile;
    }

    /**
     * Retrieve the current round object.
     *
     * @return the active round, or {@code null} if no round is in progress
     */
    public Round getCurrentRound() {
        return currentRound;
    }

    /**
     * Get the sequence number of the current round.
     *
     * @return the round number (starting from 1)
     */
    public int getCurrentRoundNumber() {
        return currentRoundNumber;
    }

    /**
     * Get the player whose turn it is currently.
     *
     * @return the active player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Get the index of the current player in the players list.
     *
     * @return the current player index
     */
    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    /**
     * Check if the entire game session has ended.
     *
     * @return {@code true} if the game is over, {@code false} otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }
}