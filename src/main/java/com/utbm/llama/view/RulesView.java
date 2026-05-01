package main.java.com.utbm.llama.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;
import java.util.ResourceBundle;

public class RulesView extends JPanel implements LocaleChangeListener{

    private static final Color BG       = Color.decode("#0D0D0D");
    private static final Color PANEL_BG = Color.decode("#111111");
    private static final Color ACCENT   = Color.decode("#C8A84B");
    private static final Color TEXT     = Color.decode("#F0EDE6");
    private static final Color SUB      = Color.decode("#8A8680");
    private static final Color GREEN    = Color.decode("#4CAF7D");
    private static final Color BLUE     = Color.decode("#5B9BD5");
    private static final Color RED      = Color.decode("#D4526E");
    private static final Color BORDER   = Color.decode("#2E2E2E");

    private final MainFrame mainFrame;
    private ResourceBundle bundle;
    private Locale currentLocale;

    private final JButton btnBack;
    private JPanel centerScrollHolder;

    public RulesView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        mainFrame.addLocaleChangeListener(this);
        this.currentLocale = mainFrame.getCurrentLocale();
        this.bundle = ResourceBundle.getBundle("main.resources.strings", currentLocale);

        setBackground(BG);
        setLayout(new BorderLayout());

        btnBack = buildButton(bundle.getString("rules.button.back"));

        add(buildHeader(),         BorderLayout.NORTH);
        centerScrollHolder = new JPanel(new BorderLayout());
        centerScrollHolder.setOpaque(false);
        centerScrollHolder.add(buildScrollContent(), BorderLayout.CENTER);
        add(centerScrollHolder,    BorderLayout.CENTER);
        add(buildFooter(),         BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(Color.decode("#0A0A0A"));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(28, 40, 20, 40)
        ));

        JLabel title = new JLabel(bundle.getString("rules.header.title"), SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(bundle.getString("rules.header.subtitle"), SwingConstants.CENTER);
        subtitle.setFont(new Font("Serif", Font.ITALIC, 15));
        subtitle.setForeground(SUB);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(new EmptyBorder(6, 0, 0, 0));

        header.add(title);
        header.add(subtitle);
        return header;
    }

    private JScrollPane buildScrollContent() {
        JPanel content = new JPanel();
        content.setBackground(BG);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(24, 60, 24, 60));

        content.add(buildSection(bundle.getString("rules.section.objective.title"),
                bundle.getString("rules.section.objective.text")));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.cards.title"),
                bundle.getString("rules.section.cards.text")));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.turns.title"),
                bundle.getString("rules.section.turns.text")));

        content.add(buildBulletCard(bundle.getString("rules.bullet.play.title"),
                bundle.getString("rules.bullet.play.text"),
                ACCENT));


        content.add(buildBulletCard(bundle.getString("rules.bullet.draw.title"),
                bundle.getString("rules.bullet.draw.text"),
                BLUE));

        content.add(buildBulletCard(bundle.getString("rules.bullet.pass.title"),
                bundle.getString("rules.bullet.pass.text"),
                RED));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.endround.title"),
                bundle.getString("rules.section.endround.text")));
        
        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.startround.title"),
                bundle.getString("rules.section.startround.text")));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.jury.title"),
                bundle.getString("rules.section.jury.text")));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.cesure.title"),
                bundle.getString("rules.section.cesure.text")));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.eras.title"),
                bundle.getString("rules.section.eras.text")));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.modes.title"),
                bundle.getString("rules.section.modes.text")));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection(bundle.getString("rules.section.victory.title"),
                bundle.getString("rules.section.victory.text")));

        content.add(Box.createVerticalStrut(24));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(16, 0, 24, 0)
        ));
        footer.add(btnBack);
        return footer;
    }

    private JPanel buildSection(String title, String description) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth()-1, getHeight()-1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(14, 20, 14, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        titleLabel.setForeground(ACCENT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] lines = description.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            JLabel descLabel = new JLabel("<html><body style='width:680px'>" + line + "</body></html>");
            descLabel.setFont(new Font("Serif", Font.PLAIN, 14));
            descLabel.setForeground(TEXT);
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            descLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
            if (i == 0) {
                panel.add(titleLabel);
                panel.add(Box.createVerticalStrut(4));
            }
            panel.add(descLabel);
        }

        return panel;
    }

    private JPanel buildBulletCard(String actionName, String description, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(PANEL_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 4, getHeight(), 4, 4);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(10, 18, 10, 18));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        JLabel nameLabel = new JLabel(actionName);
        nameLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        nameLabel.setForeground(accentColor);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><body style='width:660px'>" + description + "</body></html>");
        descLabel.setFont(new Font("Serif", Font.PLAIN, 13));
        descLabel.setForeground(TEXT);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(new EmptyBorder(4, 0, 0, 0));

        card.add(nameLabel);
        card.add(descLabel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(4, 20, 0, 0));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton buildButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setBackground(Color.decode("#1A1A1A"));
        btn.setForeground(TEXT);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(220, 46));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        return btn;
    }

    public void addBackListener(ActionListener l) { btnBack.addActionListener(l); }
    @Override
    public void onLocaleChange(Locale locale) {
        this.currentLocale = locale;
        this.bundle = ResourceBundle.getBundle("main.resources.strings", locale);

        btnBack.setText(bundle.getString("rules.button.back"));

        removeAll();
        add(buildHeader(), BorderLayout.NORTH);
        centerScrollHolder = new JPanel(new BorderLayout());
        centerScrollHolder.setOpaque(false);
        centerScrollHolder.add(buildScrollContent(), BorderLayout.CENTER);
        add(centerScrollHolder, BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

}