package main.java.com.utbm.llama.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class RulesView extends JPanel {

    private static final Color BG       = Color.decode("#0D0D0D");
    private static final Color PANEL_BG = Color.decode("#111111");
    private static final Color ACCENT   = Color.decode("#C8A84B");
    private static final Color TEXT     = Color.decode("#F0EDE6");
    private static final Color SUB      = Color.decode("#8A8680");
    private static final Color GREEN    = Color.decode("#4CAF7D");
    private static final Color BLUE     = Color.decode("#5B9BD5");
    private static final Color RED      = Color.decode("#D4526E");
    private static final Color BORDER   = Color.decode("#2E2E2E");

    private final JButton btnBack;

    public RulesView() {
        setBackground(BG);
        setLayout(new BorderLayout());

        btnBack = buildButton("← RETOUR AU MENU");

        add(buildHeader(),         BorderLayout.NORTH);
        add(buildScrollContent(),  BorderLayout.CENTER);
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

        JLabel title = new JLabel("RÈGLES DU JEU", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 32));
        title.setForeground(ACCENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("LAMA — Édition UTBM", SwingConstants.CENTER);
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

        content.add(buildSection("🎯  OBJECTIF",
                "Terminer la partie avec le plus de crédits possible. " +
                        "Les seuils honorifiques sont 180 crédits (mode court) et 300 crédits (mode long)."));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("💳  LES CARTES",
                "Le jeu comporte 7 types de cartes : 1, 2, 3, 4, 5, 6 et LAMA (valeur 10). " +
                        "Il y a 8 exemplaires de chaque carte, soit 56 cartes en tout."));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("🔄  DÉROULEMENT D'UN TOUR",
                "À chaque tour, vous pouvez effectuer l'une des trois actions suivantes :"));

        content.add(buildBulletCard("JOUER UNE CARTE",
                "Posez une carte de votre main sur la défausse. " +
                        "Elle doit avoir la même valeur que la carte du dessus, " +
                        "ou la valeur immédiatement supérieure (ex : 3 sur 2). " +
                        "Cas spéciaux : LAMA se joue sur un 6, et 1 se joue sur un LAMA (cycle).",
                ACCENT));

        content.add(buildBulletCard("PIOCHER UNE CARTE",
                "Prenez une carte depuis la pioche et ajoutez-la à votre main. " +
                        "Votre tour se termine immédiatement.",
                BLUE));

        content.add(buildBulletCard("PASSER LA MANCHE",
                "Vous quittez la manche en cours. Vous ne jouerez plus jusqu'à la prochaine manche. " +
                        "Attention : les cartes restantes dans votre main vous seront déduites !",
                RED));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("📊  FIN DE MANCHE",
                "La manche se termine quand tous les joueurs ont passé ou qu'un joueur vide sa main. " +
                        "La valeur totale des cartes encore en main est déduite des crédits de chaque joueur."));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("🔄  DÉBUT DE MANCHE",
                "Au début de chaque manche, tous les joueurs reçoivent +35 crédits, " +
                        "y compris les joueurs en semestre de césure."));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("⚖  LE JURY",
                "Si vous perdez 20 crédits ou plus en une seule manche, vous êtes convoqué devant le jury. " +
                        "Vous choisissez une carte parmi 7 cartes face cachée (valeurs 1 à 6 et LAMA). " +
                        "Vous gagnez la valeur de la carte choisie en crédits."));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("✈  SEMESTRE DE CÉSURE",
                "Si vos crédits sont négatifs après le jury, vous partez en semestre de césure. " +
                        "Vous sautez la prochaine manche mais recevez quand même les 35 crédits du début de manche. " +
                        "En mode 1v1, si les deux joueurs sont en césure, les deux sautent la manche simultanément."));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("🌍  SEMESTRE À L'ÉTRANGER",
                "Si vous terminez une manche sans aucune carte en main, " +
                        "vous partez en semestre à l'étranger. " +
                        "À la manche suivante, vous commencez avec seulement 4 cartes au lieu de 6. " +
                        "Cet avantage n'est pas cumulable et ne dure qu'une seule manche."));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("🎓  MODES DE JEU",
                "Mode Court : 6 manches. Seuil honorifique : 180 crédits (équivalent d'une licence à l'UTBM).\n" +
                        "Mode Long : 10 manches. Seuil honorifique : 300 crédits (master). " +
                        "Bonus DETEC : si vous avez ≥ 120 crédits à la fin de la manche 4, vous recevez +30 crédits."));

        content.add(Box.createVerticalStrut(16));

        content.add(buildSection("🏆  VICTOIRE",
                "À la fin de la dernière manche, le joueur avec le plus de crédits remporte la partie. " +
                        "Les seuils de 180 et 300 crédits sont honorifiques : ils signalent que vous êtes " +
                        "\"diplômé\" mais ne déterminent pas le gagnant."));

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

        for (String line : description.split("\n")) {
            JLabel descLabel = new JLabel("<html><body style='width:680px'>" + line + "</body></html>");
            descLabel.setFont(new Font("Serif", Font.PLAIN, 14));
            descLabel.setForeground(TEXT);
            descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            descLabel.setBorder(new EmptyBorder(4, 0, 0, 0));
            if (line.equals(description.split("\n")[0])) {
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
}