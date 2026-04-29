package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a complete round of the LAMA UTBM game.
 * A round takes place in three phases:
 * ┌─ Phase 1: Round Start ──────────────────────────────────────────────────────┐
 * │  - Each player receives +35 credits                                         │
 * │  - Suspended players receive credits BUT do not play                        │
 * │  - Active players receive 6 cards (or 4 if on a study abroad semester)      │
 * │  - The studyAbroad flag is reset to false after card distribution           │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ Phase 2: Play ─────────────────────────────────────────────────────────────┐
 * │  - Players take turns playing                                               │
 * │  - Round ends: all active players have quit OR one player emptied their hand│
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ Phase 3: End of Round ─────────────────────────────────────────────────────┐
 * │  MANDATORY ORDER:                                                           │
 * │  1. deductHandPenalties()  → deducts value of remaining cards               │
 * │  2. checkJury()            → loss ≥ 20 → Jury (handled by JuryController)   │
 * │  3. checkCesure()          → credits < 0 → Suspension semester              │
 * │  4. checkStudyAbroad()     → empty hand → 4 cards next round                │
 * │  5. checkDetecBonus()      → round 4, LONG mode, ≥ 120 credits → +30        │
 * └─────────────────────────────────────────────────────────────────────────────┘
 */
public class Round {

    private final int roundNumber;
    private final List<Player> allPlayers;
    private final List<Player> activePlayers;
    private final GameMode gameMode;
    private final CreditLedger ledger;

    private boolean over = false;

    public static final int CREDITS_PER_ROUND       = 35;
    public static final int JURY_TRIGGER_THRESHOLD  = 20;
    public static final int DEFAULT_HAND_SIZE       = 6;
    public static final int STUDY_ABROAD_HAND_SIZE  = 4;

    /**
     * Creates a new round.
     *
     * @param roundNumber round number (starts at 1)
     * @param allPlayers  all players in the game
     * @param gameMode    game mode (used for DETEC bonus check)
     * @param ledger      credit ledger shared with Game
     */
    public Round(int roundNumber, List<Player> allPlayers, GameMode gameMode, CreditLedger ledger) {
        this.roundNumber   = roundNumber;
        this.allPlayers    = allPlayers;
        this.gameMode      = gameMode;
        this.ledger        = ledger;
        this.activePlayers = new ArrayList<>();
    }

    /**
     * Initializes the round:
     * - Distributes +35 credits to ALL players (including suspended ones)
     * - Identifies active players (non-suspended)
     * - For each active player: resets to PLAYING state, clears hand, deals cards
     * - Applies study abroad effect (4 cards) if the flag is active
     * - Resets the studyAbroad flag to false after distribution
     * - Lifts the suspension for players coming back from a gap year
     *
     * @param drawPile the draw pile from which cards are dealt
     */
    public void startRound(Deck drawPile) {
        activePlayers.clear();

        for (Player p : allPlayers) {

            ledger.record(roundNumber, p, CreditLedger.Reason.ROUND_START, CREDITS_PER_ROUND);

            if (p.isSuspended()) {
                p.setSuspended(false);
                System.out.println("[ROUND " + roundNumber + "] " + p.getName()
                        + " returns after gap year | " + p.getCredits() + " credits");
                continue;
            }

            p.resetForNewRound();
            p.clearHand();

            int handSize = p.hasStudyAbroad() ? STUDY_ABROAD_HAND_SIZE : DEFAULT_HAND_SIZE;
            if (p.hasStudyAbroad()) {
                System.out.println("[ROUND " + roundNumber + "] " + p.getName()
                        + " → Study abroad semester: " + handSize + " cards only");
            }

            for (int i = 0; i < handSize; i++) {
                CardType drawn = drawPile.draw();
                if (drawn == null) {
                    System.out.println("[ROUND] ⚠ Draw pile empty during distribution for "
                            + p.getName() + " — stopped at " + p.getHand().size() + " cards");
                    break;
                }
                p.addCard(drawn);
            }

            p.setStudyAbroad(false);

            activePlayers.add(p);
            System.out.println("[ROUND " + roundNumber + "] " + p.getName()
                    + " | " + p.getHand().size() + " cards | " + p.getCredits() + " credits");
        }
    }

    /**
     * Checks whether the current round is over.
     * End conditions:
     * - All active players have quit (State.QUITTING) or have an empty hand
     * - At least one active player has emptied their hand
     *
     * @return true if the round should end
     */
    public boolean isOver() {
        if (activePlayers.isEmpty()) return true;

        boolean allQuit = activePlayers.stream()
                .allMatch(p -> p.getState() == State.QUITTING || p.getHand().isEmpty());

        boolean anyEmptyHand = activePlayers.stream()
                .anyMatch(p -> p.getHand().isEmpty());

        return allQuit || anyEmptyHand;
    }

