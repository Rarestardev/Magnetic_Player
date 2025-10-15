package com.rarestardev.magneticplayer.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.core.util.Pair;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayoutMediator;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.PopularMusicAdapter;
import com.rarestardev.magneticplayer.adapter.ViewPagerAdapter;
import com.rarestardev.magneticplayer.databinding.ActivityMainBinding;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.settings.PopularSettings;
import com.rarestardev.magneticplayer.settings.SortListSettings;
import com.rarestardev.magneticplayer.view.fragments.AlbumFragment;
import com.rarestardev.magneticplayer.view.fragments.ArtistFragment;
import com.rarestardev.magneticplayer.view.fragments.FolderFragment;
import com.rarestardev.magneticplayer.view.fragments.PlaylistFragment;
import com.rarestardev.magneticplayer.view.fragments.FavoriteFragment;
import com.rarestardev.magneticplayer.view.fragments.TracksFragment;
import com.rarestardev.magneticplayer.view.settings.SettingsActivity;
import com.rarestardev.magneticplayer.viewmodel.RecentListViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;
    private SortListSettings sortListSettings;
    private PopularMusicAdapter popularMusicAdapter;
    private PopularSettings popularSettings;

    private static final int MIN_LIST_ITEMS = 20;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = DataBindingUtil.setContentView(MainActivity.this, R.layout.activity_main);

        sortListSettings = new SortListSettings(MainActivity.this);
        popularSettings = new PopularSettings(MainActivity.this);

        setMusicPlayerUi(binding.viewStub.getViewStub());

        setSmallBannerAds(binding.bannerAd);

        doInitialization();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (popularSettings.getShowHidePopularValue() == PopularSettings.SELECTION[0]) {
            binding.setIsShowRecentList(true);
            showRecentPlayedMusic();
        } else if (popularSettings.getShowHidePopularValue() == PopularSettings.SELECTION[1]) {
            binding.setIsShowRecentList(false);
        }
    }

    private void doInitialization() {
        setupTabs();

        binding.searchButton.setOnClickListener(v -> {
            Intent searchActivityIntent = new Intent(this, SearchActivity.class);
            startActivity(searchActivityIntent);
        });

        binding.moreSettings.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SettingsActivity.class)));

        sortListSettings.doInitializeData();
        popularSettings.doInitialization();
    }

    private void showRecentPlayedMusic() {
        binding.recentListRecyclerView.setLayoutManager(
                new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

        RecentListViewModel viewModel = new ViewModelProvider(this).get(RecentListViewModel.class);
        viewModel.getAllRecentLists().observe(this, recentLists -> {
            if (!recentLists.isEmpty()) {
                List<MusicFile> musicFileList = viewModel.convertToMusicFileList(recentLists);

                popularMusicAdapter = new PopularMusicAdapter(MainActivity.this);
                popularMusicAdapter.setList(musicFileList, MIN_LIST_ITEMS);
                binding.recentListRecyclerView.setAdapter(popularMusicAdapter);
                binding.recentListRecyclerView.setHasFixedSize(true);

                binding.moreRecentList.setOnClickListener(v ->
                        startActivity(new Intent(MainActivity.this, MoreRecentTracksActivity.class)));

                if (popularSettings.getShowHidePopularValue() == PopularSettings.SELECTION[0]) {
                    binding.setIsShowRecentList(true);
                }

            } else {

                binding.setIsShowRecentList(false);
            }
        });
    }

    private void setupTabs() {
        List<Pair<Fragment, String>> fragmentTitlePairs = new ArrayList<>();
        fragmentTitlePairs.add(new Pair<>(new TracksFragment(), "Tracks"));
        fragmentTitlePairs.add(new Pair<>(new FavoriteFragment(), "Favorites"));
        fragmentTitlePairs.add(new Pair<>(new AlbumFragment(), "Album"));
        fragmentTitlePairs.add(new Pair<>(new ArtistFragment(), "Artist"));
        fragmentTitlePairs.add(new Pair<>(new PlaylistFragment(), "Playlist"));
        fragmentTitlePairs.add(new Pair<>(new FolderFragment(), "Folders"));

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, fragmentTitlePairs);
        binding.viewPager.setAdapter(viewPagerAdapter);

        binding.viewPager.setOffscreenPageLimit(6);

        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, i) -> tab.setText(viewPagerAdapter.getPageTitle(i)));
        tabLayoutMediator.attach();
    }
}