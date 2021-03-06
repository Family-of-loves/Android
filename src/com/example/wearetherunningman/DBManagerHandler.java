package com.example.wearetherunningman;

import java.util.ArrayList;

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
    private DBManager mDBManager;
    private SQLiteDatabase db;
    private String TB_NAME = "participant1";
     
    public DBManagerHandler (Context context){
        this.mDBManager = new DBManager(context);
        db = mDBManager.getWritableDatabase();
        String sql = "delete from " + TB_NAME + ";";
        db.execSQL(sql);
        System.out.println("DB Initialize");
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
			val.put("flag", obj.getString("flag"));
			
			db.insert(TB_NAME, null, val);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public ArrayList<String[]> read(){
		db = mDBManager.getReadableDatabase();
		String sql = "select * from " + TB_NAME + ";";
		Cursor cursor = db.rawQuery(sql, null);
		ArrayList<String[]> fetchArrayRows = new ArrayList<String[]>();

		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			System.out.println("User's empty.");
		} else {
			while(!cursor.isAfterLast()){
				fetchArrayRows.add(new String[]{cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7)});
	            cursor.moveToNext();
			}
		}
		cursor.close();
		return fetchArrayRows;
	}
	public void update(JSONObject obj){
		db = mDBManager.getWritableDatabase();
		ContentValues val = new ContentValues();
		try {
			val.put("latitude", obj.getString("latitude"));
			val.put("longitude", obj.getString("longitude"));
			val.put("name", obj.getString("name"));
			val.put("item", obj.getString("item"));	// 추가한부분
			val.put("flag", obj.getString("flag"));	// 추가한부분
			
			db.update(TB_NAME, val, "uid=?", new String[]{obj.getString("uid")});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void delete(String uid){
		db = mDBManager.getWritableDatabase();
		db.delete(TB_NAME, "uid=?", new String[]{uid});
	}
	public String[] search(String uid){
		db = mDBManager.getWritableDatabase();
		String sql = "select * from " +TB_NAME+ " where uid = \""+uid+"\";";
		Cursor result = db.rawQuery(sql, null);
		
		if(result.moveToFirst()){
			String[] fetchRows = {result.getString(1), result.getString(2),result.getString(3), result.getString(4), result.getString(5), result.getString(6),result.getString(7)};
			return fetchRows;
        }
        result.close();
		return null;
	}
}