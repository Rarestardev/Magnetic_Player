package com.rarestardev.magneticplayer.view.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.music_utils.music.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.service.MusicPlayerService;
import com.rarestardev.magneticplayer.enums.NotificationAction;
import com.rarestardev.magneticplayer.settings.storage.LanguageStorage;
import com.rarestardev.magneticplayer.settings.storage.ThemesStorage;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.ClearCache;
import com.rarestardev.magneticplayer.utilities.Constants;

import java.util.Locale;

public class BaseActivity extends AppCompatActivity {

    private MusicPlayerService musicService;
    private boolean isBound = false;
    private AdNetworkManager adNetworkManager;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
            Log.d(Constants.appLog, "BaseActivity service connect");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isBound = false;
            Log.e(Constants.appLog, "BaseActivity service disconnect");
        }
    };

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);

        handleThemeMode();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        adNetworkManager = new AdNetworkManager(this);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        LanguageStorage storage = new LanguageStorage(newBase);
        String langCode = storage.getCurrentLanguage();

        Context context = updateBaseContextLocale(newBase, langCode);
        super.attachBaseContext(context);
    }

    private Context updateBaseContextLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = context.getResources().getConfiguration();
        config.setLocale(locale);

        return context.createConfigurationContext(config);
    }


    private void handleThemeMode() {
        ThemesStorage storage = new ThemesStorage(this);
        if (storage.getThemeName() != null) {
            switch (storage.getThemeName()) {
                case "BaseColor":
                    setTheme(R.style.Base_Theme_MagneticPlayer);
                    break;
                case "Orange":
                    setTheme(R.style.Orange_Theme_MagneticPlayer);
                    break;
                case "DarkSlate":
                    setTheme(R.style.Dark_Slate_Gray_Theme_MagneticPlayer);
                    break;
                case "Red":
                    setTheme(R.style.Red_Theme_MagneticPlayer);
                    break;
                case "Purple":
                    setTheme(R.style.Purple_Theme_MagneticPlayer);
                    break;
                case "Green":
                    setTheme(R.style.Green_Theme_MagneticPlayer);
                    break;
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        MusicPlaybackSettings musicPlaybackSettings = new MusicPlaybackSettings(this);
        musicPlaybackSettings.doInitialization();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adNetworkManager.doInitializationAds();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;

            if (!musicService.getMusicIsPlaying()) {
                ClearCache clearCache = new ClearCache(BaseActivity.this);
                clearCache.clearAppCache();

                Intent bi = new Intent(BaseActivity.this, BroadcastReceiver.class);
                bi.setAction(NotificationAction.ACTION_CLOSE.getValue());
                sendBroadcast(bi);
            }
        }
        Log.d(Constants.appLog, "Base Activity onDestroy");
    }
}