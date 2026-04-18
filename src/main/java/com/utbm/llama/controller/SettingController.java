package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.model.Bot;
import main.java.com.utbm.llama.model.enums.Difficulty;
import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.view.MainFrame;
import main.java.com.utbm.llama.view.SettingsView;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrôleur de l'écran de paramètres.
 * Responsabilités :
 * - Lire les choix de l'utilisateur (nb joueurs, difficulté, mode)
 * - Construire la liste de joueurs (humain + bots)
 * - Créer un nouveau modèle Game et le transmettre au GameController
 * - Naviguer vers le menu ou lancer la partie selon l'action
 */
public class SettingController {

    private final MainFrame mainFrame;
    private final SettingsView settingsView;
    private GameController gameController;

    private int savedNbPlayers = 3;
    private Difficulty savedDifficulty = Difficulty.MEDIUM;
    private GameMode savedGameMode = GameMode.SHORT;

    public SettingController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.settingsView = mainFrame.getSettingsView();
        initListeners();
    }

    private void initListeners() {

        settingsView.addSaveListener(e -> handleSaveSettings(
                settingsView.getNbPlayers(),
                settingsView.getDifficulty(),
                settingsView.getGameMode()
        ));

        settingsView.addBackListener(e -> mainFrame.showMenu());
    }

    /**
     * Sauvegarde les paramètres, construit le modèle Game et démarre la partie.
     *
     * @param nbPlayers  nombre total de joueurs (1 humain + N-1 bots)
     * @param difficulty difficulté des bots
     * @param gameMode   SHORT (6 manches) ou LONG (10 manches)
     */
    public void handleSaveSettings(int nbPlayers, Difficulty difficulty, GameMode gameMode) {
        this.savedNbPlayers = nbPlayers;
        this.savedDifficulty = difficulty;
        this.savedGameMode = gameMode;

        List<Player> players = buildPlayers(nbPlayers, difficulty);

        Game newGame = new Game(players, gameMode);

        if (gameController != null) {
            gameController.setGame(newGame);
            gameController.setLocalPlayer(players.get(0));
            gameController.startGame();
        }
    }


    /**
     * Crée la liste de joueurs : 1 humain (index 0) + (nbPlayers - 1) bots.
     */
    private List<Player> buildPlayers(int nbPlayers, Difficulty difficulty) {
        List<Player> players = new ArrayList<>();

        players.add(new Player("Joueur"));

        String[] botNames = {"IA — Étudiant A", "IA — Étudiant B", "IA — Étudiant C",
                "IA — Étudiant D", "IA — Étudiant E"};

        for (int i = 1; i < nbPlayers; i++) {
            players.add(new Bot(botNames[i - 1], difficulty));
        }

        return players;
    }

    public int getSavedNbPlayers() {
        return savedNbPlayers;
    }

    public Difficulty getSavedDifficulty() {
        return savedDifficulty;
    }

    public GameMode getSavedGameMode() {
        return savedGameMode;
    }

    public void setGameController(GameController gc) {
        this.gameController = gc;
    }
}