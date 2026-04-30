package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.Bot;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Summary screen displayed between each round.
 * Poster for each player:
 *  - Credits lost this round (hand penalty)
 *  - Earned credits (jury, debt)
 * - Net balance of the round
 * - Current Total
 * - Badges: Semester abroad / Gap year
 * A "Next Handle" button allows you to continue.
 */
public class RoundSummaryView extends JPanel implements LocaleChangeListener{

    private static final Color BG = Color.decode("#0D0D0D");
    private static final Color PANEL_BG = Color.decode("#111111");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color TEXT = Color.decode("#F0EDE6");
    private static final Color SUB = Color.decode("#8A8680");
    private static final Color GREEN = Color.decode("#4CAF7D");
    private static final Color RED = Color.decode("#D4526E");
    private static final Color BLUE = Color.decode("#5B9BD5");
    private static final Color BORDER = Color.decode("#2E2E2E");

    // Locale and bundle
    private Locale currentLocale;
    private ResourceBundle bundle;

    private final JLabel roundLabel;
    private final JLabel modeLabel;
    private final JPanel playersPanel;
    private final JButton btnNext;
    private final JLabel bonusLabel;

    // Data for re-rendering on locale change
    private List<Player> lastPlayers;
    private int lastRoundNumber;
    private int lastMaxRounds;
    private GameMode lastGameMode;
    private java.util.Map<Player, Integer> lastCreditsLostMap;
    private java.util.Map<Player, Integer> lastCreditsGainedMap;
    private boolean lastDetecApplied;
    
    /**
     *  Initializes the view with a dark theme, sets up the layout, and builds the header, player panel, and "Next Round" button.
     *  @param mainFrame the mainFrame to add the locale listener
     */
    public RoundSummaryView(MainFrame mainFrame) {
        this.currentLocale = mainFrame.getCurrentLocale();
        this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);
        mainFrame.addLocaleChangeListener(this);

        setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        roundLabel = buildLabel(bundle.getString("round_summary.title"), 28, Font.BOLD, TEXT);
        modeLabel = buildLabel("", 13, Font.ITALIC, SUB);
        bonusLabel = buildLabel("🎓 " + bundle.getString("round_summary.bonus_deutec"), 14, Font.BOLD, ACCENT);
        bonusLabel.setVisible(false);

