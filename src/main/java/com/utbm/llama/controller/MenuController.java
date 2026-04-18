package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.view.MainFrame;
import main.java.com.utbm.llama.view.MenuView;

/**
 * Contrôleur du menu principal.
 * Responsabilités :
 *  - Brancher les boutons de MenuView sur les actions de navigation
 *  - Déléguer au GameController pour lancer/configurer une partie
 */
public class MenuController {

    private final MainFrame    mainFrame;
    private final MenuView     menuView;
    private       GameController gameController;

    public MenuController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.menuView  = mainFrame.getMenuView();
        initListeners();
    }

    private void initListeners() {

        menuView.addStartListener(e -> handleStartGame());
        menuView.addSettingsListener(e -> handleOpenSettings());
        menuView.addQuitListener(e -> handleExit());
    }

    /**
     * Lance une nouvelle partie avec les paramètres par défaut.
     * Si les paramètres n'ont jamais été sauvegardés, ouvre d'abord les settings.
     */
    public void handleStartGame() {
        if (gameController != null) {
            gameController.startGame();
        }
    }

    /** Navigue vers l'écran de paramètres. */
    public void handleOpenSettings() {
        mainFrame.showSettings();
    }

    /** Quitte l'application proprement. */
    public void handleExit() {
        System.exit(0);
    }


    /** Appelé par GameController après sa propre construction. */
    public void setGameController(GameController gc) {
        this.gameController = gc;
    }
}