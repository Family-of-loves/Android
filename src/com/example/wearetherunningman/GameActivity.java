package com.example.wearetherunningman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.BitmapDrawable;

public class GameActivity extends ActionBarActivity implements WsCallbackInterface {
	
	/*
	 * Creator Objects 
	 */
	Player player;
	Participant participant;
	GoogleMap gmap;
	WsConn ws = new WsConn(this);
	
	/*
	 * Using variables
	 */
    String room;
    String name;
    String team;
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
	    
	    TextView nameview= (TextView)findViewById(R.id.textView1) ;
	    nameview.setText(name+"님");
	    player = new Player(name ,team,item ,getApplicationContext(), gmap);
	    //participant = new Participant(getApplicationContext(), gmap);
	    participant = new Participant(team ,getApplicationContext(), gmap,handler);
	    
	    
	    
	    ws.emitJoin(room, player);
		emitServer();
	}
	
	public void emitServer(){
		/*new Thread(new Runnable() {           
			public void run() {
				while (true) {
					try {
						
						Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }
                }
            }
        }).start();*/
		new Thread(new Runnable() {           
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000);
						ws.emitMessage(player);
						participant.regMarker();
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
			participant.unRegParticipant(obj);
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
	}//
	// 미니 게임 다이얼로그를 띄우기 위한 랜들러 
	/**
	 */
	public Handler handler = new Handler()	{
		public void handleMessage( Message msg )		{
			AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
			//switch ( msg.what )	{
			//case	 0	:
				builder.setTitle("미니게임");
				//LayoutInflater mLayoutInflater = GameActivity.this.getLayoutInflater();
				//View dialogView = mLayoutInflater.inflate(R.layout.dialog, null);
				builder.setMessage("진행 하시겟습니까?");
				//builder.setView(dialogView);
				builder.setCancelable(true);        // 뒤로 버튼 클릭시 취소 가능 설정
				final String matchitem = msg.getData().getString("data");
				
				builder.setPositiveButton("예", new DialogInterface.OnClickListener() {			
					public void onClick(DialogInterface dialog, int whichButton) {
						//System.exit(0);
						dialog.cancel();
						AlertDialog.Builder builder1 = new AlertDialog.Builder(GameActivity.this);
						builder1.setTitle("게임창");
						LayoutInflater mLayoutInflater = GameActivity.this.getLayoutInflater();
						View dialogView = mLayoutInflater.inflate(R.layout.contest, null);
						ImageView iv1= (ImageView)dialogView.findViewById(R.id.imageView1);	// 내무기 이미지뷰에 표시
						BitmapDrawable dr1 = null;
						if(item.equals("0")){
							dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_scissor);
						}
						else if(item.equals("1")){
							dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_rock);
						}
						else{
							dr1 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_paper);
						}
						iv1.setImageDrawable(dr1);
						
						ImageView iv2= (ImageView)dialogView.findViewById(R.id.imageView2);	// 내무기 이미지뷰에 표시
						BitmapDrawable dr2 = null;
						if(matchitem.equals("0")){
							dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_scissor);
						}
						else if(matchitem.equals("1")){
							dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_rock);
						}
						else{
							dr2 = (BitmapDrawable)getResources().getDrawable(R.drawable.btn_paper);
						}
						iv2.setImageDrawable(dr2);
						
						builder1.setView(dialogView);
						builder1.setCancelable(true);  
						
						builder1.setPositiveButton("결과확인", new DialogInterface.OnClickListener() {			
							public void onClick(DialogInterface dialog, int whichButton) {
						
								Log.i("상대편무기",matchitem);
								MiniGame mg = new MiniGame();
								String re=mg.compare(item, matchitem);
								Toast.makeText(getApplicationContext(), re, Toast.LENGTH_LONG) .show();
								
							}
						});
						
						builder1.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.cancel();
							}
						});

						builder1.show();
						
						
					}
				});

				builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});

				builder.show();
				
				//break;
						
			//}
			
			super.handleMessage( msg );
		}
	};
	
	
}