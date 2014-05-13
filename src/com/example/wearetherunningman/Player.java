package com.example.wearetherunningman;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Location;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.app.FragmentActivity;

public class Player extends FragmentActivity implements OnMyLocationChangeListener {
	String uid ;
	String name ;
	String team ;
	String item ;
	
	double latitude ;
	double longitude ;
	
	boolean isAlive = true;
	boolean isSetComplete = false;
	
	private int zoomLevel = 15;
	private GoogleMap gmap;
	private LatLng loc;
	
    private Marker marker;
   
    private MarkerOptions markerOpt;
    
    
	public Player(String name, String team, String item, Context context,GoogleMap gmap){
		// 다이어로그 에서 입력 한 데이터에 대해 초기화
		this.gmap = gmap;
		this.gmap.setMyLocationEnabled(true);
		this.gmap.setOnMyLocationChangeListener(this);
				
		this.uid = Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		this.name = name;
		this.team = team;
		this.item = item;
	}
	@Override
	public void onMyLocationChange(Location location) {
		if (location != null) {
        	this.latitude = location.getLatitude();
        	this.longitude = location.getLongitude();
        	
        	loc = new LatLng(this.latitude, this.longitude); // 위치 좌표 설정
        	
        	gmap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        	//gmap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        	MarkerOptions optSecond = new MarkerOptions();
        	optSecond.position(loc);
    		// 위도 • 경도
    		optSecond.title("내위치");
    	 // 제목 미리보기
    		optSecond.snippet("클릭하삼");
    	//gmap.addMarker(optSecond).showInfoWindow();
    	
    		Marker m=gmap.addMarker(optSecond);		
    		//Marker marker1 = gmap.addMarker(markerOpt1);
        	
        }
	}
	
	

	
	
}