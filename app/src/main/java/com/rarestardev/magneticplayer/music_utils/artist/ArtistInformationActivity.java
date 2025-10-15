package com.rarestardev.magneticplayer.music_utils.artist;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.music_utils.music.adapters.MusicFileAdapter;
import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.music_utils.music.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.databinding.ActivityArtistInformationBinding;
import com.rarestardev.magneticplayer.enums.ShuffleMode;
import com.rarestardev.magneticplayer.helper.PlayMusicWithEqualizer;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.view.activities.BaseActivity;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ArtistInformationActivity extends BaseActivity {

    private ActivityArtistInformationBinding binding;
    private String artistName;
    private List<MusicFile> artistMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_artist_information);

        List<MusicFile> musicFiles = getIntent().getParcelableArrayListExtra("MusicList");
        artistName = getIntent().getStringExtra("artist");
        String coverPath = getIntent().getStringExtra("path");

        binding.backActivity.setOnClickListener(v -> finish());
        loadImage(coverPath);

        binding.setTvArtistName(artistName);

        initList(musicFiles);
    }

    @SuppressLint("DefaultLocale")
    private void initList(List<MusicFile> musicFiles) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        MusicFileAdapter adapter = new MusicFileAdapter(this);
        if (musicFiles != null) {

            artistMusic = new ArrayList<>();
            for (MusicFile musicFile : musicFiles) {
                if (musicFile.getArtistName().equals(artistName)) {
                    artistMusic.add(musicFile);
                }
            }

            adapter.setList(artistMusic, artistMusic.size());
            binding.recyclerView.setAdapter(adapter);
            binding.recyclerView.refreshDrawableState();
            binding.recyclerView.setHasFixedSize(true);

            binding.setTvArtistTrackSize(String.format("( %d )", artistMusic.size()));
            MusicPlaybackSettings musicPlaybackSettings = new MusicPlaybackSettings(this);
            binding.playShuffleArtistMusic.setOnClickListener(v -> {
                PlayMusicWithEqualizer playMusicWithEqualizer = new PlayMusicWithEqualizer(this);
                if (artistMusic.size() >= 4) {
                    Random random = new Random();
                    int index = random.nextInt(artistMusic.size());
                    playMusicWithEqualizer.startMusicService(artistMusic, index, true);
                } else {
                    playMusicWithEqualizer.startMusicService(artistMusic, 0, false);
                }

                musicPlaybackSettings.setShuffleMode(ShuffleMode.SHUFFLE);
            });
        }

        MusicApplication application = (MusicApplication) getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();
        adapter.setMusicStatusViewModel(musicStatusViewModel);
    }

    private void loadImage(String coverPath) {
        if (!coverPath.isEmpty()) {
            Glide.with(this)
                    .load(coverPath)
                    .into(binding.coverImageView);
            binding.setAlertMsg(false);
        } else {
            binding.coverImageView.setImageResource(R.drawable.ic_music);
            binding.setAlertMsg(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        artistMusic.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        artistMusic.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        artistMusic.clear();
    }
}