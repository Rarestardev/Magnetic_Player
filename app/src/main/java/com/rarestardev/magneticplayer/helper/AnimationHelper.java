package com.rarestardev.magneticplayer.helper;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.rarestardev.magneticplayer.R;

public class AnimationHelper {

    private ObjectAnimator rotationAnimator;

    public AnimationHelper() {
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

    public void animateAndChangeIcon(Context context, AppCompatImageView imageView, int drawable) {
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
                if (drawable != 0){
                    imageView.setImageDrawable(context.getDrawable(drawable));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void animateAndChangeColor(Context context, AppCompatImageView imageView, int color) {
        Animation rotateAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        imageView.startAnimation(rotateAnimation);

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.getDrawable().setTint(context.getColor(color));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void hideViewWithAnimation(View targetView) {
        targetView.animate()
                .translationX(-targetView.getWidth())
                .alpha(0.0f)
                .setDuration(400)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animator) { }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animator) {
                        targetView.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animator) { }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animator) { }
                });
    }

    public void showViewWithAnimation(View targetView) {
        targetView.setVisibility(View.VISIBLE);
        targetView.setAlpha(0.0f);
        targetView.setTranslationX(-targetView.getWidth());
        targetView.animate()
                .translationX(0)
                .alpha(1.0f)
                .setDuration(400)
                .setListener(null);
    }
}

