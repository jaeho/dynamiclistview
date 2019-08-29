package jful.net.dynamiclistview.example;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;


public class MainActivity extends ListActivity {

    String[] samples = new String[] {
            "SimpleDynamicList",
            "PullToRefreshList",
            "SectionDynamicList",
            "PullToRefreshSectionList",
            "ScrollBackgroundSectionDynamicList",
            "RecyclerView"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getListView().setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, samples));
        getListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                try {
                    Intent activity = new Intent(MainActivity.this, getClass().forName(getPackageName()+"."+samples[arg2]+"Example"));
                    startActivity(activity);
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

}
