package com.parking.parkingguide.menuact;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parking.parkingguide.R;
import com.parking.parkingguide.bean.JuheFootball;

import java.util.ArrayList;

public class FootBallDetialsActivity extends AppCompatActivity {
    ListView lv_fb01,lv_fb02;
    TextView tv_fb01,tv_fb02;
    ArrayList<JuheFootball.Saicheng1> saicheng1s;
    ArrayList<JuheFootball.Saicheng2> saicheng2s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_footballdetials);
        Intent intent=getIntent();
         JuheFootball juheFootball= (JuheFootball) intent.getSerializableExtra("football");
        saicheng1s=juheFootball.result.views.saicheng1;
        saicheng2s=juheFootball.result.views.saicheng2;
        lv_fb01=(ListView)findViewById(R.id.lv_fb01);
        lv_fb02=(ListView)findViewById(R.id.lv_fb02);
        tv_fb01=(TextView)findViewById(R.id.tv_fb01);
        tv_fb02= (TextView) findViewById(R.id.tv_fb02);
        tv_fb01.setText(juheFootball.result.tabs.saicheng1);
        tv_fb02.setText(juheFootball.result.tabs.saicheng2);
        if(lv_fb01==null){
            Log.e("lvfb01","isnull");
        }else {
            Log.e("lvfb01","isNotnull");
            lv_fb01.setAdapter(new SaichengOne());
        }
        if(lv_fb02!=null){
            lv_fb02.setAdapter(new SaichengTwo());
        }
    }
    class SaichengOne extends BaseAdapter{

        @Override
        public int getCount() {
            return saicheng1s.size();
        }
        @Override
        public JuheFootball.Saicheng1 getItem(int i) {
            return saicheng1s.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            OneViewHolder oneViewHolder;
            if(view==null){
                oneViewHolder=new OneViewHolder();
                view=View.inflate(getApplicationContext(),R.layout.item_football_lv,null);
                oneViewHolder.tvTime= (TextView) view.findViewById(R.id.tv_fbi_time);
                oneViewHolder.tvResult=(TextView)view.findViewById(R.id.tv_fbi_result);
                oneViewHolder.tvTeam01= (TextView) view.findViewById(R.id.tv_fbi_t1);
                oneViewHolder.tvTeam02= (TextView) view.findViewById(R.id.tv_fbi_t2);
                view.setTag(oneViewHolder);
            }else {
                oneViewHolder= (OneViewHolder) view.getTag();
            }
            oneViewHolder.tvTime.setText(getItem(i).c2+getItem(i).c3);
            oneViewHolder.tvResult.setText(getItem(i).c4R);
            oneViewHolder.tvTeam01.setText(getItem(i).c4T1);
            oneViewHolder.tvTeam02.setText(getItem(i).c4T2);
            return view;
        }
        class OneViewHolder{
            TextView tvTime,tvTeam01,tvTeam02,tvResult;
        }
    }
    class SaichengTwo extends BaseAdapter{
        @Override
        public int getCount() {
            return saicheng2s.size();
        }
        @Override
        public JuheFootball.Saicheng2 getItem(int i) {
            return saicheng2s.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TwoViewHolder twoViewHolder;
            if(view==null){
                twoViewHolder=new TwoViewHolder();
                view=View.inflate(getApplicationContext(),R.layout.item_football_lv,null);
                twoViewHolder.tvTime= (TextView) view.findViewById(R.id.tv_fbi_time);
                twoViewHolder.tvResult=(TextView)view.findViewById(R.id.tv_fbi_result);
                twoViewHolder.tvTeam01= (TextView) view.findViewById(R.id.tv_fbi_t1);
                twoViewHolder.tvTeam02= (TextView) view.findViewById(R.id.tv_fbi_t2);
                view.setTag(twoViewHolder);
            }else {
                twoViewHolder= (TwoViewHolder) view.getTag();
            }
            twoViewHolder.tvTime.setText(getItem(i).c2+getItem(i).c3);
            twoViewHolder.tvResult.setText(getItem(i).c4R);
            twoViewHolder.tvTeam01.setText(getItem(i).c4T1);
            twoViewHolder.tvTeam02.setText(getItem(i).c4T2);
            return view;
        }
        class TwoViewHolder{
            TextView tvTime,tvTeam01,tvTeam02,tvResult;
        }
    }
}
