package com.example.wearetherunningman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author  JeongMyoungHak
 */
public class MainActivity extends FragmentActivity implements WsCallbackInterface {
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

	/*
	 * 임시 사용변수(View와 함칠때는 다르게 사용함)
	 */
	EditText name;	//이름을 입력받음
	EditText room ; // 방이름 입력 받음
	EditText uid;
	String inputName; // 입력받은 값을 변수화
	String inputRoom;
	String inputUid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbHandler = new DBManagerHandler(getApplicationContext());
		
		gmap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.gMap)).getMap();
		
		// 임시 저장관련
		Button sendbutton=(Button)findViewById(R.id.button01);	
		Button ackbutton=(Button)findViewById(R.id.button02);
	
		Button read_db=(Button)findViewById(R.id.read_db);
		
		
		name = (EditText) findViewById(R.id.edittext01);
		room = (EditText) findViewById(R.id.edittext02);
		
		// 웹 소켓 사용!
		ws.run("http://dev.hagi4u.net:3000");
		
		// 임시 버튼 사용으로 인한 이벤트 리스너
		sendbutton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {  // 버튼 클릭시
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "사용자 생성!",  Toast.LENGTH_SHORT).show();
				inputName = name.getText().toString();  //입력한 값을 변수화 시킴
				inputRoom = room.getText().toString();
				
				player = new Player(inputName,"1","0",getApplicationContext(), gmap);
				// IS_SANDBOX;
				//player = new Player("정명학_mac","1","0",getApplicationContext(),gmap);

			}
		});

		ackbutton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				// IS_DEBUG;
				ws.emitJoin("test", player);
				emitServer();
			}
		});
		read_db.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dbHandler.read();
			
			}
			
		});
		
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
	
	/*
	 * 웹 소켓서버 메소드 
	 */
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
			try {
				String[] participant = dbHandler.search(obj.getString("uid"));
				
				if(participant == null){
					// DB에 사용자 정보를 삽입
					System.out.println("값이 존재하지 않습니다.");
					dbHandler.insert(obj);
				} else {
					// DB에 정보를 빼와서 좌표값 수정 (participant[2] = latitude / participant[3] = longitude
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
