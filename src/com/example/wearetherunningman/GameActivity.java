package com.example.wearetherunningman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Build;

public class GameActivity extends ActionBarActivity implements WsCallbackInterface {
	LatLng loc1;
	public double lat;
	public double lon;
	/**
	 * @uml.property  name="player"
	 * @uml.associationEnd  
	 */
	Player player;
	GoogleMap gmap;
	/**
	 * @uml.property  name="dbHandler"
	 * @uml.associationEnd  
	 */
	DBManagerHandler dbHandler;
	/**
	 * @uml.property  name="ws"
	 * @uml.associationEnd  
	 */
	WsConn ws = new WsConn(this);
	Marker marker1;
    MarkerOptions markerOpt1;
    String room;
    String name ;
    String team;
    String item;
     
     //Marker_Make m;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_game);

		dbHandler = new DBManagerHandler(getApplicationContext());
		gmap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.gMap)).getMap();
		//m=new Marker_Make();

		ws.run("http://dev.hagi4u.net:3000");
		
		Intent intent = getIntent(); // 값을 받아온다.
	    room = intent.getExtras().getString("param1"); 
	    name = intent.getExtras().getString("param2"); 
	    team = intent.getExtras().getString("param3"); 
	    item = intent.getExtras().getString("param4");

	    
	    Toast.makeText(getApplicationContext(),room+" "+name+" "+ team +" "+ item,  Toast.LENGTH_SHORT).show();
	    lat=36.1111111;
		lon=128.1111111;
		loc1 = new LatLng(lat,lon); // 위치 좌표 설정	
		markerOpt1=new MarkerOptions();
		//markerOpt1.position(loc1);
		//markerOpt1.title("초기값");
		//markerOpt1.snippet("클릭하세요");
	    
		player = new Player(name ,team,item ,getApplicationContext(), gmap);
		
		Toast.makeText(getApplicationContext(), "사용자 생성!",  Toast.LENGTH_SHORT).show();
		ws.emitJoin(room, player);
		
		emitServer();
		
	}
	
	public void emitServer(){
		new Thread(new Runnable() {           
			public void run() {
				while (true) {
					try {
						//Location location = null;
						ws.emitMessage(player);
						//player.onMyLocationChange(location,markerOpt1);
						//Marker marker1 = gmap.addMarker(markerOpt1);
						//markerOpt=new MarkerOptions().position(loc);
						
						Thread.sleep(10000);
						///m.marker(markerOpt1,loc1,gmap);
						//marker1=gmap.addMarker(markerOpt1);	
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }).start();
	}

	@Override
	public void on(String event, JSONObject obj) {
		if (event.equals("message")){
			
			dbHandler.read();
			try {
				String[] participant = dbHandler.search(obj.getString("uid"));
				
				if(participant == null){
					// DB에 사용자 정보를 삽입
					Log.i("처음","들어옴");
					dbHandler.insert(obj);
				} else {
					// DB에 정보를 빼와서 좌표값 수정 (participant[2] = latitude / participant[3] = longitude
					dbHandler.update(obj);
					Log.i("아까","들어옴");
					/*
					Log.i("SQLite", "uid ="+ participant[0] + 
									" / 이름 =" +participant[1]+ 
									" / 위도 ="+participant[2] + 
									" / 경도 =" + participant[3] + 
									"가 검색 되었습니다.");  */	
					try{
			        	 lat= Double.parseDouble(participant[2]);
			        	 lon=Double.parseDouble(participant[3]);
			        	 
			        	 loc1 = new LatLng(lat, lon); // 위치 좌표 설정	
			        	
			        	 markerOpt1.position(loc1);
			        	 markerOpt1.title("바뀐놈");
			        	 markerOpt1.snippet("클릭하세요");
			        	 Log.i("요까지","들어온다");	
			        	 
			           	}
			        	catch (NumberFormatException e) {
			        	    Log.e("TAG", "Couldn't parse latitude and/or longitude");
			        	}
					//marker=gmap.addMarker(markerOpt);
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (event.equals("leaved")){
			try {
				dbHandler.delete(obj.getString("uid"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// 에러처리
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	@SuppressLint("NewApi")
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_game, container,false);
			     
			return rootView;
		}
	}

	@Override
	public void callback(JSONArray data) throws JSONException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMessage(JSONObject json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectFailure() {
		// TODO Auto-generated method stub
		
	}
}
	


