package main.java.com.utbm.llama.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Accounting record of credits for the entire game.
 * Centralizes the history of all credit operations,
 * round by round and player by player.
 * Benefits:
 * - Avoid overloading Game and Round with accounting
 * - Allows you to display a clear summary at the end of the game
 * - Facilitates debugging (tracing each operation)
 * Typical use:
 * CreditLedger ledger = new CreditLedger();
 * ledger.record(1, player, CreditLedger.Reason.ROUND_START,  +35);
 * ledger.record(1, player, CreditLedger.Reason.HAND_PENALTY, -12);
 * ledger.record(1, player, CreditLedger.Reason.JURY_GAIN,   +6);
 * int lost = ledger.getLostThisRound(1, player);
 * int total = ledger.getTotal(player);
 */
public class CreditLedger {

    /**
     * Category of a credit transaction.
     * Used to filter history and generate summaries.
     */
    public enum Reason {

        /**
         * +35 credits at the beginning of each round.
         */
        ROUND_START("Début de manche", true),

        /**
         * Deduction of the value of the remaining cards in hand.
         */
        HAND_PENALTY("Pénalité de main", false),

        /**
         * Gain credits following the Jury’s mini-game.
         */
        JURY_GAIN("Gain au jury", true),

        /**
         * +30 credits for having validated the DETEC (long mode, round 4).
         */
        DETEC_BONUS("Bonus DETEC", true),

        /**
         * Manual correction (exceptional use).
         */
        MANUAL_CORRECTION("Correction", true);

        private final String label;
        private final boolean isGain;

        Reason(String label, boolean isGain) {
            this.label = label;
            this.isGain = isGain;
        }

        public String getLabel() {
            return label;
        }

        public boolean isGain() {
            return isGain;
        }

        public boolean isLoss() {
            return !isGain;
        }
    }

    /**
     * A line in the ledger: who, when, why, how much.
     */
    public static class Entry {
        private final int roundNumber;
        private final Player player;
        private final Reason reason;
        private final int delta;
        private final int balanceAfter;

        /**
         * Initialize a new credit entry.
         *
         * @param roundNumber  the index of the round when the transaction occurred
         * @param player       the player affected by the credit change
         * @param reason       the justification for the transaction (penalty, jury, bonus, etc.)
         * @param delta        the amount of credits added or removed
         * @param balanceAfter the total credit balance of the player after this entry
         */
        public Entry(int roundNumber, Player player, Reason reason, int delta, int balanceAfter) {
            this.roundNumber = roundNumber;
            this.player = player;
            this.reason = reason;
            this.delta = delta;
            this.balanceAfter = balanceAfter;
        }

        /**
         * Get the round number associated with this transaction.
         *
         * @return the round number
         */
        public int getRoundNumber() {
            return roundNumber;
        }

        /**
         * Retrieve the player who owns this entry.
         *
         * @return the player instance
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * Get the reason for this credit modification.
         *
         * @return the reason enum value
         */
        public Reason getReason() {
            return reason;
        }

        /**
         * Get the amount of credits gained or lost in this transaction.
         *
         * @return the credit delta
         */
        public int getDelta() {
            return delta;
        }

        /**
         * Retrieve the final balance after the transaction was applied.
         *
         * @return the resulting credit total
         */
        public int getBalanceAfter() {
            return balanceAfter;
        }

        /**
         * Provide a formatted string representation of the credit entry.
         *
         * @return a formatted string containing round, player name, reason, delta, and balance
         */
        @Override
        public String toString() {
            return String.format("[Manche %d] %-20s | %-15s | %+4d crédits → %d",
                    roundNumber, player.getName(), reason.getLabel(), delta, balanceAfter);
        }
    }

    /**
     * All entries in chronological order.
     */
    private final List<Entry> entries = new ArrayList<>();

    /**
     * Saves a credit operation AND applies it to the Player template.
     *
     * @param roundNumber round number
     * @param player      the player in question
     * @param reason      the reason for the operation
     * @param delta       amount (positive = gain, negative = loss)
     */
    public void record(int roundNumber, Player player, Reason reason, int delta) {
        player.addCredits(delta);

        Entry entry = new Entry(roundNumber, player, reason, delta, player.getCredits());
        entries.add(entry);

        System.out.println("[LEDGER] " + entry);
    }

    /**
     * Records an operation WITHOUT applying it (the Player has already been modified).
     * Useful to synchronize the ledger with external operations.
     *
     * @param roundNumber  round number
     * @param player       the player in question
     * @param reason       the reason
     * @param delta        amount applied
     * @param balanceAfter player’s current balance (after application)
     */
    public void recordOnly(int roundNumber, Player player, Reason reason, int delta, int balanceAfter) {
        entries.add(new Entry(roundNumber, player, reason, delta, balanceAfter));
    }

    /**
     * Calculates the LOST credits of a player in a given round.
     * Only losses (delta > 0) are counted.
     *
     * @param roundNumber round number
     * @param player      the player
     * @return sum of losses (positive value)
     */
    public int getLostThisRound(int roundNumber, Player player) {
        return entries.stream()
                .filter(e -> e.getRoundNumber() == roundNumber
                        && e.getPlayer().equals(player)
                        && e.getDelta() < 0)
                .mapToInt(e -> Math.abs(e.getDelta()))
                .sum();
    }

    /**
     * Calculates the EARNED credits of a player in a given round.
     *
     * @param roundNumber round number
     * @param player      the player
     * @return sum of winnings
     */
    public int getGainedThisRound(int roundNumber, Player player) {
        return entries.stream()
                .filter(e -> e.getRoundNumber() == roundNumber
                        && e.getPlayer().equals(player)
                        && e.getDelta() > 0)
                .mapToInt(Entry::getDelta)
                .sum();
    }

    /**
     * Returns a player’s net delta in one round.
     * (gains - losses)
     */
    public int getNetForRound(int roundNumber, Player player) {
        return entries.stream()
                .filter(e -> e.getRoundNumber() == roundNumber && e.getPlayer().equals(player))
                .mapToInt(Entry::getDelta)
                .sum();
    }

    /**
     * Returns all the entries of a player for one round.
     */
    public List<Entry> getEntriesForRound(int roundNumber, Player player) {
        return entries.stream()
                .filter(e -> e.getRoundNumber() == roundNumber && e.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    /**
     * Returns all of a player’s entries over the entire game.
     */
    public List<Entry> getEntriesForPlayer(Player player) {
        return entries.stream()
                .filter(e -> e.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    /**
     * Returns all entries in chronological order (defensive copy).
     */
    public List<Entry> getAllEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Generates a summary table by player and by round.
     * Format: Player → [net round 1, net round 2, ..., total]
     *
     * @param players  list of players in the game
     * @param nbRounds number of rounds played
     * @return map player → deltas table per round
     */
    public Map<Player, int[]> buildSummary(List<Player> players, int nbRounds) {
        Map<Player, int[]> summary = new LinkedHashMap<>();

        for (Player p : players) {
            int[] deltas = new int[nbRounds];
            for (int r = 1; r <= nbRounds; r++) {
                deltas[r - 1] = getNetForRound(r, p);
            }
            summary.put(p, deltas);
        }

        return summary;
    }

    /**
     * Displays the full console history (debug).
     */
    public void printFull() {
        System.out.println("══════════════ REGISTRE DES CRÉDITS ══════════════");
        entries.forEach(System.out::println);
        System.out.println("══════════════════════════════════════════════════");
    }

    /**
     * @return the total number of entries recorded
     */
    public int size() {
        return entries.size();
    }
}
