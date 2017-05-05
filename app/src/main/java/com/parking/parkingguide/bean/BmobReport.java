package com.parking.parkingguide.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by 37266 on 2017/4/30.
 */
//extends BmobObject
public class BmobReport extends BmobObject{
    public String name;
    public String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
