package com.rarestardev.magneticplayer.music_utils.album;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.databinding.ActivityAlbumBinding;
import com.rarestardev.magneticplayer.helper.HomeFragmentHelper;
import com.rarestardev.magneticplayer.model.AlbumInfo;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.music_utils.music.adapters.MusicFileAdapter;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.utilities.EndOfListMarginDecorator;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.MusicViewModel;

import java.util.ArrayList;
import java.util.List;

public class AlbumActivity extends BaseActivity {

    private ActivityAlbumBinding binding;
    private final List<MusicFile> newMusicOnAlbum = new ArrayList<>();
    private boolean isBackActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_album);

        MusicViewModel musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        binding.progressCircular.setVisibility(View.VISIBLE);
        binding.setShowAllAlbum(true);
        musicViewModel.getAllMusic().observe(this, musicFiles -> {
            if (musicFiles != null && !musicFiles.isEmpty()) {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(getOptionalSpanCount(), StaggeredGridLayoutManager.VERTICAL);
                layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                binding.allAlbumRecyclerView.setLayoutManager(layoutManager);
                AlbumsAdapter albumsAdapter = new AlbumsAdapter(this);
                List<AlbumInfo> albumInfos = HomeFragmentHelper.getAlbumFromMusic(musicFiles);
                albumsAdapter.setAlbumData(albumInfos);
                binding.allAlbumRecyclerView.setAdapter(albumsAdapter);
                binding.allAlbumRecyclerView.setHasFixedSize(true);
                binding.allAlbumRecyclerView.refreshDrawableState();

                albumsAdapter.AlbumClickListener(info -> {
                    binding.setShowAllAlbum(false);
                    albumInformation(info.getAlbumName(), info.getArtistName(), musicFiles);
                    isBackActivity = true;
                });

                binding.progressCircular.setVisibility(View.GONE);
                binding.tvNotFindAlbum.setVisibility(View.GONE);
            } else {
                binding.progressCircular.setVisibility(View.GONE);
                binding.tvNotFindAlbum.setVisibility(View.VISIBLE);
            }
        });

        musicViewModel.loadMusic();

        binding.backActivity.setOnClickListener(v -> {
            if (!isBackActivity) {
                clearMemory();
                finish();
            } else {
                binding.setShowAllAlbum(true);
                isBackActivity = false;
            }
        });

        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);
    }

    private void albumInformation(String albumName, String artistName, List<MusicFile> musicFiles) {
        newMusicOnAlbum.clear();
        if (albumName != null) {
            binding.albumName.setText(albumName);
            for (MusicFile musicFile : musicFiles) {
                if (albumName.equals(musicFile.getAlbumName())) {
                    newMusicOnAlbum.add(musicFile);
                    if (!musicFile.getAlbumArtUri().isEmpty()) {
                        Glide.with(this)
                                .load(musicFile.getAlbumArtUri())
                                .into(binding.albumCover);
                    } else {
                        binding.albumCover.setImageResource(R.drawable.ic_music);
                    }

                    if (artistName != null && !artistName.isEmpty())
                        binding.artistName.setText(artistName);

                    initializationAlbumList();
                }
            }
        }
    }

    private void initializationAlbumList() {
        binding.musicOnAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.musicOnAlbumRecyclerView.addItemDecoration(new EndOfListMarginDecorator());
        MusicFileAdapter adapter = new MusicFileAdapter(this);
        if (newMusicOnAlbum != null) {
            adapter.setList(newMusicOnAlbum, newMusicOnAlbum.size());
            binding.musicOnAlbumRecyclerView.setAdapter(adapter);
            binding.musicOnAlbumRecyclerView.setHasFixedSize(true);
            binding.musicOnAlbumRecyclerView.refreshDrawableState();
        }

        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();
        adapter.setMusicStatusViewModel(musicStatusViewModel);
    }

    private int getOptionalSpanCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float itemWidthPx = getResources().getDimension(R.dimen.artist_cover_width_grid);
        int screenWidthPx = displayMetrics.widthPixels;
        return Math.max(3, (int) (screenWidthPx / itemWidthPx));
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemory();
    }

    @Override
    public void onBackPressed() {
        if (isBackActivity) {
            binding.setShowAllAlbum(true);
            isBackActivity = false;
        } else {
            super.onBackPressed();
        }
    }

    private void clearMemory() {
        newMusicOnAlbum.clear();
    }
}