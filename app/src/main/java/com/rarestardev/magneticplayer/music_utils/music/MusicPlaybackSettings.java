package com.rarestardev.magneticplayer.music_utils.music;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.rarestardev.magneticplayer.enums.PrefKey;
import com.rarestardev.magneticplayer.enums.ShuffleMode;
import com.rarestardev.magneticplayer.service.MusicPlayerService;

public class MusicPlaybackSettings {

    private final SharedPreferences sharedPreferences;
    private final Context context;

    public MusicPlaybackSettings(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PrefKey.MUSIC_STATE.getValue(), Context.MODE_PRIVATE);
    }

    public void setShuffleMode(ShuffleMode mode) {
        sharedPreferences.edit()
                .putString(PrefKey.KEY_SHUFFLE.getValue(), mode.name())
                .apply();
        notifyMusicService();
    }

    public String getShuffleMode() {
        return sharedPreferences.getString(PrefKey.KEY_SHUFFLE.getValue(), ShuffleMode.OFF.name());
    }

    public void doInitialization() {
        String key = PrefKey.KEY_SHUFFLE.getValue();
        Object value = sharedPreferences.getAll().get(key);

        if (value instanceof Boolean) {
            setShuffleMode(ShuffleMode.OFF);
        } else if (value instanceof String) {
            String strValue = (String) value;
            if (strValue.isEmpty()) {
                setShuffleMode(ShuffleMode.OFF);
            }
        } else if (value == null) {
            setShuffleMode(ShuffleMode.OFF);
        } else {
            sharedPreferences.edit().remove(key).apply();
            setShuffleMode(ShuffleMode.OFF);
        }
    }

    private void notifyMusicService() {
        Intent intent = new Intent(context, MusicPlayerService.class);
        context.startService(intent);
    }
}
