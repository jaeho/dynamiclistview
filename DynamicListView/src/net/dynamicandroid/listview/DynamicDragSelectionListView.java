package net.dynamicandroid.listview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import net.dynamicandroid.listview.interfaces.PinselectionAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;

/**
 * 
 * @author jaehochoe
 */
public class DynamicDragSelectionListView extends DynamicListView {

	private boolean isSelectionMode = false;

	private boolean isBottomDragSelectMode = false;
	private boolean isTopDragSelectMode = false;
	private Bitmap mPinUp, mPinDown;
	private int mDragEndPosition = -3;
	private int mDragStartPosition = -3;
	private HashMap<Integer, Integer> mInvisibleSelectedItemListHeight = new HashMap<Integer, Integer>();
	private Paint mLinePaint;
	private ArrayList<Integer> mSelectedBottomPositionList = new ArrayList<Integer>();;
	private ArrayList<Integer> mSelectedPositionList = new ArrayList<Integer>();
	private ArrayList<Integer> mSelectedTopPositionList = new ArrayList<Integer>();
	private float mTouchedCoorY = -3.0F;
	private int mTouchedPosition = -3;
	private HashMap<Integer, Integer> mVisibleSelectedItemListHeight = new HashMap<Integer, Integer>();

	private Paint mSelectedBackground = new Paint();
	private PinselectionAdapter mAdapter;

	private int mMaxSize = -1;

	@Override
	public void setAdapter(ListAdapter adapter) {
		

		if(adapter instanceof PinselectionAdapter) {
			this.mAdapter = (PinselectionAdapter) adapter;
			super.setAdapter(adapter);
		} else
			System.err.println("PinSelectionDynamicListView's Adapter must be PinselectionAdapter");
	}

	public DynamicDragSelectionListView(Context context) {
		super(context);
		initialize();
	}

	public DynamicDragSelectionListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public DynamicDragSelectionListView(Context context, AttributeSet attrs, int theme) {
		super(context, attrs, theme);
		initialize();
	}

	private boolean inRect(float x, float y, View view, boolean isTop, int position) {
		if (view == null)
			return false;

		if (isTop)
			return new Rect(-mPinUp.getWidth()*2 + view.getRight() / 2, -mPinUp.getWidth() + view.getTop(), mPinUp.getWidth()*2 + view.getRight() / 2,
					mPinUp.getWidth() + view.getTop()).contains((int) x, (int) y);
		else
			return new Rect(-mPinUp.getWidth() + view.getRight() / 2, -mPinUp.getWidth() + view.getBottom(), mPinUp.getWidth() + view.getRight() / 2,
					mPinUp.getWidth() + view.getBottom()).contains((int) x, (int) y);
	}

	private void initDrag() {
		isTopDragSelectMode = false;
		isBottomDragSelectMode = false;
		this.mTouchedPosition = -3;
		this.mTouchedCoorY = -3.0F;
		this.mDragStartPosition = -3;
		this.mDragEndPosition = -3;
	}

	private final void initialize() {
		this.mLinePaint = new Paint();
		this.mLinePaint.setARGB(96, 0, 0, 0);
		this.mLinePaint.setStrokeWidth(2.0F);
		setPinUpDrawable(R.drawable.ic_launcher);
		setPinDownDrawable(R.drawable.ic_launcher);

		this.mSelectedBackground.setColor(Color.argb(50, 00, 00, 00));
	}

