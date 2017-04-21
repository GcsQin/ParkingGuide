package com.parking.parkingguide.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.parking.parkingguide.bean.ParkInfo;

/**
 * Created by 37266 on 2017/4/20.
 */

public class ParkDatabase {
    private SQLiteDatabase sqLiteDatabase=null;
    private static ParkDatabase parkDatabase;
    private ParkDatabase(Context context){
        ParkSQLOpenHelper parkSQLOpenHelper=new ParkSQLOpenHelper(context,"parkInfo",null,1);
        sqLiteDatabase=parkSQLOpenHelper.getReadableDatabase();
    }
    public synchronized static ParkDatabase getInstance(Context context){
        if(parkDatabase==null){
            parkDatabase=new ParkDatabase(context);
        }
        return parkDatabase;
    }
    public void saveParkInfo(ParkInfo parkInfo){
        if(parkInfo!=null){
            ContentValues contentValues=new ContentValues();
            contentValues.put("area ",parkInfo.getArea());
            contentValues.put("recordId",parkInfo.getRecordId());
            contentValues.put("id",parkInfo.getId());
            contentValues.put("parkName",parkInfo.getParkName());
            contentValues.put("parkType",parkInfo.getParkType());
            contentValues.put("parkCompany",parkInfo.getParkCompany());
            contentValues.put("parkNum",parkInfo.getParkNum());
            contentValues.put("parkLevel",parkInfo.getParkLevel());
            sqLiteDatabase.insert("parkInfo",null,contentValues);
        }
    }
}
