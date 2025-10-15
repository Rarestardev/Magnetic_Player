package com.rarestardev.magneticplayer.music_utils.equalizer;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.viewmodel.EqualizerViewModel;

public class BandAdapter extends RecyclerView.Adapter<BandAdapter.BandViewHolder> {

    private final EqualizerManager equalizerManager;
    private final PresetStorage presetStorage;
    private final EqualizerViewModel equalizerViewModel;
    private final short bandCount;
    private final short minLevel;
    private final short maxLevel;
    private final short[] bandLevels;

    private boolean isEnable = false;

    public BandAdapter(EqualizerManager manager, PresetStorage storage, EqualizerViewModel viewModel) {
        this.equalizerManager = manager;
        this.presetStorage = storage;
        this.equalizerViewModel = viewModel;

        this.bandCount = manager.getNumberOfBands();
        this.minLevel = manager.getMinLevel();
        this.maxLevel = manager.getMaxLevel();

        this.bandLevels = new short[bandCount];
        for (short i = 0; i < bandCount; i++) {
            bandLevels[i] = manager.getBandLevel(i);
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setEnable(boolean enable) {
        isEnable = enable;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_band, parent, false);
        return new BandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BandViewHolder holder, int position) {
        short band = (short) position;
        int freq = equalizerManager.getCenterFreq(band);
        String freqLabel = formatFrequency(freq);

        short level = bandLevels[band];
        holder.freqText.setText(freqLabel);
        holder.valueText.setText(formatLevel(level));
        holder.seekBar.setMax(maxLevel - minLevel);
        holder.seekBar.setProgress(level - minLevel);

        holder.seekBar.setEnabled(isEnable);
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    equalizerViewModel.switchToCustom(presetStorage);
                }

                short newLevel = (short) (progress + minLevel);
                equalizerManager.setBandLevels(band, newLevel);
                presetStorage.saveBandLevel(band, newLevel);
                bandLevels[band] = newLevel;
                holder.valueText.setText(formatLevel(newLevel));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return bandCount;
    }

    public void updateBandLevelsFromEqualizer() {
        for (short i = 0; i < bandCount; i++) {
            try {
                bandLevels[i] = equalizerManager.getBandLevel(i);
                notifyItemChanged(i);
            } catch (RuntimeException e) {
                Log.e(Constants.appLog, "getBandLevel failed for band " + i + ": " + e.getMessage());
            }
        }
    }

    public static class BandViewHolder extends RecyclerView.ViewHolder {
        SeekBar seekBar;
        TextView valueText, freqText;

        BandViewHolder(View itemView) {
            super(itemView);
            seekBar = itemView.findViewById(R.id.bandSeekBar);
            valueText = itemView.findViewById(R.id.valueText);
            freqText = itemView.findViewById(R.id.freqText);
        }
    }

    private String formatFrequency(int milliHz) {
        int hz = milliHz / 1000;
        return hz >= 1000 ? (hz / 1000) + " kHz" : hz + " Hz";
    }

    private String formatLevel(short level) {
        return (level >= 0 ? "+" : "") + level / 100 + " dB";
    }
}
