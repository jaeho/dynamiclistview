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

package net.jful.dynamiclistview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import net.jful.dynamiclistview.interfaces.DynamicListLayoutChild;

/**
 * {@link net.jful.dynamiclistview.DynamicListView}
 *
 * @author jaehochoe
 * @see {@link DynamicListLayout} , {@link net.jful.dynamiclistview.DynamicListView.OnOverScrollListener}
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class DynamicListView extends ListView implements OnScrollListener, DynamicListLayoutChild {

    @Deprecated
    final int MIN_OVERSCROLL_LENGTH = 20;

    OnOverScrollListener mOnOverScrolled = null;
    OnScrollListener mOnScrollListener = null;
    OnScrollListener mOnScroll = null;

    protected int mOverScrollLength = 0;
    protected boolean mIsTouchedScroll = false;
    private boolean mVisibleScrollBar = false;
    private boolean mEnableBounce = true;

    private boolean mNeverScrolled = true;

    private DynamicListLayout mDynamicListLayout = null;

    public DynamicListLayout getDynamicListLayout() {
        return mDynamicListLayout;
    }

    public void setDynamicListLayout(DynamicListLayout mDynamicListLayout) {
        this.mDynamicListLayout = mDynamicListLayout;
    }

    public DynamicListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public DynamicListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public DynamicListView(Context context) {
        super(context);

        init();
    }

    @SuppressLint("NewApi")
    void init() {
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1)
            setOverScrollMode(View.OVER_SCROLL_NEVER);
        else
            mEnableBounce = false;

        setOnMainScrollListener(this);
        setVerticalScrollBarEnabled(mVisibleScrollBar);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mDynamicListLayout == null)
            mDynamicListLayout = Util.findParents(this);
    }

    @Override
    protected void layoutChildren() {

        try {
            super.layoutChildren();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        boolean result = false;

        if (mDynamicListLayout != null && !mDynamicListLayout.isClosed() && ev.getAction() == MotionEvent.ACTION_DOWN)
            mDynamicListLayout.close();

        try {
            result = super.onTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean reachedListBottom() {
        return Util.reachedListBottom(this);
    }

    public boolean reachedListTop() {
        return Util.reachedListTop(this);
    }

    public boolean reachedListEnds() {
        return Util.reachedListEnds(this);
    }

    public int getItemIndexAtLocation(int y) {
        return Util.getItemIndexAtLocation(this, y);
    }

    @Override
    public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {

        if (mVisibleScrollBar)
            if (reachedListBottom() || reachedListTop())
                setVerticalScrollBarEnabled(false);
            else
                setVerticalScrollBarEnabled(true);

        if (mOnScroll != null)
            mOnScroll.onScroll(arg0, arg1, arg2, arg3);
    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {

        if (mOnScroll != null)
            mOnScroll.onScrollStateChanged(arg0, arg1);

        if (arg1 != OnScrollListener.SCROLL_STATE_IDLE && mNeverScrolled)
            mNeverScrolled = false;
    }

    public static interface OnOverScrollListener {
        void onOverScrolled(int overScrollY);
    }

    public OnOverScrollListener getOnScrollDynamicListView() {
        return mOnOverScrolled;
    }

    public void setOnOverScrollListener(OnOverScrollListener onScrollDynamicListView) {
        this.mOnOverScrolled = onScrollDynamicListView;
    }

    public void setOnScrollListener(OnScrollListener l) {

        this.mOnScroll = l;
    }

    public void setOnMainScrollListener(OnScrollListener l) {

        super.setOnScrollListener(l);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {

        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (!mEnableBounce)
            return;

        if (mOnOverScrolled != null && !mIsTouchedScroll)
            mOnOverScrolled.onOverScrolled(mOverScrollLength);

        mOverScrollLength = 0;
        mIsTouchedScroll = false;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

        mOverScrollLength = Math.abs(deltaY);
        mIsTouchedScroll = isTouchEvent;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
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

    public void initScrolledStatus() {
        this.mNeverScrolled = true;
    }

    public boolean isNeverScrolled() {
        return this.mNeverScrolled;
    }

    public boolean isScrollable() {
        return isScrollable(0);
    }

    public boolean isScrollable(int bottomPadding) {
        return Util.isScrollable(this, bottomPadding);
    }
}
