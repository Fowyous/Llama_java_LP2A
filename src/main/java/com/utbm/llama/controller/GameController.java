package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.view.MainFrame;

import java.util.Locale;

/**
 * Root controller of the LAMA UTBM application.
 * Role: conductor.
 * - Creates and links all subcontrollers
 * - Holds the reference to the Game model and the local player
 * - Exposes entry points used by subcontrollers
 * to navigate or change the global state
 * Lifecycle:
 * main()
 * └── new GameController(mainFrame)
 * ├── new MenuController   → switch to menu
 * ├── new SettingController  → branch the settings
 * ├── new JuryController → ready to be triggered
 * └── new BoardController → created at the time of startGame()
 * Pattern used: Mediator
 * The subcontrollers do not know each other directly,
 * they go through GameController to trigger cross-actions.
 */
public class GameController {

    private final MainFrame mainFrame;
    private Game game;
    private Player localPlayer;

    private final MenuController menuController;
    private final SettingController settingController;
    private final JuryController juryController;
    private BoardController boardController;

    private final Locale locale;

    /**
     * Initializes the entire application.
     * To be called only once from main().
     *
     * @param mainFrame the main window already built
     */
    public GameController(MainFrame mainFrame, Locale locale) {
        this.locale = locale;

        this.mainFrame = mainFrame;

        this.menuController = new MenuController(mainFrame, locale);
        this.settingController = new SettingController(mainFrame);
        this.juryController = new JuryController(mainFrame);

        menuController.setGameController(this);
        settingController.setGameController(this);

        mainFrame.showMenu();

        System.out.println("[GAME] Application démarrée — en attente de configuration");
    }

    /**
     * Starts (or restarts) a game.
     * Called by MenuController or SettingController.
     * Prerequisites: game and localPlayer must be non-null.
     * If game is null (not configured yet), redirects to settings.
     */
    public void startGame() {
        if (game == null || localPlayer == null) {
            System.out.println("[GAME] Aucune partie configurée → ouverture des paramètres");
            showSettings();
            return;
        }

        System.out.println("[GAME] Démarrage d'une partie | " + game.getPlayers().size() + " joueurs | " + game.getGameMode());
        game.start();

        boardController = new BoardController(mainFrame, game, localPlayer, juryController, locale);

        boardController.initBoard();
    }

    /**
     * Directs the main window to switch its view to the main menu screen.
     */
    public void showMenu() {
        mainFrame.showMenu();
    }

    /**
     * Directs the main window to switch its view to the game settings and configuration screen.
     */
    public void showSettings() {
        mainFrame.showSettings();
    }

    /**
     * Replaces the game template.
     * Called by SettingController after the user has backed up.
     */
    public void setGame(Game game) {
        this.game = game;
        System.out.println("[GAME] Nouveau modèle configuré : " + game.getPlayers().size() + " joueurs, mode " + game.getGameMode());
    }

    /**
     * Defines the human-controlled player.
     */
    public void setLocalPlayer(Player localPlayer) {
        this.localPlayer = localPlayer;
        System.out.println("[GAME] Joueur local : " + localPlayer.getName());
    }

    /**
     * Returns the current game model instance.
     */
    public Game getGame() {
        return game;
    }

    /**
     * Retrieves the player object assigned to the local user.
     */
    public Player getLocalPlayer() {
        return localPlayer;
    }

    /**
     * Retrieves the controller responsible for main menu interactions.
     */
    public MenuController getMenuController() {
        return menuController;
    }

    /**
     * Retrieves the controller responsible for game configuration and settings.
     */
    public SettingController getSettingController() {
        return settingController;
    }

    /**
     * Retrieves the controller responsible for the Jury mini-game logic.
     */
    public JuryController getJuryController() {
        return juryController;
    }

    /**
     * Retrieves the active game board controller (null if a game has not started).
     */
    public BoardController getBoardController() {
        return boardController;
    }

    /**
     * Returns the primary application window reference.
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }
}