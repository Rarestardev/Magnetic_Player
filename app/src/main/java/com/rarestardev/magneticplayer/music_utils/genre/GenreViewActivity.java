package com.rarestardev.magneticplayer.music_utils.genre;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.databinding.ActivityGenreViewBinding;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.music_utils.music.adapters.MusicFileAdapter;
import com.rarestardev.magneticplayer.utilities.AdNetworkManager;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.MusicViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenreViewActivity extends BaseActivity {

    private ActivityGenreViewBinding binding;
    private final List<MusicFile> newMusicFiles = new ArrayList<>();
    private boolean isGenreInformation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_genre_view);

        binding.backActivity.setOnClickListener(v -> {
            if (isGenreInformation) {
                isGenreInformation = false;
                binding.setIsShowAllGenre(true);
                binding.noGenreMusic.setVisibility(View.GONE);
            } else {
                clearMemory();
                finish();
            }
        });

        showAllGenre();
    }

    private void showAllGenre() {
        binding.setIsShowAllGenre(true);
        String[] genreArray = getResources().getStringArray(R.array.music_genres);
        List<String> genreList = new ArrayList<>(Arrays.asList(genreArray));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GenreAdapter genreAdapter = new GenreAdapter(this);
        genreAdapter.setStringList(genreList);
        binding.recyclerView.setAdapter(genreAdapter);
        binding.recyclerView.setHasFixedSize(true);

        genreAdapter.setGenreListener(genre -> {
            binding.setIsShowAllGenre(false);
            isGenreInformation = true;
            showMusicOnGenre(genre);
        });

        AdNetworkManager adNetworkManager = new AdNetworkManager(this);
        adNetworkManager.showSmallBannerAds(binding.bannerAd);
    }

    private void showMusicOnGenre(String genre) {
        newMusicFiles.clear();
        if (genre != null) {
            MusicViewModel musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);
            binding.genreCover.setImageResource(GenreAdapter.loadImageWithGenre(genre));
            binding.tvGenre.setText(genre);

            musicViewModel.getAllMusic().observe(this,musicFiles -> {
                if (musicFiles != null) {
                    for (MusicFile musicFile : musicFiles) {
                        if (musicFile.getMusicGenre().equals(genre)) {
                            newMusicFiles.add(musicFile);
                        }
                    }

                    if (!newMusicFiles.isEmpty()) {
                        binding.noGenreMusic.setVisibility(View.GONE);
                        binding.genreRecyclerView.setVisibility(View.VISIBLE);
                        binding.genreRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                        MusicFileAdapter adapter = new MusicFileAdapter(this);
                        adapter.setList(newMusicFiles, newMusicFiles.size());
                        binding.genreRecyclerView.setAdapter(adapter);
                        binding.genreRecyclerView.refreshDrawableState();
                        binding.genreRecyclerView.setHasFixedSize(true);

                        MusicApplication application = (MusicApplication) getApplication();
                        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();
                        adapter.setMusicStatusViewModel(musicStatusViewModel);
                    } else {
                        binding.noGenreMusic.setVisibility(View.VISIBLE);
                        binding.genreRecyclerView.setVisibility(View.GONE);
                    }
                }
            });

            musicViewModel.loadMusic();
        }
    }

    @Override
    public void onBackPressed() {
        if (isGenreInformation) {
            isGenreInformation = false;
            binding.setIsShowAllGenre(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        clearMemory();
    }

    private void clearMemory() {
        newMusicFiles.clear();
    }
}