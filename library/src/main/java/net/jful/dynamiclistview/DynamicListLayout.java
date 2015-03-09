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

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import net.jful.dynamiclistview.DynamicListScrollDependencyView.DynamicListScrollDependencyViewItem;
import net.jful.dynamiclistview.DynamicListView.OnOverScrollListener;
import net.jful.dynamiclistview.interfaces.DynamicListLayoutChild;
import net.jful.dynamiclistview.interfaces.Listener;

import java.util.ArrayList;

public class DynamicListLayout extends FrameLayout implements OnTouchListener {

    private boolean mUseRegistance = true;

    static final int DEFAULT_HEIGHT = 77;
    static final int UI_DELAY = 200;

    public static enum ScrollDirection {
        UP, DOWN
    }

    public static enum PullingStatus {
        START, END, ON, OFF
    }

    public static enum PullingMode {
        TOP, BOTTOM
    }

    public static enum ListPullingResistance {
        HIGH(1), LOW(2), NONE(3);

        private int value = 0;

        private ListPullingResistance(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private DynamicListLayoutChild mDynamicListView = null;

    private DynamicListScrollDependencyView mListFrame = null;
    private FrameLayout mHeaderFrame = null;
    private LinearLayout mFooterFrame = null;
    private View mHeader = null;
    private View mFooter = null;
    private int mHeaderHeight = -1;
    private int mFooterHeight = -1;
    private Listener mDynamicListViewListener;
    boolean mCompletelyClosed = true;
    boolean mNeedFirstTouch = true;

    private int mSensitivity = 0;
    private int mMinPullingLength = 40;
    private int mBounceLength = 100;
    private ListPullingResistance mResistance = ListPullingResistance.HIGH;

    private boolean mLockPullingDown = false;

    boolean mPullingNeedFirstTouch = false;
    PullingStatus mPullingState = PullingStatus.OFF;
    boolean mIsNowPullingDown = false;
    boolean mIsNowPullingUp = false;
    int mPullingTouchYPosition, mPullingScrollYPosition = 0;
    private boolean mPullingOnTouchMove = false;
    boolean mPullingIsNowClosing = false;

    private int mDefaultHeaderHeight = DEFAULT_HEIGHT;
    private int mDefaultFooterHeight = DEFAULT_HEIGHT;

    GestureDetector gesture = null;
    float scrollDistance = 0F;
    private boolean initialized = false;
    DynamicScrollWatcher mScrollWatcher = new DynamicScrollWatcher();

    private AttributeSet mAttrs = null;
    private DynamicListScrollDependencyViewItem reservedDependencyItem;
    public boolean mIsFirstPulling = false;

    public DynamicListLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAttrs = attrs;
        readAttributes();
    }

    public DynamicListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mAttrs = attrs;
        readAttributes();
    }

    public DynamicListLayout(Context context) {
        super(context);
    }

    private void readAttributes() {
        if (mAttrs == null)
            return;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mDynamicListView == null)
            init();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void init() {
        gesture = new GestureDetector(getContext(), new OnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                scrollDistance = distanceY;
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(0);
            if (child instanceof DynamicListLayoutChild) {
                DynamicListLayoutChild dynamicListView = (DynamicListLayoutChild) child;
                removeView((View) dynamicListView);
                addDynamicListView(dynamicListView);
            } else if (mHeader == null) {
                removeView(child);
                addDynamicHeaderView(child);
            } else {
                removeView(child);
                addDynamicFooterView(child);
            }
        }

        setTouchListenerToDynamicListView();
    }

    private void addDynamicListView(DynamicListLayoutChild dynamicListView) {
        this.mDynamicListView = dynamicListView;
        mDynamicListView.setOnOverScrollListener(new OnOverScrollListener() {
            @Override
            public void onOverScrolled(int overScrollY) {
                previewOpen(overScrollY);
            }
        });

        mListFrame = new DynamicListScrollDependencyView(getContext());
        mListFrame.addView((View) mDynamicListView);
        addView(mListFrame);
    }

    private void addDynamicHeaderView(View view) {
        this.mHeader = view;
    }

    private void addDynamicFooterView(View view) {
        this.mFooter = view;
    }

