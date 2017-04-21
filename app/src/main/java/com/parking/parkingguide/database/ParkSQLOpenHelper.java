package com.parking.parkingguide.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 37266 on 2017/4/20.
 */

public class ParkSQLOpenHelper extends SQLiteOpenHelper {
    private static ParkSQLOpenHelper parkSQLOpenHelper=null;

    public ParkSQLOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    public static ParkSQLOpenHelper getInstance(Context context){
            if(parkSQLOpenHelper==null){
                parkSQLOpenHelper=new ParkSQLOpenHelper(context,"park.db",null,1);
            }
        return parkSQLOpenHelper;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table parkInfo(_id integer primary key autoincrement," +
                "area varchar(50)," +
                "recordId varchar(50)," +
                "id varchar(10)," +
                "parkName varchar(50)," +
                "parkType varchar(50)," +
                "parkCompany varchar(50)," +
                "parkNum varchar(50)," +
                "parkLevel varchar(50)" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
