package com.rarestardev.magneticplayer.view.activities;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.custom_views.ExpandedMenuMusicPlayerDialog;
import com.rarestardev.magneticplayer.helper.AnimationHelper;
import com.rarestardev.magneticplayer.custom_views.ListQueueCustomDialog;
import com.rarestardev.magneticplayer.controller.MusicCoverManager;
import com.rarestardev.magneticplayer.controller.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.controller.MusicPlayerService;
import com.rarestardev.magneticplayer.databinding.ActivityMusicPlayerBinding;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.utilities.FileFormater;
import com.rarestardev.magneticplayer.utilities.NavigationBarUtils;
import com.rarestardev.magneticplayer.viewmodel.FavoriteMusicViewModel;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

public class MusicPlayerActivity extends AppCompatActivity {

    private ActivityMusicPlayerBinding binding;
    private AnimationHelper animationHelper;
    private AudioManager audioManager;
    private MusicPlaybackSettings musicPlaybackSettings;
    private final Handler handler = new Handler();
    private Runnable updateTask;
    private MusicPlayerService musicService;
    private FavoriteMusicViewModel favoriteMusicViewModel;
    private ListQueueCustomDialog listQueueCustomDialogDialog;
    private AdNetworkManager adNetworkManager;

    private static final int SEEK_INTERVAL = 10000;
    private static final int STATUS_BANNER_INTERVAL = 2000;
    private boolean isBound = false;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
            Log.d(Constants.appLog, "MusicPlayerActivity service connect");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
            isBound = false;
            Log.e(Constants.appLog, "MusicPlayerActivity service disconnect");
        }
    };

    @SuppressLint({"SourceLockedOrientationActivity", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_player);

        doInitialization();
        updateUi();
        controlMusic();
    }


    @SuppressLint("SetTextI18n")
    private void doInitialization() {
        binding.musicName.setText("Music name");
        binding.artistName.setText("Artist name");
        binding.albumName.setText("Album name");
        binding.setMaxDuration("00:00");
        binding.setTimeMusic("00:00");

        animationHelper = new AnimationHelper();
        animationHelper.rotateAnimCoverMusic(binding.coverMusic);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        musicPlaybackSettings = new MusicPlaybackSettings(this);
        initIsShuffleValue();
        initIsRepeatValue();

        favoriteMusicViewModel = new ViewModelProvider(this).get(FavoriteMusicViewModel.class);
        listQueueCustomDialogDialog = new ListQueueCustomDialog(this, binding);

        adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.doInitializationAds();
        adNetworkManager.showSmallBannerAds(binding.bannerAd);
    }

    private void initIsShuffleValue() {
        if (musicPlaybackSettings.getIsShuffle()) {
            animationHelper.animateAndChangeColor(this, binding.shufflePlay, R.color.progress_color);
        } else {
            animationHelper.animateAndChangeColor(this, binding.shufflePlay, R.color.icons_night_mode);
        }
    }

    private void initIsRepeatValue() {
        if (musicPlaybackSettings.getIsRepeat()) {
            binding.repeatMusic.setImageResource(R.drawable.ic_repeat);
            animationHelper.animateAndChangeColor(this, binding.repeatMusic, R.color.progress_color);
        } else {
            binding.repeatMusic.setImageResource(R.drawable.ic_repeat_off);
            animationHelper.animateAndChangeColor(this, binding.repeatMusic, R.color.icons_night_mode);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateUi() {
        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();

        musicStatusViewModel.getIsPlayMusic().observe(this, aBoolean -> {
            if (aBoolean) {
                binding.playPauseMusic.setImageDrawable(getDrawable(R.drawable.ic_pause));
                animationHelper.resumeRotation();
            } else {
                binding.playPauseMusic.setImageDrawable(getDrawable(R.drawable.ic_play_circle_filled));
                animationHelper.pauseRotation();
            }
        });

        musicStatusViewModel.getMusicInfo().observe(this, musicFile -> {
            if (musicFile != null) {
                binding.setMusicDetail(musicFile);
                binding.setMaxDuration(FileFormater.formatDuration(musicFile.getDuration()));
                binding.semiCircleProgressBar.setMaxProgress(musicFile.getDuration());

                MusicCoverManager coverManager = new MusicCoverManager(MusicPlayerActivity.this);
                coverManager.loadCoverAndSetBackground(musicFile.getAlbumArtUri(), binding.coverMusic, binding.playerBackground);

                coverManager.setColorChangeListener(color -> NavigationBarUtils.setNavigationBarColor(MusicPlayerActivity.this, color));

                checkFavoriteMusicInDatabase(musicFile);
                binding.expandedMenu.setOnClickListener(v -> {
                    ExpandedMenuMusicPlayerDialog dialog = new ExpandedMenuMusicPlayerDialog(MusicPlayerActivity.this);
                    dialog.setMusicFile(musicFile);
                    dialog.show();
                });
            } else {
                doInitialization();
            }
        });

        musicStatusViewModel.getDurationMusic().observe(this, integer -> {
            binding.setTimeMusic(FileFormater.formatDuration(integer));
            binding.semiCircleProgressBar.setProgress(integer);
        });

        musicStatusViewModel.getQueueList().observe(this, musicFiles -> listQueueCustomDialogDialog.getMusicList(musicFiles));
        musicStatusViewModel.getCurrentPosition().observe(this, integer -> listQueueCustomDialogDialog.getPosition(integer));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void controlMusic() {
        binding.playPauseMusic.setOnClickListener(v -> {
            if (musicService.getMusicIsPlaying()) {
                musicService.pauseMusic();
                showStatus("Pause Music");
            } else {
                musicService.resumeMusic();
                showStatus("Resume Music");
            }
        });

        binding.skipNextMusic.setOnClickListener(v -> {
            musicService.playNextMusic();
            listQueueCustomDialogDialog.doInitializationViews();
            showStatus("Next");
        });
        binding.skipPreviousMusic.setOnClickListener(v -> {
            musicService.playPreviousMusic();
            listQueueCustomDialogDialog.doInitializationViews();
            showStatus("Previous");
        });
        binding.volumeMusic.setOnClickListener(v -> {
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                    AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);

            showStatus("Volume");
            animationHelper.animateAndChangeIcon(this, binding.volumeMusic, 0);
        });

        binding.repeatMusic.setOnClickListener(v -> {
            if (musicPlaybackSettings.getIsRepeat()) {
                musicPlaybackSettings.setIsRepeat(false);
                showStatus("Repeat off");
            } else {
                musicPlaybackSettings.setIsRepeat(true);
                showStatus("Repeat on");
            }
            initIsRepeatValue();
        });

        binding.shufflePlay.setOnClickListener(v -> {
            if (musicPlaybackSettings.getIsShuffle()) {
                musicPlaybackSettings.setIsShuffle(false);
                showStatus("Shuffle off");
            } else {
                musicPlaybackSettings.setIsShuffle(true);
                showStatus("Shuffle on");
            }
            initIsShuffleValue();
        });

        binding.backActivity.setOnClickListener(v -> finish());

        binding.listQueue.setOnClickListener(v -> {
            animationHelper.animateAndChangeIcon(this, binding.listQueue, 0);
            if (binding.listQueueLayout.getVisibility() == View.GONE) {
                animationHelper.showViewWithAnimation(binding.listQueueLayout);
                listQueueCustomDialogDialog.doInitializationViews();
                binding.stopView.setVisibility(View.VISIBLE);
                showStatus("Queue list");
            }
        });

        listQueueCustomDialogDialog.setListener(() -> {
            musicPlaybackSettings.setIsShuffle(true);
            initIsShuffleValue();
            if (musicService != null) {
                musicService.playNextMusic();
            }
        });

        seekMusicHandler();

        binding.stopView.setOnClickListener(v -> {
            if (binding.listQueueLayout.getVisibility() == View.VISIBLE) {
                animationHelper.hideViewWithAnimation(binding.listQueueLayout);
                binding.stopView.setVisibility(View.GONE);
            }
        });

        binding.semiCircleProgressBar.setOnProgressChangedListener(progress -> {
            if (isBound && musicService != null) {
                musicService.seekTo((int) progress);
            }
        });

        binding.launchEq.setOnClickListener(v -> Toast.makeText(this, "Not available", Toast.LENGTH_SHORT).show());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void seekMusicHandler() {
        binding.skipNextMusic.setOnLongClickListener(v -> {
            startSeekingForward();
            return true;
        });

        binding.skipNextMusic.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                stopSeeking();
            }
            return false;
        });

        binding.skipPreviousMusic.setOnLongClickListener(v -> {
            startSeekingBackward();
            return true;
        });

        binding.skipPreviousMusic.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                stopSeeking();
            }
            return false;
        });
    }

    private void startSeekingForward() {
        updateTask = () -> {
            if (isBound && musicService != null) {
                musicService.seekTo(musicService.getCurrentPosition() + SEEK_INTERVAL);
                handler.postDelayed(updateTask, 1000);
            }
        };
        handler.post(updateTask);
    }

    private void startSeekingBackward() {
        updateTask = () -> {
            if (isBound && musicService != null) {
                musicService.seekTo(musicService.getCurrentPosition() - SEEK_INTERVAL);
                handler.postDelayed(updateTask, 1000);
            }
        };
        handler.post(updateTask);
    }

    private void stopSeeking() {
        handler.removeCallbacks(updateTask);
    }

    private void showStatus(String message) {
        if (binding.statusBanner.getVisibility() == View.GONE) {
            binding.statusBanner.setVisibility(View.VISIBLE);
            binding.statusText.setText(message);
            new Handler().postDelayed(() ->
                            binding.statusBanner.setVisibility(View.GONE),
                    STATUS_BANNER_INTERVAL);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void checkFavoriteMusicInDatabase(MusicFile musicFile) {
        favoriteMusicViewModel.getFilePath(musicFile.getFilePath()).observe(this, string -> {
            if (string != null) {
                if (!string.isEmpty() && string.equals(musicFile.getFilePath())) {
                    binding.addFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite));
                    deleteMusicFavoriteInDatabase(musicFile);
                } else {
                    binding.addFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_border));
                    addMusicFavoriteInDatabase(musicFile);
                }
            } else {
                binding.addFavorite.setImageDrawable(getDrawable(R.drawable.ic_favorite_border));
                addMusicFavoriteInDatabase(musicFile);
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addMusicFavoriteInDatabase(MusicFile musicFile) {
        binding.addFavorite.setOnClickListener(v -> {
            favoriteMusicViewModel.insertFavoriteData(musicFile);
            showStatus("Add Favorite");
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void deleteMusicFavoriteInDatabase(MusicFile musicFile) {
        binding.addFavorite.setOnClickListener(v -> {
            favoriteMusicViewModel.deleteFavoriteMusic(musicFile);
            showStatus("Remove Favorite");
        });
    }

    private void checkHeadsetConnected() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        binding.setIsHeadset(audioManager.isWiredHeadsetOn());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        checkHeadsetConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkHeadsetConnected();

        adNetworkManager.showNativeBannerAdWithHide(binding.nativeAdView, binding.nativeAdViewLayout);

        binding.adiveryCloseButton.setOnClickListener(v -> {
            if (binding.nativeAdViewLayout.getVisibility() == View.VISIBLE) {
                binding.nativeAdViewLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTask);
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(updateTask);
        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    @Override
    public void onBackPressed() {
        if (binding.listQueueLayout.getVisibility() == View.VISIBLE) {
            animationHelper.hideViewWithAnimation(binding.listQueueLayout);
            binding.stopView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}