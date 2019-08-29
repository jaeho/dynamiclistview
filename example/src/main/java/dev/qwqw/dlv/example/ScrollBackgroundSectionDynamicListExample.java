package dev.qwqw.dlv.example;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import dev.qwqw.dlv.DynamicListLayout;

public class ScrollBackgroundSectionDynamicListExample extends FixedHeaderSectionDynamicListExample {
	
	final int GAP = 200;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		View backgroundBody = findViewById(R.id.background_body);
		ImageView backgroundIv = (ImageView) findViewById(R.id.background_iv);
		((DynamicListLayout) findViewById(R.id.dynamiclistlayout)).setTopBackgroundView(backgroundBody, GAP);
		
		backgroundIv.setLayoutParams(new FrameLayout.LayoutParams(600, 800));
		backgroundIv.setImageResource(R.drawable.ic_launcher);
	}
}