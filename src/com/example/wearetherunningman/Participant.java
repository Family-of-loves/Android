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
	public static Marker m = null;
	
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
	
	public class asyncTaskMarker extends AsyncTask<String, ArrayList<String[]>, String> {
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
					if(rows != null){
						LatLng loc = new LatLng(Double.parseDouble(rows[2]), Double.parseDouble(rows[3]));
						gmap.addMarker(new MarkerOptions().position(loc).title(rows[1]).snippet(rows[0]));
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
	}
}