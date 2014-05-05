package com.example.wearetherunningman;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements WsCallbackInterface {
	Player player;
	DBManager db;
	//Participant participants;
	ArrayList<Participant> participants = new ArrayList<Participant>();
	WsConn ws = new WsConn(this);

	/*
	 * 임시 사용변수(View와 함칠때는 다르게 사용함)
	 */
	EditText name;	//이름을 입력받음
	EditText room ; // 방이름 입력 받음
	EditText uid;
	String inputName; // 입력받은 값을 변수화
	String inputRoom;
	String inputUid;
	// 임시 사용변수 끝
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 임시 저장관련
		Button sendbutton=(Button)findViewById(R.id.button01);	
		Button ackbutton=(Button)findViewById(R.id.button02);

		name = (EditText) findViewById(R.id.edittext01);
		room = (EditText) findViewById(R.id.edittext02);
		
		// 웹 소켓 사용!
		db = new DBManager(this);
		ws.run("http://dev.hagi4u.net:3000");
		
		
		// 임시 버튼 사용으로 인한 이벤트 리스너
		sendbutton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {  // 버튼 클릭시
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "사용자 생성!",  Toast.LENGTH_SHORT).show();
				inputName = name.getText().toString();  //입력한 값을 변수화 시킴
				inputRoom = room.getText().toString();
				
				player = new Player(inputName,"1","0",getApplicationContext());
			}
		});

		ackbutton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				ws.emitJoin(inputRoom, player);
				emitServer();
			}
		});
		
	}
	
	public void emitServer(){
		new Thread(new Runnable() {           
			public void run() {
				while (true) {
					try {
						Location location = null;
						player.gc.onLocationChanged(location);
						ws.emitMessage(player);
						Thread.sleep(10000);                       
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }).start();
	}
	
	/*
	 * 웹 소켓서버 메소드 
	 */
	@Override
	public void on(String event, JSONObject obj) {
		Log.i("systemEvent", event);
		if(event.equals("join")){
		} else if (event.equals("message")){
			//db.selectAll();
		} else if (event.equals("leaved")){
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
	
	
	/*================ 하단은 건들면안됨 ====================*/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,false);
			
			return rootView;
		}
	}

}
