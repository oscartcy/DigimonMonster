package com.example.digimonmonster;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

public class DigimonService extends Service {

	protected DigimonMonster app;
	protected TimerHandler timerHandler;
	protected LocalBroadcastManager lbm;
	protected BroadcastReceiver mReceiver;
	protected Digidatabase database;

	boolean sleepInterrupt = false;
	boolean todaySleep = false;
	boolean perfectTried;
	private final IBinder mBinder = new LocalBinder();

	static final int min = 60000;

	static final int ShitDelay = 20 * min;
	static final int ShitPeriod = 40 * min;

	static final int HungerDelay = 2 * min;
	static final int HungerPeriod = 30 * min;

	static final int StrengthDelay = 50 * min;
	static final int StrengthPeriod = 50 * min;

	static final int MissCallDelay = 10 * min;
	static final int MissCallDuration = 20 * min;

	static final int SleepDuration = 60 * min * 12;
	static final int DeathDuration = min * 30;

	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	public class LocalBinder extends Binder {
		DigimonService getService() {
			return DigimonService.this;
		}
	}

	public void onCreate() {
		app = (DigimonMonster) getApplicationContext();
		lbm = LocalBroadcastManager.getInstance(app);

		perfectTried = false;
		database = new Digidatabase(app);
		database.readDatabse("Digidatabase.txt");

		startTimer();
		startShitTimer();
		startHungerTimer();
		startStrengthTimer();

	}

