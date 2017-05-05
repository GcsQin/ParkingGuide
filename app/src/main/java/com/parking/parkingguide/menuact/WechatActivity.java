package com.parking.parkingguide.menuact;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.parking.parkingguide.R;
import com.parking.parkingguide.WebNewsActivity;
import com.parking.parkingguide.bean.WeChatBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WechatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HeaderRecyclerAdapter headerRecyclerAdapter;
    public static Context mContext;
    Intent itemIntent;
    Bundle itemBundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inner_layout_wechat);
        EventBus.getDefault().register(this);
        mContext=getApplicationContext();
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView_wechat);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        parseData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void parseData(){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder().url("http://v.juhe.cn/weixin/query?" +
                "pno=&ps=&dtype=&key=1c7e12ec6e2f98b121295afc8869421a").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                if(responseData!=null){
                    Gson gson=new Gson();
                    WeChatBean weChatBean=gson.fromJson(responseData,WeChatBean.class);
                    if(weChatBean.error_code==0){
                        EventBus.getDefault().post(weChatBean);
                    }
                }
                call.cancel();
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleWechatMsg(final WeChatBean weChatBean){
        headerRecyclerAdapter=new HeaderRecyclerAdapter(weChatBean.result.list);
        headerRecyclerAdapter.setInnerHeaderView();
        headerRecyclerAdapter.setOnRecyclerViewItemClickListener(new HeaderRecyclerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemIntent=new Intent(WechatActivity.this,WebNewsActivity.class);
                itemBundle=new Bundle();
                itemBundle.putString("url",weChatBean.result.list.get(position).url.replaceAll("\\\\",""));
                itemIntent.putExtra("webBundle",itemBundle);
                startActivity(itemIntent);
            }
        });
        recyclerView.setAdapter(headerRecyclerAdapter);
    }
    static class HeaderRecyclerAdapter extends RecyclerView.Adapter implements View.OnClickListener{
        public static final int TYPE_HEADER=0;//说明是带有header的
        public static final int TYPE_FOOTER=1;//说明是带有footer的
        public static final int TYPE_NOBOTH=2;//说明不带有header和footer
        public boolean isInner =false;
        //
        private View headerView;
        private View footerView;
        //从activity传过来的集合数据
        private ArrayList<WeChatBean.Data> mDatas;

        public HeaderRecyclerAdapter(ArrayList<WeChatBean.Data> datas) {
            this.mDatas = datas;
        }
        //添加内置自带的头布局和叫布局
        public void setInnerHeaderView(){
            this.headerView=View.inflate(mContext,R.layout.header_recyclerview_wechat
            ,null);
            this.isInner=true;
            notifyItemInserted(0);
        }
        //添加自定义的头布局和脚布局
        public void setHeaderView(View headerView) {
            this.headerView = headerView;
            notifyItemInserted(0);
        }
        public void setFooterView(View footerView) {
            this.footerView = footerView;
            notifyItemInserted(mDatas.size()-1);
        }
        //获取头布局和脚布局
        public View getFooterView() {
            return footerView;
        }
        public View getHeaderView() {
            return headerView;
        }
        //这个方法用于我们在创建view的时候进行判断，进行不同布局的创建。
        @Override
        public int getItemViewType(int position) {
            if(headerView==null&&footerView==null){//如果头布局为空，而且脚布局也为空
                return TYPE_NOBOTH;
            }
            if(position==0&&headerView!=null){
                //如果当前位置是第一个而且头布局不为空，那么当前的view类型是TYPE_HEADER
                return TYPE_HEADER;
            }
            if(position==mDatas.size()-1&&footerView!=null){
                //如果当前位置是最后一个位置而且脚布局不为空。那么当前的view类型是TYPE_FOOTER
                return TYPE_FOOTER;
            }
            //如果不当前位置不是头和尾，那么布局是正常的布局类型TYPE_NOBOTH
            return TYPE_NOBOTH;
        }
        //创建view,判断当前view的类型，如果当前的view是头布局或脚布局，直接在viewholder中返回
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==TYPE_HEADER&&headerView!=null){
                //如果view的类型是头布局而且headerView通过setHeaderView不为空的时将布局返回到HFViewHolder
                //中进行处理
                /*如果当前的view的类型是TYPE_HEADER而且headerView!=null的时候，判断当前的header类型是
                * 否为自定义的header还是内置header
                * */
//                if(isInner){
//                    TextView tvHeader= (TextView) headerView.findViewById(R.id.tv_header_recyclerview);
//                    ImageView imageView=(ImageView)headerView.findViewById(R.id.img_header_recyclerview);
//                    tvHeader.setText(mDatas.get(0).title);
//                    String url=mDatas.get(0).url.replace("\\\\","");
//                    Glide.with(parent.getContext()).load(url).error(R.drawable.nopicture).
//                            placeholder(R.drawable.pictureloading).into(imageView);
//                }
                headerView.setOnClickListener(this);
                return new HFViewHolder(headerView);
            }
            if(viewType==TYPE_FOOTER&&footerView!=null){
                //如果view的类型是头布局而且footerView通过setFootView不为空的时将布局返回到HFViewHolder
                //中进行处理
                return new HFViewHolder(footerView);
            }
            //如果当前的view类型不是头布局也不是脚布局，那么就从资源文件中导入我们的item来填充一个view对象，
            //并且将这个view返回到HFViewHolder中进行处理
            View view=View.inflate(parent.getContext(),R.layout.item_recyclerview_wechat,null);
            view.setOnClickListener(this);
            return new HFViewHolder(view);
        }
        //绑定view
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            //这里是为了点击事件声明的，为点击的item设置一个tag为当前的item的位置
            ((HFViewHolder)holder).itemView.setTag(position);
            //==================================================
            if(getItemViewType(position)==TYPE_NOBOTH){
                //如果当前需要绑定的布局是普通布局
                if(holder instanceof HFViewHolder){
                    ((HFViewHolder)holder).itemTextTitle.setText(mDatas.get(position).title);
                    ((HFViewHolder)holder).itemTextResource.setText(mDatas.get(position).source);
                    String url=mDatas.get(position).firstImg.replaceAll("\\\\","");
                    Log.e("url",url);
                    Glide.with(mContext).load(url).placeholder(R.drawable.pictureloading)
                            .error(R.drawable.nopicture).into(((HFViewHolder)holder).itemImgview);
                    return;
                }
                return;
            }else if(getItemViewType(position)==TYPE_HEADER){//如果当前的绑定的对象是头布局
                if(isInner){
                    TextView tvHeader= (TextView) headerView.findViewById(R.id.tv_header_recyclerview);
                    ImageView imageView=(ImageView)headerView.findViewById(R.id.img_header_recyclerview);
                    tvHeader.setText(mDatas.get(0).title);
                    String url=mDatas.get(0).firstImg.replaceAll("\\\\","");
                    Glide.with(mContext).load(url).error(R.drawable.nopicture).
                            placeholder(R.drawable.pictureloading).into(imageView);
                }
                return;
            }else {
                return;
            }
        }
        //布局的数量,在这里我根据项目需求,我的头布局和脚布局的数据都来自传进来的WeChatBean.Data，所以我这
        //里直接获取WeChatBean.Data集合的长度就可以了。如果是其他的情况，可以根据需求在这里进行判断即可。
        @Override
        public int getItemCount() {
            return mDatas.size();
        }
        /*设置点击事件的步骤：
        * 1.Adapter实现View.OnClickListener接口
        * 2.创建一个本身点击事件监听器接口
        * 3.声明一个本身接口的对象
        * 4.在onCreateViewHolder()中为每个item添加点击事件view.setOnclickListener(this)
        * 5.在OnClickListener的onClick方法中将点击事件转移给外面的调用者。
        * 6.最后暴露给外面的调用者，定义一个设置Listener的方法
        * */
        public static interface OnRecyclerViewItemClickListener{
            void onItemClick(View view ,int position);
        }
        private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;

        @Override
        public void onClick(View view) {
            if(onRecyclerViewItemClickListener!=null){
                onRecyclerViewItemClickListener.onItemClick(view,(int)view.getTag());
//              注意上面调用接口的onItemClick()中的v.getTag()方法，这需要在onBindViewHolder()方法中设置和item的position
            }
        }
        public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener){
            this.onRecyclerViewItemClickListener=listener;
        }

        class HFViewHolder extends RecyclerView.ViewHolder{
            ImageView itemImgview;
            TextView itemTextTitle;
            TextView itemTextResource;
            public HFViewHolder(View itemView) {
                super(itemView);
                if(itemView==headerView){
                    return;
                }
                if(itemView==footerView){
                    return;
                }
                itemImgview= (ImageView) itemView.findViewById(R.id.img_wechat_item);
                itemTextResource= (TextView) itemView.findViewById(R.id.tv_wechat_title);
                itemTextTitle= (TextView) itemView.findViewById(R.id.tv_wechat_resource);
            }
        }
    }
}
