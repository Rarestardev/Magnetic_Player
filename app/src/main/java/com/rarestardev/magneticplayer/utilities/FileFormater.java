package com.rarestardev.magneticplayer.utilities;

import android.annotation.SuppressLint;

public class FileFormater {


    @SuppressLint("DefaultLocale")
    public static String formatDuration(long duration) {
        int minutes = (int) (duration / 60000);
        int seconds = (int) (duration % 60000 / 1000);
        return String.format("%d:%02d", minutes, seconds);
    }
}
