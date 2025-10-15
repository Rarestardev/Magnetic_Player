package com.rarestardev.magneticplayer.view.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.databinding.ActivitySettingsBinding;
import com.rarestardev.magneticplayer.databinding.ThemeItemBinding;
import com.rarestardev.magneticplayer.settings.PopularSettings;
import com.rarestardev.magneticplayer.settings.ThemeSettings;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.utilities.NavigationBarUtils;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private ThemeSettings themeSettings;
    private PopularSettings popularSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        binding.backActivity.setOnClickListener(v -> finish());

        popularSettings = new PopularSettings(this);

        NavigationBarUtils.setNavigationBarColor(this,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showHidePopularManager();
        themeController();

        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);

        openSocialNetwork();
    }

    private void showHidePopularManager() {
        if (popularSettings.getShowHidePopularValue() != 0) {
            if (popularSettings.getShowHidePopularValue() == PopularSettings.SELECTION[0]) {
                binding.showPopularMusicSwitch.setChecked(true);
            } else if (popularSettings.getShowHidePopularValue() == PopularSettings.SELECTION[1]) {
                binding.showPopularMusicSwitch.setChecked(false);
            }
        }

        binding.showPopularMusicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                popularSettings.showHidePopularValue(PopularSettings.SELECTION[0]);
            } else {
                popularSettings.showHidePopularValue(PopularSettings.SELECTION[1]);
            }
        });
    }

    private void themeController() {
        ThemeItemBinding themeItemBindingAutoMode = DataBindingUtil.bind(binding.autoMode.getRoot());
        ThemeItemBinding themeItemBindingNightMode = DataBindingUtil.bind(binding.nightMode.getRoot());
        ThemeItemBinding themeItemBindingLightMode = DataBindingUtil.bind(binding.lightMode.getRoot());

        if (themeItemBindingLightMode == null || themeItemBindingAutoMode == null || themeItemBindingNightMode == null) {
            Log.e(Constants.appLog, "Settings theme binding null");
            return;
        }

        setThemeItem(themeItemBindingAutoMode, R.string.auto, R.drawable.ic_auto_mode);
        setThemeItem(themeItemBindingNightMode, R.string.nightMode, R.drawable.ic_mode_night);
        setThemeItem(themeItemBindingLightMode, R.string.lightMode, R.drawable.ic_light_mode);

        themeSettings = new ThemeSettings(this);

        int current_theme = themeSettings.getCurrentTheme();

        updateThemeSelection(current_theme, themeItemBindingAutoMode, themeItemBindingNightMode, themeItemBindingLightMode);

        binding.lightMode.themeItemBaseLayout.setOnClickListener(v ->
                selectTheme(AppCompatDelegate.MODE_NIGHT_NO, themeItemBindingAutoMode, themeItemBindingNightMode, themeItemBindingLightMode));

        binding.nightMode.themeItemBaseLayout.setOnClickListener(v ->
                selectTheme(AppCompatDelegate.MODE_NIGHT_YES, themeItemBindingAutoMode, themeItemBindingNightMode, themeItemBindingLightMode));

        binding.autoMode.themeItemBaseLayout.setOnClickListener(v ->
                selectTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, themeItemBindingAutoMode, themeItemBindingNightMode, themeItemBindingLightMode));
    }

    private void setThemeItem(ThemeItemBinding themeItemBinding, int textResId, int iconResId) {
        themeItemBinding.themeText.setText(getString(textResId));
        themeItemBinding.themeIcon.setImageResource(iconResId);
    }

    @SuppressLint("ResourceAsColor")
    private void updateThemeSelection(int currentTheme, ThemeItemBinding themeItemBindingAutoMode, ThemeItemBinding themeItemBindingNightMode, ThemeItemBinding themeItemBindingLightMode) {

        switch (currentTheme) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                themeItemBindingAutoMode.themeItemBaseLayout.setCardBackgroundColor(getColor(R.color.settings_btn_color));
                themeItemBindingNightMode.themeItemBaseLayout.setCardBackgroundColor(Color.TRANSPARENT);
                themeItemBindingLightMode.themeItemBaseLayout.setCardBackgroundColor(Color.TRANSPARENT);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                themeItemBindingAutoMode.themeItemBaseLayout.setCardBackgroundColor(Color.TRANSPARENT);
                themeItemBindingNightMode.themeItemBaseLayout.setCardBackgroundColor(getColor(R.color.settings_btn_color));
                themeItemBindingLightMode.themeItemBaseLayout.setCardBackgroundColor(Color.TRANSPARENT);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                themeItemBindingAutoMode.themeItemBaseLayout.setCardBackgroundColor(Color.TRANSPARENT);
                themeItemBindingNightMode.themeItemBaseLayout.setCardBackgroundColor(Color.TRANSPARENT);
                themeItemBindingLightMode.themeItemBaseLayout.setCardBackgroundColor(getColor(R.color.settings_btn_color));
                break;
        }
    }

    private void selectTheme(int newTheme, ThemeItemBinding themeItemBindingAutoMode, ThemeItemBinding themeItemBindingNightMode, ThemeItemBinding themeItemBindingLightMode) {
        updateThemeSelection(newTheme, themeItemBindingAutoMode, themeItemBindingNightMode, themeItemBindingLightMode);
        themeSettings.changeTheme(newTheme);
        applyTheme(newTheme);
        themeController();
    }

    private void applyTheme(int newTheme) {
        AppCompatDelegate.setDefaultNightMode(newTheme);
    }

    private void openSocialNetwork(){
        binding.github.setOnClickListener(v -> openInBrowser("https://github.com/Rarestardev"));
        binding.instagram.setOnClickListener(v -> openInBrowser("https://www.instagram.com/rarestar.dev"));
        binding.linkedin.setOnClickListener(v -> openInBrowser("https://www.linkedin.com/in/soheyl-darzi-707238274/"));
        binding.support.setOnClickListener(v -> openGmail());
    }

    private void openInBrowser(String url) {
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    private void openGmail() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"rarestar.dev@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Magnetic Player Support");
        intent.putExtra(Intent.EXTRA_TEXT, "Hi, enter your question or request...");

        try {
            startActivity(Intent.createChooser(intent, "Send gmail with ..."));
        } catch (android.content.ActivityNotFoundException e) {
            openInBrowser("https://mail.google.com/");
        }
    }
}