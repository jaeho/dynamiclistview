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

package dev.qwqw.dlv.animation;

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
