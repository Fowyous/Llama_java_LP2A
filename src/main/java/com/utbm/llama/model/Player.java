package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Représente un joueur humain dans le jeu LAMA UTBM.
 * <p>
 * Données gérées :
 * - Main de cartes
 * - Crédits (remplace les points du jeu original)
 * - État dans la manche (PLAYING / QUITTING)
 * - Flags spéciaux : semestre à l'étranger, semestre de césure
 * - Suivi des crédits perdus dans la manche (pour déclencher le jury)
 */
public class Player {

    private final String name;

    private final List<CardType> hand = new ArrayList<>();

    /**
     * Crédits actuels du joueur. Peuvent être négatifs.
     */
    private int credits = 0;

    /**
     * Crédits perdus au cours de la manche en cours.
     * Remis à 0 au début de chaque manche.
     * Utilisé pour déclencher le jury (seuil ≥ 20).
     */
    private int creditsLostThisRound = 0;

    /**
     * Nombre de cartes distribuées en début de manche.
     * Vaut 6 par défaut, 4 si le joueur bénéficie du semestre à l'étranger.
     */
    private int startingHandSize = 6;

    /**
     * true → le joueur a vidé sa main lors de la manche précédente.
     * Il commence la prochaine manche avec 4 cartes au lieu de 6.
     * Ce flag est remis à false au début de la manche où il s'applique.
     * Non cumulable.
     */
    private boolean studyAbroad = false;

    /**
     * true → le joueur est en semestre de césure.
     * Il saute la prochaine manche mais reçoit quand même les +35 crédits.
     * Remis à false au début de la manche où il reprend.
     */
    private boolean suspended = false;

    private State state = State.PLAYING;

    public Player(String name) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Le nom du joueur ne peut pas être vide");
        this.name = name;
    }

    /**
     * Ajoute une carte à la main du joueur.
     *
     * @param card la carte à ajouter
     */
    public void addCard(CardType card) {
        if (card == null) throw new IllegalArgumentException("Carte null impossible");
        hand.add(card);
    }

    /**
     * Retire une carte de la main du joueur.
     *
     * @param card la carte à retirer
     * @throws IllegalStateException si la carte n'est pas dans la main
     */
    public void removeCard(CardType card) {
        if (!hand.remove(card))
            throw new IllegalStateException(name + " n'a pas la carte " + card + " en main");
    }

    /**
     * Vide complètement la main du joueur.
     * Utilisé entre deux manches.
     */
    public void clearHand() {
        hand.clear();
    }

    /**
     * Retourne la main sous forme de liste non modifiable.
     */
    public List<CardType> getHand() {
        return Collections.unmodifiableList(hand);
    }

    /**
     * Calcule la valeur totale des cartes encore en main.
     * C'est ce montant qui sera déduit des crédits en fin de manche.
     *
     * @return somme des valeurs des cartes en main
     */
    public int computeHandValue() {
        return hand.stream()
                .mapToInt(CardType::getValue)
                .sum();
    }

    /**
     * Ajoute (ou soustrait si négatif) un nombre de crédits.
     *
     * @param amount montant à ajouter (peut être négatif)
     */
    public void addCredits(int amount) {
        if (amount < 0) {
            creditsLostThisRound += Math.abs(amount);
        }
        credits += amount;
    }

    /**
     * @return les crédits actuels (peut être négatif)
     */
    public int getCredits() {
        return credits;
    }

    /**
     * Force la valeur des crédits (utiliser avec précaution).
     */
    public void setCredits(int credits) {
        this.credits = credits;
    }

    /**
     * @return les crédits perdus au cours de la manche en cours (≥ 0)
     */
    public int getCreditsLostThisRound() {
        return creditsLostThisRound;
    }

    /**
     * Remet le compteur de perte à 0 (appelé au début de chaque manche).
     */
    public void resetCreditsLostThisRound() {
        creditsLostThisRound = 0;
    }

    /**
     * @return true si le joueur bénéficie du semestre à l'étranger (4 cartes).
     */
    public boolean hasStudyAbroad() {
        return studyAbroad;
    }

    /**
     * Active ou désactive le flag semestre à l'étranger.
     * Quand true, startingHandSize est automatiquement mis à 4.
     */
    public void setStudyAbroad(boolean studyAbroad) {
        this.studyAbroad = studyAbroad;
        this.startingHandSize = studyAbroad ? 4 : 6;
    }

    /**
     * @return true si le joueur est en semestre de césure (saute une manche).
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * Active ou désactive la suspension (semestre de césure).
     */
    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    /**
     * @return le nombre de cartes à distribuer en début de manche (4 ou 6).
     */
    public int getStartingHandSize() {
        return startingHandSize;
    }

    public State getState() {
        return state;
    }

    public void changeState(State newState) {
        if (newState == null) throw new IllegalArgumentException("State null impossible");
        this.state = newState;
    }

    public String getName() {
        return name;
    }

    /**
     * Remet le joueur en état PLAYING et réinitialise le suivi des crédits.
     * Appelé par Round.startRound() pour chaque joueur actif.
     */
    public void resetForNewRound() {
        state = State.PLAYING;
        creditsLostThisRound = 0;
    }

    @Override
    public String toString() {
        return String.format("Player[%s | %d crédits | %d cartes | %s%s%s]",
                name, credits, hand.size(), state,
                studyAbroad ? " | ABROAD" : "",
                suspended ? " | CÉSURE" : "");
    }

    public void setCreditsLostThisRound(int creditsLostThisRound) {
        this.creditsLostThisRound = creditsLostThisRound;
    }
}