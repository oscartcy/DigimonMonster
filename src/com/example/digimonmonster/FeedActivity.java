package com.example.digimonmonster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FeedActivity extends Activity {
	private int selected = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.meat_layout);
		layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onTouchMeat();
				return true;
			}
		});
		
		layout = (LinearLayout) findViewById(R.id.pill_layout);
		layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				onTouchPill();
				return true;
			}
		});
	}
	
	private void onTouchMeat() {
		if(selected == 1){
			eatMeat();
		}else{
			selected = 1;
			
			ImageView arrow = (ImageView) findViewById(R.id.feed_arrow1);
			arrow.setVisibility(View.VISIBLE);
			
			arrow = (ImageView) findViewById(R.id.feed_arrow2);
			arrow.setVisibility(View.INVISIBLE);
		}
	}
	
	private void onTouchPill() {
		if(selected == 2){
			eatPill();
		}else{
			selected = 2;
			
			ImageView arrow = (ImageView) findViewById(R.id.feed_arrow1);
			arrow.setVisibility(View.INVISIBLE);
			
			arrow = (ImageView) findViewById(R.id.feed_arrow2);
			arrow.setVisibility(View.VISIBLE);
		}
	}
	
	private void eatMeat() {
		setResult(MainActivity.FEED_MEAT, new Intent());
		finish();
	}
	
	private void eatPill() {
		setResult(MainActivity.FEED_PILL, new Intent());
		finish();
	}
}
