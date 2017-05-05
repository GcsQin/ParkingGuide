package com.parking.parkingguide.menuact;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.parking.parkingguide.R;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_layout_nba);
        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("moviceBundler");
        final String city=bundle.getString("city");
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh_movie);
        listView=(ListView)findViewById(R.id.lv_movie);
        EventBus.getDefault().register(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer(city);

            }
        });
        getDataFromServer(city);
    }
    private void getDataFromServer(String city){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url("http://op.juhe.cn/onebox/movie/pmovie?" +
                "dtype=&city="+city+"&key=b950ed558218b18445c1a10ec7a0738f").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Gson gson=new Gson();
                call.cancel();
            }
        });
    }
    class ListMovieAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}
