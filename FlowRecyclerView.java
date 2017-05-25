package com.tools.gifapp.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by liuwei64 on 2017/5/15.
 */

public class FlowRecyclerView extends RecyclerView {

    private OnScrollToBottomListener scrollToBottomListener;

    public FlowRecyclerView(Context context) {
        super(context);
        init();
    }

    public FlowRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlowRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init() {
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    int count = getLayoutManager().getItemCount();
                    int itemPosition = findLastVisibleItemPosition(getLayoutManager());
                    if (count <= getSpanCount(getLayoutManager()) * 3 + itemPosition) {
                        if (scrollToBottomListener != null) {
                            scrollToBottomListener.onScrollBottom(FlowRecyclerView.this);
                        }
                    }
                }
            }
        });
    }

    public static int findLastVisibleItemPosition(@NonNull LayoutManager layoutManager) {
        int position = -1;
        int spanCount;
        for (spanCount = getSpanCount(layoutManager) - 1; spanCount >= 0; --spanCount) {
            int lastVisibleItemPosition = findLastVisibleItemPosition(layoutManager, spanCount);
            if (position == -1 || position < lastVisibleItemPosition) {
                position = lastVisibleItemPosition;
            }
        }

        return position;
    }

    public static int findLastVisibleItemPosition(@NonNull LayoutManager layoutManager, int colIndex) {
        int position = (layoutManager instanceof StaggeredGridLayoutManager)
                ? ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null)[colIndex] :
                ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        return position;
    }

    public static int getSpanCount(@Nullable LayoutManager layoutManager) {
        int count;
        if ((layoutManager instanceof StaggeredGridLayoutManager)) {
            count = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else if ((layoutManager instanceof GridLayoutManager)) {
            count = ((GridLayoutManager) layoutManager).getSpanCount();
        } else {
            count = 1;
        }

        return count;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (layout instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) layout;
            addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    layoutManager.invalidateSpanAssignments();
                }
            });
            layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        }
    }

    public void setScrollToBottomListener(OnScrollToBottomListener scrollToBottomListener) {
        this.scrollToBottomListener = scrollToBottomListener;
    }

    public interface OnScrollToBottomListener {
        void onScrollBottom(RecyclerView recyclerView);
    }
}
