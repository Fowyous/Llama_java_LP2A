package main.java.com.utbm.llama.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Show pickup.
 * One click triggers the "draw a card" action via an ActionListener.
 */
class DrawPileView extends JPanel implements LocaleChangeListener {

    private static final Color BG = new Color(0, 0, 0, 0);
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color TEXT = Color.decode("#F0EDE6");
    private static final Color SUB = Color.decode("#8A8680");

    // Locale and bundle
    private Locale currentLocale;
    private ResourceBundle bundle;
    
    private final JButton drawButton;
    private final JLabel countLabel;
    private final JLabel piocheLabel;

    private int remaining = 0;

    /**
     * Initializes the draw pile component, creating a layered UI that features a custom-painted stack of cards and an invisible interactive button.
     */
    public DrawPileView(MainFrame mainFrame) {
    	this.currentLocale = mainFrame.getCurrentLocale();
        this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);
        mainFrame.addLocaleChangeListener(this);
        
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel pileGraphic = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                for (int i = 3; i >= 0; i--) {
                    int ox = i * 2;
                    int oy = i * 2;
                    g2.setColor(Color.decode("#1A3A5C").darker());
                    g2.fill(new RoundRectangle2D.Float(ox, oy, CardView.CARD_W, CardView.CARD_H, 10, 10));
                }

                g2.setColor(Color.decode("#1A3A5C"));
                g2.fill(new RoundRectangle2D.Float(0, 0, CardView.CARD_W, CardView.CARD_H, 10, 10));
                g2.setColor(ACCENT);
                g2.setFont(new Font("Serif", Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "LAMA";
                g2.drawString(txt,
                        (CardView.CARD_W - fm.stringWidth(txt)) / 2,
                        CardView.CARD_H / 2 + 5);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(CardView.CARD_W + 8, CardView.CARD_H + 8);
            }
        };
        pileGraphic.setOpaque(false);
        pileGraphic.setAlignmentX(Component.CENTER_ALIGNMENT);

        drawButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) { /* invisible */ }
        };
        drawButton.setOpaque(false);
        drawButton.setBorderPainted(false);
        drawButton.setContentAreaFilled(false);
        drawButton.setFocusPainted(false);
        drawButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        drawButton.setPreferredSize(pileGraphic.getPreferredSize());

        JLayeredPane layered = new JLayeredPane();
        layered.setPreferredSize(pileGraphic.getPreferredSize());
        layered.setOpaque(false);
        pileGraphic.setBounds(0, 0, pileGraphic.getPreferredSize().width, pileGraphic.getPreferredSize().height);
        drawButton.setBounds(0, 0, pileGraphic.getPreferredSize().width, pileGraphic.getPreferredSize().height);
        layered.add(pileGraphic, JLayeredPane.DEFAULT_LAYER);
        layered.add(drawButton, JLayeredPane.PALETTE_LAYER);
        layered.setAlignmentX(Component.CENTER_ALIGNMENT);

        piocheLabel = new JLabel(bundle.getString("draw_pile.label"), SwingConstants.CENTER);
        piocheLabel.setFont(new Font("Monospaced", Font.BOLD, 10));
        piocheLabel.setForeground(ACCENT);
        piocheLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        countLabel = new JLabel(bundle.getString("draw_pile.cards"), SwingConstants.CENTER);
        countLabel.setFont(new Font("Serif", Font.ITALIC, 12));
        countLabel.setForeground(SUB);
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(layered);
        add(Box.createVerticalStrut(6));
        add(piocheLabel);
        add(countLabel);
    }

    /**
     * Updates the number of cards remaining in the deck.
     */
    public void render(int remaining) {
        this.remaining = remaining;
        String cardsText = bundle.getString("draw_pile.card");
        countLabel.setText(remaining + " " + cardsText + (remaining > 1 ? "s" : ""));
    }

    /**
     * Activates or deactivates the draw button.
     */
    public void setDrawable(boolean drawable) {
        drawButton.setEnabled(drawable);
        drawButton.setCursor(drawable
                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : Cursor.getDefaultCursor());
    }

    /**
     * Registers an ActionListener to the draw button to handle card-drawing logic within the game controller.
     */
    public void addDrawListener(ActionListener l) {
        drawButton.addActionListener(l);
    }

    @Override  
    public void onLocaleChange(Locale locale) {  
    	this.currentLocale = locale;    
    	this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);    
    	
    	// Update all locale-dependent UI elements    
    	piocheLabel.setText(bundle.getString("draw_pile.label"));  
    	
    	// Repaint to reflect changes  
    	revalidate();
    	repaint();
    	
    }
}
    
    