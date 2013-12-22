package com.example.digimonmonster;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;


public class Digimon {
	private String name;
	private int age;
	private int hunger;
	private int strength;
	private int basicpower;
	// Attritube =Vaccine,Data,Virus
	private String attritube;
	//level = Digitama,Baby I,Baby II,Child,Adult,Perfect,Ultimate
	private String level;
	
	
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

	
	static int babyICount=10;
	static int babyIICount=15;
	static int childCount=20;
	static int adultCount=25;


public Digimon()
{
	this.age=0;
	this.level="Digitama";
	this.photo=R.drawable.digi_0001;
	this.weight=20;
	this.sleep=false;
	this.light=true;
	this.DP=20;
	this.hunger=0;
	this.strength=0;
	this.basicpower=0;
	this.sick=false;
	this.misscall=0;
	this.name="Egg";
	this.trainCount=0;
	this.trainSuccess=0;
}

public String getAttritube(){
	return attritube;
}
	public int getTrainCount(){
		return trainCount;
	}
	
	public int getTrainSuccess(){
		return trainSuccess;
	}
	public String getLevel(){
		return level;
	}
	
	public int getAge(){
		return age;
	}
	
	public String getName(){
		return name;
	}
	public double getWeight(){
		return weight;
	}
	
	public int getPhoto(){
		return photo;
	}
	
	public double getDP(){
		return DP;
	}

	public int getHunger(){
		return hunger;
	}
	
	public int getStrength(){
		return strength;
	}
	
	public int getBasicPower(){
		return this.basicpower;
	}
	
	public int getMissCall(){
		return misscall;
	}
	
	public void addMissCall(){
		misscall++;
	}
	public boolean getLight(){
		return light;
	}
	
	public boolean getSleep(){
		return sleep;
	}
	
	public int getShit(){
		return shit;
	}
	
	public void refillDP(){
		DP=20.0;
	}
	
	public void evolution(String level,String attritube,String name,int photo,int basicpower)
	{
		this.level=level;
		if (attritube!=null)
			this.attritube=attritube;
		this.name=name;
		this.photo=R.drawable.digi_1000;
		this.basicpower=basicpower;
	}
	
	
	public boolean loseStrength()
	{
		if (strength>0)
		{
			strength--;
			return true;
		}
		else
			return false;
	}
	
	public boolean loseHunger()
	{
		if (hunger>0)
		{
			hunger--;
			return true;
		}
		else
			return false;
	}
	public boolean addShit(){
		if (shit<7)
		{
			shit++;
			return true;
		}
		else
			return false;
	}
	
	public void clearShit(){
		shit=0;
	}
	
	public void setLight(boolean on){
		if (on)
			light=true;
		else
			light=false;
	}
	
	public void setSleep(boolean slept)
	{
		if (slept)
			sleep=true;
		else
			sleep=false;
	}
	
	public int feed()
	{
		// 1=feed success 0=fail -1=sick
		if (weight>99 || sick)
			return 0;			
		else if (hunger<4)
		{
			hunger++;
			weight=weight+2;
			if (weight>99)
			{
				sick=true;
				sickTimer();
				return -1;
			}
			return 1;
		}
		else return 0;
	}
	
	public void sickTimer()
	{
		Timer timer=new Timer();
		timer.schedule(new TimerTask(){
			public void run(){
				try {
		 	        Thread.sleep(1000*60*20);
				 	} catch (InterruptedException e) {
		 	        e.printStackTrace(); }
				sick=false;
			}
		},0);
	}
	
	
	
	public int eatVitamin(){
		// 1=feed success 0=fail -1=sick
		if (weight>99 || sick)
			return 0;			
		else 
		{
			DP=DP+0.25;
			weight=weight+2;
			if (weight>99)
			{
				sick=true;
				sickTimer();
				return -1;
			}
			return 1;
		}
	}
	
	public void increaseAge(){
		age++;
	}
	
	
	
	public boolean canTrain(){
		if (hunger==0 || DP <=1.0 || weight <10)
			return false;
		else
			return true;
	}
	public boolean train(int count)
	{
		
		boolean success=false;
		trainCount++;
	    hunger--;
	    DP=DP-1;
	    weight=weight-5;
	    if (level.compareTo("Baby I")==0)
	    {
	    }
	    
	    
	    
	    return success;
	}
	
	public boolean train(boolean success)
	{ 
		trainCount++;
	    hunger--;
	    DP=DP-1;
	    if (weight>10)
	    	weight=weight-10;
	    else
	    	weight=0;
	    if (success)
	    {
	    	trainSuccess++;
	    	if (strength<=3)
	    		strength++;
	    	return true;
	    }
	    else
	    {
	    	if (strength>0)
	    		strength--;
	    	return false;
	    }
	    	
	    
	}
}



