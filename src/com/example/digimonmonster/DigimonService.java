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

	boolean sleepInterrupt = false;
	boolean todaySleep = false;

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

		try {
			Thread.sleep(MissCallDuration);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (reason == 1 && app.getDigimon().getShit() > 0)
			app.getDigimon().addMissCall();
		if (reason == 2 && app.getDigimon().getHunger() == 0)
			app.getDigimon().addMissCall();
		if (reason == 3 && app.getDigimon().getStrength() == 0)
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
		}, 15000 , min);
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
			//if (minutes == 1
			if ((minutes == 0 && seconds > 10)
					&& app.getDigimon().getLevel().compareTo("Digitama") == 0) {
				app.getDigimon().evolution("Baby I", null, "AAA", 2, 30);
				Intent i = new Intent("evolution");
				lbm.sendBroadcast(i);
			}

			if (hours == 1
					&& app.getDigimon().getLevel().compareTo("Baby I") == 0) {
				app.getDigimon().evolution("Baby II", null, "BBB", 3, 70);
				Intent i = new Intent("evolution");
				lbm.sendBroadcast(i);
			}

			if (hours == 10
					&& app.getDigimon().getLevel().compareTo("Baby II") == 0) {
				childEvolution();
				Intent i = new Intent("evolution");
				lbm.sendBroadcast(i);
			}

			if (hours == 24
					&& app.getDigimon().getLevel().compareTo("Child") == 0) {
				adultEvolution();
				Intent i = new Intent("evolution");
				lbm.sendBroadcast(i);
			}

			if (hours != 0 && hours % 24 == 0)
				app.getDigimon().increaseAge();

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

	// Attritube =Vaccine,Data,Virus
	public void childEvolution() {
		int misscall = app.getDigimon().getMissCall();

		if (misscall <= 1)
			app.getDigimon().evolution("Child", "Vaccine", "ABCD", 6, 100);
		else if (misscall > 2 && misscall <= 4)
			app.getDigimon().evolution("Child", "Data", "ABEECD", 7, 80);
		else
			app.getDigimon().evolution("Child", "Virus", "ABEECD", 8, 70);
	}

	public void adultEvolution() {
		int traincount = app.getDigimon().getTrainCount();
		int trainsuccess = app.getDigimon().getTrainSuccess();
		int misscall = app.getDigimon().getMissCall();
		String attritube = app.getDigimon().getAttritube();

		if (attritube.compareTo("Vaccine") == 0) {
			if (traincount >= 40 && traincount - trainsuccess <= 2
					&& misscall <= 2)
				app.getDigimon().evolution("Adult", "Vaccine", "ABCDDDD", 9,
						200);
			else if (traincount > 30 && traincount < 40)
				app.getDigimon().evolution("Adult", "Vaccine", "ABDD", 10, 160);
			else
				app.getDigimon().evolution("Adult", "Data", "ABCC", 11, 150);
		} else if (attritube.compareTo("Data") == 0) {
			if (traincount >= 40 && traincount - trainsuccess <= 2
					&& misscall <= 3)
				app.getDigimon().evolution("Adult", "Data", "ABCDDdddDD", 12,
						190);
			else if (traincount > 30 && traincount < 40)
				app.getDigimon().evolution("Adult", "Vaccine", "ABDD", 13, 160);
			else if (traincount > 20 && traincount < 30)
				app.getDigimon().evolution("Adult", "Data", "ABCC", 14, 150);
			else
				app.getDigimon().evolution("Adult", "Virus", "ABEE", 15, 150);
		} else {
			if (traincount >= 40 && traincount - trainsuccess > 3
					&& misscall > 3)
				app.getDigimon().evolution("Adult", "Virus", "ABCDDdedeDD", 16,
						170);
			else if (traincount > 30 && traincount < 40)
				app.getDigimon().evolution("Adult", "Virus", "ABEE", 17, 150);
			else
				app.getDigimon().evolution("Adult", "Data", "ABCC", 18, 150);

		}
	}
}