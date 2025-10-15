package com.rarestardev.magneticplayer.view.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.ViewPagerAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.databinding.ActivityMainBinding;
import com.rarestardev.magneticplayer.enums.NotificationAction;
import com.rarestardev.magneticplayer.helper.AnimationHelper;
import com.rarestardev.magneticplayer.music_utils.album.AlbumActivity;
import com.rarestardev.magneticplayer.music_utils.artist.AllArtistViewActivity;
import com.rarestardev.magneticplayer.music_utils.genre.GenreViewActivity;
import com.rarestardev.magneticplayer.music_utils.history.HistoryActivity;
import com.rarestardev.magneticplayer.music_utils.music.MusicPlayerActivity;
import com.rarestardev.magneticplayer.music_utils.music_folder.FolderActivity;
import com.rarestardev.magneticplayer.music_utils.popular.AllPopularActivity;
import com.rarestardev.magneticplayer.receiver.MusicBroadcastReceiver;
import com.rarestardev.magneticplayer.service.MusicPlayerService;
import com.rarestardev.magneticplayer.settings.helper.ThemeHelper;
import com.rarestardev.magneticplayer.settings.storage.CoverAnimationSettingsStorage;
import com.rarestardev.magneticplayer.settings.storage.SortListSettings;
import com.rarestardev.magneticplayer.settings.views.SettingsPageActivity;
import com.rarestardev.magneticplayer.utilities.ClearCache;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.utilities.FileFormater;
import com.rarestardev.magneticplayer.settings.views.SettingsActivity;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private SortListSettings sortListSettings;
    private static final int REQUEST_PHONE_STATE = 320;
    private MusicPlayerService musicService;
    private boolean isBound = false;
    private long backPressTime = 0;
    private Toast backToast;

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

        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sortListSettings = new SortListSettings(MainActivity.this);

        doInitialization();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    REQUEST_PHONE_STATE
            );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CoverAnimationSettingsStorage storage = new CoverAnimationSettingsStorage(MainActivity.this);
        setupMusicUiManager(storage.getEnabledAnimation());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(connection);
            isBound = false;

            if (!musicService.getMusicIsPlaying()) {
                ClearCache clearCache = new ClearCache(this);
                clearCache.clearAppCache();

                Intent bi = new Intent(this, BroadcastReceiver.class);
                bi.setAction(NotificationAction.ACTION_CLOSE.getValue());
                sendBroadcast(bi);
            }
        }
    }

    private void doInitialization() {
        binding.navigationDrawer.bringToFront();
        binding.icOpenDrawer.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        setupNavigationDrawerMenu();

        setupTabs();

        binding.btnSearch.setOnClickListener(v -> {
            Intent searchActivityIntent = new Intent(this, SearchActivity.class);
            startActivity(searchActivityIntent);
        });

        sortListSettings.doInitializeData();
    }

    private void setupNavigationDrawerMenu() {
        binding.navigationDrawer.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.popular_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, AllPopularActivity.class));
            } else if (menuItem.getItemId() == R.id.history_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            } else if (menuItem.getItemId() == R.id.folder_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, FolderActivity.class));
            } else if (menuItem.getItemId() == R.id.genre_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, GenreViewActivity.class));
            } else if (menuItem.getItemId() == R.id.artist_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, AllArtistViewActivity.class));
            } else if (menuItem.getItemId() == R.id.album_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, AlbumActivity.class));
            } else if (menuItem.getItemId() == R.id.settings_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else if (menuItem.getItemId() == R.id.about_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(MainActivity.this, SettingsPageActivity.class)
                        .putExtra(Constants.ACTIVITY_ACTION, "about_app");
                startActivity(intent);

            } else if (menuItem.getItemId() == R.id.support_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"rarestar.dev@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Mg Player Support");
                intent.putExtra(Intent.EXTRA_TEXT, "Hi, enter your question or request...");
                try {
                    startActivity(Intent.createChooser(intent, "Send gmail with ..."));
                } catch (android.content.ActivityNotFoundException e) {
                    openInBrowser();
                }
            } else if (menuItem.getItemId() == R.id.exit_menu) {
                binding.drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(MainActivity.this, MusicBroadcastReceiver.class)
                        .setAction(NotificationAction.ACTION_CLOSE.getValue());
                sendBroadcast(intent);
                finish();
            }

            return true;
        });
    }

    private void openInBrowser() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://mail.google.com/"));
        startActivity(intent);
    }

    private void setupMusicUiManager(boolean enabledAnimation) {
        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();
        AnimationHelper animationHelper = new AnimationHelper(MainActivity.this);

        if (!enabledAnimation) {
            animationHelper.stopRotation();
        }

        binding.musicPlayer.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MusicPlayerActivity.class)));

        musicStatusViewModel.getMusicInfo().observe(this, musicFile -> {
            if (musicFile != null) {
                binding.setShowMusicPlayer(true);
                binding.musicName.setText(musicFile.getSongTitle());
                binding.artistName.setText(musicFile.getArtistName());

                binding.progressHorizontal.setMax((int) musicFile.getDuration());
                musicStatusViewModel.getDurationMusic().observe(this, integer -> {
                    binding.musicTimer.setText(String.format("%s / %s", FileFormater.formatDuration(integer),
                            FileFormater.formatDuration(musicFile.getDuration())));

                    binding.progressHorizontal.setProgress(integer, true);
                });

                if (musicFile.getAlbumArtUri().isEmpty()) {
                    binding.coverMusic.setImageResource(R.drawable.ic_music);
                } else {
                    Glide.with(MainActivity.this)
                            .load(musicFile.getAlbumArtUri())
                            .into(binding.coverMusic);
                }
                if (enabledAnimation) {
                    animationHelper.rotateAnimCoverMusic(binding.coverMusic);
                } else {
                    animationHelper.stopRotation();
                }
            } else {
                binding.setShowMusicPlayer(false);
            }
        });

        binding.closeMusic.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicBroadcastReceiver.class)
                    .setAction(NotificationAction.ACTION_CLOSE.getValue());
            sendBroadcast(intent);
            animationHelper.stopRotation();
        });

        binding.nextMusic.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicBroadcastReceiver.class)
                    .setAction(NotificationAction.ACTION_NEXT.getValue());
            sendBroadcast(intent);
        });

        binding.skipPreviousMusic.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicBroadcastReceiver.class)
                    .setAction(NotificationAction.ACTION_PREVIOUS.getValue());
            sendBroadcast(intent);
        });

        binding.bottomNavigationBarLayout.setStrokeColor(Color.TRANSPARENT);
        binding.groupDivider.setDividerColor(Color.TRANSPARENT);
        musicStatusViewModel.getIsPlayMusic().observe(this, aBoolean -> {
            if (aBoolean) {
                animationHelper.animateAndChangeIcon(binding.playPauseMusic, R.drawable.ic_pause);
                binding.bottomNavigationBarLayout.setStrokeColor(ThemeHelper.themeColorManager(this));
                binding.groupDivider.setDividerColor(ThemeHelper.themeColorManager(this));
            } else {
                animationHelper.animateAndChangeIcon(binding.playPauseMusic, R.drawable.ic_play_circle_filled);
                binding.bottomNavigationBarLayout.setStrokeColor(Color.TRANSPARENT);
                binding.groupDivider.setDividerColor(Color.TRANSPARENT);
            }

            binding.playPauseMusic.setOnClickListener(v -> {
                if (isBound && musicService != null) {
                    if (aBoolean) {
                        musicService.pauseMusic();
                        if (enabledAnimation) {
                            animationHelper.pauseRotation();
                        } else {
                            animationHelper.stopRotation();
                        }
                    } else {
                        musicService.resumeMusic();
                        if (enabledAnimation) {
                            animationHelper.resumeRotation();
                        } else {
                            animationHelper.stopRotation();
                        }
                    }
                }
            });
        });
    }

    @SuppressLint("InflateParams")
    private void setupTabs() {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.viewPager.setUserInputEnabled(false);
        binding.viewPager.setOffscreenPageLimit(3);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, ((tab, i) -> {
            switch (i) {
                case 0:
                    tab.setText(getString(R.string.home_menu));
                    tab.setIcon(R.drawable.ic_home);
                    break;
                case 1:
                    tab.setText(getString(R.string.play_list));
                    tab.setIcon(R.drawable.ic_playlist);
                    break;
                case 2:
                    tab.setText(getString(R.string.favorite_menu));
                    tab.setIcon(R.drawable.ic_favorite);
                    break;
            }
        })).attach();

        binding.tabLayout.getTabAt(0).getIcon().setTint(ThemeHelper.themeColorManager(MainActivity.this));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setTint(ThemeHelper.themeColorManager(MainActivity.this));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setTint(ThemeHelper.resolveThemeColor(MainActivity.this, android.R.attr.textColor));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressTime + 2000 > System.currentTimeMillis()) {
            if (backToast != null) backToast.cancel();
            super.onBackPressed();
            return;
        } else {
            backToast = Toast.makeText(this, R.string.press_the_back_button_again_to_exit, Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressTime = System.currentTimeMillis();
    }
}