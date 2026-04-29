package main.java.com.utbm.llama.view;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.ArrayList;
import java.util.List;

/**
 * Main window of the lama game
 * Manages the navigation between windows via a CardLayout.
 */
public class MainFrame extends JFrame {

    public static final String SCREEN_MENU = "MENU";
    public static final String SCREEN_SETTINGS = "SETTINGS";
    public static final String SCREEN_BOARD = "BOARD";
    public static final String SCREEN_JURY = "JURY";
    public static final String SCREEN_CESURE = "CESURE";

    private final MenuView menuView;
    private final SettingsView settingsView;
    private BoardView boardView;
    private JuryView juryView;
    private CesureView cesureView;

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    public static final String SCREEN_ROUND_SUMMARY = "ROUND_SUMMARY";
    private RoundSummaryView roundSummaryView;

    private Locale currentLocale;
    private List<LocaleChangeListener> localeListeners = new ArrayList<>();

    /**
     * Initializes the main application window, sets up the CardLayout navigation system, and loads the initial menu and settings views.
     */
    public MainFrame(Locale locale) {
        super("LAMA UTBM — Survivre au cursus");


        this.currentLocale = locale;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 800));
        setPreferredSize(new Dimension(1440, 900));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.decode("#0D0D0D"));

        menuView = new MenuView(this);
        settingsView = new SettingsView(this);

        contentPanel.add(menuView, SCREEN_MENU);
        contentPanel.add(settingsView, SCREEN_SETTINGS);

        setContentPane(contentPanel);
        pack();
    }

    /**
     * Display the main menu.
     */
    public void showMenu() {
        cardLayout.show(contentPanel, SCREEN_MENU);
    }

    /**
     * Display the settings menu.
     */
    public void showSettings() {
        cardLayout.show(contentPanel, SCREEN_SETTINGS);
    }

    /**
     * Show the game board.
     * Replaces the existing BoardView if a new one is provided.
     *
     * @param boardView the board view to display
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
     * Displays the Jury’s mini-game for a given player.
     *
     * @param juryView the jury view to display
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
     * Displays the semester screen of the gap.
     *
     * @param cesureView the hyphenation view to display
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

    /**
     * Dynamically replaces and displays the round results screen at the end of a game round.
     */
    public void showRoundSummary(RoundSummaryView view) {
        if (this.roundSummaryView != null) {
            contentPanel.remove(this.roundSummaryView);
        }
        this.roundSummaryView = view;
        contentPanel.add(view, SCREEN_ROUND_SUMMARY);
        cardLayout.show(contentPanel, SCREEN_ROUND_SUMMARY);
        revalidate();
        repaint();
    }

    /**
     * Updates the internal language state and notifies all registered listeners to refresh their text components.
     */
    public void updateApplicationLocale(Locale locale) {
        this.currentLocale = locale;

        for (LocaleChangeListener listener : localeListeners) {
            listener.onLocaleChange(locale);
        }

    }

    /**
     * Registers a new component to receive notifications whenever the application's language is changed.
     */
    public void addLocaleChangeListener(LocaleChangeListener listener) {
        localeListeners.add(listener);
    }

    /**
     * Removes a listener from the notification list, typically called when a view is destroyed.
     */
    public void removeLocaleChangeListener(LocaleChangeListener listener) {
        localeListeners.remove(listener);
    }

    /**
     * Returns the current Locale (language and region) being used by the application.
     */
    public Locale getCurrentLocale() {
        return currentLocale;
    }

    /**
     * Retrieves the current instance of the round summary screen.
     */
    public RoundSummaryView getRoundSummaryView() {
        return roundSummaryView;
    }

    /**
     * Retrieves the main menu view instance.
     */
    public MenuView getMenuView() {
        return menuView;
    }

    /**
     * Retrieves the settings view instance.
     */
    public SettingsView getSettingsView() {
        return settingsView;
    }

    /**
     * Retrieves the current game board view instance.
     */
    public BoardView getBoardView() {
        return boardView;
    }

    /**
     * Retrieves the current Jury mini-game view instance.
     */
    public JuryView getJuryView() {
        return juryView;
    }

    /**
     * Retrieves the current Gap Year (Cesure) view instance.
     */
    public CesureView getCesureView() {
        return cesureView;
    }
}