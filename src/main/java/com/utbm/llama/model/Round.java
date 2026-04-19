package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;

import java.util.ArrayList;
import java.util.List;

/**
 * Modélise une manche complète du jeu LAMA UTBM.
 * Une manche se déroule en trois phases :
 * ┌─ Phase 1 : Début de manche ─────────────────────────────────────────────────┐
 * │  - Chaque joueur reçoit +35 crédits                                         │
 * │  - Les joueurs en césure reçoivent les crédits MAIS ne jouent pas           │
 * │  - Les joueurs normaux reçoivent 6 cartes (ou 4 si semestre à l'étranger)   │
 * │  - Le flag studyAbroad est remis à false après distribution                 │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ Phase 2 : Jeu ─────────────────────────────────────────────────────────────┐
 * │  - Les joueurs jouent tour par tour                                          │
 * │  - Fin de manche : tous les joueurs actifs ont passé OU un a vidé sa main   │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ Phase 3 : Fin de manche ───────────────────────────────────────────────────┐
 * │  Ordre OBLIGATOIRE :                                                         │
 * │  1. deductHandPenalties()  → retire la valeur des cartes restantes           │
 * │  2. checkJury()            → perte ≥ 20 → Jury (géré par JuryController)    │
 * │  3. checkCesure()          → crédits < 0 → Césure                           │
 * │  4. checkStudyAbroad()     → main vide → 4 cartes la prochaine manche        │
 * │  5. checkDetecBonus()      → manche 4, LONG, ≥ 120 crédits → +30            │
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
     * @param roundNumber numéro de la manche (commence à 1)
     * @param allPlayers  tous les joueurs de la partie
     * @param gameMode    mode de jeu (pour le bonus DETEC)
     * @param ledger      registre comptable partagé avec Game
     */
    public Round(int roundNumber, List<Player> allPlayers, GameMode gameMode, CreditLedger ledger) {
        this.roundNumber = roundNumber;
        this.allPlayers = allPlayers;
        this.gameMode = gameMode;
        this.ledger = ledger;
        this.activePlayers = new ArrayList<>();
    }

    /**
     * Initialise la manche :
     * - Distribue +35 crédits à TOUS les joueurs (y compris les suspendus)
     * - Identifie les joueurs actifs (non suspendus)
     * - Pour chaque joueur actif : remet en PLAYING, vide la main, distribue les cartes
     * - Applique le semestre à l'étranger (4 cartes) si actif
     * - Remet le flag studyAbroad à false après distribution
     * - Lève la suspension des joueurs en césure
     *
     * @param drawPile pioche depuis laquelle distribuer les cartes
     */
    public void startRound(Deck drawPile) {
        activePlayers.clear();

        for (Player p : allPlayers) {

            ledger.record(roundNumber, p, CreditLedger.Reason.ROUND_START, CREDITS_PER_ROUND);

            if (p.isSuspended()) {
                p.setSuspended(false);
                System.out.println("[ROUND " + roundNumber + "] " + p.getName()
                        + " reprend après césure | " + p.getCredits() + " crédits");
                continue;
            }

            p.resetForNewRound();
            p.clearHand();

            int handSize = p.hasStudyAbroad() ? STUDY_ABROAD_HAND_SIZE : DEFAULT_HAND_SIZE;
            if (p.hasStudyAbroad()) {
                System.out.println("[ROUND " + roundNumber + "] " + p.getName()
                        + " → Semestre à l'étranger : " + handSize + " cartes seulement");
            }

            // ✅ Dans startRound() — vérification robuste
            for (int i = 0; i < handSize; i++) {
                CardType drawn = drawPile.draw();
                if (drawn == null) {
                    System.out.println("[ROUND] ⚠ Pioche vide pendant la distribution pour "
                            + p.getName() + " — arrêt à " + p.getHand().size() + " cartes");
                    break;
                }
                p.addCard(drawn);
            }

            p.setStudyAbroad(false);

            activePlayers.add(p);
            System.out.println("[ROUND " + roundNumber + "] " + p.getName()
                    + " | " + p.getHand().size() + " cartes | " + p.getCredits() + " crédits");
        }
    }


    /**
     * Vérifie si la manche est terminée.
     * Conditions de fin :
     * - Tous les joueurs actifs ont passé (State.QUITTING)
     * - Au moins un joueur actif a vidé sa main
     *
     * @return true si la manche doit se terminer
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
     * Applique la pénalité de fin de manche à tous les joueurs actifs.
     * Déduit la valeur des cartes restantes en main.
     * ⚠ Doit être appelé EN PREMIER dans la séquence de fin de manche.
     *
     * @return map joueur → crédits perdus (pour que JuryController sache qui convoquer)
     */
    public List<JuryCandidate> deductHandPenalties() {
        List<JuryCandidate> juryCandidates = new ArrayList<>();

        for (Player p : activePlayers) {
            int handValue = p.computeHandValue();

            if (handValue > 0) {
                int creditsBefore = p.getCredits();
                ledger.record(roundNumber, p, CreditLedger.Reason.HAND_PENALTY, -handValue);
                int lostActual = creditsBefore - p.getCredits();

                System.out.println("[ROUND END " + roundNumber + "] " + p.getName()
                        + " | pénalité main = -" + handValue
                        + " | crédits : " + creditsBefore + " → " + p.getCredits());

                if (lostActual >= JURY_TRIGGER_THRESHOLD) {
                    juryCandidates.add(new JuryCandidate(p, lostActual));
                    System.out.println("[ROUND END " + roundNumber + "] " + p.getName()
                            + " → convoqué au jury (perte : " + lostActual + " crédits)");
                }
            }
        }

        return juryCandidates;
    }

    /**
     * Vérifie et applique le flag "semestre à l'étranger" pour les joueurs
     * ayant vidé leur main lors de cette manche.
     * ⚠ Doit être appelé APRÈS deductHandPenalties() et la résolution du jury.
     */
    public void checkStudyAbroad() {
        for (Player p : activePlayers) {
            if (p.getHand().isEmpty()) {
                p.setStudyAbroad(true);
                System.out.println("[ROUND END " + roundNumber + "] " + p.getName()
                        + " → Semestre à l'étranger activé ! (4 cartes la prochaine manche)");
            }
        }
    }

    /**
     * Vérifie et applique le bonus DETEC.
     * Conditions : mode LONG + fin de la manche 4 + crédits ≥ 120.
     * ⚠ Doit être appelé à la fin de la manche 4 uniquement.
     */
    public void checkDetecBonus() {
        if (!gameMode.hasDetecBonus()) return;
        if (roundNumber != GameMode.DETEC_ROUND) return;

        for (Player p : allPlayers) {
            if (p.getCredits() >= GameMode.DETEC_THRESHOLD) {
                ledger.record(roundNumber, p, CreditLedger.Reason.DETEC_BONUS, GameMode.DETEC_BONUS);
                System.out.println("[DETEC] " + p.getName()
                        + " valide le DETEC ! +" + GameMode.DETEC_BONUS
                        + " crédits → " + p.getCredits());
            }
        }
    }

    /**
     * Vérifie si un joueur doit partir en semestre de césure.
     * Condition : crédits < 0 (après éventuel jury).
     *
     * @param player le joueur à vérifier
     * @return true si ce joueur doit partir en césure
     */
    public boolean needsCesure(Player player) {
        return player.getCredits() < 0;
    }

    /**
     * Applique la suspension (semestre de césure) à un joueur.
     * La suspension sera levée au début de la manche suivante.
     *
     * @param player le joueur à suspendre
     */
    public void applyCesure(Player player) {
        player.setSuspended(true);
        System.out.println("[CESURE] " + player.getName()
                + " part en semestre de césure (crédits : " + player.getCredits() + ")");
    }

    /**
     * Cas particulier 1v1 : si TOUS les joueurs actifs sont en césure,
     * ils sautent la manche ensemble.
     *
     * @return true si tous les joueurs actifs sont suspendus
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
     * Représente un joueur convoqué au jury avec le montant de sa perte.
     * Retourné par deductHandPenalties() pour que Game/Controller
     * puisse déclencher les jurys dans le bon ordre.
     */
    public record JuryCandidate(Player player, int creditsLost) {
    }
}
