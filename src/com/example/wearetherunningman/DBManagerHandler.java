package com.example.wearetherunningman;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
 
public class DBManagerHandler {
    private DBManager mDBManager;
    private SQLiteDatabase db;
     
    public DBManagerHandler (Context context){
        this.mDBManager =new DBManager(context);
    }
	//�ݱ�
	public void close() {
		db.close();
	}
	 //����
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
			
			db.insert("participant", null, val);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	//�������� 
	public Cursor select(){
		db=mDBManager.getReadableDatabase();
		Cursor cursor = db.query("participant", null, null, null, null, null, null);  
		return cursor;
	 }
}
	 
	 
	/**  ������Ʈ���
	 *     public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {}
	 *      
	 * �������
	 *     public int delete(String table, String whereClause, String[] whereArgs) {}
	 */