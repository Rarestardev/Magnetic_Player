package com.rarestardev.magneticplayer.utilities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.view.activities.MoreRecentTracksActivity;
import com.rarestardev.magneticplayer.view.activities.MusicPlayerActivity;

public class NavigationBarUtils {

    public static void setNavigationBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.setDecorFitsSystemWindows(true);
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        if (color == 0) {
            window.setNavigationBarColor(Color.TRANSPARENT);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            if (activity instanceof MusicPlayerActivity) {
                window.setNavigationBarColor(activity.getColor(R.color.window_background_night_mode));

            } else if (activity instanceof MoreRecentTracksActivity) {
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        } else {
            window.setNavigationBarColor(color);
            window.setStatusBarColor(color);
        }

        Log.d(Constants.appLog, "" + color);
    }

    public static void setTransparentNavigationBar(Activity activity) {
        Window window = activity.getWindow();
        window.setNavigationBarColor(Color.TRANSPARENT);

        window.getDecorView().setSystemUiVisibility(
                window.getDecorView().getSystemUiVisibility() |
                        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
        );
    }
}
