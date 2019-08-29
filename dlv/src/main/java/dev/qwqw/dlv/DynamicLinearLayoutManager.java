package dev.qwqw.dlv;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by jaehochoe on 2019-08-23.
 */
public class DynamicLinearLayoutManager extends LinearLayoutManager {

    DynamicListView.OnOverScrollListener mOnOverScrolled = null;

    public DynamicLinearLayoutManager(Context context) {
        super(context);
    }

    public void setOnOverScrollListener(DynamicListView.OnOverScrollListener onScrollDynamicListView) {
        this.mOnOverScrolled = onScrollDynamicListView;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int scrollRange = super.scrollVerticallyBy(dy, recycler, state);
        int overScroll = dy - scrollRange;
        mOnOverScrolled.onOverScrolled(overScroll > 0 ? overScroll : (-2 * overScroll));
        return scrollRange;
    }
}
