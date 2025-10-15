package com.rarestardev.magneticplayer.music_utils.popular;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.helper.PlayMusicWithEqualizer;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.model.RecentList;

import java.util.ArrayList;
import java.util.List;

import eu.gsottbauer.equalizerview.EqualizerView;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.VerticalPopularViewHolder> {

    private final Context context;
    private List<RecentList> recentLists = new ArrayList<>();
    private final List<MusicFile> musicFiles = new ArrayList<>();
    private boolean isPlayedMusic = false;
    private String songPath;

    public PopularAdapter(Context context) {
        this.context = context;
    }

    public void setRecentLists(List<RecentList> recentLists) {
        this.recentLists = recentLists;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPlayedMusic(boolean playedMusic) {
        isPlayedMusic = playedMusic;
        notifyDataSetChanged();
    }

    public void setSongPath(String songPath) {
        this.songPath = songPath;
    }

    @NonNull
    @Override
    public PopularAdapter.VerticalPopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vertical_popular_item, parent, false);
        return new VerticalPopularViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PopularAdapter.VerticalPopularViewHolder holder, int position) {
        RecentList recentList = recentLists.get(position);
        musicFiles.add(recentList.getMusicFiles());

        holder.tvArtistAlbumName.setText(String.format("%s | %s", recentList.getMusicFiles().getArtistName(), recentList.getMusicFiles().getAlbumName()));
        long count = recentList.getPlayCount();
        if (count > 999) {
            holder.tvPlayCount.setText("999+");
        } else {
            holder.tvPlayCount.setText(String.valueOf(count));
        }

        holder.tvSongName.setText(recentList.getMusicFiles().getSongTitle());

        if (recentList.getMusicFiles().getAlbumArtUri().isEmpty()) {
            holder.cover.setImageResource(R.drawable.ic_music);
        } else {
            Glide.with(context)
                    .load(recentList.getMusicFiles().getAlbumArtUri())
                    .into(holder.cover);
        }

        holder.itemView.setOnClickListener(v -> {
            PlayMusicWithEqualizer playMusicWithEqualizer = new PlayMusicWithEqualizer(context);
            playMusicWithEqualizer.startMusicService(musicFiles, position,false);
        });

        if (songPath != null) {
            if (songPath.equals(recentList.getMusicFiles().getFilePath())) {
                if (isPlayedMusic) {
                    holder.play_pause_music.setVisibility(View.VISIBLE);
                    holder.play_pause_music.animateBars();
                } else {
                    holder.play_pause_music.setVisibility(View.GONE);
                    holder.play_pause_music.stopBars();
                }
            } else {
                holder.play_pause_music.setVisibility(View.GONE);
                holder.play_pause_music.stopBars();
            }
        } else {
            holder.play_pause_music.setVisibility(View.GONE);
            holder.play_pause_music.stopBars();
        }
    }

    @Override
    public int getItemCount() {
        return recentLists.size();
    }

    public static class VerticalPopularViewHolder extends RecyclerView.ViewHolder {

        AppCompatTextView tvPlayCount, tvSongName, tvArtistAlbumName;
        RoundedImageView cover;
        EqualizerView play_pause_music;

        public VerticalPopularViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPlayCount = itemView.findViewById(R.id.tvPlayCount);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvArtistAlbumName = itemView.findViewById(R.id.tvArtistAlbumName);
            cover = itemView.findViewById(R.id.cover);
            play_pause_music = itemView.findViewById(R.id.play_pause_music);
        }
    }
}
