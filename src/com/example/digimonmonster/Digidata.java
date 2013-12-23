package com.example.digimonmonster;

public class Digidata {

	protected int id;
	protected String name;
	protected String attritube;
	protected String level;
	protected int basicPower;
	protected int HP;
	protected int[] nextDigimon;

	public Digidata(int id, String name, String level, String attritube,
			int[] nextDigimon,int basicPower,int HP) {
		this.id = id;
		this.name = name;
		this.attritube = attritube;
		this.level = level;
		this.basicPower=basicPower;
		this.HP=HP;
		
		this.nextDigimon = nextDigimon;
	}
	
	public int getID(){
		return id;
	}
	
	public int getHP(){
		return HP;
	}
	
	public int getBasicPower(){
		return basicPower;
	}
	
	public String getName(){
		return name;
	}
	
	public String getAttritube(){
		return attritube;
	}
	
	public String getLevel(){
		return level;
	}
	
	public int[] getNextDigimon(){
		return nextDigimon;
	}
}