    /**
     * Applies the end-of-round penalty to all active players.
     * Deducts the total value of cards remaining in each player's hand.
     * ⚠ Must be called FIRST in the end-of-round sequence.
     *
     * @return list of jury candidates (players who lost ≥ 20 credits)
     */
    public List<JuryCandidate> deductHandPenalties() {
        List<JuryCandidate> juryCandidates = new ArrayList<>();

        for (Player p : activePlayers) {
            int handValue = p.computeHandValue();

            if (handValue > 0) {
                int creditsBefore = p.getCredits();
                ledger.record(roundNumber, p, CreditLedger.Reason.HAND_PENALTY, -handValue);
                int actualLoss = creditsBefore - p.getCredits();

                System.out.println("[ROUND END " + roundNumber + "] " + p.getName()
                        + " | hand penalty = -" + handValue
                        + " | credits: " + creditsBefore + " → " + p.getCredits());

                if (actualLoss >= JURY_TRIGGER_THRESHOLD) {
                    juryCandidates.add(new JuryCandidate(p, actualLoss));
                    System.out.println("[ROUND END " + roundNumber + "] " + p.getName()
                            + " → summoned to jury (loss: " + actualLoss + " credits)");
                }
            }
        }

        return juryCandidates;
    }

    /**
     * Checks and applies the study abroad flag for players who emptied their hand.
     * ⚠ Must be called AFTER deductHandPenalties() and jury resolution.
     */
    public void checkStudyAbroad() {
        for (Player p : activePlayers) {
            if (p.getHand().isEmpty()) {
                p.setStudyAbroad(true);
                System.out.println("[ROUND END " + roundNumber + "] " + p.getName()
                        + " → Study abroad semester activated! (4 cards next round)");
            }
        }
    }

    /**
     * Checks and applies the DETEC bonus.
     * Conditions: LONG mode + end of round 4 + credits ≥ 120.
     * ⚠ Should only be called at the end of round 4.
     */
    public void checkDetecBonus() {
        if (!gameMode.hasDetecBonus())           return;
        if (roundNumber != GameMode.DETEC_ROUND) return;

        for (Player p : allPlayers) {
            if (p.getCredits() >= GameMode.DETEC_THRESHOLD) {
                ledger.record(roundNumber, p, CreditLedger.Reason.DETEC_BONUS, GameMode.DETEC_BONUS);
                System.out.println("[DETEC] " + p.getName()
                        + " validates the DETEC! +" + GameMode.DETEC_BONUS
                        + " credits → " + p.getCredits());
            }
        }
    }

    /**
     * Checks whether a player must start a gap year (suspension semester).
     * Condition: credits < 0 (after optional jury resolution).
     *
     * @param player the player to check
     * @return true if the player must be suspended
     */
    public boolean needsSuspension(Player player) {
        return player.getCredits() < 0;
    }

    /**
     * Applies the suspension (gap year semester) to a player.
     * The suspension will be lifted at the start of the next round.
     *
     * @param player the player to suspend
     */
    public void applySuspension(Player player) {
        player.setSuspended(true);
        System.out.println("[SUSPENSION] " + player.getName()
                + " starts a gap year semester (credits: " + player.getCredits() + ")");
    }

    /**
     * Handles the special 1v1 case: if ALL players are suspended,
     * they skip the round together.
     *
     * @return true if all players are currently suspended
     */
    public boolean allPlayersSuspended() {
        return allPlayers.stream().allMatch(Player::isSuspended);
    }

    /**
     * Returns the round number.
     *
     * @return round number (starts at 1)
     */
    public int getRoundNumber() { return roundNumber; }

    /**
     * Returns all players in the game (including suspended ones).
     *
     * @return list of all players
     */
    public List<Player> getAllPlayers() { return allPlayers; }

    /**
     * Returns only the players who are active in this round (not suspended).
     *
     * @return list of active players
     */
    public List<Player> getActivePlayers() { return activePlayers; }

    /**
     * Returns whether this round has been marked as ended.
     *
     * @return true if markEnded() has been called
     */
    public boolean isEnded() { return over; }

    /**
     * Marks this round as ended.
     * Called by Game.endCurrentRound() after penalties are applied.
     */
    public void markEnded() { over = true; }

    @Override
    public String toString() {
        return "Round[" + roundNumber + " | " + activePlayers.size() + " active players]";
    }

    /**
     * Represents a player summoned to the jury along with the amount of credits lost.
     * Returned by deductHandPenalties() so that Game/Controller can
     * trigger jury sessions in the correct order.
     */
    public record JuryCandidate(Player player, int creditsLost) {}
}