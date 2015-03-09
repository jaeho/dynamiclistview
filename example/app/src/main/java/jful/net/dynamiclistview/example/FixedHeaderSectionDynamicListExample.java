package jful.net.dynamiclistview.example;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.jful.dynamiclistview.DynamicSectionListView;


public class FixedHeaderSectionDynamicListExample extends Activity {

    final int ID = 777;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section_dynamiclist);

        TextView fixedHeader = new TextView(this);
        fixedHeader.setBackgroundColor(Color.YELLOW);
        fixedHeader.setText("Fixed Header");
        LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addContentView(fixedHeader, params);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        TextView blank = new TextView(this);
        blank.setText("\n\n\n");
        TextView listHeader = new TextView(this);
        listHeader.setId(ID);
        listHeader.setBackgroundColor(Color.YELLOW);
        listHeader.setText("Fixed Header");
        header.addView(blank);
        header.addView(listHeader);

        DynamicSectionListView listView = (DynamicSectionListView) findViewById(R.id.section_dynamiclist);
        listView.addFixedHeaderView(fixedHeader);
        listView.addHeaderView(header);
        listView.setFixedHeaderId(ID);
        listView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_row_section, listView, false));
        listView.setAdapter(SectionDynamicListExample.getSampleSectionAdapter(this));
    }
}