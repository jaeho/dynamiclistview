package jful.net.dynamiclistview.example;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.jful.dynamiclistview.DynamicListLayout;
import net.jful.dynamiclistview.DynamicListLayout.PullingMode;
import net.jful.dynamiclistview.DynamicListLayout.PullingStatus;
import net.jful.dynamiclistview.DynamicListLayout.ScrollDirection;
import net.jful.dynamiclistview.animation.DynamicListAnimationUtils;
import net.jful.dynamiclistview.interfaces.DynamicListLayoutChild;
import net.jful.dynamiclistview.interfaces.Listener;

public class PullToRefreshListExample extends Activity {

    DynamicListLayout dynamicListLayout;
    ProgressBar progress;
    View headerBody;
    FakeLoadingTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamiclist_pulltorefresh);

        dynamicListLayout = (DynamicListLayout) findViewById(R.id.dynamiclistlayout);
        final ImageView arrow = (ImageView) findViewById(R.id.view_pulltorefresh_header_arrow);
        final TextView msg = (TextView) findViewById(R.id.view_pulltorefresh_header_tv);
        progress = (ProgressBar) findViewById(R.id.view_pulltorefresh_header_progress);
        headerBody = findViewById(R.id.view_pulltorefresh_header_body);

        dynamicListLayout.setListener(new Listener() {
            @Override
            public void onScrollDirectionChanged(DynamicListLayout layout, DynamicListLayoutChild layoutChild, ScrollDirection status) {

            }

            @Override
            public void onRelease(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingMode pulling, PullingStatus pullingStatus) {
                // TODO Auto-generated method stub

                if (pulling == PullingMode.TOP && pullingStatus == PullingStatus.ON) {
                    if (task == null) {
                        task = new FakeLoadingTask();
                        task.execute();
                    } else
                        dynamicListLayout.close(false);
                } else
                    dynamicListLayout.close();

            }

            @Override
            public void onCloesed(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingMode pulling, boolean completelyClosed) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPullingStatusChanged(DynamicListLayout layout, DynamicListLayoutChild baseDynamicListView, PullingStatus status,
                                               PullingMode pulling) {
                // TODO Auto-generated method stub

                if (status == PullingStatus.ON) {
                    if (pulling == PullingMode.TOP) {
                        DynamicListAnimationUtils.rotationAnimation(arrow);
                        msg.setText("Release to refresh");
                    }
                } else if (status == PullingStatus.OFF) {
                    if (pulling == PullingMode.TOP) {
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
                new Thread().sleep(2000);
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
