package com.rarestardev.magneticplayer.custom_views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.rarestardev.magneticplayer.R;

public class SemiCircleProgressBar extends View {
    private Paint paintBackground, paintProgress, paintIndicator;
    private RectF rectF;
    private float sweepAngle = 0, maxProgress = 100;
    private final float indicatorRadius = 14;
    private float currentIndicatorRadius = indicatorRadius;
    private OnProgressChangedListener onProgressChangedListener;

    public SemiCircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        paintBackground = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBackground.setColor(Color.GRAY);
        paintBackground.setStyle(Paint.Style.STROKE);
        paintBackground.setStrokeWidth(6);

        paintProgress = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintProgress.setColor(context.getColor(R.color.progress_color));
        paintProgress.setStyle(Paint.Style.STROKE);
        paintProgress.setStrokeWidth(6);

        paintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintIndicator.setColor(context.getColor(R.color.progress_color));
        paintIndicator.setStyle(Paint.Style.FILL);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth(), height = getHeight();
        float radius = Math.min(width, height) / 2 - 20;
        rectF.set(width / 2 - radius, height / 2 - radius, width / 2 + radius, height / 2 + radius);

        final int startAngle = 0;
        canvas.drawArc(rectF, startAngle, 288, false, paintBackground);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, paintProgress);

        float indicatorX = (float) (width / 2 + radius * Math.cos(Math.toRadians(startAngle + sweepAngle)));
        float indicatorY = (float) (height / 2 + radius * Math.sin(Math.toRadians(startAngle + sweepAngle)));
        canvas.drawCircle(indicatorX, indicatorY, currentIndicatorRadius, paintIndicator);
    }

    public void setProgress(float progress) {
        sweepAngle = Math.min(progress, maxProgress) * 288 / maxProgress;
        invalidate();
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float centerX = (float) getWidth() / 2;
        float centerY = (float) getHeight() / 2;
        float radius = (float) Math.min(getWidth(), getHeight()) / 2 - 20;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                float distance = (float) Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
                if (distance >= radius - 100 && distance <= radius + 100) {
                    float angle = (float) Math.toDegrees(Math.atan2(y - centerY, x - centerX));
                    angle = (angle + 360) % 360;
                    if (angle >= 0 && angle <= 288) {
                        float progress = angle * maxProgress / 288;
                        setProgress(progress);

                        if (onProgressChangedListener != null) {
                            onProgressChangedListener.onProgressChanged(progress);
                        }
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                currentIndicatorRadius = indicatorRadius;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        this.onProgressChangedListener = listener;
    }

    public interface OnProgressChangedListener {
        void onProgressChanged(float progress);
    }
}




