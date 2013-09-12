package net.dynamicandroid.listview;

import java.util.ArrayList;

import net.dynamicandroid.listview.DynamicListScrollDependencyView.DynamicListScrollDependencyViewItem;
import net.dynamicandroid.listview.DynamicListView.OnOverScrollListener;
import net.dynamicandroid.listview.animation.ScrollAnimation;
import net.dynamicandroid.listview.animation.ScrollAnimationItem;
import net.dynamicandroid.listview.animation.ScrollAnimationListener;
import net.dynamicandroid.listview.interfaces.DynamicListLayoutChild;
import net.dynamicandroid.listview.sortable.DynamicSortableListView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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
	private DynamicListViewListener mDynamicListViewListener;
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
	ScrollAnimation mPullingAnimationForClose;
	boolean mPullingIsNowClosing = false;
	private boolean isClosed = true;

	private int mDefaultHeaderHeight = DEFAULT_HEIGHT;
	private int mDefaultFooterHeight = DEFAULT_HEIGHT;

	GestureDetector gesture = null;
	float scrollDistance = 0F;
	private boolean initialized = false;
	DynamicScrollWatcher mScrollWatcher = new DynamicScrollWatcher();

	private AttributeSet mAttrs = null;
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

	void readAttributes() {
		if(mAttrs==null)
			return;
	}

	public DynamicListLayoutChild getDynamicListView() {
		return mDynamicListView;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mDynamicListView == null) 
			init();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	void init() {
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

	void addDynamicListView(DynamicListLayoutChild dynamicListView) {
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

	void addDynamicHeaderView(View view) {
		this.mHeader = view;
	}

	void addDynamicFooterView(View view) {
		this.mFooter = view;
	}

	void checkHeaderAndFooter() {
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

	private DynamicListScrollDependencyViewItem reservedDependencyItem;

	public void setDependencyView(View view, int gapX, int gapY, int gravity) {
		reservedDependencyItem = new DynamicListScrollDependencyViewItem(view, gapX, gapY, gravity);
	}

	public void setTopBackgroundViewScrollable(View view, int gapY, int gravity) {
		view.scrollTo(0, gapY);
		setDependencyView(view, 0, gapY, gravity);
	}

	public void setTopBackgroundView(View view, int gapY) {
		setTopBackgroundViewScrollable(view, gapY, 2);
	}

	void setTouchListenerToDynamicListView() {
		if (mDynamicListView != null)
			mDynamicListView.setOnTouchListener(this);
	}

	public void setmDynamicListView(DynamicListView dynamicListView) throws Exception {
		if (this.mDynamicListView != null) {
			return;
		}

		addDynamicListView(dynamicListView);
		setTouchListenerToDynamicListView();
	}

	void checkPullingState(int scrollY) {

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
		int bounceTarget = 0;

		boolean isTop = scrollDistance < 0;

		if (isTop) {
			bounceLength = -overScrollY;
			bounceTarget = overScrollY;
		} else {
			bounceLength = overScrollY;
			bounceTarget = -overScrollY;
		}

		ScrollAnimationItem item = new ScrollAnimationItem(mListFrame, bounceLength, bounceTarget, new ScrollAnimationListener() {
			@Override
			public void onAnimationEnd() {
				
				if (!mPullingOnTouchMove) {
					close();
				}
			}

			@Override
			public void onProgress() {
				
				checkPullingState(mListFrame.getScrollY());
			}
		}, mIsNowPullingDown ? ScrollAnimationItem.TOP : ScrollAnimationItem.BOTTOM);
		mPullingAnimationForClose = new ScrollAnimation();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			mPullingAnimationForClose.execute(item);
		else
			mPullingAnimationForClose.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);
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

		int orientation = ScrollAnimationItem.TOP;
		if (mListFrame.getScrollY() < 0)
			orientation = ScrollAnimationItem.TOP;
		else
			orientation = ScrollAnimationItem.BOTTOM;

		ScrollAnimationItem item = new ScrollAnimationItem(mListFrame, moveAmount, target, new ScrollAnimationListener() {
			@Override
			public void onAnimationEnd() {
				
				finishedClosed(completelyClosed);
			}

			@Override
			public void onProgress() {
				

			}
		}, orientation);
		mPullingAnimationForClose = new ScrollAnimation();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			mPullingAnimationForClose.execute(item);
		else
			mPullingAnimationForClose.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, item);

	}

	public boolean isClosed() {
		return mListFrame.getScrollY() == 0;
	}

	void finishedClosed(final boolean completelyClosed) {
		if (mDynamicListViewListener != null)
			mDynamicListViewListener.onCloesed(this, mDynamicListView, mIsNowPullingDown ? PullingMode.TOP : PullingMode.BOTTOM,
					completelyClosed);

		initializeData();
	}

	void initializeData() {
		mPullingAnimationForClose = null;
		mPullingIsNowClosing = false;

		mIsNowPullingDown = false;
		mIsNowPullingUp = false;

		mIsFirstPulling = false;

		if (mDynamicListViewListener != null)
			mDynamicListViewListener.onPullingStatusChanged(this, mDynamicListView, PullingStatus.END, null);
	}

	boolean isAvailableStatus() {
		if (mDynamicListView instanceof DynamicDragSelectionListView) {
			return !((DynamicDragSelectionListView) mDynamicListView).isDragMode();
		}

		return true;
	}

	private class DynamicScrollWatcher {
		private ScrollDirection scrollDirection = null;
		private float savedY = 0;

		public void setEvent(MotionEvent event, DynamicListViewListener listener) {
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
		 * ADDED BY jangc. cancle touch-event on start pulling.
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

	public boolean mIsFirstPulling = false;

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
				if(mDynamicListView instanceof DynamicSortableListView)
					if(((DynamicSortableListView) mDynamicListView).isDragging())
						return false;
				
				mIsNowPullingUp = false;
				mIsNowPullingDown = true;
				firstTouch(event);

				mIsFirstPulling = true;
			} else if (mDynamicListView.reachedListBottom() && (mPullingTouchYPosition - (int) event.getRawY() > 0)
					&& Math.abs(mPullingTouchYPosition - (int) event.getRawY()) > mMinPullingLength) {
				if(mDynamicListView instanceof DynamicSortableListView)
					if(((DynamicSortableListView) mDynamicListView).isDragging())
						return false;
				
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

	void firstTouch(MotionEvent event) {
		mNeedFirstTouch = false;
		if (mDynamicListView.reachedListTop() || mDynamicListView.reachedListBottom()) {
			if (mPullingAnimationForClose != null) {
				mPullingAnimationForClose.cancel(true);
				mPullingAnimationForClose = null;
			}

			mPullingScrollYPosition = mListFrame.getScrollY();
			mPullingTouchYPosition = (int) event.getRawY();
		}
	}

	void showHeader() {
		if (mHeaderFrame != null && mHeaderFrame.getVisibility() != View.VISIBLE)
			mHeaderFrame.setVisibility(View.VISIBLE);
	}

	void showFooter() {
		if (mFooterFrame != null && mFooterFrame.getVisibility() != View.VISIBLE)
			mFooterFrame.setVisibility(View.VISIBLE);
	}

	void pull(MotionEvent event) {
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
	void throwActionDownEventToListView(MotionEvent event) {
		int action = event.getAction();
		event.setAction(MotionEvent.ACTION_DOWN);
		((View) mDynamicListView).onTouchEvent(event);
		event.setAction(action);
	}

	int getPullRegistance(View view) {
		return mUseRegistance?getPullRegistance(view, mResistance.getValue()):1;
	}

	int getPullRegistance(View view, int multiple) {

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

	public static interface DynamicListViewListener {
		void onPullingStatusChanged(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingStatus status,
				PullingMode pulling);

		void onRelease(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingMode pulling, PullingStatus pullingStatus);

		void onCloesed(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingMode pulling, boolean completelyClosed);

		void onScrollDirectionChanged(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, ScrollDirection scrollDirection);
	}

	public DynamicListViewListener getOnPullingList() {
		return mDynamicListViewListener;
	}

	public void setDynamicListViewListener(DynamicListViewListener dynamicListViewListener) {
		this.mDynamicListViewListener = dynamicListViewListener;
	}

	public int getSensitivity() {
		return mSensitivity;
	}

	public void setSensitivity(int mSensitivity) {
		this.mSensitivity = mSensitivity;
	}

	public int getMinPullingLength() {
		return mMinPullingLength;
	}

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

	public void setLockPullingDown(boolean lockPulling) {
		this.mLockPullingDown = lockPulling;
	}

	public int getBounceLength() {
		return mBounceLength;
	}

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

	public void scrollToTop() {
		if (mDynamicListView == null)
			return;

		scrollDistance = -1;

		if (mDynamicListView instanceof AbsListView) {
			((AbsListView) mDynamicListView).smoothScrollBy(-getScreenHeight(getContext()) * 10, 2000);
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

	public static int getScreenHeight(Context context) {
		WindowManager display = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		return display.getDefaultDisplay().getHeight();
	}

	public static void setSimpleListener(final DynamicListLayout layout) {
		layout.setDynamicListViewListener(new DynamicListViewListener() {

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
	
	
}
