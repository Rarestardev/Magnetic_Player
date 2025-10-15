package com.rarestardev.magneticplayer.music_utils.music;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.mig35.carousellayoutmanager.CarouselLayoutManager;
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.mig35.carousellayoutmanager.CenterScrollListener;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.helper.AnimationHelper;
import com.rarestardev.magneticplayer.music_utils.equalizer.EqualizerManager;
import com.rarestardev.magneticplayer.music_utils.equalizer.PresetStorage;
import com.rarestardev.magneticplayer.music_utils.music.adapters.MusicPlayerAdapter;
import com.rarestardev.magneticplayer.music_utils.music.dialogs.ExpandedMenuMusicPlayerDialog;
import com.rarestardev.magneticplayer.music_utils.music.dialogs.ListQueueCustomDialog;
import com.rarestardev.magneticplayer.service.MusicPlayerService;
import com.rarestardev.magneticplayer.databinding.ActivityMusicPlayerBinding;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.settings.storage.CoverAnimationSettingsStorage;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.utilities.FileFormater;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.music_utils.equalizer.views.EqualizerActivity;
import com.rarestardev.magneticplayer.viewmodel.FavoriteMusicViewModel;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class MusicPlayerActivity extends BaseActivity {

    private ActivityMusicPlayerBinding binding;
    private AnimationHelper animationHelper;
    private AudioManager audioManager;
    private final Handler handler = new Handler();
    private Runnable updateTask;
    private MusicPlayerService musicService;
    private FavoriteMusicViewModel favoriteMusicViewModel;
    private ListQueueCustomDialog listQueueCustomDialogDialog;
    private AdNetworkManager adNetworkManager;
    private MusicStatusViewModel musicStatusViewModel;
    private MusicPlayerAdapter adapter;
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

    private final ActivityResultLauncher<String> recordAudioPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Log.d(Constants.appLog, "Record Audio permission granted");
                } else {
                    Log.e(Constants.appLog, "Record Audio permission denied");
                }
            });

    @SuppressLint({"SourceLockedOrientationActivity", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_player);
        adapter = new MusicPlayerAdapter(this);
        doInitialization();
        updateUi();
        controlMusic();
        musicCoverState();
    }

    @SuppressLint("SetTextI18n")
    private void doInitialization() {
        binding.musicName.setText("Music name");
        binding.artistName.setText("Artist name");
        binding.setMaxDuration("00:00");
        binding.setTimeMusic("00:00");

        animationHelper = new AnimationHelper(this);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        favoriteMusicViewModel = new ViewModelProvider(this).get(FavoriteMusicViewModel.class);
        listQueueCustomDialogDialog = new ListQueueCustomDialog(this, binding);

        CoverAnimationSettingsStorage storage = new CoverAnimationSettingsStorage(MusicPlayerActivity.this);
        binding.setIsDynamicBackground(storage.getEnabledDynamic());

        adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.doInitializationAds();

        binding.playerBackground.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateUi() {
        MusicApplication application = (MusicApplication) getApplication();
        musicStatusViewModel = application.getMusicViewModel();

        musicStatusViewModel.getIsPlayMusic().observe(this, aBoolean -> {
            if (aBoolean) {
                animationHelper.animateAndChangeIcon(binding.playPauseMusic, R.drawable.ic_pause);
                animationHelper.resumeRotation();
            } else {
                animationHelper.animateAndChangeIcon(binding.playPauseMusic, R.drawable.ic_play_circle_filled);
                animationHelper.pauseRotation();
            }

            adapter.setPlayedMusic(aBoolean);
        });

        musicStatusViewModel.getMusicInfo().observe(this, musicFile -> {
            if (musicFile != null) {
                binding.setMusicDetail(musicFile);
                binding.setMaxDuration(FileFormater.formatDuration(musicFile.getDuration()));
                binding.durationMusicSeekbar.setMax((int) musicFile.getDuration());

                if (!musicFile.getAlbumArtUri().isEmpty()) {
                    Glide.with(this)
                            .load(musicFile.getAlbumArtUri())
                            .transform(new BlurTransformation(5, 2))
                            .into(binding.gradientCoverBackground);
                } else {
                    binding.gradientCoverBackground.setImageResource(R.drawable.ic_music);
                }


                checkFavoriteMusicInDatabase(musicFile);
                binding.expandedMenu.setOnClickListener(v -> {
                    ExpandedMenuMusicPlayerDialog dialog = new ExpandedMenuMusicPlayerDialog(MusicPlayerActivity.this);
                    dialog.setMusicFile(musicFile);
                    dialog.musicService(musicService);
                    dialog.getViewModel(musicStatusViewModel);
                    dialog.show();
                });
            } else {
                doInitialization();
            }
        });

        musicStatusViewModel.getDurationMusic().observe(this, integer -> {
            binding.setTimeMusic(FileFormater.formatDuration(integer));
            binding.durationMusicSeekbar.setProgress(integer);
        });
    }

    private void musicCoverState() {
        musicStatusViewModel.getQueueList().observe(this, musicFiles -> {
            if (musicFiles != null) {
                adapter.setMusicFiles(musicFiles);
                CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, false);
                layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
                layoutManager.setMaxVisibleItems(2);

                binding.coverMusicViewPager.setLayoutManager(layoutManager);
                binding.coverMusicViewPager.setHasFixedSize(true);
                binding.coverMusicViewPager.setAdapter(adapter);
                musicStatusViewModel.getCurrentPosition().observe(this, integer -> {
                    binding.coverMusicViewPager.scrollToPosition(integer);
                    adapter.setCurrent_index(integer);
                });

                binding.coverMusicViewPager.addOnScrollListener(new CenterScrollListener());
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void controlMusic() {
        binding.playPauseMusic.setOnClickListener(v -> {
            if (musicService.getMusicIsPlaying()) {
                musicService.pauseMusic();
                showStatus(getString(R.string.pause_music));
            } else {
                musicService.resumeMusic();
                showStatus(getString(R.string.resume_music));
            }
        });

        binding.skipNextMusic.setOnClickListener(v -> {
            animationHelper.simpleRotateAnimation(binding.skipNextMusic);
            musicService.playNextMusic();
            listQueueCustomDialogDialog.doInitializationViews();
            showStatus(getString(R.string.next));
        });

        binding.skipPreviousMusic.setOnClickListener(v -> {
            animationHelper.simpleRotateAnimation(binding.skipPreviousMusic);
            musicService.playPreviousMusic();
            listQueueCustomDialogDialog.doInitializationViews();
            showStatus(getString(R.string.previous));
        });

        ShuffleModeHelper shuffleModeHelper = new ShuffleModeHelper(MusicPlayerActivity.this);
        shuffleModeHelper.setTargetView(binding.shufflePlay);
        shuffleModeHelper.shuffleManager();
        shuffleModeHelper.setMessageListener(msg -> {
            if (msg != null && !msg.isEmpty()) {
                showStatus(msg);
            }
        });

        binding.backActivity.setOnClickListener(v -> finish());

        binding.listQueue.setOnClickListener(v -> {
            binding.navigationBarItemDrawer.bringToFront();
            binding.playerBackground.openDrawer(GravityCompat.START);
            listQueueCustomDialogDialog.doInitializationViews();
            showStatus(getString(R.string.queue_list));
        });

        seekMusicHandler();

        binding.durationMusicSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isBound && musicService != null) {
                    if (fromUser) {
                        musicService.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.launchEq.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                            != PackageManager.PERMISSION_GRANTED) {
                recordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
            } else {
                if (EqualizerManager.isBoostSupported()) {
                    startActivity(new Intent(MusicPlayerActivity.this, EqualizerActivity.class));
                } else {
                    PresetStorage storage = new PresetStorage(MusicPlayerActivity.this);
                    if (storage.isActivePreset()) {
                        storage.setPresetActiveMode(false);
                    }
                    Toast.makeText(MusicPlayerActivity.this, R.string.your_device_does_not_support_this_equalize, Toast.LENGTH_LONG).show();
                }
            }
        });
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
            showStatus(getString(R.string.add_favorite));
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void deleteMusicFavoriteInDatabase(MusicFile musicFile) {
        binding.addFavorite.setOnClickListener(v -> {
            favoriteMusicViewModel.deleteFavoriteMusic(musicFile);
            showStatus(getString(R.string.remove_favorite));
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
        if (binding.playerBackground.isDrawerOpen(GravityCompat.START)) {
            binding.playerBackground.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }
}