	public void setPinUpDrawable(int res) {
		Drawable pin = getContext().getResources().getDrawable(res);
		this.mPinUp = Bitmap.createBitmap(pin.getIntrinsicWidth(), pin.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(this.mPinUp);
		pin.setBounds(0, 0, pin.getIntrinsicWidth(), pin.getIntrinsicHeight());
		pin.draw(canvas);		
	}

	public void setPinDownDrawable(int res) {
		Drawable pin = getContext().getResources().getDrawable(res);
		this.mPinDown = Bitmap.createBitmap(pin.getIntrinsicWidth(), pin.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(this.mPinDown);
		pin.setBounds(0, 0, pin.getIntrinsicWidth(), pin.getIntrinsicHeight());
		pin.draw(canvas);		
	}

	public boolean isDragMode() {
		return (isTopDragSelectMode) || (isBottomDragSelectMode);
	}

	private void arrangePositions() {
		Collections.sort(this.mSelectedPositionList);
		this.mSelectedTopPositionList.clear();
		this.mSelectedBottomPositionList.clear();
		int i = -3;
		Iterator<Integer> localIterator = this.mSelectedPositionList.iterator();
		while (true) {
			if (!localIterator.hasNext()) {
				this.mSelectedBottomPositionList.add(Integer.valueOf(i));
				invalidateViews();
				return;
			}
			int j = ((Integer) localIterator.next()).intValue();
			if (j > i + 1) {
				this.mSelectedTopPositionList.add(Integer.valueOf(j));
				if (i != -3)
					this.mSelectedBottomPositionList.add(Integer.valueOf(i));
			}
			i = j;
		}

	}

	public void addSelectedPositionList(int position, int height) {
		if (!this.mSelectedPositionList.contains(Integer.valueOf(position)))
			this.mSelectedPositionList.add(Integer.valueOf(position));

		if (height > 0)
			this.mVisibleSelectedItemListHeight.put(Integer.valueOf(position), Integer.valueOf(height));
		arrangePositions();
	}

	public void clearSelectedPositionList() {
		this.mSelectedPositionList.clear();
		arrangePositions();
		this.mInvisibleSelectedItemListHeight.clear();
		this.mVisibleSelectedItemListHeight.clear();
		invalidate();
	}

	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		int firstVisiblePosition = getFirstVisiblePosition();
		int lastVisiblePosition = getLastVisiblePosition();

		for(int i = 0 ; i < mSelectedTopPositionList.size() ; i++) {

			int topPosition = mSelectedTopPositionList.get(i);
			int bottomPosition = mSelectedBottomPositionList.get(i);
			float rowTop = -1.0f;
			float rowBottom = -1.0f;

			if ((isTopDragSelectMode) && (topPosition == this.mTouchedPosition)) {
				canvas.drawLine(getLeft(), this.mTouchedCoorY, getRight(), this.mTouchedCoorY, this.mLinePaint);
				canvas.drawBitmap(this.mPinUp, (getRight() - this.mPinUp.getWidth()) / 2, this.mTouchedCoorY - this.mPinUp.getHeight()
						/ 2, null);
				rowTop = this.mTouchedCoorY;
			} else if ((firstVisiblePosition <= topPosition + getHeaderViewsCount()) || (lastVisiblePosition >= topPosition)) {
				View row = getChildAt(topPosition - firstVisiblePosition + getHeaderViewsCount());
				if (row != null) {
					canvas.drawLine(getLeft(), row.getTop(), getRight(), row.getTop(), this.mLinePaint);
					canvas.drawBitmap(this.mPinUp, (getRight() - this.mPinUp.getWidth()) / 2,
							row.getTop() - this.mPinUp.getHeight() / 2, null);

					rowTop = row.getTop();
					this.mVisibleSelectedItemListHeight.put(Integer.valueOf(topPosition), Integer.valueOf(row.getMeasuredHeight()));
				} else {

					if(getLastVisiblePosition() - getHeaderViewsCount() == topPosition - 1) {
						row = getChildAt(topPosition - 1 - firstVisiblePosition + getHeaderViewsCount());
						canvas.drawBitmap(this.mPinUp, (getRight() - this.mPinUp.getWidth()) / 2, row.getBottom() - this.mPinUp.getHeight() / 2, null);
					}

					rowTop = getTop();
				}
			} else 
				rowTop = getTop();

			if ((isBottomDragSelectMode) && (bottomPosition == this.mTouchedPosition)) {
				canvas.drawLine(getLeft(), this.mTouchedCoorY, getRight(), this.mTouchedCoorY, this.mLinePaint);
				canvas.drawBitmap(this.mPinDown, (getRight() - this.mPinUp.getWidth()) / 2, this.mTouchedCoorY - this.mPinUp.getHeight()
						/ 2, null);
				rowBottom = this.mTouchedCoorY;
			} else if ((firstVisiblePosition <= bottomPosition + getHeaderViewsCount()) && (lastVisiblePosition >= bottomPosition)) {
				View row = getChildAt(bottomPosition - firstVisiblePosition + getHeaderViewsCount());
				if (row != null) {
					canvas.drawLine(getLeft(), row.getBottom(), getRight(), row.getBottom(), this.mLinePaint);
					canvas.drawBitmap(this.mPinDown, (getRight() - this.mPinUp.getWidth()) / 2, row.getBottom() - this.mPinUp.getHeight() / 2, null);

					rowBottom = row.getBottom();
					this.mVisibleSelectedItemListHeight.put(Integer.valueOf(bottomPosition), Integer.valueOf(row.getMeasuredHeight()));
				} else 
					rowBottom = getBottom();
			} else {
				if(getFirstVisiblePosition() - getHeaderViewsCount() == bottomPosition + 1) {
					View row = getChildAt(bottomPosition + 1 - firstVisiblePosition + getHeaderViewsCount());
					canvas.drawBitmap(this.mPinDown, (getRight() - this.mPinUp.getWidth()) / 2, row.getTop() - this.mPinUp.getHeight() / 2, null);
				}
				rowBottom = getBottom();
			}

			if(!isDragMode()) {
				if(bottomPosition >= getFirstVisiblePosition() - getHeaderViewsCount() && topPosition <= getLastVisiblePosition() - getHeaderViewsCount())
					canvas.drawRect(getLeft(), rowTop, getRight(), rowBottom, mSelectedBackground);
			} else if(isBottomDragSelectMode) {
				if(rowBottom < rowTop) {
					float temp = rowTop;
					rowTop = rowBottom;
					rowBottom = temp;
				}

				if(topPosition <= getLastVisiblePosition() - getHeaderViewsCount())
					canvas.drawRect(getLeft(), rowTop, getRight(), rowBottom, mSelectedBackground);
			} else if(isTopDragSelectMode) {

				if(rowTop > rowBottom) {
					float temp = rowTop;
					rowTop = rowBottom;
					rowBottom = temp;
				}

				if(bottomPosition >= getFirstVisiblePosition() - getHeaderViewsCount())
					canvas.drawRect(getLeft(), rowTop, getRight(), rowBottom, mSelectedBackground);
			}

		}

	}

	public ArrayList<Integer> getSelectedPositionList() {
		return this.mSelectedPositionList;
	}

	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if ( ((this.mSelectedTopPositionList.size() == 0) && (this.mSelectedBottomPositionList.size() == 0)) || !isSelectionMode )
			return super.onInterceptTouchEvent(ev);

		float touchedX = ev.getX();
		float touchedY = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int firstVisiblePosition = getFirstVisiblePosition();
			int lastVisiblePosition = getLastVisiblePosition();

			for (int n = 0; n < this.mSelectedBottomPositionList.size(); n++) {
				int touchedPosition = ((Integer) this.mSelectedBottomPositionList.get(n)).intValue();
				if ((firstVisiblePosition <= touchedPosition + getHeaderViewsCount()) && (lastVisiblePosition >= touchedPosition) && (inRect(touchedX, touchedY, getChildAt(touchedPosition - firstVisiblePosition + getHeaderViewsCount()), false, touchedPosition))) {
					isBottomDragSelectMode = true;
					this.mTouchedPosition = touchedPosition;
					this.mTouchedCoorY = touchedY;
					this.mDragEndPosition = touchedPosition;
					this.mDragStartPosition = ((Integer) this.mSelectedTopPositionList.get(n)).intValue();
					return true;
				}
			}

			for (int k = 0; k < this.mSelectedTopPositionList.size(); k++) {
				int touchedPosition = ((Integer) this.mSelectedTopPositionList.get(k)).intValue();
				if ((firstVisiblePosition <= touchedPosition + getHeaderViewsCount()) && (lastVisiblePosition >= touchedPosition) && (inRect(touchedX, touchedY, getChildAt(touchedPosition - firstVisiblePosition + getHeaderViewsCount()), true, touchedPosition))) {
					isTopDragSelectMode = true;
					this.mTouchedPosition = touchedPosition;
					this.mTouchedCoorY = touchedY;
					this.mDragStartPosition = touchedPosition;
					this.mDragEndPosition = ((Integer) this.mSelectedBottomPositionList.get(k)).intValue();
					return true;
				}
			}
			break;
		}

