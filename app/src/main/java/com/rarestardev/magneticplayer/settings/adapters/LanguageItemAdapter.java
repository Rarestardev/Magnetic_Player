package com.rarestardev.magneticplayer.settings.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.LanguageItem;
import com.rarestardev.magneticplayer.settings.helper.ThemeHelper;
import com.rarestardev.magneticplayer.settings.storage.LanguageHelper;
import com.rarestardev.magneticplayer.settings.storage.LanguageStorage;
import com.rarestardev.magneticplayer.view.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class LanguageItemAdapter extends RecyclerView.Adapter<LanguageItemAdapter.LanguageViewHolder> {

    private final Context context;
    private List<LanguageItem> languages = new ArrayList<>();

    public LanguageItemAdapter(Context context) {
        this.context = context;
    }

    public void setLanguagesList(List<LanguageItem> languages) {
        this.languages = languages;
    }

    @NonNull
    @Override
    public LanguageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.language_items, parent, false);
        return new LanguageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LanguageViewHolder holder, int position) {
        LanguageItem language = languages.get(position);
        holder.tv_language_name.setText(language.displayName);

        LanguageStorage languageStorage = new LanguageStorage(context);

        initSelectedLanguage(languageStorage, language.key, holder);

        holder.language_card.setOnClickListener(v -> {
            languageStorage.setLanguage(language.key);
            LanguageHelper.applyLanguage(context, language.key);
            initSelectedLanguage(languageStorage, language.key, holder);
            Intent intent = new Intent(context, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            context.startActivity(intent);
        });
    }

    private void initSelectedLanguage(LanguageStorage languageStorage, String language, LanguageViewHolder holder) {
        if (languageStorage.getCurrentLanguage().equals(language)) {
            holder.language_card.setCardBackgroundColor(ThemeHelper.themeColorManager(context));
        } else {
            holder.language_card.setCardBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tv_language_name;
        MaterialCardView language_card;

        public LanguageViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_language_name = itemView.findViewById(R.id.tv_language_name);
            language_card = itemView.findViewById(R.id.language_card);
        }
    }
}
