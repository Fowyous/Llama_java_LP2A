package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 * Mini-jeu du Jury.
 * Contexte : déclenché quand un joueur perd ≥ 20 crédits dans une manche.
 * Déroulement :
 * 1. Les 7 types de cartes (ONE à LLAMA) sont mélangés et présentés face cachée.
 * 2. Le joueur choisit exactement UNE carte (par son index 0-6).
 * 3. La carte est révélée : le joueur gagne sa valeur en crédits (1 à 10).
 * 4. Après le jury :
 * - si crédits toujours < 0 → semestre de césure
 * - sinon → retour au jeu normal
 * Note : le choix appartient à la vue/contrôleur.
 * Jury (modèle) ne fait que stocker les cartes et appliquer le résultat.
 */
public class Jury {


    /**
     * Le joueur qui passe devant le jury.
     */
    private final Player targetPlayer;

    /**
     * Crédits perdus cette manche (pour info / log).
     */
    private final int creditsLostThisRound;

    /**
     * Les 7 cartes dans un ordre aléatoire.
     * Seul l'index est visible par le joueur (les cartes sont face cachée).
     */
    private final List<CardType> hiddenCards;

    /**
     * La carte choisie par le joueur, null tant que le jury n'est pas résolu.
     */
    private CardType chosenCard = null;

    /**
     * true quand le joueur a fait son choix.
     */
    private boolean resolved = false;

    /**
     * Crée un jury pour un joueur donné.
     * Mélange aléatoirement les 7 cartes.
     *
     * @param player               le joueur convoqué
     * @param creditsLostThisRound crédits perdus cette manche (≥ 20)
     */
    public Jury(Player player, int creditsLostThisRound) {
        if (player == null)
            throw new IllegalArgumentException("Le joueur du jury ne peut pas être null");
        if (creditsLostThisRound < 20)
            throw new IllegalArgumentException(
                    "Le jury se déclenche uniquement pour une perte ≥ 20 crédits (reçu : "
                            + creditsLostThisRound + ")");

        this.targetPlayer = player;
        this.creditsLostThisRound = creditsLostThisRound;

        List<CardType> cards = new ArrayList<>(Arrays.asList(CardType.values()));
        Collections.shuffle(cards);
        this.hiddenCards = Collections.unmodifiableList(cards);
    }

    /**
     * Le joueur choisit une carte par son index (0 à 6).
     * Applique immédiatement le gain de crédits sur le modèle Player.
     *
     * @param index index de la carte choisie (0-6)
     * @return la carte révélée
     * @throws IllegalStateException    si le jury est déjà résolu
     * @throws IllegalArgumentException si l'index est invalide
     */
    public CardType pickCard(int index) {
        if (resolved)
            throw new IllegalStateException("Le jury a déjà été résolu pour " + targetPlayer.getName());
        if (index < 0 || index >= hiddenCards.size())
            throw new IllegalArgumentException("Index invalide : " + index + " (doit être 0-6)");

        chosenCard = hiddenCards.get(index);
        int gained = chosenCard.getValue();

        targetPlayer.addCredits(gained);

        resolved = true;

        System.out.println("[JURY] " + targetPlayer.getName()
                + " choisit la carte index " + index
                + " → " + chosenCard.name()
                + " | +" + gained + " crédits"
                + " | Total : " + targetPlayer.getCredits());

        return chosenCard;
    }

    /**
     * Indique si le joueur doit partir en semestre de césure après le jury.
     * Condition : crédits toujours négatifs après le gain du jury.
     *
     * @return true si une césure est nécessaire
     * @throws IllegalStateException si le jury n'est pas encore résolu
     */
    public boolean requiresCesure() {
        if (!resolved)
            throw new IllegalStateException("Le jury doit être résolu avant de vérifier la césure");
        return targetPlayer.getCredits() < 0;
    }

    /**
     * @return le joueur passant devant le jury
     */
    public Player getTargetPlayer() {
        return targetPlayer;
    }

    /**
     * @return les crédits perdus ayant déclenché le jury
     */
    public int getCreditsLostThisRound() {
        return creditsLostThisRound;
    }

    /**
     * @return la liste des 7 cartes dans leur ordre mélangé.
     * La vue ne montre que l'index (dos visible), pas le type.
     */
    public List<CardType> getHiddenCards() {
        return hiddenCards;
    }

    /**
     * @return la carte choisie, ou null si le jury n'est pas encore résolu
     */
    public CardType getChosenCard() {
        return chosenCard;
    }

    /**
     * @return le gain obtenu, ou 0 si pas encore résolu
     */
    public int getGained() {
        return chosenCard != null ? chosenCard.getValue() : 0;
    }

    /**
     * @return true si le jury a été joué
     */
    public boolean isResolved() {
        return resolved;
    }

    @Override
    public String toString() {
        return String.format("Jury[%s | perdu=%d | résolu=%s | carte=%s]",
                targetPlayer.getName(), creditsLostThisRound, resolved,
                chosenCard != null ? chosenCard.name() : "—");
    }
}
