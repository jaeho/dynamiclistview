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

package dev.qwqw.dlv;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

public class DynamicListScrollDependencyView extends FrameLayout {

    private List<DynamicListScrollDependencyViewItem> mDependencyViews = null;

    private onScrollScrollDependencyView mOnScroll = null;

    public onScrollScrollDependencyView getOnScroll() {
        return mOnScroll;
    }

    public void setOnScroll(onScrollScrollDependencyView onScroll) {
        this.mOnScroll = onScroll;
    }

    public DynamicListScrollDependencyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DynamicListScrollDependencyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicListScrollDependencyView(Context context) {
        super(context);
    }

    public void setDependencyView(View dependencyView, int gapX, int gapY, int gravity) {
        mDependencyViews.add(new DynamicListScrollDependencyViewItem(dependencyView, gapX, gapY, gravity));
    }

    public void setDependencyViews(List<DynamicListScrollDependencyViewItem> dependencyViews) {
        this.mDependencyViews = dependencyViews;
    }

    public List<DynamicListScrollDependencyViewItem> getDependencyViews() {
        return mDependencyViews;
    }

    @Override
    public void scrollTo(int x, int y) {

        if (mDependencyViews != null)
            for (DynamicListScrollDependencyViewItem item : mDependencyViews) {
                if (item.gravity != 1 && (y / item.gravity) + item.gapY < 0)
                    item.view.scrollTo(x + item.gapX, 0);
                else
                    item.view.scrollTo(x + item.gapX, (y / item.gravity) + item.gapY);
            }

        if (mOnScroll != null)
            mOnScroll.onScroll();

        super.scrollTo(x, y);
    }

    /**
     * @author jaehochoe
     */
    public static class DynamicListScrollDependencyViewItem {
        public View view;
        public int gapX;
        public int gapY;
        public int gravity = 1;

        public DynamicListScrollDependencyViewItem(View view, int gapX, int gapY) {
            super();
            this.view = view;
            this.gapX = gapX;
            this.gapY = gapY;
        }

        public DynamicListScrollDependencyViewItem(View view, int gapX, int gapY, int gravity) {
            super();
            this.view = view;
            this.gapX = gapX;
            this.gapY = gapY;
            this.gravity = gravity;
        }
    }

    public static interface onScrollScrollDependencyView {
        void onScroll();
    }
}
