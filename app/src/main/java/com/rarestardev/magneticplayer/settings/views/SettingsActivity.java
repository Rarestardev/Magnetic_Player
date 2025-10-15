package com.rarestardev.magneticplayer.settings.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.databinding.ActivitySettingsBinding;
import com.rarestardev.magneticplayer.settings.storage.CoverAnimationSettingsStorage;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;

public class SettingsActivity extends BaseActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        initializationViews();

        rotateAnimationSwitchManager();
        dynamicBackgroundSwitchManager();
        currentVersionApp();
    }

    private void initializationViews() {
        binding.backActivity.setOnClickListener(v -> finish());
        binding.personalization.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsPageActivity.class)
                    .putExtra(Constants.ACTIVITY_ACTION, "themes");

            startActivity(intent);
        });

        binding.languageLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsPageActivity.class)
                    .putExtra(Constants.ACTIVITY_ACTION, "language");

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);

        openSocialNetwork();
    }

    private void rotateAnimationSwitchManager() {
        CoverAnimationSettingsStorage storage = new CoverAnimationSettingsStorage(SettingsActivity.this);
        boolean isActive = storage.getEnabledAnimation();

        binding.rotateAnimationCoverMusicSwitch.setChecked(isActive);
        binding.rotateAnimationCoverMusicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            storage.setKeyEnable(isChecked);
            rotateAnimationSwitchManager();
        });
    }

    private void dynamicBackgroundSwitchManager() {
        CoverAnimationSettingsStorage storage = new CoverAnimationSettingsStorage(SettingsActivity.this);
        boolean isActive = storage.getEnabledDynamic();

        binding.dynamicPlayerBackgroundSwitch.setChecked(isActive);
        binding.dynamicPlayerBackgroundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            storage.setKeyDynamic(isChecked);
            dynamicBackgroundSwitchManager();
        });
    }

    private void openSocialNetwork() {
        binding.github.setOnClickListener(v -> openInBrowser("https://github.com/Rarestardev"));
        binding.instagram.setOnClickListener(v -> openInBrowser("https://www.instagram.com/rarestar.dev"));
        binding.linkedin.setOnClickListener(v -> openInBrowser("https://www.linkedin.com/in/soheyl-darzi-707238274/"));
    }

    private void openInBrowser(String url) {
        if (url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    private void currentVersionApp() {
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            binding.setVersionName(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(Constants.appLog, e.getMessage());
            binding.setVersionName("1.0.1");
        }
    }
}