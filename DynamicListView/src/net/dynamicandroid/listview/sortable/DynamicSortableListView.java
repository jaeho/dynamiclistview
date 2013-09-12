package net.dynamicandroid.listview.sortable;

import net.dynamicandroid.listview.DynamicListView;
import net.dynamicandroid.listview.R;
import net.dynamicandroid.listview.animation.MoveAnimation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListAdapter;

/**
 * @author jaehochoe
 * @see {@link AnimateDrawable} , {@link ProxyDrawable} , {@link MoveAnimation}
 */
public class DynamicSortableListView extends DynamicListView implements AbsListView.OnScrollListener, AbsListView.RecyclerListener {

	private final float ANIMATION_ALPHA = 0.8F;
	private final long ANIMATION_DURATION = 250L;
	private final int SHADOW_ALPHA = 150;
	private final int SCROLL_LENGTH = 16;
	private final int REVERSE_SCROLL_LENGTH1 = -12;
	private final int REVERSE_SCROLL_LENGTH2 = -4;

	private int mGrabberViewRes = -1;
	private int mShadowImageRes = R.drawable.listshadow;
	
	public static enum SortableListViewScrollSensitivity {
		HIGH(2), LOW(1);

		private int value;

		private SortableListViewScrollSensitivity(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static SortableListViewScrollSensitivity fromValue(int value) {
			for (SortableListViewScrollSensitivity sortableListViewScrollSensitivity : values())
				if (sortableListViewScrollSensitivity.getValue() == value)
					return sortableListViewScrollSensitivity;

			return HIGH;
		}
	}

	private AnimateDrawable mDragDrawable;
	private int mDragItemType;
	private int mDragPosition;
	private int mDragY;
	private boolean mDragging = false;
	private int mFirstPos;
	private int mItemHeight;
	private int mItemHalfHeight;
	private int mLastPos;
	private int mLowerBound;
	private int mOffsetYInDraggingItem;
	private OnOrderChangedListener mOnOrderChangedListener;
	private AbsListView.OnScrollListener mOnScrollListener;
	private AnimateDrawable mShadowDrawable;
	private int mSelectedPosition;
	private int mUpperBound;

	private int mScrollSensitivity = SortableListViewScrollSensitivity.HIGH.value;

	private Integer[] mExceptionalPosition = null;
	
	public DynamicSortableListView(Context context) {
		this(context, null);
		init();
	}

	public DynamicSortableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	void init() {
		super.setOnScrollListener(this);
		super.setRecyclerListener(this);
	}

	public int getGrabberViewRes() {
		return mGrabberViewRes;
	}

	public void setGrabberViewRes(int mGrabberViewRes) {
		this.mGrabberViewRes = mGrabberViewRes;
	}

	public int getShadowImageRes() {
		return mShadowImageRes;
	}

	public void setShadowImageRes(int mShadowImageRes) {
		this.mShadowImageRes = mShadowImageRes;
	}

	public void setExeptionalPosition(Integer... exceptionalPosition) {
		this.mExceptionalPosition = exceptionalPosition;
	}
	
	private void adjustScrollBounds() {
		this.mUpperBound = this.mItemHeight * mScrollSensitivity;
		this.mLowerBound = (getBottom() - this.mItemHeight * mScrollSensitivity);
	}

	private void updateDraggingToPosition(int dragPos) {
		if (dragPos == -1)
			return;

		int i = this.mDragPosition;
		if (this.mDragPosition < dragPos) {
			this.mDragPosition = (1 + this.mDragPosition);
			animateItem(i, dragPos);
		} else if (this.mDragPosition > dragPos) {
			this.mDragPosition = (-1 + this.mDragPosition);
			animateItem(i, dragPos);
		}
	}

