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
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;

import dev.qwqw.dlv.DynamicListView.OnOverScrollListener;
import dev.qwqw.dlv.interfaces.BaseSectionAdapter;
import dev.qwqw.dlv.interfaces.DynamicListLayoutChild;

@TargetApi(VERSION_CODES.GINGERBREAD)
public class DynamicSectionListView extends ExpandableListView implements OnScrollListener, DynamicListLayoutChild {

    static final int DEFAULT_HEIGHT = DynamicListLayout.DEFAULT_HEIGHT / 2;

    BaseSectionAdapter mAdapter;
    private View mMaskView;
    private boolean mMaskViewVisible = true;

    private int mMaskViewWidth;
    private int mMaskViewHeight;
    private int mMaskBottomY;
    private int mMaskTopY;

    OnOverScrollListener mOnOverScrolled = null;
    OnScrollListener mOnScrollListener = null;
    OnScrollListener mOnScroll = null;

    protected int mOverScrollLength = 0;
    protected boolean mIsTouchedScroll = false;
    private boolean mVisibleScrollBar = false;
    private boolean mEnableBounce = true;
    private boolean mHideFloatingLabel = false;

    private View mFixedHeaderView, mHeaderView;
    private int mFixedHeaderId = -1;

    private boolean mAlwaysExpanded = true;
    private int mDefaultSectionHeight = DEFAULT_HEIGHT;

    private DynamicListLayout mDynamicListLayout = null;

    public void addFixedHeaderView(View v) {

        this.mFixedHeaderView = v;
    }

    public DynamicSectionListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public DynamicSectionListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicSectionListView(Context context) {
        super(context);
        init();
    }

    @SuppressLint("NewApi")
    void init() {
        if (VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1)
            setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        else
            mEnableBounce = false;

        setOnMainScrollListener(this);
        setVerticalScrollBarEnabled(mVisibleScrollBar);

        setGroupIndicator(null);
        setChildIndicator(null);
        setChildIndicatorBounds(0, 0);
    }

    @Override
    public void setAdapter(ExpandableListAdapter adapter) {

        if (!(adapter instanceof BaseSectionAdapter)) {
            System.err.print("SectionListView need BaseSectionAdapter");
            return;
        }
        super.setAdapter(adapter);
        mAdapter = (BaseSectionAdapter) adapter;

        if (mAlwaysExpanded) {
            openGroup();
            fixOpenedGroup();
        }
    }

    public void openGroup() {
        for (int i = 0; i < ((ExpandableListAdapter) mAdapter).getGroupCount(); i++)
            expandGroup(i);
    }

