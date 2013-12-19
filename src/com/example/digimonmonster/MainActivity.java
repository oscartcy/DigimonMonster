package com.example.digimonmonster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private ImageView digimon;
	private Point layoutSize;
	private Point digimonSize;
	private float animatePadding;
	final private int duration = 2000; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		digimon = (ImageView) findViewById(R.id.digimon);
		
		if(digimonSize == null)
			digimonSize = new Point();
		digimonSize.x = digimon.getWidth();
		digimonSize.y = digimon.getHeight();
		
		animatePadding = digimon.getX();
		
		layoutSize = getLayoutSize();
		
		startAnimation();
	}
	
	private Point getLayoutSize() {
		RelativeLayout field = (RelativeLayout) findViewById(R.id.Field);
		Point size = new Point();
		size.x = field.getWidth() - (int)animatePadding;
		size.y = field.getHeight();
		
		return size;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
	
	private void startAnimation() {
		digimon.setImageResource(R.drawable.digi_0001);
		
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(digimon, "x", layoutSize.x - digimonSize.x);
		anim1.setDuration(duration);
		anim1.setStartDelay(1000);
		anim1.setInterpolator(new LinearInterpolator());
		
		ObjectAnimator anim11 = ObjectAnimator.ofFloat(digimon, "y", layoutSize.y / 4.0f - digimonSize.y / 2.0f);
		anim11.setDuration(100);
		anim11.setStartDelay(1000);
		anim11.setInterpolator(new LinearInterpolator());
		
		ObjectAnimator anim12 = ObjectAnimator.ofFloat(digimon, "y", layoutSize.y / 2.0f - digimonSize.y / 2.0f);
		anim12.setDuration(100);
		anim12.setInterpolator(new LinearInterpolator());
		
		AnimatorSet animatorSet1 = new AnimatorSet();
		animatorSet1.playSequentially(anim11, anim12);
		
		AnimatorSet animatorSet2 = new AnimatorSet();
		animatorSet2.playSequentially(anim11, anim12);
		
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playSequentially(anim1, animatorSet1, animatorSet2);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				digimon.setImageBitmap(flipImage(BitmapFactory.decodeResource(getResources(), R.drawable.digi_0001)));
				ObjectAnimator anim11 = ObjectAnimator.ofFloat(digimon, "y", layoutSize.y / 4.0f - digimonSize.y / 2.0f);
				anim11.setDuration(100);
				anim11.setStartDelay(500);
				anim11.setInterpolator(new LinearInterpolator());
				
				ObjectAnimator anim12 = ObjectAnimator.ofFloat(digimon, "y", layoutSize.y / 2.0f - digimonSize.y / 2.0f);
				anim12.setDuration(100);
				anim12.setInterpolator(new LinearInterpolator());

				ObjectAnimator anim2 = ObjectAnimator.ofFloat(digimon, "x", animatePadding);
				anim2.setDuration(duration);
				anim2.setStartDelay(1000);
				anim2.setInterpolator(new LinearInterpolator());
				
				AnimatorSet animatorSet2 = new AnimatorSet();
				animatorSet2.playSequentially(anim11, anim12);
				
				AnimatorSet animatorSet3 = new AnimatorSet();
				animatorSet3.playSequentially(anim11, anim12);
				
				AnimatorSet animatorSet = new AnimatorSet();
				animatorSet.playSequentially(anim2, animatorSet2, animatorSet3);
				animatorSet.addListener(new AnimatorListenerAdapter() {
					public void onAnimationEnd(Animator animation) {
						startAnimation();
					}
				});
				animatorSet.start();
			}
		});
		animatorSet.start();
	}
	
	private Bitmap flipImage(Bitmap src) {
	     // create new matrix for transformation
	     Matrix matrix = new Matrix();
	     matrix.preScale(-1.0f, 1.0f);
	  
	     // return transformed image
	     return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
	}
}