	private void startShitTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (app.getDigimon().getSleep() == false) {
					if (app.getDigimon().addShit()) {
						Intent i = new Intent("addShit");
						lbm.sendBroadcast(i);
					}
					if (app.getDigimon().getShit() > 0) {
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							public void run() {
								if (app.getDigimon().getShit() > 0)
									startMissCallTimer(1);
							}
						}, MissCallDelay);
					}
				}
			}
		}, ShitDelay, ShitPeriod);

	}

	private void startHungerTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if (app.getDigimon().getSleep() == false) {
					if (app.getDigimon().loseHunger()) {
						Intent i = new Intent("loseHunger");
						lbm.sendBroadcast(i);
					}

					if (app.getDigimon().getHunger() == 0) {
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							public void run() {
								if (app.getDigimon().getHunger() == 0)
									startMissCallTimer(2);
							}
						}, MissCallDelay);
					}
				}
			}
		}, HungerDelay, HungerPeriod);

	}

	private void startStrengthTimer() {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			public void run() {
				if (app.getDigimon().getSleep() == false) {
					if (app.getDigimon().loseStrength()) {
						Intent i = new Intent("loseStrength");
						lbm.sendBroadcast(i);
					}
					if (app.getDigimon().getStrength() == 0) {
						Timer timer = new Timer();
						timer.schedule(new TimerTask() {
							public void run() {
								if (app.getDigimon().getStrength() == 0)
									startMissCallTimer(3);
							}
						}, MissCallDelay);
					}
				}
			}
		};
		timer.schedule(task, StrengthDelay, StrengthPeriod);

	}

	private void startMissCallTimer(int reason) {
		// 1= Shit 2=Hunger 3=Strength
		// check app foreground and then sd noti to notification bar
		ActivityManager am;
		am = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = null;
		if (am != null) {
			cn = am.getRunningTasks(2).get(0).topActivity;
		}

		if (!cn.getPackageName().equals("com.example.digimonmonster")) {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			// find a digimon icon
			Bitmap bm = BitmapFactory.decodeResource(getResources(),
					R.drawable.angry1);

			bm = Bitmap.createScaledBitmap(bm, 50, 50, true);

			Intent notintent = new Intent(app, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getActivity(app, 0,
					notintent, PendingIntent.FLAG_UPDATE_CURRENT);

			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					app).setContentTitle("Miss Call")
					.setContentText("A miss call from Digimon")
					.setLargeIcon(bm).setContentIntent(pendingIntent);
			Notification notification = mBuilder.build();
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			notification.defaults |= Notification.DEFAULT_VIBRATE;

			mNotificationManager.notify(1, notification);

		}
		// no matter what also sd intent to activity
		Intent i = new Intent("misscall");
		lbm.sendBroadcast(i);
		app.setMissCall(true);
		for (int k = 0; k < 40; k++) {

			try {
				Thread.sleep(min / 2);
			} catch (InterruptedException x) {
			}
			if (app.getMissCall() == false)
				return;
		}
		app.getDigimon().addMissCall();

	}

	private void startTimer() {
		if (app.getStartTime() == 0L) {
			app.setStartTime();
		}
		timerHandler = new TimerHandler();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				timerHandler.sendEmptyMessage(0);
			}
		}, 100, min);
	}

	private void startSleeping() {
		app.getDigimon().setSleep(true);
		Intent i = new Intent("sleep");
		lbm.sendBroadcast(i);
		mReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				sleepInterrupt = true;
				lbm.unregisterReceiver(mReceiver);
			}
		};
		lbm.registerReceiver(mReceiver, new IntentFilter("sleepInterrupt"));
	}

	private void endSleeping() {
		app.getDigimon().setSleep(false);
		app.getDigimon().refillDP();
		if (mReceiver != null)
			lbm.unregisterReceiver(mReceiver);
	}

	private void checkDeath() {
		if (app.getDigimon().getStrength() == 0
				&& app.getDigimon().getHunger() == 0
				&& app.getDigimon().getShit() == 7) {
			Thread t = new Thread() {
				boolean deathflag = true;

				public void run() {
					for (int i = 0; i < 30; i++) {
						if (app.getDigimon().getStrength() == 0
								&& app.getDigimon().getHunger() == 0
								&& app.getDigimon().getShit() == 7) {
							try {
								Thread.sleep(min);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							continue;
						} else {
							deathflag = false;
							break;
						}

					}

					if (deathflag) {
						Intent i = new Intent("gameover");
						lbm.sendBroadcast(i);
					}
				}
			};
			t.start();

		}
	}

	final class TimerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			long millis = System.currentTimeMillis() - app.getStartTime();
			int seconds = (int) (millis / 1000);
			int minutes = seconds / 60;
			seconds = seconds % 60;
			int hours = minutes / 60;
			minutes = minutes % 60;

			// check evolution send broadcast
			if (minutes == 1
					&& app.getDigimon().getLevel().compareTo("Digitama") == 0) {
				database.toBABYI(app.getDigimon());
				Intent i = new Intent("evolution");
				lbm.sendBroadcast(i);
			}

			if (hours == 1
					&& app.getDigimon().getLevel().compareTo("Baby I") == 0) {
				database.toBABYII(app.getDigimon());
				Intent i = new Intent("evolution");
				lbm.sendBroadcast(i);
			}

			if (hours == 10
					&& app.getDigimon().getLevel().compareTo("Baby II") == 0) {
				database.childEvolution(app.getDigimon());
				Intent i = new Intent("evolution");
				lbm.sendBroadcast(i);
			}

			if (hours == 48
					&& app.getDigimon().getLevel().compareTo("Child") == 0) {
				database.adultEvolution(app.getDigimon());
				Intent i = new Intent("evolution");
				lbm.sendBroadcast(i);
			}

			if (hours != 0 && hours % 24 == 0)
				app.getDigimon().increaseAge();

			if (app.getDigimon().getAge() >= 5 && perfectTried == false) {
				database.perfectEvolution(app.getDigimon());
				perfectTried = true;
			}

			// check death
			checkDeath();

			// This is for sleep
			Date startDate = new Date();
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(startDate);
			if (calendar.get(Calendar.HOUR_OF_DAY) == 20 && todaySleep == false)
				startSleeping();

			todaySleep = true;

			if (calendar.get(Calendar.HOUR_OF_DAY) == 8
					&& sleepInterrupt == false)
				endSleeping();

			sleepInterrupt = false;

		}
	}

}