package com.example.vision.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DBUserDataManager {
    private DBHelper dbHelper;
    private SQLiteDatabase db;
    private ArrayList<String> userNames = new ArrayList<>();
    private ArrayList<String> password = new ArrayList<>();
    private ArrayList<String> Config = new ArrayList<>();
    private ArrayList<String> Saved = new ArrayList<>();
    private ContentValues values = new ContentValues();
    private String TableName = "User";
    private Cursor cursor;

    public DBUserDataManager(Context context, String DataBaseName){
        dbHelper = new DBHelper(context, DataBaseName, null, 1, TableName);
        db = dbHelper.getWritableDatabase();
        readAllName();
    }

    @SuppressLint("Range")
    private void readAllName(){
        userNames.clear();
        password.clear();
        Config.clear();
        cursor = db.query(TableName, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                userNames.add(
                        cursor.getString(cursor.getColumnIndex("username"))
                );
                password.add(
                        cursor.getString(cursor.getColumnIndex("password"))
                );
                Config.add(
                        cursor.getString(cursor.getColumnIndex("config"))
                );
                Saved.add(
                        cursor.getString(cursor.getColumnIndex("save"))
                );
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    // check username is unique
    public boolean check(String newUserName){
        boolean result = true;
        for (int i=0;i<userNames.size();i++){
            if (newUserName.equals(userNames.get(i))){
                result = false;
                break;
            }
        }
        return result;
    }

    // add
    public void add(String newUserName, String newPassword){
        values.clear();
        values.put("username", newUserName);
        values.put("password", newPassword);
        values.put("config","0-0");             // useAudio-distance
        values.put("save","0");
        db.insert(TableName, null, values);
        readAllName();
    }

    public void update(String username, String Password, String config, String save){
        values.clear();
        values.put("username", username);
        values.put("password", Password);
        values.put("config",config);
        values.put("save", save);
        db.update(TableName, values, "username=?", new String[]{username});
        readAllName();
    }

    public String getPassword(String username){
        String password = null;
        for (int i=0;i<userNames.size();i++){
            if (username.equals(userNames.get(i))){
                password = this.password.get(i);
                break;
            }
        }
        return password;
    }

    public String getConfig(String username){
        String _config = null;
        for (int i=0;i<userNames.size();i++){
            if (username.equals(userNames.get(i))){
                _config = this.Config.get(i);
                break;
            }
        }
        return _config;
    }

    public String getIsSaved(String username){
        for(int i=0;i<Saved.size();i++){
            if (username.equals(userNames.get(i))){
                return Saved.get(i);
            }
        }
        return "0";
    }

    public void getAutoLoginUser(ArrayList<String> userList){
        if (userNames.size()==0) readAllName();
        for(int i=0;i<userNames.size();i++){
            Log.v("DBUserDataManager", userNames.get(i)+":"+Saved.get(i));
            if(Saved.get(i).equals("1") || Saved.get(i).equals("2")){
                userList.add(userNames.get(i));
            }
        }
    }

    public String getAutoLogin(){
        if (userNames.size()==0) readAllName();
        for(int i=0;i<userNames.size();i++){
            Log.v("DBUserDataManager", userNames.get(i)+":"+Saved.get(i));
            if(Saved.get(i).equals("2")){
                return userNames.get(i);
            }
        }
        return null;
    }

}
