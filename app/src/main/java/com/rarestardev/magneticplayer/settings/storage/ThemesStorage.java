package com.rarestardev.magneticplayer.settings.storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.rarestardev.magneticplayer.utilities.Constants;

public class ThemesStorage {

    private static final String PREF_NAME = "com.rarestardev.magneticplayer.THEME_STORAGE";
    private static final String KEY_THEME_NAME = "com.rarestardev.magneticplayer.THEME_NAME";
    private static final String THEME_PREF_KEY = "com.rarestardev.magneticplayer.THEME_KEY";
    private final SharedPreferences preferences;

    public ThemesStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void doInitializationTheme() {
        String themeName = preferences.getString(KEY_THEME_NAME, "");
        int themeId = preferences.getInt(THEME_PREF_KEY, 0);
        if (themeId == 0) {
            preferences.edit().putInt(THEME_PREF_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    .apply();
        }
        if (themeName.isEmpty()) {
            preferences.edit()
                    .putString(KEY_THEME_NAME, Constants.THEMES_NAME[0])
                    .apply();
        }
    }

    public void saveNewTheme(String name) {
        preferences.edit()
                .putString(KEY_THEME_NAME, name)
                .apply();
    }

    public String getThemeName() {
        return preferences.getString(KEY_THEME_NAME, "");
    }

    public void changeTheme(int themeId) {
        if (themeId != 0) {
            preferences.edit().putInt(THEME_PREF_KEY, themeId)
                    .apply();
        }
    }

    public int getCurrentTheme() {
        return preferences.getInt(THEME_PREF_KEY, 0);
    }
}
