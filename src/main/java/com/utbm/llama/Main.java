package main.java.com.utbm.llama;

import main.java.com.utbm.llama.controller.GameController;
import main.java.com.utbm.llama.view.MainFrame;

import javax.swing.*;

import java.util.Locale;

/**
 * 
 *  Entry point of the LAMA UTBM application.
 *  Launches the main window and initializes the GameController
 *  which orchestrates all sub-controllers.
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
            new GameController(mainFrame, locale);
            mainFrame.setVisible(true);
        });
    }
}