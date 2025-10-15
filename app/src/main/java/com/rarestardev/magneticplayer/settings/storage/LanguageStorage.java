package com.rarestardev.magneticplayer.settings.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class LanguageStorage {

    private final SharedPreferences preferences;

    private static final String PREF_NAME = "com.rarestardev.magneticplayer.LANGUAGE";
    private static final String PREF_KEY = "com.rarestardev.magneticplayer.LANG";

    public LanguageStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLanguage(String language) {
        preferences.edit()
                .putString(PREF_KEY, language)
                .apply();
    }

    public String getCurrentLanguage() {
        return preferences.getString(PREF_KEY, "en");
    }
}
