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

    <net.jful.dynamiclistview.DynamicListLayout 
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <net.jful.dynamiclistview.DynamicListView
            android:id="@+id/dynamiclist"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </net.jful.dynamiclistview.DynamicListLayout>

### ListView with Header and Footer

    <net.jful.dynamiclistview.DynamicListLayout 
    	xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <include
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            layout="@layout/view_header" />

        <net.jful.dynamiclistview.DynamicListView
            android:id="@+id/dynamiclist"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

        <include
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            layout="@layout/view_footer" />

    </net.jful.dynamiclistview.DynamicListLayout>

### ScrollView with Header and Footer
		
	 <net.jful.dynamiclistview.DynamicListLayout 
	 	xmlns:android="http://schemas.android.com/apk/res/android"
		android:layout_width="match_parent"
		android:layout_height="match_parent"
		android:clipChildren="false" >
		
	    <include
	        android:layout_width="match_parent"
	        android:layout_height="100dp"
	        layout="@layout/view_header" />
	
	    <net.jful.dynamiclistview.DynamicScrollView
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
	    </net.jful.dynamiclistview.DynamicScrollView>
	
	    <include
	        android:layout_width="match_parent"
	        android:layout_height="100dp"
	        layout="@layout/view_footer" />
		
	</net.jful.dynamiclistview.DynamicListLayout>

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
    	android:id="@+id/dynamiclist"
    	android:layout_width="match_parent"
    	android:layout_height="wrap_content"/>

This code can be easily changed to DynamicListView like this.

    <net.jful.dynamiclistview.DynamicListLayout 
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <net.jful.dynamiclistview.DynamicListView
            android:id="@+id/dynamiclist"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />

    </net.jful.dynamiclistview.DynamicListLayout>
    
    
