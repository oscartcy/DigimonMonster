package com.example.digimonmonster;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TrainActivity extends Activity {
	private RelativeLayout field;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);
		
		field = (RelativeLayout) findViewById(R.id.train_field);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	              switchToCount(); 
	         } 
	    }, 2000); 
	}
	
	private void switchToCount() {
		ImageView image = (ImageView) findViewById(R.id.train_ready);
		image.setVisibility(View.GONE);
		
		image = (ImageView) findViewById(R.id.train_count);
		image.setVisibility(View.VISIBLE);
	}
}
