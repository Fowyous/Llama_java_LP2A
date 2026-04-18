package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.CardType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Composant visuel représentant une carte du jeu LAMA.
 * Peut être :
 *  - face visible  (type connu)
 *  - face cachée   (mode jury ou dos de pioche)
 *  - sélectionnable (dans HandView ou JuryView)
 */
public class CardView extends JPanel {

    public static final int CARD_W = 80;
    public static final int CARD_H = 120;
    private static final int ARC   = 12;

    private static final Color CARD_BG      = Color.decode("#FAF5E4");
    private static final Color CARD_BACK_BG = Color.decode("#1A3A5C");
    private static final Color LLAMA_COLOR  = Color.decode("#D4526E");
    private static final Color NUMBER_COLOR = Color.decode("#1A1A2E");
    private static final Color SELECTED_BDR = Color.decode("#C8A84B");
    private static final Color HOVER_BDR    = Color.decode("#8A6E30");
    private static final Color DEFAULT_BDR  = Color.decode("#C8C0A0");
    private static final Color SHADOW       = new Color(0, 0, 0, 80);

    private final CardType type;
    private boolean faceDown    = false;
    private boolean selected    = false;
    private boolean hovering    = false;
    private boolean selectable  = false;

    /** Carte face visible. */
    public CardView(CardType type) {
        this.type = type;
        init();
    }

    /** Carte face cachée (dos). */
    public CardView() {
        this.type     = null;
        this.faceDown = true;
        init();
    }

    private void init() {
        setOpaque(false);
        setPreferredSize(new Dimension(CARD_W, CARD_H));
        setMinimumSize(new Dimension(CARD_W, CARD_H));
        setMaximumSize(new Dimension(CARD_W, CARD_H));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (selectable) { hovering = true; repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                hovering = false; repaint();
            }
            @Override public void mousePressed(MouseEvent e) {
                if (selectable) { selected = !selected; repaint(); }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int offsetY = (selected || hovering) ? -6 : 0;
        int w = getWidth() - 4;
        int h = getHeight() - 8;
        int x = 2;
        int y = 4 + offsetY;

        g2.setColor(SHADOW);
        g2.fill(new RoundRectangle2D.Float(x + 2, y + 4, w, h, ARC, ARC));

        if (faceDown) {
            drawBack(g2, x, y, w, h);
        } else {
            drawFace(g2, x, y, w, h);
        }

        if (selected) {
            g2.setColor(SELECTED_BDR);
            g2.setStroke(new BasicStroke(2.5f));
            g2.draw(new RoundRectangle2D.Float(x, y, w, h, ARC, ARC));
        } else if (hovering) {
            g2.setColor(HOVER_BDR);
            g2.setStroke(new BasicStroke(1.5f));
            g2.draw(new RoundRectangle2D.Float(x, y, w, h, ARC, ARC));
        }

        g2.dispose();
    }

    private void drawFace(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(CARD_BG);
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, ARC, ARC));

        g2.setColor(DEFAULT_BDR);
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(x, y, w, h, ARC, ARC));

        if (type == CardType.LLAMA) {
            drawLlama(g2, x, y, w, h);
        } else {
            drawNumber(g2, x, y, w, h);
        }
    }

    private void drawNumber(Graphics2D g2, int x, int y, int w, int h) {
        String val = String.valueOf(type.getValue());
        g2.setColor(NUMBER_COLOR);

        g2.setFont(new Font("Serif", Font.BOLD, 42));
        FontMetrics fm = g2.getFontMetrics();
        int tx = x + (w - fm.stringWidth(val)) / 2;
        int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(val, tx, ty);

        g2.setFont(new Font("Monospaced", Font.BOLD, 11));
        g2.drawString(val, x + 6, y + 16);
        g2.drawString(val, x + w - 6 - g2.getFontMetrics().stringWidth(val), y + h - 6);
    }

    private void drawLlama(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(212, 82, 110, 30));
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, ARC, ARC));

        g2.setColor(LLAMA_COLOR);
        g2.setFont(new Font("Serif", Font.BOLD, 36));
        FontMetrics fm = g2.getFontMetrics();
        String txt = "🦙";
        int tx = x + (w - fm.stringWidth(txt)) / 2;
        int ty = y + (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(txt, tx, ty);

        g2.setFont(new Font("Monospaced", Font.BOLD, 9));
        g2.drawString("LAMA", x + 6, y + 15);
        g2.drawString("10", x + w - 18, y + 15);
    }

    private void drawBack(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(CARD_BACK_BG);
        g2.fill(new RoundRectangle2D.Float(x, y, w, h, ARC, ARC));

        g2.setColor(new Color(255, 255, 255, 20));
        g2.setStroke(new BasicStroke(1f));
        int step = 12;
        for (int i = x; i < x + w; i += step) {
            g2.drawLine(i, y, i, y + h);
        }
        for (int j = y; j < y + h; j += step) {
            g2.drawLine(x, j, x + w, j);
        }

        g2.setColor(Color.decode("#C8A84B"));
        g2.setFont(new Font("Serif", Font.BOLD, 14));
        String logo = "LAMA";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(logo, x + (w - fm.stringWidth(logo)) / 2, y + h / 2 + fm.getAscent() / 2);
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
        setCursor(selectable
                ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                : Cursor.getDefaultCursor());
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
        repaint();
    }

    public boolean isSelected() { return selected; }
    public CardType getCardType() { return type; }
}