package com.example.vision.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DBVisionDataManager {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private String userName;
    private ContentValues values = new ContentValues();
    private String TableName = "Vision";
    private Cursor cursor;


    public DBVisionDataManager(Context context, String DataBaseName, String userName){
        Log.v("MikeDean", TableName+" "+DataBaseName);
        this.userName = userName;
        dbHelper = new DBHelper(context, DataBaseName, null, 1, TableName);
        db = dbHelper.getWritableDatabase();
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    // add data
    // user time_stamp type eyes value
    public void insert(String TimeStamp, String type, String eyes, String value){
        values.clear();
        values.put("user", userName);
        values.put("time", TimeStamp);
        values.put("type", type);
        values.put("eyes", eyes);
        values.put("value", value);
        db.insert(TableName, null, values);
    }

    // read data
    // given type and eyes , it will return timestamp and value
    @SuppressLint("Range")
    public void read(String type, String eyes, ArrayList<Long> time, ArrayList<String> value){
        String _name, _type, _eyes;

        cursor = db.query(TableName, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                _name = cursor.getString(cursor.getColumnIndex("user"));
                _type = cursor.getString(cursor.getColumnIndex("type"));
                _eyes = cursor.getString(cursor.getColumnIndex("eyes"));
                if (_name.equals(userName) && _type.equals(type) && _eyes.equals(eyes)){
                    time.add(Long.parseLong(cursor.getString(cursor.getColumnIndex("time"))));
                    value.add(cursor.getString(cursor.getColumnIndex("value")));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    // read all time to statistic
    @SuppressLint("Range")
    public void readAllTime(ArrayList<Long> time){
        cursor = db.query(TableName, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                if(cursor.getString(cursor.getColumnIndex("user")).equals(userName))
                    time.add(Long.parseLong(cursor.getString(cursor.getColumnIndex("time"))));
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    @SuppressLint("Range")
    public String readAchromate(){
        String _type;
        String achromate = null;
        cursor = db.query(TableName, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                _type = cursor.getString(cursor.getColumnIndex("type"));
                if(_type.equals("1")){
                    achromate = cursor.getString(cursor.getColumnIndex("value"));
                }
            }while (cursor.moveToNext());
        }
        return achromate;
    }

    // delete data when the user does not exist
    private void delete(){

    }
}
