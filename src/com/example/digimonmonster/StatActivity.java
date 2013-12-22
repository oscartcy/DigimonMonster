package com.example.digimonmonster;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

public class StatActivity extends Activity {
	
	private DigimonMonster app;
	protected Digimon digimonModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stat);
		
		app = (DigimonMonster) getApplicationContext();
		digimonModel=app.getDigimon();
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		TextView textView = (TextView) findViewById(R.id.stat_name);
		textView.setText(digimonModel.getName());
		
		textView = (TextView) findViewById(R.id.stat_age);
		textView.setText("Age: " + Integer.toString(digimonModel.getAge()));
		
		
		textView = (TextView) findViewById(R.id.stat_weight);
		textView.setText("Weight: " + Double.toString(digimonModel.getWeight()));
		
		textView = (TextView) findViewById(R.id.stat_hunger);
		textView.setText("Hunger: " +  Integer.toString(digimonModel.getHunger()) +" / 4");
		
		textView = (TextView) findViewById(R.id.stat_strength);
		textView.setText("Strength" + Integer.toString(digimonModel.getStrength()) +" / 4");
		
		textView = (TextView) findViewById(R.id.stat_type);
		textView.setText("Level: "+ digimonModel.getLevel());
		
		textView = (TextView) findViewById(R.id.stat_dp);
		textView.setText("DP: " +Double.toString(digimonModel.getDP()) +" / 20");
		
	}
}
