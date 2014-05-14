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
    /**
	 * @uml.property  name="mDBManager"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private DBManager mDBManager;
    /**
	 * @uml.property  name="db"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
    private SQLiteDatabase db;
    /**
	 * @uml.property  name="tB_NAME"
	 */
    private String TB_NAME = "participant";
     
    public DBManagerHandler (Context context){
        this.mDBManager = new DBManager(context);
        db = mDBManager.getWritableDatabase();
        String sql = "delete from " + TB_NAME + ";";
        db.execSQL(sql);
        System.out.println("초기화 완료!");
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
		ArrayList<String[]> result = new ArrayList<String[]>();

		cursor.moveToFirst();
		if(cursor.isAfterLast()){
			System.out.println("정보가 없습니다.");
		} else {
			while(!cursor.isAfterLast()){
				result.add(new String[]{cursor.getString(1),cursor.getString(2),cursor.getString(4),cursor.getString(5)});
	            cursor.moveToNext();
			}
		}
		cursor.close();
		return result;
	}
	public void update(JSONObject obj){
		db = mDBManager.getWritableDatabase();
		ContentValues val = new ContentValues();
		try {
			val.put("latitude", obj.getString("latitude"));
			val.put("longitude", obj.getString("longitude"));
			val.put("name", obj.getString("name"));
			
			db.update(TB_NAME, val, "uid=?", new String[]{obj.getString("uid")});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void delete(String uid){
		db = mDBManager.getWritableDatabase();
		db.delete(TB_NAME, "uid=?", new String[]{uid});
		Log.i("SQLite : ", uid + "가 정상적으로 삭제 되었습니다.");
	}
	public String[] search(String uid){
		db = mDBManager.getWritableDatabase();
		String sql = "select * from " +TB_NAME+ " where uid = \""+uid+"\";";
		Cursor result = db.rawQuery(sql, null);
		
		if(result.moveToFirst()){
			String[] result_arr = {result.getString(1), result.getString(2), result.getString(4), result.getString(5)};
			return result_arr;
        }
        result.close();
		return null;
	}
}