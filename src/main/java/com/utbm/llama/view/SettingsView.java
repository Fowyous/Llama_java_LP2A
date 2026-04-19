package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.Difficulty;
import main.java.com.utbm.llama.model.enums.GameMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Écran de configuration d'une partie.
 * Permet de choisir le nombre de joueurs, leur difficulté (bots)
 * et le mode de jeu (6 ou 10 manches).
 */
public class SettingsView extends JPanel {

    private static final Color BG = Color.decode("#0D0D0D");
    private static final Color PANEL_BG = Color.decode("#141414");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color TEXT_MAIN = Color.decode("#F0EDE6");
    private static final Color TEXT_SUB = Color.decode("#8A8680");
    private static final Color BORDER = Color.decode("#2E2E2E");

    private final JComboBox<Integer> comboNbPlayers;
    private final JComboBox<Difficulty> comboDifficulty;
    private final JComboBox<GameMode> comboGameMode;
    private final JButton btnSave;
    private final JButton btnBack;

    public SettingsView() {
        setLayout(new BorderLayout());
        setBackground(BG);

        comboNbPlayers = buildCombo(new Integer[]{2, 3, 4});
        comboDifficulty = buildCombo(Difficulty.values());
        comboGameMode = buildCombo(GameMode.values());

        btnSave = buildButton("SAUVEGARDER", true);
        btnBack = buildButton("← RETOUR", false);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(40, 60, 24, 60));

        JLabel title = new JLabel("PARAMÈTRES DE PARTIE");
        title.setFont(new Font("Monospaced", Font.BOLD, 28));
        title.setForeground(TEXT_MAIN);

        JLabel sub = new JLabel("Configurez votre cursus avant de commencer");
        sub.setFont(new Font("Serif", Font.ITALIC, 15));
        sub.setForeground(TEXT_SUB);
        sub.setBorder(new EmptyBorder(6, 0, 0, 0));

        header.add(title, BorderLayout.NORTH);
        header.add(sub, BorderLayout.SOUTH);

        JSeparator sep = new JSeparator();
        sep.setForeground(ACCENT);
        sep.setBackground(ACCENT);
        header.add(sep, BorderLayout.CENTER);

        return header;
    }

    private JPanel buildForm() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BG);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(PANEL_BG);
        form.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), new EmptyBorder(32, 40, 32, 40)));
        form.setMaximumSize(new Dimension(600, 500));

        form.add(buildFieldGroup("NOMBRE DE JOUEURS", "Entre 2 et 6 participants (humains + bots)", comboNbPlayers));
        form.add(Box.createVerticalStrut(24));

        form.add(buildFieldGroup("DIFFICULTÉ DES BOTS", "Niveau des joueurs contrôlés par l'IA", comboDifficulty));
        form.add(Box.createVerticalStrut(24));

        form.add(buildFieldGroup("MODE DE JEU", "6 manches (180 crédits) ou 10 manches (300 crédits + bonus DETEC)", comboGameMode));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 60, 0, 60);
        wrapper.add(form, gbc);

        return wrapper;
    }

    private JPanel buildFieldGroup(String label, String hint, JComponent input) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(PANEL_BG);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 12));
        lbl.setForeground(ACCENT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel hintLbl = new JLabel(hint);
        hintLbl.setFont(new Font("Serif", Font.ITALIC, 13));
        hintLbl.setForeground(TEXT_SUB);
        hintLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        hintLbl.setBorder(new EmptyBorder(2, 0, 8, 0));

        styleCombo(input);
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        group.add(lbl);
        group.add(hintLbl);
        group.add(input);

        return group;
    }

    private JPanel buildActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 24));
        actions.setBackground(BG);
        actions.add(btnBack);
        actions.add(btnSave);
        return actions;
    }

    private <T> JComboBox<T> buildCombo(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        styleCombo(combo);
        return combo;
    }

    private void styleCombo(JComponent comp) {
        comp.setBackground(Color.decode("#1A1A1A"));
        comp.setForeground(TEXT_MAIN);
        comp.setFont(new Font("Monospaced", Font.PLAIN, 14));
        if (comp instanceof JComboBox<?>) {
            ((JComboBox<?>) comp).setBorder(BorderFactory.createLineBorder(BORDER, 1));
        }
    }

    private JButton buildButton(String text, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(200, 46));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (isPrimary) {
            btn.setBackground(ACCENT);
            btn.setForeground(Color.decode("#0D0D0D"));
        } else {
            btn.setBackground(Color.decode("#1A1A1A"));
            btn.setForeground(TEXT_MAIN);
            btn.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        }
        return btn;
    }

    public void addSaveListener(ActionListener l) {
        btnSave.addActionListener(l);
    }

    public void addBackListener(ActionListener l) {
        btnBack.addActionListener(l);
    }

    public int getNbPlayers() {
        return (Integer) comboNbPlayers.getSelectedItem();
    }

    public Difficulty getDifficulty() {
        return (Difficulty) comboDifficulty.getSelectedItem();
    }

    public GameMode getGameMode() {
        return (GameMode) comboGameMode.getSelectedItem();
    }

    public void setNbPlayers(int n) {
        comboNbPlayers.setSelectedItem(n);
    }

    public void setDifficulty(Difficulty d) {
        comboDifficulty.setSelectedItem(d);
    }

    public void setGameMode(GameMode m) {
        comboGameMode.setSelectedItem(m);
    }
}
