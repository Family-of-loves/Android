package com.example.wearetherunningman;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.widget.Toast;

public class DBManager {
	// DB���� ��� ����
    private static final String dbName = "participants.db";
    private static final String tableName = "participants";
    public static final int dbVersion = 2;
 
    // DB���� ��ü ����
    private OpenHelper opener; // DB opener
    private SQLiteDatabase db; // DB controller
 
    // �ΰ����� ��ü��
    private Context context;
 
    // ������
    public DBManager(Context context) {
        this.context = context;
        this.opener = new OpenHelper(context, dbName, null, dbVersion);
        db = opener.getWritableDatabase();

        Toast.makeText(context, "DB is created", 0).show();
    }
 
    // Opener of DB and Table
    private class OpenHelper extends SQLiteOpenHelper {
 
        public OpenHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, null, version);
            // TODO Auto-generated constructor stub
        }
 
        // ������ DB�� ���� ��쿡 �ѹ��� ȣ���
        @Override
        public void onCreate(SQLiteDatabase arg0) {
            String dropSql = "drop table if exists " + tableName;
            db.execSQL(dropSql);
 
            String createSql = "create table " + tableName + " ("
                    + "id integer primary key autoincrement, " 
            		+ "SSID text, "
                    + "capabilities integer, " 
            		+ "passwd text"
            		+ ""
            		+ ")";
            arg0.execSQL(createSql);
            Toast.makeText(context, "DB is opened", 0).show();
        }
 
        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
        	 Log.i("xxx", "onUpgrade >>>>>>>>>>>>>>>.....");
        	   
        	  String sql = "drop table if exists person";
        	        db.execSQL(sql);
        	         
        	        onCreate(db);
        	        Toast.makeText(context, "DB is Updated", 0).show();
        }
    }
 /*
   // ������ �߰�
    public void insertData(APinfo info) {
        String sql = "insert into " + tableName + " values(NULL, '"
                + info.getSSID() + "', " + info.getCapabilities() + ", '"
                + info.getPasswd() + "');";
        db.execSQL(sql);
    }
  
    // ������ ����
    public void updateData(APinfo info, int index) {
        String sql = "update " + tableName + " set SSID = '" + info.getSSID()
                + "', capabilities = " + info.getCapabilities()
                + ", passwd = '" + info.getPasswd() + "' where id = " + index
                + ";";
        db.execSQL(sql);
    }
 
    // ������ ����
    public void removeData(int index) {
        String sql = "delete from " + tableName + " where id = " + index + ";";
        db.execSQL(sql);
    }
 
    // ������ �˻�
    public APinfo selectData(int index) {
        String sql = "select * from " + tableName + " where id = " + index
                + ";";
        Cursor result = db.rawQuery(sql, null);
 
        // result(Cursor ��ü)�� ��� ������ false ����
        if (result.moveToFirst()) {
            APinfo info = new APinfo(result.getInt(0), result.getString(1),
                    result.getInt(2), result.getString(3));
            result.close();
            return info;
        }
        result.close();
        return null;
    }
 
    // ������ ��ü �˻�
    public ArrayList<apinfo> selectAll() {
        String sql = "select * from " + tableName + ";";
        Cursor results = db.rawQuery(sql, null);
 
        results.moveToFirst();
        ArrayList<apinfo> infos = new ArrayList<apinfo>();
 
        while (!results.isAfterLast()) {
            APinfo info = new APinfo(results.getInt(0), results.getString(1),
                    results.getInt(2), results.getString(3));
            infos.add(info);
            results.moveToNext();
        }
        results.close();
        return infos;
    }*/
}
