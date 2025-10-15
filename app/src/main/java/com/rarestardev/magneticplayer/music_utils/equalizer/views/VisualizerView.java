package com.rarestardev.magneticplayer.music_utils.equalizer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class VisualizerView extends View {

    private byte[] waveform;
    private final Paint paint = new Paint();
    private final Path wavePath = new Path();
    private final int lineCount = 64;
    private final float[] amplitudes = new float[lineCount];
    private final int[] colors = new int[lineCount];

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f);
        paint.setAntiAlias(true);

        for (int i = 0; i < lineCount; i++) {
            colors[i] = Color.HSVToColor(new float[]{i * 360f / lineCount, 1f, 1f});
        }
    }

    public void updateWaveform(byte[] waveform) {
        this.waveform = waveform;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (waveform == null) return;

        int width = getWidth();
        int height = getHeight();
        float centerY = height / 2f;
        float spacing = width / (float) lineCount;

        wavePath.reset();

        for (int i = 0; i < lineCount; i++) {
            int index = i * waveform.length / lineCount;
            float value = waveform[index];
            float amplitude = value / 128f * centerY;

            amplitudes[i] = 0.7f * amplitudes[i] + 0.3f * amplitude;

            float x = i * spacing;
            float y = centerY - amplitudes[i];

            paint.setColor(colors[i]);
            canvas.drawLine(x, centerY, x, y, paint);
        }

        postInvalidateDelayed(16); // ~60fps
    }
}
