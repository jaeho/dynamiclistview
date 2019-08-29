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

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import dev.qwqw.dlv.DynamicListView.OnOverScrollListener;
import dev.qwqw.dlv.interfaces.DynamicListLayoutChild;

public class DynamicScrollView extends ScrollView implements DynamicListLayoutChild {

    private boolean mEnableBounce = true;
    OnOverScrollListener mOnScrollDynamicListView = null;
    protected int mOverScrollLength = 0;
    protected boolean mIsTouchedScroll = false;

    public DynamicScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DynamicScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicScrollView(Context context) {
        super(context);
        init();
    }

    @SuppressLint("NewApi")
    void init() {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public void setOnOverScrollListener(OnOverScrollListener onScrollDynamicListView) {

        this.mOnScrollDynamicListView = onScrollDynamicListView;
    }

    @Override
    public boolean reachedListTop() {

        return getScrollY() == 0;
    }

    @Override
    public boolean reachedListBottom() {

        return getChildCount() == 0 ? true : getScrollY() == (getChildAt(0).getHeight() - getHeight());
    }

    @SuppressLint("NewApi")
    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {

        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (!mEnableBounce)
            return;
        if (mOnScrollDynamicListView != null && !mIsTouchedScroll && clampedY)
            mOnScrollDynamicListView.onOverScrolled(mOverScrollLength);

        mOverScrollLength = 0;
        mIsTouchedScroll = false;
    }

    @SuppressLint("NewApi")
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

        mOverScrollLength = Math.abs(deltaY);
        mIsTouchedScroll = isTouchEvent;
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);
    }

}
