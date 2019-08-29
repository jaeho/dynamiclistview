package dev.qwqw.dlv;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dev.qwqw.dlv.interfaces.DynamicListLayoutChild;

/**
 * Created by jaehochoe on 2019-08-23.
 */
public class DynamicRecyclerView extends RecyclerView implements DynamicListLayoutChild {

    protected int mOverScrollLength = 0;
    protected boolean mIsTouchedScroll = false;
    DynamicListView.OnOverScrollListener mOnOverScrolled = null;
    private boolean mEnableBounce = true;

    public DynamicRecyclerView(@NonNull Context context) {
        super(context);
        init();
    }

    public DynamicRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        super.setLayoutManager(new DynamicLinearLayoutManager(getContext()));
        super.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
    }

    @Override
    @Deprecated
    public void setOverScrollMode(int overScrollMode) {
    }

    @Override
    @Deprecated
    public void setLayoutManager(@Nullable LayoutManager layout) {
    }

    @Override
    public void setOnOverScrollListener(DynamicListView.OnOverScrollListener onScrollDynamicListView) {
        this.mOnOverScrolled = onScrollDynamicListView;
        ((DynamicLinearLayoutManager)getLayoutManager()).setOnOverScrollListener(onScrollDynamicListView);
    }

    @Override
    public boolean reachedListTop() {
        if (getChildCount() > 0) {
            LayoutManager lm = getLayoutManager();
            int firstVisiblePosition = -1;
            int firstVisibleViewY = -1;
            if (lm instanceof LinearLayoutManager) {
                firstVisiblePosition = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
            } else if (lm instanceof GridLayoutManager) {
                firstVisiblePosition = ((LinearLayoutManager) lm).findFirstVisibleItemPosition();
            }
            firstVisibleViewY = getChildAt(0).getTop();
            return firstVisiblePosition == 0 && firstVisibleViewY == getPaddingTop();
        } else
            return false;
    }

    @Override
    public boolean reachedListBottom() {
        if (getChildCount() > 0) {
            LayoutManager lm = getLayoutManager();
            int lastVisiblePosition = -1;
            int lastVisibleViewY = -1;
            if (lm instanceof LinearLayoutManager) {
                lastVisiblePosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
            } else if (lm instanceof GridLayoutManager) {
                lastVisiblePosition = ((LinearLayoutManager) lm).findLastVisibleItemPosition();
            }
            lastVisibleViewY = getChildAt(getChildCount() - 1).getBottom();
            int height = getHeight();

            return lastVisiblePosition == lm.getItemCount() - 1 && lastVisibleViewY <= height;
        } else
            return false;
    }

    public boolean isEnableBounce() {
        return mEnableBounce;
    }

    public void setEnableBounce(boolean mEnableBounce) {
        this.mEnableBounce = mEnableBounce;
    }
}
