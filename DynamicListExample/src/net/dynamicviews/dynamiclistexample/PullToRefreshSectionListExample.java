package net.dynamicviews.dynamiclistexample;

import net.dynamicandroid.listview.DynamicListLayout;
import net.dynamicandroid.listview.DynamicListLayout.PullingMode;
import net.dynamicandroid.listview.DynamicListLayout.PullingStatus;
import net.dynamicandroid.listview.DynamicListLayout.ScrollDirection;
import net.dynamicandroid.listview.DynamicSectionListView;
import net.dynamicandroid.listview.animation.DynamicListAnimationUtils;
import net.dynamicandroid.listview.interfaces.DynamicListLayoutChild;
import net.dynamicandroid.listview.interfaces.Listener;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PullToRefreshSectionListExample extends Activity {

	DynamicListLayout dynamicListLayout;
	ProgressBar progress;
	View headerBody;
	FakeLoadingTask task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pulltorefresh_section_dynamiclist);

		DynamicSectionListView listView = (DynamicSectionListView) findViewById(R.id.section_dynamiclist);
		listView.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.list_row_section, listView, false));
		listView.setAdapter(SectionDynamicListExample.getSampleSectionAdapter(this));
		
		dynamicListLayout = (DynamicListLayout) findViewById(R.id.dynamiclistlayout);
		final ImageView arrow = (ImageView) findViewById(R.id.view_pulltorefresh_header_arrow);	
		final TextView msg = (TextView) findViewById(R.id.view_pulltorefresh_header_tv);
		progress = (ProgressBar) findViewById(R.id.view_pulltorefresh_header_progress);
		headerBody = findViewById(R.id.view_pulltorefresh_header_body);

		dynamicListLayout.setListener(new Listener() {

			@Override
			public void onRelease(DynamicListLayout layout, DynamicListLayoutChild DynamicListLayoutChild,PullingMode pulling, PullingStatus pullingStatus) {
				// TODO Auto-generated method stub

				if(pulling == PullingMode.TOP && pullingStatus == PullingStatus.ON) {
					if(task==null) {
						task = new FakeLoadingTask();
						task.execute();
					} else
						dynamicListLayout.close(false);
				} else
					dynamicListLayout.close();

			}

			@Override
			public void onCloesed(DynamicListLayout layout, DynamicListLayoutChild DynamicListLayoutChild,PullingMode pulling, boolean completelyClosed) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollDirectionChanged(DynamicListLayout layout, DynamicListLayoutChild DynamicListLayoutChild,ScrollDirection scrollDirection) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPullingStatusChanged(DynamicListLayout layout, DynamicListLayoutChild DynamicListLayoutChild, PullingStatus status,
					PullingMode pulling) {
				// TODO Auto-generated method stub
				if(status==PullingStatus.ON) {
					if(pulling == PullingMode.TOP) {
						DynamicListAnimationUtils.rotationAnimation(arrow);
						msg.setText("Release to refresh");
					}
				} else if(status== PullingStatus.OFF) {
					if(pulling == PullingMode.TOP) {
						DynamicListAnimationUtils.reverseRotationAnimation(arrow);
						msg.setText("Pull to refresh");
					}
				}
			}

		});
	}

	class FakeLoadingTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			try {
				new Thread().sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			dynamicListLayout.close();
			headerBody.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			task = null;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			dynamicListLayout.close(false);
			headerBody.setVisibility(View.GONE);
			progress.setVisibility(View.VISIBLE);
		}
	}

}
