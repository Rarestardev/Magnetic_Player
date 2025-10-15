package com.rarestardev.magneticplayer.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rarestardev.magneticplayer.R;

public class MusicCoverManager {

    private final Context context;
    private static final int DEFAULT_BG_COLOR = R.color.window_background_night_mode;

    private ChangeColorCoverListener listener;

    public MusicCoverManager(Context context) {
        this.context = context;
    }

    public void setColorChangeListener(ChangeColorCoverListener listener){
        this.listener = listener;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void loadCoverAndSetBackground(String url, RoundedImageView imageView, View backgroundView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.ic_music)
                .placeholder(context.getDrawable(R.drawable.ic_music))
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        setBackgroundColorFromPalette(resource, backgroundView, context.getColor(DEFAULT_BG_COLOR));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        imageView.setImageResource(R.drawable.ic_music);
                        backgroundView.setBackgroundColor(context.getColor(DEFAULT_BG_COLOR));
                        if (listener != null){
                            listener.onChanged(0);
                        }
                    }
                });
    }

    private void setBackgroundColorFromPalette(Bitmap bitmap, View backgroundView, int defaultColor) {
        Palette.from(bitmap).generate(palette -> {
            int dominantColor = palette.getDominantColor(defaultColor);

            if (isNearWhiteOrGray(dominantColor)) {
                backgroundView.setBackgroundColor(defaultColor);
                if (listener != null){
                    listener.onChanged(defaultColor);
                }
            } else {
                dominantColor = darkenColorIfTooBright(dominantColor);
                backgroundView.setBackgroundColor(dominantColor);
                if (listener != null){
                    listener.onChanged(dominantColor);
                }
            }
        });
    }

    public interface ChangeColorCoverListener{

        void onChanged(int color);
    }

    private boolean isNearWhiteOrGray(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);


        return (red > 200 && green > 200 && blue > 200) ||
                (Math.abs(red - green) < 10 && Math.abs(red - blue) < 10 && Math.abs(green - blue) < 10 && red > 150);
    }

    private int darkenColorIfTooBright(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);


        if (red > 220 && green > 220 && blue > 220) {
            red = (int) (red * 1.0);
            green = (int) (green * 1.0);
            blue = (int) (blue * 1.0);
        }

        return Color.rgb(red, green, blue);
    }
}
