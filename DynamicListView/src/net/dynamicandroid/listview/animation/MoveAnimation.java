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

import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class MoveAnimation extends Animation {
	private static final int DURATION = 250;
	private float mFromXDelta;
	private float mFromYDelta;
	private float mToXDelta;
	private float mToYDelta;

	public MoveAnimation(float fromX, float toX, float fromY, float yoY) {
		this.mFromXDelta = fromX;
		this.mToXDelta = toX;
		this.mFromYDelta = fromY;
		this.mToYDelta = yoY;
		setDuration(DURATION);
		setFillAfter(true);
	}

	private void swapDelta() {
		float x = this.mFromXDelta;
		this.mFromXDelta = this.mToXDelta;
		this.mToXDelta = x;
		float y = this.mFromYDelta;
		this.mFromYDelta = this.mToYDelta;
		this.mToYDelta = y;
	}

	protected void applyTransformation(float value, Transformation transformation) {
		float x = this.mFromXDelta;
		float y = this.mFromYDelta;
		if (this.mFromXDelta != this.mToXDelta)
			x = this.mFromXDelta + value * (this.mToXDelta - this.mFromXDelta);
		if (this.mFromYDelta != this.mToYDelta)
			y = this.mFromYDelta + value * (this.mToYDelta - this.mFromYDelta);
		transformation.getMatrix().setTranslate(x, y);
	}

	public float getXDelta() {
		return this.mToXDelta;
	}

	public float getYDelta() {
		return this.mToYDelta;
	}

	public void reverse() {
		long currentTime = SystemClock.uptimeMillis();
		long playingTime = getDuration() - (currentTime - getStartTime());
		getTransformation(currentTime, new Transformation());
		swapDelta();
		if (!hasEnded())
			setStartTime(currentTime - playingTime);
		else
			setStartTime(START_ON_FIRST_FRAME);
	}
}