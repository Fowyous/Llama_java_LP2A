package main.java.com.utbm.llama;

import main.java.com.utbm.llama.controller.GameController;
import main.java.com.utbm.llama.view.MainFrame;

import javax.swing.*;

import java.util.Locale;

/**
 * Entry point of the LAMA UTBM application.
 * Launch the main window and initialize the GameController
 * which orchestrates all the subcontrollers.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
        	Locale locale = Locale.ENGLISH;
            MainFrame mainFrame = new MainFrame(locale);
            new GameController(mainFrame, locale);
            mainFrame.setVisible(true);
        });
    }
}