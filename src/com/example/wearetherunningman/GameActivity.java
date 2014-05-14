package com.example.wearetherunningman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class GameActivity extends ActionBarActivity implements WsCallbackInterface {
	
	/*
	 * Creator Objects 
	 */
	/**
	 * @uml.property  name="player"
	 * @uml.associationEnd  
	 */
	Player player;
	/**
	 * @uml.property  name="participant"
	 * @uml.associationEnd  
	 */
	Participant participant;
	/**
	 * @uml.property  name="gmap"
	 * @uml.associationEnd  
	 */
	GoogleMap gmap;
	/**
	 * @uml.property  name="ws"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	WsConn ws = new WsConn(this);
	
	/*
	 * Using variables
	 */
    /**
	 * @uml.property  name="room"
	 */
    String room;
    /**
	 * @uml.property  name="name"
	 */
    String name;
    /**
	 * @uml.property  name="team"
	 */
    String team;
    /**
	 * @uml.property  name="item"
	 */
    String item;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_game);

		gmap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.gMap)).getMap();
		
		ws.run("http://dev.hagi4u.net:3000");
		
		Intent intent = getIntent(); // 값을 받아온다.
	    room = intent.getExtras().getString("param1"); 
	    name = intent.getExtras().getString("param2"); 
	    team = intent.getExtras().getString("param3"); 
	    item = intent.getExtras().getString("param4");
	    
	    player = new Player(name ,team,item ,getApplicationContext(), gmap);
	    participant = new Participant(getApplicationContext(), gmap);
	    		
	    ws.emitJoin(room, player);
		emitServer();
	}
	
	public void emitServer(){
		new Thread(new Runnable() {           
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000);
						participant.regMarker();
						ws.emitMessage(player);
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }).start();
		
	}

	/*
	 * WebSocket Connection.
	 */
	@Override
	public void on(String event, JSONObject obj) {
		if (event.equals("message")){
			participant.regParticipant(obj);
		} else if (event.equals("leaved")){
			//participant.unRegParticipant(obj);
		} else {
			// 에러처리
		}
	}
	@Override
	public void callback(JSONArray data) throws JSONException {}
	@Override
	public void onMessage(String message) {}
	@Override
	public void onMessage(JSONObject json) {}
	@Override
	public void onConnect() {}
	@Override
	public void onDisconnect() {}
	@Override
	public void onConnectFailure() {}
	
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
}