package com.example.digimonmonster;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Digidatabase {
	
	protected ArrayList<Digidata> database;
	private DigimonMonster app;
	
	public Digidatabase(DigimonMonster app){
		database= new ArrayList<Digidata>();
		this.app = app;
	}
	
	
	public void readDatabse(String location){
//		File sdcard = Environment.getExternalStorageDirectory();
//		File file = new File(sdcard, location);
		InputStream databaseInputStream = app.getResources().openRawResource(R.raw.digidatabase);
		
		try
		{
			Scanner sc=new Scanner(databaseInputStream);
			sc.nextLine();
			while (sc.hasNextLine()){
				String data=sc.nextLine();
				StringTokenizer st1= new StringTokenizer(data);
				int id=Integer.valueOf(st1.nextToken());
				String name=st1.nextToken();
				String level=st1.nextToken();
				String attritube=st1.nextToken();
				String next=st1.nextToken();
				String[] nextDigi=next.split(",");
				int[] array=new int[nextDigi.length];
				for (int i=0;i < nextDigi.length;i++)
					array[i]=Integer.valueOf(nextDigi[i]);
				int basicpower=Integer.valueOf(st1.nextToken());
				int HP=Integer.valueOf(st1.nextToken());
				Digidata digidata=new Digidata(id,name,level,attritube,array,basicpower,HP);
				database.add(digidata);
		
			}
			
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public Digidata findDigi(int id )
	{
		for (int i=0; i<database.size();i++)
		{
			if (database.get(i).getID()==id)
				return database.get(i);
		}
		return null;
	}
	
	public void toBABYI(Digimon digimon)
	{
		Digidata target=null;
		for (int i=0; i<database.size();i++)
		{
			if (database.get(i).getID()==1000)
				target=database.get(i);
		}
		digimon.evolution(target);
	}
	
	public void toBABYII(Digimon digimon)
	{
		Digidata target=null;
		for (int i=0; i<database.size();i++)
		{
			if (database.get(i).getID()==2000)
				target=database.get(i);
		}
		digimon.evolution(target);
	}
	
	
	public void childEvolution(Digimon digimon)
	{
		int misscall=digimon.getMissCall();
		Digidata target=null;
		if (misscall<=3)
		{
			for (int i=0; i<database.size();i++)
			{
				if (database.get(i).getID()==3001)
					target=database.get(i);
			}
			digimon.evolution(target);
		}
		else
		{
			for (int i=0; i<database.size();i++)
			{
				if (database.get(i).getID()==3002)
					target=database.get(i);
			}
			digimon.evolution(target);
		}
	}
	
	
	public boolean perfectEvolution(Digimon digimon)
	{
		double probability=0;
		if (digimon.getNumofBattle()<20)
			return false;
		else  if ((digimon.getNumofBattle()<50))
			probability=0.5- ((double)(50-digimon.getNumofBattle())/100);
		else
			probability=0.5;
		probability=probability+ (double)digimon.getNumofWin()/(digimon.getNumofBattle()+digimon.getNumofWin() * 0.7);
		
		int targetid=0;
		Random random=new Random();
		double chance=random.nextDouble();
		if (probability>=chance)
		{
			if (digimon.getID()==4001 ||digimon.getID()==4003 || digimon.getID()==4005)
				targetid=5001;
			
			if (digimon.getID()==4002 ||digimon.getID()==4004 || digimon.getID()==4006)
				targetid=5002;
			
			if (digimon.getID()==4007)
				targetid=5003;
			
			Digidata target=this.findDigi(targetid);
			digimon.evolution(target);
			
			return true;
		}
		else
			return false;
	
	}
	
	public void adultEvolution(Digimon digimon)
	{
		Digidata original=findDigi(digimon.getID());
		int misscall=digimon.getMissCall();
		int trainsuccess=digimon.getTrainSuccess();
		int targetid=4007;
		if (original.id==3001)
		{
			if (misscall<=3 && trainsuccess>=32)
				targetid=4001;
			else if (misscall>4 && trainsuccess>=5 && trainsuccess<=15)
				targetid=4002;
			else if (misscall<=3 && trainsuccess<31)
				targetid=4003;
			else if (misscall>5 && trainsuccess>16)
				targetid=4004;		
		}
		else
		{
			if (misscall<=3 && trainsuccess>=47)
				targetid=4003;
			else if (misscall<=3 && trainsuccess<47)
					targetid=4004;
			else if (misscall>4 && trainsuccess>=8)
					targetid=4005;
			else if (misscall>4 && trainsuccess<8)
					targetid=4006;
		}
		Digidata target=this.findDigi(targetid);
		digimon.evolution(target);
			
	}
}
	



