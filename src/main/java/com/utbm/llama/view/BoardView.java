package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;
import main.java.com.utbm.llama.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main view of the game board.
 * Organization:
 * ┌─────────────────────────────────────────────────┐
 * │  HUD (round, mode, credits to validate)         │  NORTH
 * ├────────────────┬────────────────────────────────┤
 * │    Opponents   │       Central area             │
 * │    (G column)  │       Draw | Discard           │  CENTER
 * │                │       Local player actions     │
 * ├────────────────┴────────────────────────────────┤
 * │             Local player panel                  │  SOUTH
 * └─────────────────────────────────────────────────┘
 */
public class BoardView extends JPanel implements LocaleChangeListener{

    private static final Color BG = Color.decode("#0D0D0D");
    private static final Color PANEL_BG = Color.decode("#111111");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color TEXT_MAIN = Color.decode("#F0EDE6");
    private static final Color TEXT_SUB = Color.decode("#8A8680");
    private static final Color RED = Color.decode("#D4526E");

    private final DrawPileView drawPileView;
    private final DiscardPileView discardPileView;
    private final List<PlayerView> playerViews = new ArrayList<>();
    private PlayerView localPlayerView;

    private final JLabel roundLabel;
    private final JLabel modeLabel;
    private final JLabel thresholdLabel;
    private final JLabel detecBonusLabel;
    private final JLabel opponentsTitleLabel;

    
    private final JButton btnDraw;
    private final JButton btnQuit;

    private final JPanel opponentsPanel;
    private final JPanel centerPanel;
    private final JPanel localPlayerPanel;

    private ResourceBundle bundle;
    private Locale currentLocale;
    private Game currentGame;
    private Player currentLocalPlayer;
    private MainFrame mainFrame;
    /**
     * Initializes the game board with the specified locale, setting up the HUD,
     * draw/discard piles, and the various layout panels for opponents and the local player.
     */
    public BoardView(MainFrame mainFrame) {
    	this.mainFrame = mainFrame;
        mainFrame.addLocaleChangeListener(this);
    	this.currentLocale = mainFrame.getCurrentLocale();
        this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);

        setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        drawPileView = new DrawPileView(mainFrame);
        discardPileView = new DiscardPileView(mainFrame);

        roundLabel = buildHudLabel(bundle.getString("hud.default.round"), 20, Font.BOLD);
        modeLabel = buildHudLabel("", 12, Font.PLAIN);
        thresholdLabel = buildHudLabel("", 12, Font.ITALIC);
        detecBonusLabel = buildHudLabel(bundle.getString("hud.detecBonus"),3, Font.BOLD);
        detecBonusLabel.setForeground(ACCENT);
        detecBonusLabel.setVisible(false);

        btnDraw = buildActionButton(bundle.getString("action.draw"), Color.decode("#1E3A5F"), TEXT_MAIN);
        btnQuit = buildActionButton(bundle.getString("action.quit"), RED, Color.WHITE);

        opponentsPanel = new JPanel();
        opponentsPanel.setBackground(PANEL_BG);
        opponentsPanel.setLayout(new BoxLayout(opponentsPanel, BoxLayout.Y_AXIS));
        opponentsPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        opponentsPanel.setPreferredSize(new Dimension(300, 0));
        
