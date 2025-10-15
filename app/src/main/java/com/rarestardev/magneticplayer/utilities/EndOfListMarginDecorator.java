package com.rarestardev.magneticplayer.utilities;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EndOfListMarginDecorator extends RecyclerView.ItemDecoration {

    private static final int bottomMargin = 145;

    public EndOfListMarginDecorator() {

    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        RecyclerView.Adapter<?> adapter = parent.getAdapter();
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof LinearLayoutManager && adapter != null) {
            int position = parent.getChildAdapterPosition(view);
            if (position == adapter.getItemCount() - 1){
                outRect.bottom = bottomMargin;
            }else {
                outRect.bottom = 0;
            }
        }
    }
}
