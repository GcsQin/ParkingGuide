package com.parking.parkingguide.eventbus;

import com.parking.parkingguide.database.ToutiaoBean;

import java.util.ArrayList;

/**
 * Created by 37266 on 2017/4/29.
 */

public class MsgToutiao {
    private ArrayList<ToutiaoBean.Data> datas;

    public MsgToutiao(ArrayList<ToutiaoBean.Data> datas) {
        this.datas = datas;
    }

    public ArrayList<ToutiaoBean.Data> getDatas() {
        return datas;
    }

    public void setDatas(ArrayList<ToutiaoBean.Data> datas) {
        this.datas = datas;
    }
}
