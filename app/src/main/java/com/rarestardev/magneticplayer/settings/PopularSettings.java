package com.rarestardev.magneticplayer.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class PopularSettings {

    private static final String PREF_SHOW_POPULAR = "com.rarestardev.magneticplayer.PREF_SHOW_POPULAR";
    private static final String PREF_SHOW_POPULAR_KEY = "com.rarestardev.magneticplayer.PREF_SHOW_POPULAR_KEY";

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public static final int[] SELECTION = {1, 2};

    public PopularSettings(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_SHOW_POPULAR, Context.MODE_PRIVATE);
    }

    public void doInitialization() {
        int defaultValue = sharedPreferences.getInt(PREF_SHOW_POPULAR_KEY, 0);

        if (defaultValue == 0) {
            editor = sharedPreferences.edit();
            editor.putInt(PREF_SHOW_POPULAR_KEY, SELECTION[0]);
            editor.apply();
        }
    }

    public void showHidePopularValue(int value) {
        if (value != 0) {
            editor = sharedPreferences.edit();
            editor.putInt(PREF_SHOW_POPULAR_KEY, value);
            editor.apply();
        }
    }

    public int getShowHidePopularValue(){
        return sharedPreferences.getInt(PREF_SHOW_POPULAR_KEY,SELECTION[0]);
    }
}