		return super.onInterceptTouchEvent(ev);
	}

	public boolean onTouchEvent(MotionEvent ev) {
		if ((this.mSelectedTopPositionList.size() == 0) && (this.mSelectedBottomPositionList.size() == 0))
			return super.onTouchEvent(ev);

		float y = ev.getY();

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if(isDragMode())
				return true;
			else
				break;
		case MotionEvent.ACTION_MOVE:
			if ((isTopDragSelectMode) || (isBottomDragSelectMode)) {
				if ((y <= 32.0F) && (getFirstVisiblePosition() != getHeaderViewsCount())) {
					this.mTouchedCoorY = 32.0F;
					if (getFirstVisiblePosition() > 1) {
						smoothScrollToPosition(-2 + getFirstVisiblePosition());
						return false;
					}
					smoothScrollToPosition(-1 + getFirstVisiblePosition());
					return false;
				}
				if ((y >= -32 + getBottom()) && (getLastVisiblePosition() != -1 + getCount())) {
					this.mTouchedCoorY = (-32 + getBottom());
					if (getLastVisiblePosition() < -1 + getCount()) {
						smoothScrollToPosition(2 + getLastVisiblePosition());
						return false;
					}
					smoothScrollToPosition(1 + getLastVisiblePosition());
					return false;
				}

				this.mTouchedCoorY = y;
				if ((getFirstVisiblePosition() == getHeaderViewsCount()) && (this.mTouchedCoorY < getChildAt(getHeaderViewsCount()).getTop())) {
					this.mTouchedCoorY = getChildAt(getHeaderViewsCount()).getTop();
				} else if(getLastVisiblePosition() == getCount() - 1 && (this.mTouchedCoorY > getChildAt(getLastVisiblePosition()-getFirstVisiblePosition()-getFooterViewsCount()).getBottom())) {  
					this.mTouchedCoorY = getChildAt(getLastVisiblePosition()-getFirstVisiblePosition()-getFooterViewsCount()).getBottom();
				} else {
					if (this.mTouchedCoorY < getPaddingTop())
						this.mTouchedCoorY = getPaddingTop();
					else if (this.mTouchedCoorY > getBottom() - getPaddingBottom())
						this.mTouchedCoorY = (getBottom() - getPaddingBottom());
				}
				invalidate();
				return false;
			}

			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			if(!isDragMode())
				break;

			int intY = (int)y;
			int dragEndPosition = 0;

			View lastVisibleView = getChildAt(getLastVisiblePosition()-getFirstVisiblePosition());
			View firstVisibleView = getChildAt(0);

			if(intY > lastVisibleView.getBottom()) {
				dragEndPosition = getLastVisiblePosition() + 1 - getHeaderViewsCount();					
			} else if(intY < firstVisibleView.getTop()) {
				
			} else {
				dragEndPosition = getItemIndexAtLocation(intY);

				View dragEndView = getChildAt(dragEndPosition - getFirstVisiblePosition());
				if(dragEndView==null) {
					if(isDragMode())
						initDrag();
					return false;
				}

				int viewTop = dragEndView.getTop();
				int viewBottom = dragEndView.getBottom();
				dragEndPosition -= getHeaderViewsCount();

				if(intY-viewTop >= (viewBottom-viewTop)/2) 
					dragEndPosition++;
			}

			Log.d("DynamicListView",dragEndPosition + " / " + mDragEndPosition + " / " + mDragStartPosition);
			
			if(dragEndPosition > mDragEndPosition) {

				if(isBottomDragSelectMode) {
					for(int i = mDragStartPosition ; i < dragEndPosition ; i++)
						selectPosition(i);
				} else if(isTopDragSelectMode) {
					for(int i = mDragEndPosition+1 ; i < dragEndPosition ; i++) 
						selectPosition(i);
					for(int i = mDragStartPosition ; i <= mDragEndPosition ; i++)
						deselectPosition(i);
				}

			} else if(dragEndPosition < mDragStartPosition) {

				if(isTopDragSelectMode) {
					for(int i = dragEndPosition ; i < mDragEndPosition ; i++)
						selectPosition(i);
				} else if(isBottomDragSelectMode) {

					for(int i = dragEndPosition ; i < mDragStartPosition ; i++) 
						selectPosition(i);
					for(int i = mDragStartPosition ; i <= mDragEndPosition ; i++)
						deselectPosition(i);
				}

			} else if(dragEndPosition > mDragStartPosition && dragEndPosition <= mDragEndPosition) {

				if(isTopDragSelectMode)
					for(int i = mDragStartPosition ; i < dragEndPosition ; i++)
						deselectPosition(i);
				else if(isBottomDragSelectMode)
					for(int i = dragEndPosition ; i <= mDragEndPosition ; i++) 
						deselectPosition(i);


			} else if(mDragStartPosition == mDragEndPosition && mDragStartPosition == dragEndPosition && !isTopDragSelectMode) {

				for(int i = mDragStartPosition ; i <= mDragEndPosition ; i++) 
					deselectPosition(i);

			} else if(mDragStartPosition == mDragEndPosition && mDragStartPosition == dragEndPosition-1 && isTopDragSelectMode) {

				for(int i = mDragStartPosition ; i <= mDragEndPosition ; i++) 
					deselectPosition(i);

			} else if(dragEndPosition == mDragStartPosition && isBottomDragSelectMode) {

				for(int i = mDragStartPosition ; i <= mDragEndPosition ; i++)
					deselectPosition(i);

			}

			initDrag();
			break;
		}

		return super.onTouchEvent(ev);
	}

	public void deselectPosition(int position) {
		this.mSelectedPositionList.remove(Integer.valueOf(position));
		this.mVisibleSelectedItemListHeight.remove(Integer.valueOf(position));
		arrangePositions();
	}

	public void setInvisibleSelectedItemsHeight(int position, int height) {
		this.mInvisibleSelectedItemListHeight.put(Integer.valueOf(position), Integer.valueOf(height));
	}

	public boolean selectPosition(int position) {
		return selectPosition(position, false);
	}

	public boolean togglePosition(int position) {
		return selectPosition(position, true);
	}

	public boolean selectPosition(int position, boolean toggle) {
		if(mSelectedPositionList==null)
			mSelectedPositionList = new ArrayList<Integer>();

		if(position >= getCount() - getFooterViewsCount() - getHeaderViewsCount()) {
			return false;
		}

		if(position >= 0)
			if(toggle && mSelectedPositionList.contains(Integer.valueOf(position))) {
				deselectPosition(position);
				return true;
			} else if(!mSelectedPositionList.contains(Integer.valueOf(position)) 
					&& mAdapter.isSelectableItem(position)
					&& (mMaxSize==-1 || mMaxSize > mSelectedPositionList.size())) {
				mSelectedPositionList.add(position);
				arrangePositions();
				return true;
			} else if(!mAdapter.isSelectableItem(position)) {
				return false;
			} else if(mMaxSize <= mSelectedPositionList.size()) {
				return false;
			} 
		return false;
	}

	public boolean isSelectionMode() {
		return isSelectionMode;
	}

	public void setSelectionMode(boolean isSelectionMode) {
		this.isSelectionMode = isSelectionMode;

		if(!isSelectionMode) {
			clearSelectedPositionList();
		}
	}

	public int getMaxSize() {
		return mMaxSize;
	}

	public void setMaxSize(int mMaxSize) {
		this.mMaxSize = mMaxSize;
	}
}
