package com.rarestardev.magneticplayer.settings.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.SystemThemes;
import com.rarestardev.magneticplayer.settings.helper.ThemeHelper;
import com.rarestardev.magneticplayer.settings.storage.ThemesStorage;

import java.util.List;

public class SystemThemesAdapter extends RecyclerView.Adapter<SystemThemesAdapter.SystemThemeViewHolder> {

    private final List<SystemThemes> systemThemes;
    private final Context context;
    private ApplySystemThemeListener applySystemThemeListener;

    public SystemThemesAdapter(List<SystemThemes> systemThemes, Context context) {
        this.systemThemes = systemThemes;
        this.context = context;
    }

    public void setApplySystemThemeListener(ApplySystemThemeListener applySystemThemeListener) {
        this.applySystemThemeListener = applySystemThemeListener;
    }

    @NonNull
    @Override
    public SystemThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.system_themes_item, parent, false);
        return new SystemThemeViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull SystemThemeViewHolder holder, int position) {
        SystemThemes themes = systemThemes.get(position);

        holder.theme_text.setText(themes.getName());

        holder.theme_icon.setImageDrawable(context.getDrawable(themes.getIcon()));

        ThemesStorage storage = new ThemesStorage(context);
        if (storage.getCurrentTheme() == themes.getTheme()) {
            holder.theme_item_base_layout.setCardBackgroundColor(ThemeHelper.themeColorManager(context));
        } else {
            holder.theme_item_base_layout.setCardBackgroundColor(context.getColor(android.R.color.transparent));
        }

        holder.theme_item_base_layout.setOnClickListener(v -> {
            applySystemThemeListener.onApplySystemTheme(themes.getTheme());
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return systemThemes.size();
    }

    public interface ApplySystemThemeListener {
        void onApplySystemTheme(int theme);
    }

    public static class SystemThemeViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView theme_text;
        AppCompatImageView theme_icon;
        MaterialCardView theme_item_base_layout;

        public SystemThemeViewHolder(@NonNull View itemView) {
            super(itemView);

            theme_text = itemView.findViewById(R.id.theme_text);
            theme_icon = itemView.findViewById(R.id.theme_icon);
            theme_item_base_layout = itemView.findViewById(R.id.theme_item_base_layout);
        }
    }
}
