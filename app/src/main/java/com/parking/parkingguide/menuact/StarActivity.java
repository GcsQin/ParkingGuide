package com.parking.parkingguide.menuact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.parking.parkingguide.R;
import com.parking.parkingguide.bean.BusBean;
import com.parking.parkingguide.bean.StarBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class StarActivity extends AppCompatActivity {
    TextView tvTime,tvAll,tvColor,tvHealth,tvLove,tvMoney,tvNumber,tvQfriend,tvSummary;
    Spinner spinner;
    String xingzhuo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_layout_star);
        EventBus.getDefault().register(this);
        tvTime=(TextView)findViewById(R.id.tv_starTime);
        tvAll=(TextView)findViewById(R.id.tv_starall);
        tvColor=(TextView)findViewById(R.id.tv_color);
        tvHealth=(TextView)findViewById(R.id.tv_starJiankang);
        tvLove=(TextView)findViewById(R.id.tv_starLove);
        tvMoney=(TextView)findViewById(R.id.tv_starMoney);
        tvNumber=(TextView)findViewById(R.id.tv_starXingyunNum);
        tvQfriend=(TextView)findViewById(R.id.tv_starXingzuo);
        tvSummary=(TextView)findViewById(R.id.tv_summarize);
        spinner=(Spinner)findViewById(R.id.spinner_star);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                xingzhuo=adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getDataFromServer("水瓶座");
    }
    public void searchXingzuo(View view){
        if(xingzhuo.isEmpty()){
            xingzhuo="水瓶座";
        }
        getDataFromServer(xingzhuo);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getDataFromServer(String xing){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url("http://web.juhe.cn:8080/constellation/getAll?" +
                "consName="+xing+"&type=today&key=5498f3d5e6c29d66c68b9fea80e8ae60").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Gson gson=new Gson();
                StarBean starBean=gson.fromJson(responseData,StarBean.class);
                EventBus.getDefault().post(starBean);
                call.cancel();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleStar(StarBean starBean){
//        tvTime,tvAll,tvColor,tvHealth,tvLove,tvMoney,tvNumber,tvQfriend,tvSummary;
        tvTime.setText(starBean.datetime);
        tvAll.setText(starBean.all);
        tvColor.setText(starBean.color);
        tvHealth.setText(starBean.health);
        tvLove.setText(starBean.love);
        tvMoney.setText(starBean.money);
        tvNumber.setText(starBean.number);
        tvQfriend.setText(starBean.QFriend);
        tvSummary.setText(starBean.summary);
    }
}
