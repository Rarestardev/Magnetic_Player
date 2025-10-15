package com.rarestardev.magneticplayer.music_utils.history;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.model.RecentList;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;
import com.rarestardev.magneticplayer.viewmodel.RecentListViewModel;

import java.util.List;

import eu.gsottbauer.equalizerview.EqualizerView;

public class HistoryMusicAdapter extends RecyclerView.Adapter<HistoryMusicAdapter.PopularMusicViewHolder> {
    private final Context context;
    private List<RecentList> recentLists;
    private RecentListViewModel viewModel;
    private MusicStatusViewModel musicStatusViewModel;
    private HistoryListener historyListener;

    public HistoryMusicAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<RecentList> recentLists) {
        this.recentLists = recentLists;
    }

    public void setViewModel(RecentListViewModel viewModel,MusicStatusViewModel musicStatusViewModel) {
        this.viewModel = viewModel;
        this.musicStatusViewModel = musicStatusViewModel;
    }

    public void setHistoryListener(HistoryListener historyListener) {
        this.historyListener = historyListener;
    }

    @NonNull
    @Override
    public PopularMusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_item, viewGroup, false);
        return new PopularMusicViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull PopularMusicViewHolder holder, int i) {
        List<MusicFile> musicFiles = viewModel.convertToMusicFileList(recentLists);
        MusicFile musicFile = musicFiles.get(i);

        Glide.with(context)
                .load(musicFile.getAlbumArtUri())
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .into(holder.cover_music);

        holder.artistName.setText(musicFile.getArtistName());
        holder.music_name.setText(musicFile.getSongTitle());
        holder.album_name.setText(musicFile.getAlbumName());

        holder.itemView.setOnClickListener(v -> historyListener.onClick(musicFiles, i));
        holder.itemView.setOnLongClickListener(v -> historyListener.onLongClick(recentLists.get(i)));

        musicStatusViewModel.getMusicInfo().observe((LifecycleOwner) context, musicFile1 -> {
            if (musicFile1 != null) {
                if (musicFile1.getFilePath() != null) {
                    if (musicFile1.getFilePath().equals(musicFile.getFilePath())) {
                        holder.is_playing_music.setVisibility(View.VISIBLE);
                        holder.is_playing_music.animateBars();
                    } else {
                        holder.is_playing_music.setVisibility(View.GONE);
                        holder.is_playing_music.stopBars();
                    }
                } else {
                    holder.is_playing_music.setVisibility(View.GONE);
                    holder.is_playing_music.stopBars();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentLists.size();
    }

    public static class PopularMusicViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView music_name, artistName, album_name;
        RoundedImageView cover_music;
        EqualizerView is_playing_music;

        public PopularMusicViewHolder(@NonNull View itemView) {
            super(itemView);
            music_name = itemView.findViewById(R.id.music_name);
            artistName = itemView.findViewById(R.id.artistName);
            cover_music = itemView.findViewById(R.id.cover_music);
            album_name = itemView.findViewById(R.id.album_name);
            is_playing_music = itemView.findViewById(R.id.is_playing_music);
        }
    }

    public interface HistoryListener {
        void onClick(List<MusicFile> musicFiles, int current_position);

        boolean onLongClick(RecentList recentList);
    }
}
