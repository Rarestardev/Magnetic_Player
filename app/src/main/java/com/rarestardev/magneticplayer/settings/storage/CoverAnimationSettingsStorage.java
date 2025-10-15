package com.rarestardev.magneticplayer.settings.storage;

import android.content.Context;
import android.content.SharedPreferences;

public class CoverAnimationSettingsStorage {
    private final SharedPreferences preferences;
    private static final String PREF_NAME = "com.rarestardev.magneticplayer.settings.storage.COVER_ANIMATION";
    private static final String KEY_ENABLE = "com.rarestardev.magneticplayer.settings.storage.KEY_ENABLE";
    private static final String KEY_DYNAMIC = "com.rarestardev.magneticplayer.settings.storage.KEY_DYNAMIC";

    public CoverAnimationSettingsStorage(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setKeyEnable(boolean enable) {
        preferences.edit()
                .putBoolean(KEY_ENABLE, enable)
                .apply();
    }

    public boolean getEnabledAnimation() {
        return preferences.getBoolean(KEY_ENABLE, true);
    }

    public void setKeyDynamic(boolean enable) {
        preferences.edit()
                .putBoolean(KEY_DYNAMIC, enable)
                .apply();
    }

    public boolean getEnabledDynamic() {
        return preferences.getBoolean(KEY_DYNAMIC, true);
    }
}
