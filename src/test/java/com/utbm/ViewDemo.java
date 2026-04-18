package test.java.com.utbm;

import test.java.com.utbm.modeltest.*;
import main.java.com.utbm.llama.model.enums.CardType;
import main.java.com.utbm.llama.model.enums.Difficulty;
import main.java.com.utbm.llama.model.enums.GameMode;
import main.java.com.utbm.llama.model.enums.State;
import main.java.com.utbm.llama.view.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════════
 *  LAMA UTBM — Démo interactive de la couche View
 * ══════════════════════════════════════════════════════════════════
 * Lance une fenêtre avec tous les écrans navigables via une barre
 * de contrôle en bas. Simule des données réalistes pour chaque vue.
 * Écrans testés :
 *   1. MenuView        — écran d'accueil
 *   2. SettingsView    — paramètres (joueurs, difficulté, mode)
 *   3. BoardView       — plateau de jeu complet (tour normal)
 *   4. BoardView       — état "Semestre à l'étranger" + joueur qui passe
 *   5. JuryView        — mini-jeu du jury (sélection + révélation)
 *   6. CesureView      — écran de semestre de césure
 */

public class ViewDemo {

    // ── Palette barre de contrôle ─────────────────────────────────────────────
    private static final Color CTRL_BG  = Color.decode("#050505");
    private static final Color CTRL_BTN = Color.decode("#1E1E1E");
    private static final Color ACCENT   = Color.decode("#C8A84B");
    private static final Color TEXT     = Color.decode("#F0EDE6");
    private static final Color SUB      = Color.decode("#8A8680");

    // ── Composants globaux ────────────────────────────────────────────────────
    private static MainFrame    frame;
    private static JLabel       statusLabel;
    private static int          currentScenario = 0;

    // ── Données de démo ───────────────────────────────────────────────────────
    private static Game         game;
    private static Player       localPlayer;
    private static Player       bot1;
    private static Player       bot2;

