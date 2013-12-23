package com.example.digimonmonster;

import java.util.LinkedList;
import java.util.Random;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class TrainActivity extends Activity {
	private RelativeLayout field;
	private Vibrator vibrator;
	
	private boolean firstTime = true;
	private boolean train=false;
	
	//sensor
	private SensorManager mSensorManager;
	private float mAccel; // acceleration apart from gravity
	private float mAccelCurrent; // current acceleration including gravity
	private float mAccelLast; // last acceleration including gravity
	
	private boolean enableShake = false;
	private boolean isShakeBreak = false;
	private int shakeCounter = 0;
	
	//shake setting
	private final float threshold = 2.0f;
	private final int shakeTime = 5000;
	private final int shakeInterval = 80;
	
	//sound
	private SoundPool soundPool;
	private boolean soundLoaded = false;
	private int readySound;
	private int beepSound;
	private int superHitSound;
	private int megaHitSound;
	private int shakeSound;
	LinkedList<Boolean> powers;
	
	private DigimonMonster app;
	protected Digimon digimonModel;

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		public void onSensorChanged(SensorEvent se) {
			float x = se.values[0];
			float y = se.values[1];
			float z = se.values[2];
			mAccelLast = mAccelCurrent;
			mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
			float delta = mAccelCurrent - mAccelLast;
			mAccel = mAccel * 0.9f + delta; // perform low-cut filter
			
			if(enableShake && !isShakeBreak && (mAccel > threshold)){
				shakeCounter++;
				
				isShakeBreak = true;
				
				Handler handler = new Handler(); 
			    handler.postDelayed(new Runnable() { 
			         public void run() { 
			        	 isShakeBreak = false; 
			         } 
			    }, shakeInterval);
			    
			    soundPool.play(shakeSound, 1.0f, 1.0f, 0, 0, 1.0f);
				vibrator.vibrate(50);
			}
			
			if(enableShake && !isShakeBreak)
				Log.d("TrainActivity", "mAccel: " + Float.toString(mAccel));
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);
		
		//initial field
		field = (RelativeLayout) findViewById(R.id.train_field);
		
		app = (DigimonMonster) getApplicationContext();
		digimonModel=app.getDigimon();
		
		//initial sensor
	    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
	    mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	    mAccel = 0.00f;
	    mAccelCurrent = SensorManager.GRAVITY_EARTH;
	    mAccelLast = SensorManager.GRAVITY_EARTH;
	    
	    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	    
	    //initial sound
	    soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 100);
	    soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				if(!soundLoaded && (sampleId == readySound)) {
					soundPool.play(readySound, 1.0f, 1.0f, 0, 0, 1.0f);
					soundLoaded = true;
				}
			}
		});
	    readySound = soundPool.load(this, R.raw.e04, 1);
	    beepSound = soundPool.load(this, R.raw.e02, 1);
	    superHitSound = soundPool.load(this,  R.raw.e05, 1);
	    megaHitSound = soundPool.load(this,  R.raw.e06, 1);
	    shakeSound = soundPool.load(this,  R.raw.e19, 1);
	    
	    //initial image
	    DigimonMonster app = (DigimonMonster) getApplicationContext();
		Digimon digimonModel=app.getDigimon();
		int photo = digimonModel.getPhoto();
		
	    ImageView image = (ImageView) findViewById(R.id.digimon);
	    image.setImageResource(photo);
	    image = (ImageView) findViewById(R.id.digimon2);
	    image.setImageResource(photo);
	    image = (ImageView) findViewById(R.id.digimon3);
	    image.setImageResource(photo);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(mSensorListener,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		mSensorManager.unregisterListener(mSensorListener);
		super.onPause();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && firstTime){
			firstTime = false;

			Handler handler = new Handler(); 
		    handler.postDelayed(new Runnable() { 
		         public void run() { 
		              switchToCount(); 
		         } 
		    }, 3000); 
		}
	}
	
	@Override
	public void onDestroy() {
		soundPool.release();
		
		super.onDestroy();
	}
	
	private void switchToCount() {
		ImageView image = (ImageView) findViewById(R.id.train_ready);
		image.setVisibility(View.GONE);
		
		image = (ImageView) findViewById(R.id.train_count);
		image.setVisibility(View.VISIBLE);
		
		enableShake = true;
		Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	              countShake(); 
	         } 
	    }, shakeTime);
	}
	
	private void countShake() {
		enableShake = false;
		Log.d("TrainActivity", "shakecount: " + String.valueOf(shakeCounter));
		
		soundPool.play(beepSound, 1.0f, 1.0f, 0, 0, 1.0f);
		
		ImageView image = (ImageView) findViewById(R.id.train_count);
		image.setVisibility(View.GONE);
		
		//gen power
		double probability=digimonModel.train(shakeCounter);

		if (probability<0.2)
			train=true;
		
		powers = new LinkedList<Boolean>();
		Random rand = new Random();
		
		for (int i=0;i<5;i++)
		{
			if (rand.nextDouble()>probability)
				powers.add(Boolean.valueOf(true));
			else
				powers.add(Boolean.valueOf(false));
		}
		
		ImageView digimon = (ImageView) findViewById(R.id.digimon);
		
		float fieldWidth = field.getWidth();
		float fieldHeight = field.getHeight();
		
		digimon.setX(fieldWidth * 3 / 4 - digimon.getWidth() / 2.0f);
		digimon.setY(fieldHeight / 2 - digimon.getHeight() / 2.0f);

		digimon.setVisibility(View.VISIBLE);
		
		SystemClock.sleep(1000);
		
		fire();
	}
	
	private void fire() {
		if(powers.isEmpty()){
			showResult();
			return;
		}
		
		boolean power = powers.pollFirst().booleanValue();
		
		ImageView digimon = (ImageView) findViewById(R.id.digimon);
		
		float fieldWidth = field.getWidth();
		float fieldHeight = field.getHeight();
		
		digimon.setX(fieldWidth * 3 / 4 - digimon.getWidth() / 2.0f);
		digimon.setY(fieldHeight / 2 - digimon.getHeight() / 2.0f);

		digimon.setVisibility(View.VISIBLE);

		ImageView fireball1 = (ImageView) findViewById(R.id.train_ball1);
		ImageView fireball2 = (ImageView) findViewById(R.id.train_ball2);
		int fireball_space = getResources()
				.getInteger(R.integer.fireball_space);

		fireball1.setX(digimon.getX() - fireball1.getWidth());
		fireball1.setY(fieldHeight / 2 - fireball1.getHeight() - fireball_space
				/ 2.0f);

		fireball2.setX(digimon.getX() - fireball2.getWidth());
		fireball2.setY(fieldHeight / 2 + fireball_space / 2.0f);

		fireball1.setVisibility(View.VISIBLE);
		if(power){
			fireball2.setVisibility(View.VISIBLE);
		} else {
			fireball2.setVisibility(View.INVISIBLE);
		}

		ObjectAnimator anim1 = ObjectAnimator.ofFloat(fireball1, "x",
				-fireball1.getWidth());
		anim1.setDuration(1000);
		anim1.setStartDelay(500);
		anim1.setInterpolator(new LinearInterpolator());

		ObjectAnimator anim2 = ObjectAnimator.ofFloat(fireball2, "x",
				-fireball1.getWidth());
		anim2.setDuration(1000);
		anim2.setInterpolator(new LinearInterpolator());

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(anim1, anim2);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				fire();
			}
		});
		animatorSet.start();

		if(power)
			soundPool.play(megaHitSound, 1.0f, 1.0f, 0, 0, 1.0f);
		else
			soundPool.play(superHitSound, 1.0f, 1.0f, 0, 0, 1.0f);
	}
	
	private void showResult() {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.train_layout);
		layout.setVisibility(View.INVISIBLE);
		
		layout = (RelativeLayout) findViewById(R.id.train_megahit);
		layout.setVisibility(View.VISIBLE);
		layout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				endTraining();
				return true;
			}
		});
	}
	
	private void endTraining() {
		if (train)
			setResult(MainActivity.TRAIN_HAPPY, new Intent());
		else
			setResult(MainActivity.TRAIN_UNHAPPY, new Intent());
		train=false;
		finish();
	}
}
