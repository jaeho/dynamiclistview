package net.dynamicandroid.listview.interfaces;

import net.dynamicandroid.listview.DynamicListView.OnOverScrollListener;
import android.view.View.OnTouchListener;


public interface DynamicListLayoutChild {

	public void setOnOverScrollListener(OnOverScrollListener onScrollDynamicListView);
	public void setOnTouchListener(OnTouchListener onTouchListener);
	public boolean reachedListTop();
	public boolean reachedListBottom();
	
}
