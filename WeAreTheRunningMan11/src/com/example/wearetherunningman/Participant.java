package com.example.wearetherunningman;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;

public class Participant extends Application {
	
	String uid ;
	String name ;
	String team ;
	String latitude ;
	String longitude ;
	String item;
	boolean isAlive = true;
	boolean isSetComplete = false;
	
	public Participant(JSONObject obj) throws JSONException{
		this.uid = obj.getString(uid);
		this.name = obj.getString(name);
		this.team = obj.getString(team);
		this.latitude = obj.getString(latitude);
		this.longitude = obj.getString(longitude);
		this.item = obj.getString(item);		
	}
	
}
