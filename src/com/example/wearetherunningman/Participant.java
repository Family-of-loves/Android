package com.example.wearetherunningman;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

public class Participant extends FragmentActivity {
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
	
	public Participant(Context c,GoogleMap gmap){
		// 积己
		this.context = c;
		this.gmap = gmap;
		dbHandler = new DBManagerHandler(this.context);
		
		System.out.println("Debug Participant Map : "+ this.gmap);
		System.out.println("曼啊 积己");
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
		new asyncTaskMarker().execute();
	}
	
	public class asyncTaskMarker extends AsyncTask<String[], String, String> {

		@Override
		protected void onPreExecute() {
			System.out.println("onPreExecute");
			super.onPreExecute();
        }
		@Override
		protected String doInBackground(String[]... params) {
			System.out.println("doInBackground");
			ArrayList<String[]> resultAll = dbHandler.read();
			System.out.println("Marker : " + m);
			if(resultAll!=null){
				for(int i=0; i < resultAll.size(); i++)
					publishProgress(resultAll.get(i));
			}
			
			return null;
		}
		@Override
		protected void onProgressUpdate(String[] result){
			// TODO Auto-generated method stub
			if(result != null){
				Double lat = Double.parseDouble(result[2]);
				Double lng = Double.parseDouble(result[3]);
				LatLng loc = new LatLng(lat, lng);
				m = gmap.addMarker(new MarkerOptions().position(loc).title(result[1]).snippet(result[0]));
				System.out.println("insert Marker : " + m);
				//System.out.println(result[0] + " / " + result[1] + " / " + result[2] + " / "+ result[3]);
			} else {
				onCancelled();
			}
		}
		@Override
        protected void onPostExecute(String result) {
            System.out.println("AsyncTask Complete!");           
			super.onPostExecute(result);
        }
        @Override
        protected void onCancelled() {
        	System.out.println("AsyncTask Cancelled!");
            super.onCancelled();
        }
	}
}