    public void fixOpenedGroup() {
        setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView arg0, View arg1, int arg2, long arg3) {

                expandGroup(arg2);
                return true;
            }
        });
    }

    public void setPinnedHeaderView(View view) {
        mMaskView = view;
        if (mMaskView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mDynamicListLayout == null)
            mDynamicListLayout = Util.findParents(this);

        if (mMaskView != null) {
            measureChild(mMaskView, widthMeasureSpec, heightMeasureSpec);
            mMaskViewWidth = mMaskView.getMeasuredWidth();
            mMaskViewHeight = mMaskView.getMeasuredHeight();
            if (mMaskViewHeight == LayoutParams.MATCH_PARENT || mMaskViewHeight == LayoutParams.WRAP_CONTENT)
                mMaskViewHeight = mDefaultSectionHeight;

            mMaskBottomY = mMaskViewHeight;

            if (mFixedHeaderView != null) {
                mMaskTopY = mFixedHeaderView.getMeasuredHeight();
                mMaskBottomY += mMaskTopY;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mMaskView != null) {
            mMaskView.layout(0, mMaskTopY, mMaskViewWidth, mMaskViewHeight);
            getPinnedHeaderState();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mMaskViewVisible && mMaskView != null) {
            drawChild(canvas, mMaskView, getDrawingTime());
        }
    }

    public void getPinnedHeaderState() {
        if (mMaskView == null)
            return;

        int position = getFirstVisiblePosition();
        if (position < 0 || getCount() == 0 || (mFixedHeaderView == null ? false : mFixedHeaderView.getVisibility() != View.VISIBLE)
                || mHideFloatingLabel || getFirstVisiblePosition() == getHeaderViewsCount() - 1) {
            mMaskViewVisible = false;
            return;
        }

        GroupInfo info = getFirstGroupPosition();
        if (info == null) {
            mMaskViewVisible = true;
            mMaskView.layout(0, mMaskTopY, mMaskViewWidth, mMaskViewHeight + mMaskTopY);
            mAdapter.onChangedSection(mMaskView, getPackedPositionGroup(getExpandableListPosition(position)), -256);
            requestLayout();
            return;
        }

        int listPosition = info.listPosition;
        if (((ExpandableListAdapter) mAdapter).getChildrenCount(info.groupPosition) == 0 || !isGroupExpanded(info.groupPosition)) {
            listPosition++;
            info.groupPosition++;
        }

        View groupSection = getChildAt(listPosition);
        if (groupSection != null) {
            int y = 0;
            int alpha = 0;
            if (mMaskBottomY >= groupSection.getTop()) {
                y = groupSection.getTop() - mMaskBottomY;
                if (groupSection.getTop() != 0 && -y < mMaskViewHeight) {
                    alpha = 255 * (mMaskViewHeight + y) / mMaskViewHeight;
                    if (alpha < 0)
                        alpha = 0;
                } else
                    alpha = 255;
            } else {
                y = 0;
                alpha = 255;
            }

            if (mMaskView.getTop() != y) {
                if (-y > mMaskViewHeight) {
                    mMaskView.layout(0, mMaskTopY, mMaskViewWidth, mMaskViewHeight + mMaskTopY);
                    mAdapter.onChangedSection(mMaskView, info.groupPosition, 255);
                } else {
                    int groupPostion = info.groupPosition - 1;
                    if (groupPostion >= 0) {
                        mAdapter.onChangedSection(mMaskView, groupPostion, alpha);
                    }
                    mMaskView.layout(0, mMaskTopY + y, mMaskViewWidth, mMaskViewHeight + y + mMaskTopY);
                }
                requestLayout();
            }
            mMaskViewVisible = true;
            return;
        }

    }

    GroupInfo getFirstGroupPosition() {

        for (int i = getFirstVisiblePosition(); i < getLastVisiblePosition(); i++) {
            long id = getExpandableListPosition(i);
            if (getPackedPositionType(id) == PACKED_POSITION_TYPE_GROUP)
                return new GroupInfo(i - getFirstVisiblePosition(), getPackedPositionGroup(id));
        }

        return null;
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
        getPinnedHeaderState();

        if (mVisibleScrollBar)
            if (reachedListBottom() || reachedListTop())
                setVerticalScrollBarEnabled(false);
            else
                setVerticalScrollBarEnabled(true);

        if (mFixedHeaderView != null && mHeaderView != null && mFixedHeaderId != -1) {
            int[] location1 = new int[2];
            int[] location2 = new int[2];
            if (mHeaderView != null) {
                mHeaderView.findViewById(mFixedHeaderId).getLocationInWindow(location1);
                mFixedHeaderView.getLocationInWindow(location2);

                if (location1[1] < location2[1]) {
                    mFixedHeaderView.setVisibility(View.VISIBLE);
                    mHeaderView.setVisibility(View.INVISIBLE);
                } else {
                    mFixedHeaderView.setVisibility(View.INVISIBLE);
                    mHeaderView.setVisibility(View.VISIBLE);
                }
            }
        }

        if (mOnScroll != null)
            mOnScroll.onScroll(arg0, arg1, arg2, arg3);
    }

    @Override
    public void onScrollStateChanged(AbsListView arg0, int arg1) {

        if (mOnScroll != null)
            mOnScroll.onScrollStateChanged(arg0, arg1);
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

    class GroupInfo {
        public int listPosition;
        public int groupPosition;

        private GroupInfo(int listPosition, int groupPosition) {
            super();
            this.listPosition = listPosition;
            this.groupPosition = groupPosition;
        }
    }

    public boolean isAlwaysExpanded() {
        return mAlwaysExpanded;
    }

    public void setAlwaysExpanded(boolean alwaysExpanded) {
        this.mAlwaysExpanded = alwaysExpanded;
    }

    public int getDefaultSectionHeight() {
        return mDefaultSectionHeight;
    }

    public void setDefaultSectionHeight(int mDefaultSectionHeight) {
        this.mDefaultSectionHeight = mDefaultSectionHeight;
    }

    @Override
    public void addHeaderView(View v) {
        super.addHeaderView(v);
        this.mHeaderView = v;
    }

    @Override
    public void addHeaderView(View v, Object data, boolean isSelectable) {
        super.addHeaderView(v, data, isSelectable);
        this.mHeaderView = v;
    }

    public int getFixedHeaderId() {
        return mFixedHeaderId;
    }

    public void setFixedHeaderId(int mFixedHeaderId) {
        this.mFixedHeaderId = mFixedHeaderId;
    }

    public boolean isHideFloatingLabel() {
        return mHideFloatingLabel;
    }

    public void setHideFloatingLabel(boolean hideFloatingLabel) {
        this.mHideFloatingLabel = hideFloatingLabel;
    }

}
