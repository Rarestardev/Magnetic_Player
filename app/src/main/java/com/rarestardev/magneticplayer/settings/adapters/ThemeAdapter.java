package com.rarestardev.magneticplayer.settings.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.Themes;
import com.rarestardev.magneticplayer.settings.storage.ThemesStorage;
import com.rarestardev.magneticplayer.view.activities.MainActivity;

import java.util.List;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ThemeViewHolder> {

    private final List<Themes> themesList;
    private final Context context;

    public ThemeAdapter(Context context, List<Themes> themesList) {
        this.themesList = themesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ThemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.theme_items, parent, false);
        return new ThemeViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ThemeViewHolder holder, int position) {
        holder.items.setCardBackgroundColor(themesList.get(position).getThemeColor());

        ThemesStorage themesStorage = new ThemesStorage(context);
        String currentTheme = themesStorage.getThemeName();

        if (currentTheme != null && currentTheme.equals(themesList.get(position).getThemeName())) {
            holder.is_active_icon.setVisibility(View.VISIBLE);
        } else {
            holder.is_active_icon.setVisibility(View.GONE);
        }

        holder.items.setOnClickListener(v -> {
            String theme = themesList.get(position).getThemeName();
            themesStorage.saveNewTheme(theme);
            notifyDataSetChanged();

            Intent intent = new Intent(context, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return themesList.size();
    }

    public static class ThemeViewHolder extends RecyclerView.ViewHolder {

        CardView items;
        AppCompatImageView is_active_icon;

        public ThemeViewHolder(@NonNull View itemView) {
            super(itemView);

            items = itemView.findViewById(R.id.items);
            is_active_icon = itemView.findViewById(R.id.is_active_icon);
        }
    }
}
