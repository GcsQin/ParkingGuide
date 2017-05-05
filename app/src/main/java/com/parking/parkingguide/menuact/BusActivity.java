package com.parking.parkingguide.menuact;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineQuery;
import com.amap.api.services.busline.BusLineResult;
import com.amap.api.services.busline.BusLineSearch;
import com.google.gson.Gson;
import com.parking.parkingguide.R;
import com.parking.parkingguide.bean.BusBean;
import com.parking.parkingguide.utils.ToastUtils;

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

public class BusActivity extends AppCompatActivity  {
    EditText etName,etLine;
    TextView tvCompany,tvsTime,tveTime,tvBusNum,tvDistance,tvSLocation,tvELocation;
    ListView lvTo,lvFrom;
    RelativeLayout relativeLayout;
    OkHttpClient okHttpClient;
    private ArrayList<BusBean.Result> results;
    private ArrayList<BusBean.Stationdes> stationdesTo;
    private ArrayList<BusBean.Stationdes> stationdesFrom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_layout_bus);
        EventBus.getDefault().register(BusActivity.this);
        etName=(EditText)findViewById(R.id.et_bus_name);
        etLine= (EditText) findViewById(R.id.et_bus_line);
        tvCompany= (TextView) findViewById(R.id.tv_company);
        tvsTime= (TextView) findViewById(R.id.tv_sTime);
        tveTime= (TextView) findViewById(R.id.tv_eTime);
        tvBusNum= (TextView) findViewById(R.id.tv_bus_num);
        tvDistance=(TextView)findViewById(R.id.f_tv_distance);
        tvSLocation= (TextView) findViewById(R.id.tv_sLocation);
        tvELocation= (TextView) findViewById(R.id.tv_eLocation);
        lvTo=(ListView)findViewById(R.id.lv_bus01);
        lvFrom=(ListView)findViewById(R.id.lv_bus02);
        relativeLayout= (RelativeLayout) findViewById(R.id.rl_businfo);
        okHttpClient=new OkHttpClient();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        okHttpClient=null;
        EventBus.getDefault().unregister(BusActivity.this);
    }

    public void searchBusLine(View view){
        String name=etName.getText().toString();
        String line=etLine.getText().toString();
        if(name.isEmpty()|line.isEmpty()){
            ToastUtils.showToastShort(BusActivity.this,"查询信息不能为空");
        }else {
            final Request request=new Request.Builder().url("http://op.juhe.cn/189/bus/busline?" +
                    "key=5447881c2a6e866120b849ae0241fc9f" +
                    "&city=" +name+
                    "&bus="+line).build();
            okHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    call.cancel();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String reponseData=response.body().string();
                    Log.e("BusActivity",response+"==========");
                    if(reponseData!=null){
                        Gson gson=new Gson();
                        BusBean busBean=gson.fromJson(reponseData,BusBean.class);
                        if(busBean.error_code==0){
                            EventBus.getDefault().post(busBean);
                        }
                    }
                    call.cancel();
                }
            });
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleBusMsg(BusBean busBean){
        Log.e("BusActivity",busBean.reason);
        results=busBean.result;
        stationdesTo=busBean.result.get(0).stationdes;
        stationdesFrom=busBean.result.get(1).stationdes;
        tvCompany.setText(results.get(0).company);
        Log.e("BusActivity",results.get(0).company+results.get(0).front_name+results.get(0).terminal_name);
        tvSLocation.setText(results.get(0).front_name+"-");
        tvELocation.setText(results.get(0).terminal_name);
        tvsTime.setText(results.get(0).start_time);
        tveTime.setText(results.get(0).end_time);
        tvDistance.setText(results.get(0).length);
        tvBusNum.setText(results.get(0).name);
        relativeLayout.setVisibility(View.VISIBLE);
        lvTo.setAdapter(new BusToListAdapter());
        lvFrom.setAdapter(new BusFromListAdapter());
    }

    class BusToListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return stationdesTo.size();
        }

        @Override
        public BusBean.Stationdes getItem(int i) {
            return stationdesTo.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ToViewHolder viewHolder;
            if(view==null){
                viewHolder=new ToViewHolder();
                view=View.inflate(BusActivity.this,R.layout.item_bus_listview,null);
                viewHolder.tvBusName= (TextView) view.findViewById(R.id.tv_busitem_name);
                viewHolder.tvBusNum=(TextView)view.findViewById(R.id.tv_busitem_num);
                view.setTag(viewHolder);
            }else {
                viewHolder= (ToViewHolder) view.getTag();
            }
            viewHolder.tvBusName.setText(getItem(i).name);
            viewHolder.tvBusNum.setText(getItem(i).stationNum);
            return view;
        }
        class ToViewHolder{
            TextView tvBusName,tvBusNum;
        }
    }
    class BusFromListAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return stationdesFrom.size();
        }

        @Override
        public BusBean.Stationdes getItem(int i) {
            return stationdesFrom.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            FromViewHolder viewHolder;
            if(view==null){
                viewHolder=new FromViewHolder();
                view=View.inflate(BusActivity.this,R.layout.item_bus_listview,null);
                viewHolder.tvBusName= (TextView) view.findViewById(R.id.tv_busitem_name);
                viewHolder.tvBusNum=(TextView)view.findViewById(R.id.tv_busitem_num);
                view.setTag(viewHolder);
            }else {
                viewHolder= (FromViewHolder) view.getTag();
            }
            viewHolder.tvBusName.setText(getItem(i).name);
            viewHolder.tvBusNum.setText(getItem(i).stationNum);
            return view;
        }
        class FromViewHolder{
            TextView tvBusName,tvBusNum;
        }
    }


}
