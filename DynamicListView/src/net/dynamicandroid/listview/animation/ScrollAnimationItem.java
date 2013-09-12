package net.dynamicandroid.listview.animation;

import android.view.View;

public class ScrollAnimationItem {
	public static final int TOP = 1;
	public static final int BOTTOM = -1;

	public View view;
	public int moveAmount;
	public int aimValue;
	public int step = 20;
	public ScrollAnimationListener listener;
	public int orientation = 0;
	public boolean isHorizontalMode = false;

	public ScrollAnimationItem(View view, int moveAmount, int aimValue, ScrollAnimationListener onAnimationEnd, int orientation) {
		super();
		this.view = view;
		this.moveAmount = moveAmount;
		this.aimValue = aimValue;
		this.listener = onAnimationEnd;
		this.orientation = orientation;
	}

	public void setHorizontalMode(boolean isHorizontalMode) {
		this.isHorizontalMode = isHorizontalMode;
	}

	public void setStep(int step) {
		this.step = step;
	}
}