package com.rarestardev.magneticplayer.controller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.helper.AnimationHelper;
import com.rarestardev.magneticplayer.model.MusicFile;
import com.rarestardev.magneticplayer.utilities.Constants;
import com.rarestardev.magneticplayer.view.activities.MusicPlayerActivity;

/**
 * Manages the UI for music information display.
 */
public class MusicUiManager {

    private final Context context;
    private MusicFile musicFile;
    private boolean isPlaying;
    private ViewStub viewStub;
    private final AnimationHelper animationHelper;
    private View inflateView;
    private OnPlayPauseMusicListener listener;

    private AppCompatImageView play_pause_music, next_music, previous_music;
    private AppCompatTextView music_name_ui, artist_name_ui;
    private FrameLayout control_music_layout;
    private RoundedImageView cover_music;

    public MusicUiManager(Context context) {
        this.context = context;
        animationHelper = new AnimationHelper();
    }

    public void getViewStub(ViewStub viewStub) {
        this.viewStub = viewStub;
    }

    public void getIsPlayMusic(boolean isPlaying) {
        this.isPlaying = isPlaying;
        updateUI();
    }

    public void getMusicData(MusicFile musicFile) {
        this.musicFile = musicFile;
        checkViews();
        updateUI();
    }

    public void setListener(OnPlayPauseMusicListener listener) {
        this.listener = listener;
    }

    private void checkViews() {
        if (viewStub != null) {
            if (inflateView == null) {
                try {
                    inflateView = viewStub.inflate();
                    if (inflateView != null) {
                        initViews();
                        animationHelper.rotateAnimCoverMusic(cover_music);
                        updateUI();
                    } else {
                        Log.e(Constants.appLog, "MusicUiManager: inflation failed");
                    }
                } catch (Exception e) {
                    Log.e(Constants.appLog, "MusicUiManager: Exception during inflation", e);
                }
            }
        } else {
            Log.e(Constants.appLog, "MusicUiManager: viewStub is null");
        }
    }

    private void initViews() {
        play_pause_music = inflateView.findViewById(R.id.play_pause_music);
        next_music = inflateView.findViewById(R.id.next_music);
        previous_music = inflateView.findViewById(R.id.previous_music);
        music_name_ui = inflateView.findViewById(R.id.music_name_ui);
        artist_name_ui = inflateView.findViewById(R.id.artist_name_ui);
        control_music_layout = inflateView.findViewById(R.id.control_music_layout);
        cover_music = inflateView.findViewById(R.id.cover_music);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void updateUI() {
        if (musicFile == null) {
            return;
        }

        handleViewsWithAnimation();

        music_name_ui.setText(musicFile.getSongTitle());
        artist_name_ui.setText(musicFile.getArtistName());

        Glide.with(context)
                .load(musicFile.getAlbumArtUri())
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .into(cover_music);

        control_music_layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.putExtra("MusicFile", musicFile);
            intent.putExtra("isPlay", isPlaying);
            context.startActivity(intent);
            ((Activity) context).overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        });

        play_pause_music.setOnClickListener(v -> {
            listener.onClickViewPlayPause();
            handleViewsWithAnimation();
        });
        next_music.setOnClickListener(v -> listener.onClickNext());
        previous_music.setOnClickListener(v -> listener.onClickPrevious());
    }

    private void handleViewsWithAnimation() {
        if (isPlaying) {
            animationHelper.animateAndChangeIcon(context, play_pause_music, R.drawable.ic_pause);
            animationHelper.resumeRotation();
        } else {
            animationHelper.animateAndChangeIcon(context, play_pause_music, R.drawable.ic_play_circle_filled);
            animationHelper.pauseRotation();
        }
    }

    public interface OnPlayPauseMusicListener {
        void onClickViewPlayPause();

        void onClickNext();

        void onClickPrevious();
    }
}
