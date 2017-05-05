package com.parking.parkingguide;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.parking.parkingguide.bean.ParkInfo;
import com.parking.parkingguide.database.ParkDatabase;
import com.parking.parkingguide.eventbus.MessageEvent;
import com.parking.parkingguide.menuact.BusActivity;
import com.parking.parkingguide.menuact.MovieActivity;
import com.parking.parkingguide.menuact.StarActivity;
import com.parking.parkingguide.menuact.WechatActivity;
import com.parking.parkingguide.menuact.FootBallActivity;
import com.parking.parkingguide.menuact.HappyActivity;
import com.parking.parkingguide.menuact.HistoryActivity;
import com.parking.parkingguide.menuact.NewsActivity;
import com.parking.parkingguide.menuact.ReportActivity;
import com.parking.parkingguide.services.WeatherService;
import com.parking.parkingguide.utils.ExcelUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


public class MainActivity extends AppCompatActivity implements AMapLocationListener{
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mToggle;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private FloatingActionButton mFloatButton;
    private ListView menuListView;
    private TextView tvCity,tvTime,tvStatu,tvTemp,tvWinDire,tvWinpower,tvHumidi;
    private static Context mContext;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;
    private double currentLatitude;
    private double currentLongtitude;
    private static int[] colors=new int[]{R.color.default_material_dark,R.color.material_dark
    ,R.color.disabled_material_light,R.color.material_light};
    private  String[] listitems=new String[]{"违规停车场举报","分享应用","新闻头条","开心一刻",
            "公交时刻查询", "足球联赛","微信精选","星座运势","历史上的今天"};
    private int[] listitemsImg=new int[]{R.drawable.menu_jubao,R.drawable.menu_share,R.drawable.menu_xinwen,
            R.drawable.menu_kaixin, R.drawable.menu_gongjiao,R.drawable.menu_zuqiu,R.drawable.menu_weixin,
            R.drawable.menu_xingzhuo,R.drawable.menu_lishi};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        Bmob.initialize(this,"ded6d46a3239b2511b5ebfbaa28a702f");
        //为MainActivity注册事件
        EventBus.getDefault().register(this);
        mContext=MainActivity.this;
        //初始化定位
        initLocation();
        //初始化控件数据
        initView();
        //初始化ToolBar和DrawableLayout
        initToolBarAnadDrawableLayout();
        //启动ExcelAsynTask，execute的参数会传递到doInBackground的形参中
        new ExcelAsynTask().execute();
        //启动天气服务
        startService(new Intent(MainActivity.this, WeatherService.class));
    }
    private void initLocation(){
        locationClient=new AMapLocationClient(getApplicationContext());//实例化定位服务对象
        locationClient.setLocationListener(this);//为定位服务对象设置定位监听器
        locationClientOption=new AMapLocationClientOption();//实例化定位服务参数对象
        locationClientOption.setInterval(15000);//通过定位服务参数对象设置多少时间进行一次定位
        //设置定位的模式为高精度模式
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClient.setLocationOption(locationClientOption);//为定位服务对象设置定位参数
        locationClient.startLocation();//开启定位
    }
    private void initView(){//通过我们在资源文件中第一的id来找到这些控件
        drawerLayout= (DrawerLayout) findViewById(R.id.dlMain);
        toolbar=(Toolbar)findViewById(R.id.toolBar);
        recyclerView=(RecyclerView)findViewById(R.id.recycleView_main);
        mFloatButton=(FloatingActionButton)findViewById(R.id.float_btn);
        tvCity=(TextView)findViewById(R.id.tv_wtCity);
        tvTime=(TextView)findViewById(R.id.tv_wtTime);
        tvStatu=(TextView)findViewById(R.id.tv_wtStatu);
        tvTemp=(TextView)findViewById(R.id.tv_wtTemp);
        tvWinDire=(TextView)findViewById(R.id.tv_wtWindyDire);
        tvWinpower=(TextView)findViewById(R.id.tv_wtWindyPower);
        tvHumidi=(TextView)findViewById(R.id.tv_wtHumidi);

        menuListView=(ListView)findViewById(R.id.lvMenu);
        menuListView.setAdapter(new MenuListAdapter());
        menuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
//                        "违规停车场举报"
                        startActivity(new Intent(MainActivity.this, ReportActivity.class));
                        break;
                    case 1:
//                        "分享应用"
                        showShare();
                        break;
                    case 2:
//                        "新闻头条"
                        startActivity(new Intent(MainActivity.this, NewsActivity.class));
                        break;
                    case 3:
//                        "开心一刻"
                        startActivity(new Intent(MainActivity.this, HappyActivity.class));
                        break;
                    case 4:
//                        "公交时刻查询"
                        startActivity(new Intent(MainActivity.this, BusActivity.class));
                        break;
                    case 5:
//                        "足球联赛"
                        startActivity(new Intent(MainActivity.this,FootBallActivity.class));
                        break;
                    case 6:
//                        "微信精选"
                        startActivity(new Intent(MainActivity.this,WechatActivity.class));
                        break;
                    case 7:
//                        "星座运势"
                        startActivity(new Intent(MainActivity.this, StarActivity.class));
                        break;
                    case 8:
//                        "历史上的今天"
                        startActivity(new Intent(MainActivity.this,HistoryActivity.class));
                        break;
                }
            }
        });
    }
    private void initToolBarAnadDrawableLayout(){
        setSupportActionBar(toolbar);//设置actionbar为我们的toolbar
        getSupportActionBar().setHomeButtonEnabled(true);//决定左上角的图标是否可以i安吉
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//给左上角的图标设置一个返回图标
        getSupportActionBar().setDisplayShowTitleEnabled(false);//展示标题
        //将toolbar与DrawableLayout进行绑定
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
        drawerLayout.addDrawerListener(mToggle);//为drawableLayout设置监听器。
        mToggle.syncState();
    }
    private void initRecyclerView(ArrayList<ParkInfo> parkInfos){
        linearLayoutManager=new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //不改变recycleView的尺寸
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        myRecyclerViewAdapter=new MyRecyclerViewAdapter(parkInfos);
        myRecyclerViewAdapter.setRecyclerViewOnitemClickListener(new MyRecyclerViewAdapter.RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView= (TextView) view.findViewById(R.id.tv_parkname);
                String parkName=textView.getText().toString();
                if(parkName!=null) {
                    Toast.makeText(getApplicationContext(),parkName,Toast.LENGTH_LONG).show();
                    Bundle bundle=new Bundle();
                    bundle.putString("parkName",parkName);
                    Intent intent=new Intent(MainActivity.this,LocationActivity.class);
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"parkNameIsNULL",Toast.LENGTH_LONG).show();
                }
            }
        });
        recyclerView.setAdapter(myRecyclerViewAdapter);
    }
    public void floatLocation(View view){
        Bundle bundle=new Bundle();
        bundle.putString("parkName","停车场");
        Intent intent=new Intent(MainActivity.this,LocationActivity.class);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }
