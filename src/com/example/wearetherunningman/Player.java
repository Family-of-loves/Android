package com.example.wearetherunningman;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;

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

    //private Marker marker;
    //private MarkerOptions markerOpt;
    
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
        	gmap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        	/*if(marker!=null){
    			marker.remove();
    		};*/
        	//markerOpt=new MarkerOptions().position(loc).title("정명").snippet("클릭하세요");
        	//marker = gmap.addMarker(markerOpt);
        }
	}
}