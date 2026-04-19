package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Modèle central du jeu LAMA UTBM.
 * Game est le point d'entrée unique pour accéder à l'état complet d'une partie.
 * Il coordonne les interactions entre :
 * - Les joueurs (liste + joueur courant)
 * - La pioche et la défausse
 * - La manche en cours (Round)
 * - Le registre de crédits (CreditLedger)
 * - Le mode de jeu (SHORT ou LONG)
 * Ce que Game fait :
 * - Démarrer la partie (start)
 * - Passer au tour suivant (nextTurn)
 * - Appliquer un coup (applyMove) via validation
 * - Démarrer / terminer une manche
 * - Vérifier si la partie est terminée
 * - Identifier le gagnant
 * Ce que Game ne fait PAS (délégué à Round) :
 * - La logique détaillée de début/fin de manche
 * - Le déclenchement du jury (asynchrone, géré par le Controller)
 * - L'affichage
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
     * @param players  liste des joueurs (index 0 = humain local)
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
     * Démarre la partie : initialise la première manche.
     */
    public void start() {
        System.out.println("=== DÉBUT DE PARTIE | " + gameMode
                + " | " + players.size() + " joueurs ===");
        beginNextRound();
    }

    /**
     * Prépare et démarre la manche suivante.
     * Vérifie d'abord si la partie est terminée.
     *
     * @return true si une nouvelle manche a été démarrée,
     * false si la partie est terminée
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
     * Recrée une pioche complète mélangée.
     * Appelé quand la pioche est trop petite pour distribuer les cartes.
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
     * Passe au joueur suivant.
     * Saute automatiquement les joueurs en état QUITTING ou SUSPENDED.
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
     * @return true si le joueur courant doit être sauté.
     */
    private boolean shouldSkipCurrentPlayer() {
        Player current = getCurrentPlayer();
        return current.getState() == State.QUITTING
                || current.isSuspended();
    }

    /**
     * Applique un coup au jeu SANS validation (la validation est faite
     * par RuleEngine dans le Controller).
     *
     * @param move le coup à appliquer
     * @throws IllegalArgumentException si le coup est mal formé
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
     * Exécute la séquence de fin de manche (phase 3 de Round).
     * Retourne la liste des joueurs à passer devant le jury.
     * Le Controller est responsable d'orchestrer les jurys (asynchrone)
     * puis d'appeler checkStudyAbroad(), checkDetecBonus() et beginNextRound().
     * Ordre OBLIGATOIRE respecté :
     * 1. Déduit les pénalités de main
     * 2. Retourne les candidats au jury
     * (3. Jury → géré par JuryController)
     * (4. Césure → géré par BoardController)
     * 5. Semestre à l'étranger  → appelé par endRoundPostJury()
     * 6. Bonus DETEC            → appelé par endRoundPostJury()
     *
     * @return liste des candidats au jury (peut être vide)
     */
    public List<Round.JuryCandidate> endCurrentRound() {
        currentRound.markEnded();
        return currentRound.deductHandPenalties();
    }

    /**
     * Finalise la fin de manche après résolution de tous les jurys.
     * Vérifie le semestre à l'étranger et le bonus DETEC.
     * Doit être appelé par BoardController une fois tous les jurys terminés.
     */
    public void endRoundPostJury() {
        currentRound.checkStudyAbroad();
        currentRound.checkDetecBonus();
    }

    /**
     * @return true si la partie est terminée (toutes les manches jouées)
     */
    public boolean isOver() {
        return gameOver || currentRoundNumber >= gameMode.getMaxRounds();
    }

    /**
     * Détermine le vainqueur : le joueur avec le plus de crédits.
     * En cas d'égalité, retourne le premier de la liste.
     *
     * @return le joueur vainqueur
     */
    public Player getWinner() {
        return players.stream()
                .max((a, b) -> Integer.compare(a.getCredits(), b.getCredits()))
                .orElse(players.get(0));
    }

    /**
     * @return true si le joueur a atteint le seuil honorifique (diplôme)
     */
    public boolean isGraduated(Player player) {
        return player.getCredits() >= gameMode.getGraduationThreshold();
    }

    /**
     * @return le seuil de crédits honorifique (180 ou 300)
     */
    public int getGraduationThreshold() {
        return gameMode.getGraduationThreshold();
    }

    /**
     * Affiche un résumé complet de la partie dans la console.
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

    public List<Player> getPlayers() {
        return players;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public CreditLedger getLedger() {
        return ledger;
    }

    public Deck getDrawPile() {
        return drawPile;
    }

    public Deck getDiscardPile() {
        return discardPile;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public int getCurrentRoundNumber() {
        return currentRoundNumber;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}