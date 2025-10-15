package com.rarestardev.magneticplayer.music_utils.music;

import android.content.Context;

import androidx.appcompat.widget.AppCompatImageView;

import com.rarestardev.magneticplayer.R;
import com.rarestardev.magneticplayer.enums.ShuffleMode;
import com.rarestardev.magneticplayer.helper.AnimationHelper;

public class ShuffleModeHelper {

    private AppCompatImageView targetView;
    private final MusicPlaybackSettings musicPlaybackSettings;
    private final Context context;
    private StatusMessageListener messageListener;
    private final AnimationHelper animationHelper;

    public ShuffleModeHelper(Context context) {
        this.context = context;
        musicPlaybackSettings = new MusicPlaybackSettings(context);
        animationHelper = new AnimationHelper(context);
    }

    public void setTargetView(AppCompatImageView targetView) {
        this.targetView = targetView;
    }

    public void setMessageListener(StatusMessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void shuffleManager() {
        if (musicPlaybackSettings.getShuffleMode().equals(ShuffleMode.SHUFFLE.name())) {
            animationHelper.animateAndChangeIcon(targetView,R.drawable.ic_round_shuffle);
            targetView.setOnClickListener(v -> {
                musicPlaybackSettings.setShuffleMode(ShuffleMode.REPEAT);
                messageListener.onMessage(context.getString(R.string.repeat_music));
                shuffleManager();
            });
        } else if (musicPlaybackSettings.getShuffleMode().equals(ShuffleMode.REPEAT.name())) {
            animationHelper.animateAndChangeIcon(targetView,R.drawable.ic_repeat);
            targetView.setOnClickListener(v -> {
                musicPlaybackSettings.setShuffleMode(ShuffleMode.OFF);
                messageListener.onMessage(context.getString(R.string.off_all));
                shuffleManager();
            });
        } else {
            animationHelper.animateAndChangeIcon(targetView,R.drawable.ic_repeat_off);
            targetView.setOnClickListener(v -> {
                musicPlaybackSettings.setShuffleMode(ShuffleMode.SHUFFLE);
                messageListener.onMessage(context.getString(R.string.shuffle_play));
                shuffleManager();
            });
        }
    }

    public interface StatusMessageListener {
        void onMessage(String msg);
    }
}
