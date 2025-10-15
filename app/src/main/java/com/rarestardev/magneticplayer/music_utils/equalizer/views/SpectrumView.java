package com.rarestardev.magneticplayer.music_utils.equalizer.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

public class SpectrumView extends View {

    private byte[] fftData;
    private final Paint paint = new Paint();
    private final int barCount = 32;
    private final int[] barHeights = new int[barCount];
    private final int[] colors = new int[barCount];

    public SpectrumView(Context context) {
        super(context);
        init();
    }

    public SpectrumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        for (int i = 0; i < barCount; i++) {
            colors[i] = Color.HSVToColor(new float[]{i * 360f / barCount, 1f, 1f});
        }
    }

    public void updateFFT(byte[] fft) {
        this.fftData = fft;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if (fftData == null) return;

        int width = getWidth();
        int height = getHeight();
        int barWidth = width / barCount;

        for (int i = 0; i < barCount; i++) {
            int index = 2 * i;
            if (index + 1 >= fftData.length) break;

            float re = fftData[index];
            float im = fftData[index + 1];
            float magnitude = (float) Math.sqrt(re * re + im * im);
            int barHeight = (int) (magnitude * height / 128f);

            barHeights[i] = (int) (0.7f * barHeights[i] + 0.3f * barHeight);

            paint.setColor(colors[i]);
            canvas.drawRect(
                    i * barWidth,
                    height - barHeights[i],
                    (i + 1) * barWidth,
                    height,
                    paint
            );
        }

        postInvalidateDelayed(16); // ~60fps
    }
}
