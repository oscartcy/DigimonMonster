package com.example.digimonmonster;


import android.app.Application;
public class DigimonMonster extends Application{
	private Digimon digimon;
	private long startTime = 0L;
	
	
	public Digimon getDigimon(){
		return digimon;
	}

	public void setDigimon(Digimon digimon){
		this.digimon=digimon;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public void setStartTime() {
		startTime = System.currentTimeMillis();
	}
}
