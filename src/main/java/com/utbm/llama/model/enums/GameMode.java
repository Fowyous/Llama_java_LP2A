package main.java.com.utbm.llama.model.enums;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Game mode chosen before the game starts.
 * SHORT → 6 rounds, honorary threshold: 180 credits
 * LONG → 10 innings, honorary threshold: 300 credits
 * + DETEC bonus of +30 credits if ≥ 120 credits at the end of round 4
 * In both modes, the real winner is the one with the most credits
 * at the end of the last round. The thresholds are purely honorary.
 * (equivalent to being "graduated").
 */
public enum GameMode {

    /**
     * Short mode—6 innings.
     * Honorary threshold: 180 credits (= validate one’s LP at the UTBM).
     */
    SHORT(6, 180),

    /**
     * Long mode—10 innings.
     * Honorary threshold: 300 credits (= validate one’s master’s degree at the UTBM).
     * DETEC bonus available at the end of round 4.
     */
    LONG(10, 300);

    private final int maxRounds;
    private final int graduationThreshold;

    /**
     * Internal constructor for the GameMode enum.
     * Sets the duration and the graduation objective for each mode.
     *
     * @param maxRounds           the total number of rounds to be played
     * @param graduationThreshold the honorary credit goal for this mode
     */
    GameMode(int maxRounds, int graduationThreshold) {
        this.maxRounds = maxRounds;
        this.graduationThreshold = graduationThreshold;
    }

    /**
     * @return the total number of rounds in this mode.
     */
    public int getMaxRounds() {
        return maxRounds;
    }

    /**
     * @return the honorary credit threshold (diploma).
     */
    public int getGraduationThreshold() {
        return graduationThreshold;
    }

    /**
     * @return true if the DEUTEC bonus is applicable in this mode.
     * (only in LONG mode, at the end of round 4)
     */
    public boolean hasDetecBonus() {
        return this == LONG;
    }

    /**
     * Number of the round after which the DEUTEC bonus is verified.
     */
    public static final int DEUTEC_ROUND = 4;

    /**
     * Credit threshold to obtain the DETEC bonus.
     */
    public static final int DEUTEC_THRESHOLD = 120;

    /**
     * Value of the DETEC bonus in credits.
     */
    public static final int DEUTEC_BONUS = 30;

    /**
     * Provide a default string representation of the game mode in French.
     *
     * @return a descriptive string of the mode and its threshold
     */
    @Override
    public String toString() {
        return this == SHORT
                ? "Court (6 manches — 180 crédits)"
                : "Long (10 manches — 300 crédits)";
    }

    /**
     * Provide a localized string representation of the game mode.
     * Uses the provided Locale to fetch the translated label from the resource bundle.
     *
     * @param locale the locale to use for translation
     *               * @return the translated name of the game mode
     */
    public String toString(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("main.resources.strings", locale);
        return this == SHORT
                ? bundle.getString("gamemode.SIX_ROUNDS")
                : bundle.getString("gamemode.TEN_ROUNDS");
    }
}
