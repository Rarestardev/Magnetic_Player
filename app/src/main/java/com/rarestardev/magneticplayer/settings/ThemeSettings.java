package com.rarestardev.magneticplayer.settings;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeSettings {

    private static final String THEME_PREF_NAME = "com.rarestardev.magneticplayer.THEME";
    private static final String THEME_PREF_KEY = "com.rarestardev.magneticplayer.THEME_KEY";

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public ThemeSettings(Context context){
        sharedPreferences = context.getSharedPreferences(THEME_PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void doInitialization(){
        int themeId = sharedPreferences.getInt(THEME_PREF_KEY,0);
        if (themeId == 0){
            editor.putInt(THEME_PREF_KEY, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    .apply();
        }
    }

    public void changeTheme(int themeId){
        if (themeId != 0){
            editor.putInt(THEME_PREF_KEY, themeId)
                    .apply();
        }
    }

    public int getCurrentTheme(){
        return sharedPreferences.getInt(THEME_PREF_KEY,0);
    }
}
