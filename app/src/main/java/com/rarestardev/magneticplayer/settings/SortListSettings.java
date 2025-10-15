package com.rarestardev.magneticplayer.settings;

import android.content.Context;
import android.content.SharedPreferences;

public class SortListSettings {

    private static final String PREF_SORT_VIEW = "com.rarestardev.magneticplayer.SORT_VIEW";
    private static final String PREF_SORT_VIEW_KEY = "com.rarestardev.magneticplayer.SORT_VIEW_KEY";

    public static final String ALPHA = "ALPHA";
    public static final String NEWEST = "NEWEST";
    public static final String OLDEST = "OLDEST";

    private final SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SortListSettings(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_SORT_VIEW, Context.MODE_PRIVATE);
    }

    public void doInitializeData() {
        String sortView = sharedPreferences.getString(PREF_SORT_VIEW_KEY, "");
        if (sortView.isEmpty()) {
            editor = sharedPreferences.edit();
            editor.putString(PREF_SORT_VIEW_KEY, ALPHA);
            editor.apply();
        }
    }

    public void setPrefSortView(String sortView) {
        if (!sortView.isEmpty()) {
            editor = sharedPreferences.edit();
            editor.putString(PREF_SORT_VIEW_KEY, sortView);
            editor.apply();
        }
    }

    public String getPrefSortView() {
        return sharedPreferences.getString(PREF_SORT_VIEW_KEY, NEWEST);
    }
}
