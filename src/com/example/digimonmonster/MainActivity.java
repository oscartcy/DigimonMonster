package com.example.digimonmonster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private ImageView digimon;
	private Point layoutSize;
	private Point digimonSize;
	private float animatePadding;
	final private float animatePaddingPd = 40;
	final private int duration = 2000;

	private AnimatorSet currentAnimation;
	private boolean endAnimation = false;
	private boolean animationStarted = false;

	public final static int FEED_REQUEST_CODE = 689;
	public final static int FEED_MEAT = 11;
	public final static int FEED_PILL = 12;

	private int animationCode = 0;
	
	private ImageView food;

	// activity callback
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
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			digimon = (ImageView) findViewById(R.id.digimon);

			if (digimonSize == null)
				digimonSize = new Point();
			digimonSize.x = digimon.getWidth();
			digimonSize.y = digimon.getHeight();

			// animatePadding = digimon.getX();
			Resources r = getResources();
			animatePadding = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, animatePaddingPd,
					r.getDisplayMetrics());

			layoutSize = getLayoutSize();

			switch (animationCode) {
			case 0:
				//endAnimation = false;
				walkAnimation();
				break;
				
			case FEED_MEAT:
				eatAnimation(FEED_MEAT);
				break;
				
			case FEED_PILL:
				eatAnimation(FEED_MEAT);
				break;

			default:
				break;
			}
		} else {
			stopAnimation();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FEED_REQUEST_CODE) {
			animationCode = resultCode;

			RelativeLayout field = (RelativeLayout) findViewById(R.id.Field);

			food = new ImageView(this);
			if (resultCode == FEED_MEAT) {
				food.setImageResource(R.drawable.meat);
			} else {
				food.setImageResource(R.drawable.pill);
			}

			field.addView(food);
		}
	}

	private Point getLayoutSize() {
		RelativeLayout field = (RelativeLayout) findViewById(R.id.Field);
		Point size = new Point();
		size.x = field.getWidth() - (int) animatePadding;
		size.y = field.getHeight();

		return size;
	}

	// animation coding
	private void walkAnimation() {
		animationStarted = true;
		
		digimon.setImageResource(R.drawable.digi_0001);
		digimon.setX(animatePadding);
		digimon.setY(layoutSize.y / 2.0f - digimonSize.y / 2.0f);

		ObjectAnimator anim1 = ObjectAnimator.ofFloat(digimon, "x",
				layoutSize.x - digimonSize.x);
		anim1.setDuration(duration);
		anim1.setStartDelay(1000);
		anim1.setInterpolator(new LinearInterpolator());

		ObjectAnimator anim11 = ObjectAnimator.ofFloat(digimon, "y",
				layoutSize.y / 4.0f - digimonSize.y / 2.0f);
		anim11.setDuration(100);
		anim11.setStartDelay(1000);
		anim11.setInterpolator(new LinearInterpolator());

		ObjectAnimator anim12 = ObjectAnimator.ofFloat(digimon, "y",
				layoutSize.y / 2.0f - digimonSize.y / 2.0f);
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
				if (!endAnimation) {
					digimon.setImageBitmap(flipImage(BitmapFactory
							.decodeResource(getResources(),
									R.drawable.digi_0001)));
					ObjectAnimator anim11 = ObjectAnimator.ofFloat(digimon,
							"y", layoutSize.y / 4.0f - digimonSize.y / 2.0f);
					anim11.setDuration(100);
					anim11.setStartDelay(500);
					anim11.setInterpolator(new LinearInterpolator());

					ObjectAnimator anim12 = ObjectAnimator.ofFloat(digimon,
							"y", layoutSize.y / 2.0f - digimonSize.y / 2.0f);
					anim12.setDuration(100);
					anim12.setInterpolator(new LinearInterpolator());

					ObjectAnimator anim2 = ObjectAnimator.ofFloat(digimon, "x",
							animatePadding);
					anim2.setDuration(duration);
					anim2.setStartDelay(1000);
					anim2.setInterpolator(new LinearInterpolator());

					AnimatorSet animatorSet2 = new AnimatorSet();
					animatorSet2.playSequentially(anim11, anim12);

					AnimatorSet animatorSet3 = new AnimatorSet();
					animatorSet3.playSequentially(anim11, anim12);

					AnimatorSet animatorSet = new AnimatorSet();
					animatorSet.playSequentially(anim2, animatorSet2,
							animatorSet3);
					animatorSet.addListener(new AnimatorListenerAdapter() {
						public void onAnimationEnd(Animator animation) {
							if (!endAnimation)
								walkAnimation();
						}
					});
					animatorSet.start();
					currentAnimation = animatorSet;
				}
			}
		});
		animatorSet.start();
		currentAnimation = animatorSet;
	}

	private Bitmap flipImage(Bitmap src) {
		// create new matrix for transformation
		Matrix matrix = new Matrix();
		matrix.preScale(-1.0f, 1.0f);

		// return transformed image
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(),
				matrix, true);
	}

	private void stopAnimation() {
		endAnimation = true;
		currentAnimation.end();
		digimon.clearAnimation();
		animationStarted = false;
		endAnimation = false;
	}

	private void eatAnimation(int type) {
		digimon.setX(layoutSize.x / 2.0f - digimonSize.x / 2.0f);
		digimon.setY(layoutSize.y * 3.0f / 5.0f - digimonSize.y / 2.0f);

		food.setX(digimon.getX() - food.getWidth());
		food.setY(digimon.getY() - food.getHeight());
		
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(food, "y",
				digimon.getY() + food.getHeight());
		anim1.setDuration(1000);
		anim1.setStartDelay(500);
		anim1.setInterpolator(new LinearInterpolator());
		
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(food, "alpha",
				0f);
		anim2.setDuration(1500);
		anim2.setStartDelay(500);
		anim2.setInterpolator(new LinearInterpolator());
		
		ObjectAnimator anim3 = ObjectAnimator.ofFloat(digimon, "alpha",
				0f);
		anim3.setDuration(500);
		anim3.setStartDelay(500);
		anim3.setInterpolator(new LinearInterpolator());
		
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playSequentially(anim1, anim2, anim3);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				digimon.setAlpha(1.0f);
				walkAnimation();
			}
		});
		animatorSet.start();
	}

	// buttons callback
	public void statButtonPressed(View view) {
		Intent intent = new Intent(this, StatActivity.class);
		startActivity(intent);
	}

	public void feedButtonPressed(View view) {
		Intent intent = new Intent(this, FeedActivity.class);
		startActivityForResult(intent, FEED_REQUEST_CODE);
	}
	
	public void trainButtonPressed(View view) {
		Intent intent = new Intent(this, TrainActivity.class);
		startActivity(intent);
	}
}
