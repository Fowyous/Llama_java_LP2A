package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Model a complete sleeve of the LAMA UTBM game.
 * One round takes place in three phases:
 * ┌─ Phase 1 : Début de manche ─────────────────────────────────────────────────┐
 * │  - Each player receives +35 credits                                         │
 * │  - Players on a break receive the credits BUT do not play                   │
 * │  - Normal players receive 6 cards (or 4 if semester abroad)                 │
 * │  - The Abroad study flag is reset to false after distribution               │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ Phase 2 : Jeu ─────────────────────────────────────────────────────────────┐
 * │  - The players play turn-based                                              │
 * │  - End of round: all active players have passed OR one has emptied his hand │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ Phase 3 : Fin de manche ───────────────────────────────────────────────────┐
 * │                            Mandatory order:                                 │
 * │    1. deductHandPenalties()  → removes the value from the remaining cards   │
 * │    2. checkJury()   → loss ≥ 20 → Jury (managed by JuryController)          │
 * │    3. checkCesure()   → credits > 0 → Gap                                   │
 * │    4. checkStudyAbroad()   → empty hand → 4 cards in the next round         │
 * │    5. checkDetecBonus()   → round 4, LONG, ≥ 120 credits → +30              │
 * └─────────────────────────────────────────────────────────────────────────────┘
 */
public class Round {

    private final int roundNumber;
    private final List<Player> allPlayers;
    private final List<Player> activePlayers;
    private final GameMode gameMode;
    private final CreditLedger ledger;

    private boolean over = false;

    public static final int CREDITS_PER_ROUND = 35;
    public static final int JURY_TRIGGER_THRESHOLD = 20;
    public static final int DEFAULT_HAND_SIZE = 6;
    public static final int STUDY_ABROAD_HAND_SIZE = 4;

    /**
     * @param roundNumber round number (starts at 1)
     * @param allPlayers all the players in the game
     * @param gameMode game mode (for the DETEC bonus)
     * @param ledger accounting record shared with Game
     */
    public Round(int roundNumber, List<Player> allPlayers, GameMode gameMode, CreditLedger ledger) {
        this.roundNumber = roundNumber;
        this.allPlayers = allPlayers;
        this.gameMode = gameMode;
        this.ledger = ledger;
        this.activePlayers = new ArrayList<>();
    }

    /**
     * Initialize the round:
     * - Distributes +35 credits to ALL players (including suspended ones)
     * - Identifies active (not suspended) players
     * - For each active player: put the hand back to PLAYING, empty it, deal the cards
     * - Applies the semester abroad (4 cards) if active
     * - Resets the Abroad study flag to false after distribution
     * - Lifts the suspension of players on a hiatus
     *
     * @param drawPile picks from which to deal the cards
     */
    public void startRound(Deck drawPile) {
        activePlayers.clear();

        for (Player p : allPlayers) {

            ledger.record(roundNumber, p, CreditLedger.Reason.ROUND_START, CREDITS_PER_ROUND);

            if (p.isSuspended()) {
                p.setSuspended(false);
                System.out.println("[ROUND " + roundNumber + "] " + p.getName() + " reprend après césure | " + p.getCredits() + " crédits");
                continue;
            }

            p.resetForNewRound();
            p.clearHand();

            int handSize = p.hasStudyAbroad() ? STUDY_ABROAD_HAND_SIZE : DEFAULT_HAND_SIZE;
            if (p.hasStudyAbroad()) {
                System.out.println("[ROUND " + roundNumber + "] " + p.getName() + " → Semestre à l'étranger : " + handSize + " cartes seulement");
            }

            for (int i = 0; i < handSize; i++) {
                CardType drawn = drawPile.draw();
                if (drawn == null) {
                    System.out.println("[ROUND] ⚠ Pioche vide pendant la distribution pour " + p.getName() + " — arrêt à " + p.getHand().size() + " cartes");
                    break;
                }
                p.addCard(drawn);
            }

            p.setStudyAbroad(false);

            activePlayers.add(p);
            System.out.println("[ROUND " + roundNumber + "] " + p.getName() + " | " + p.getHand().size() + " cartes | " + p.getCredits() + " crédits");
        }
    }


