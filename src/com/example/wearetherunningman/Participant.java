package com.example.wearetherunningman;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;

import android.content.Context;
import android.util.Log;

public class Participant {
	Context context;
	DBManagerHandler dbHandler;
	
	public Participant(Context c,GoogleMap gmap){
		// 생성
		this.context = c;
		dbHandler = new DBManagerHandler(this.context);
	}
	
	public void regParticipant(JSONObject obj){
		dbHandler.read();
		System.out.println("Debug : 참가자 DB 읽음");
		try {
			String[] participant = dbHandler.search(obj.getString("uid"));
			
			if(participant == null) {
				dbHandler.insert(obj);
				System.out.println("Debug : 참가자 DB 삽입");
			}else {
				dbHandler.update(obj);
				System.out.println("Debug : 참가자 DB 수정");
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
		int rowCnt = dbHandler.getRow();
		
		String[][] allParticipant = dbHandler.read();
		
		
		/*for(int i=0; i < rowCnt ; i++){
			for(int j=0; j < 4 ; j++)
			System.out.println("Debug : " + allParticipant[i][j] + "\n");
		}*/
		
	}
	
}