    private void checkHeaderAndFooter() {
        if (mHeader != null && mHeaderFrame == null) {
            if (this.mHeaderHeight == -1) {
                if (mHeader.getLayoutParams().height == LayoutParams.MATCH_PARENT
                        || mHeader.getLayoutParams().height == LayoutParams.WRAP_CONTENT)
                    this.mHeaderHeight = mDefaultHeaderHeight;
                else
                    this.mHeaderHeight = mHeader.getLayoutParams().height;
            }
            mHeaderFrame = new FrameLayout(getContext());
            mHeaderFrame.addView(mHeader);
            addView(mHeaderFrame, 0);
            mHeaderFrame.scrollTo(0, mHeaderHeight);
        }

        if (mFooter != null && mFooterFrame == null) {
            if (this.mFooterHeight == -1) {
                if (mFooter.getLayoutParams().height == LayoutParams.MATCH_PARENT
                        || mFooter.getLayoutParams().height == LayoutParams.WRAP_CONTENT)
                    this.mFooterHeight = mDefaultFooterHeight;
                else
                    this.mFooterHeight = mFooter.getLayoutParams().height;
            }
            mFooterFrame = new LinearLayout(getContext());
            mFooterFrame.addView(mFooter);
            addView(mFooterFrame);
            mFooterFrame.setGravity(Gravity.BOTTOM);
            mFooterFrame.scrollTo(0, -mFooterHeight);
        }

        if (mListFrame != null && mHeaderFrame != null && mListFrame.getDependencyViews() == null) {
            ArrayList<DynamicListScrollDependencyViewItem> items = new ArrayList<DynamicListScrollDependencyView.DynamicListScrollDependencyViewItem>();
            if (mHeaderFrame != null)
                items.add(new DynamicListScrollDependencyViewItem(mHeaderFrame, 0, mHeaderHeight));
            if (mFooterFrame != null)
                items.add(new DynamicListScrollDependencyViewItem(mFooterFrame, 0, -mFooterHeight));
            if (reservedDependencyItem != null) {
                items.add(reservedDependencyItem);
            }

            mListFrame.setDependencyViews(items);

            showHeader();
            showFooter();
        }

        initialized = true;
    }

    private void setDependencyView(View view, int gapX, int gapY, int gravity) {
        reservedDependencyItem = new DynamicListScrollDependencyViewItem(view, gapX, gapY, gravity);
    }

    public void setTopBackgroundViewScrollable(View view, int gapY, int gravity) {
        view.scrollTo(0, gapY);
        setDependencyView(view, 0, gapY, gravity);
    }

    private void setTouchListenerToDynamicListView() {
        if (mDynamicListView != null)
            mDynamicListView.setOnTouchListener(this);
    }

    private void checkPullingState(int scrollY) {

        if (mIsNowPullingDown) {
            if (mPullingState == PullingStatus.OFF && -scrollY > mHeaderHeight + mSensitivity) {
                mPullingState = PullingStatus.ON;
                if (mDynamicListViewListener != null)
                    mDynamicListViewListener.onPullingStatusChanged(this, mDynamicListView, PullingStatus.ON, PullingMode.TOP);
            } else if (mPullingState == PullingStatus.ON && -scrollY < mHeaderHeight + mSensitivity) {
                mPullingState = PullingStatus.OFF;
                if (mDynamicListViewListener != null)
                    mDynamicListViewListener.onPullingStatusChanged(this, mDynamicListView, PullingStatus.OFF, PullingMode.TOP);
            }
        } else {
            if (mPullingState == PullingStatus.OFF && scrollY > mFooterHeight + mSensitivity) {
                mPullingState = PullingStatus.ON;
                if (mDynamicListViewListener != null)
                    mDynamicListViewListener.onPullingStatusChanged(this, mDynamicListView, PullingStatus.ON, PullingMode.BOTTOM);
            } else if (mPullingState == PullingStatus.ON && scrollY < mFooterHeight + mSensitivity) {
                mPullingState = PullingStatus.OFF;
                if (mDynamicListViewListener != null)
                    mDynamicListViewListener.onPullingStatusChanged(this, mDynamicListView, PullingStatus.OFF, PullingMode.BOTTOM);
            }
        }

    }

