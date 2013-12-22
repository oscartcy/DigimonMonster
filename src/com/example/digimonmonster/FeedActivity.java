package com.example.digimonmonster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class FeedActivity extends Activity {
	private int selected = 1;
	private DigimonMonster app;
	protected Digimon digimonModel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feed);
		
		app = (DigimonMonster) getApplicationContext();
		digimonModel=app.getDigimon();
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.meat_layout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onTouchMeat();
			}
		});
		
		layout = (LinearLayout) findViewById(R.id.pill_layout);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onTouchPill();
			}
		});
	}
	
	private void onTouchMeat() {
		if(selected == 1){
			// 1=feed success 0=fail -1=sick	
			int feed=digimonModel.feed();
			if (feed==1)
				eatMeat();
			if (feed==-1)
				eatMeatAndSick();
			if (feed==0)
				notEat();
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
			int feed=digimonModel.eatVitamin();
			if (feed==1)
				eatPill();
			if (feed==-1)
				eatPillAndSick();
			if (feed==0)
				notEat();
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
	
	private void eatMeatAndSick() {
		setResult(MainActivity.FEED_MEATSICK, new Intent());
		finish();
	}
	private void eatPill() {
		setResult(MainActivity.FEED_PILL, new Intent());
		finish();
	}
	private void eatPillAndSick() {
		setResult(MainActivity.FEED_PILLSICK, new Intent());
		finish();
	}
	
	private void notEat(){
		setResult(MainActivity.FEED_NOTEAT, new Intent());
		finish();
	}
}
