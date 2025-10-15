package com.rarestardev.magneticplayer.view.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ViewStub;

import androidx.appcompat.app.AppCompatActivity;

import com.adivery.sdk.AdiveryBannerAdView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.controller.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.controller.MusicPlayerService;
import com.rarestardev.magneticplayer.controller.MusicUiManager;
import com.rarestardev.magneticplayer.enums.NotificationAction;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.ClearCache;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.utilities.NavigationBarUtils;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

public class BaseActivity extends AppCompatActivity {

    private MusicPlayerService musicService;
    private boolean isBound = false;
    private MusicUiManager uiManager;
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
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        NavigationBarUtils.setNavigationBarColor(this,0);
        adNetworkManager = new AdNetworkManager(this);
    }

    public void setMusicPlayerUi(ViewStub viewStub) {
        uiManager = new MusicUiManager(this);
        uiManager.getViewStub(viewStub);

        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        musicStatusViewModel.getMusicInfo().observe(this, musicFile -> {
            if (musicFile != null) {
                uiManager.getMusicData(musicFile);
            }
        });

        musicStatusViewModel.getIsPlayMusic().observe(this, aBoolean -> {
            if (aBoolean != null)
                uiManager.getIsPlayMusic(aBoolean);
        });

        uiManager.setListener(new MusicUiManager.OnPlayPauseMusicListener() {
            @Override
            public void onClickViewPlayPause() {
                if (isBound && musicService != null) { // check the service connected and not null music service
                    if (musicService.getMusicIsPlaying()) { // music is play
                        musicService.pauseMusic();
                        uiManager.getIsPlayMusic(false);
                    } else {

                        musicService.resumeMusic();
                        uiManager.getIsPlayMusic(true);
                    }
                }
            }

            @Override
            public void onClickNext() {
                if (isBound && musicService != null) {
                    musicService.playNextMusic();
                }
            }

            @Override
            public void onClickPrevious() {
                if (isBound && musicService != null) {
                    musicService.playPreviousMusic();
                }
            }
        });
    }

    public void setSmallBannerAds(AdiveryBannerAdView bannerAdView){
        if (bannerAdView != null){
            adNetworkManager.showSmallBannerAds(bannerAdView);
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