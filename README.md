Introduction
===============
DynamicListView provides an easy way to add a bounce-effect and the pull-to-refresh interaction to your listview.


Supported Views
===============
+ [AbsListView](https://developer.android.com/reference/android/widget/AbsListView.html)
+ [ScrollView](https://developer.android.com/reference/android/widget/ScrollView.html)
+ And Your Custom Listview


Usage
===============

For using DynamicListView Functions, You should use [DynamicListLayout](https://github.com/jaeho/dynamiclistview/blob/master/DynamicListView/src/net/dynamicandroid/listview/DynamicListLayout.java) first.

### ListView

    <net.dynamicandroid.listview.DynamicListLayout 
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <net.dynamicandroid.listview.DynamicListView
            android:id="@+id/dynamiclist"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </net.dynamicandroid.listview.DynamicListLayout>

### ListView with Header and Footer

    <net.dynamicandroid.listview.DynamicListLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <include
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            layout="@layout/view_header" />

        <net.dynamicandroid.listview.DynamicListView
            android:id="@+id/dynamiclist"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

        <include
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            layout="@layout/view_footer" />

    </net.dynamicandroid.listview.DynamicListLayout>

### ScrollView with Header and Footer
		
	 <net.dynamicandroid.listview.DynamicListLayout xmlns:android="http://schemas.android.com/apk/res/android"
		    android:layout_width="match_parent"
		    android:layout_height="match_parent"
		    android:clipChildren="false" >
		
		    <include
		        android:layout_width="match_parent"
		        android:layout_height="100dp"
		        layout="@layout/view_header" />
		
		    <net.dynamicandroid.listview.DynamicScrollView
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
		    </net.dynamicandroid.listview.DynamicScrollView>
		
		    <include
		        android:layout_width="match_parent"
		        android:layout_height="100dp"
		        layout="@layout/view_footer" />
		
		</net.dynamicandroid.listview.DynamicListLayout>

### Making Custom ListView 

For making Custom ListView (or GridView, ScrollView), You should implements [DynamicListLayoutChild](https://github.com/jaeho/dynamiclistview/blob/master/DynamicListView/src/net/dynamicandroid/listview/interfaces/DynamicListLayoutChild.java) first.

    public void setOnOverScrollListener(OnOverScrollListener onScrollDynamicListView);
	  public void setOnTouchListener(OnTouchListener onTouchListener);
	  public boolean reachedListTop();
	  public boolean reachedListBottom();
	  
`setOnOverScrollListener"` and `setOnTouchListener"`, you do not need to be implemented. but You should implement `reachedListTop"` and `reachedListBottom"` 
use [Util](https://github.com/jaeho/dynamiclistview/blob/master/DynamicListView/src/net/dynamicandroid/listview/Util.java)'s functions like this.

