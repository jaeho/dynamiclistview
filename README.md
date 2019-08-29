DynamicListView
===============
DynamicListView provides an easy way to add a bounce-effect and the pull-to-refresh interaction to your listview.


Supported Views
===============
+ RecyclerView
+ [AbsListView](https://developer.android.com/reference/android/widget/AbsListView.html)
+ [ScrollView](https://developer.android.com/reference/android/widget/ScrollView.html)

Download
===============
You can use Gradle like this:
```
repositories {
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.jaeho:dynamiclistview:1.1.0'
}
```

Usage
===============
For using DynamicListView Functions, You should use `DynamicListLayout` first.

### RecyclerView

    <dev.qwqw.dlv.DynamicListLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:id="@+id/dynamiclistlayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">

        <dev.qwqw.dlv.DynamicRecyclerView
            android:id="@+id/dynamiclist"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </dev.qwqw.dlv.DynamicListLayout>


### ListView

    <dev.qwqw.dlv.DynamicListLayout 
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <dev.qwqw.dlv.DynamicListView
            android:id="@+id/dynamiclist"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </dev.qwqw.dlv.DynamicListLayout>

### ListView with Header and Footer

    <dev.qwqw.dlv.DynamicListLayout 
    	xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <include
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            layout="@layout/view_header" />

        <dev.qwqw.dlv.DynamicListView
            android:id="@+id/dynamiclist"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

        <include
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            layout="@layout/view_footer" />

    </dev.qwqw.dlv.DynamicListLayout>

### ScrollView with Header and Footer
		
	 <dev.qwqw.dlv.DynamicListLayout 
	 	xmlns:android="http://schemas.android.com/apk/res/android"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:clipChildren="false" >
		
	    <include
	        android:layout_width="match_parent"
	        android:layout_height="100dp"
	        layout="@layout/view_header" />
	
	    <dev.qwqw.dlv.DynamicScrollView
	        android:id="@+id/scrollview"
	        android:layout_width="match_parent"
	        android:layout_height="match_parent" >
	
	        <LinearLayout
	            android:layout_width="match_parent"
	            android:layout_height="match_parent"
	            android:orientation="vertical" >
	
	            <View
	                android:layout_width="wrap_content"
	                android:layout_height="2048dp"
	                android:text="Button" />
	        </LinearLayout>
	    </dev.qwqw.dlv.DynamicScrollView>
	
	    <include
	        android:layout_width="match_parent"
	        android:layout_height="100dp"
	        layout="@layout/view_footer" />
		
	</dev.qwqw.dlv.DynamicListLayout>

### Making Custom ListView 

For making Custom ListView (or GridView, ScrollView), You should implements [DynamicListLayoutChild](https://github.com/jaeho/dynamiclistview/blob/master/DynamicListView/src/net/dynamicandroid/listview/interfaces/DynamicListLayoutChild.java) first.

    public void setOnOverScrollListener(OnOverScrollListener onScrollDynamicListView);
	  public void setOnTouchListener(OnTouchListener onTouchListener);
	  public boolean reachedListTop();
	  public boolean reachedListBottom();
	  
`setOnOverScrollListener` and `setOnTouchListener`, you do not need to be implemented. but You should implement `reachedListTop` and `reachedListBottom` 

Use [Util](https://github.com/jaeho/dynamiclistview/blob/master/DynamicListView/src/net/dynamicandroid/listview/Util.java) like this.

   	public boolean reachedListBottom() {
		return Util.reachedListBottom(this);
	}

	public boolean reachedListTop() {
		return Util.reachedListTop(this);
	}

If so, your ListView(or GridView, ScrollView) and [DynamicListLayout](https://github.com/jaeho/dynamiclistview/blob/master/DynamicListView/src/net/dynamicandroid/listview/DynamicListLayout.java) will be works.

### Easy way to convert your "ListView" into "DynamicListView"

Your ListView,

    <ListView xmlns:android="http://schemas.android.com/apk/res/android"
    	android:layout_width="match_parent"
    	android:layout_height="wrap_content"/>

This code can be easily changed to DynamicListView like this.

    <dev.qwqw.dlv.DynamicListLayout 
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <dev.qwqw.dlv.DynamicListView
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </dev.qwqw.dlv.DynamicListLayout>
    
    
### License

```
The MIT License (MIT)

Copyright (c) 2019 Jaeho Choe

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```