	private void animateItem(int beforePos, int newPos) {
		if(mExceptionalPosition!=null) {
			
			for(int i : mExceptionalPosition) {
				if(beforePos==i || newPos==i)
					return;
			}
			
		};
		
		if (this.mSelectedPosition < this.mDragPosition) {
			if (beforePos < newPos) {
				if (!reverseAnimation(this.mDragPosition)) {
					Animation case1Animation = createAnimation(0, 0, 0, -this.mItemHeight);
					setViewAnimationByPisition(this.mDragPosition, case1Animation);
				}
			} else {
				if (!reverseAnimation(1 + this.mDragPosition)) {
					Animation case2Animation = createAnimation(0, 0, -this.mItemHeight, 0);
					setViewAnimationByPisition(1 + this.mDragPosition, case2Animation);
				}
			}
		} else if (this.mSelectedPosition > this.mDragPosition) {
			if (beforePos < newPos) {
				if (!reverseAnimation(-1 + this.mDragPosition)) {
					Animation case3Animation = createAnimation(0, 0, this.mItemHeight, 0);
					setViewAnimationByPisition(-1 + this.mDragPosition, case3Animation);
				}
			} else {
				if (!reverseAnimation(this.mDragPosition)) {
					Animation case4Animation = createAnimation(0, 0, 0, this.mItemHeight);
					setViewAnimationByPisition(this.mDragPosition, case4Animation);
				}
			}
		} else {
			if (beforePos < newPos) {
				Animation case5Animation = createAnimation(0, 0, this.mItemHeight, 0);
				setViewAnimationByPisition(-1 + this.mDragPosition, case5Animation);
			} else {
				Animation case6Animation = createAnimation(0, 0, -this.mItemHeight, 0);
				setViewAnimationByPisition(1 + this.mDragPosition, case6Animation);
			}
		}
	}

	private Animation createAnimation(int fromX, int toX, int fromY, int toY) {
		return new MoveAnimation(fromX, toX, fromY, toY);
	}

	private void dragDrawable(int x, int y) {
		int i = y - this.mOffsetYInDraggingItem;
		Rect rect1 = new Rect(0, i, this.mDragDrawable.getProxy().getIntrinsicWidth(), i + this.mItemHeight);
		this.mDragDrawable.getProxy().setBounds(rect1);
		Rect rect2 = new Rect();
		this.mShadowDrawable.getProxy().getPadding(rect2);
		Rect rect3 = new Rect();
		rect3.left = 0;
		rect3.top = (i - rect2.top);
		rect3.right = getWidth();
		rect3.bottom = (i + this.mItemHeight + rect2.bottom);
		this.mShadowDrawable.getProxy().setBounds(rect3);
		invalidate();
	}

	private int getItemForPosition(int y) {
		int index = -1;

		if (getCount() <= 0)
			return index;

		int firstVisiblePosition = getFirstVisiblePosition();

		for (int i = firstVisiblePosition; i <= getLastVisiblePosition(); i++) {
			View view = getChildAt(i - firstVisiblePosition);
			if (y > view.getTop() && y < view.getBottom()) {
				return index = i;
			}
		}

		return -1;
	}

	private boolean reverseAnimation(int position) {
		if (getChildAt(position - getFirstVisiblePosition()) == null)
			return false;

		Animation reverseAnimation = getChildAt(position - getFirstVisiblePosition()).getAnimation();

		if ((reverseAnimation != null) && (reverseAnimation.hasStarted()) && (!reverseAnimation.hasEnded())) {
			((MoveAnimation) reverseAnimation).reverse();
			return true;
		}
		return false;
	}

