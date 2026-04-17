package main.java.com.utbm.lama.model;

import main.java.com.utbm.lama.model.enums.CardType;
import main.java.com.utbm.lama.model.enums.Difficulty;
import main.java.com.utbm.lama.model.enums.MoveType;

import java.util.List;
import java.util.Random;

/**
 * Joueur contrôlé par l'IA. Hérite de Player — un Bot EST un Player
 * et peut donc être utilisé partout où un Player est attendu.
 * La seule addition par rapport à Player est la méthode {@link #decideMove},
 * qui choisit automatiquement un coup selon la difficulté configurée.
 * Niveaux de difficulté :
 *   EASY   — Aléatoire pur parmi les coups légaux.
 *             Si une carte jouable existe, 70 % de chance de la jouer,
 *             30 % de piocher. Se couche si la main est pleine (≥6 cartes).
 *   MEDIUM — Préfère toujours jouer une carte plutôt que piocher.
 *             Se couche si le score actuel dépasse un seuil ou si
 *             la main est trop chargée.
 *   HARD   — Tient compte de la carte visible en défausse et des
 *             scores adversaires pour décider si se coucher est rentable.
 *             Joue la carte qui minimise ses pénalités futures.
 */
public class Bot extends Player {

    private static final int EASY_QUIT_HAND_SIZE   = 6;
    private static final int MEDIUM_QUIT_SCORE      = 30;
    private static final int MEDIUM_QUIT_HAND_SIZE  = 5;
    private static final int HARD_QUIT_HAND_SIZE    = 4;

    private Difficulty difficulty;
    private final Random random;

    public Bot(String name, Difficulty difficulty) {
        super(name);
        if (difficulty == null) throw new IllegalArgumentException("La difficulté ne peut pas être null");
        this.difficulty = difficulty;
        this.random     = new Random();
    }

    /** Constructeur avec graine fixe — utile pour les tests déterministes. */
    public Bot(String name, Difficulty difficulty, long seed) {
        super(name);
        this.difficulty = difficulty;
        this.random     = new Random(seed);
    }

    /**
     * Choisit le meilleur coup selon la difficulté du Bot.
     * @param topDiscard  carte visible sur la défausse (null si défausse vide)
     * @param drawEmpty   true si la pioche est vide
     * @param opponents   liste des autres joueurs (pour HARD)
     * @return le MoveType choisi (PLAY_CARD, DRAW_CARD ou QUIT_ROUND)
     */
    public MoveType decideMove(CardType topDiscard, boolean drawEmpty, List<Player> opponents) {
        return switch (difficulty) {
            case EASY   -> decideEasy(topDiscard, drawEmpty);
            case MEDIUM -> decideMedium(topDiscard, drawEmpty);
            case HARD   -> decideHard(topDiscard, drawEmpty, opponents);
        };
    }

    private MoveType decideEasy(CardType topDiscard, boolean drawEmpty) {
        if (getHand().size() >= EASY_QUIT_HAND_SIZE) return MoveType.QUIT_ROUND;
        boolean canPlay = topDiscard != null && hasPlayableCard(topDiscard);

        if (canPlay) {
            if (random.nextDouble() < 0.70) return MoveType.PLAY_CARD;
        }

        if (!drawEmpty) return MoveType.DRAW_CARD;
        return canPlay ? MoveType.PLAY_CARD : MoveType.QUIT_ROUND;
    }

    private MoveType decideMedium(CardType topDiscard, boolean drawEmpty) {
        int handSize = getHand().size();
        if (computeScore() >= MEDIUM_QUIT_SCORE || handSize >= MEDIUM_QUIT_HAND_SIZE) {
            return MoveType.QUIT_ROUND;
        }
        if (topDiscard != null && hasPlayableCard(topDiscard)) return MoveType.PLAY_CARD;
        if (!drawEmpty) return MoveType.DRAW_CARD;
        return MoveType.QUIT_ROUND;
    }

    private MoveType decideHard(CardType topDiscard, boolean drawEmpty, List<Player> opponents) {
        int handSize = getHand().size();

        int minOpponentScore = opponents.stream()
                .mapToInt(Player::computeScore)
                .min()
                .orElse(Integer.MAX_VALUE);
        if (computeScore() < minOpponentScore && handSize >= HARD_QUIT_HAND_SIZE) {
            return MoveType.QUIT_ROUND;
        }
        if (topDiscard != null && hasPlayableCard(topDiscard)) return MoveType.PLAY_CARD;
        if (!drawEmpty && handSize < HARD_QUIT_HAND_SIZE) return MoveType.DRAW_CARD;

        return MoveType.QUIT_ROUND;
    }

    /**
     * Vérifie si le bot possède au moins une carte jouable
     * sur la carte visible de la défausse.
     */
    private boolean hasPlayableCard(CardType topDiscard) {
        return getHand().stream().anyMatch(topDiscard::accepts);
    }

    /**
     * Retourne la première carte jouable de la main par rapport à la défausse.
     * Appelé par BoardController après que decideMove() a retourné PLAY_CARD.
     *
     * @param topDiscard carte du dessus de la défausse
     * @return la carte à jouer, ou null si aucune n'est jouable
     */
    public CardType chooseCard(CardType topDiscard) {
        if (topDiscard == null) return null;
        return getHand().stream()
                .filter(topDiscard::accepts)
                .findFirst()
                .orElse(null);
    }

    public Difficulty getDifficulty() { return difficulty; }

    public void setDifficulty(Difficulty difficulty) {
        if (difficulty == null) throw new IllegalArgumentException("La difficulté ne peut pas être null");
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return String.format("Bot{name='%s', difficulty=%s, score=%d, state=%s}",
                getName(), difficulty, computeScore(), getState());
    }
}
