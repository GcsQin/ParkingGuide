package com.parking.parkingguide.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.parking.parkingguide.bean.ParkInfo;
import com.parking.parkingguide.database.ParkDatabase;
import com.parking.parkingguide.database.ParkSQLOpenHelper;

import jxl.Sheet;
import jxl.Workbook;
import java.io.InputStream;

/**
 * Created by 37266 on 2017/4/21.
 */

public class ExcelUtils {
    private ParkSQLOpenHelper parkSQLOpenHelper=null;
    private ParkDatabase parkDatabase=null;
    private static ExcelUtils excelUtils=null;

    public ExcelUtils(Context context) {
        parkSQLOpenHelper=ParkSQLOpenHelper.getInstance(context);
        parkDatabase=ParkDatabase.getInstance(context);
    }
    public static ExcelUtils getInstance(Context context){
        if(excelUtils==null){
            excelUtils=new ExcelUtils(context.getApplicationContext());
        }
        return excelUtils;
    }
    /*
    * 将excel中的数据读取到数据库中
    * */
    public void readExcelToDB(Context context){
        SQLiteDatabase sqLiteDatabase=parkSQLOpenHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query("parkInfo",null,null,null,null,null,null);
        int count=cursor.getCount();
//        判断是否已经将excel的数据保存在数据库，如果没有保存，就将数据保存在数据库中，为了避免重复添加，
//        所以每次添加前先都要先将数据库清空。
        SharedPreferences sharedPreferences=context.getSharedPreferences("excel",Context.MODE_PRIVATE);
        Boolean config=sharedPreferences.getBoolean("readExcel",false);
        if(config&&count<=0){//如果没有保存过而且数据库parkInfo表的内容为空
            try {
                InputStream inputStream=context.getAssets().open("data.xls");
                Workbook workbook=Workbook.getWorkbook(inputStream);
                Sheet sheet=workbook.getSheet(0);
                int rows=sheet.getRows();
                ParkInfo parkInfo=null;
                for(int i=0;i<rows;i++){
                    String area=(sheet.getCell(0,i)).getContents();
                    String recordId=(sheet.getCell(1,i)).getContents();
                    String id=(sheet.getCell(2,i)).getContents();
                    String parkName=(sheet.getCell(3,i)).getContents();
                    String parkTime=(sheet.getCell(4,i)).getContents();
                    String parkCompany=(sheet.getCell(5,i)).getContents();
                    String parkNum=(sheet.getCell(6,i)).getContents();
                    String parkLevel=(sheet.getCell(9,i)).getContents();
                    parkInfo=new ParkInfo(area,recordId,id,parkName,parkTime,parkCompany,parkNum,parkLevel);
                    Log.e("ExcelUtils",parkInfo.toString());
                    parkDatabase.saveParkInfo(parkInfo);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
