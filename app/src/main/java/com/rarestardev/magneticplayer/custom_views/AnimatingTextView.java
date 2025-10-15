package com.rarestardev.magneticplayer.custom_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.TranslateAnimation;
import androidx.appcompat.widget.AppCompatTextView;

public class AnimatingTextView extends AppCompatTextView {
    private static final int CHARACTER_LIMIT = 25;
    private static final long ANIMATION_DURATION = 10000;

    public AnimatingTextView(Context context) {
        super(context);
    }

    public AnimatingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (text.length() > CHARACTER_LIMIT) {
            startMarqueeAnimation();
        } else {
            clearAnimation();
        }
    }

    private void startMarqueeAnimation() {
        float textWidth = getPaint().measureText(getText().toString());
        float viewWidth = getWidth();

        TranslateAnimation animation = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, viewWidth,
                TranslateAnimation.ABSOLUTE, -textWidth,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, 0);

        animation.setDuration(ANIMATION_DURATION);
        animation.setRepeatCount(TranslateAnimation.INFINITE);
        animation.setRepeatMode(TranslateAnimation.RESTART);
        startAnimation(animation);
    }
}


