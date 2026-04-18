package main.java.com.utbm.llama.controller;

import main.java.com.utbm.llama.model.Game;
import main.java.com.utbm.llama.model.Player;
import main.java.com.utbm.llama.view.MainFrame;

/**
 * Contrôleur racine de l'application LAMA UTBM.
 * Rôle : chef d'orchestre.
 * - Crée et relie tous les sous-contrôleurs
 * - Détient la référence au modèle Game et au joueur local
 * - Expose les points d'entrée utilisés par les sous-contrôleurs
 * pour naviguer ou changer d'état global
 * Cycle de vie :
 * main()
 * └── new GameController(mainFrame)
 * ├── new MenuController      → branche le menu
 * ├── new SettingController  → branche les paramètres
 * ├── new JuryController      → prêt à être déclenché
 * └── new BoardController     → créé au moment de startGame()
 * Pattern utilisé : Mediator
 * Les sous-contrôleurs ne se connaissent pas directement,
 * ils passent par GameController pour déclencher des actions croisées.
 */
public class GameController {

    private final MainFrame mainFrame;
    private Game game;
    private Player localPlayer;

    private final MenuController menuController;
    private final SettingController settingController;
    private final JuryController juryController;
    private BoardController boardController;

    /**
     * Initialise l'application entière.
     * À appeler une seule fois depuis main().
     *
     * @param mainFrame la fenêtre principale déjà construite
     */
    public GameController(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        this.menuController = new MenuController(mainFrame);
        this.settingController = new SettingController(mainFrame);
        this.juryController = new JuryController(mainFrame);

        menuController.setGameController(this);
        settingController.setGameController(this);

        mainFrame.showMenu();

        System.out.println("[GAME] Application démarrée — en attente de configuration");
    }

    /**
     * Démarre (ou redémarre) une partie.
     * Appelé par MenuController ou SettingController.
     * Prérequis : game et localPlayer doivent être non-null.
     * Si game est null (pas encore configuré), redirige vers les settings.
     */
    public void startGame() {
        if (game == null || localPlayer == null) {
            System.out.println("[GAME] Aucune partie configurée → ouverture des paramètres");
            showSettings();
            return;
        }

        System.out.println("[GAME] Démarrage d'une partie | "
                + game.getPlayers().size() + " joueurs | "
                + game.getGameMode());

        boardController = new BoardController(mainFrame, game, localPlayer, juryController);

        boardController.initBoard();
    }

    public void showMenu() {
        mainFrame.showMenu();
    }

    public void showSettings() {
        mainFrame.showSettings();
    }

    /**
     * Remplace le modèle de jeu.
     * Appelé par SettingController après que l'utilisateur a sauvegardé.
     */
    public void setGame(Game game) {
        this.game = game;
        System.out.println("[GAME] Nouveau modèle configuré : "
                + game.getPlayers().size() + " joueurs, mode " + game.getGameMode());
    }

    /**
     * Définit le joueur contrôlé par l'humain.
     */
    public void setLocalPlayer(Player localPlayer) {
        this.localPlayer = localPlayer;
        System.out.println("[GAME] Joueur local : " + localPlayer.getName());
    }

    public Game getGame() {
        return game;
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    public MenuController getMenuController() {
        return menuController;
    }

    public SettingController getSettingController() {
        return settingController;
    }

    public JuryController getJuryController() {
        return juryController;
    }

    public BoardController getBoardController() {
        return boardController;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }
}