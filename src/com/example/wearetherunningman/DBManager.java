package com.example.wearetherunningman;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
 
public class DBManager extends SQLiteOpenHelper {
    private final static String TB_NAME = "participant1";
    public static final String DB_NAME = "participant1.db";
    public static final int DB_VERSION = 1;
    
    String quary ;
     
    //constructor
    public DBManager(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
         
        quary = "CREATE TABLE "+ TB_NAME  +"(_id INTEGER PRIMARY KEY AUTOINCREMENT "
                + "," + "uid TEXT "
                + "," + "name TEXT"
                + "," + "team TEXT"
                + "," + "latitude TEXT"
                + "," + "longitude TEXT "
                + "," + "item TEXT"
                + "," + "flag TEXT"
                + ");";
    }  
 
    //테이블을 생성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(quary);
    }
    //업그레이드 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	String sql_droptable = "DROP TABLE IF EXISTS " + TB_NAME;  
        db.execSQL(sql_droptable);
    }

}