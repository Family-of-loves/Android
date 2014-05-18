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
	int num=0; // 상대팀 마커 찍을때 갯수
	
	public Participant( String team,Context c,GoogleMap gmap,Handler handler){
		// 생성
		this.context = c;
		this.gmap = gmap;
		this.team = team;
		this.mHandler = handler;
		
		dbHandler = new DBManagerHandler(this.context);
		
		System.out.println("Debug Participant Map : "+ this.gmap);
		System.out.println("참가 생성");
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
						if(team.equals(rows[2])){	// 나와 같은팀이면
							
							if(team.equals("1")){	// 레드팀일 경우
								
								
								LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
								gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_RED )));
														
							}
							else{	// 블루팀일경우
								
								LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
								gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_BLUE )));
							}
						}
						else{ // 나와같은팀이 아닐경우
										        	
							float[] distance = new float[2]; // float 형태의 사이즈 2의 행렬 생성
							float actual_distance; //실제 거리 값을 담을 변수
							 
							Location.distanceBetween(latitude, longitude ,  Double.parseDouble(rows[3]), Double.parseDouble(rows[4]), distance);
							//lat1와 lon1은 첫번째 사용자, lat2와 lon2는 두번째 사용자의 GPS 값.
							//distanceBetween은 Location클래스 내에서 정의된 static 함수이기 때문에 Location 클래스를 통해 아무데서나 부를 수 있다.
							//이 메소드가 호출되고 나면 distance 행렬의 첫번째 요소로 두 지점의 거리가 할당된다.
							actual_distance = distance[0]; //간단한 사용을 위해 일반 변수로 넘겨주기.
							
							
							Marker[] Marker=new Marker[5];
							//String a=""+actual_distance;
							//Log.i("실제거리",a);
							if(actual_distance<1.336547E7){	// 계산된 거리 비교
								
								gmap.setOnMarkerClickListener(this); // 지도에 리스너등록	
								
								
								if(rows[2].equals("1")){	// 같은팀이 아닌데 레드일경우
									LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
									
									 Marker[num]=gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_RED )));
									// 클릭 리스너를 쓸려면 마커객체를 등록하여야 해서 일단 상태팀마커는 등록하엿슴. 여러개찍는건 생각해봐야할듯
									 num++;
								}
								else{		// 블루일경우
										LatLng loc = new LatLng(Double.parseDouble(rows[3]), Double.parseDouble(rows[4]));
										Marker[num]=gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory. HUE_BLUE )));
										num++;
								}
							}
							
							//상대팀이면 반경 10미터 이내일때만 찍기
												
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
    	public boolean onMarkerClick(Marker marker) {	// 마커클릭 메소드
    		
    		//if (marker.equals(Marker)) 	// 상대방 마커를 찍을경우 // 우리편 마커는 객체가 없기때문에 여기로 안들어옴
           // {
    			//Log.i("마커","클릭됨");	// 로그로 확익가능
    			String[] match = dbHandler.search(marker.getSnippet());
    			
    			//Log.i("마커",marker.getTitle());	// 로그로 확익가능
    			Log.i("마커",match[1]);
    			
    			Bundle data = new Bundle();
    			data.putString("data", match[4]);
    			Message msg = Message.obtain();
    			msg.setData(data);
    			mHandler.sendMessage(msg); 
    			
    			
    			//mHandler.sendEmptyMessage(0,match[1]);  // 핸들러 메세지를 넘김으로써 미니게임 다이얼로그가 실행됨
    			//미니게임 다이얼로그 띄워야됨
    			//MiniGame game = new MiniGame();
    			//game.show(getFragmentManager(), "USER");
    			
    			//Toast.makeText(getApplicationContext(), "덤벼", Toast.LENGTH_LONG) .show();
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