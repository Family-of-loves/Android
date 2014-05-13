package com.example.wearetherunningman;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.util.Log;

public class Participant {
	Context context;
	DBManagerHandler dbHandler;
	
	public Participant(Context c,GoogleMap gmap){
		// »ý¼º
		this.context = c;
		dbHandler = new DBManagerHandler(this.context);
	}
	
	public void regParticipant(JSONObject obj){
		try {
			String[] participant = dbHandler.search(obj.getString("uid"));
			
			if(participant == null) {
				dbHandler.insert(obj);
			}else {
				dbHandler.update(obj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void unRegParticipant(JSONObject obj){
		try {
			dbHandler.delete(obj.getString("uid"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void regMarker(){
		ArrayList<String[]> resultAll = dbHandler.read();
		
		for(int i=0; i < resultAll.size(); i++){
			String[] row = resultAll.get(i);
			for(int j=0; j < row.length; j++)
			System.out.println("Elements "+ i +": "+ row[j]);
		}
	}
}
