package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.Difficulty;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Joueur contrôlé par l'IA.
 * Hérite de Player — un Bot est un Player avec une logique de décision automatique.
 * ┌─ EASY ──────────────────────────────────────────────────────────────────────┐
 * │  Joue une carte légale au hasard.                                           │
 * │  Si aucune carte jouable → pioche.                                          │
 * │  Ne passe jamais la manche volontairement (sauf si forcé).                  │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ MEDIUM ────────────────────────────────────────────────────────────────────┐
 * │  Préfère jouer une carte plutôt que piocher.                                │
 * │  Choisit la carte jouable de PLUS GRANDE valeur (vide la main plus vite).   │
 * │  Pioche si aucune carte jouable et que la pioche n'est pas vide.            │
 * │  Passe la manche si aucun coup possible.                                    │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ┌─ HARD ──────────────────────────────────────────────────────────────────────┐
 * │  Minimise la valeur des cartes restantes en main.                           │
 * │  Joue la carte de PLUS PETITE valeur parmi les jouables (garde les grosses  │
 * │  cartes pour plus tard si possible).                                        │
 * │  Passe la manche si la main restante vaut > 15 crédits ET qu'il ne peut    │
 * │  rien jouer (limite les dégâts).                                            │
 * │  Ne pioche JAMAIS si la main contient déjà ≥ 4 cartes.                     │
 * └─────────────────────────────────────────────────────────────────────────────┘
 */
public class Bot extends Player {

    private final Difficulty difficulty;
    private final Random rng = new Random();

    public Bot(String name, Difficulty difficulty) {
        super(name);
        if (difficulty == null) throw new IllegalArgumentException("La difficulté d'un bot ne peut pas être null");
        this.difficulty = difficulty;
    }

    /**
     * Décide du meilleur coup à jouer selon la difficulté.
     *
     * @param game l'état courant du jeu (lecture seule pour la prise de décision)
     * @return un Move valide pour ce bot
     */
    public Move decideMove(Game game) {
        return switch (difficulty) {
            case EASY -> decideMoveEasy(game);
            case MEDIUM -> decideMovemedium(game);
            case HARD -> decideMoveHard(game);
        };
    }

    private Move decideMoveEasy(Game game) {
        List<CardType> playable = getPlayableCards(game);

        if (!playable.isEmpty()) {
            CardType chosen = playable.get(rng.nextInt(playable.size()));
            return Move.playCard(this, chosen);
        }

        if (!game.getDrawPile().isEmpty()) {
            return Move.drawCard(this);
        }

        return Move.quitRound(this);
    }

    private Move decideMovemedium(Game game) {
        List<CardType> playable = getPlayableCards(game);

        if (!playable.isEmpty()) {
            CardType best = playable.stream().max(Comparator.comparingInt(CardType::getValue)).orElseThrow();
            return Move.playCard(this, best);
        }

        if (!game.getDrawPile().isEmpty()) {
            return Move.drawCard(this);
        }

        return Move.quitRound(this);
    }

    private Move decideMoveHard(Game game) {
        List<CardType> playable = getPlayableCards(game);

        if (!playable.isEmpty()) {
            CardType safest = playable.stream().min(Comparator.comparingInt(CardType::getValue)).orElseThrow();
            return Move.playCard(this, safest);
        }

        int handValue = computeHandValue();

        if (!game.getDrawPile().isEmpty() && getHand().size() < 4) {
            return Move.drawCard(this);
        }

        if (handValue > 15) {
            return Move.quitRound(this);
        }

        if (!game.getDrawPile().isEmpty()) {
            return Move.drawCard(this);
        }

        return Move.quitRound(this);
    }

    /**
     * Retourne la liste des cartes de la main jouables sur la défausse actuelle.
     *
     * @param game état du jeu
     * @return cartes jouables (peut être vide)
     */
    private List<CardType> getPlayableCards(Game game) {
        CardType top = game.getDiscardPile().peek();
        return getHand().stream().filter(card -> card.canBePlayedOn(top)).toList();
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    @Override
    public String toString() {
        return "Bot[" + getName() + " | " + difficulty + " | " + getCredits() + " crédits]";
    }
}