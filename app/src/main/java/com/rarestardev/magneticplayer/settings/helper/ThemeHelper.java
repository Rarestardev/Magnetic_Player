package com.rarestardev.magneticplayer.settings.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.TypedValue;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.settings.storage.ThemesStorage;

public class ThemeHelper {

    /**
     * Extracts a color from the current theme.
     * @param context The context with the active theme.
     * @param attrResId The attribute resource ID (e.g. R.attr.colorPrimary).
     * @return The resolved color as int.
     */

    public static int resolveThemeColor(Context context, int attrResId) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attrResId, typedValue, true)) {
            return typedValue.data;
        }
        return Color.BLACK;
    }

    public static int themeColorManager(Context context){
        ThemesStorage storage = new ThemesStorage(context);
        int color = context.getColor(R.color.base_color_theme);
        if (storage.getThemeName() != null) {
            switch (storage.getThemeName()) {
                case "BaseColor":
                    color = context.getColor(R.color.base_color_theme);
                    break;
                case "Orange":
                    color = context.getColor(R.color.orange_color_theme);
                    break;
                case "DarkSlate":
                    color = context.getColor(R.color.dark_slate_gray_theme);
                    break;
                case "Red":
                    color = context.getColor(R.color.red_color_theme);
                    break;
                case "Purple":
                    color = context.getColor(R.color.purple_color_theme);
                    break;
                case "Green":
                    color = context.getColor(R.color.green_color_theme);
                    break;
            }
        }
        return color;
    }

    public static int systemTheme(Context context){
        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES){
            return context.getColor(R.color.icons_night_mode);
        }else {
            return context.getColor(R.color.icons_light_mode);
        }
    }
}
