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
	/**
	 * @uml.property  name="uid"
	 */
	String uid ;
	/**
	 * @uml.property  name="name"
	 */
	String name ;
	/**
	 * @uml.property  name="team"
	 */
	String team ;
	/**
	 * @uml.property  name="item"
	 */
	String item ;
	
	/**
	 * @uml.property  name="latitude"
	 */
	double latitude ;
	/**
	 * @uml.property  name="longitude"
	 */
	double longitude ;
	
	/**
	 * @uml.property  name="isAlive"
	 */
	boolean isAlive = true;
	/**
	 * @uml.property  name="isSetComplete"
	 */
	boolean isSetComplete = false;
	
	/**
	 * @uml.property  name="zoomLevel"
	 */
	private int zoomLevel = 15;
	/**
	 * @uml.property  name="zoomFlag"
	 */
	private boolean zoomFlag = false;
	/**
	 * @uml.property  name="gmap"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private GoogleMap gmap;
	/**
	 * @uml.property  name="loc"
	 * @uml.associationEnd  
	 */
	private LatLng loc;
	
	public Player(String name, String team, String item, Context context,GoogleMap gmap){
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
        	
        	loc = new LatLng(this.latitude, this.longitude);
        	gmap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        	
        	if(!zoomFlag){
        		gmap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        		zoomFlag = true;
        	}
        }
	}
}