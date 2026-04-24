package main.java.com.utbm.llama.view;

import main.java.com.utbm.llama.model.enums.Difficulty;
import main.java.com.utbm.llama.model.enums.GameMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Écran de configuration d'une partie.
 * Permet de choisir le nombre de joueurs, leur difficulté (bots)
 * et le mode de jeu (6 ou 10 manches).
 */
public class SettingsView extends JPanel implements LocaleChangeListener{

	
    private static final Color BG = Color.decode("#0D0D0D");
    private static final Color PANEL_BG = Color.decode("#141414");
    private static final Color ACCENT = Color.decode("#C8A84B");
    private static final Color TEXT_MAIN = Color.decode("#F0EDE6");
    private static final Color TEXT_SUB = Color.decode("#8A8680");
    private static final Color BORDER = Color.decode("#2E2E2E");

    private final JComboBox<Integer> comboNbPlayers;
    private final JComboBox<Difficulty> comboDifficulty;
    private final JComboBox<GameMode> comboGameMode;
    private final JComboBox<String> comboLanguage;
    private final JButton btnSave;
    private final JButton btnBack;

    private ResourceBundle bundle;
    private Locale currentLocale;
    
    private JLabel lblNbPlayers;    
    private JLabel hintNbPlayers;   
    private JLabel lblDifficulty;    
    private JLabel hintDifficulty;   
    private JLabel lblMode;    
    private JLabel hintMode;    
    private JLabel lblLanguage;    
    private JLabel hintLanguage;
    private JLabel title;
    private JLabel sub;
    private JLabel lbl, hintLbl;
    public SettingsView(MainFrame mainFrame) {
        mainFrame.addLocaleChangeListener(this);

    	this.bundle = ResourceBundle.getBundle("main.resources.strings", mainFrame.getCurrentLocale());

        setLayout(new BorderLayout());
        setBackground(BG);

        comboNbPlayers = buildCombo(new Integer[]{2, 3, 4});
        comboDifficulty = buildCombo(Difficulty.values());
        comboGameMode = buildCombo(GameMode.values());
        comboLanguage = buildCombo(new String[]{"English", "Français", "Deutsch"});
        setLanguageSelection(mainFrame.getCurrentLocale());
        btnSave = buildButton(bundle.getString("settings.save"), true);
        btnBack = buildButton(bundle.getString("settings.back"), false);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildForm(), BorderLayout.CENTER);
        add(buildActions(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(40, 60, 24, 60));

        title = new JLabel(bundle.getString("settings.title"));
        title.setFont(new Font("Monospaced", Font.BOLD, 28));
        title.setForeground(TEXT_MAIN);

        sub = new JLabel(bundle.getString("settings.subtitle"));
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

        form.add(buildFieldGroupNbPlayers());
        form.add(Box.createVerticalStrut(24));

        form.add(buildFieldGroupDifficulty());
        form.add(Box.createVerticalStrut(24));

        form.add(buildFieldGroupMode());

        form.add(buildFieldGroupLanguage());
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

        lbl = new JLabel(label);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 12));
        lbl.setForeground(ACCENT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        hintLbl = new JLabel(hint);
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
    private JPanel buildFieldGroupNbPlayers() {  
    	JPanel group = new JPanel();        
    	group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));        
    	group.setBackground(PANEL_BG);        
    	group.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	lblNbPlayers = new JLabel(bundle.getString("settings.nb_players.label"));        
    	lblNbPlayers.setFont(new Font("Monospaced", Font.BOLD, 12));        
    	lblNbPlayers.setForeground(ACCENT);      
    	lblNbPlayers.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	hintNbPlayers = new JLabel(bundle.getString("settings.nb_players.hint"));        
    	hintNbPlayers.setFont(new Font("Serif", Font.ITALIC, 13));        
    	hintNbPlayers.setForeground(TEXT_SUB);        
    	hintNbPlayers.setAlignmentX(Component.LEFT_ALIGNMENT);        
    	hintNbPlayers.setBorder(new EmptyBorder(2, 0, 8, 0));
    	
    	styleCombo(comboNbPlayers);        	
    	comboNbPlayers.setAlignmentX(Component.LEFT_ALIGNMENT);        
    	comboNbPlayers.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    	group.add(lblNbPlayers);        
    	group.add(hintNbPlayers);        
    	group.add(comboNbPlayers);
    
    	return group;
    	}

    private JPanel buildFieldGroupDifficulty() {    
    	JPanel group = new JPanel();      
    	group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));     
    	group.setBackground(PANEL_BG);    
    	group.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	lblDifficulty = new JLabel(bundle.getString("settings.difficulty.label"));      
    	lblDifficulty.setFont(new Font("Monospaced", Font.BOLD, 12));     
    	lblDifficulty.setForeground(ACCENT);     
    	lblDifficulty.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	hintDifficulty = new JLabel(bundle.getString("settings.difficulty.hint"));      
    	hintDifficulty.setFont(new Font("Serif", Font.ITALIC, 13));   
    	hintDifficulty.setForeground(TEXT_SUB);    
    	hintDifficulty.setAlignmentX(Component.LEFT_ALIGNMENT);  
    	hintDifficulty.setBorder(new EmptyBorder(2, 0, 8, 0));
    	
    	styleCombo(comboDifficulty);    
    	comboDifficulty.setAlignmentX(Component.LEFT_ALIGNMENT);   
    	comboDifficulty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    	group.add(lblDifficulty);      
    	group.add(hintDifficulty);     
    	group.add(comboDifficulty);
    	return group; 
    	}
    
    private JPanel buildFieldGroupLanguage() {  
    	JPanel group = new JPanel();  
    	group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
    	group.setBackground(PANEL_BG);      
    	group.setAlignmentX(Component.LEFT_ALIGNMENT);
    	lblLanguage = new JLabel(bundle.getString("settings.language.label"));    
    	lblLanguage.setFont(new Font("Monospaced", Font.BOLD, 12)); 
    	lblLanguage.setForeground(ACCENT);  
    	lblLanguage.setAlignmentX(Component.LEFT_ALIGNMENT);
   
    	hintLanguage = new JLabel(bundle.getString("settings.language.hint"));  
    	hintLanguage.setFont(new Font("Serif", Font.ITALIC, 13));     
    	hintLanguage.setForeground(TEXT_SUB); 
    	hintLanguage.setAlignmentX(Component.LEFT_ALIGNMENT);      
    	hintLanguage.setBorder(new EmptyBorder(2, 0, 8, 0));
   
    	styleCombo(comboLanguage);  
    	comboLanguage.setAlignmentX(Component.LEFT_ALIGNMENT);    
    	comboLanguage.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    
    	group.add(lblLanguage);      
    	group.add(hintLanguage);     
    	group.add(comboLanguage);
    
    	return group;    
    	}
    private JPanel buildFieldGroupMode() { 
    	JPanel group = new JPanel();  
    	group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));  
    	group.setBackground(PANEL_BG);   
    	group.setAlignmentX(Component.LEFT_ALIGNMENT);
    	
    	lblMode = new JLabel(bundle.getString("settings.mode.label"));   
    	lblMode.setFont(new Font("Monospaced", Font.BOLD, 12));     
    	lblMode.setForeground(ACCENT);     
    	lblMode.setAlignmentX(Component.LEFT_ALIGNMENT);
  
    	hintMode = new JLabel(bundle.getString("settings.mode.hint"));     
    	hintMode.setFont(new Font("Serif", Font.ITALIC, 13));   
    	hintMode.setForeground(TEXT_SUB);   
    	hintMode.setAlignmentX(Component.LEFT_ALIGNMENT);      
    	hintMode.setBorder(new EmptyBorder(2, 0, 8, 0));
    
    	styleCombo(comboGameMode);    
    	comboGameMode.setAlignmentX(Component.LEFT_ALIGNMENT);       
    	comboGameMode.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    	group.add(lblMode);     
    	group.add(hintMode);   
    	group.add(comboGameMode);
    	
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

    public void addLanguageChangeListener(ActionListener l) {
        comboLanguage.addActionListener(l);
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
    public Locale getSelectedLanguage() {
        String selected = (String) comboLanguage.getSelectedItem();
        return switch (selected) {
            case "English" -> Locale.of("en");
            case "Français" -> Locale.of("fr");
            case "Deutsch" -> Locale.of("de");
            default -> currentLocale;
        };
    }
    
    private void setLanguageSelection(Locale locale) {
        String language = switch (locale.getLanguage()) {
            case "en" -> "English";
            case "fr" -> "Français";
            case "de" -> "Deutsch";
            case "es" -> "Español";
            default -> "English";
        };
        comboLanguage.setSelectedItem(language);
    }
    @Override    
    public void onLocaleChange(Locale locale) {
        this.currentLocale = locale;
        ResourceBundle newBundle = ResourceBundle.getBundle("main.resources.strings", locale);
        bundle = newBundle;
        
        // update header.
        title.setText(newBundle.getString("settings.title"));
        sub.setText(newBundle.getString("settings.subtitle"));
        //update all groups
        lblNbPlayers.setText(newBundle.getString("settings.nb_players.label"));  
        hintNbPlayers.setText(newBundle.getString("settings.nb_players.hint"));
        lblDifficulty.setText(newBundle.getString("settings.difficulty.label"));  
        hintDifficulty.setText(newBundle.getString("settings.difficulty.hint"));
        lblMode.setText(newBundle.getString("settings.mode.label"));   
        
        hintMode.setText(newBundle.getString("settings.mode.hint"));
        lblLanguage.setText(newBundle.getString("settings.language.label"));    
        hintLanguage.setText(newBundle.getString("settings.language.hint"));
        // Update buttons       
        btnSave.setText(newBundle.getString("settings.save"));     
        btnBack.setText(newBundle.getString("settings.back"));
        // Update language selection display        
        setLanguageSelection(locale);
        
    }

}
