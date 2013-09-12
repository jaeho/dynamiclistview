package net.dynamicandroid.listview.animation;

import net.dynamicandroid.listview.animation.ViewSizeAnimation.ViewAnimationItem;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class ViewSizeAnimation extends AsyncTask<ViewAnimationItem, Void, ViewAnimationItem>{

	@Override
	protected ViewAnimationItem doInBackground(ViewAnimationItem... params) {
		// TODO Auto-generated method stub
		final ViewAnimationItem item = params[0];
		final int x_final = (item.beforeAimValue==-1)?item.aimValue:(item.aimValue-item.beforeAimValue);

		int step = item.duration;
		double[] exp = new double[step];
		double sum = 0;
		double interpolation_wi = 0.15;

		for (int i = 0; i < step; i++) {
			double expVal = Math.exp(-(interpolation_wi * (i + 1))); 
			exp[i] = expVal;
			sum += expVal;
		}

		for (int i = 0; i < step; i++) {
			exp[i] = exp[i] / sum;
		}

		for (int i = 0; i < step; i++) {
			final double x_pos = x_final * exp[i];
			item.targetView.post(new Runnable() {
				@Override
				public void run() {
					if(!isCancelled()) {
						int x = item.targetView.getHeight() + (int)Math.round(x_pos);
						if(x<0)
							x=0;
						if(item.parentsType == LinearLayout.class) {
							item.targetView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, x));
						} else if(item.parentsType == FrameLayout.class) {
							item.targetView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, x));
						} else if(item.parentsType == RelativeLayout.class) {
							item.targetView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, x));
						} else {
							item.targetView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, x));
						}
					}
				}
			});

			try {
				new Thread().sleep(7);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}

		if(!isCancelled())
			if(item.targetView.getHeight()!=item.aimValue) 
				item.targetView.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(item.parentsType == LinearLayout.class) {
							item.targetView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (item.aimValue)>0?item.aimValue:0));
						} else if(item.parentsType == FrameLayout.class) {
							item.targetView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, (item.aimValue)>0?item.aimValue:0));
						} else if(item.parentsType == RelativeLayout.class) {
							item.targetView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (item.aimValue)>0?item.aimValue:0));
						} else {
							item.targetView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, (item.aimValue)>0?item.aimValue:0));
						}
					}
				});


		return item;
	}

	@Override
	protected void onPostExecute(ViewAnimationItem result) {
		// TODO Auto-generated method stub
		if(result!=null) 
			result.targetView.post(result.onFinishedAction);
	}

	public static class ViewAnimationItem {
		public int duration = 20;
		public int beforeAimValue = -1;
		public int aimValue;
		public View targetView;
		public Class parentsType;
		public Runnable onFinishedAction;
	}
}
