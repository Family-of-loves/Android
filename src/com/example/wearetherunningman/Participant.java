package com.example.wearetherunningman;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.wearetherunningman.MainActivity.UserInfoDialog;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

public class Participant extends FragmentActivity implements OnMyLocationChangeListener{
	/**
	 * @uml.property  name="context"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Context context;
	/**
	 * @uml.property  name="dbHandler"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	public DBManagerHandler dbHandler;
	/**
	 * @uml.property  name="gmap"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	public GoogleMap gmap;
	public static Marker m;
	/**
	 * @uml.property  name="team"
	 */
	public String team;
	
	/**
	 * @uml.property  name="latitude"
	 */
	double latitude ;
	/**
	 * @uml.property  name="longitude"
	 */
	double longitude ;
	
	/**
	 * @uml.property  name="mHandler"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	public Handler mHandler;
	
	/**
	 * @uml.property  name="handler"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Handler handler = new Handler(Looper.getMainLooper());
	
	/**
	 * @uml.property  name="num"
	 */
	int num=0; // ����� ��Ŀ ������ ����
	
	public Participant( String team,Context c,GoogleMap gmap,Handler handler){
		// ����
		this.context = c;
		this.gmap = gmap;
		this.team = team;
		this.mHandler = handler;
		
		dbHandler = new DBManagerHandler(this.context);
		
		System.out.println("Debug Participant Map : "+ this.gmap);
		System.out.println("���� ����");
	}
	
	public void regParticipant(JSONObject obj){
		try {
			String[] participant = dbHandler.search(obj.getString("uid"));
			if(participant == null) {
				dbHandler.insert(obj);
			}else {
				dbHandler.update(obj);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void unRegParticipant(JSONObject obj){
		try {
			dbHandler.delete(obj.getString("uid"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void regMarker(){
		
		handler.post(new Runnable() {
		      public void run() {
		    	  num=0;
		    	  new asyncTaskMarker().execute();
		      }
		   });
	}
	
	
	
	public class asyncTaskMarker extends AsyncTask<String, ArrayList<String[]>, String> implements OnMarkerClickListener {
		
		@SuppressWarnings("unchecked")
		@Override
		protected String doInBackground(String... params) {
			ArrayList<String[]> fetchArrayRows = dbHandler.read();
			publishProgress(fetchArrayRows);
			return null;
		}
		@Override
		protected void onProgressUpdate(ArrayList<String[]>... fetchArrayRows){
			// TODO Auto-generated method stub
			ArrayList<String[]> fetchRows = fetchArrayRows[0];
			gmap.clear();
			if(fetchRows!=null){
				for(int i=0; i < fetchRows.size(); i++){
					String[] rows = fetchRows.get(i);
					//Log.i("team",team);
					//Log.i("team",rows[2]);
					
					if(rows != null){
						if(team.equals(rows[2])){	// ���� �������̸�
							
							if(team.equals("1")){	// �������� ���
								
								
								LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
								gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_RED )));
														
							}
							else{	// ������ϰ��
								
								LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
								gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_BLUE )));
							}
						}
						else{ // ���Ͱ������� �ƴҰ��
										        	
							float[] distance = new float[2]; // float ������ ������ 2�� ��� ����
							float actual_distance; //���� �Ÿ� ���� ���� ����
							 
							Location.distanceBetween(latitude, longitude ,  Double.parseDouble(rows[3]), Double.parseDouble(rows[4]), distance);
							//lat1�� lon1�� ù��° �����, lat2�� lon2�� �ι�° ������� GPS ��.
							//distanceBetween�� LocationŬ���� ������ ���ǵ� static �Լ��̱� ������ Location Ŭ������ ���� �ƹ������� �θ� �� �ִ�.
							//�� �޼ҵ尡 ȣ��ǰ� ���� distance ����� ù��° ��ҷ� �� ������ �Ÿ��� �Ҵ�ȴ�.
							actual_distance = distance[0]; //������ ����� ���� �Ϲ� ������ �Ѱ��ֱ�.
							
							
							Marker[] Marker=new Marker[5];
							//String a=""+actual_distance;
							//Log.i("�����Ÿ�",a);
							if(actual_distance<1.336547E7){	// ���� �Ÿ� ��
								
								gmap.setOnMarkerClickListener(this); // ������ �����ʵ��	
								
								
								if(rows[2].equals("1")){	// �������� �ƴѵ� �����ϰ��
									LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
									
									 Marker[num]=gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_RED )));
									// Ŭ�� �����ʸ� ������ ��Ŀ��ü�� ����Ͽ��� �ؼ� �ϴ� ��������Ŀ�� ����Ͽ���. ��������°� �����غ����ҵ�
									 num++;
								}
								else{		// ����ϰ��
										LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
										Marker[num]=gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_BLUE )));
										num++;
								}
							}
							
							//������̸� �ݰ� 10���� �̳��϶��� ���
												
						}
					} else {
						onCancelled();
					}
				}
			}
		}
        @Override
        protected void onCancelled() {
        	System.out.println("AsyncTask Cancelled!");
            super.onCancelled();
        }
        @Override
    	public boolean onMarkerClick(Marker marker) {	// ��ĿŬ�� �޼ҵ�
    		
    		//if (marker.equals(Marker)) 	// ���� ��Ŀ�� ������� // �츮�� ��Ŀ�� ��ü�� ���⶧���� ����� �ȵ���
           // {
    			//Log.i("��Ŀ","Ŭ����");	// �α׷� Ȯ�Ͱ���
    			String[] match = dbHandler.search(marker.getSnippet());
    			
    			//Log.i("��Ŀ",marker.getTitle());	// �α׷� Ȯ�Ͱ���
    			Log.i("��Ŀ",match[1]);
    			
    			Bundle data = new Bundle();
    			data.putString("data", match[4]);
    			Message msg = Message.obtain();
    			msg.setData(data);
    			mHandler.sendMessage(msg); 
    			
    			
    			//mHandler.sendEmptyMessage(0,match[1]);  // �ڵ鷯 �޼����� �ѱ����ν� �̴ϰ��� ���̾�αװ� �����
    			//�̴ϰ��� ���̾�α� ����ߵ�
    			//MiniGame game = new MiniGame();
    			//game.show(getFragmentManager(), "USER");
    			
    			//Toast.makeText(getApplicationContext(), "����", Toast.LENGTH_LONG) .show();
           // }
    		    		
    		return false;
    	}
	}

	@Override
	public void onMyLocationChange(Location location) {
		if (location != null) {
        	this.latitude = location.getLatitude();
        	this.longitude = location.getLongitude();
		}
		// TODO Auto-generated method stub
		
	}

	
	
}