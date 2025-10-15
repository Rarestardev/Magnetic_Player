package com.rarestardev.magneticplayer.music_utils.equalizer;

import android.media.audiofx.AudioEffect;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.LoudnessEnhancer;
import android.media.audiofx.PresetReverb;
import android.util.Log;

import com.rarestardev.magneticplayer.utilities.Constants;

public class EqualizerManager {

    private Equalizer equalizer;
    private BassBoost bassBoost;
    private PresetReverb presetReverb;
    private LoudnessEnhancer loudnessEnhancer;
    private short numberOfBands;
    private short minLevel;
    private short maxLevel;

    public EqualizerManager(int audioSessionId) {
        if (audioSessionId != 0) {
            try {
                if (equalizer == null || !equalizer.getEnabled()) {
                    equalizer = new Equalizer(0, audioSessionId);
                    equalizer.setEnabled(true);
                }

                if (presetReverb == null || !presetReverb.getEnabled()) {
                    presetReverb = new PresetReverb(0, audioSessionId);
                    presetReverb.setEnabled(true);
                }

                if (bassBoost == null || !bassBoost.getEnabled()) {
                    bassBoost = new BassBoost(0, audioSessionId);
                    bassBoost.setEnabled(true);
                }

                if (loudnessEnhancer == null || !loudnessEnhancer.getEnabled()) {
                    loudnessEnhancer = new LoudnessEnhancer(audioSessionId);
                    loudnessEnhancer.setEnabled(true);
                }
            } catch (RuntimeException e) {
                Log.e(Constants.appLog,"EqualizerManager : " + e.getMessage());
            }

            numberOfBands = equalizer.getNumberOfBands();
            minLevel = equalizer.getBandLevelRange()[0];
            maxLevel = equalizer.getBandLevelRange()[1];
        }
    }

    public short getNumberOfBands() {
        return numberOfBands;
    }

    public short getMinLevel() {
        return minLevel;
    }

    public short getMaxLevel() {
        return maxLevel;
    }

    public short getBandLevel(short band) {
        if (equalizer == null || !equalizer.getEnabled()) {
            Log.w(Constants.appLog, "Equalizer not ready for getBandLevel");
            return 0;
        }

        return equalizer.getBandLevel(band);
    }

    public void setBandLevels(short band, short level) {
        equalizer.setBandLevel(band, level);
    }

    public String[] getPresets() {
        int count = equalizer.getNumberOfPresets();
        String[] presets = new String[count];
        for (short i = 0; i < count; i++) {
            presets[i] = equalizer.getPresetName(i);
        }
        return presets;
    }

    public void setEnabled(boolean enabled) {
        if (equalizer != null) {
            try {
                equalizer.setEnabled(enabled);
            } catch (RuntimeException e) {
                Log.e(Constants.appLog, "setEnabled failed: " + e.getMessage());
            }
        }
    }

    public boolean getEnable() {
        return equalizer != null && equalizer.getEnabled();
    }

    public int getCenterFreq(short band) {
        return equalizer.getCenterFreq(band);
    }

    public void usePreset(short index) {
        if (equalizer != null && equalizer.getEnabled()) {
            int presetCount = equalizer.getNumberOfPresets();
            if (index >= 0 && index < presetCount) {
                try {
                    equalizer.usePreset(index);
                } catch (UnsupportedOperationException e) {
                    Log.e(Constants.appLog, "usePreset failed: " + e.getMessage());
                }
            } else {
                Log.e(Constants.appLog, "Invalid preset index: " + index);
            }
        } else {
            Log.e(Constants.appLog, "Equalizer not enabled or null");
        }
    }

    public void release() {
        if (equalizer != null) equalizer.release();
    }

    public void setBassStrength(short strength) {
        if (bassBoost != null && bassBoost.getStrengthSupported()) {
            bassBoost.setStrength(strength);
        } else {
            Log.e(Constants.appLog, "bass booster in equalizer null pointer");
        }
    }

    public void setReverbLevel(int level) {
        if (presetReverb != null) {
            presetReverb.setPreset((short) level);
        }
    }

    public void setLoudnessGain(int gainMillibels) {
        if (loudnessEnhancer != null) {
            loudnessEnhancer.setTargetGain(gainMillibels);
        } else {
            Log.e(Constants.appLog, "loudnessEnhancer in equalizer null pointer");
        }
    }

    public static boolean isBoostSupported() {
        AudioEffect.Descriptor[] descriptors = AudioEffect.queryEffects();
        for (AudioEffect.Descriptor descriptor : descriptors) {
            if (descriptor.type.equals(BassBoost.EFFECT_TYPE_BASS_BOOST)) {
                Log.d(Constants.appLog, "BassBoost is supported");
                return true;
            }
        }
        Log.e(Constants.appLog, "BassBoost is NOT supported");
        return false;
    }
}
