package net.dynamicandroid.listview;

import android.view.View;
import android.view.ViewParent;
import android.widget.AbsListView;

public class Util {
	
	public static boolean reachedListBottom(AbsListView listView) {
		boolean flag = true;
		if (listView.getChildCount() != 0) {
			int i = listView.getLastVisiblePosition();
			int j = listView.getCount();
			int k = listView.getHeight();
			int l = listView.getChildAt(-1 + listView.getChildCount()).getBottom();
			if (i != j - 1 || l > k) {
				flag = false;
			}
		}
		return flag;
	}
	
	public static boolean reachedListTop(AbsListView listView) {
		boolean flag = true;
		if (listView.getChildCount() != 0) {
			int i = listView.getFirstVisiblePosition();
			int j = listView.getChildAt(0).getTop();
			if (i != 0 || j != listView.getPaddingTop()) {
				flag = false;
			}
		}
		return flag;
	}
	
	public static boolean reachedListEnds(AbsListView listView) {
		boolean flag;
		if (reachedListTop(listView) || reachedListBottom(listView)) {
			flag = true;
		} else {
			flag = false;
		}
		return flag;
	}
	
	public static int getItemIndexAtLocation(AbsListView listView, int y) {
		int index = 0;

		if (listView.getCount() <= 0) 
			return index;

		int k = listView.getFirstVisiblePosition();

		for(int i = k ; i <= listView.getLastVisiblePosition() ; i++) {
			View view = listView.getChildAt(i - k);
			if (y > view.getTop() && y < view.getBottom() ) {
				return index = i;
			}
		}

		return 0;
	}
	
	public static boolean isScrollable(AbsListView listView, int bottomPadding) {
		View child = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition()- bottomPadding);
		if(child!=null && child.getBottom() < listView.getBottom())
			return false;
		
		return true;
	}
	
	public static DynamicListLayout findParents(AbsListView listView) {
		if(listView.getParent()==null)
			return null;
		
		ViewParent parent = listView.getParent().getParent();
		
		if(parent==null)
			return null;
		
		if(parent instanceof DynamicListLayout) 
			return (DynamicListLayout) parent;
		else
			return null;
	}
}
