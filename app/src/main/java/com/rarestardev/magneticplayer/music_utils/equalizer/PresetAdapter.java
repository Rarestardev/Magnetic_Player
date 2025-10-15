package com.rarestardev.magneticplayer.music_utils.equalizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.settings.helper.ThemeHelper;

import java.util.ArrayList;
import java.util.List;

public class PresetAdapter extends RecyclerView.Adapter<PresetAdapter.PresetViewHolder> {

    private List<String> presets = new ArrayList<>();
    private int selectedIndex = -1;
    private OnPresetSelected listener;
    private final Context context;

    public interface OnPresetSelected {
        void onPresetSelected(int index);
    }

    public PresetAdapter(Context context) {
        this.context = context;
    }

    public void PresetListener(OnPresetSelected listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPresets(List<String> presets) {
        this.presets = presets != null ? presets : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setSelectedIndex(int index) {
        int old = selectedIndex;
        selectedIndex = index;
        notifyItemChanged(old);
        notifyItemChanged(selectedIndex);
    }

    @NonNull
    @Override
    public PresetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_preset_card, parent, false);
        return new PresetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PresetViewHolder holder, int position) {
        String name = presets.get(position);
        holder.name.setText(name);

        int backgroundColor = position == selectedIndex ? ThemeHelper.themeColorManager(context) : ThemeHelper.resolveThemeColor(context, android.R.attr.colorPrimary);
        int textColor = position == selectedIndex ? context.getColor(R.color.white) : ThemeHelper.resolveThemeColor(context, android.R.attr.textColor);

        holder.name.setTextColor(textColor);

        holder.presetLayout.setCardBackgroundColor(backgroundColor);
        holder.presetLayout.setOnClickListener(v -> listener.onPresetSelected(position));
    }

    @Override
    public int getItemCount() {
        return presets.size();
    }

    public static class PresetViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView name;
        CardView presetLayout;

        PresetViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.presetName);
            presetLayout = itemView.findViewById(R.id.presetLayout);
        }
    }
}
