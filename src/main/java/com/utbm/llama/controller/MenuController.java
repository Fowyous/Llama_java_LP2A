package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.view.MainFrame;
import main.java.com.utbm.llama.view.MenuView;

import java.util.Locale;

/**
 * Contrôleur du menu principal.
 * Responsabilités :
 * - Brancher les boutons de MenuView sur les actions de navigation
 * - Déléguer au GameController pour lancer/configurer une partie
 */
public class MenuController {

    private final MainFrame mainFrame;
    private final MenuView menuView;
    private GameController gameController;

    private final Locale locale;

    /**
     * Initializes the menu controller by retrieving the view from the main window and attaching event listeners to the buttons.
     */
    public MenuController(MainFrame mainFrame, Locale locale) {
        this.locale = locale;
        this.mainFrame = mainFrame;
        this.menuView = mainFrame.getMenuView();
        initListeners();
    }

    /**
     * Links the user interface actions (Launch, Settings, Exit) to the corresponding logic methods on the controller.
     */
    private void initListeners() {

        menuView.addStartListener(e -> handleStartGame());
        menuView.addSettingsListener(e -> handleOpenSettings());
        menuView.addQuitListener(e -> handleExit());
        menuView.addRulesListener(e -> handleOpenRules());
    }

    /**
     * Launches a new game with the default settings.
     * If the settings were never saved, open the settings first.
     */
    public void handleStartGame() {
        if (gameController != null) {
            gameController.startGame();
        }
    }

    /**
     * Navigates to the settings screen.
     */
    public void handleOpenSettings() {
        mainFrame.showSettings();
    }

    /**
     * Exits the app cleanly.
     */
    public void handleExit() {
        System.exit(0);
    }


    /**
     * Called by GameController after its own construction.
     */
    public void setGameController(GameController gc) {
        this.gameController = gc;
    }

    public void handleOpenRules() {
        mainFrame.showRules();
    }
}