    /**
     * Check if the round is finished.
     * Completion Conditions:
     * - All active players have passed (State.QUITTING)
     * - At least one active player has emptied their hand
     *
     * @return true if the round should end
     */
    public boolean isOver() {
        if (activePlayers.isEmpty()) return true;

        boolean allQuit = activePlayers.stream().allMatch(p -> p.getState() == State.QUITTING || p.getHand().isEmpty());

        boolean anyEmptyHand = activePlayers.stream().anyMatch(p -> p.getHand().isEmpty());

        return allQuit || anyEmptyHand;
    }

    /**
     * Applies the endgame penalty to all active players.
     * Deduct the value of the remaining cards in hand.
     * Must be called FIRST in the last inning sequence.
     *
     * @return map player → lost credits (so that JuryController knows who to summon)
     */
    public List<JuryCandidate> deductHandPenalties() {
        List<JuryCandidate> juryCandidates = new ArrayList<>();

        for (Player p : activePlayers) {
            int handValue = p.computeHandValue();

            if (handValue > 0) {
                int creditsBefore = p.getCredits();
                ledger.record(roundNumber, p, CreditLedger.Reason.HAND_PENALTY, -handValue);
                int lostActual = creditsBefore - p.getCredits();

                System.out.println("[ROUND END " + roundNumber + "] " + p.getName() + " | pénalité main = -" + handValue + " | crédits : " + creditsBefore + " → " + p.getCredits());

                if (lostActual >= JURY_TRIGGER_THRESHOLD) {
                    juryCandidates.add(new JuryCandidate(p, lostActual));
                    System.out.println("[ROUND END " + roundNumber + "] " + p.getName() + " → convoqué au jury (perte : " + lostActual + " crédits)");
                }
            }
        }

        return juryCandidates;
    }

    /**
     * Checks and applies the "semester abroad" flag for players
     * having emptied their hand during this round.
     * Must be called AFTER deductHandPenalties() and jury resolution.
     */
    public void checkStudyAbroad() {
        for (Player p : activePlayers) {
            if (p.getHand().isEmpty()) {
                p.setStudyAbroad(true);
                System.out.println("[ROUND END " + roundNumber + "] " + p.getName() + " → Semestre à l'étranger activé ! (4 cartes la prochaine manche)");
            }
        }
    }

    /**
     * Checks and applies the DETEC bonus.
     * Conditions: LONG mode + end of round 4 + credits ≥ 120.
     * Must be called at the end of round 4 only.
     */
    public void checkDetecBonus() {
        if (!gameMode.hasDetecBonus()) return;
        if (roundNumber != GameMode.DETEC_ROUND) return;

        for (Player p : allPlayers) {
            if (p.getCredits() >= GameMode.DETEC_THRESHOLD) {
                ledger.record(roundNumber, p, CreditLedger.Reason.DETEC_BONUS, GameMode.DETEC_BONUS);
                System.out.println("[DETEC] " + p.getName() + " valide le DETEC ! +" + GameMode.DETEC_BONUS + " crédits → " + p.getCredits());
            }
        }
    }

    /**
     * Checks if a player has to leave during the gap semester.
     * Condition: credits > 0 (after possible jury).
     *
     * @param player the player to check
     * @return true if this player has to go on hiatus
     */
    public boolean needsCesure(Player player) {
        return player.getCredits() < 0;
    }

    /**
     * Applies the suspension (gap semester) to a player.
     * The suspension will be lifted at the beginning of the next round.
     *
     * @param player the player to be suspended
     */
    public void applyCesure(Player player) {
        player.setSuspended(true);
        System.out.println("[CESURE] " + player.getName() + " part en semestre de césure (crédits : " + player.getCredits() + ")");
    }

    /**
     * Special case 1v1: if ALL active players are on a hiatus,
     * They’re up to bat together.
     *
     * @return true if all active players are suspended
     */
    public boolean allPlayersSuspended() {
        return allPlayers.stream().allMatch(Player::isSuspended);
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public List<Player> getAllPlayers() {
        return allPlayers;
    }

    public List<Player> getActivePlayers() {
        return activePlayers;
    }

    public boolean isEnded() {
        return over;
    }

    public void markEnded() {
        over = true;
    }

    @Override
    public String toString() {
        return "Round[" + roundNumber + " | " + activePlayers.size() + " joueurs actifs]";
    }

    /**
     * Represents a player summoned to the jury with the amount of his loss.
     * Returned by deductHandPenalties() so that Game/Controller
     * can trigger the juries in the correct order.
     */
    public record JuryCandidate(Player player, int creditsLost) {
    }
}
