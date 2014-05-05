package com.example.wearetherunningman;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.Secure;

public class Player extends Application {
	String id ;
	String name ;
	String team ;
	Double latitude ;
	Double longitude ;
	String item;
	boolean isAlive = true;
	boolean isSetComplete = false;
	
	GPSListener gc;
	
	public Player(String name, String team, String item, Context context){
		// ���̾�α� ���� �Է� �� �����Ϳ� ���� �ʱ�ȭ
		this.id = Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		this.name = name;
		this.team = team;
		this.item = item;
		gc = new GPSListener(context, this);
	}
}