package main.java.com.utbm.llama;

import main.java.com.utbm.llama.controller.GameController;
import main.java.com.utbm.llama.view.MainFrame;

import javax.swing.*;

import java.util.Locale;
/**
 * Point d'entrée de l'application LAMA UTBM.
 * Lance la fenêtre principale et initialise le GameController
 * qui orchestre tous les sous-contrôleurs.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
        	Locale locale = Locale.ENGLISH;//default language.
            MainFrame mainFrame = new MainFrame(locale);
            new GameController(mainFrame);
            mainFrame.setVisible(true);
        });
    }
}