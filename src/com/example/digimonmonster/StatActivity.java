package com.example.digimonmonster;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class StatActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stat);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		TextView textView = (TextView) findViewById(R.id.stat_name);
		textView.setText("HIHI獸");
		
		textView = (TextView) findViewById(R.id.stat_age);
		textView.setText("年齡: " + "2yr");
		
		textView = (TextView) findViewById(R.id.stat_hunger);
		textView.setText("飽滿值: " + "2" +" / 4");
		
		textView = (TextView) findViewById(R.id.stat_strength);
		textView.setText("力量值: " + "2" +" / 4");
		
		textView = (TextView) findViewById(R.id.stat_type);
		textView.setText("形態: " + "成長形");
		
		textView = (TextView) findViewById(R.id.stat_dp);
		textView.setText("DP: " + "19" +" / 20");
		
	}
}
