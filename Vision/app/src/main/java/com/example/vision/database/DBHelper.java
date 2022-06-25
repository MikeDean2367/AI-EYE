package com.example.vision.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    /*
    * user_id time_stamp type eyes value
    * string  string     string string string
    * type:
    *   0-vision
    *   1-achromate
    *   2-astigmatism
    *
    * eyes:
    *   0-left
    *   1-right
    *
    * value:
    *   "4.2"
    *   "YELLOW-I"
    *   "150"
    * */
    String TAG = "MikeDean-DBHelper";

    // Create SQL
    // TABLE NAME: Vision
    private static final String CREATE_SQL_VISION = "CREATE TABLE Vision(" +
                                "user text," +
                                "time text," +
                                "type text," +
                                "eyes text," +
                                "value text)";
    private static final String CREATE_SQL_USER = "CREATE TABLE User("+
                                "username text," +
                                "password text,"+
                                "config text," +
                                "save text)";       // 0-don't auto login 1-auto login

    private String tableName;

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, String TableName) {
        super(context, name, factory, version);
        this.tableName = TableName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // when Table Vision does not exist
        Log.v("MikeDeanHelper:", tableName);
        sqLiteDatabase.execSQL(CREATE_SQL_VISION);
        sqLiteDatabase.execSQL(CREATE_SQL_USER);
//        if(tableName.contains("Vision")){
//            Log.v("MikeDeanHelperInVison:", tableName);
//            Log.v(TAG, "Create Table Vision Successfully");
//        }else if (tableName.contains("User")){
//            Log.v(TAG, "Create Table User Successfully");
//        }else{
//            Log.v(TAG, "Create Wrong!");
//        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
