package com.example.wearetherunningman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

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

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		dbHandler = new DBManagerHandler(getApplicationContext());
		
		gmap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.gMap)).getMap();
		
		ws.run("http://dev.hagi4u.net:3000");
		
		Intent intent = getIntent(); // 값을 받아온다.
	    String room = intent.getExtras().getString("param1"); 
	    String name = intent.getExtras().getString("param2"); 
	    String team = intent.getExtras().getString("param3"); 
	    String item = intent.getExtras().getString("param4");
	    
	    Toast.makeText(getApplicationContext(),room+" "+name+" "+ team +" "+ item,  Toast.LENGTH_SHORT).show();
	    
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
						Location location = null;
						player.onMyLocationChange(location);
						ws.emitMessage(player);
						Thread.sleep(10000);                       
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
			/*
			 * 사용자 정보 업데이
			 * 1. 객체 or DB 검색
			 * 2. gmap 마커 삭제 후 다시 찍기
			 * 3. 팀 구분
			 * 3-1. 상대팀인 경우 거리 계산(10m)
			 * 3-2. 거리에 해당하면 마커 표시
			 * 3-3. 같은 팀인경우 바로 마커 표시
			 * 
			 */
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
					
					Log.i("SQLite", "uid ="+ participant[0] + 
									" / 이름 =" +participant[1]+ 
									" / 위도 ="+participant[2] + 
									" / 경도 =" + participant[3] + 
									"가 검색 되었습니다.");  	
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
			View rootView = inflater.inflate(R.layout.activity_main, container,false);
			     
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
	


