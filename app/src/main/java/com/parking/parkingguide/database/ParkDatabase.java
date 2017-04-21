package com.parking.parkingguide.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.parking.parkingguide.bean.ParkInfo;

import java.util.ArrayList;

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
    public ArrayList<ParkInfo> readAllParkInfo(){
        ArrayList<ParkInfo> parkInfos=new ArrayList<ParkInfo>();
        Cursor cursor=sqLiteDatabase.query("parkInfo",null,null,null,null,null,null);
        try {
            if(cursor.moveToFirst()){
                do{
                    String area=cursor.getString(cursor.getColumnIndex("area"));
                    String recordId=cursor.getString(cursor.getColumnIndex("recordId"));
                    String id=cursor.getString(cursor.getColumnIndex("id"));
                    String parkName=cursor.getString(cursor.getColumnIndex("parkName"));
                    String parkType=cursor.getString(cursor.getColumnIndex("parkType"));
                    String parkCompany=cursor.getString(cursor.getColumnIndex("parkCompany"));
                    String parkNum=cursor.getString(cursor.getColumnIndex("parkNum"));
                    String parkLevel=cursor.getString(cursor.getColumnIndex("parkLevel"));
                    ParkInfo parkInfo=new ParkInfo(area,recordId,id,parkName,parkType,parkCompany,parkNum,parkLevel);
                    parkInfos.add(parkInfo);
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(cursor!=null){
                cursor.close();
                cursor=null;
            }
            if(sqLiteDatabase!=null){
                sqLiteDatabase.close();
            }
        }
        return parkInfos;
    }
}
