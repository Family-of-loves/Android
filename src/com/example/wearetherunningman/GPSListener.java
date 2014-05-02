package com.example.wearetherunningman;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GPSListener implements LocationListener {
	
	private LocationManager locationManager;
    private String locationProvider = null;
    
    private double latitude;
	private double longitude;

    private Context ct;
    
    private Player player;
    
    public GPSListener(Context c, Player p){
        ct = c;
        player = p;
    	start();
    }
    
    protected void start() {
        // TODO Auto-generated method stub
    	long minTime = 10000;
		float minDistance = 0;
        try {
        	Toast.makeText(ct, "위치 확인이 시작되었습니다.", Toast.LENGTH_SHORT).show();
            
        	// LocationManager 객체 생성, LocationManager 객체는
            // Context.getSystemService(Context.LOCATION_SERVICE) 로 생성된다.
            locationManager = (LocationManager) ct.getSystemService(Context.LOCATION_SERVICE);
            Toast.makeText(ct, "LocationManager 생성", Toast.LENGTH_SHORT).show();
            
            // LocationManaer.NETWORK_PROVIDER : 기지국들로부터 현재 위치 확인
            // LocationManaer.GPS_PROVIDER : GPS들로부터 현재 위치 확인
            // boolean isProviderEnabled(String provider)
            // provider의 상태가 현재 어떠한지 boolean으로 return 해준다.
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
                // NETWORK_PROVIDER가 Enabled 라면..
                locationProvider = LocationManager.NETWORK_PROVIDER;
            } else {
                // GPS_PROVIDER가 Enabled 라면..
                locationProvider = LocationManager.GPS_PROVIDER;
            }
 
            // 현재 위치를 조회한다. 결과는 locationListener를 통해 수신
            // public void requestLocationUpdates
            // (String provider, 방법
            // long minTime, 위치 업데이트하는 최소한의 간격(단위 밀리세컨드)
            // float minDistance, 위치 업데이트되는 최소한의 거리(단위 미터)
            // LocationListener listener )
            locationManager.requestLocationUpdates(locationProvider, minTime, minDistance,this);
            Toast.makeText(ct, "RequestLocationUpdates", Toast.LENGTH_SHORT).show();
            
            /*
            // 최근 위치를 조회한다. 결과는 바로 얻을 수 있음
            // Location getLastKnownLocation(String provider)
            // Provider를 인자로 받아 마지막으로 알려진 위치를 수정해서 위치를 리턴함
            // Provider가 비활성화 되어있는 경우는 null이 return 된다.
            Location lastKnowLocation = locationManager.getLastKnownLocation(locationProvider);
            */
        } catch (Exception e) {
            Log.d("GPSListener Start() Exception", e.getMessage()); //로그켓으로 확인하려고
        }
    }
    
    public void onLocationChanged(Location location) {
		// TODO 자동 생성된 메소드 스텁
        if (location != null) {
        	longitude = location.getLongitude();
        	latitude = location.getLatitude();
        	
        	player.longitude = Double.toString(longitude);
        	player.latitude = Double.toString(latitude);
        	player.isSetComplete = true;
        } 
	}
	public void onProviderDisabled(String provider) {}
    public void onProviderEnabled(String provider) {}
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
