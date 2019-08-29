/*
 * Copyright 2013 Jaeho Choe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.qwqw.dlv;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

import dev.qwqw.dlv.DynamicListView.OnOverScrollListener;
import dev.qwqw.dlv.interfaces.DynamicListLayoutChild;

@TargetApi(VERSION_CODES.GINGERBREAD)
public class DynamicGridView extends GridView implements DynamicListLayoutChild {

    private OnOverScrollListener onScrollDynamicListView;
    protected int mOverScrollLength = 0;
    protected boolean mIsTouchedScroll = false;
    private boolean mVisibleScrollBar = false;
    private boolean mEnableBounce = true;
    private DynamicListLayout mDynamicListLayout = null;

    public DynamicGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DynamicGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicGridView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setFadingEdgeLength(0);
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1)
            setOverScrollMode(View.OVER_SCROLL_NEVER);
        else
            mEnableBounce = false;

        setVerticalScrollBarEnabled(mVisibleScrollBar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mDynamicListLayout == null)
            mDynamicListLayout = Util.findParents(this);
    }

    @Override
    public void setOnOverScrollListener(OnOverScrollListener onScrollDynamicListView) {
        this.onScrollDynamicListView = onScrollDynamicListView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDynamicListLayout != null && !mDynamicListLayout.isClosed() && ev.getAction() == MotionEvent.ACTION_DOWN)
            mDynamicListLayout.close();

        return super.onTouchEvent(ev);
    }

    @Override
    public boolean reachedListTop() {
        boolean flag = true;
        if (getChildCount() != 0) {
            int i = getFirstVisiblePosition();
            int j = getChildAt(0).getTop();
            if (i != 0 || j != getPaddingTop()) {
                flag = false;
            }
        }

        return flag;
    }

    @Override
    public boolean reachedListBottom() {
        // TODO Auto-generated method stub
        boolean flag = true;
        if (getChildCount() != 0) {
            int i = getLastVisiblePosition();
            int j = getCount();
            int k = getHeight();
            int l = getChildAt(-1 + getChildCount()).getBottom();
            if (i != j - 1 || l > k) {
                flag = false;
            }
        }

        return flag;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        // TODO Auto-generated method stub
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (!mEnableBounce)
            return;

        if (onScrollDynamicListView != null && !mIsTouchedScroll)
            onScrollDynamicListView.onOverScrolled(mOverScrollLength);

        mOverScrollLength = 0;
        mIsTouchedScroll = false;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        mOverScrollLength = Math.abs(deltaY);
        mIsTouchedScroll = isTouchEvent;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY,
                isTouchEvent);
    }

    public boolean isVisibleScrollBar() {
        return mVisibleScrollBar;
    }

    public void setVisibleScrollBar(boolean mVisibleScrollBar) {
        this.mVisibleScrollBar = mVisibleScrollBar;
    }

    public boolean isEnableBounce() {
        return mEnableBounce;
    }

    public void setEnableBounce(boolean mEnableBounce) {
        this.mEnableBounce = mEnableBounce;
    }

    public DynamicListLayout getDynamicListLayout() {
        return mDynamicListLayout;
    }

    public void setDynamicListLayout(DynamicListLayout mDynamicListLayout) {
        this.mDynamicListLayout = mDynamicListLayout;
    }
}
