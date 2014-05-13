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
	
	/*
    private Marker marker;   
    private MarkerOptions markerOpt;
    */
    
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
        	
        	loc = new LatLng(this.latitude, this.longitude); // ���移� 醫���� ��ㅼ��
        	
        	gmap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        	gmap.animateCamera(CameraUpdateFactory.zoomTo(zoomLevel));
        	/*MarkerOptions optSecond = new MarkerOptions();
        	optSecond.position(loc);
    		// ������ ��� 寃쎈��
    		optSecond.title("��댁��移�");
    	 // ���紐� 誘몃━蹂닿린
    		optSecond.snippet("��대┃������");
    	//gmap.addMarker(optSecond).showInfoWindow();
    	
    		Marker m=gmap.addMarker(optSecond);		
    		//Marker marker1 = gmap.addMarker(markerOpt1);
        	*/
        }
	}
	
	

	
	
}