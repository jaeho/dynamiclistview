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

package net.dynamicandroid.listview.animation;

import android.os.AsyncTask;


/**
 * @author jaehochoe
 */
public class ScrollAnimation extends AsyncTask<ScrollAnimationItem, Void, Void> {
	ScrollAnimationItem mItem = null;
	boolean end = false;
	
	@Override
	protected Void doInBackground(final ScrollAnimationItem... params) {
		// TODO Auto-generated method stub
		mItem = params[0];

		int step = mItem.step;
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
			final double x = mItem.moveAmount * exp[i];

			if (!isCancelled() || end) {
				mItem.view.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if(mItem.isHorizontalMode) 
							mItem.view.scrollTo(mItem.view.getScrollX() + (int)Math.round(x), mItem.view.getScrollY());
						else {
							int scrollYValue = mItem.view.getScrollY() + (int)Math.round(x);
							if(scrollYValue>0 && mItem.orientation == ScrollAnimationItem.TOP)
								scrollYValue=0;
							
							mItem.view.scrollTo(mItem.view.getScrollX(), scrollYValue);
							
							if(Math.abs(scrollYValue - mItem.aimValue) <= 0) {
								mItem.view.scrollTo(mItem.view.getScrollX(), mItem.aimValue);
								end = true;
							}
								
						}
					}
				});

			} else if(end)
				break;

			if(mItem.listener!=null)
				mItem.listener.onProgress();
			
			try {
				new Thread().sleep(7);
			} catch (InterruptedException ie) {
				//				ie.printStackTrace();
			}
		}

		if(!isCancelled())
			mItem.view.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					if(mItem.isHorizontalMode)
						mItem.view.scrollTo(mItem.aimValue, 0);
					else
						mItem.view.scrollTo(0, -mItem.aimValue);

					if(mItem.listener!=null)
						mItem.listener.onAnimationEnd();

				}
			});

		return null;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

}
