package com.parking.parkingguide.menuact;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parking.parkingguide.R;
import com.parking.parkingguide.customview.MaterialProgressDialog;
import com.parking.parkingguide.database.HappeyBean;
import com.parking.parkingguide.eventbus.MsgHappy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HappyActivity extends AppCompatActivity {
    private ListView listView;
    private SwipeRefreshLayout refreshLayout;
    private ListAdapter listAdapter;
    private OkHttpClient okHttpClient;
    private ArrayList<HappeyBean.Result> list;
    private MaterialProgressDialog materialProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_layout_happy);
        EventBus.getDefault().register(this);
        materialProgressDialog = new MaterialProgressDialog(this, R.style.CustomProgressDialog);
        materialProgressDialog.show();
        okHttpClient=new OkHttpClient();
        listView= (ListView) findViewById(R.id.lv_happy);
        refreshLayout= (SwipeRefreshLayout) findViewById(R.id.srl_happey);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //开始刷新,设置当前状态为刷新状态
//                refreshLayout.setRefreshing(true);
                getInfoFromServer();
                refreshLayout.setRefreshing(false);
                Toast.makeText(HappyActivity.this,"刷新完成",Toast.LENGTH_LONG).show();
            }
        });
        getInfoFromServer();
    }
    private void getInfoFromServer(){
        final Request request=new Request.Builder().url("http://v.juhe.cn/joke/randJoke.php?type=&key=d6dabe62aaad07235914c27faf97ee5d").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Gson gson=new Gson();
                HappeyBean happeyBean=gson.fromJson(responseData, HappeyBean.class);
                list=happeyBean.result;
                EventBus.getDefault().post(new MsgHappy(list));
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleHappyData(MsgHappy msgHappy){
        listView.setAdapter(new HappyAdapter());
        materialProgressDialog.dismiss();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        okHttpClient=null;
    }
    class HappyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(view==null){
                viewHolder=new ViewHolder();
                view=View.inflate(HappyActivity.this,R.layout.item_listview_happy,null);
                viewHolder.content= (TextView) view.findViewById(R.id.tv_happy_content);
                viewHolder.textNum=(TextView)view.findViewById(R.id.tv_happy_num);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) view.getTag();
            }
            viewHolder.content.setText(list.get(i).content);
            viewHolder.textNum.setText("笑话"+i);
            return view;
        }
        class ViewHolder {
            TextView textNum;
            TextView content;
        }
    }
}
