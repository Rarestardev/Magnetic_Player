package com.rarestardev.magneticplayer.music_utils.equalizer.views;

import android.annotation.SuppressLint;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.music_utils.equalizer.BandAdapter;
import com.rarestardev.magneticplayer.music_utils.equalizer.EqualizerManager;
import com.rarestardev.magneticplayer.music_utils.equalizer.PresetAdapter;
import com.rarestardev.magneticplayer.music_utils.equalizer.PresetStorage;
import com.rarestardev.magneticplayer.databinding.ActivityEqualizerBinding;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.EqualizerViewModel;

import java.util.ArrayList;
import java.util.List;

public class EqualizerActivity extends BaseActivity {
    private ActivityEqualizerBinding binding;
    private EqualizerManager equalizerManager;
    private PresetStorage presetStorage;
    private EqualizerViewModel equalizerViewModel;
    private Visualizer visualizer;
    List<SeekBar> seekBars = new ArrayList<>();
    List<TextView> valueLabels = new ArrayList<>();
    private BandAdapter bandAdapter;
    private boolean isActiveEqualizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_equalizer);

        doInitialization();
    }

    private void doInitialization() {
        binding.setShowWaveformAndSpectrum(false);
        equalizerViewModel = new ViewModelProvider(this).get(EqualizerViewModel.class);
        presetStorage = new PresetStorage(this);

        isActiveEqualizer = presetStorage.isActivePreset();

        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel viewModel = application.getMusicViewModel();

        binding.waveformSpectrumLayout.setOnClickListener(v -> {
            binding.setShowWaveformAndSpectrum(!binding.getShowWaveformAndSpectrum());
            updateWaveformAndSpectrumIconView();
        });

        viewModel.getAudioSessionIdLive().observe(this, sessionId -> {
            if (sessionId != 0) {
                setupEqualizer(sessionId);
                setupVisualizer(sessionId);
                setupUI();
            } else {
                Log.e(Constants.appLog, "Invalid audioSessionId received");
            }
        });
    }


    private void setupEqualizer(int sessionId) {
        equalizerManager = new EqualizerManager(sessionId);
        equalizerManager.setEnabled(presetStorage.isActivePreset());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupUI() {
        bandAdapter = new BandAdapter(equalizerManager, presetStorage, equalizerViewModel);
        bandAdapter.setEnable(isActiveEqualizer);

        binding.backActivity.setOnClickListener(v -> this.finish());

        binding.equalizerSwitch.setChecked(presetStorage.isActivePreset());
        updateControlsEnabledState(presetStorage.isActivePreset());
        binding.equalizerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            equalizerManager.setEnabled(isChecked);
            presetStorage.setPresetActiveMode(isChecked);
            updateControlsEnabledState(isChecked);
            isActiveEqualizer = isChecked;
            bandAdapter.setEnable(isChecked);
            if (!isChecked) binding.setShowWaveformAndSpectrum(false);
            setupUI();
        });

        seekBars.clear();
        valueLabels.clear();

        presetsLayoutView();

        binding.bassSeekBar.setMaxProgress(1000);
        short bassStrength = presetStorage.loadBassStrength((short) 500);
        binding.bassSeekBar.setProgress(bassStrength);
        binding.bassSeekBar.setStrokeWidth(14f);
        binding.tvBassBoost.setText(formatPercent(bassStrength));

        binding.bassSeekBar.setEnabled(isActiveEqualizer);
        binding.bassSeekBar.setOnProgressChangedListener(progress -> {
            binding.setShowWaveformAndSpectrum(false);
            updateWaveformAndSpectrumIconView();
            equalizerManager.setBassStrength((short) progress);
            presetStorage.saveBassStrength((short) progress);
            binding.tvBassBoost.setText(formatPercent(progress));
        });

        //  Loudness Enhancer
        binding.loudnessSeekBar.setMaxProgress(1000);
        int loudnessGain = presetStorage.loadLoudnessGain(1000);
        binding.loudnessSeekBar.setProgress(loudnessGain);
        binding.loudnessSeekBar.setStrokeWidth(14f);
        binding.tvLoudnessGain.setText(formatDb(loudnessGain));

        binding.loudnessSeekBar.setEnabled(isActiveEqualizer);
        binding.loudnessSeekBar.setOnProgressChangedListener(progress -> {
            binding.setShowWaveformAndSpectrum(false);
            updateWaveformAndSpectrumIconView();
            equalizerManager.setLoudnessGain((int) progress);
            presetStorage.saveLoudnessGain((int) progress);
            binding.tvLoudnessGain.setText(formatDb(progress));
        });

        // Reverb
        binding.reverbSeekBar.setMaxProgress(1000);
        binding.reverbSeekBar.setStrokeWidth(14f);
        int rawReverb = presetStorage.loadReverbLevel(500);
        int mappedPreset = Math.min(6, rawReverb / 167);

        binding.reverbSeekBar.setProgress(rawReverb);
        binding.tvReverbLevel.setText(formatPercent(rawReverb));

        binding.reverbSeekBar.setEnabled(isActiveEqualizer);
        binding.reverbSeekBar.setOnProgressChangedListener(progress -> {
            binding.setShowWaveformAndSpectrum(false);
            updateWaveformAndSpectrumIconView();
            equalizerManager.setReverbLevel(mappedPreset);
            presetStorage.saveReverbLevel((short) progress);
            binding.tvReverbLevel.setText(formatPercent(progress));
        });

        binding.bandRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.bandRecycler.setAdapter(bandAdapter);
    }

    private void presetsLayoutView() {
        PresetAdapter adapter = new PresetAdapter(this);
        adapter.PresetListener(index -> {
            if (presetStorage.isActivePreset()) {
                equalizerViewModel.selectPreset(index, equalizerManager, presetStorage);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    bandAdapter.updateBandLevelsFromEqualizer();
                    short min = equalizerManager.getMinLevel();
                    short bandCount = equalizerManager.getNumberOfBands();
                    for (int i = 0; i < seekBars.size(); i++) {
                        if (i >= bandCount) {
                            Log.w(Constants.appLog, "Skipping band " + i + ": not supported");
                            continue;
                        }

                        try {
                            short level = equalizerManager.getBandLevel((short) i);
                            seekBars.get(i).setProgress(level - min);

                            int freq = equalizerManager.getCenterFreq((short) i);
                            valueLabels.get(i).setText(String.format("%s: %s", formatFrequency(freq), formatLevel(level)));
                        } catch (RuntimeException e) {
                            Log.e(Constants.appLog, "getBandLevel failed for band " + i + ": " + e.getMessage());
                        }
                    }
                }, 100);
            }
        });

        binding.presetRecycler.setLayoutManager(new GridLayoutManager(this, 4));
        binding.presetRecycler.setAdapter(adapter);

        equalizerViewModel.getPresetList().observe(this, adapter::setPresets);
        equalizerViewModel.getSelectedIndex().observe(this, adapter::setSelectedIndex);
        equalizerViewModel.loadPresets(equalizerManager, presetStorage);
    }

    private String formatLevel(short level) {
        return (level >= 0 ? "+" : "") + level / 100 + " dB";
    }

    private String formatFrequency(int milliHz) {
        int hz = milliHz / 1000;
        return hz >= 1000 ? (hz / 1000) + " kHz" : hz + " Hz";
    }

    private String formatPercent(float value) {
        return ((int) value * 100 / 1000) + " %";
    }

    private String formatDb(float value) {
        int db = (int) value / 100;
        return "+" + db + " dB";
    }

    private void updateControlsEnabledState(boolean enabled) {
        for (SeekBar seekBar : seekBars) {
            seekBar.setEnabled(enabled);
        }

        for (TextView label : valueLabels) {
            label.setAlpha(enabled ? 1f : 0.5f);
        }

        updateStateVisualizer(enabled);
        binding.bassSeekBar.setEnabled(enabled);
        binding.reverbSeekBar.setEnabled(enabled);
        binding.loudnessSeekBar.setEnabled(enabled);
        binding.presetRecycler.setAlpha(enabled ? 1f : 0.4f);
        binding.presetRecycler.setEnabled(enabled);
        binding.waveformSpectrumLayout.setEnabled(enabled);
        binding.waveformSpectrumLayout.setAlpha(enabled ? 1.0f : 0.4f);
    }

    private void setupVisualizer(int sessionId) {
        if (sessionId <= 0) {
            Log.e(Constants.appLog, "Invalid audioSessionId for Visualizer");
            return;
        }

        try {
            if (visualizer != null) {
                visualizer.release();
                visualizer = null;
            }

            visualizer = new Visualizer(sessionId);
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                    binding.visualizerView.updateWaveform(waveform);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                    binding.spectrumView.updateFFT(fft);
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, true);

            visualizer.setEnabled(presetStorage.isActivePreset());
            Log.d(Constants.appLog, "Visualizer initialized successfully");
        } catch (RuntimeException e) {
            Log.e(Constants.appLog, "Visualizer init failed: " + e.getMessage());
        }
    }

    public void updateStateVisualizer(boolean isActive) {
        if (visualizer != null){
            visualizer.setEnabled(isActive);
        }
    }

    private void updateWaveformAndSpectrumIconView() {
        if (binding.getShowWaveformAndSpectrum()) {
            binding.waveformIconView.setImageResource(R.drawable.ic_expand_more);
        } else {
            binding.waveformIconView.setImageResource(R.drawable.ic_forward);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (equalizerManager != null) equalizerManager.release();
        if (visualizer != null) visualizer.release();
        binding.setShowWaveformAndSpectrum(false);
    }

    @Override
    public void onBackPressed() {
        if (!binding.getShowWaveformAndSpectrum()) {
            super.onBackPressed();
        } else {
            binding.setShowWaveformAndSpectrum(false);
            updateWaveformAndSpectrumIconView();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.setShowWaveformAndSpectrum(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.setShowWaveformAndSpectrum(false);
    }
}