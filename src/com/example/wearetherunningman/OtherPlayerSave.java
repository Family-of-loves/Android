package com.example.wearetherunningman;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class OtherPlayerSave {

	OtherPlayer[] otherplayer = new OtherPlayer[20]; // 객체배열 
	int j=0;
	
	public OtherPlayerSave(){
		for (int i = 0; i < otherplayer.length; i++)  // 객체배열 초기화!!
				{
					otherplayer[i] = new OtherPlayer();
					otherplayer[i].id=null;
				}
	}
	
	public void compare(JSONObject jsondata) {
		// TODO Auto-generated method stub
		String receive_uid=null;
		try {
			receive_uid = jsondata.get("uid").toString();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
		e1.printStackTrace();
		}
		
		Log.i("받은 id",receive_uid);
		///해결할 부분
		for(int k=0; k<otherplayer.length; k++){
			
			if(receive_uid.equals(otherplayer[k].id)){ // 받아온 아이디를 배열0부터 비교.. 같은게 있다면
				array_update(jsondata,k);			// 갱신
				break;
				
			}
			else		// 같은게 없다면
			{
				
				if (k==j)  //j가 배열 번호, 그 번호부터 비어잇다는 말이다.
					{
						array_insert(jsondata);
						break;
					}
				
			}
					
		}
	}
		
	void array_update(JSONObject jsondata,int i){
		
		try {
				otherplayer[i].id = jsondata.get("uid").toString();
		} catch (JSONException e1) {
				// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		try {
			otherplayer[i].name = jsondata.get("username").toString();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			otherplayer[i].team = jsondata.get("team").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			otherplayer[i].latitude = jsondata.get("latitude").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			otherplayer[i].longitude = jsondata.get("longitude").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(i);		
		
		Log.i("갱신됨",otherplayer[i].id+""+otherplayer[i].name);
		}
		
		
		// 생성 메소드
		void array_insert(JSONObject jsondata){
				
		try {
			otherplayer[j].id = jsondata.get("uid").toString();
		} catch (JSONException e1) {
				// TODO Auto-generated catch block
			e1.printStackTrace();
		}
							
		try {
			otherplayer[j].name = jsondata.get("username").toString();
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			otherplayer[j].team = jsondata.get("team").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			otherplayer[j].latitude = jsondata.get("latitude").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			otherplayer[j].longitude = jsondata.get("longitude").toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(j);		
		
		Log.i("생성됨",otherplayer[j].id+""+otherplayer[j].name);
		j++; // 배열 인덱스 증가
		}
		
		
		
	
}
