package com.rarestardev.magneticplayer.music_utils.artist;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.databinding.ActivityAllArtistViewBinding;
import com.rarestardev.magneticplayer.helper.HomeFragmentHelper;
import com.rarestardev.magneticplayer.model.LocalArtist;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.viewmodel.MusicViewModel;

import java.util.ArrayList;
import java.util.List;

public class AllArtistViewActivity extends BaseActivity {

    private ActivityAllArtistViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_artist_view);

        binding.setLoading(true);
        MusicViewModel musicViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MusicViewModel.class);

        binding.backActivity.setOnClickListener(v -> finish());

        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getOptionalSpanCount(), StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        binding.artistRecyclerView.setLayoutManager(layoutManager);
        musicViewModel.getAllMusic().observe(this, musicFiles -> {
            ArtistAdapter adapter = new ArtistAdapter(this, musicFiles.size());

            if (!musicFiles.isEmpty()) {
                List<LocalArtist> localArtists = HomeFragmentHelper.getArtistFromMusic(musicFiles);
                adapter.artistList(localArtists);
                binding.artistRecyclerView.setAdapter(adapter);
                binding.artistRecyclerView.refreshDrawableState();
                binding.artistRecyclerView.setHasFixedSize(true);
                binding.setLoading(false);
            }

            adapter.setListener((artist, coverPath) -> {
                if (artist != null && coverPath != null) {
                    if (!artist.isEmpty()) {
                        Intent intent = new Intent(this, ArtistInformationActivity.class)
                                .putExtra("MusicList", new ArrayList<>(musicFiles))
                                .putExtra("artist", artist)
                                .putExtra("path", coverPath);

                        startActivity(intent);
                    }
                }
            });
        });

        musicViewModel.loadMusic();
    }

    private int getOptionalSpanCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float itemWidthPx = getResources().getDimension(R.dimen.artist_cover_width_grid);
        int screenWidthPx = displayMetrics.widthPixels;
        return Math.max(3, (int) (screenWidthPx / itemWidthPx));
    }
}