package com.rarestardev.magneticplayer.music_utils.equalizer;

import android.content.Context;
import android.content.SharedPreferences;

public class PresetStorage {

    private final SharedPreferences prefs;

    public PresetStorage(Context context) {
        prefs = context.getSharedPreferences("EQ_PREFS", Context.MODE_PRIVATE);
    }

    public void saveBandLevel(int band, short level) {
        prefs.edit().putInt("band_" + band, level).apply();
    }

    public short loadBandLevel(int band, short defaultLevel) {
        return (short) prefs.getInt("band_" + band, defaultLevel);
    }

    public void saveBassStrength(short strength) {
        prefs.edit().putInt("bass_strength", strength).apply();
    }

    public short loadBassStrength(short defaultStrength) {
        return (short) prefs.getInt("bass_strength", defaultStrength);
    }

    public int loadLoudnessGain(int defaultValue) {
        return prefs.getInt("loudness_gain", defaultValue);
    }

    public void saveLoudnessGain(int gain) {
        prefs.edit().putInt("loudness_gain", gain).apply();
    }

    public int loadReverbLevel(int defaultValue) {
        return prefs.getInt("reverb_level", defaultValue);
    }

    public void saveReverbLevel(int level) {
        prefs.edit().putInt("reverb_level", level).apply();
    }

    public void savePresetIndex(int index) {
        prefs.edit().putInt("preset_index", index).apply();
    }

    public int loadPresetIndex() {
        return prefs.getInt("preset_index", 0);
    }

    public void setPresetActiveMode(boolean isActive) {
        prefs.edit().putBoolean("preset_active", isActive).apply();
    }

    public boolean isActivePreset() {
        return prefs.getBoolean("preset_active", false);
    }
}
