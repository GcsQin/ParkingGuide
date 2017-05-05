package com.parking.parkingguide.menuact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parking.parkingguide.R;
import com.parking.parkingguide.bean.JuheFootball;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FootBallActivity extends AppCompatActivity {
    OkHttpClient okHttpClient;
    EditText etSaishi,etQiudui;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        okHttpClient=new OkHttpClient();
        setContentView(R.layout.inner_layout_football);
        etSaishi=(EditText)findViewById(R.id.foot_et_zuqiuliansai);
        etQiudui=(EditText)findViewById(R.id.foot_et_qiuduisaishi);
    }
    public void searchZuqiuLiansai(View view){
        String league=etSaishi.getText().toString();
        if(league.isEmpty()){
            Toast.makeText(FootBallActivity.this,"联赛信息不能为空",Toast.LENGTH_SHORT).show();
        }else {
            final Request request=new Request.Builder().
                    url("http://op.juhe.cn/onebox/football/league?key=99c4242fc243ea2c87d08b04002c94ab" +
                            "&league="+league).
                    build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Toast.makeText(FootBallActivity.this,"查询失败",Toast.LENGTH_SHORT).show();
                    call.cancel();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String reponseData=response.body().string();
                    gson = new Gson();
                    JuheFootball juheFootball=gson.fromJson(reponseData,JuheFootball.class);
                    int error_code=juheFootball.error_code;
                    if(error_code==0){
                        String result=juheFootball.reason;
                        String key=juheFootball.result.key;
                        String saicheng1=juheFootball.result.tabs.saicheng1;
                        String saicheng2=juheFootball.result.tabs.saicheng2;
                        Log.e("FootBallActivity",result+"--"+key+"--"+saicheng1+"--"+saicheng2);
                        Intent intent=new Intent(FootBallActivity.this,FootBallDetialsActivity.class);
                        Bundle bundle=new Bundle();
                        bundle.putSerializable("football",juheFootball);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    call.cancel();
                }
            });
        }
    }
    public void searchQiuduisaishi(View view){
        Request request=new Request.Builder().url("").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                call.cancel();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(okHttpClient!=null){
            okHttpClient=null;
        }
    }
}
