package com.example.wearetherunningman;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
 
/**
 * @author  JeongMyoungHak
 */
public class DBManagerHandler {
    /**
	 * @uml.property  name="mDBManager"
	 * @uml.associationEnd  
	 */
    private DBManager mDBManager;
    private SQLiteDatabase db;
    private String tableName = "participant";
     
    public DBManagerHandler (Context context){
        this.mDBManager = new DBManager(context);
        
    }
	//닫기
	public void close() {
		db.close();
	}
	
	 //저장
	public void insert (JSONObject obj){
	    db = mDBManager.getWritableDatabase();
	    ContentValues val = new ContentValues();
		try {
		    val.put("uid", obj.getString("uid"));
			val.put("name", obj.getString("name"));
			val.put("team", obj.getString("team"));
			val.put("latitude", obj.getString("latitude"));
			val.put("longitude", obj.getString("longitude"));
			val.put("item", obj.getString("item"));
			
			db.insert(tableName, null, val);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void update(JSONObject obj){
		db = mDBManager.getWritableDatabase();
		ContentValues val = new ContentValues();
		try {
			val.put("latitude", obj.getString("latitude"));
			val.put("longitude", obj.getString("longitude"));
			
			db.update(tableName, val, "uid=?", new String[]{obj.getString("uid")});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void delete(String uid){
		db = mDBManager.getWritableDatabase();
		db.delete(tableName, "uid=?", new String[]{uid});
		Log.i("SQLite : ", uid + "가 정상적으로 삭제 되었습니다.");
	}
	
	//가저오기 
	public void read(){
		db = mDBManager.getReadableDatabase();
		String sql = "select * from " + tableName + ";";
		Cursor result = db.rawQuery(sql, null);

		result.moveToFirst();
		if(result.isAfterLast()){
			System.out.println("정보가 없습니다.");
		} else {
			while(!result.isAfterLast()){
				String rUid = result.getString(1);
	            String rName = result.getString(2);
	            String rLatitude = result.getString(4);
	            String rLongitude = result.getString(5);
	            Log.i("SQLite", "uid ="+ rUid + "rName =" +rName+ " latitude ="+rLatitude + "longitude =" + rLongitude + " Total readed");
			    result.moveToNext();
			}
		}
		result.close();
	}

	public String[] search(String uid){
		db = mDBManager.getWritableDatabase();
		String sql = "select * from " +tableName+ " where uid = \""+uid+"\";";
		Cursor result = db.rawQuery(sql, null);
		
		if(result.moveToFirst()){
			String[] result_arr = {result.getString(1), result.getString(2), result.getString(4), result.getString(5)};
			return result_arr;
        }
        result.close();
		return null;
	}
	
}