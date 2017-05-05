package com.parking.parkingguide.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.parking.parkingguide.R;
import com.parking.parkingguide.customview.MaterialProgressDialog;
import com.parking.parkingguide.eventbus.MsgToutiao;
import com.parking.parkingguide.database.ToutiaoBean;

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

/**
 * Created by 37266 on 2017/4/28.
 */

public class ToutiaoFragment extends BaseFragment {
    Activity mAtivity;
    private OkHttpClient okHttpClient;
    private MaterialProgressDialog materialProgressDialog;
    private ListView listView;
    private ArrayList<ToutiaoBean.Data> datas;
//    public ToutiaoFragment(Activity activity) {
//        mAtivity=activity;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Log.e("onCreate","onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.e("onDestory","onDestory");
    }

    @Override
    public View initViews() {
        rootView=View.inflate(mAtivity,R.layout.frag_layout_toutiao,null);
        listView= (ListView) rootView.findViewById(R.id.lv_toutiao);
        return rootView;
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
        materialProgressDialog=new MaterialProgressDialog(mAtivity,R.style.CustomProgressDialog);
        materialProgressDialog.show();
        super.initDatas();
        okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url("http://v.juhe.cn/toutiao/index?type=top&key=" +
                "7ebe84fbc18670d508b12a0ab1d53ee4").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                Gson gson=new Gson();
                ToutiaoBean toutiaoBean=gson.fromJson(responseData,ToutiaoBean.class);
                Log.e("onResponse",toutiaoBean.result.data.toString());
                Log.e("Thread",Thread.currentThread().getName()+"线程名称");
                EventBus.getDefault().post(new MsgToutiao(toutiaoBean.result.data));
                call.cancel();
            }
        });

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleToutiao(MsgToutiao msgToutiao){
        Log.e("handleToutiao",Thread.currentThread().getName()+"线程名称");
        datas= msgToutiao.getDatas();
        listView.setAdapter(new ToutiaoAdapter());
        materialProgressDialog.dismiss();
    }
    class ToutiaoAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return datas.size();
        }

        @Override
        public ToutiaoBean.Data getItem(int i) {
            return datas.get(i);
        }
        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView=View.inflate(mAtivity,R.layout.item_listview_toutiao,null);
                viewHolder=new ViewHolder();
                viewHolder.title=(TextView)convertView.findViewById(R.id.tv_toutiao_title);
                viewHolder.iv=(ImageView)convertView.findViewById(R.id.iv_toutiao_img);
                viewHolder.time=(TextView) convertView.findViewById(R.id.tv_toutiao_time);
                viewHolder.author=(TextView)convertView.findViewById(R.id.tv_toutiao_author);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            viewHolder.author.setText(getItem(i).title);
            Glide.with(mAtivity).load(getItem(i).thumbnail_pic_s).placeholder(R.drawable.pictureloading)
                    .error(R.drawable.nopicture).diskCacheStrategy(DiskCacheStrategy.RESULT).into(
                            viewHolder.iv
            );
            viewHolder.time.setText(getItem(i).date);
            viewHolder.author.setText(getItem(i).author_name);
            return convertView;
        }
        class ViewHolder{
            TextView title;
            ImageView iv;
            TextView time;
            TextView author;
        }
    }

}
