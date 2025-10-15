package com.rarestardev.magneticplayer.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;

import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.enums.ShuffleMode;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.music_utils.equalizer.EqualizerManager;
import com.rarestardev.magneticplayer.music_utils.equalizer.PresetStorage;
import com.rarestardev.magneticplayer.music_utils.music.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.service.MusicPlayerService;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.ArrayList;
import java.util.List;

public class PlayMusicWithEqualizer {

    private final Context context;
    private EqualizerManager equalizerManager;
    private final PresetStorage storage;
    private int currentSessionId = -1;
    private boolean observersRegistered = false;

    public PlayMusicWithEqualizer(Context context) {
        this.context = context;
        storage = new PresetStorage(context);
    }

    public void startMusicService(List<MusicFile> musicFiles, int current_position, boolean isShuffleMode) {
        MusicPlaybackSettings musicPlaybackSettings = new MusicPlaybackSettings(context);
        if (!isShuffleMode && musicPlaybackSettings.getShuffleMode().equals(ShuffleMode.SHUFFLE.name())) {
            musicPlaybackSettings.setShuffleMode(ShuffleMode.OFF);
        }
        playMusic(musicFiles, current_position);
    }

    private void playMusic(List<MusicFile> musicFiles, int position) {
        Intent service = new Intent(context, MusicPlayerService.class);
        service.putParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue(), new ArrayList<>(musicFiles));
        service.putExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), position);
        context.startService(service);

        if (storage.isActivePreset() && EqualizerManager.isBoostSupported()) {
            setEqualizerOnMusic();
        } else {
            storage.setPresetActiveMode(false);
        }

        Log.d(Constants.appLog, "Is Active Equalizer = " + storage.isActivePreset());
    }

    private void setEqualizerOnMusic() {
        MusicApplication application = (MusicApplication) ((Activity) context).getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        if (!observersRegistered) {
            musicStatusViewModel.getIsPlayMusic().observe((LifecycleOwner) context, isPlayedMusic -> {
                if (isPlayedMusic) {
                    musicStatusViewModel.getAudioSessionIdLive().observe((LifecycleOwner) context, sessionID -> {
                        if (sessionID != 0 && sessionID != AudioManager.ERROR && sessionID != currentSessionId) {
                            currentSessionId = sessionID;
                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                try {
                                    release();

                                    equalizerManager = new EqualizerManager(sessionID);

                                    if (!equalizerManager.getEnable()) {
                                        equalizerManager.setEnabled(true);
                                        Log.d(Constants.appLog, "Equalizer initialized");

                                        for (int i = 0; i < equalizerManager.getNumberOfBands(); i++) {
                                            short level = storage.loadBandLevel(i, (short) 0);
                                            if (level >= equalizerManager.getMinLevel() && level <= equalizerManager.getMaxLevel()) {
                                                equalizerManager.setBandLevels((short) i, level);
                                            }
                                        }

                                        equalizerManager.setBassStrength(storage.loadBassStrength((short) 500));
                                        equalizerManager.setLoudnessGain(storage.loadLoudnessGain(1000));

                                        int rawReverb = storage.loadReverbLevel(500);
                                        int mappedPreset = Math.max(0, Math.min(6, rawReverb / 167));
                                        equalizerManager.setReverbLevel(mappedPreset);
                                    }
                                } catch (IllegalArgumentException e) {
                                    Log.e(Constants.appLog, "Equalizer failed: " + e.getMessage());
                                }
                            }, 400);
                        }
                    });
                }
            });
            observersRegistered = true;
        }
    }

    public void release() {
        if (equalizerManager != null) {
            equalizerManager.release();
            equalizerManager = null;
        }
        currentSessionId = -1;
    }
}
