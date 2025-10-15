package com.rarestardev.magneticplayer.application;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.rarestardev.magneticplayer.settings.ThemeSettings;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.PlayListViewModel;
import com.rarestardev.magneticplayer.viewmodel.RecentListViewModel;

public class MusicApplication extends Application {
    private MusicStatusViewModel musicViewModel;
    private RecentListViewModel recentListViewModel;
    private PlayListViewModel playListViewModel;

    @Override
    public void onCreate() {
        super.onCreate();

        ThemeSettings themeSettings = new ThemeSettings(this);

        int current_theme = themeSettings.getCurrentTheme();
        if (current_theme != 0) {
            if (current_theme == AppCompatDelegate.MODE_NIGHT_NO) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (current_theme == AppCompatDelegate.MODE_NIGHT_YES) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }


        musicViewModel = new ViewModelProvider.AndroidViewModelFactory(this)
                .create(MusicStatusViewModel.class);

        recentListViewModel = new ViewModelProvider.AndroidViewModelFactory(this)
                .create(RecentListViewModel.class);

        playListViewModel = new ViewModelProvider.AndroidViewModelFactory(this)
                .create(PlayListViewModel.class);
    }

    public MusicStatusViewModel getMusicViewModel() {
        return musicViewModel;
    }

    public RecentListViewModel getRecentViewModel(){
        return recentListViewModel;
    }

    public PlayListViewModel getPlayListViewModel() {
        return playListViewModel;
    }
}