        opponentsTitleLabel = new JLabel(bundle.getString("opponents.title"));
        opponentsTitleLabel.setFont(new Font("Monospaced", Font.BOLD, 10));
        opponentsTitleLabel.setForeground(Color.decode("#8A8680"));
        opponentsTitleLabel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 10, 0));

        centerPanel = buildCenterPanel();

        localPlayerPanel = new JPanel(new BorderLayout());
        localPlayerPanel.setBackground(Color.decode("#0F0F0F"));
        localPlayerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#2E2E2E")),
                new EmptyBorder(0, 0, 0, 0)
        ));

        add(buildHud(), BorderLayout.NORTH);
        add(opponentsPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(localPlayerPanel, BorderLayout.SOUTH);
    }

    /**
     * Constructs the top navigation bar containing round information,
     * game mode details, and the dynamic DETEC bonus indicator.
     */
    private JPanel buildHud() {
        JPanel hud = new JPanel(new BorderLayout(12, 0));
        hud.setBackground(Color.decode("#0A0A0A"));
        hud.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#2E2E2E")),
                new EmptyBorder(10, 20, 10, 20)
        ));

        JPanel leftInfo = new JPanel();
        leftInfo.setOpaque(false);
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.add(roundLabel);
        leftInfo.add(modeLabel);

        JPanel centerInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerInfo.setOpaque(false);
        centerInfo.add(detecBonusLabel);

        hud.add(leftInfo, BorderLayout.WEST);
        hud.add(centerInfo, BorderLayout.CENTER);
        hud.add(thresholdLabel, BorderLayout.EAST);

        return hud;
    }

    /**
     * Sets up the central gameplay area where the draw pile, discard pile,
     * and primary player action buttons (Draw/Quit) are located.
     */
    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 24, 16, 24);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(drawPileView, gbc);

        gbc.gridx = 1;
        panel.add(discardPileView, gbc);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setForeground(Color.decode("#2E2E2E"));
        sep.setPreferredSize(new Dimension(1, 150));
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.VERTICAL;
        panel.add(sep, gbc);
        gbc.fill = GridBagConstraints.NONE;

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.add(btnDraw);
        actions.add(Box.createVerticalStrut(12));
        actions.add(btnQuit);

        gbc.gridx = 3;
        panel.add(actions, gbc);

        return panel;
    }

    /**
     * Completely refreshes the view from the state of the Game model.
     *
     * @param game        the game model
     * @param localPlayer the local player (visible hand)
     */
    public void updateBoard(Game game, Player localPlayer) {
        this.currentGame = game;
        this.currentLocalPlayer = localPlayer;
        updateHud(game);
        updatePiles(game);
        updatePlayers(game, localPlayer);
        updateActions(game, localPlayer);
    }

    /**
     * Refreshes the HUD labels with the current round number, graduation threshold,
     * and toggles the visibility of special event bonuses.
     */
    private void updateHud(Game game) {
        if (game.getCurrentRound() == null) return;
        int maxRounds = game.getGameMode() == GameMode.SHORT ? 6 : 10;
        int threshold = game.getGraduationThreshold();
        roundLabel.setText(bundle.getString("hud.round") + game.getCurrentRound().getRoundNumber() + " / " + maxRounds);
        modeLabel.setText("MODE " + (game.getGameMode() == GameMode.SHORT ? bundle.getString("hud.mode.short"): bundle.getString("hud.mode.long"))
                + " — " + threshold + bundle.getString("hud.mode.credits"));

        boolean showDetec = game.getGameMode() == GameMode.LONG
                && game.getCurrentRound().getRoundNumber() == 4;
        detecBonusLabel.setVisible(showDetec);
    }

    /**
     * Updates the visual state of the deck and discard pile based on the current number of cards and the top card's value.
     */
    private void updatePiles(Game game) {
        drawPileView.render(game.getDrawPile().size());
        discardPileView.render(game.getDiscardPile().isEmpty()
                ? null
                : game.getDiscardPile().peek());
    }

    /**
     * Rebuilds the player list and local player panel, updating names,
     * bots icons, active turn indicators, and academic statuses.
     */
    private void updatePlayers(Game game, Player localPlayer) {
        localPlayerPanel.removeAll();

        localPlayerView = new PlayerView(
                localPlayer.getName(),
                localPlayer instanceof Bot,
                mainFrame
        );

        boolean isLocalActive = game.getCurrentPlayer().equals(localPlayer);
        localPlayerView.update(localPlayer, true, isLocalActive);
        localPlayerPanel.add(localPlayerView, BorderLayout.CENTER);

        opponentsPanel.removeAll();/*
        JLabel oppTitle = new JLabel("ADVERSAIRES");
        oppTitle.setFont(new Font("Monospaced", Font.BOLD, 10));
        oppTitle.setForeground(Color.decode("#8A8680"));
        oppTitle.setBorder(new javax.swing.border.EmptyBorder(0, 0, 10, 0));
        opponentsPanel.add(oppTitle);*/
        opponentsPanel.add(opponentsTitleLabel);


        for (Player p : game.getPlayers()) {
            if (p.equals(localPlayer)) continue;
            PlayerView pv = new PlayerView(p.getName(), p instanceof Bot, mainFrame);
            pv.update(p, false, game.getCurrentPlayer().equals(p));
            opponentsPanel.add(pv);
            opponentsPanel.add(Box.createVerticalStrut(8));
        }

        localPlayerPanel.revalidate();
        localPlayerPanel.repaint();
        opponentsPanel.revalidate();
        opponentsPanel.repaint();
        revalidate();
        repaint();
    }

    /**
     * Enables or disables the game controls (Draw, Quit, Card selection)
     * based on whether it is currently the local player's turn.
     */
    private void updateActions(Game game, Player localPlayer) {
        boolean isActive = game.getCurrentPlayer().equals(localPlayer)
                && !localPlayer.isSuspended();
        btnDraw.setEnabled(isActive && !game.getDrawPile().isEmpty());
        btnQuit.setEnabled(isActive && localPlayer.getState() == State.PLAYING);
        localPlayerView.getHandView().setInteractive(isActive);
    }

    /**
     * Utility method to create a standardized label for the HUD using a monospaced font and consistent coloring.
     */
    private JLabel buildHudLabel(String text, int size, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", style, size));
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

    /**
     * Creates a stylized JButton for game actions with specific dimensions, colors, and a hand cursor.
     */
    private JButton buildActionButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(220, 44));
        btn.setMaximumSize(new Dimension(220, 44));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /**
     * Registers an event listener for the "Draw" button.
     */
    public void addDrawListener(ActionListener l) {
        btnDraw.addActionListener(l);
    }

    /**
     * Registers an event listener for the "Quit Round" button.
     */
    public void addQuitRoundListener(ActionListener l) {
        btnQuit.addActionListener(l);
    }

    /**
     * Returns the component representing the draw pile.
     */
    public DrawPileView getDrawPileView() {
        return drawPileView;
    }

    /**
     * Returns the component representing the discard pile.
     */
    public DiscardPileView getDiscardPileView() {
        return discardPileView;
    }

    /**
     * Returns the view component specifically assigned to the local human player.
     */
    public PlayerView getLocalPlayerView() {
        return localPlayerView;
    }

    /**
     * Returns the list of all player view components on the board.
     */
    public List<PlayerView> getPlayerViews() {
        return playerViews;
    }

	@Override
	public void onLocaleChange(Locale locale) {
		this.currentLocale = locale;
		this.bundle = ResourceBundle.getBundle("main.resources.strings", locale);
        // Update all labels with new localized strings
		roundLabel.setText(bundle.getString("hud.round"));        
		btnDraw.setText(bundle.getString("button.draw"));        
		btnQuit.setText(bundle.getString("button.quit.round"));        
		detecBonusLabel.setText(bundle.getString("hud.bonus.detec"));        
		opponentsTitleLabel.setText(bundle.getString("label.opponents"));
        // Refresh HUD with current game state in new language        
		if (currentGame != null) {            
			updateHud(currentGame);
		}
		

        // Repaint the entire board to reflect changes    
		revalidate();       
		repaint();
		
	}

}