    @SuppressLint("NewApi")
    public void previewOpen(int overScrollY) {
        if (mListFrame.getScrollY() != 0)
            return;

        int bounceLength = 0;

        boolean isTop = scrollDistance < 0;

        if (isTop) {
            bounceLength = -overScrollY;
        } else {
            bounceLength = overScrollY;
        }

        animate(mListFrame.getScrollY(), mListFrame.getScrollY()+bounceLength, new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mPullingOnTouchMove) {
                    close();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void animate(float fromY, float toY, AnimatorListener listener) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromY, toY);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                mListFrame.scrollTo(0, (int) val);
                checkPullingState(mListFrame.getScrollY());
            }
        });
        animator.addListener(listener);
        animator.start();
    }

    public void openHeader() {
        mListFrame.scrollTo(0, -mHeaderHeight);
    }

    public void openFooter() {
        mListFrame.scrollTo(0, mFooterHeight);
    }

    public void rightNowClosePlz() {
        mListFrame.scrollTo(0, 0);
    }

    public void close() {
        close(true);
    }

    @SuppressLint("NewApi")
    public void close(final boolean completelyClosed) {
        if (mListFrame == null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {

                    close(completelyClosed);
                }
            }, UI_DELAY);
            return;
        }

        if (!initialized)
            checkHeaderAndFooter();
        else if (mListFrame.getScrollY() == 0) {
            finishedClosed(completelyClosed);
            return;
        }

        this.mCompletelyClosed = completelyClosed;

        if (mPullingOnTouchMove)
            return;

        mPullingIsNowClosing = true;
        int moveAmount = 0 - mListFrame.getScrollY();
        moveAmount = !completelyClosed ? -mHeaderHeight + moveAmount : moveAmount;
        int target = !completelyClosed ? mHeaderHeight : 0;
        animate(mListFrame.getScrollY(), mListFrame.getScrollY() + moveAmount, new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finishedClosed(completelyClosed);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void finishedClosed(final boolean completelyClosed) {
        if (mDynamicListViewListener != null)
            mDynamicListViewListener.onCloesed(this, mDynamicListView, mIsNowPullingDown ? PullingMode.TOP : PullingMode.BOTTOM,
                    completelyClosed);

        initializeData();
    }

    private void initializeData() {
        mPullingIsNowClosing = false;

        mIsNowPullingDown = false;
        mIsNowPullingUp = false;

        mIsFirstPulling = false;

        if (mDynamicListViewListener != null)
            mDynamicListViewListener.onPullingStatusChanged(this, mDynamicListView, PullingStatus.END, null);
    }

    /**
     * You can use this method when the situation what should not use pulling.
     *
     * @return default : true
     */
    private boolean isAvailableStatus() {
//		if (mDynamicListView instanceof DynamicDragSelectionListView) {
//			return !((DynamicDragSelectionListView) mDynamicListView).isDragMode();
//		}
        return true;
    }

    private class DynamicScrollWatcher {
        private ScrollDirection scrollDirection = null;
        private float savedY = 0;

        public void setEvent(MotionEvent event, Listener listener) {
            float y = event.getY();

            if (savedY == 0)
                savedY = y;

            if (Math.abs(savedY - y) < 10)
                return;

            ScrollDirection nowDirection = savedY > event.getY() ? ScrollDirection.DOWN : ScrollDirection.UP;

            if (nowDirection != this.scrollDirection && listener != null)
                listener.onScrollDirectionChanged(DynamicListLayout.this, mDynamicListView, nowDirection);

            this.scrollDirection = nowDirection;
            this.savedY = event.getY();
        }

        public void init() {
            savedY = 0;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        /**
         * ADDED BY jangc. 
         * Cancel touch-event on start pulling.
         */
        if (mIsNowPullingDown || mIsNowPullingUp) {
            boolean isFirstPulling = mIsFirstPulling;
            onTouch(null, event);
            if (isFirstPulling) {
                if (mDynamicListViewListener != null)
                    mDynamicListViewListener.onPullingStatusChanged(this, mDynamicListView, PullingStatus.START, null);

                mIsFirstPulling = false;
                event.setAction(MotionEvent.ACTION_CANCEL);
                ((View) mDynamicListView).onTouchEvent(event);
            }

            return true;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (!initialized)
            checkHeaderAndFooter();

        gesture.onTouchEvent(event);

        if (!isAvailableStatus())
            return super.onTouchEvent(event);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                firstTouch(event);
                break;

            case MotionEvent.ACTION_MOVE:
                mScrollWatcher.setEvent(event, mDynamicListViewListener);

                if (mNeedFirstTouch)
                    firstTouch(event);

                mPullingOnTouchMove = true;

                if (mIsNowPullingDown || mIsNowPullingUp) {
                    pull(event);
                    return true;
                }

                if (mDynamicListView.reachedListTop() && (mPullingTouchYPosition - (int) event.getRawY() < 0)
                        && Math.abs(mPullingTouchYPosition - (int) event.getRawY()) > mMinPullingLength) {
                    mIsNowPullingUp = false;
                    mIsNowPullingDown = true;
                    firstTouch(event);

                    mIsFirstPulling = true;
                } else if (mDynamicListView.reachedListBottom() && (mPullingTouchYPosition - (int) event.getRawY() > 0)
                        && Math.abs(mPullingTouchYPosition - (int) event.getRawY()) > mMinPullingLength) {
                    mIsNowPullingDown = false;
                    mIsNowPullingUp = true;
                    firstTouch(event);

                    mIsFirstPulling = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsFirstPulling = false;
                mScrollWatcher.init();

                mPullingOnTouchMove = false;
                mNeedFirstTouch = true;
                if (mIsNowPullingDown || mIsNowPullingUp) {
                    if (mPullingState == PullingStatus.ON) {
                        if (mDynamicListViewListener != null)
                            mDynamicListViewListener.onRelease(this, mDynamicListView,
                                    mIsNowPullingDown ? PullingMode.TOP : PullingMode.BOTTOM, mPullingState);
                        else
                            close();

                        mPullingState = PullingStatus.OFF;
                    } else if (mPullingState == PullingStatus.OFF) {
                        close();
                    }
                } else {
                }

                break;
        }

        return false;
    }

    private void firstTouch(MotionEvent event) {
        mNeedFirstTouch = false;
        if (mDynamicListView.reachedListTop() || mDynamicListView.reachedListBottom()) {
            mPullingScrollYPosition = mListFrame.getScrollY();
            mPullingTouchYPosition = (int) event.getRawY();
        }
    }

    private void showHeader() {
        if (mHeaderFrame != null && mHeaderFrame.getVisibility() != View.VISIBLE)
            mHeaderFrame.setVisibility(View.VISIBLE);
    }

    private void showFooter() {
        if (mFooterFrame != null && mFooterFrame.getVisibility() != View.VISIBLE)
            mFooterFrame.setVisibility(View.VISIBLE);
    }

    private void pull(MotionEvent event) {
        int divide = getPullRegistance(mListFrame);
        int scrollY = 0;
        if (mIsNowPullingDown) {
            if (isLockPullingDown())
                return;
            scrollY = mPullingScrollYPosition
                    + ((mPullingTouchYPosition - (int) event.getRawY()) / ((mPullingTouchYPosition - (int) event.getRawY()) < 0 ? divide
                    : 1));
        } else {
            scrollY = mPullingScrollYPosition
                    + ((mPullingTouchYPosition - (int) event.getRawY()) / ((mPullingTouchYPosition - (int) event.getRawY()) > 0 ? divide
                    : 1));
        }

        if ((scrollY > 0 && mIsNowPullingDown) || (scrollY < 0 && mIsNowPullingUp)) {
            scrollY = 0;
            initializeData();
            throwActionDownEventToListView(event);
        }

        mListFrame.scrollTo(0, scrollY);

        mPullingTouchYPosition = (int) event.getRawY();
        mPullingScrollYPosition = mListFrame.getScrollY();

        checkPullingState(scrollY);
    }

    /**
     * ADDED BY jangc
     */
    private void throwActionDownEventToListView(MotionEvent event) {
        int action = event.getAction();
        event.setAction(MotionEvent.ACTION_DOWN);
        ((View) mDynamicListView).onTouchEvent(event);
        event.setAction(action);
    }

    private int getPullRegistance(View view) {
        return mUseRegistance ? getPullRegistance(view, mResistance.getValue()) : 1;
    }

    private int getPullRegistance(View view, int multiple) {

        int rule = 5;
        int divide = 1;
        if (view.getScrollY() != 0) {
            divide = view.getHeight() / Math.abs(view.getScrollY());

            if (divide > 10)
                divide = 10;

            divide = rule - (divide / 2);
            divide = divide / multiple;

            if (divide == 0)
                divide = 1;
        }

        return divide;
    }

    /**
     * Check the list is closed.
     *
     * @return true : list is closed /
     * false : list isn't closed.
     */
    public boolean isClosed() {
        return mListFrame.getScrollY() == 0;
    }

    public Listener getListener() {
        return mDynamicListViewListener;
    }

    public void setListener(Listener dynamicListViewListener) {
        this.mDynamicListViewListener = dynamicListViewListener;
    }

    public int getSensitivity() {
        return mSensitivity;
    }

    /**
     * This method is to set the sensitivity.
     * Higher the number, the lot must draw.
     */
    public void setSensitivity(int mSensitivity) {
        this.mSensitivity = mSensitivity;
    }

    public int getMinPullingLength() {
        return mMinPullingLength;
    }

    /**
     * This method is to set the min-length for pulling.
     * Higher the number, the lot must draw.
     */
    public void setMinPullingLength(int mMinPullingLength) {
        this.mMinPullingLength = mMinPullingLength;
    }

    public ListPullingResistance getResistance() {
        return mResistance;
    }

    public void setResistance(ListPullingResistance mResistance) {
        this.mResistance = mResistance;
    }

    public boolean isLockPullingDown() {
        return mLockPullingDown;
    }

    /**
     * Pulling-Function is used to avoid using.
     *
     * @param lockPulling
     */
    public void setLockPullingDown(boolean lockPulling) {
        this.mLockPullingDown = lockPulling;
    }

    public int getBounceLength() {
        return mBounceLength;
    }

    /**
     * Bounce to set the distance.
     *
     * @param mBounceLength
     */
    public void setBounceLength(int mBounceLength) {
        this.mBounceLength = mBounceLength;
    }

    public int getCustomHeaderHeight() {
        return mDefaultHeaderHeight;
    }

    public void setCustomHeaderHeight(int mCustomHeaderHeight) {
        this.mDefaultHeaderHeight = mCustomHeaderHeight;
    }

    public int getCustomFooterHeight() {
        return mDefaultFooterHeight;
    }

    public void setCustomFooterHeight(int mCustomFooterHeight) {
        this.mDefaultFooterHeight = mCustomFooterHeight;
    }

    /**
     * Scroll to the top of the list.
     */
    public void scrollToTop() {
        if (mDynamicListView == null)
            return;

        scrollDistance = -1;

        if (mDynamicListView instanceof AbsListView) {
            ((AbsListView) mDynamicListView).smoothScrollBy(-Util.getScreenHeight(getContext()) * 10, 2000);
            postDelayed(scrollListToTop, 800);
        } else if (mDynamicListView instanceof ScrollView) {
            ((ScrollView) mDynamicListView).smoothScrollTo(0, 0);
            postDelayed(scrollScrollViewToTop, 800);
        }
    }

    Runnable scrollListToTop = new Runnable() {
        @Override
        public void run() {

            ((AbsListView) mDynamicListView).setSelection(0);
        }
    };

    Runnable scrollScrollViewToTop = new Runnable() {
        @Override
        public void run() {

            ((ScrollView) mDynamicListView).scrollTo(0, 0);
        }
    };

    /**
     * This method uses the default listener.
     *
     * @param layout
     */
    public static void setSimpleListener(final DynamicListLayout layout) {
        layout.setListener(new Listener() {

            @Override
            public void onScrollDirectionChanged(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView,
                                                 ScrollDirection scrollDirection) {


            }

            @Override
            public void onRelease(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingMode pulling,
                                  PullingStatus pullingStatus) {

                if (pulling == PullingMode.TOP && pullingStatus == PullingStatus.ON) {
                    layout.close(false);
                } else
                    layout.close();
            }

            @Override
            public void onCloesed(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingMode pulling,
                                  boolean completelyClosed) {

            }

            @Override
            public void onPullingStatusChanged(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingStatus status,
                                               PullingMode pulling) {

            }
        });
    }

    public boolean isUseRegistance() {
        return mUseRegistance;
    }

    public void setUseRegistance(boolean mUseRegistance) {
        this.mUseRegistance = mUseRegistance;
    }

    public DynamicListLayoutChild getDynamicListView() {
        return mDynamicListView;
    }

    public void setTopBackgroundView(View view, int gapY) {
        setTopBackgroundViewScrollable(view, gapY, 2);
    }
}
