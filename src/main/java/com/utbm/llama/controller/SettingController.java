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

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller of the settings screen.
 * Responsibilities:
 * - Read the user’s choices (number of players, difficulty, mode)
 * - Build the list of players (human + bots)
 * - Create a new Game template and send it to the GameController
 * - Navigate to the menu or start the game depending on the action
 */
public class SettingController {

    private final MainFrame mainFrame;
    private final SettingsView settingsView;
    private GameController gameController;

    private Locale currentLocale;
    private ResourceBundle bundle;

    private int savedNbPlayers = 3;
    private Difficulty savedDifficulty = Difficulty.MEDIUM;
    private GameMode savedGameMode = GameMode.SHORT;

    /**
     * Initialise le contrôleur des paramètres en récupérant la vue dédiée,
     * la langue actuelle du système et en configurant les écouteurs d'événements.
     */
    public SettingController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.settingsView = mainFrame.getSettingsView();
        this.currentLocale = mainFrame.getCurrentLocale();
        this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);
        initListeners();
    }

    /**
     * Relie les interactions de la vue (sauvegarde, retour, changement de langue)
     * aux méthodes logiques du contrôleur pour mettre à jour la configuration.
     */
    private void initListeners() {

        settingsView.addSaveListener(e -> handleSaveSettings(settingsView.getNbPlayers(), settingsView.getDifficulty(), settingsView.getGameMode()));

        settingsView.addBackListener(e -> mainFrame.showMenu());

        settingsView.addLanguageChangeListener(e -> handleLanguageChange());
    }

    /**
     * Récupère la nouvelle langue sélectionnée par l'utilisateur et
     * demande à la fenêtre principale de mettre à jour l'ensemble de l'application.
     */
    private void handleLanguageChange() {
        Locale newLocale = settingsView.getSelectedLanguage();
        this.currentLocale = newLocale;
        mainFrame.updateApplicationLocale(newLocale);
    }

    /**
     * Save the settings, build the Game template and start the game.
     *
     * @param nbPlayers  total number of players (1 human + N-1 bots)
     * @param difficulty bots difficulty
     * @param gameMode   SHORT (6 rounds) or LONG (10 rounds)
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
     * Create the player list: 1 human (index 0) + (nbPlayers - 1) bots.
     */
    private List<Player> buildPlayers(int nbPlayers, Difficulty difficulty) {
        List<Player> players = new ArrayList<>();

        players.add(new Player(bundle.getString("player_human")));

        String[] botNameKeys = {
                "bot_student_a",
                "bot_student_b",
                "bot_student_c",
                "bot_student_d",
                "bot_student_e"
        };
        for (int i = 1; i < nbPlayers; i++) {
            String botName = bundle.getString(botNameKeys[i - 1]);
            players.add(new Bot(botName, difficulty));
        }

        return players;
    }

    /**
     * Retourne le nombre de joueurs actuellement enregistré dans les paramètres.
     */
    public int getSavedNbPlayers() {
        return savedNbPlayers;
    }

    /**
     * Retourne le niveau de difficulté sélectionné pour les bots.
     */
    public Difficulty getSavedDifficulty() {
        return savedDifficulty;
    }

    /**
     * Retourne le mode de jeu (court ou long) choisi par l'utilisateur.
     */
    public GameMode getSavedGameMode() {
        return savedGameMode;
    }

    /**
     * Définit la référence vers le contrôleur de jeu principal pour permettre la transition vers le plateau lors du lancement.
     */
    public void setGameController(GameController gc) {
        this.gameController = gc;
    }
}