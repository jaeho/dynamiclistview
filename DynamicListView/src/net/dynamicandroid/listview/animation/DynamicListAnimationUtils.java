package net.dynamicandroid.listview.animation;

import android.view.View;
import android.view.animation.RotateAnimation;
import android.view.animation.Animation.AnimationListener;

public class DynamicListAnimationUtils {

	public static void rotationAnimation(View view) {
		rotationAnimation(view, null);
	}
	
	public static void rotationAnimation(View view, AnimationListener listener) {
		if(view==null)
			return;
		
		final RotateAnimation rotation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotation.setDuration(300);
		rotation.setFillAfter(true);
		if(listener!=null)
			rotation.setAnimationListener(listener);
		
		view.startAnimation(rotation);
	}

	public static void reverseRotationAnimation(View view) {
		reverseRotationAnimation(view, null);
	}
	
	public static void reverseRotationAnimation(View view, AnimationListener listener) {
		if(view==null)
			return;
		
		final RotateAnimation rotation = new RotateAnimation(180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		rotation.setDuration(300);
		rotation.setFillAfter(true);
		if(listener!=null)
			rotation.setAnimationListener(listener);
		
		view.startAnimation(rotation);
	}
}
