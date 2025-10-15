package com.rarestardev.magneticplayer.music_utils.music.adapters;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.helper.PlayMusicWithEqualizer;
import com.rarestardev.magneticplayer.helper.MusicFileAdapterHelper;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.viewmodel.MusicStatusViewModel;

import java.util.List;

import eu.gsottbauer.equalizerview.EqualizerView;

public class MusicFileAdapter extends RecyclerView.Adapter<MusicFileAdapter.MusicViewHolder> {

    private final Context context;
    private List<MusicFile> musicFiles;
    private int listValue;
    private MusicStatusViewModel musicStatusViewModel;

    private boolean isRotateAnimationActive = false;

    public MusicFileAdapter(Context context) {
        this.context = context;
    }

    public void setList(List<MusicFile> musicFiles, int listValue) {
        this.musicFiles = musicFiles;
        this.listValue = listValue;
    }

    public void setMusicStatusViewModel(MusicStatusViewModel musicStatusViewModel) {
        this.musicStatusViewModel = musicStatusViewModel;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRotateAnimationActive(boolean rotateAnimationActive) {
        isRotateAnimationActive = rotateAnimationActive;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_music, viewGroup, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        MusicFile musicFile = musicFiles.get(position);

        holder.tvFileName.setText(musicFile.getSongTitle());
        holder.tvAlbumName.setText(String.format("%s | %s", musicFile.getArtistName(), musicFile.getAlbumName()));

        Glide.with(context)
                .load(musicFile.getAlbumArtUri())
                .placeholder(R.drawable.ic_music)
                .thumbnail(0.1f)
                .into(holder.albumArt);

        holder.itemView.setOnClickListener(v -> {
            PlayMusicWithEqualizer playMusicWithEqualizer = new PlayMusicWithEqualizer(context);
            playMusicWithEqualizer.startMusicService(musicFiles, position,false);
        });

        holder.itemView.setOnLongClickListener(v -> {
            MusicFileAdapterHelper adapterHelper = new MusicFileAdapterHelper((context));
            adapterHelper.showOptionMenu(musicFiles, position);
            return true;
        });

        holder.is_playing_music.setVisibility(View.GONE);

        RotateAnimation animation = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        animation.setDuration(20000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setInterpolator(new AccelerateInterpolator());

        musicStatusViewModel.getIsPlayMusic().observe((LifecycleOwner) context, aBoolean -> {
            if (aBoolean) {
                musicStatusViewModel.getMusicInfo().observe((LifecycleOwner) context, musicFile1 -> {
                    if (musicFile1 != null) {
                        if (musicFile1.getFilePath() != null) {
                            if (musicFile1.getFilePath().equals(musicFile.getFilePath())) {
                                if (isRotateAnimationActive) {
                                    handlePlayMusicItem(holder, animation);
                                } else {
                                    handlePlayMusicItem(holder, null);
                                }
                            } else {
                                handlePauseMusicItem(holder);
                            }
                        } else {
                            handlePauseMusicItem(holder);
                        }
                    }
                });
            } else {
                handlePauseMusicItem(holder);
            }
        });
    }

    private void handlePlayMusicItem(MusicViewHolder holder, RotateAnimation anim) {
        holder.is_playing_music.setVisibility(View.VISIBLE);
        holder.is_playing_music.animateBars();

        ValueAnimator animator = ValueAnimator.ofFloat(24f, 100f);
        animator.setDuration(800);
        animator.addUpdateListener(animation ->
                holder.albumArt.setCornerRadius((Float) animation.getAnimatedValue()));

        animator.start();

        new Handler().postAtTime(() -> {
            if (anim == null) {
                holder.albumArt.clearAnimation();
            } else {
                holder.albumArt.setAnimation(anim);
            }
        }, 900);
    }

    private void handlePauseMusicItem(MusicViewHolder holder) {
        holder.is_playing_music.setVisibility(View.GONE);
        holder.is_playing_music.stopBars();
        holder.albumArt.setCornerRadius(24f);
        holder.albumArt.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return Math.min(musicFiles.size(), listValue);
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView albumArt;
        AppCompatTextView tvFileName, tvAlbumName;
        EqualizerView is_playing_music;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);

            albumArt = itemView.findViewById(R.id.albumArt);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            tvAlbumName = itemView.findViewById(R.id.tvAlbumName);
            is_playing_music = itemView.findViewById(R.id.is_playing_music);
        }
    }
}
