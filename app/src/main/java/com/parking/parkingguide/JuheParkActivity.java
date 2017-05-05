package com.parking.parkingguide;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.parking.parkingguide.bean.JuheParkInfo;
import com.parking.parkingguide.bean.ParkInfo;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JuheParkActivity extends AppCompatActivity {
    private OkHttpClient okHttpClient=null;
    private RecyclerView recyclerView;
    private double latitude;
    private double longtitude;
    private ArrayList<JuheParkInfo> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_juhe_park);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        latitude=bundle.getDouble("latitude");//维度
        longtitude=bundle.getDouble("longtitude");//精度
        okHttpClient=new OkHttpClient();
        if(latitude!=0&&longtitude!=0){
            Log.e("JuheParkActivity",""+latitude+"------"+longtitude);
            getJuheParkInfo(latitude,longtitude);
        }else {
            Log.e("出错了","出错了");
        }
//        initView();
    }
    class JuheAsyncTask extends AsyncTask<Void,Void,ArrayList<JuheParkInfo>>{

        @Override
        protected ArrayList<JuheParkInfo> doInBackground(Void... voids) {
            ArrayList<JuheParkInfo> asynList=getJuheParkInfo(latitude,longtitude);
            return asynList;
        }
    }
    private ArrayList<JuheParkInfo> getJuheParkInfo(double latitude ,double longitude){
        Request request=new Request.Builder().url("http://japi.juhe.cn/park/nearPark.from?JD=" +
                latitude+"&WD="+longitude+"&JLCX=1000&key=bc15f48013fef0126fe2bbfcae48d6f2").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String reponseBody=response.body().string();
                Log.e("JuheParkActivity",reponseBody);
                call.cancel();
            }
        });
        return arrayList;
    }
    private void initView(){
        recyclerView= (RecyclerView) findViewById(R.id.rccv_juhepark);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
