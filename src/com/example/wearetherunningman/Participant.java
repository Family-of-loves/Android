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

public class Participant extends FragmentActivity  {
	
	Context context;
	public DBManagerHandler dbHandler;
	public GoogleMap gmap;
	public static Marker m;
	public String team;
		
	public Handler mHandler;
	
	private Handler handler = new Handler(Looper.getMainLooper());
	Player player;
	int num=0; // ����� ��Ŀ ������ ����
	
	public Participant( String team,Context c,GoogleMap gmap,Handler handler, Player player){
		// ����
		this.context = c;
		this.gmap = gmap;
		this.team = team;
		this.mHandler = handler;
		this.player=player;
		
		dbHandler = new DBManagerHandler(this.context);
		
		System.out.println("Debug Participant Map : "+ this.gmap);
		System.out.println("���� ����");
	}
	
	public ArrayList<String[]> read(){
		ArrayList<String[]> pa= dbHandler.read();
		
		return pa;
	}
	
	
	public String[] search(String uid){
		
		String[] match = dbHandler.search(uid);
		
		return match;
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
		double latitude ;
		double longitude ;
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
					
					gmap.setOnMarkerClickListener(this); // ������ �����ʵ��	
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
							
							//LatLng myloc=player.
							
							Location.distanceBetween(player.latitude/ 1E6, player.longitude/ 1E6 ,  Double.parseDouble(rows[3])/ 1E6, Double.parseDouble(rows[4])/ 1E6, distance);
							
							//lat1�� lon1�� ù��° �����, lat2�� lon2�� �ι�° ������� GPS ��.
							//distanceBetween�� LocationŬ���� ������ ���ǵ� static �Լ��̱� ������ Location Ŭ������ ���� �ƹ������� �θ� �� �ִ�.
							//�� �޼ҵ尡 ȣ��ǰ� ���� distance ����� ù��° ��ҷ� �� ������ �Ÿ��� �Ҵ�ȴ�.
							
							actual_distance = distance[0] * 0.000621371192f; //������ ����� ���� �Ϲ� ������ �Ѱ��ֱ�.
							//							
							String aa=""+actual_distance;
							Log.i("�����Ÿ�",aa);
							if(actual_distance<=30.0147793E-9 && actual_distance>=1.80482E-9){	// ���� �Ÿ� ��
																
								if(rows[2].equals("1")){	// �������� �ƴѵ� �����ϰ��
									LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
									
									gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_RED )));
																		 
								}
								else{		// ����ϰ��
										LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
										gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_BLUE )));
										
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
    		
    		
    			String[] match = dbHandler.search(marker.getSnippet());
    			    			
    			if(!team.equals(match[2])){	// ��Ŀ�� �츮���� �ƴҶ��� ���̾�α׸� ���ٴ�
    				Bundle data = new Bundle();
    				data.putString("data", match[0]);
    				Message msg = Message.obtain();
    				msg.setData(data);
    				mHandler.sendMessage(msg); 
    			}
    			    			
    		    		
    		return false;
    	}
	}

	

	
	
}