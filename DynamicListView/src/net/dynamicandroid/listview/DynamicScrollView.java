package net.dynamicandroid.listview;

import net.dynamicandroid.listview.DynamicListView.OnOverScrollListener;
import net.dynamicandroid.listview.interfaces.DynamicListLayoutChild;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ScrollView;

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
		
		return getScrollY()==0;
	}

	@Override
	public boolean reachedListBottom() {
		
		return getChildCount()==0?true:getScrollY()==(getChildAt(0).getHeight() - getHeight());
	}

	@SuppressLint("NewApi")
	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
		if(!mEnableBounce)
			return;
		if(mOnScrollDynamicListView!=null && !mIsTouchedScroll && clampedY)
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
