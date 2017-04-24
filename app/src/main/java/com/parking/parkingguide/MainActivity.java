package com.parking.parkingguide;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.parking.parkingguide.bean.ParkInfo;
import com.parking.parkingguide.database.ParkDatabase;
import com.parking.parkingguide.utils.ExcelUtils;

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBarDrawerToggle mToggle;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private FloatingActionButton mFloatButton;
    private static Context mContext;

    private static int[] colors=new int[]{R.color.default_material_dark,R.color.material_dark
    ,R.color.disabled_material_light,R.color.material_light};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        mContext=MainActivity.this;
        initView();
        initToolBarAnadDrawableLayout();
        //启动ExcelAsynTask，execute的参数会传递到doInBackground的形参中
        new ExcelAsynTask().execute();
        //初始化控件的数据
    }
    private void initView(){
        drawerLayout= (DrawerLayout) findViewById(R.id.dlMain);
        toolbar=(Toolbar)findViewById(R.id.toolBar);
        recyclerView=(RecyclerView)findViewById(R.id.recycleView_main);
        mFloatButton=(FloatingActionButton)findViewById(R.id.float_btn);
    }
    private void initToolBarAnadDrawableLayout(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
        drawerLayout.addDrawerListener(mToggle);
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
        //
    }
    public void floatLocation(View view){
        startActivity(new Intent(MainActivity.this,LocationActivity.class));
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
//            for (int i=0;i<100;i++){
//                ParkInfo parkInfo=list.get(i);
//                Log.e("MainActivity",parkInfo.toString());
//            }
            super.onPostExecute(list);
        }
    }

}
