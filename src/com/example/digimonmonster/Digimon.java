package com.example.digimonmonster;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Digimon {
	private String name;
	private int age;
	private int hunger;
	private int strength;
	private int basicpower;
	private int HP;
	private int id;
	// Attritube =Vaccine,Data,Virus
	private String attritube;
	// level = Digitama,BabyI,BabyII,Child,Adult,Perfect,Ultimate
	private String level;

	private int numofBattle;
	private int numofWin;

	// location of photo
	private int photo;

	private double DP;
	private double weight;
	private int shit;
	private boolean light;
	private boolean sleep;
	private boolean sick;
	private int misscall;

	private int trainCount;
	private int trainSuccess;

	static int babyICount = 10;
	static int babyIICount = 15;
	static int childCount = 20;
	static int adultCount = 25;

	private Context context;

	public Digimon(Context context) {
		this.context = context;
		
		this.age = 0;
		this.level = "Digitama";
		this.id = 0;
		this.weight = 20;
		this.sleep = false;
		this.light = true;
		this.DP = 20;
		this.hunger = 0;
		this.strength = 0;
		this.basicpower = 0;
		this.HP = 0;
		this.sick = false;
		this.misscall = 0;
		this.name = "Egg";
		this.trainCount = 0;
		this.trainSuccess = 0;
		this.numofBattle = 0;
		this.numofWin = 0;

	}

	public int getNumofBattle() {
		return numofBattle;
	}

	public int getNumofWin() {
		return numofWin;
	}

	public void addNumofBattle() {
		numofBattle++;
	}

	public void addNumofWin() {
		numofWin++;
	}

	public String getAttritube() {
		return attritube;
	}

	public int getTrainCount() {
		return trainCount;
	}

	public int getID() {
		return id;
	}

	public int getTrainSuccess() {
		return trainSuccess;
	}

	public String getLevel() {
		return level;
	}

	public int getAge() {
		return age;
	}

	public String getName() {
		return name;
	}

	public double getWeight() {
		return weight;
	}

	public int getPhoto() {
		String idString = "digi_" + id;
		//String idString = "digi_1002";
		Log.d("Digimon", "id: " + idString);
		int rid =  context.getResources().getIdentifier(idString, "drawable",
				context.getPackageName());
		Log.d("Digimon", "rid: " + rid);
		return rid;
//		return rid == 0 ? context.getResources().getIdentifier("digi_0001", "drawable",
//				context.getPackageName()) : rid;
	}

	public double getDP() {
		return DP;
	}

	public int getHunger() {
		return hunger;
	}

	public int getStrength() {
		return strength;
	}

	public int getBasicPower() {
		return this.basicpower;
	}

	public int getMissCall() {
		return misscall;
	}

	public void addMissCall() {
		misscall++;
	}

	public boolean getLight() {
		return light;
	}

	public boolean getSleep() {
		return sleep;
	}

	public int getShit() {
		return shit;
	}

	public void refillDP() {
		DP = 20.0;
	}

	public void evolution(Digidata digi) {
		this.id = digi.id;
		this.basicpower = digi.id;
		this.HP = digi.HP;
		this.name = digi.name;
		if (digi.attritube != null)
			this.attritube = digi.attritube;
		this.level = digi.level;
	}

	public boolean loseStrength() {
		if (strength > 0) {
			strength--;
			return true;
		} else
			return false;
	}

	public boolean loseHunger() {
		if (hunger > 0) {
			hunger--;
			return true;
		} else
			return false;
	}

	public boolean addShit() {
		if (shit < 7) {
			shit++;
			return true;
		} else
			return false;
	}

	public void clearShit() {
		shit = 0;
	}

	public void setLight(boolean on) {
		if (on)
			light = true;
		else
			light = false;
	}

	public void setSleep(boolean slept) {
		if (slept)
			sleep = true;
		else
			sleep = false;
	}

	public int feed() {
		// 1=feed success 0=fail -1=sick
		if (weight > 99 || sick)
			return 0;
		else if (hunger < 4) {
			hunger++;
			weight = weight + 2;
			if (weight > 99) {
				sick = true;
				sickTimer();
				return -1;
			}
			return 1;
		} else
			return 0;
	}

	public void sickTimer() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					Thread.sleep(1000 * 60 * 20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sick = false;
			}
		}, 0);
	}

	public int eatVitamin() {
		// 1=feed success 0=fail -1=sick
		if (weight > 99 || sick)
			return 0;
		else {
			DP = DP + 0.25;
			weight = weight + 2;
			if (weight > 99) {
				sick = true;
				sickTimer();
				return -1;
			}
			return 1;
		}
	}

	public void increaseAge() {
		age++;
	}

	public boolean canTrain() {
		if (hunger == 0 || DP <= 1.0 || weight < 10)
			return false;
		else
			return true;
	}

	public double train(int count) {

		double probability = 0;
		boolean success = false;
		int requiredCount = 0;
		trainCount++;
		hunger--;
		DP = DP - 1;
		weight = weight - 5;

		if (level.compareTo("BabyI") == 0) {
			requiredCount = babyICount;
		}
		if (level.compareTo("BabyII") == 0) {
			requiredCount = babyIICount;
		}
		if (level.compareTo("Child") == 0) {
			requiredCount = childCount;
		}

		if (level.compareTo("Adult") == 0) {
			requiredCount = adultCount;
		}

		probability = Math.abs(requiredCount - count) / 10.0 - 0.2;
		
		if(probability < 0)
			probability = 0;
		
		if(probability > 1)
			probability = 1;
		
		return probability;

	}

	public void trainResult(boolean success) {
		if (success) {
			trainSuccess++;
			if (strength <= 3)
				strength++;
		} else {
			if (strength > 0)
				strength--;
		}
	}
}
