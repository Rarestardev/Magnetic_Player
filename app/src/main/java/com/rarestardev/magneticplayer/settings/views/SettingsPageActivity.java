package com.rarestardev.magneticplayer.settings.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.databinding.ActivitySettingsPageBinding;
import com.rarestardev.magneticplayer.model.LanguageItem;
import com.rarestardev.magneticplayer.model.SystemThemes;
import com.rarestardev.magneticplayer.model.Themes;
import com.rarestardev.magneticplayer.settings.adapters.LanguageItemAdapter;
import com.rarestardev.magneticplayer.settings.storage.LanguageStorage;
import com.rarestardev.magneticplayer.settings.storage.ThemesStorage;
import com.rarestardev.magneticplayer.settings.adapters.SystemThemesAdapter;
import com.rarestardev.magneticplayer.settings.adapters.ThemeAdapter;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SettingsPageActivity extends BaseActivity {
    private ActivitySettingsPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings_page);

        String action = getIntent().getStringExtra(Constants.ACTIVITY_ACTION);

        binding.backActivity.setOnClickListener(v -> {
            initDefaultValue();
            finish();
        });

        if (action != null) {
            switch (action) {
                case "themes":
                    binding.setTitleActivity(getString(R.string.personalization));
                    binding.setThemeLayoutShow(true);
                    themeColors();
                    break;
                case "language":
                    binding.setTitleActivity(getString(R.string.language));
                    binding.setLanguageLayoutShow(true);
                    initializationLanguageView();
                    break;
                case "about_app":
                    binding.setTitleActivity(getString(R.string.about));
                    binding.setAboutAppLayoutShow(true);
                    initializationAboutAppView();
                    break;
            }
        }
    }

    private void themeColors() {
        binding.recyclerViewTheme.setLayoutManager(new GridLayoutManager(this, 3));
        List<Themes> themes = new ArrayList<>();
        themes.add(new Themes(Constants.THEMES_NAME[0], getColor(R.color.base_color_theme)));
        themes.add(new Themes(Constants.THEMES_NAME[1], getColor(R.color.orange_color_theme)));
        themes.add(new Themes(Constants.THEMES_NAME[2], getColor(R.color.dark_slate_gray_theme)));
        themes.add(new Themes(Constants.THEMES_NAME[3], getColor(R.color.red_color_theme)));
        themes.add(new Themes(Constants.THEMES_NAME[4], getColor(R.color.purple_color_theme)));
        themes.add(new Themes(Constants.THEMES_NAME[5], getColor(R.color.green_color_theme)));

        ThemeAdapter adapter = new ThemeAdapter(this, themes);
        binding.recyclerViewTheme.setAdapter(adapter);
        binding.recyclerViewTheme.setHasFixedSize(true);

        List<SystemThemes> systemThemes = new ArrayList<>();
        systemThemes.add(new SystemThemes("Auto", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, R.drawable.ic_auto_mode));
        systemThemes.add(new SystemThemes("Light", AppCompatDelegate.MODE_NIGHT_NO, R.drawable.ic_light_mode));
        systemThemes.add(new SystemThemes("Night", AppCompatDelegate.MODE_NIGHT_YES, R.drawable.ic_mode_night));


        binding.systemThemeRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        SystemThemesAdapter systemThemesAdapter = new SystemThemesAdapter(systemThemes, this);
        binding.systemThemeRecycler.setAdapter(systemThemesAdapter);
        binding.systemThemeRecycler.setHasFixedSize(true);
        systemThemesAdapter.setApplySystemThemeListener(theme -> {
            switch (theme) {
                case 1:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case 2:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case -1:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }

            ThemesStorage storage = new ThemesStorage(this);
            storage.changeTheme(theme);
        });
    }

    private void initializationLanguageView() {
        binding.languageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<LanguageItem> languages = new ArrayList<>();
        languages.add(new LanguageItem("en", getString(R.string.english)));
        languages.add(new LanguageItem("fa", getString(R.string.persian)));

        LanguageItemAdapter adapter = new LanguageItemAdapter(this);
        adapter.setLanguagesList(languages);

        binding.languageRecyclerView.setAdapter(adapter);
        binding.languageRecyclerView.setHasFixedSize(true);
    }

    private void initializationAboutAppView() {
        LanguageStorage languageStorage = new LanguageStorage(this);
        String langCode = languageStorage.getCurrentLanguage();
        if (langCode.equals("fa")) {
            binding.setAboutApp(displayAssetTextFile("about_app_fa.txt"));
        } else if (langCode.equals("en")) {
            binding.setAboutApp(displayAssetTextFile("about_app_en.txt"));
        }
    }

    public String displayAssetTextFile(String fileName) {
        try {
            InputStream inputStream = getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        initDefaultValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        initDefaultValue();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        initDefaultValue();
    }

    private void initDefaultValue() {
        binding.setAboutAppLayoutShow(false);
        binding.setLanguageLayoutShow(false);
        binding.setThemeLayoutShow(false);
    }
}