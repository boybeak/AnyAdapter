package com.github.boybeak.pexels.widget;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Beak on 2015/7/9.
 */
public abstract class OnScrollBottomListener extends RecyclerView.OnScrollListener {

    private static final String TAG = OnScrollBottomListener.class.getSimpleName();

    private int mLastDy = 0;

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        View lastChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
        final int positionInAdapter = recyclerView.getChildAdapterPosition(lastChild);
        if (recyclerView.getAdapter() != null
                && positionInAdapter == recyclerView.getAdapter().getItemCount() - 1
                && newState == RecyclerView.SCROLL_STATE_IDLE && mLastDy > 0) {
            onScrollBottom(recyclerView, newState);
        }
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy != 0) {
            mLastDy = dy;
        }
    }

    public abstract void onScrollBottom (@NonNull RecyclerView recyclerView, int newState);

}