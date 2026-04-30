package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Show the top card of the discard pile.
 */
class DiscardPileView extends JPanel implements LocaleChangeListener{

    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color SUB = Color.decode("#8A8680");

    private CardView topCardView;
    private final JLabel label;
    private final JPanel cardHolder;
    private JLabel emptyLabel;

    private ResourceBundle bundle;
    private Locale currentLocale;
    
    /**
     * Initializes the discard pile component, setting up a card holder container and the localized label with a default empty state.
     *
     * @param mainFrame the locale for UI text localization
     */
    public DiscardPileView(MainFrame mainFrame) {
    	this.currentLocale = mainFrame.getCurrentLocale();
        this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);
        mainFrame.addLocaleChangeListener(this);


        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        cardHolder = new JPanel(new GridBagLayout());
        cardHolder.setOpaque(false);
        cardHolder.setPreferredSize(new Dimension(CardView.CARD_W + 8, CardView.CARD_H + 8));
        cardHolder.setMinimumSize(cardHolder.getPreferredSize());
        cardHolder.setMaximumSize(cardHolder.getPreferredSize());
        cardHolder.setAlignmentX(Component.CENTER_ALIGNMENT);

        label = new JLabel(bundle.getString("label.discard.pile"), SwingConstants.CENTER);
        label.setFont(new Font("Monospaced", Font.BOLD, 10));
        label.setForeground(ACCENT);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        emptyLabel = new JLabel(bundle.getString("label.empty"), SwingConstants.CENTER);
        emptyLabel.setFont(new Font("Serif", Font.ITALIC, 12));
        emptyLabel.setForeground(SUB);
        cardHolder.add(emptyLabel);
        /* 
        JLabel empty = new JLabel("(vide)", SwingConstants.CENTER);
        empty.setFont(new Font("Serif", Font.ITALIC, 12));
        empty.setForeground(SUB);
        cardHolder.add(empty);*/

        add(cardHolder);
        add(Box.createVerticalStrut(6));
        add(label);
    }

    /**
     * Updates the card displayed on the discard.
     *
     * @param topCard null if the discard is empty
     */
    public void render(CardType topCard) {
        cardHolder.removeAll();

        if (topCard == null) {
            emptyLabel = new JLabel(bundle.getString("label.empty"), SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Serif", Font.ITALIC, 12));
            emptyLabel.setForeground(SUB);
            cardHolder.add(emptyLabel);
        } else {
            topCardView = new CardView(topCard);
            cardHolder.add(topCardView);
        }

        cardHolder.revalidate();
        cardHolder.repaint();
    }

    /**
     * Retrieves the CardView instance currently displayed at the top of the discard pile.
     */
    public CardView getTopCardView() {
        return topCardView;
    }
    @Override    
    public void onLocaleChange(Locale locale) {    
    	this.currentLocale = locale;    
    	this.bundle = ResourceBundle.getBundle("main.resources.strings", locale);
    	// Update labels with new localized strings  
    	label.setText(bundle.getString("label.discard.pile"));
    	// If currently showing empty state, update the empty label
    	if (topCardView == null && cardHolder.getComponentCount() > 0) {   
    		Component comp = cardHolder.getComponent(0);  
    		if (comp instanceof JLabel) {    
    			((JLabel) 
    					comp).setText(bundle.getString("label.empty"));    
    		}
    	}
    
    	// Repaint to reflect changes  
    	revalidate();
    	repaint();
    }
}