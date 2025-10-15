package com.rarestardev.magneticplayer.music_utils.music.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.enums.ShuffleMode;
import com.rarestardev.magneticplayer.helper.PlayMusicWithEqualizer;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.music_utils.music.MusicPlaybackSettings;
import com.rarestardev.magneticplayer.settings.storage.CoverAnimationSettingsStorage;

import java.util.List;

public class MusicPlayerAdapter extends RecyclerView.Adapter<MusicPlayerAdapter.MusicPlayerViewHolder> {

    private final Context context;
    private List<MusicFile> musicFiles;

    private boolean isPlayedMusic;
    private int current_index;

    public MusicPlayerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MusicPlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_player_item, parent, false);
        return new MusicPlayerViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPlayedMusic(boolean playedMusic) {
        isPlayedMusic = playedMusic;
        notifyDataSetChanged();
    }

    public void setCurrent_index(int current_index) {
        this.current_index = current_index;
    }

    public void setMusicFiles(List<MusicFile> musicFiles) {
        this.musicFiles = musicFiles;
    }

    @Override
    public void onBindViewHolder(@NonNull MusicPlayerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        MusicFile file = musicFiles.get(position);
        String imageUrl = file.getAlbumArtUri();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_music)
                    .into(holder.cover_music);
        } else {
            holder.cover_music.setImageResource(R.drawable.ic_music);
        }

        CoverAnimationSettingsStorage storage = new CoverAnimationSettingsStorage(context);

        if (storage.getEnabledAnimation()) {
            RotateAnimation animation = new RotateAnimation(
                    0f, 360f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            animation.setDuration(30000);
            animation.setRepeatCount(Animation.INFINITE);
            animation.setInterpolator(new LinearInterpolator());

            if (isPlayedMusic) {
                if (current_index == position) {
                    holder.cover_music.setAnimation(animation);
                    holder.next_song_info_layout.setVisibility(View.INVISIBLE);
                } else {
                    holder.cover_music.clearAnimation();
                    initNextMusicInfo(holder, file, position);
                }
            } else {
                holder.cover_music.clearAnimation();
                initNextMusicInfo(holder, file, position);
            }
        }
    }

    private void initNextMusicInfo(MusicPlayerViewHolder holder, MusicFile musicFile, int position) {
        holder.next_song_info_layout.setVisibility(View.VISIBLE);
        holder.tvSongName.setText(musicFile.getSongTitle());
        holder.tvArtist.setText(musicFile.getArtistName());

        holder.btn_play_music.setOnClickListener(v -> {
            if (musicFiles != null) {
                MusicPlaybackSettings musicPlaybackSettings = new MusicPlaybackSettings(context);
                if (musicPlaybackSettings.getShuffleMode().equals(ShuffleMode.SHUFFLE.name())) {
                    musicPlaybackSettings.setShuffleMode(ShuffleMode.OFF);
                }
                PlayMusicWithEqualizer playMusicWithEqualizer = new PlayMusicWithEqualizer(context);
                playMusicWithEqualizer.startMusicService(musicFiles,position,false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicFiles.size();
    }

    public static class MusicPlayerViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView cover_music;
        LinearLayoutCompat next_song_info_layout;
        AppCompatTextView tvSongName, tvArtist;
        MaterialButton btn_play_music;

        public MusicPlayerViewHolder(@NonNull View itemView) {
            super(itemView);

            cover_music = itemView.findViewById(R.id.cover_music);
            next_song_info_layout = itemView.findViewById(R.id.next_song_info_layout);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvArtist = itemView.findViewById(R.id.tvArtist);
            btn_play_music = itemView.findViewById(R.id.btn_play_music);
        }
    }
}
