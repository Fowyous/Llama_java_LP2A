package main.java.com.utbm.llama.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Registre comptable des crédits pour toute la partie.
 * Centralise l'historique de toutes les opérations de crédits,
 * manche par manche et joueur par joueur.
 * Avantages :
 *  - Évite de surcharger Game et Round avec de la comptabilité
 *  - Permet d'afficher un récapitulatif clair en fin de partie
 *  - Facilite le débogage (trace de chaque opération)
 * Utilisation typique :
 *  CreditLedger ledger = new CreditLedger();
 *  ledger.record(1, player, CreditLedger.Reason.ROUND_START,  +35);
 *  ledger.record(1, player, CreditLedger.Reason.HAND_PENALTY, -12);
 *  ledger.record(1, player, CreditLedger.Reason.JURY_GAIN,    +6);
 *  int lost = ledger.getLostThisRound(1, player);   // → 12
 *  int total = ledger.getTotal(player);             // → 35 - 12 + 6 = 29
 */
public class CreditLedger {

    /**
     * Catégorie d'une opération de crédits.
     * Utilisée pour filtrer l'historique et générer des résumés.
     */
    public enum Reason {

        /** +35 crédits au début de chaque manche. */
        ROUND_START("Début de manche", true),

        /** Déduction de la valeur des cartes restantes en main. */
        HAND_PENALTY("Pénalité de main", false),

        /** Gain de crédits suite au mini-jeu du Jury. */
        JURY_GAIN("Gain au jury", true),

        /** +30 crédits pour avoir validé le DETEC (mode LONG, manche 4). */
        DETEC_BONUS("Bonus DETEC", true),

        /** Correction manuelle (usage exceptionnel). */
        MANUAL_CORRECTION("Correction", true);

        private final String label;
        private final boolean isGain;

        Reason(String label, boolean isGain) {
            this.label  = label;
            this.isGain = isGain;
        }

        public String  getLabel()  { return label; }
        public boolean isGain()    { return isGain; }
        public boolean isLoss()    { return !isGain; }
    }

    /**
     * Une ligne du registre : qui, quand, pourquoi, combien.
     */
    public static class Entry {
        private final int    roundNumber;
        private final Player player;
        private final Reason reason;
        private final int    delta;
        private final int    balanceAfter;

        public Entry(int roundNumber, Player player, Reason reason, int delta, int balanceAfter) {
            this.roundNumber  = roundNumber;
            this.player       = player;
            this.reason       = reason;
            this.delta        = delta;
            this.balanceAfter = balanceAfter;
        }

        public int    getRoundNumber()  { return roundNumber; }
        public Player getPlayer()       { return player; }
        public Reason getReason()       { return reason; }
        public int    getDelta()        { return delta; }
        public int    getBalanceAfter() { return balanceAfter; }

        @Override
        public String toString() {
            return String.format("[Manche %d] %-20s | %-15s | %+4d crédits → %d",
                    roundNumber, player.getName(), reason.getLabel(), delta, balanceAfter);
        }
    }

    /** Toutes les entrées dans l'ordre chronologique. */
    private final List<Entry> entries = new ArrayList<>();

    /**
     * Enregistre une opération de crédits ET l'applique sur le modèle Player.
     *
     * @param roundNumber numéro de la manche
     * @param player      le joueur concerné
     * @param reason      la raison de l'opération
     * @param delta       montant (positif = gain, négatif = perte)
     */
    public void record(int roundNumber, Player player, Reason reason, int delta) {
        player.addCredits(delta);

        Entry entry = new Entry(roundNumber, player, reason, delta, player.getCredits());
        entries.add(entry);

        System.out.println("[LEDGER] " + entry);
    }

    /**
     * Enregistre une opération SANS l'appliquer (le Player a déjà été modifié).
     * Utile pour synchroniser le ledger avec des opérations externes.
     *
     * @param roundNumber  numéro de la manche
     * @param player       le joueur concerné
     * @param reason       la raison
     * @param delta        montant appliqué
     * @param balanceAfter solde actuel du joueur (après application)
     */
    public void recordOnly(int roundNumber, Player player, Reason reason, int delta, int balanceAfter) {
        entries.add(new Entry(roundNumber, player, reason, delta, balanceAfter));
    }

    /**
     * Calcule les crédits PERDUS par un joueur lors d'une manche donnée.
     * Seules les pertes (delta < 0) sont comptées.
     *
     * @param roundNumber numéro de la manche
     * @param player      le joueur
     * @return somme des pertes (valeur positive)
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
     * Calcule les crédits GAGNÉS par un joueur lors d'une manche donnée.
     *
     * @param roundNumber numéro de la manche
     * @param player      le joueur
     * @return somme des gains
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
     * Retourne le delta net d'un joueur sur une manche.
     * (gains - pertes)
     */
    public int getNetForRound(int roundNumber, Player player) {
        return entries.stream()
                .filter(e -> e.getRoundNumber() == roundNumber && e.getPlayer().equals(player))
                .mapToInt(Entry::getDelta)
                .sum();
    }

    /**
     * Retourne toutes les entrées d'un joueur pour une manche.
     */
    public List<Entry> getEntriesForRound(int roundNumber, Player player) {
        return entries.stream()
                .filter(e -> e.getRoundNumber() == roundNumber && e.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    /**
     * Retourne toutes les entrées d'un joueur sur toute la partie.
     */
    public List<Entry> getEntriesForPlayer(Player player) {
        return entries.stream()
                .filter(e -> e.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    /**
     * Retourne toutes les entrées dans l'ordre chronologique (copie défensive).
     */
    public List<Entry> getAllEntries() {
        return Collections.unmodifiableList(entries);
    }

    /**
     * Génère un tableau récapitulatif par joueur et par manche.
     * Format : Joueur → [manche 1 net, manche 2 net, ..., total]
     *
     * @param players  liste des joueurs de la partie
     * @param nbRounds nombre de manches jouées
     * @return map joueur → tableau des deltas par manche
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
     * Affiche l'historique complet dans la console (debug).
     */
    public void printFull() {
        System.out.println("══════════════ REGISTRE DES CRÉDITS ══════════════");
        entries.forEach(System.out::println);
        System.out.println("══════════════════════════════════════════════════");
    }

    /** @return le nombre total d'entrées enregistrées */
    public int size() { return entries.size(); }
}
