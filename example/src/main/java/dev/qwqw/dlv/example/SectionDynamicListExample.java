package dev.qwqw.dlv.example;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import dev.qwqw.dlv.DynamicSectionListView;
import dev.qwqw.dlv.interfaces.BaseSectionAdapter;

import java.util.ArrayList;
import java.util.Collections;

public class SectionDynamicListExample extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_section_dynamiclist);
		
		DynamicSectionListView listView = (DynamicSectionListView) findViewById(R.id.section_dynamiclist);
		listView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_row_section, listView, false));
		listView.setAdapter(getSampleSectionAdapter(this));
	}
	
	public static SectionAdapter getSampleSectionAdapter(Context context) {
		ArrayList<String> sections = new ArrayList<String>();
		ArrayList<ArrayList<String>> childs = new ArrayList<ArrayList<String>>();
		ArrayList<String> contentsKor = new ArrayList<String>();
		ArrayList<String> contentsEng = new ArrayList<String>();
		ArrayList<String> contentsNum = new ArrayList<String>();
		
		sections.add("Korean");
		sections.add("English");
		sections.add("Number");
		
		Collections.addAll(contentsKor, context.getResources().getStringArray(android.R.array.phoneTypes));
		Collections.addAll(contentsEng, context.getResources().getStringArray(android.R.array.imProtocols));
		Collections.addAll(contentsNum, context.getResources().getStringArray(android.R.array.emailAddressTypes));

		Collections.addAll(contentsKor, context.getResources().getStringArray(android.R.array.phoneTypes));
		Collections.addAll(contentsEng, context.getResources().getStringArray(android.R.array.imProtocols));
		Collections.addAll(contentsNum, context.getResources().getStringArray(android.R.array.emailAddressTypes));

		childs.add(contentsKor);
		childs.add(contentsEng);
		childs.add(contentsNum);
		
		SectionAdapter adapter = new SectionAdapter(context, sections, childs);
		return adapter;
	}
	
	public static class SectionAdapter extends BaseExpandableListAdapter implements BaseSectionAdapter {

		private ArrayList<String> groupList = null;
		private ArrayList<ArrayList<String>> childList = null;
		private LayoutInflater inflater = null;
		private ViewHolder viewHolder = null;

		public SectionAdapter(Context c, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList) {
			super();
			this.inflater = LayoutInflater.from(c);
			this.groupList = groupList;
			this.childList = childList;
		}

		@Override
		public String getGroup(int groupPosition) {
			return groupList.get(groupPosition);
		}

		@Override
		public int getGroupCount() {
			return groupList.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

			View v = convertView;

			if (v == null) {
				viewHolder = new ViewHolder();
				v = inflater.inflate(R.layout.list_row_section, parent, false);
				viewHolder.tv_groupName = (TextView) v.findViewById(R.id.list_row_section_tv);
				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) v.getTag();
			}

			viewHolder.tv_groupName.setText(getGroup(groupPosition));
			return v;
		}

		@Override
		public String getChild(int groupPosition, int childPosition) {
			return childList.get(groupPosition).get(childPosition);
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childList.get(groupPosition).size();
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

			View v = convertView;

			if (v == null) {
				viewHolder = new ViewHolder();
				v = inflater.inflate(R.layout.list_row_contents, null);
				viewHolder.tv_childName = (TextView) v.findViewById(R.id.list_row_contents_tv);
				v.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) v.getTag();
			}

			viewHolder.tv_childName.setText(getChild(groupPosition, childPosition));

			return v;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		class ViewHolder {
			public TextView tv_groupName;
			public TextView tv_childName;
		}

		@Override
		public void onChangedSection(View sectionView, int groupPosition, int alpha) {
			// TODO Auto-generated method stub
			((TextView) sectionView.findViewById(R.id.list_row_section_tv)).setText(getGroup(groupPosition));
		}
	}
}