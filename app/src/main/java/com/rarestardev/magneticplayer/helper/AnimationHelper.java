package com.rarestardev.magneticplayer.helper;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.widget.AppCompatImageView;

import com.rarestardev.magneticplayer.R;

public class AnimationHelper {

    private ObjectAnimator rotationAnimator;
    private final Context context;

    public AnimationHelper(Context context) {
        this.context = context;
    }

    public void rotateAnimCoverMusic(View coverMusic) {
        rotationAnimator = ObjectAnimator.ofFloat(coverMusic, "rotation", 0f, 360f);
        rotationAnimator.setDuration(20000);
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        rotationAnimator.setRepeatMode(ObjectAnimator.RESTART);
        rotationAnimator.start();
    }

    public void pauseRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.pause();
        }
    }

    public void resumeRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.resume();
        }
    }

    public void stopRotation() {
        if (rotationAnimator != null) {
            rotationAnimator.cancel();
        }
    }

    public void animateAndChangeIcon(AppCompatImageView imageView, int drawable) {
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        if (imageView == null) return;
        imageView.startAnimation(rotateAnimation);

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onAnimationEnd(Animation animation) {
                if (drawable != 0) {
                    imageView.setImageDrawable(context.getDrawable(drawable));
                }
            }


            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void simpleRotateAnimation(AppCompatImageView imageView) {
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        imageView.startAnimation(rotateAnimation);
    }
}