    // ─────────────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // Look & Feel natif pour un meilleur rendu Swing
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        // Force le rendu sur l'EDT
        SwingUtilities.invokeLater(ViewDemo::launch);
    }

    private static void launch() {
        initDemoData();

        frame = new MainFrame();

        // Ajout de la barre de contrôle en bas
        JPanel controlBar = buildControlBar();
        frame.getContentPane().add(controlBar, BorderLayout.SOUTH);

        frame.setVisible(true);

        // Scénario 1 par défaut
        showScenario(0);
    }

    // ── Initialisation des données de démo ────────────────────────────────────

    private static void initDemoData() {
        localPlayer = new Player("Vous");
        localPlayer.addCard(CardType.THREE);
        localPlayer.addCard(CardType.THREE);
        localPlayer.addCard(CardType.FOUR);
        localPlayer.addCard(CardType.LLAMA);
        localPlayer.addCard(CardType.ONE);
        localPlayer.addCard(CardType.SIX);
        localPlayer.setCredits(78);

        bot1 = new Bot("IA — Marie", Difficulty.MEDIUM);
        bot1.addCard(CardType.TWO);
        bot1.addCard(CardType.FIVE);
        bot1.addCard(CardType.LLAMA);
        bot1.setCredits(112);

        bot2 = new Bot("IA — Paul", Difficulty.HARD);
        bot2.addCard(CardType.ONE);
        bot2.addCard(CardType.ONE);
        bot2.addCard(CardType.TWO);
        bot2.addCard(CardType.SIX);
        bot2.setCredits(35);

        game = new Game(List.of(localPlayer, bot1, bot2), GameMode.SHORT);

        // Simule une carte en défausse
        game.getDiscardPile().add(CardType.THREE);
    }

    // ── Scénarios ─────────────────────────────────────────────────────────────

    private static final String[] SCENARIO_NAMES = {
            "1 · Menu principal",
            "2 · Paramètres",
            "3 · Plateau — Tour normal",
            "4 · Plateau — États spéciaux",
            "5 · Jury — Sélection",
            "6 · Césure"
    };

    private static void showScenario(int index) {
        currentScenario = index;
        updateStatusLabel();

        switch (index) {
            case 0 -> showMenu();
            case 1 -> showSettings();
            case 2 -> showBoardNormal();
            case 3 -> showBoardSpecialStates();
            case 4 -> showJury();
            case 5 -> showCesure();
        }
    }

    // ── Scénario 1 : Menu ─────────────────────────────────────────────────────

    private static void showMenu() {
        frame.getMenuView().addStartListener(e ->
                showScenario(2));
        frame.getMenuView().addSettingsListener(e ->
                showScenario(1));
        frame.getMenuView().addQuitListener(e ->
                System.exit(0));

        frame.showMenu();
        log("Menu affiché — cliquez sur les boutons pour naviguer");
    }

    // ── Scénario 2 : Settings ─────────────────────────────────────────────────

    private static void showSettings() {
        SettingsView sv = frame.getSettingsView();
        sv.setNbPlayers(3);
        sv.setDifficulty(Difficulty.MEDIUM);
        sv.setGameMode(GameMode.SHORT);

        sv.addSaveListener(e -> {
            int nb   = sv.getNbPlayers();
            Difficulty d = sv.getDifficulty();
            GameMode m   = sv.getGameMode();
            log("✔ Sauvegardé — " + nb + " joueurs | " + d + " | " + m);
            showScenario(2);
        });
        sv.addBackListener(e -> showScenario(0));

        frame.showSettings();
        log("Paramètres — modifiez puis cliquez Sauvegarder");
    }

    // ── Scénario 3 : Plateau normal ───────────────────────────────────────────

    private static void showBoardNormal() {
        // Réinitialise les états
        localPlayer.changeState(State.PLAYING);
        localPlayer.setSuspended(false);
        localPlayer.setStudyAbroad(false);
        bot1.changeState(State.PLAYING);
        bot2.changeState(State.PLAYING);

        BoardView board = new BoardView();

        // ── Listeners actions ──────────────────────────────────────────────
        board.addDrawListener(e -> {
            CardType drawn = game.getDrawPile().draw();
            if (drawn != null) {
                localPlayer.addCard(drawn);
                board.updateBoard(game, localPlayer);
                log("🃏 Carte piochée : " + drawn.name() + " (valeur " + drawn.getValue() + ")");
            } else {
                log("⚠ Pioche vide !");
            }
        });

        board.addQuitRoundListener(e -> {
            localPlayer.changeState(State.QUITTING);
            board.updateBoard(game, localPlayer);
            log("🚪 Vous avez passé la manche");
        });

        board.getLocalPlayerView(); // pré-warm
        board.updateBoard(game, localPlayer);

        // Listener carte jouée
        board.getLocalPlayerView().getHandView().setOnCardPlayed(card -> {
            localPlayer.removeCard(card);
            game.getDiscardPile().add(card);
            board.updateBoard(game, localPlayer);
            log("✅ Carte jouée : " + card.name() + " (valeur " + card.getValue() + ")");
        });

        frame.showGame(board);
        log("Plateau — " + localPlayer.getHand().size() + " cartes en main | Pioche : "
                + game.getDrawPile().size() + " cartes");
    }

    // ── Scénario 4 : États spéciaux ───────────────────────────────────────────

    private static void showBoardSpecialStates() {
        // Simule : bot1 passe la manche, bot2 en étude à l'étranger (4 cartes)
        bot1.changeState(State.QUITTING);
        bot2.setStudyAbroad(true);
        localPlayer.setCredits(12);   // crédits bas → badge orange/rouge

        BoardView board = new BoardView();
        board.addDrawListener(e -> {
            log("Pioche désactivée dans ce scénario de démo");
        });
        board.addQuitRoundListener(e -> {
            log("Quitter désactivé dans ce scénario de démo");
        });
        board.updateBoard(game, localPlayer);

        frame.showGame(board);
        log("États spéciaux — bot1 a passé | bot2 en semestre à l'étranger | vos crédits sont bas");
    }

    // ── Scénario 5 : Jury ─────────────────────────────────────────────────────

    private static void showJury() {
        JuryView jury = new JuryView();
        jury.setup("Vous", 24, -3);

        jury.setOnCardPicked(idx -> {
            // Simule la révélation (cycle sur les CardType)
            CardType[] all = CardType.values();
            CardType revealed = all[idx % all.length];
            jury.revealCard(idx, revealed);
            int gained = revealed.getValue();
            localPlayer.addCredits(gained);
            log("🎲 Carte révélée : " + revealed.name() + " → +" + gained + " crédits ! (total : " + localPlayer.getCredits() + ")");
        });

        frame.showJury(jury);
        log("Jury — cliquez sur une carte pour la choisir, puis Valider");
    }

    // ── Scénario 6 : Césure ───────────────────────────────────────────────────

    private static void showCesure() {
        localPlayer.setCredits(-8);
        localPlayer.setSuspended(true);

        CesureView cesure = new CesureView();
        cesure.setup("Vous", localPlayer.getCredits(), 3, false);
        cesure.addContinueListener(e -> {
            // Simule le début de la manche suivante
            localPlayer.addCredits(35);
            localPlayer.setSuspended(false);
            log("▶ Manche 3 démarrée — +35 crédits → total : " + localPlayer.getCredits());
            showScenario(2);
        });

        frame.showCesure(cesure);
        log("Césure — crédits négatifs (" + localPlayer.getCredits() + ") | cliquez Continuer pour reprendre");
    }

    // ── Barre de contrôle ─────────────────────────────────────────────────────

    private static JPanel buildControlBar() {
        JPanel bar = new JPanel(new BorderLayout(0, 0));
        bar.setBackground(CTRL_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.decode("#2A2A2A")),
                new EmptyBorder(8, 16, 8, 16)
        ));

        // Boutons de navigation rapide
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnPanel.setOpaque(false);

        JLabel navLabel = new JLabel("ÉCRANS :");
        navLabel.setFont(new Font("Monospaced", Font.BOLD, 10));
        navLabel.setForeground(SUB);
        btnPanel.add(navLabel);

        for (int i = 0; i < SCENARIO_NAMES.length; i++) {
            final int idx = i;
            JButton btn = new JButton(SCENARIO_NAMES[i]);
            btn.setFont(new Font("Monospaced", Font.PLAIN, 11));
            btn.setBackground(CTRL_BTN);
            btn.setForeground(TEXT);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.decode("#2E2E2E"), 1),
                    new EmptyBorder(4, 10, 4, 10)
            ));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                initDemoData();    // reset des données à chaque navigation
                showScenario(idx);
            });
            btnPanel.add(btn);
        }

        // Zone de log
        statusLabel = new JLabel("Prêt.");
        statusLabel.setFont(new Font("Monospaced", Font.ITALIC, 11));
        statusLabel.setForeground(ACCENT);
        statusLabel.setBorder(new EmptyBorder(0, 16, 0, 0));

        bar.add(btnPanel,     BorderLayout.WEST);
        bar.add(statusLabel,  BorderLayout.CENTER);

        // Indicateur de scénario actif (à droite)
        JLabel scenLabel = new JLabel("LAMA UTBM · View Demo");
        scenLabel.setFont(new Font("Monospaced", Font.PLAIN, 10));
        scenLabel.setForeground(SUB);
        bar.add(scenLabel, BorderLayout.EAST);

        return bar;
    }

    // ── Logger ────────────────────────────────────────────────────────────────

    private static void log(String msg) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(msg));
        System.out.println("[DEMO] " + msg);
    }

    private static void updateStatusLabel() {
        if (statusLabel != null) {
            log("→ " + SCENARIO_NAMES[currentScenario]);
        }
    }
}