	private void reverseScroll(int x, int y) {
		int firstPosition = this.mFirstPos;
		this.mFirstPos = x;
		int lastPosition = this.mLastPos;
		this.mLastPos = y;
		int itemPosition = getItemForPosition(this.mDragY);
		if ((this.mSelectedPosition < this.mDragPosition) && (x < firstPosition)) {
			if (getAdapter().getItemViewType(x) != this.mDragItemType)
				return;
			Animation animation1 = createAnimation(0, 0, 0, -this.mItemHeight);
			animation1.setDuration(0L);
			setViewAnimationByPisition(x, animation1);
		} else {
			updateDraggingToPosition(itemPosition);
			if ((this.mSelectedPosition > this.mDragPosition) && (y > lastPosition)) {
				if (getAdapter().getItemViewType(y) != this.mDragItemType)
					return;
				Animation animation2 = createAnimation(0, 0, 0, this.mItemHeight);
				animation2.setDuration(0L);
				setViewAnimationByPisition(y, animation2);
			}
		}
	}

	private void setViewAnimationByPisition(int position, Animation animation) {
		final View view = getChildAt(position - getFirstVisiblePosition());
		if (view != null) {
			view.startAnimation(animation);
		}
	}

	private void startDragging(Bitmap bitmap, int x, int y) {
		int i = y - this.mOffsetYInDraggingItem;
		
		Drawable shadow = getResources().getDrawable(mShadowImageRes);
		
		Rect rect1 = new Rect();
		shadow.getPadding(rect1);
		
		Rect rect2 = new Rect();
		rect2.left = 0;
		rect2.top = (i - rect1.top);
		rect2.right = bitmap.getWidth();
		rect2.bottom = (i + this.mItemHeight + rect1.bottom);
		
		shadow.setAlpha(SHADOW_ALPHA);
		shadow.setBounds(rect2);
		
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0F, 1.0F);
		alphaAnimation.setDuration(ANIMATION_DURATION);
		this.mShadowDrawable = new AnimateDrawable(shadow, alphaAnimation);
		alphaAnimation.startNow();
		Rect rect3 = new Rect(0, i, bitmap.getWidth(), i + bitmap.getHeight());
		this.mDragDrawable = new AnimateDrawable(new BitmapDrawable(getResources(), bitmap));
		this.mDragDrawable.getProxy().setBounds(rect3);
		AlphaAnimation alphaAnimation2 = new AlphaAnimation(1.0F, ANIMATION_ALPHA);
		alphaAnimation2.setDuration(ANIMATION_DURATION);
		this.mDragDrawable.setAnimation(alphaAnimation2);
		alphaAnimation2.startNow();
		invalidate(rect3);
	}

	public boolean isDragging() {
		return mDragging;
	}
	
