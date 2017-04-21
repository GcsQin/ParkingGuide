package com.parking.parkingguide;

import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.parking.parkingguide.bean.ParkInfo;
import com.parking.parkingguide.database.ParkDatabase;
import com.parking.parkingguide.utils.ExcelUtils;

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    ActionBarDrawerToggle mToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initView();
        initToolBarAnadDrawableLayout();
        //启动ExcelAsynTask，execute的参数会传递到doInBackground的形参中
        new ExcelAsynTask().execute();
        //初始化控件的数据
    }
    private void initView(){
        drawerLayout= (DrawerLayout) findViewById(R.id.dlMain);
        toolbar=(Toolbar)findViewById(R.id.toolBar);
    }
    private void initToolBarAnadDrawableLayout(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,toolbar,R.string.open_draw,R.string.close_draw){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
    }
    /*
    * 第一个泛型定义的是doInBackground的形参参数泛型
    * 第二个泛型定义的是onProgressUpdate的形参参数泛型
    * 第三个泛型定义的是doInBackground的返回值类型和onPostExecute的形参泛型，doInBackground的返回值会
    * 传递到onPostExecute的形参中。
    * */
    class ExcelAsynTask extends AsyncTask<Void,Void,ArrayList<ParkInfo>>{
        //运行子主线程，执行耗时操作，防止主线程阻塞，出现ANR
        @Override
        protected ArrayList<ParkInfo> doInBackground(Void... voids) {
            ExcelUtils excelUtils=ExcelUtils.getInstance(MainActivity.this);
            excelUtils.readExcelToDB(MainActivity.this);
            ParkDatabase parkDatabase=ParkDatabase.getInstance(MainActivity.this);
            ArrayList<ParkInfo> parkInfos=parkDatabase.readAllParkInfo();
            return parkInfos;
        }
        //运行于主线程，更新进度
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
        //运行于主线程，耗时操作结束后执行该方法
        @Override
        protected void onPostExecute(ArrayList<ParkInfo> list) {
            for (int i=0;i<100;i++){
                ParkInfo parkInfo=list.get(i);
                Log.e("MainActivity",parkInfo.toString());
            }
            super.onPostExecute(list);
        }
    }

}
