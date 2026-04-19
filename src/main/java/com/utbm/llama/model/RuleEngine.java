package main.java.com.utbm.llama.model;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.Move;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.model.Round;
import main.java.com.utbm.llama.model.rules.DrawCardRule;
import main.java.com.utbm.llama.model.rules.PlayCardRule;
import main.java.com.utbm.llama.model.rules.QuitRoundRule;
import main.java.com.utbm.llama.model.rules.Rule;

import java.util.List;
import java.util.ArrayList;

/**
 * Moteur de règles du jeu LAMA UTBM.
 * Rôle :
 * - Agrège toutes les règles de validation de coup (PlayCard, Draw, Quit)
 * - Expose deux méthodes principales :
 * * validateMove()  → vérifie qu'un coup est légal
 * * applyRules()    → applique les effets des règles après un coup valide
 * - Expose des méthodes utilitaires pour les règles de fin de manche
 * * processEndOfRound() → séquence complète de fin de manche
 * Ce que RuleEngine ne fait PAS :
 * - Il ne modifie pas directement le modèle (c'est Game.applyMove())
 * - Il ne gère pas le jury ni la césure de façon asynchrone
 * (ces éléments nécessitent une interaction UI → BoardController)
 * Utilisation typique dans BoardController :
 * Move move = Move.playCard(player, card);
 * if (ruleEngine.validateMove(move, game)) {
 * game.applyMove(move);
 * ruleEngine.applyRules(move, game);
 * }
 * // En fin de manche :
 * List<Round.JuryCandidate> candidates = ruleEngine.processEndOfRound(game);
 * // → déclencher les jurys via JuryController
 * ruleEngine.processPostJury(game);
 */
public class RuleEngine {

    private final List<Rule> rules = new ArrayList<>();

    private final StudyAbroadRule studyAbroadRule = new StudyAbroadRule();
    private final JuryRule juryRule = new JuryRule();
    private final CesureRule cesureRule = new CesureRule();
    private final DetecBonusRule detecBonusRule = new DetecBonusRule();


    /**
     * Crée le moteur de règles avec toutes les règles de coup enregistrées.
     */
    public RuleEngine() {
        rules.add(new PlayCardRule());
        rules.add(new DrawCardRule());
        rules.add(new QuitRoundRule());
    }

    /**
     * Vérifie si un coup est légal selon toutes les règles applicables.
     * Seules les règles dont isApplicable() retourne true sont évaluées.
     * Si une seule règle échoue, le coup est refusé.
     *
     * @param move le coup à valider
     * @param game l'état courant du jeu
     * @return true si le coup est légal
     */
    public boolean validateMove(Move move, Game game) {
        if (move == null || game == null) return false;

        for (Rule rule : rules) {
            if (rule.isApplicable(move, game)) {
                if (!rule.validate(move, game)) {
                    System.out.println("[RuleEngine] Coup refusé par " + rule.getName());
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Applique les effets des règles après qu'un coup valide a été exécuté.
     * Appelé APRÈS Game.applyMove().
     *
     * @param move le coup qui vient d'être appliqué
     * @param game l'état du jeu après le coup
     */
    public void applyRules(Move move, Game game) {
        for (Rule rule : rules) {
            if (rule.isApplicable(move, game)) {
                rule.apply(move, game);
            }
        }
    }

    /**
     * Exécute la phase 1 de fin de manche :
     * 1. Déduit les pénalités de main (via Game.endCurrentRound())
     * 2. Identifie les joueurs à passer devant le jury (perte ≥ 20)
     * ⚠ Après cette méthode, BoardController doit :
     * - Déclencher les jurys un par un (JuryController.startJury())
     * - Appeler processPostJury() une fois tous les jurys résolus
     *
     * @param game l'état du jeu en fin de manche
     * @return liste des joueurs convoqués au jury (peut être vide)
     */
    public List<Round.JuryCandidate> processEndOfRound(Game game) {
        System.out.println("[RuleEngine] === Début de la séquence de fin de manche ===");

        List<Round.JuryCandidate> candidates = game.endCurrentRound();

        System.out.println("[RuleEngine] Pénalités appliquées | "
                + candidates.size() + " candidat(s) au jury");

        return candidates;
    }

    /**
     * Exécute la phase 2 de fin de manche, APRÈS résolution de tous les jurys :
     * 1. Vérifie la césure pour chaque joueur encore en négatif
     * 2. Active le semestre à l'étranger pour ceux qui ont vidé leur main
     * 3. Applique le bonus DETEC si les conditions sont remplies
     *
     * @param game l'état du jeu après résolution des jurys
     * @return liste des joueurs partant en semestre de césure
     */
    public List<Player> processPostJury(Game game) {
        List<Player> cesurePlayers = new ArrayList<>();

        for (Player p : game.getCurrentRound().getActivePlayers()) {
            if (CesureRule.shouldTrigger(p)) {
                CesureRule.applyTo(p, game);
                cesurePlayers.add(p);
            }
        }

        for (Player p : game.getCurrentRound().getActivePlayers()) {
            StudyAbroadRule.applyTo(p);
        }

        DetecBonusRule.applyIfEligible(game, game.getLedger());

        game.endRoundPostJury();

        System.out.println("[RuleEngine] === Fin de manche traitée | "
                + cesurePlayers.size() + " césure(s) ===");

        return cesurePlayers;
    }

    /**
     * Vérifie si un joueur peut encore jouer dans la manche (possède une carte jouable).
     * Utile pour les bots et l'affichage de la vue.
     *
     * @param player le joueur à vérifier
     * @param game   l'état du jeu
     * @return true si le joueur a au moins une carte jouable
     */
    public boolean hasPlayableCard(Player player, Game game) {
        CardType top = game.getDiscardPile().peek();
        return player.getHand().stream().anyMatch(c -> c.canBePlayedOn(top));
    }

    /**
     * Retourne la liste des cartes jouables dans la main d'un joueur.
     *
     * @param player le joueur
     * @param game   l'état du jeu
     * @return liste des cartes légalement jouables (peut être vide)
     */
    public List<CardType> getPlayableCards(Player player, Game game) {
        CardType top = game.getDiscardPile().peek();
        return player.getHand().stream()
                .filter(c -> c.canBePlayedOn(top))
                .toList();
    }

    /**
     * Vérifie si la manche en cours est terminée
     * (tous les joueurs actifs ont passé ou un a vidé sa main).
     *
     * @param game l'état du jeu
     * @return true si la manche doit se terminer
     */
    public boolean isRoundOver(Game game) {
        return game.getCurrentRound() != null && game.getCurrentRound().isOver();
    }

    /**
     * Ajoute une règle personnalisée au moteur.
     * Utile pour les extensions ou les tests.
     *
     * @param rule la règle à ajouter
     */
    public void addRule(Rule rule) {
        if (rule != null) rules.add(rule);
    }

    /**
     * @return liste non modifiable de toutes les règles enregistrées
     */
    public List<Rule> getRules() {
        return java.util.Collections.unmodifiableList(rules);
    }
}