	public void stopDragging() {
		this.mDragging = false;

		int i = 0;
		if (this.mDragDrawable != null) {
			if (this.mDragPosition >= getFirstVisiblePosition()) {
				if (getLastVisiblePosition() < this.mDragPosition)
					i = getBottom() - getListPaddingBottom();
				else {

					int position = this.mDragPosition - getFirstVisiblePosition();

					if (getFooterViewsCount() > 0)
						if (getFirstVisiblePosition() + position == getCount() - 1)
							position--;
					
					if(mExceptionalPosition!=null && position==mExceptionalPosition[0]) {
						position++;
						this.mDragPosition++;
					}
					
					i = getChildAt(position).getTop();
				}
			} else
				i = getTop() + getListPaddingTop() - this.mItemHeight;

			int toY = i - this.mDragDrawable.getProxy().getBounds().top;
			final AnimationSet rowFixAnimation = new AnimationSet(true);
			TranslateAnimation trans1 = new TranslateAnimation(0.0F, 0.0F, 0.0F, toY);
			AlphaAnimation fadeIn = new AlphaAnimation(ANIMATION_ALPHA, 1.0F);
			rowFixAnimation.addAnimation(fadeIn);
			rowFixAnimation.addAnimation(trans1);
			rowFixAnimation.setDuration(ANIMATION_DURATION);
			rowFixAnimation.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationEnd(Animation animation) {
					endDragging();
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationStart(Animation animation) {
				}
			});
			rowFixAnimation.initialize(10, 10, 10, 10);
			this.mDragDrawable.setAnimation(rowFixAnimation);

			final AnimationSet shadowFixAnimation = new AnimationSet(true);
			TranslateAnimation trans2 = new TranslateAnimation(0.0F, 0.0F, 0.0F, toY);
			AlphaAnimation fadeOut = new AlphaAnimation(1.0F, 0.0F);
			shadowFixAnimation.addAnimation(fadeOut);
			shadowFixAnimation.addAnimation(trans2);
			shadowFixAnimation.setDuration(ANIMATION_DURATION);
			shadowFixAnimation.initialize(10, 10, 10, 10);
			this.mShadowDrawable.setAnimation(shadowFixAnimation);

			rowFixAnimation.start();
			shadowFixAnimation.start();
			invalidate();
		}
	}

	void endDragging() {
		DynamicSortableListView.this.mDragDrawable = null;
		DynamicSortableListView.this.mShadowDrawable = null;
		if ((DynamicSortableListView.this.mOnOrderChangedListener != null) && (DynamicSortableListView.this.mDragPosition >= 0)
				&& (DynamicSortableListView.this.mDragPosition < DynamicSortableListView.this.getCount()))
			DynamicSortableListView.this.mOnOrderChangedListener.onOrderChanged(DynamicSortableListView.this.mSelectedPosition,
					DynamicSortableListView.this.mDragPosition);
		DynamicSortableListView.this.mSelectedPosition = -1;
		int i = DynamicSortableListView.this.getChildCount();
		for (int j = 0;; j++) {
			if (j >= i) {
				DynamicSortableListView.this.invalidate();
				return;
			}
			View view = DynamicSortableListView.this.getChildAt(j);
			view.setVisibility(0);
			view.clearAnimation();
		}
	}

	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (this.mDragDrawable != null) {
			this.mShadowDrawable.draw(canvas);
			this.mDragDrawable.draw(canvas);
			if ((this.mDragDrawable != null) && (this.mDragDrawable.hasStarted()) && (!this.mDragDrawable.hasEnded()))
				invalidate();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if ((this.mOnOrderChangedListener != null) && (this.mDragDrawable == null) && mGrabberViewRes!=-1) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				int x = (int) ev.getX();
				int y = (int) ev.getY();
				int rawX = (int) ev.getRawX();
				int rawY = (int) ev.getRawY();
				int position = pointToPosition(x, y);
				if (position != -1) {
					View view1 = getChildAt(position - getFirstVisiblePosition());
					View handle = view1.findViewById(mGrabberViewRes);
					if ((handle != null) && (handle.getVisibility() == 0)) {
						Rect rect = new Rect();
						handle.getGlobalVisibleRect(rect);
						if (rect.contains(rawX, rawY)) {
							this.mItemHeight = view1.getHeight();
							this.mItemHalfHeight = (this.mItemHeight / 2);
							this.mDragging = true;
							view1.destroyDrawingCache();
							view1.buildDrawingCache();
							startDragging(Bitmap.createBitmap(view1.getDrawingCache()), x, y);
							view1.setVisibility(View.INVISIBLE);
							this.mDragY = y;
							this.mOffsetYInDraggingItem = (y - view1.getTop());
							this.mSelectedPosition = position;
							this.mDragPosition = position;
							this.mFirstPos = getFirstVisiblePosition();
							this.mLastPos = getLastVisiblePosition();
							this.mDragItemType = getAdapter().getItemViewType(this.mDragPosition);
							adjustScrollBounds();
							return false;
						}
					}
					stopDragging();
				}
				break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (this.mDragging)
			reverseScroll(firstVisibleItem, -1 + (firstVisibleItem + visibleItemCount));
		if (this.mOnScrollListener != null)
			this.mOnScrollListener.onScroll(listView, firstVisibleItem, visibleItemCount, totalItemCount);
	}

	public void onScrollStateChanged(AbsListView listView, int scrollState) {
		if (this.mOnScrollListener != null)
			this.mOnScrollListener.onScrollStateChanged(listView, scrollState);
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if ((this.mOnOrderChangedListener != null) && (this.mDragging)) {
			if (MotionEvent.ACTION_UP == ev.getAction()) {
				stopDragging();
				return true;
			}

			int i = (int) ev.getX();
			int j = (int) ev.getY();
			this.mDragY = j;
			dragDrawable(i, j);
			updateDraggingToPosition(getItemForPosition(j));
			adjustScrollBounds();
			if ((this.mDragY < this.mUpperBound) || (this.mDragY > this.mLowerBound))
				scroll();
		}
		return super.onTouchEvent(ev);
	}

	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

	public void setOnOrderChangedListener(OnOrderChangedListener onOrderChangedListener) {
		this.mOnOrderChangedListener = onOrderChangedListener;
	}

	public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
		this.mOnScrollListener = onScrollListener;
	}

	private void scroll() {
		removeCallbacks(this.mScrollRunnable);
		post(this.mScrollRunnable);
	}

	private Runnable mScrollRunnable = new Runnable() {
		public void run() {
			if (!DynamicSortableListView.this.mDragging)
				return;

			DynamicSortableListView.this.doScroll();
			DynamicSortableListView.this.scroll();
		}
	};

	private boolean doScroll() {
		int moveAmount = 0;
		int position = 0;

		if (this.mDragY > this.mLowerBound) {
			if (getLastVisiblePosition() == -1 + getCount()
					&& getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()).getBottom() <= getHeight() - getListPaddingBottom())
				return false;
			if (this.mDragY > this.mLowerBound + this.mItemHalfHeight / 2)
				moveAmount = SCROLL_LENGTH;
		} else if (this.mDragY < this.mUpperBound) {
			if ((getFirstVisiblePosition() == 0) && (getChildAt(0).getTop() >= getListPaddingTop()))
				return false;
			if (this.mDragY < this.mUpperBound - this.mItemHalfHeight / 2)
				moveAmount = REVERSE_SCROLL_LENGTH1;
			else
				moveAmount = REVERSE_SCROLL_LENGTH2;
		}

		if (moveAmount != 0) {
			int m = pointToPosition(0, getHeight() / 2);
			if (m == -1)
				m = pointToPosition(0, getHeight() / 2 + getDividerHeight() + this.mItemHeight);
			if (m == -1)
				m = mDragPosition;

			View view = getChildAt(m - getFirstVisiblePosition());

			if (view != null)
				setSelectionFromTop(m, view.getTop() - moveAmount);
		}

		View draggingView = getChildAt(position);
		int index = position + getFirstVisiblePosition();
		if (this.mSelectedPosition == index) {
			draggingView.clearAnimation();
			draggingView.setVisibility(View.INVISIBLE);
		} else {
			position++;
			draggingView.setVisibility(View.VISIBLE);
		}

		return false;
	}

	/**
	 * remove recycledView
	 */
	public void onMovedToScrapHeap(View view) {
		clearReverseScrollViewAnimation(view);
	}

	private void clearReverseScrollViewAnimation(View view) {
		if (((this.mSelectedPosition < this.mDragPosition) && (this.mLowerBound < this.mDragY))
				|| ((this.mSelectedPosition > this.mDragPosition) && (this.mUpperBound > this.mDragY))) {
			view.clearAnimation();
			view.setVisibility(View.VISIBLE);
		}
	}

	public int getItemHeight() {
		return mItemHeight;
	}

	public abstract interface OnOrderChangedListener {
		public abstract void onOrderChanged(int from, int to);
	}

	public SortableListViewScrollSensitivity getmScrollSensitivity() {
		return SortableListViewScrollSensitivity.fromValue(mScrollSensitivity);
	}

	public void setmScrollSensitivity(SortableListViewScrollSensitivity mScrollSensitivity) {
		this.mScrollSensitivity = mScrollSensitivity.getValue();
	}

}