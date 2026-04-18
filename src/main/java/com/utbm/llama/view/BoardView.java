package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;
import test.java.com.utbm.modeltest.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue principale du plateau de jeu.
 * Organisation :
 *  ┌─────────────────────────────────────────────────┐
 *  │  HUD (manche, mode, crédits à valider)          │  NORTH
 *  ├────────────────┬────────────────────────────────┤
 *  │  Adversaires   │   Zone centrale                │
 *  │  (colonne G)   │   Pioche | Défausse            │  CENTER
 *  │                │   Actions du joueur local      │
 *  ├────────────────┴────────────────────────────────┤
 *  │  Panneau du joueur local                        │  SOUTH
 *  └─────────────────────────────────────────────────┘
 */
public class BoardView extends JPanel {

    private static final Color BG         = Color.decode("#0D0D0D");
    private static final Color PANEL_BG   = Color.decode("#111111");
    private static final Color ACCENT     = Color.decode("#C8A84B");
    private static final Color TEXT_MAIN  = Color.decode("#F0EDE6");
    private static final Color TEXT_SUB   = Color.decode("#8A8680");
    private static final Color RED        = Color.decode("#D4526E");

    private final DrawPileView          drawPileView;
    private final DiscardPileView       discardPileView;
    private final List<PlayerView>      playerViews = new ArrayList<>();
    private PlayerView                  localPlayerView;

    private final JLabel  roundLabel;
    private final JLabel  modeLabel;
    private final JLabel  thresholdLabel;
    private final JLabel  detecBonusLabel;

    private final JButton btnDraw;
    private final JButton btnQuit;

    private final JPanel opponentsPanel;
    private final JPanel centerPanel;
    private final JPanel localPlayerPanel;

    public BoardView() {
        setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        drawPileView    = new DrawPileView();
        discardPileView = new DiscardPileView();

        roundLabel     = buildHudLabel("MANCHE 1 / 6", 20, Font.BOLD);
        modeLabel      = buildHudLabel("MODE COURT — 180 crédits", 12, Font.PLAIN);
        thresholdLabel = buildHudLabel("", 12, Font.ITALIC);
        detecBonusLabel = buildHudLabel("🎓 BONUS DETEC +30 crédits !", 13, Font.BOLD);
        detecBonusLabel.setForeground(ACCENT);
        detecBonusLabel.setVisible(false);

        btnDraw = buildActionButton("PIOCHER",     Color.decode("#1E3A5F"), TEXT_MAIN);
        btnQuit = buildActionButton("PASSER LA MANCHE", RED,               Color.WHITE);

        opponentsPanel  = new JPanel();
        opponentsPanel.setBackground(PANEL_BG);
        opponentsPanel.setLayout(new BoxLayout(opponentsPanel, BoxLayout.Y_AXIS));
        opponentsPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        opponentsPanel.setPreferredSize(new Dimension(300, 0));

        centerPanel = buildCenterPanel();

        localPlayerPanel = new JPanel(new BorderLayout());
        localPlayerPanel.setBackground(Color.decode("#0F0F0F"));
        localPlayerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#2E2E2E")),
                new EmptyBorder(0, 0, 0, 0)
        ));

        add(buildHud(),      BorderLayout.NORTH);
        add(opponentsPanel,  BorderLayout.WEST);
        add(centerPanel,     BorderLayout.CENTER);
        add(localPlayerPanel, BorderLayout.SOUTH);
    }

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

        hud.add(leftInfo,    BorderLayout.WEST);
        hud.add(centerInfo,  BorderLayout.CENTER);
        hud.add(thresholdLabel, BorderLayout.EAST);

        return hud;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(16, 24, 16, 24);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(drawPileView, gbc);

        gbc.gridx = 1;
        panel.add(discardPileView, gbc);

        JSeparator sep = new JSeparator(SwingConstants.VERTICAL);
        sep.setForeground(Color.decode("#2E2E2E"));
        sep.setPreferredSize(new Dimension(1, 150));
        gbc.gridx = 2; gbc.fill = GridBagConstraints.VERTICAL;
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
     * Rafraîchit entièrement la vue depuis l'état du modèle Game.
     *
     * @param game          le modèle de jeu
     * @param localPlayer   le joueur local (main visible)
     */
    public void updateBoard(Game game, Player localPlayer) {
        updateHud(game);
        updatePiles(game);
        updatePlayers(game, localPlayer);
        updateActions(game, localPlayer);
    }

    private void updateHud(Game game) {
        int maxRounds = game.getGameMode() == GameMode.SHORT ? 6 : 10;
        int threshold = game.getGraduationThreshold();
        roundLabel.setText("MANCHE " + game.getCurrentRound().getRoundNumber() + " / " + maxRounds);
        modeLabel.setText("MODE " + (game.getGameMode() == GameMode.SHORT ? "COURT" : "LONG")
                + " — " + threshold + " crédits pour valider");

        boolean showDetec = game.getGameMode() == GameMode.LONG
                && game.getCurrentRound().getRoundNumber() == 4;
        detecBonusLabel.setVisible(showDetec);
    }

    private void updatePiles(Game game) {
        drawPileView.render(game.getDrawPile().size());
        discardPileView.render(game.getDiscardPile().isEmpty()
                ? null
                : game.getDiscardPile().peek());
    }

    private void updatePlayers(Game game, Player localPlayer) {
        localPlayerPanel.removeAll();
        if (localPlayerView == null || !localPlayerView.getPlayerName().equals(localPlayer.getName())) {
            localPlayerView = new PlayerView(localPlayer.getName(), localPlayer instanceof Bot);
        }
        boolean isLocalActive = game.getCurrentPlayer().equals(localPlayer);
        localPlayerView.update(localPlayer, true, isLocalActive);
        localPlayerPanel.add(localPlayerView, BorderLayout.CENTER);

        opponentsPanel.removeAll();
        JLabel oppTitle = new JLabel("ADVERSAIRES");
        oppTitle.setFont(new Font("Monospaced", Font.BOLD, 10));
        oppTitle.setForeground(TEXT_SUB);
        oppTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        opponentsPanel.add(oppTitle);

        for (Player p : game.getPlayers()) {
            if (p.equals(localPlayer)) continue;
            PlayerView pv = new PlayerView(p.getName(), p instanceof Bot);
            pv.update(p, false, game.getCurrentPlayer().equals(p));
            opponentsPanel.add(pv);
            opponentsPanel.add(Box.createVerticalStrut(8));
        }

        localPlayerPanel.revalidate();
        opponentsPanel.revalidate();
        repaint();
    }

    private void updateActions(Game game, Player localPlayer) {
        boolean isActive = game.getCurrentPlayer().equals(localPlayer)
                && !localPlayer.isSuspended();
        btnDraw.setEnabled(isActive && !game.getDrawPile().isEmpty());
        btnQuit.setEnabled(isActive && localPlayer.getState() == State.PLAYING);
        localPlayerView.getHandView().setInteractive(isActive);
    }

    private JLabel buildHudLabel(String text, int size, int style) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Monospaced", style, size));
        lbl.setForeground(TEXT_MAIN);
        return lbl;
    }

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

    public void addDrawListener(ActionListener l){
        btnDraw.addActionListener(l);
    }
    public void addQuitRoundListener(ActionListener l){
        btnQuit.addActionListener(l);
    }

    public DrawPileView    getDrawPileView(){
        return drawPileView;
    }
    public DiscardPileView getDiscardPileView(){
        return discardPileView;
    }
    public PlayerView      getLocalPlayerView(){
        return localPlayerView;
    }
    public List<PlayerView> getPlayerViews(){
        return playerViews;
    }

}