//    public void searchJuhePark(View view){
//        Bundle bundle=new Bundle();
//        bundle.putDouble("latitude",currentLatitude);
//        bundle.putDouble("longtitude",currentLongtitude);
//        Intent intent=new Intent(MainActivity.this,JuheParkActivity.class);
//        intent.putExtra("bundle",bundle);
//        startActivity(intent);
//    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation!=null&&aMapLocation.getErrorCode()==0){
            currentLatitude=aMapLocation.getLatitude();
            currentLongtitude=aMapLocation.getLongitude();
        }
    }

    static class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MYViewHolder>{
        private ArrayList<ParkInfo> parkInfos;
        public RecyclerViewOnItemClickListener recyclerViewOnitemClickListener;
        public MyRecyclerViewAdapter(ArrayList<ParkInfo> Infos) {
            this.parkInfos = Infos;
        }
        public void setRecyclerViewOnitemClickListener(RecyclerViewOnItemClickListener listener){
           this.recyclerViewOnitemClickListener=listener;
        }
        public interface RecyclerViewOnItemClickListener{
            void onItemClick(View vie,int position);
        }
        @Override
        public MYViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_recyclerview,parent,false);
            return new MYViewHolder(view);
        }
        @Override
        public void onBindViewHolder(MYViewHolder holder, int position) {
            position=position+1;
            ParkInfo parkInfo=parkInfos.get(position);
            holder.linearRoot.setBackgroundColor(ContextCompat.getColor(mContext,colors[position%4]));
            holder.area.setText(parkInfo.getArea());
            holder.recordId.setText(parkInfo.getRecordId());
            holder.id.setText(parkInfo.getId());
            holder.parkName.setText(parkInfo.getParkName());
            holder.parkType.setText(parkInfo.getParkType());
            holder.parkCompany.setText(parkInfo.getParkCompany());
            holder.parkNum.setText(parkInfo.getParkNum());
            holder.parkLevel.setText(parkInfo.getParkLevel());
        }

        @Override
        public int getItemCount() {
            return parkInfos.size();
        }
        class MYViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            LinearLayout linearRoot;
            TextView area,recordId,id,parkName,parkType,parkCompany,parkNum,parkLevel;
            public MYViewHolder(View itemView) {
                super(itemView);
                linearRoot=(LinearLayout)itemView.findViewById(R.id.ll_item_recyclerview);
                area= (TextView) itemView.findViewById(R.id.tv_area);
                recordId= (TextView) itemView.findViewById(R.id.tv_recordId);
                id= (TextView) itemView.findViewById(R.id.tv_id);
                parkName= (TextView) itemView.findViewById(R.id.tv_parkname);
                parkType= (TextView) itemView.findViewById(R.id.tv_parktype);
                parkCompany= (TextView) itemView.findViewById(R.id.tv_parkcompany);
                parkNum= (TextView) itemView.findViewById(R.id.tv_parknum);
                parkLevel= (TextView) itemView.findViewById(R.id.tv_level);
                itemView.setOnClickListener(this);
            }
            @Override
            public void onClick(View view) {
                if(recyclerViewOnitemClickListener!=null){
                    recyclerViewOnitemClickListener.onItemClick(view,getPosition());
                }
            }
        }
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
            Log.e("ExcelAsynTask","ExcelAsynTask----run");
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
            initRecyclerView(list);
            super.onPostExecute(list);
        }
    }
    class MenuListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return listitems.length;
        }

        @Override
        public String getItem(int i) {
            return listitems[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if(convertView==null){
                convertView=View.inflate(MainActivity.this,R.layout.layout_listmenu,null);
                viewHolder=new ViewHolder();
                viewHolder.imageView= (ImageView) convertView.findViewById(R.id.menu_iv);
                viewHolder.textView=(TextView)convertView.findViewById(R.id.menu_tv);
                convertView.setTag(viewHolder);
            }else {
                viewHolder=(ViewHolder)convertView.getTag();
            }
            viewHolder.textView.setText(getItem(i));
            viewHolder.imageView.setImageResource(listitemsImg[i]);
            return convertView;
        }
        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }
    //这里权限要声明为public的
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessageEvent(MessageEvent messageEvent){
        //        TextView tvCity,tvTime,tvStatu,tvTemp,tvWinDire,tvWinpower,tvHumidi;
        Log.e("eventBus",messageEvent.toString());
        tvCity.setText(messageEvent.getCity());
        tvTime.setText(messageEvent.getReportTime());
        tvStatu.setText(messageEvent.getStatus());
        tvTemp.setText(messageEvent.getTemp());
        tvHumidi.setText(messageEvent.getHumidity());
        tvWinpower.setText(messageEvent.getWindyPower()+"级");
        tvWinDire.setText(messageEvent.getWindy()+"风");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("停车场导航应用");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");
        // 启动分享GUI
        oks.show(this);
    }
}
