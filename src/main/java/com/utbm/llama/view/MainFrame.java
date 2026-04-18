package main.java.com.utbm.llama.view;

import javax.swing.*;
import java.awt.*;

/**
 * Fenêtre principale de l'application LAMA UTBM.
 * Gère la navigation entre les différents écrans via un CardLayout.
 */
public class MainFrame extends JFrame {

    public static final String SCREEN_MENU     = "MENU";
    public static final String SCREEN_SETTINGS = "SETTINGS";
    public static final String SCREEN_BOARD    = "BOARD";
    public static final String SCREEN_JURY     = "JURY";
    public static final String SCREEN_CESURE   = "CESURE";

    private final MenuView menuView;
    private final SettingsView settingsView;
    private BoardView boardView;
    private JuryView           juryView;
    private CesureView         cesureView;

    private final CardLayout cardLayout;
    private final JPanel     contentPanel;

    public MainFrame() {
        super("LAMA UTBM — Survivre au cursus");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 800));
        setPreferredSize(new Dimension(1440, 900));
        setLocationRelativeTo(null);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.decode("#0D0D0D"));

        menuView     = new MenuView();
        settingsView = new SettingsView();

        contentPanel.add(menuView, SCREEN_MENU);
        contentPanel.add(settingsView, SCREEN_SETTINGS);

        setContentPane(contentPanel);
        pack();
    }

    /** Affiche le menu principal. */
    public void showMenu() {
        cardLayout.show(contentPanel, SCREEN_MENU);
    }

    /** Affiche l'écran de paramètres. */
    public void showSettings() {
        cardLayout.show(contentPanel, SCREEN_SETTINGS);
    }

    /**
     * Affiche le plateau de jeu.
     * Remplace la BoardView existante si une nouvelle est fournie.
     *
     * @param boardView la vue du plateau à afficher
     */
    public void showGame(BoardView boardView) {
        if (this.boardView != null) {
            contentPanel.remove(this.boardView);
        }
        this.boardView = boardView;
        contentPanel.add(boardView, SCREEN_BOARD);
        cardLayout.show(contentPanel, SCREEN_BOARD);
        revalidate();
        repaint();
    }

    /**
     * Affiche le mini-jeu du Jury pour un joueur donné.
     *
     * @param juryView la vue du jury à afficher
     */
    public void showJury(JuryView juryView) {
        if (this.juryView != null) {
            contentPanel.remove(this.juryView);
        }
        this.juryView = juryView;
        contentPanel.add(juryView, SCREEN_JURY);
        cardLayout.show(contentPanel, SCREEN_JURY);
        revalidate();
        repaint();
    }

    /**
     * Affiche l'écran de semestre de césure.
     *
     * @param cesureView la vue de césure à afficher
     */
    public void showCesure(CesureView cesureView) {
        if (this.cesureView != null) {
            contentPanel.remove(this.cesureView);
        }
        this.cesureView = cesureView;
        contentPanel.add(cesureView, SCREEN_CESURE);
        cardLayout.show(contentPanel, SCREEN_CESURE);
        revalidate();
        repaint();
    }


    public MenuView     getMenuView()     {
        return menuView;
    }
    public SettingsView getSettingsView() {
        return settingsView;
    }
    public BoardView    getBoardView()    {
        return boardView;
    }
    public JuryView     getJuryView()     {
        return juryView;
    }
    public CesureView   getCesureView()   {
        return cesureView;
    }
}