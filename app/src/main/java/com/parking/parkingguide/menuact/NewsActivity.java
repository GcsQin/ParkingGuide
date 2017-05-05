package com.parking.parkingguide.menuact;

import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.parking.parkingguide.R;
import com.parking.parkingguide.WebNewsActivity;
import com.parking.parkingguide.customview.MaterialProgressDialog;
import com.parking.parkingguide.database.ToutiaoBean;
import com.parking.parkingguide.eventbus.MsgToutiao;
import com.parking.parkingguide.fragment.BaseFragment;
import com.parking.parkingguide.fragment.CaijingFragment;
import com.parking.parkingguide.fragment.GuojiFragment;
import com.parking.parkingguide.fragment.GuoneiFragment;
import com.parking.parkingguide.fragment.JunshiFragment;
import com.parking.parkingguide.fragment.KejiFragment;
import com.parking.parkingguide.fragment.ShehuiFragment;
import com.parking.parkingguide.fragment.ShishangFragment;
import com.parking.parkingguide.fragment.TiyuFragment;
import com.parking.parkingguide.fragment.ToutiaoFragment;
import com.parking.parkingguide.fragment.YuleFragment;

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

public class NewsActivity extends AppCompatActivity {
    private OkHttpClient okHttpClient=null;
    private ArrayList<ToutiaoBean.Data> datas;
    private MaterialProgressDialog materialProgressDialog;
//    private ViewPager mViewPager;
//    private RadioGroup radioGroup;
//    private ArrayList<BaseFragment> baseFragments;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_layout_news);
        EventBus.getDefault().register(this);
        okHttpClient=new OkHttpClient();
        listView=(ListView)findViewById(R.id.lv_toutiao_news);
        initDatas();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String url=datas.get(i).url;
                Intent intent=new Intent(NewsActivity.this, WebNewsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("url",url);
                intent.putExtra("webBundle",bundle);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    public void initDatas() {
        materialProgressDialog=new MaterialProgressDialog(NewsActivity.this,R.style.CustomProgressDialog);
        materialProgressDialog.show();
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

    class ToutiaoAdapter extends BaseAdapter {

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
                convertView=View.inflate(NewsActivity.this,R.layout.item_listview_toutiao,null);
                viewHolder=new ViewHolder();
                viewHolder.title=(TextView)convertView.findViewById(R.id.tv_toutiao_title);
                viewHolder.iv=(ImageView)convertView.findViewById(R.id.iv_toutiao_img);
                viewHolder.time=(TextView) convertView.findViewById(R.id.tv_toutiao_time);
                viewHolder.author=(TextView)convertView.findViewById(R.id.tv_toutiao_author);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(getItem(i).title);
            Glide.with(NewsActivity.this).load(getItem(i).thumbnail_pic_s).placeholder(R.drawable.pictureloading)
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
//    private void initDatas(){
//        baseFragments=new ArrayList<BaseFragment>();
//        baseFragments.add(new ToutiaoFragment(this));
//        baseFragments.add(new ShehuiFragment(this));
//        baseFragments.add(new GuoneiFragment(this));
//        baseFragments.add(new GuojiFragment(this));
//        baseFragments.add(new YuleFragment(this));
//        baseFragments.add(new TiyuFragment(this));
//        baseFragments.add(new JunshiFragment(this));
//        baseFragments.add(new KejiFragment(this));
//        baseFragments.add(new CaijingFragment(this));
//        baseFragments.add(new ShishangFragment(this));
//    }
//    private void initView(){
//        mViewPager=(ViewPager)findViewById(R.id.recycleView_News);
//        radioGroup=(RadioGroup)findViewById(R.id.rg_news);
//        radioGroup.check(R.id.rb_00);
//            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
//                Log.e("onCheckedChanged",""+i);
//                switch (i){
//                    case R.id.rb_00:
//                        mViewPager.setCurrentItem(0,false);
//                        break;
//                    case R.id.rb_01:
//                        mViewPager.setCurrentItem(1,false);
//                        break;
//                    case R.id.rb_02:
//                        mViewPager.setCurrentItem(2,false);
//                        break;
//                    case R.id.rb_03:
//                        mViewPager.setCurrentItem(3,false);
//                        break;
//                    case R.id.rb_04:
//                        mViewPager.setCurrentItem(4,false);
//                        break;
//                    case R.id.rb_05:
//                        mViewPager.setCurrentItem(5,false);
//                        break;
//                    case R.id.rb_06:
//                        mViewPager.setCurrentItem(6,false);
//                        break;
//                    case R.id.rb_07:
//                        mViewPager.setCurrentItem(7,false);
//                        break;
//                    case R.id.rb_08:
//                        mViewPager.setCurrentItem(8,false);
//                        break;
//                    case R.id.rb_09:
//                        mViewPager.setCurrentItem(9,false);
//                        break;
//                }
//            }
//        });
//        mViewPager.setAdapter(new NewsPagerAdapter());
//        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//            @Override
//            public void onPageSelected(int position) {
//                Log.e("onPageSelectedPosition",position+"---poi---");
//                switch (position){
//                    case 0:
//                        Log.e("onPageSelected",""+R.id.rb_00);
//                        radioGroup.check(R.id.rb_00);
//                        break;
//                    case 1:
//                        Log.e("onPageSelected",""+R.id.rb_01);
//                        radioGroup.check(R.id.rb_01);
//                        break;
//                    case 2:
//                        Log.e("onPageSelected",""+R.id.rb_02);
//                        radioGroup.check(R.id.rb_02);
//                        break;
//                    case 3:
//                        Log.e("onPageSelected",""+R.id.rb_03);
//                        radioGroup.check(R.id.rb_03);
//                        break;
//                    case 4:
//                        Log.e("onPageSelected",""+R.id.rb_04);
//                        radioGroup.check(R.id.rb_04);
//                        break;
//                    case 5:
//                        Log.e("onPageSelected",""+R.id.rb_05);
//                        radioGroup.check(R.id.rb_05);
//                        break;
//                    case 6:
//                        radioGroup.check(R.id.rb_06);
//                        break;
//                    case 7:
//                        radioGroup.check(R.id.rb_07);
//                        break;
//                    case 8:
//                        radioGroup.check(R.id.rb_08);
//                        break;
//                    case 9:
//                        radioGroup.check(R.id.rb_09);
//                        break;
//                }
//            }
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });
//        mViewPager.setCurrentItem(0,false);
//    }
//
//    class NewsPagerAdapter extends PagerAdapter{
//        @Override
//        public Object instantiateItem(ViewGroup container, int position) {
//            BaseFragment baseFragment=baseFragments.get(position);
//            baseFragment.initDatas();
//            baseFragment.initViews();
//            if(baseFragment.rootView!=null){
//                container.addView(baseFragment.rootView);
//            }else {
//
//            }
//            return baseFragment.rootView;
//        }
//        @Override
//        public void destroyItem(ViewGroup container, int position, Object object) {
//                container.removeView((View)object);
//        }
//        @Override
//        public int getCount() {
//            return baseFragments.size();
//        }
//        @Override
//        public boolean isViewFromObject(View view, Object object) {
//            return view == object;
//        }
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
}
