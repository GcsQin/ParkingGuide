package com.parking.parkingguide.menuact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.parking.parkingguide.R;
import com.parking.parkingguide.bean.HistoryBean;
import com.parking.parkingguide.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {
    String month;
    String day;
    ListView listView;
    ArrayList<HistoryBean.Result> results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_layout_history);
        EventBus.getDefault().register(this);
        Calendar calendar=Calendar.getInstance();
        listView=(ListView)findViewById(R.id.lv_history);
        month=calendar.get(Calendar.MONTH)+"";
        day=calendar.get(Calendar.DAY_OF_MONTH)+"";
        getDataFromSerber();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private void getDataFromSerber(){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url("http://api.juheapi.com/japi/toh?v=1.0&month="+month
        +"&day="+day+"&key=13066f9857a04076246fe18bf648c4bf").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtils.showToastShort(HistoryActivity.this,"数据获取失败");
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Gson gson=new Gson();
                HistoryBean historyBean=gson.fromJson(responseData,HistoryBean.class);
                EventBus.getDefault().post(historyBean);
                call.cancel();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleHistory(HistoryBean historyBean){
        results=historyBean.result;
        listView.setAdapter(new ListHisAdapter());
    }
    class ListHisAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return results.size();
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
            ViewHolder holder=null;
            if(view==null){
                holder=new ViewHolder();
                view=View.inflate(HistoryActivity.this,R.layout.item_listview,null);
                holder.tvData= (TextView) view.findViewById(R.id.tv_his_data);
                holder.tvLunar= (TextView) view.findViewById(R.id.tv_his_lunar);
                holder.tvTitle= (TextView) view.findViewById(R.id.tv_his_title);
                holder.tvDes= (TextView) view.findViewById(R.id.tv_his_des);
                view.setTag(holder);
            }else {
                holder=(ViewHolder) view.getTag();
            }
            HistoryBean.Result result=results.get(i);
            holder.tvData.setText(""+result.year+"年"+result.month+"月"+result.day+"日");
            holder.tvLunar.setText(""+result.lunar);
            holder.tvTitle.setText(""+result.title);
            holder.tvDes.setText(""+result.des);
            return view;
        }
        class ViewHolder{
            TextView tvData,tvLunar,tvTitle,tvDes;
        }
    }
}
