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

public class MainActivity extends FragmentActivity implements WsCallbackInterface {
	Player player;
	GoogleMap gmap;
	DBManagerHandler dbHandler;
	WsConn ws = new WsConn(this);

	/*
	 * �ӽ� ��뺯��(View�� ��ĥ���� �ٸ��� �����)
	 */
	EditText name;	//�̸��� �Է¹���
	EditText room ; // ���̸� �Է� ����
	EditText uid;
	String inputName; // �Է¹��� ���� ����ȭ
	String inputRoom;
	String inputUid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbHandler = new DBManagerHandler(getApplicationContext());
		gmap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.gMap)).getMap();
		
		// �ӽ� �������
		Button sendbutton=(Button)findViewById(R.id.button01);	
		Button ackbutton=(Button)findViewById(R.id.button02);
		name = (EditText) findViewById(R.id.edittext01);
		room = (EditText) findViewById(R.id.edittext02);
		
		// �� ���� ���!
		ws.run("http://dev.hagi4u.net:3000");
		
		// �ӽ� ��ư ������� ���� �̺�Ʈ ������
		sendbutton.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {  // ��ư Ŭ����
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "����� ����!",  Toast.LENGTH_SHORT).show();
				inputName = name.getText().toString();  //�Է��� ���� ����ȭ ��Ŵ
				inputRoom = room.getText().toString();
				
				//player = new Player(inputName,"1","0",getApplicationContext());
				// IS_SANDBOX;
				player = new Player("������_mac","1","0",getApplicationContext(),gmap);
				
			}
		});

		ackbutton.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				// IS_DEBUG;
				ws.emitJoin("test", player);
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
	 * �� ���ϼ��� �޼ҵ� 
	 */
	@Override
	public void on(String event, JSONObject obj) {
		Log.i("systemEvent", event);
		System.out.println("Recv MSG : " + obj);
		if(event.equals("joined")){
			/* 
			 * ����� ��ü ����
			 * 1. ��ü ���� (DB�� ����?������?)
			 * 2. gmap �� ��Ŀ ���
			 * 3. �� ���� �� show/visible ����
			 */
			dbHandler.insert(obj);
			Toast.makeText(getApplicationContext(), "������ ������", 1).show();
		} else if (event.equals("message")){
			/*
			 * ����� ���� ������
			 * 1. ��ü or DB �˻�
			 * 2. gmap ��Ŀ ���� �� �ٽ� ���
			 * 3. �� ����
			 * 3-1. ������� ��� �Ÿ� ���(10m)
			 * 3-2. �Ÿ��� �ش��ϸ� ��Ŀ ǥ��
			 * 3-3. ���� ���ΰ�� �ٷ� ��Ŀ ǥ��
			 * 
			 */
		} else if (event.equals("leaved")){
		} else {
			// ����ó��
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
	
	
	/*================ �ϴ��� �ǵ��ȵ� ====================*/
	
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
