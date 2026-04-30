package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.State;
import main.java.com.utbm.llama.model.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Panel representing a player (human or bot) on the board.
 * Poster:
 * - the name and type (bot/human)
 * - current credits
 * - the hand (visible to the local player, hidden from others)
 * - special conditions: "Semester abroad," "Jury," "Gap"
 * - an indicator "active turn"
 */
public class PlayerView extends JPanel implements LocaleChangeListener{

    private static final Color BG_IDLE = Color.decode("#141414");
    private static final Color BG_ACTIVE = Color.decode("#1C1C1C");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color TEXT_MAIN = Color.decode("#F0EDE6");
    private static final Color TEXT_SUB = Color.decode("#8A8680");
    private static final Color BORDER_ACT = Color.decode("#C8A84B");
    private static final Color BORDER_IDL = Color.decode("#2E2E2E");
    private static final Color GREEN = Color.decode("#4CAF7D");
    private static final Color RED = Color.decode("#D4526E");
    private static final Color BLUE = Color.decode("#5B9BD5");

    private final String playerName;
    private final boolean isBot;

    private final JLabel nameLabel;
    private final JLabel creditsLabel;
    private final JLabel statusBadge;
    private final HandView handView;
    private final JLabel activeIndicator;
    
    private ResourceBundle bundle;
    private Locale currentLocale;
    private Player currentPlayer;
    /**
     * Initializes the player panel with name and type, setting up the layout for the header (name, credits, status) and the hand container.
     * 
     * @param playerName the name of the player
     * @param isBot true if the player is an AI-controlled bot
     * @param mainFrame the mainframe
     */
    public PlayerView(String playerName, boolean isBot, MainFrame mainFrame) {
        this.playerName = playerName;
        this.isBot = isBot;
        this.currentLocale = mainFrame.getCurrentLocale();
        this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);
        mainFrame.addLocaleChangeListener(this);

        setBackground(BG_IDLE);
        setBorder(new CompoundBorder(new LineBorder(BORDER_IDL, 1), new EmptyBorder(12, 14, 12, 14)));
        setLayout(new BorderLayout(8, 8));

        activeIndicator = new JLabel("▶", SwingConstants.CENTER);
        activeIndicator.setFont(new Font("Monospaced", Font.BOLD, 12));
        activeIndicator.setForeground(ACCENT);
        activeIndicator.setVisible(false);
        activeIndicator.setPreferredSize(new Dimension(18, 18));

        nameLabel = new JLabel(buildNameText());
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_MAIN);

        creditsLabel = new JLabel("35 crédits");
        creditsLabel.setFont(new Font("Serif", Font.BOLD, 20));
        creditsLabel.setForeground(ACCENT);

        statusBadge = new JLabel();
        statusBadge.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusBadge.setBorder(new EmptyBorder(2, 6, 2, 6));
        statusBadge.setVisible(false);

        JPanel topLeft = new JPanel();
        topLeft.setOpaque(false);
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.Y_AXIS));
        topLeft.add(nameLabel);
        topLeft.add(statusBadge);

        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setOpaque(false);
        header.add(activeIndicator, BorderLayout.WEST);
        header.add(topLeft, BorderLayout.CENTER);
        header.add(creditsLabel, BorderLayout.EAST);

        handView = new HandView();

        add(header, BorderLayout.NORTH);
        add(handView, BorderLayout.CENTER);
    }

    /**
     * Updates the entire panel from the Player template.
     *
     * @param player the template to display
     * @param isLocal true if it’s the local player (visible hand)
     * @param isActive true if it’s that player’s turn
     */
    public void update(Player player, boolean isLocal, boolean isActive) {
        this.currentPlayer = player;
        updateCredits(player.getCredits());
        updateActive(isActive);
        updateStatus(player);

        if (isLocal) {
            handView.updateHand(player.getHand(), isActive);
        } else {
            handView.showHidden(player.getHand().size());
        }
    }

    /**
     * Updates the display of credits with color according to value.
     */
    public void updateCredits(int credits) {
        creditsLabel.setText(credits + bundle.getString("label.credits"));
        if (credits >= 180) {
            creditsLabel.setForeground(GREEN);
        } else if (credits < 0) {
            creditsLabel.setForeground(RED);
        } else if (credits < 20) {
            creditsLabel.setForeground(Color.decode("#E07B54"));
        } else {
            creditsLabel.setForeground(ACCENT);
        }
    }

    /**
     * Toggles the active tower indicator.
     */
    public void updateActive(boolean active) {
        activeIndicator.setVisible(active);
        setBackground(active ? BG_ACTIVE : BG_IDLE);
        setBorder(new CompoundBorder(new LineBorder(active ? BORDER_ACT : BORDER_IDL, active ? 2 : 1), new EmptyBorder(12, 14, 12, 14)));
    }

    /**
     * Updates special state badges.
     */
    public void updateStatus(Player player) {
        if (player.isSuspended()) {
            showBadge(bundle.getString("status.suspended"), RED);
        } else if (player.hasStudyAbroad()) {
            showBadge(bundle.getString("status.study.abroad"), BLUE);
        } else if (player.getState() == State.QUITTING) {
            showBadge(bundle.getString("status.quitting"), TEXT_SUB);
        } else {
            statusBadge.setVisible(false);
        }
    }

    /**
     * Configures and displays a formatted status badge with specific colors and borders to highlight a player's temporary condition.
     */
    private void showBadge(String text, Color color) {
        statusBadge.setText(text);
        statusBadge.setForeground(color);
        statusBadge.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(BorderFactory.createCompoundBorder(
        		BorderFactory.createLineBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80), 1), 
        		new EmptyBorder(2, 6, 2, 6)
        		));
        statusBadge.setVisible(true);
    }

    /**
     *  @return  the HandView component associated with this player to allow interaction with the cards.
     */
    public HandView getHandView() {
        return handView;
    }

    /**
     *  @return the name of the player associated with this view.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return  true if the player represented by this view is an AI-controlled bot.
     */
    public boolean isBot() {
        return isBot;
    }

    /**
     *  Generates the display string for the player's name, appending a robot icon if the player is a bot.
     *  @return the generated string
     */
    private String buildNameText() {
        return playerName + (isBot ? "  🤖" : "");
    }
    /**     
     * Called when the application locale changes. Updates all UI text to reflect the new language.     
     * */    
    @Override    
    public void onLocaleChange(Locale locale) {        
    	this.currentLocale = locale;        
    	this.bundle = ResourceBundle.getBundle("main.resources.strings", locale);
    	
    	// Update credits label with new localized text        
    	if (currentPlayer != null) {        
    		updateCredits(currentPlayer.getCredits());            
    		updateStatus(currentPlayer);        
    	}
    	
    	// Repaint to reflect changes        
    	revalidate();        
    	repaint();    
    	}
}
