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
	
	public Player(String name, String team, String item, Context context,GoogleMap gmap){
		this.gmap = gmap;
		this.gmap.setMyLocationEnabled(true);
		this.gmap.setOnMyLocationChangeListener(this);
				
		this.uid = Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		this.name = name;
		this.team = team;
		this.item = item;
		
		System.out.println("사용자 생성");
	}
	@Override
	public void onMyLocationChange(Location location) {
		if (location != null) {
        	this.latitude = location.getLatitude();
        	this.longitude = location.getLongitude();
        	
        	loc = new LatLng(this.latitude, this.longitude);
        	
        	gmap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        	gmap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        }
	}
}