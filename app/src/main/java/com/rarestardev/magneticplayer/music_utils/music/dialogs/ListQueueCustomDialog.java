package com.rarestardev.magneticplayer.music_utils.music.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.helper.PlayMusicWithEqualizer;

import com.rarestardev.magneticplayer.application.MusicApplication;
import com.rarestardev.magneticplayer.enums.ShuffleMode;
import com.rarestardev.magneticplayer.music_utils.music.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.databinding.ActivityMusicPlayerBinding;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.music_utils.music.adapters.MusicFileAdapter;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.List;
import java.util.Random;

public class ListQueueCustomDialog {
    private int currentPosition;
    private final Context context;
    private final ActivityMusicPlayerBinding binding;
    private MusicFileAdapter adapter;

    public ListQueueCustomDialog(Context context, ActivityMusicPlayerBinding binding) {
        this.context = context;
        this.binding = binding;
    }

    @SuppressLint("DefaultLocale")
    public void doInitializationViews() {
        MusicPlaybackSettings musicPlaybackSettings = new MusicPlaybackSettings(context);
        MusicApplication application = (MusicApplication) ((Activity) context).getApplication();
        MusicStatusViewModel musicStatusViewModel = application.getMusicViewModel();
        adapter = new MusicFileAdapter(context);
        binding.listQueueRecycler.setLayoutManager(new LinearLayoutManager(context));

        musicStatusViewModel.getCurrentPosition().observe((LifecycleOwner) context, integer -> currentPosition = integer);

        musicStatusViewModel.getQueueList().observe((LifecycleOwner) context, musicFiles -> {
            if (musicFiles != null) {
                adapter.setList(musicFiles,musicFiles.size());
                binding.listQueueRecycler.setAdapter(adapter);

                binding.setMusicNameDialog(musicFiles.get(currentPosition).getSongTitle());
                binding.setArtistNameDialog(musicFiles.get(currentPosition).getArtistName());
                binding.listQueueRecycler.scrollToPosition(currentPosition);

                loadMusicCover(musicFiles);

                binding.setMusicPositionDialog("( " + (currentPosition + 1) + " )");

                binding.totalListSize.setText(String.format("( %d )", musicFiles.size() + 1));

                playShuffleMusic(musicPlaybackSettings, musicFiles);

                binding.recentMusic.setOnClickListener(v -> binding.listQueueRecycler.scrollToPosition(currentPosition));
            }
        });

        adapter.setMusicStatusViewModel(musicStatusViewModel);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void loadMusicCover(List<MusicFile> musicFiles) {
        Glide.with(context)
                .load(musicFiles.get(currentPosition).getAlbumArtUri())
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .into(binding.coverMusicDialog);
    }

    private void playShuffleMusic(MusicPlaybackSettings musicPlaybackSettings, List<MusicFile> musicFiles) {
        binding.shufflePlayDialog.setOnClickListener(v -> {
            Random random = new Random();
            int index = random.nextInt(musicFiles.size());
            PlayMusicWithEqualizer musicWithEqualizer = new PlayMusicWithEqualizer(context);
            musicWithEqualizer.startMusicService(musicFiles, index,true);
            musicPlaybackSettings.setShuffleMode(ShuffleMode.SHUFFLE);
            Toast.makeText(context, context.getString(R.string.shuffle_play_started), Toast.LENGTH_SHORT).show();
            doInitializationViews();
            binding.playerBackground.closeDrawer(GravityCompat.START);
        });
    }
}
