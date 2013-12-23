package com.example.digimonmonster;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
	private DigimonMonster app;

	private ImageView digimon;
	private ImageView newDigimon;
	private Point layoutSize;
	private Point digimonSize;
	private float animatePadding;
	final private float animatePaddingPd = 40;
	final private int duration = 2000;
	private float shitPadding = 0;

	private ImageView sleep;
	private boolean sleepState = false; // false = sleep1

	private boolean canPressButton = true;

	private Animator currentAnimation;
	private boolean endAnimation = false;
	private boolean animationStarted = false;

	public final static int FEED_REQUEST_CODE = 10;
	public final static int FEED_MEAT = 11;
	public final static int FEED_PILL = 12;
	public final static int FEED_MEATSICK = 13;
	public final static int FEED_PILLSICK = 14;
	public final static int FEED_NOTEAT = 15;

	public final static int TRAIN_REQUEST_CODE = 20;
	public final static int TRAIN_HAPPY = 21;
	public final static int TRAIN_UNHAPPY = 22;
	
	public final static int BATTLE_REQUEST_CODE = 30;

	private int animationCode = 0;

	private ImageView food;
	private ImageView emotion;

	private final int emotionFlashDuration = 500;

	private ImageView water;
	private boolean cleanHappy;

	// sound
	private SoundPool soundPool;
	private int successSound;
	private int failSound;
	private int evoSound;

	protected LocalBroadcastManager lbm;
	protected BroadcastReceiver shitReceiver;
	protected BroadcastReceiver evolutionReceiver;
	protected BroadcastReceiver misscallReceiver;
	protected BroadcastReceiver sleepReceiver;
	protected Digimon digimonModel;

	// activity callback
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		RelativeLayout field = (RelativeLayout) findViewById(R.id.Field);
		emotion = new ImageView(this);
		emotion.setImageResource(R.drawable.happy);
		emotion.setVisibility(View.INVISIBLE);
		field.addView(emotion);

		sleep = new ImageView(this);
		sleep.setImageResource(R.drawable.sleep1);
		sleep.setVisibility(View.INVISIBLE);
		field.addView(sleep);

		// initial sound
		soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
		successSound = soundPool.load(this, R.raw.e07, 1);
		failSound = soundPool.load(this, R.raw.e08, 1);
		evoSound = soundPool.load(this, R.raw.e11, 1);

		// start service
		app = (DigimonMonster) getApplicationContext();
		lbm = LocalBroadcastManager.getInstance(app);

		app.setDigimon(new Digimon(getApplicationContext()));
		digimonModel = app.getDigimon();

		Intent intent = new Intent(this, DigimonService.class);
		startService(intent);

		// register menu
		ImageButton button = (ImageButton) findViewById(R.id.setting_button);
		registerForContextMenu(button);
		button.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.showContextMenu();
			}
		});
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();

		// register intent
		registerShit();
		registerEvolution();
		registerMissCall();
		registerSleep();
	}

	@Override
	public void onPause() {
		lbm.unregisterReceiver(evolutionReceiver);
		lbm.unregisterReceiver(sleepReceiver);
		lbm.unregisterReceiver(misscallReceiver);
		lbm.unregisterReceiver(shitReceiver);
		stopAnimation();

		super.onPause();
	}

	@Override
	public void onDestroy() {
		Intent intent = new Intent(this, DigimonService.class);
		stopService(intent);

		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus) {
			digimon = (ImageView) findViewById(R.id.digimon);
			digimon.setImageResource(digimonModel.getPhoto());

			if (digimonSize == null)
				digimonSize = new Point();
			digimonSize.x = digimon.getWidth();
			digimonSize.y = digimon.getHeight();

			Resources r = getResources();
			animatePadding = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, animatePaddingPd,
					r.getDisplayMetrics());

			layoutSize = getLayoutSize();

			switch (animationCode) {
			case 0:
				walkAnimation();
				break;

			case FEED_MEAT:
				eatAnimation(FEED_MEAT);
				break;

			case FEED_PILL:
				eatAnimation(FEED_MEAT);
				break;

			case TRAIN_HAPPY:
				emotionAnimation(TRAIN_HAPPY);
				break;

			case TRAIN_UNHAPPY:
				emotionAnimation(TRAIN_UNHAPPY);
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
		animationCode = resultCode;
		RelativeLayout field = (RelativeLayout) findViewById(R.id.Field);

		switch (requestCode) {
		case FEED_REQUEST_CODE:
			food = new ImageView(this);
			if (resultCode == FEED_MEAT) {
				food.setImageResource(R.drawable.meat);
			} else {
				food.setImageResource(R.drawable.pill);
			}

			field.addView(food);
			koMissCall();
			break;

		case TRAIN_REQUEST_CODE:
			if (resultCode == TRAIN_HAPPY) {
				emotion.setImageResource(R.drawable.happy);
			} else {
				emotion.setImageResource(R.drawable.angry1);
			}

			// field.addView(emotion);
			koMissCall();
		default:
			break;
		}
	}

	private Point getLayoutSize() {
		RelativeLayout field = (RelativeLayout) findViewById(R.id.Field);
		Point size = new Point();
		size.x = field.getWidth() - (int) animatePadding;
		size.y = field.getHeight();

		return size;
	}

	// Context Menu
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.addShit:
			digimonModel.addShit();
			return true;

		case R.id.goSleep:
			digimonModel.setSleep(!digimonModel.getSleep());
			return true;
			
		case R.id.misscall:
//			Digidatabase database = new Digidatabase(app);
//			database.readDatabse("Digidatabase.txt");
//			database.toBABYI(app.getDigimon());
//			
//			stopAnimation();
//			evolutionAnimation();
			digimonModel.addMissCall();
			digimonModel.addMissCall();
			digimonModel.addMissCall();
			digimonModel.addMissCall();
			return true;

		default:
			return super.onContextItemSelected(item);
		}
	}

	// animation coding
	private void walkAnimation() {
		animationStarted = true;
		animationCode = 0;

		if (digimonModel.getSleep()) {
			sleepAnimation();
			return;
		}

		drawShit();

		//digimon.setImageResource(digimonModel.getPhoto());
		digimon.setImageBitmap(flipImage(BitmapFactory
				.decodeResource(getResources(),
						digimonModel.getPhoto())));
		digimon.setX(animatePadding);
		digimon.setY(layoutSize.y / 2.0f - digimonSize.y / 2.0f);

		ObjectAnimator anim1 = ObjectAnimator.ofFloat(digimon, "x",
				layoutSize.x - digimonSize.x - shitPadding);
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
//					digimon.setImageBitmap(flipImage(BitmapFactory
//							.decodeResource(getResources(),
//									digimonModel.getPhoto())));
					digimon.setImageResource(digimonModel.getPhoto());
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
		canPressButton = false;

		digimon.setX(layoutSize.x / 2.0f - digimonSize.x / 2.0f);
		digimon.setY(layoutSize.y * 3.0f / 5.0f - digimonSize.y / 2.0f);

		food.setX(digimon.getX() - food.getWidth());
		food.setY(digimon.getY() - food.getHeight());

		ObjectAnimator anim1 = ObjectAnimator.ofFloat(food, "y", digimon.getY()
				+ food.getHeight());
		anim1.setDuration(1000);
		anim1.setStartDelay(500);
		anim1.setInterpolator(new LinearInterpolator());

		ObjectAnimator anim2 = ObjectAnimator.ofFloat(food, "alpha", 0f);
		anim2.setDuration(1500);
		anim2.setStartDelay(500);
		anim2.setInterpolator(new LinearInterpolator());

		ObjectAnimator anim3 = ObjectAnimator.ofFloat(digimon, "alpha", 0f);
		anim3.setDuration(500);
		anim3.setStartDelay(500);
		anim3.setInterpolator(new LinearInterpolator());

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playSequentially(anim1, anim2, anim3);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				digimon.setAlpha(1.0f);
				canPressButton = true;

				walkAnimation();
			}
		});
		animatorSet.start();
	}

	private void emotionAnimation(int emotionType) {
		canPressButton = false;

		digimon.setX(layoutSize.x / 2.0f - digimonSize.x / 2.0f);
		digimon.setY(layoutSize.y * 3.0f / 5.0f - digimonSize.y / 2.0f);

		emotion.setX(digimon.getX() + digimonSize.x);
		emotion.setY(digimon.getY() - emotion.getHeight());

		emotion.setVisibility(View.VISIBLE);

		Animator anim1 = ObjectAnimator.ofFloat(emotion, "alpha", 0);
		anim1.setDuration(0);
		anim1.setStartDelay(emotionFlashDuration);

		Animator anim2 = ObjectAnimator.ofFloat(emotion, "alpha", 1);
		anim2.setDuration(0);
		anim2.setStartDelay(emotionFlashDuration);

		Animator anim3 = ObjectAnimator.ofFloat(emotion, "alpha", 0);
		anim3.setDuration(0);
		anim3.setStartDelay(emotionFlashDuration);

		Animator anim4 = ObjectAnimator.ofFloat(emotion, "alpha", 1);
		anim4.setDuration(0);
		anim4.setStartDelay(emotionFlashDuration);

		Animator anim5 = ObjectAnimator.ofFloat(emotion, "alpha", 0);
		anim5.setDuration(0);
		anim5.setStartDelay(emotionFlashDuration);

		Animator anim6 = ObjectAnimator.ofFloat(emotion, "alpha", 0);
		anim6.setDuration(0);
		anim6.setStartDelay(emotionFlashDuration);

		ObjectAnimator anim7 = ObjectAnimator.ofFloat(digimon, "alpha", 0f);
		anim7.setDuration(500);
		anim7.setStartDelay(500);
		anim7.setInterpolator(new LinearInterpolator());

		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playSequentially(anim1, anim2, anim3, anim4, anim5, anim6,
				anim7);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				digimon.setAlpha(1.0f);
				emotion.setVisibility(View.INVISIBLE);
				emotion.setAlpha(1.0f);

				canPressButton = true;
				walkAnimation();
			}
		});
		animatorSet.start();

		if (emotionType == TRAIN_HAPPY)
			soundPool.play(successSound, 1.0f, 1.0f, 0, 0, 1.0f);
		else {
			soundPool.play(failSound, 1.0f, 1.0f, 0, 0, 1.0f);
		}
	}

	private void sleepAnimation() {
		digimon.setX(layoutSize.x / 2.0f - digimonSize.x / 2.0f);
		digimon.setY(layoutSize.y * 3.0f / 5.0f - digimonSize.y / 2.0f);

		if (sleepState)
			sleep.setImageResource(R.drawable.sleep1);
		else
			sleep.setImageResource(R.drawable.sleep2);

		sleep.setVisibility(View.VISIBLE);

		sleep.setX(digimon.getX() + digimonSize.x);
		sleep.setY(digimon.getY() - sleep.getHeight());

		Animator anim1 = ObjectAnimator.ofFloat(sleep, "alpha", 1);
		anim1.setDuration(0);
		anim1.setStartDelay(emotionFlashDuration);
		anim1.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				sleepState = !sleepState;
				sleep.setVisibility(View.INVISIBLE);
				if (!endAnimation) {
					walkAnimation();
				}
			}
		});
		anim1.start();
		currentAnimation = anim1;
	}
	
	private void evolutionAnimation() {
		canPressButton = false;
		
		digimon.setX(layoutSize.x / 2.0f - digimonSize.x / 2.0f);
		digimon.setY(layoutSize.y * 3.0f / 5.0f - digimonSize.y / 2.0f);
		
		newDigimon = (ImageView) findViewById(R.id.newDigimon);
		newDigimon.setImageResource(digimonModel.getPhoto());
		
		newDigimon.setX(digimon.getX());
		newDigimon.setY(digimon.getY());
		
		newDigimon.setAlpha(0.0f);
		newDigimon.setVisibility(View.VISIBLE);
		
		ObjectAnimator anim1 = ObjectAnimator.ofFloat(digimon, "alpha", 0f);
		anim1.setDuration(3500);
		//anim1.setStartDelay(500);
		anim1.setInterpolator(new LinearInterpolator());
		
		ObjectAnimator anim2 = ObjectAnimator.ofFloat(newDigimon, "alpha", 1f);
		anim2.setDuration(3500);
		//anim2.setStartDelay(500);
		anim2.setInterpolator(new LinearInterpolator());
		
		AnimatorSet animatorSet = new AnimatorSet();
		animatorSet.playTogether(anim1, anim2);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			public void onAnimationEnd(Animator animation) {
				newDigimon.setVisibility(View.INVISIBLE);
				
				digimon.setImageResource(digimonModel.getPhoto());
				digimon.setAlpha(1.0f);
				
				emotion.setImageResource(R.drawable.happy);
				emotionAnimation(TRAIN_HAPPY);
			}
		});
		animatorSet.start();
		
		soundPool.play(evoSound, 1.0f, 1.0f, 0, 2, 1.0f);
	}

	private void drawShit() {
		int shitCount = digimonModel.getShit();

		ImageView shit1 = (ImageView) findViewById(R.id.shit1);
		ImageView shit2 = (ImageView) findViewById(R.id.shit2);

		switch (shitCount) {
		case 0:
			shit1.setVisibility(View.INVISIBLE);
			shit2.setVisibility(View.INVISIBLE);
			break;

		case 1:
			shit1.setVisibility(View.VISIBLE);
			shit2.setVisibility(View.INVISIBLE);
			break;

		case 2:
			shit1.setVisibility(View.VISIBLE);
			shit2.setVisibility(View.VISIBLE);
			break;

		default:
			break;
		}

		if (shitCount == 0)
			shitPadding = 0;
		else
			shitPadding = animatePadding + shit1.getWidth();
	}

	// buttons callback
	public void statButtonPressed(View view) {
		if (canPressButton) {
			Intent intent = new Intent(this, StatActivity.class);
			startActivity(intent);
		}
	}

	public void feedButtonPressed(View view) {
		if (canPressButton
				&& digimonModel.getLevel().compareTo("Digitama") != 0) {
			checkSleep();
			Intent intent = new Intent(this, FeedActivity.class);
			startActivityForResult(intent, FEED_REQUEST_CODE);
		}
	}

	public void trainButtonPressed(View view) {
		if (canPressButton
				&& digimonModel.getLevel().compareTo("Digitama") != 0) {
			if (digimonModel.canTrain()) {
				checkSleep();
				Intent intent = new Intent(this, TrainActivity.class);
				startActivityForResult(intent, TRAIN_REQUEST_CODE);
			} else {
				stopAnimation();
				emotion.setImageResource(R.drawable.angry1);
				emotionAnimation(TRAIN_UNHAPPY);
			}
		}
	}
	
	public void battleButtonPressed(View view) {
		if (canPressButton
				&& digimonModel.getLevel().compareTo("Digitama") != 0) {
			if (digimonModel.canTrain()) {
				checkSleep();
				Intent intent = new Intent(this, BattleActivity.class);
				startActivityForResult(intent, BATTLE_REQUEST_CODE);
			} else {
				stopAnimation();
				emotion.setImageResource(R.drawable.angry1);
				emotionAnimation(TRAIN_UNHAPPY);
			}
		}
	}

	public void cleanButtonPressed(View view) {
		if (canPressButton) {
			canPressButton = false;

			stopAnimation();
			checkSleep();

			if (digimonModel.getShit() == 0)
				cleanHappy = false;
			else {
				cleanHappy = true;
			}

			water = (ImageView) findViewById(R.id.water);
			water.setY(layoutSize.y);
			water.setVisibility(View.VISIBLE);

			Animator anim1 = ObjectAnimator.ofFloat(water, "y", 0);
			anim1.setDuration(1000);
			anim1.addListener(new AnimatorListenerAdapter() {
				public void onAnimationEnd(Animator animation) {
					digimonModel.clearShit();
					drawShit();

					digimon.setX(layoutSize.x / 2.0f - digimonSize.x / 2.0f);
					digimon.setY(layoutSize.y * 3.0f / 5.0f - digimonSize.y
							/ 2.0f);

					ObjectAnimator anim7 = ObjectAnimator.ofFloat(water,
							"alpha", 0f);
					anim7.setDuration(500);
					anim7.setStartDelay(500);
					anim7.addListener(new AnimatorListenerAdapter() {
						public void onAnimationEnd(Animator animation) {
							water.setVisibility(View.INVISIBLE);
							water.setAlpha(1.0f);
							canPressButton = true;
							koMissCall();

							if (cleanHappy) {
								emotion.setImageResource(R.drawable.happy);

								emotionAnimation(TRAIN_HAPPY);
							} else {
								emotion.setImageResource(R.drawable.angry1);

								emotionAnimation(TRAIN_UNHAPPY);
							}
						}
					});
					anim7.start();
				}
			});

			anim1.start();
		}
	}

	public void lightButtonPressed(View view) {
		if (canPressButton) {
			RelativeLayout light = (RelativeLayout) findViewById(R.id.light);
			if (light.getVisibility() == View.VISIBLE)
				light.setVisibility(View.INVISIBLE);
			else {
				light.setVisibility(View.VISIBLE);
			}
		}
	}

	// all motion call this method , if interrupt sleep do sth??
	public boolean checkSleep() {
		if (digimonModel.getSleep()) {
			digimonModel.setSleep(false);
			Intent i = new Intent("sleepInterrupt");
			lbm.sendBroadcast(i);
			return true;
		}

		return false;
	}
	
	public void koMissCall(){
		if (digimonModel.getStrength()!=0 && digimonModel.getShit() ==0 && digimonModel.getHunger()!=0 )
		{
			app.setMissCall(false);
			ImageButton call = (ImageButton) findViewById(R.id.call_button);
			call.setAlpha(0.3f);
		}
	}

	// intent
	public void registerShit() {
		shitReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

			}
		};

		lbm.registerReceiver(shitReceiver, new IntentFilter("matchEvent"));
	}

	public void registerSleep() {
		sleepReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d("DEBUG", "This is SLeep.");
			}
		};

		lbm.registerReceiver(sleepReceiver, new IntentFilter("sleep"));
	}

	public void registerEvolution() {
		evolutionReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// evolution animation
				Log.d("DEBUG", "This is Evolution.");
				stopAnimation();
				evolutionAnimation();
			}
		};

		lbm.registerReceiver(evolutionReceiver, new IntentFilter("evolution"));
	}

	public void registerMissCall() {
		misscallReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				// misscall
				Log.d("DEBUG", "This is MISSCALL.");
				ImageButton call = (ImageButton) findViewById(R.id.call_button);
				call.setAlpha(1.0f);
			}
		};

		lbm.registerReceiver(misscallReceiver, new IntentFilter("misscall"));
	}
	
	@Override
	public void onBackPressed() {
	}
}
