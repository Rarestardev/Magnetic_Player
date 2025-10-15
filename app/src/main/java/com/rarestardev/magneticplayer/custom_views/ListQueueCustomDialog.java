package com.rarestardev.magneticplayer.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.adapter.ListQueueAdapter;

import com.rarestardev.magneticplayer.controller.MusicPlayerService;
import com.rarestardev.magneticplayer.databinding.ActivityMusicPlayerBinding;
import com.rarestardev.magneticplayer.enums.ExtraKey;
import com.rarestardev.magneticplayer.model.MusicFile;

import java.util.ArrayList;
import java.util.List;

public class ListQueueCustomDialog {

    private List<MusicFile> musicFiles;
    private int currentPosition;
    private final Context context;
    private final ActivityMusicPlayerBinding binding;
    private onDialogViewsListener listener;
    private ListQueueAdapter adapter;

    public ListQueueCustomDialog(Context context, ActivityMusicPlayerBinding binding) {
        this.context = context;
        this.binding = binding;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "DefaultLocale"})
    public void doInitializationViews() {
        adapter = new ListQueueAdapter(context);
        binding.listQueueRecycler.setLayoutManager(new LinearLayoutManager(context));
        adapter.getListQueue(musicFiles);
        binding.listQueueRecycler.setAdapter(adapter);

        binding.setMusicNameDialog(musicFiles.get(currentPosition).getSongTitle());
        binding.setArtistNameDialog(musicFiles.get(currentPosition).getArtistName());
        adapter.getMusicIsPlaying(musicFiles.get(currentPosition).getFilePath());
        binding.listQueueRecycler.scrollToPosition(currentPosition);

        Glide.with(context)
                .load(musicFiles.get(currentPosition).getAlbumArtUri())
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .into(binding.coverMusicDialog);

        binding.setMusicPositionDialog("( " + (currentPosition + 1) + " )");

        binding.totalListSize.setText(String.format("( %d )", musicFiles.size() + 1));

        binding.shufflePlayDialog.setOnClickListener(v -> {
            listener.onShufflePlayMusic();
            doInitializationViews();
        });

        binding.recentMusic.setOnClickListener(v -> binding.listQueueRecycler.scrollToPosition(currentPosition));

        adapter.setListener(new ListQueueAdapter.onListQueueListener() {
            @Override
            public void onMusicPlay(List<MusicFile> musicFiles, int position) {
                Intent intent = new Intent(context, MusicPlayerService.class);
                intent.putParcelableArrayListExtra(ExtraKey.MUSIC_LIST.getValue(), new ArrayList<>(musicFiles));
                intent.putExtra(ExtraKey.MUSIC_LIST_POSITION.getValue(), position);
                context.startService(intent);
                getPosition(position);
                getMusicList(musicFiles);
                doInitializationViews();
                adapter.getMusicIsPlaying(musicFiles.get(position).getFilePath());
            }

            @Override
            public void onMultiSelect(List<Integer> items) {

            }
        });
    }

    public void getMusicList(List<MusicFile> musicFiles) {
        this.musicFiles = musicFiles;
    }

    public void getPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setListener(onDialogViewsListener listener) {
        this.listener = listener;
    }

    public interface onDialogViewsListener {
        void onShufflePlayMusic();
    }
}