        playersPanel = new JPanel();
        playersPanel.setBackground(BG);
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));

        btnNext = new JButton(bundle.getString("round_summary.next_button"));
        btnNext.setFont(new Font("Monospaced", Font.BOLD, 15));
        btnNext.setBackground(ACCENT);
        btnNext.setForeground(Color.decode("#0D0D0D"));
        btnNext.setFocusPainted(false);
        btnNext.setOpaque(true);
        btnNext.setBorderPainted(false);
        btnNext.setPreferredSize(new Dimension(280, 54));
        btnNext.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    /**
     *  Constructs the top section of the screen containing the end-of-round title, the game mode label, and the DETEC bonus indicator.
     */
    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.decode("#0A0A0A"));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(28, 40, 20, 40)
        ));

        roundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        modeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bonusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bonusLabel.setBorder(new EmptyBorder(8, 0, 0, 0));

        header.add(roundLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(modeLabel);
        header.add(bonusLabel);

        return header;
    }

    /**
     *  Creates a scrollable container for the players' statistics to handle different screen sizes or large player counts.
     */
    private JScrollPane buildCenter() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        wrapper.add(playersPanel, gbc);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(null);
        return scroll;
    }

    /**
     *  Sets up the bottom section of the screen containing the navigation button to proceed to the next round.
     */
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG);
        footer.setBorder(new EmptyBorder(0, 0, 32, 0));
        footer.add(btnNext);
        return footer;
    }

    /**
     * Populates the view with endgame data.
     * @param players list of players
     * @param roundNumber round number that has just ended
     * @param maxRounds total number of rounds
     * @param gameMode game mode
     * @param creditsLostMap lost credits per player this round (penalty)
     * @param creditsGainedMap credits earned per player (jury, debrief)
     * @param detecApplied true if the DETEC bonus has been applied
     */
    public void setup(List<Player> players,
                      int roundNumber, int maxRounds,
                      GameMode gameMode,
                      java.util.Map<Player, Integer> creditsLostMap,
                      java.util.Map<Player, Integer> creditsGainedMap,
                      boolean detecApplied) {
        // Store data for potential re-rendering on locale change
        this.lastPlayers = players;
        this.lastRoundNumber = roundNumber;
        this.lastMaxRounds = maxRounds;
        this.lastGameMode = gameMode;
        this.lastCreditsLostMap = creditsLostMap;
        this.lastCreditsGainedMap = creditsGainedMap;
        this.lastDetecApplied = detecApplied;


        roundLabel.setText(bundle.getString("round_summary.round_header") + roundNumber + " / " + maxRounds);
        modeLabel.setText(gameMode.toString());
        bonusLabel.setVisible(detecApplied);

        playersPanel.removeAll();

        for (Player p : players) {
            int lost = creditsLostMap.getOrDefault(p, 0);
            int gained = creditsGainedMap.getOrDefault(p, 0);
            int net = gained - lost;

            playersPanel.add(buildPlayerRow(p, lost, gained, net));
            playersPanel.add(Box.createVerticalStrut(10));
        }

        playersPanel.revalidate();
        playersPanel.repaint();
    }

    /**
     * Builds a player’s line in the summary.
     */
    private JPanel buildPlayerRow(Player player, int lost, int gained, int net) {
        JPanel row = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setLayout(new BorderLayout(12, 0));
        row.setBorder(new EmptyBorder(14, 20, 14, 20));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftCol = new JPanel();
        leftCol.setOpaque(false);
        leftCol.setLayout(new BoxLayout(leftCol, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(player.getName() + (player instanceof Bot ? "  🤖" : ""));
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        nameLabel.setForeground(TEXT);
        leftCol.add(nameLabel);

        if (player.isSuspended()) {
            leftCol.add(buildBadge("✈ " + bundle.getString("round_summary.badge_gap_semester"), RED));
        } else if (player.hasStudyAbroad()) {
            leftCol.add(buildBadge("🌍 " + bundle.getString("round_summary.badge_study_abroad"), BLUE));
        }

        JPanel rightCol = new JPanel(new GridLayout(3, 2, 8, 2));
        rightCol.setOpaque(false);

        addStatLine(rightCol, "Pénalité main :", "-" + lost + " crédits", RED);
        addStatLine(rightCol, "Gains :", "+" + gained + " crédits", GREEN);
        addStatLine(rightCol, "Net manche :",
                (net >= 0 ? "+" : "") + net + " crédits",
                net >= 0 ? GREEN : RED);

        JLabel totalLabel = new JLabel(player.getCredits() + " crédits");
        totalLabel.setFont(new Font("Serif", Font.BOLD, 22));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setForeground(player.getCredits() < 0 ? RED
                : player.getCredits() >= 180 ? GREEN
                : ACCENT);

        row.add(leftCol, BorderLayout.WEST);
        row.add(rightCol, BorderLayout.CENTER);
        row.add(totalLabel, BorderLayout.EAST);

        return row;
    }

    /**
     *  Helper method to add a formatted row of statistics (label and value) to a player's summary panel.
     */
    private void addStatLine(JPanel panel, String labelText, String valueText, Color valueColor) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lbl.setForeground(SUB);

        JLabel val = new JLabel(valueText);
        val.setFont(new Font("Monospaced", Font.BOLD, 11));
        val.setForeground(valueColor);

        panel.add(lbl);
        panel.add(val);
    }

    /**
     *  Creates a small, colored indicator label for specific player statuses like academic semesters abroad or suspensions.
     */
    private JLabel buildBadge(String text, Color color) {
        JLabel badge = new JLabel(text);
        badge.setFont(new Font("Monospaced", Font.PLAIN, 10));
        badge.setForeground(color);
        badge.setBorder(new EmptyBorder(3, 0, 0, 0));
        return badge;
    }

    /**
     *  Utility method to create a standardized JLabel with specific font settings and colors.
     */
    private JLabel buildLabel(String text, int size, int style, Color color) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", style, size));
        lbl.setForeground(color);
        return lbl;
    }

    /**
     *  Attaches an event listener to the "Next Round" button to handle screen transitions.
     */
    public void addNextRoundListener(ActionListener l) {
        btnNext.addActionListener(l);
    }
}