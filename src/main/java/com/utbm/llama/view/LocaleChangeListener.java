package main.java.com.utbm.llama.view;

import java.util.Locale;

public interface LocaleChangeListener {

    /**
     * Triggered when the application language is updated,
	 * allowing the implementing component to refresh its translated text.
     */
    void onLocaleChange(Locale locale);
}
