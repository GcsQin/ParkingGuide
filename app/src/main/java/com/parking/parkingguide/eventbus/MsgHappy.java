package com.parking.parkingguide.eventbus;

import com.parking.parkingguide.database.HappeyBean;

import java.util.ArrayList;

/**
 * Created by 37266 on 2017/4/29.
 */

public class MsgHappy {
    private ArrayList<HappeyBean.Result> arrayList;

    public MsgHappy(ArrayList<HappeyBean.Result> arrayList) {
        this.arrayList = arrayList;
    }

    public ArrayList<HappeyBean.Result> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<HappeyBean.Result> arrayList) {
        this.arrayList = arrayList;
    }
}
